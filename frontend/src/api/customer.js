import request from '../utils/request'

export function fetchCustomers(params) {
  return request({
    url: '/v1/customers',
    method: 'get',
    params
  })
}

export function createCustomer(data) {
  return request({
    url: '/v1/customers',
    method: 'post',
    data
  })
}

export function updateCustomer(id, data) {
  return request({
    url: `/v1/customers/${id}`,
    method: 'put',
    data
  })
}

export function deleteCustomer(id) {
  return request({
    url: `/v1/customers/${id}`,
    method: 'delete'
  })
}

export function updateCustomerStatus(id, status) {
  return request({
    url: '/v1/customers/update-status',
    method: 'post',
    data: { id, status }
  })
}
