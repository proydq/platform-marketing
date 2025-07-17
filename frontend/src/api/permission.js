import request from '../utils/request'

export function fetchPermissions(params) {
  return request.get('/v1/permissions', { params })
}

export function createPermission(data) {
  return request.post('/v1/permissions', data)
}

export function updatePermission(id, data) {
  return request.put(`/v1/permissions/${id}`, data)
}

export function deletePermission(id) {
  return request.delete(`/v1/permissions/${id}`)
}
