import request from '../utils/request'

export function login(credentials) {
  return request({
    url: '/v1/auth/login',
    method: 'post',
    data: credentials
  })
}

export function getCurrentUser() {
  return request({
    url: '/v1/user/me',
    method: 'get'
  })
}

export function logout() {
  localStorage.removeItem('token')
}
