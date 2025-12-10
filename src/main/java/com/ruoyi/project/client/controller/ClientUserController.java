package com.ruoyi.project.client.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.project.client.domain.ClientUser;
import com.ruoyi.project.client.service.IClientUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 客户端用户信息
 * 
 * @author ruoyi
 */
@Tag(name = "客户端用户管理")
@RestController
@RequestMapping("/client/user")
public class ClientUserController extends BaseController
{
    @Autowired
    private IClientUserService clientUserService;

    /**
     * 获取客户端用户列表
     */
    @Operation(summary = "获取客户端用户列表")
    @PreAuthorize("@ss.hasPermi('client:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(ClientUser clientUser)
    {
        startPage();
        List<ClientUser> list = clientUserService.selectClientUserList(clientUser);
        return getDataTable(list);
    }

    /**
     * 导出客户端用户列表
     */
    @Operation(summary = "导出客户端用户列表")
    @Log(title = "客户端用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('client:user:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, ClientUser clientUser)
    {
        List<ClientUser> list = clientUserService.selectClientUserList(clientUser);
        ExcelUtil<ClientUser> util = new ExcelUtil<ClientUser>(ClientUser.class);
        util.exportExcel(response, list, "客户端用户数据");
    }

    /**
     * 根据用户编号获取详细信息
     */
    @Operation(summary = "根据用户ID获取详细信息")
    @PreAuthorize("@ss.hasPermi('client:user:query')")
    @GetMapping(value = { "/", "/{userId}" })
    public AjaxResult getInfo(@Parameter(description = "用户ID") @PathVariable(value = "userId", required = false) Long userId)
    {
        AjaxResult ajax = AjaxResult.success();
        if (StringUtils.isNotNull(userId))
        {
            ClientUser clientUser = clientUserService.selectClientUserById(userId);
            ajax.put(AjaxResult.DATA_TAG, clientUser);
        }
        return ajax;
    }

    /**
     * 新增客户端用户
     */
    @Operation(summary = "新增客户端用户")
    @PreAuthorize("@ss.hasPermi('client:user:add')")
    @Log(title = "客户端用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ClientUser clientUser)
    {
        if (!clientUserService.checkUserNameUnique(clientUser))
        {
            return error("新增客户端用户'" + clientUser.getUserName() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(clientUser.getPhonenumber()) && !clientUserService.checkPhoneUnique(clientUser))
        {
            return error("新增客户端用户'" + clientUser.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(clientUser.getEmail()) && !clientUserService.checkEmailUnique(clientUser))
        {
            return error("新增客户端用户'" + clientUser.getUserName() + "'失败，邮箱账号已存在");
        }
        clientUser.setCreateBy(getUsername());
        clientUser.setPassword(SecurityUtils.encryptPassword(clientUser.getPassword()));
        return toAjax(clientUserService.insertClientUser(clientUser));
    }

    /**
     * 修改客户端用户
     */
    @Operation(summary = "修改客户端用户")
    @PreAuthorize("@ss.hasPermi('client:user:edit')")
    @Log(title = "客户端用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ClientUser clientUser)
    {
        clientUserService.checkClientUserAllowed(clientUser);
        if (!clientUserService.checkUserNameUnique(clientUser))
        {
            return error("修改客户端用户'" + clientUser.getUserName() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(clientUser.getPhonenumber()) && !clientUserService.checkPhoneUnique(clientUser))
        {
            return error("修改客户端用户'" + clientUser.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(clientUser.getEmail()) && !clientUserService.checkEmailUnique(clientUser))
        {
            return error("修改客户端用户'" + clientUser.getUserName() + "'失败，邮箱账号已存在");
        }
        clientUser.setUpdateBy(getUsername());
        return toAjax(clientUserService.updateClientUser(clientUser));
    }

    /**
     * 删除客户端用户
     */
    @Operation(summary = "删除客户端用户")
    @PreAuthorize("@ss.hasPermi('client:user:remove')")
    @Log(title = "客户端用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@Parameter(description = "用户ID数组") @PathVariable Long[] userIds)
    {
        return toAjax(clientUserService.deleteClientUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @Operation(summary = "重置客户端用户密码")
    @PreAuthorize("@ss.hasPermi('client:user:resetPwd')")
    @Log(title = "客户端用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody ClientUser clientUser)
    {
        clientUserService.checkClientUserAllowed(clientUser);
        clientUser.setPassword(SecurityUtils.encryptPassword(clientUser.getPassword()));
        clientUser.setUpdateBy(getUsername());
        return toAjax(clientUserService.resetPwd(clientUser));
    }

    /**
     * 状态修改
     */
    @Operation(summary = "修改客户端用户状态")
    @PreAuthorize("@ss.hasPermi('client:user:edit')")
    @Log(title = "客户端用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ClientUser clientUser)
    {
        clientUserService.checkClientUserAllowed(clientUser);
        clientUser.setUpdateBy(getUsername());
        return toAjax(clientUserService.updateClientUserStatus(clientUser));
    }
}
