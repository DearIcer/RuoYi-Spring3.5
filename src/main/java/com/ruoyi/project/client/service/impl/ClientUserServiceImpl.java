package com.ruoyi.project.client.service.impl;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.project.client.domain.ClientUser;
import com.ruoyi.project.client.mapper.ClientUserMapper;
import com.ruoyi.project.client.service.IClientUserService;

/**
 * 客户端用户 业务层处理
 * 
 * @author ruoyi
 */
@Service
public class ClientUserServiceImpl implements IClientUserService
{
    private static final Logger log = LoggerFactory.getLogger(ClientUserServiceImpl.class);

    @Autowired
    private ClientUserMapper clientUserMapper;

    /**
     * 根据条件分页查询客户端用户列表
     * 
     * @param clientUser 客户端用户信息
     * @return 客户端用户信息集合信息
     */
    @Override
    public List<ClientUser> selectClientUserList(ClientUser clientUser)
    {
        return clientUserMapper.selectClientUserList(clientUser);
    }

    /**
     * 通过用户名查询客户端用户
     * 
     * @param userName 用户名
     * @return 客户端用户对象信息
     */
    @Override
    public ClientUser selectClientUserByUserName(String userName)
    {
        return clientUserMapper.selectClientUserByUserName(userName);
    }

    /**
     * 通过用户ID查询客户端用户
     * 
     * @param userId 用户ID
     * @return 客户端用户对象信息
     */
    @Override
    public ClientUser selectClientUserById(Long userId)
    {
        return clientUserMapper.selectClientUserById(userId);
    }

    /**
     * 校验用户名称是否唯一
     * 
     * @param clientUser 客户端用户信息
     * @return 结果
     */
    @Override
    public boolean checkUserNameUnique(ClientUser clientUser)
    {
        Long userId = StringUtils.isNull(clientUser.getUserId()) ? -1L : clientUser.getUserId();
        ClientUser info = clientUserMapper.checkUserNameUnique(clientUser.getUserName());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验手机号码是否唯一
     *
     * @param clientUser 客户端用户信息
     * @return 结果
     */
    @Override
    public boolean checkPhoneUnique(ClientUser clientUser)
    {
        Long userId = StringUtils.isNull(clientUser.getUserId()) ? -1L : clientUser.getUserId();
        ClientUser info = clientUserMapper.checkPhoneUnique(clientUser.getPhonenumber());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验email是否唯一
     *
     * @param clientUser 客户端用户信息
     * @return 结果
     */
    @Override
    public boolean checkEmailUnique(ClientUser clientUser)
    {
        Long userId = StringUtils.isNull(clientUser.getUserId()) ? -1L : clientUser.getUserId();
        ClientUser info = clientUserMapper.checkEmailUnique(clientUser.getEmail());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验客户端用户是否允许操作
     * 
     * @param clientUser 客户端用户信息
     */
    @Override
    public void checkClientUserAllowed(ClientUser clientUser)
    {
        // 客户端用户没有管理员概念，可根据业务需要扩展
    }

    /**
     * 新增保存客户端用户信息
     * 
     * @param clientUser 客户端用户信息
     * @return 结果
     */
    @Override
    public int insertClientUser(ClientUser clientUser)
    {
        return clientUserMapper.insertClientUser(clientUser);
    }

    /**
     * 注册客户端用户信息
     * 
     * @param clientUser 客户端用户信息
     * @return 结果
     */
    @Override
    public boolean registerClientUser(ClientUser clientUser)
    {
        return clientUserMapper.insertClientUser(clientUser) > 0;
    }

    /**
     * 修改保存客户端用户信息
     * 
     * @param clientUser 客户端用户信息
     * @return 结果
     */
    @Override
    public int updateClientUser(ClientUser clientUser)
    {
        return clientUserMapper.updateClientUser(clientUser);
    }

    /**
     * 修改客户端用户状态
     * 
     * @param clientUser 客户端用户信息
     * @return 结果
     */
    @Override
    public int updateClientUserStatus(ClientUser clientUser)
    {
        return clientUserMapper.updateClientUserStatus(clientUser.getUserId(), clientUser.getStatus());
    }

    /**
     * 修改客户端用户基本信息
     * 
     * @param clientUser 客户端用户信息
     * @return 结果
     */
    @Override
    public int updateClientUserProfile(ClientUser clientUser)
    {
        return clientUserMapper.updateClientUser(clientUser);
    }

    /**
     * 修改客户端用户头像
     * 
     * @param userId 用户ID
     * @param avatar 头像地址
     * @return 结果
     */
    @Override
    public boolean updateClientUserAvatar(Long userId, String avatar)
    {
        return clientUserMapper.updateClientUserAvatar(userId, avatar) > 0;
    }

    /**
     * 更新客户端用户登录信息（IP和登录时间）
     * 
     * @param userId 用户ID
     * @param loginIp 登录IP地址
     * @param loginDate 登录时间
     */
    @Override
    public void updateLoginInfo(Long userId, String loginIp, Date loginDate)
    {
        clientUserMapper.updateLoginInfo(userId, loginIp, loginDate);
    }

    /**
     * 重置客户端用户密码
     * 
     * @param clientUser 客户端用户信息
     * @return 结果
     */
    @Override
    public int resetPwd(ClientUser clientUser)
    {
        return clientUserMapper.resetClientUserPwd(clientUser.getUserId(), clientUser.getPassword());
    }

    /**
     * 重置客户端用户密码
     * 
     * @param userId 用户ID
     * @param password 密码
     * @return 结果
     */
    @Override
    public int resetClientUserPwd(Long userId, String password)
    {
        return clientUserMapper.resetClientUserPwd(userId, password);
    }

    /**
     * 通过用户ID删除客户端用户
     * 
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public int deleteClientUserById(Long userId)
    {
        return clientUserMapper.deleteClientUserById(userId);
    }

    /**
     * 批量删除客户端用户信息
     * 
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    public int deleteClientUserByIds(Long[] userIds)
    {
        for (Long userId : userIds)
        {
            checkClientUserAllowed(new ClientUser(userId));
        }
        return clientUserMapper.deleteClientUserByIds(userIds);
    }
}
