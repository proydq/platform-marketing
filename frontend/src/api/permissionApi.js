import request from '../utils/request'

export function listPermissions(params) {
  return request.get('/api/v1/permissions', { params })
}

export function getPermission(id) {
  return request.get(`/api/v1/permissions/${id}`)
}

export function createPermission(data) {
  return request.post('/api/v1/permissions', data)
}

export function updatePermission(id, data) {
  return request.put(`/api/v1/permissions/${id}`, data)
}

export function deletePermission(id) {
  return request.delete(`/api/v1/permissions/${id}`)
}

export function deletePermissions(ids) {
  return request.delete('/api/v1/permissions', { data: ids })
}
