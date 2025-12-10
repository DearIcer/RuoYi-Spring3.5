package com.ruoyi.project.client.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import jakarta.servlet.http.HttpServletRequest;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.enums.UserStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.user.UserPasswordNotMatchException;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.ip.AddressUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.framework.redis.RedisCache;
import com.ruoyi.project.client.domain.ClientLoginUser;
import com.ruoyi.project.client.domain.ClientUser;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * 客户端登录服务
 * 
 * @author ruoyi
 */
@Service
public class ClientLoginService
{
    private static final Logger log = LoggerFactory.getLogger(ClientLoginService.class);

    // 令牌自定义标识
    @Value("${token.header}")
    private String header;

    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;

    // 令牌有效期（默认30分钟）
    @Value("${token.expireTime}")
    private int expireTime;

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    private static final Long MILLIS_MINUTE_TWENTY = 20 * 60 * 1000L;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private IClientUserService clientUserService;

    /**
     * 客户端登录验证
     * 
     * @param username 用户名
     * @param password 密码
     * @return 令牌
     */
    public String login(String username, String password)
    {
        // 登录前置校验
        loginPreCheck(username, password);
        
        // 查询客户端用户
        ClientUser clientUser = clientUserService.selectClientUserByUserName(username);
        if (StringUtils.isNull(clientUser))
        {
            log.info("客户端登录用户：{} 不存在.", username);
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.not.exists")));
            throw new ServiceException(MessageUtils.message("user.not.exists"));
        }
        else if (UserStatus.DELETED.getCode().equals(clientUser.getDelFlag()))
        {
            log.info("客户端登录用户：{} 已被删除.", username);
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.delete")));
            throw new ServiceException(MessageUtils.message("user.password.delete"));
        }
        else if (UserStatus.DISABLE.getCode().equals(clientUser.getStatus()))
        {
            log.info("客户端登录用户：{} 已被停用.", username);
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.blocked")));
            throw new ServiceException(MessageUtils.message("user.blocked"));
        }
        
        // 验证密码
        validatePassword(clientUser, password, username);
        
        // 记录登录信息
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        recordLoginInfo(clientUser.getUserId());
        
        // 创建登录用户
        ClientLoginUser loginUser = new ClientLoginUser(clientUser);
        
        // 生成token
        return createToken(loginUser);
    }

    /**
     * 验证密码
     */
    private void validatePassword(ClientUser clientUser, String password, String username)
    {
        String cacheKey = getCacheKey(username);
        Integer retryCount = redisCache.getCacheObject(cacheKey);
        if (retryCount == null)
        {
            retryCount = 0;
        }

        if (retryCount >= 5)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.retry.limit.exceed", 5, 10)));
            throw new ServiceException(MessageUtils.message("user.password.retry.limit.exceed", 5, 10));
        }

        if (!SecurityUtils.matchesPassword(password, clientUser.getPassword()))
        {
            retryCount = retryCount + 1;
            redisCache.setCacheObject(cacheKey, retryCount, 10, TimeUnit.MINUTES);
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        }
        else
        {
            // 登录成功，清除密码错误次数
            redisCache.deleteObject(cacheKey);
        }
    }

    /**
     * 登录前置校验
     */
    public void loginPreCheck(String username, String password)
    {
        // 用户名或密码为空 错误
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("not.null")));
            throw new ServiceException(MessageUtils.message("not.null"));
        }
        // 用户名长度校验
        if (username.length() < 2 || username.length() > 20)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new ServiceException(MessageUtils.message("user.password.not.match"));
        }
        // 密码长度校验
        if (password.length() < 5 || password.length() > 20)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new ServiceException(MessageUtils.message("user.password.not.match"));
        }
    }

    /**
     * 记录登录信息
     */
    public void recordLoginInfo(Long userId)
    {
        clientUserService.updateLoginInfo(userId, IpUtils.getIpAddr(), new Date());
    }

    /**
     * 创建令牌
     */
    public String createToken(ClientLoginUser loginUser)
    {
        String token = IdUtils.fastUUID();
        loginUser.setToken(token);
        setUserAgent(loginUser);
        refreshToken(loginUser);

        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.LOGIN_USER_KEY, token);
        claims.put(Constants.JWT_USERNAME, loginUser.getUsername());
        claims.put("user_type", "client"); // 标识为客户端用户
        return createToken(claims);
    }

    /**
     * 从数据声明生成令牌
     */
    private String createToken(Map<String, Object> claims)
    {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        String token = Jwts.builder()
                .claims(claims)
                .signWith(key)
                .compact();
        return token;
    }

    /**
     * 刷新令牌有效期
     */
    public void refreshToken(ClientLoginUser loginUser)
    {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getToken());
        redisCache.setCacheObject(userKey, loginUser, expireTime, TimeUnit.MINUTES);
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     */
    public void verifyToken(ClientLoginUser loginUser)
    {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TWENTY)
        {
            refreshToken(loginUser);
        }
    }

    /**
     * 设置用户代理信息
     */
    public void setUserAgent(ClientLoginUser loginUser)
    {
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        String ip = IpUtils.getIpAddr();
        loginUser.setIpaddr(ip);
        loginUser.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs(userAgent.getOperatingSystem().getName());
    }

    /**
     * 获取客户端登录用户
     */
    public ClientLoginUser getClientLoginUser(HttpServletRequest request)
    {
        // 获取请求携带的令牌
        String token = getToken(request);
        if (StringUtils.isNotEmpty(token))
        {
            try
            {
                Claims claims = parseToken(token);
                // 检查是否是客户端用户
                String userType = (String) claims.get("user_type");
                if (!"client".equals(userType))
                {
                    return null;
                }
                // 解析对应的权限以及用户信息
                String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
                String userKey = getTokenKey(uuid);
                ClientLoginUser user = redisCache.getCacheObject(userKey);
                return user;
            }
            catch (Exception e)
            {
                log.error("获取客户端用户信息异常'{}'", e.getMessage());
            }
        }
        return null;
    }

    /**
     * 删除用户身份信息
     */
    public void delLoginUser(String token)
    {
        if (StringUtils.isNotEmpty(token))
        {
            String userKey = getTokenKey(token);
            redisCache.deleteObject(userKey);
        }
    }

    /**
     * 从令牌中获取数据声明
     */
    private Claims parseToken(String token)
    {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 获取请求token
     */
    private String getToken(HttpServletRequest request)
    {
        String token = request.getHeader(header);
        if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX))
        {
            token = token.replace(Constants.TOKEN_PREFIX, "");
        }
        return token;
    }

    private String getTokenKey(String uuid)
    {
        return CacheConstants.CLIENT_LOGIN_TOKEN_KEY + uuid;
    }

    private String getCacheKey(String username)
    {
        return CacheConstants.CLIENT_PWD_ERR_CNT_KEY + username;
    }
}
