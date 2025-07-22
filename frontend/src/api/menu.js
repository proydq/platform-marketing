import request from '../utils/request'

export function fetchMenus(params) {
  return request({
    url: '/v1/menus',
    method: 'get',
    params
  })
}

export function fetchMenuTree() {
  return request({
    url: '/v1/menus/tree',
    method: 'get'
  })
}

export function createMenu(data) {
  return request({
    url: '/v1/menus',
    method: 'post',
    data
  })
}

export function updateMenu(id, data) {
  return request({
    url: `/v1/menus/${id}`,
    method: 'put',
    data
  })
}

export function deleteMenu(id) {
  return request({
    url: `/v1/menus/${id}`,
    method: 'delete'
  })
}

export function updateMenuStatus(id, status) {
  return request({
    url: '/v1/menus/update-status',
    method: 'post',
    data: { id, status }
  })
}
