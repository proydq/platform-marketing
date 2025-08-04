import request from '@/utils/request'

export function uploadImage(data) {
  return request({
    url: '/file-info/uploadFile',
    method: 'post',
    data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function sendEmail(data) {
  return request({
    url: '/v1/email/send',
    method: 'post',
    data
  })
}

export function sendTestEmail(data) {
  return request({
    url: '/v1/email/test',
    method: 'post',
    data
  })
}

export function getEmailRecords() {
  return request({
    url: '/v1/email/records',
    method: 'get'
  })
}
