import request from '@/utils/request'

// 查询客户端在线用户列表
export function list(query) {
  return request({
    url: '/client/online/list',
    method: 'get',
    params: query
  })
}

// 强退客户端用户
export function forceLogout(tokenId) {
  return request({
    url: '/client/online/' + tokenId,
    method: 'delete'
  })
}
