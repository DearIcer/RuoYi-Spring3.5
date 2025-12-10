package com.ruoyi.project.client.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.project.client.domain.ClientUser;

/**
 * 客户端用户表 数据层
 * 
 * @author ruoyi
 */
public interface ClientUserMapper
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
     * 新增客户端用户信息
     * 
     * @param clientUser 客户端用户信息
     * @return 结果
     */
    public int insertClientUser(ClientUser clientUser);

    /**
     * 修改客户端用户信息
     * 
     * @param clientUser 客户端用户信息
     * @return 结果
     */
    public int updateClientUser(ClientUser clientUser);

    /**
     * 修改客户端用户头像
     * 
     * @param userId 用户ID
     * @param avatar 头像地址
     * @return 结果
     */
    public int updateClientUserAvatar(@Param("userId") Long userId, @Param("avatar") String avatar);

    /**
     * 修改客户端用户状态
     * 
     * @param userId 用户ID
     * @param status 状态
     * @return 结果
     */
    public int updateClientUserStatus(@Param("userId") Long userId, @Param("status") String status);

    /**
     * 更新客户端用户登录信息（IP和登录时间）
     * 
     * @param userId 用户ID
     * @param loginIp 登录IP地址
     * @param loginDate 登录时间
     * @return 结果
     */
    public int updateLoginInfo(@Param("userId") Long userId, @Param("loginIp") String loginIp, @Param("loginDate") Date loginDate);

    /**
     * 重置客户端用户密码
     * 
     * @param userId 用户ID
     * @param password 密码
     * @return 结果
     */
    public int resetClientUserPwd(@Param("userId") Long userId, @Param("password") String password);

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

    /**
     * 校验用户名称是否唯一
     * 
     * @param userName 用户名称
     * @return 结果
     */
    public ClientUser checkUserNameUnique(String userName);

    /**
     * 校验手机号码是否唯一
     *
     * @param phonenumber 手机号码
     * @return 结果
     */
    public ClientUser checkPhoneUnique(String phonenumber);

    /**
     * 校验email是否唯一
     *
     * @param email 用户邮箱
     * @return 结果
     */
    public ClientUser checkEmailUnique(String email);
}
