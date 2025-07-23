import request from '@/utils/request'

export function getCampaignList(params) {
  return request({
    url: '/marketing-campaign',
    method: 'get',
    params,
  })
}

export function getCampaignDetail(id) {
  return request({
    url: `/marketing-campaign/${id}`,
    method: 'get',
  })
}

export function createCampaign(data) {
  return request({
    url: '/marketing-campaign',
    method: 'post',
    data,
  })
}

export function updateCampaign(id, data) {
  return request({
    url: `/marketing-campaign/${id}`,
    method: 'put',
    data,
  })
}

export function deleteCampaign(id) {
  return request({
    url: `/marketing-campaign/${id}`,
    method: 'delete',
  })
}

export function publishCampaign(data) {
  return request({
    url: '/marketing-campaign/publish',
    method: 'post',
    data,
  })
}

export function toggleCampaignStatus(data) {
  return request({
    url: '/marketing-campaign/toggle',
    method: 'post',
    data,
  })
}

