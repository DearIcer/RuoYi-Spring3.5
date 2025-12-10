import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 查询客户端用户列表
export function listClientUser(query) {
  return request({
    url: '/client/user/list',
    method: 'get',
    params: query
  })
}

// 查询客户端用户详细
export function getClientUser(userId) {
  return request({
    url: '/client/user/' + parseStrEmpty(userId),
    method: 'get'
  })
}

// 新增客户端用户
export function addClientUser(data) {
  return request({
    url: '/client/user',
    method: 'post',
    data: data
  })
}

// 修改客户端用户
export function updateClientUser(data) {
  return request({
    url: '/client/user',
    method: 'put',
    data: data
  })
}

// 删除客户端用户
export function delClientUser(userId) {
  return request({
    url: '/client/user/' + userId,
    method: 'delete'
  })
}

// 客户端用户密码重置
export function resetClientUserPwd(userId, password) {
  const data = {
    userId,
    password
  }
  return request({
    url: '/client/user/resetPwd',
    method: 'put',
    data: data
  })
}

// 客户端用户状态修改
export function changeClientUserStatus(userId, status) {
  const data = {
    userId,
    status
  }
  return request({
    url: '/client/user/changeStatus',
    method: 'put',
    data: data
  })
}
