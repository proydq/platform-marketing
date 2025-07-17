import request from '../utils/request'

/**
 * 获取用户分页列表
 * GET /v1/users?page=&size=&keyword=
 */
export function fetchUsers(params) {
  return request({
    url: '/v1/users',
    method: 'get',
    params
  })
}

/**
 * 创建用户
 * POST /v1/users
 */
export function createUser(data) {
  return request({
    url: '/v1/users',
    method: 'post',
    data
  })
}

/**
 * 更新用户信息
 * PUT /v1/users/{id}
 */
export function updateUser(id, data) {
  return request({
    url: `/v1/users/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除用户
 * DELETE /v1/users/{id}
 */
export function deleteUser(id) {
  return request({
    url: `/v1/users/${id}`,
    method: 'delete'
  })
}

/**
 * 重置用户密码
 * POST /v1/users/{id}/reset-password
 */
export function resetUserPassword(id) {
  return request({
    url: `/v1/users/${id}/reset-password`,
    method: 'post'
  })
}

/**
 * 更新用户状态（启用/禁用）
 * PUT /v1/users/{id}/status
 */
export function updateUserStatus(id, status) {
  return request({
    url: `/v1/users/${id}/status`,
    method: 'put',
    data: { status }
  })
}
/**
 * 批量导入用户（例如上传 Excel）
 * POST /v1/users/import
 */
export function importUsers(fileFormData) {
  return request({
    url: '/v1/users/import',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data: fileFormData
  })
}