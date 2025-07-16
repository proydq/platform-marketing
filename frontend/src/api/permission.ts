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
