import request from '../utils/request'

/**
 * 查询权限分页列表
 * GET /v1/permissions
 * 支持查询参数：page, size, keyword, type, status
 */
export function fetchPermissions(params) {
  return request({
    url: '/v1/permissions',
    method: 'get',
    params
  })
}

/**
 * 创建权限项
 * POST /v1/permissions
 */
export function createPermission(data) {
  return request({
    url: '/v1/permissions',
    method: 'post',
    data
  })
}

/**
 * 更新权限项
 * POST /v1/permissions/update
 */
export function updatePermission(data) {
  return request({
    url: '/v1/permissions/update',
    method: 'post',
    data
  })
}

/**
 * 删除单个权限项
 * DELETE /v1/permissions/{id}
 */
export function deletePermission(id) {
  return request({
    url: `/v1/permissions/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除权限项
 * POST /v1/permissions/delete-batch
 * @param {Array<string>} ids - 权限ID数组
 */
export function deletePermissionsBatch(ids) {
  return request({
    url: '/v1/permissions/delete-batch',
    method: 'post',
    data: ids
  })
}

/**
 * 获取权限树结构
 * GET /v1/permissions/tree
 */
export function fetchPermissionTree() {
  return request({
    url: '/v1/permissions/tree',
    method: 'get'
  })
}

/**
 * 切换权限状态（启用/禁用）
 * POST /v1/permissions/update-status
 */
export function updatePermissionStatus(id, status) {
  return request({
    url: '/v1/permissions/update-status',
    method: 'post',
    data: { id, status }
  })
}