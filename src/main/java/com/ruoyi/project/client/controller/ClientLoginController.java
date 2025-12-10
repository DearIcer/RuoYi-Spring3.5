package com.ruoyi.project.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.security.LoginBody;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.client.domain.ClientLoginUser;
import com.ruoyi.project.client.domain.ClientUser;
import com.ruoyi.project.client.service.ClientLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 客户端登录验证
 * 
 * @author ruoyi
 */
@Tag(name = "客户端登录认证")
@RestController
@RequestMapping("/client")
public class ClientLoginController
{
    @Autowired
    private ClientLoginService clientLoginService;

    /**
     * 客户端登录方法
     * 
     * @param loginBody 登录信息
     * @return 结果
     */
    @Operation(summary = "客户端用户登录")
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = clientLoginService.login(loginBody.getUsername(), loginBody.getPassword());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取客户端用户信息
     * 
     * @return 用户信息
     */
    @Operation(summary = "获取客户端用户信息")
    @GetMapping("/getInfo")
    public AjaxResult getInfo()
    {
        ClientLoginUser loginUser = clientLoginService.getClientLoginUser(ServletUtils.getRequest());
        if (StringUtils.isNull(loginUser))
        {
            return AjaxResult.error("获取用户信息失败，请重新登录");
        }
        ClientUser user = loginUser.getUser();
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        return ajax;
    }

    /**
     * 客户端退出登录
     */
    @Operation(summary = "客户端用户退出")
    @PostMapping("/logout")
    public AjaxResult logout()
    {
        ClientLoginUser loginUser = clientLoginService.getClientLoginUser(ServletUtils.getRequest());
        if (StringUtils.isNotNull(loginUser))
        {
            // 删除用户缓存记录
            clientLoginService.delLoginUser(loginUser.getToken());
        }
        return AjaxResult.success("退出成功");
    }

    /**
     * 刷新令牌
     */
    @Operation(summary = "刷新客户端令牌")
    @PostMapping("/refreshToken")
    public AjaxResult refreshToken()
    {
        ClientLoginUser loginUser = clientLoginService.getClientLoginUser(ServletUtils.getRequest());
        if (StringUtils.isNull(loginUser))
        {
            return AjaxResult.error("令牌已过期，请重新登录");
        }
        // 刷新令牌
        clientLoginService.refreshToken(loginUser);
        return AjaxResult.success();
    }
}
