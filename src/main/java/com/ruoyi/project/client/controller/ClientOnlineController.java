package com.ruoyi.project.client.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.framework.redis.RedisCache;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.framework.websocket.ClientWebSocketHandler;
import com.ruoyi.project.client.domain.ClientLoginUser;
import com.ruoyi.project.client.domain.ClientUserOnline;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 客户端在线用户监控
 * 
 * @author ruoyi
 */
@Tag(name = "客户端会话管理")
@RestController
@RequestMapping("/client/online")
public class ClientOnlineController extends BaseController
{
    @Autowired
    private RedisCache redisCache;

    @Autowired(required = false)
    private ClientWebSocketHandler webSocketHandler;

    /**
     * 获取客户端在线用户列表
     */
    @Operation(summary = "获取客户端在线用户列表")
    @PreAuthorize("@ss.hasPermi('client:online:list')")
    @GetMapping("/list")
    public TableDataInfo list(String ipaddr, String userName)
    {
        Collection<String> keys = redisCache.keys(CacheConstants.CLIENT_LOGIN_TOKEN_KEY + "*");
        List<ClientUserOnline> userOnlineList = new ArrayList<ClientUserOnline>();
        for (String key : keys)
        {
            ClientLoginUser user = redisCache.getCacheObject(key);
            if (user == null || user.getUser() == null)
            {
                continue;
            }
            if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName))
            {
                if (StringUtils.equals(ipaddr, user.getIpaddr()) && StringUtils.equals(userName, user.getUsername()))
                {
                    userOnlineList.add(loginUserToUserOnline(user));
                }
            }
            else if (StringUtils.isNotEmpty(ipaddr))
            {
                if (StringUtils.equals(ipaddr, user.getIpaddr()))
                {
                    userOnlineList.add(loginUserToUserOnline(user));
                }
            }
            else if (StringUtils.isNotEmpty(userName))
            {
                if (StringUtils.equals(userName, user.getUsername()))
                {
                    userOnlineList.add(loginUserToUserOnline(user));
                }
            }
            else
            {
                userOnlineList.add(loginUserToUserOnline(user));
            }
        }
        Collections.reverse(userOnlineList);
        userOnlineList.removeAll(Collections.singleton(null));
        return getDataTable(userOnlineList);
    }

    /**
     * 强退客户端用户
     */
    @Operation(summary = "强退客户端用户")
    @PreAuthorize("@ss.hasPermi('client:online:forceLogout')")
    @Log(title = "客户端在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/{tokenId}")
    public AjaxResult forceLogout(@Parameter(description = "会话编号") @PathVariable String tokenId)
    {
        String redisKey = CacheConstants.CLIENT_LOGIN_TOKEN_KEY + tokenId;
        // 先获取用户信息，用于发送WebSocket通知
        ClientLoginUser loginUser = redisCache.getCacheObject(redisKey);
        if (loginUser != null && webSocketHandler != null)
        {
            // 发送WebSocket踢出通知
            webSocketHandler.sendKickOutNotification(loginUser.getUsername(), "您已被管理员强制下线");
        }
        // 删除Redis会话
        redisCache.deleteObject(redisKey);
        return success();
    }

    /**
     * 设置客户端在线用户信息
     */
    private ClientUserOnline loginUserToUserOnline(ClientLoginUser user)
    {
        if (user == null || user.getUser() == null)
        {
            return null;
        }
        ClientUserOnline userOnline = new ClientUserOnline();
        userOnline.setTokenId(user.getToken());
        userOnline.setUserName(user.getUsername());
        userOnline.setNickName(user.getUser().getNickName());
        userOnline.setIpaddr(user.getIpaddr());
        userOnline.setLoginLocation(user.getLoginLocation());
        userOnline.setBrowser(user.getBrowser());
        userOnline.setOs(user.getOs());
        userOnline.setLoginTime(user.getLoginTime());
        return userOnline;
    }
}
