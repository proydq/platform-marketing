// src/api/dashboard.js
import request from "@/utils/request";

// 获取统计卡片数据
export function getDashboardStats() {
  return request.get("/v1/dashboard/stats");
}

// 获取邮件发送趋势图
export function getSendTrend() {
  return request.get("/v1/dashboard/trend/send");
}

// 获取客户增长趋势图
export function getCustomerTrend() {
  return request.get("/v1/dashboard/trend/customers");
}

// 获取最近任务列表
export function getRecentTasks() {
  return request.get("/v1/dashboard/tasks");
}

// 获取任务详情
export function getTaskDetail(id) {
  return request.get(`/v1/dashboard/tasks/${id}`);
}

// 获取关键词搜索量
export function getKeywordSearchCount(keyword) {
  return request({
    url: '/v1/dashboard/keyword-search-count',
    method: 'get',
    params: { keyword }
  });
}

// 获取关键词搜索量
export function getKeywordSearchCount(keyword) {
  return request({
    url: '/v1/dashboard/keyword-search-count',
    method: 'get',
    params: { keyword }
  });
}
