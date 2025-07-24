// src/api/scheduleTask.js
import request from "@/utils/request";

// 获取任务列表（含过滤）
export function getTaskList(params) {
  return request.get("/v1/schedule-task/list", { params });
}

// 新建任务
export function createTask(data) {
  return request.post("/v1/schedule-task/create", data);
}

// 编辑任务
export function updateTask(data) {
  return request.put("/v1/schedule-task/update", data);
}

// 获取任务详情
export function getTaskDetail(id) {
  return request.get(`/v1/schedule-task/${id}`);
}

// 删除任务
export function deleteTask(id) {
  return request.delete(`/v1/schedule-task/${id}`);
}

// 启用/禁用任务
export function toggleTask(id) {
  return request.put(`/v1/schedule-task/${id}/toggle`);
}
