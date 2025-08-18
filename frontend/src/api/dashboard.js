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

// 创建新任务
export function createTask(data) {
  return request.post("/v1/dashboard/tasks", data);
}

// 更新任务状态
export function updateTaskStatus(id, status) {
  return request.put(`/v1/dashboard/tasks/${id}/status`, { status });
}

// 删除任务
export function deleteTask(id) {
  return request.delete(`/v1/dashboard/tasks/${id}`);
}

// 重启任务
export function retryTask(id) {
  return request.post(`/v1/dashboard/tasks/${id}/retry`);
}
