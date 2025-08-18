/**
 * 营销渠道管理API
 */
import request from '@/utils/request';

/**
 * 获取渠道列表
 * GET /api/channels
 */
export function getChannels(params = {}) {
  return request({
    url: '/api/channels',
    method: 'get',
    params
  });
}

/**
 * 获取渠道详情
 * GET /api/channels/{id}
 */
export function getChannel(id) {
  return request({
    url: `/api/channels/${id}`,
    method: 'get'
  });
}

/**
 * 创建渠道
 * POST /api/channels
 */
export function createChannel(data) {
  return request({
    url: '/api/channels',
    method: 'post',
    data
  });
}

/**
 * 更新渠道
 * PUT /api/channels/{id}
 */
export function updateChannel(id, data) {
  return request({
    url: `/api/channels/${id}`,
    method: 'put',
    data
  });
}

/**
 * 删除渠道
 * DELETE /api/channels/{id}
 */
export function deleteChannel(id) {
  return request({
    url: `/api/channels/${id}`,
    method: 'delete'
  });
}

/**
 * 启用/禁用渠道
 * PATCH /api/channels/{id}/toggle
 */
export function toggleChannel(id, enabled) {
  return request({
    url: `/api/channels/${id}/toggle`,
    method: 'patch',
    data: { enabled }
  });
}

/**
 * 测试渠道连接
 * POST /api/channels/{id}/test
 */
export function testChannel(id, testData = {}) {
  return request({
    url: `/api/channels/${id}/test`,
    method: 'post',
    data: testData
  });
}

/**
 * 获取渠道统计信息
 * GET /api/channels/stats
 */
export function getChannelStats(params = {}) {
  return request({
    url: '/api/channels/stats',
    method: 'get',
    params
  });
}