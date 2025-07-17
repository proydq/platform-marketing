import request from '../utils/request'

export function getRoleList() {
  return request.get('/v1/roles')
}

export function createRole(data) {
  return request.post('/v1/roles', data)
}

export function updateRole(id, data) {
  return request.put(`/v1/roles/${id}`, data)
}

export function deleteRole(id) {
  return request.delete(`/v1/roles/${id}`)
}

export function bindRolePermissions(id, permissions) {
  return request.post(`/v1/roles/${id}/permissions`, { permissions })
}
