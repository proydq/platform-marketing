import request from '../utils/request'

export function getUserList(params) {
  return request.get('/v1/users', { params })
}

export function getUserDetail(id) {
  return request.get(`/v1/users/${id}`)
}

export function updateUser(id, data) {
  return request.put(`/v1/users/${id}`, data)
}

export function importUsers(formData) {
  return request.post('/v1/users/import', formData)
}

export function createUser(data) {
  return request.post('/v1/users', data)
}

export function deleteUser(id) {
  return request.delete(`/v1/users/${id}`)
}

export function resetUserPassword(id) {
  return request.post(`/v1/users/${id}/reset-password`)
}
