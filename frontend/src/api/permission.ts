import request from '../utils/request'

export function getRoleList() {
  return request.get('/api/v1/roles')
}

export function getPermissionTree(roleId: number | string) {
  return request.get(`/api/v1/roles/${roleId}/permissions`)
}

export function savePermissions(roleId: number | string, permissions: string[]) {
  return request.post(`/api/v1/roles/${roleId}/permissions`, { permissions })
}

export function listPermissions() {
  return request.get('/api/v1/permissions')
}

export function createPermission(data: any) {
  return request.post('/api/v1/permissions', data)
}

export function updatePermission(id: number | string, data: any) {
  return request.put(`/api/v1/permissions/${id}`, data)
}

export function deletePermission(id: number | string) {
  return request.delete(`/api/v1/permissions/${id}`)
}
