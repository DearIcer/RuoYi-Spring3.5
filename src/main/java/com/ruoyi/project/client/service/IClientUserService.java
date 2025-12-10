package com.ruoyi.project.client.service;

import java.util.Date;
import java.util.List;
import com.ruoyi.project.client.domain.ClientUser;

/**
 * 客户端用户 业务层
 * 
 * @author ruoyi
 */
public interface IClientUserService
{
    /**
     * 根据条件分页查询客户端用户列表
     * 
     * @param clientUser 客户端用户信息
     * @return 客户端用户信息集合信息
     */
    public List<ClientUser> selectClientUserList(ClientUser clientUser);

    /**
     * 通过用户名查询客户端用户
     * 
     * @param userName 用户名
     * @return 客户端用户对象信息
     */
    public ClientUser selectClientUserByUserName(String userName);

    /**
     * 通过用户ID查询客户端用户
     * 
     * @param userId 用户ID
     * @return 客户端用户对象信息
     */
    public ClientUser selectClientUserById(Long userId);

    /**
     * 校验用户名称是否唯一
     * 
     * @param clientUser 客户端用户信息
     * @return 结果
     */
    public boolean checkUserNameUnique(ClientUser clientUser);

    /**
     * 校验手机号码是否唯一
     *
     * @param clientUser 客户端用户信息
     * @return 结果
     */
    public boolean checkPhoneUnique(ClientUser clientUser);

    /**
     * 校验email是否唯一
     *
     * @param clientUser 客户端用户信息
     * @return 结果
     */
    public boolean checkEmailUnique(ClientUser clientUser);

    /**
     * 校验客户端用户是否允许操作
     * 
     * @param clientUser 客户端用户信息
     */
    public void checkClientUserAllowed(ClientUser clientUser);

    /**
     * 新增客户端用户信息
     * 
     * @param clientUser 客户端用户信息
     * @return 结果
     */
    public int insertClientUser(ClientUser clientUser);

    /**
     * 注册客户端用户信息
     * 
     * @param clientUser 客户端用户信息
     * @return 结果
     */
    public boolean registerClientUser(ClientUser clientUser);

    /**
     * 修改客户端用户信息
     * 
     * @param clientUser 客户端用户信息
     * @return 结果
     */
    public int updateClientUser(ClientUser clientUser);

    /**
     * 修改客户端用户状态
     * 
     * @param clientUser 客户端用户信息
     * @return 结果
     */
    public int updateClientUserStatus(ClientUser clientUser);

    /**
     * 修改客户端用户基本信息
     * 
     * @param clientUser 客户端用户信息
     * @return 结果
     */
    public int updateClientUserProfile(ClientUser clientUser);

    /**
     * 修改客户端用户头像
     * 
     * @param userId 用户ID
     * @param avatar 头像地址
     * @return 结果
     */
    public boolean updateClientUserAvatar(Long userId, String avatar);

    /**
     * 更新客户端用户登录信息
     * 
     * @param userId 用户ID
     * @param loginIp 登录IP
     * @param loginDate 登录时间
     */
    public void updateLoginInfo(Long userId, String loginIp, Date loginDate);

    /**
     * 重置客户端用户密码
     * 
     * @param clientUser 客户端用户信息
     * @return 结果
     */
    public int resetPwd(ClientUser clientUser);

    /**
     * 重置客户端用户密码
     * 
     * @param userId 用户ID
     * @param password 密码
     * @return 结果
     */
    public int resetClientUserPwd(Long userId, String password);

    /**
     * 通过用户ID删除客户端用户
     * 
     * @param userId 用户ID
     * @return 结果
     */
    public int deleteClientUserById(Long userId);

    /**
     * 批量删除客户端用户信息
     * 
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    public int deleteClientUserByIds(Long[] userIds);
}
