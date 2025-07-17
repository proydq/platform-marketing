import request from '../utils/request'

/**
 * 获取角色列表（分页）
 */
export function fetchRoles(params) {
  return request({
    url: '/v1/roles',
    method: 'get',
    params
  })
}

/**
 * 获取单个角色信息
 */
export function getRole(id) {
  return request({
    url: `/v1/roles/${id}`,
    method: 'get'
  })
}

/**
 * 新增角色
 */
export function createRole(data) {
  return request({
    url: '/v1/roles',
    method: 'post',
    data
  })
}

/**
 * 更新角色信息（✅ 修复路径问题，必须带 id）
 */
export function updateRole(id, data) {
  return request({
    url: `/v1/roles/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除角色
 */
export function deleteRole(id) {
  return request({
    url: `/v1/roles/${id}`,
    method: 'delete'
  })
}

/**
 * 绑定权限到角色
 */
export function bindPermissions(roleId, permissionIds) {
  return request({
    url: `/v1/roles/${roleId}/permissions`,
    method: 'post',
    data: permissionIds
  })
}
export function fetchRolePermissions(roleId) {
  return request({
    url: `/v1/roles/${roleId}/permissions`,
    method: 'get'
  })
}