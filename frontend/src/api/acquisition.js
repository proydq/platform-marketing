// src/api/acquisition.js
import request from "@/utils/request";

// 创建数据采集任务
export function createAcquisitionTask(data) {
  return request.post("/api/acquisition/tasks", data);
}

// 获取采集任务列表
export function getAcquisitionTasks(params) {
  return request.get("/api/acquisition/tasks", { params });
}

// 获取任务详情
export function getAcquisitionTaskDetail(id) {
  return request.get(`/api/acquisition/tasks/${id}`);
}

// 启动任务
export function startAcquisitionTask(id) {
  return request.post(`/api/acquisition/tasks/${id}/start`);
}

// 停止任务
export function stopAcquisitionTask(id) {
  return request.post(`/api/acquisition/tasks/${id}/stop`);
}

// 删除任务
export function deleteAcquisitionTask(id) {
  return request.delete(`/api/acquisition/tasks/${id}`);
}

// 获取采集到的数据
export function getAcquisitionData(params) {
  return request.get("/api/acquisition/data", { params });
}

// 获取数据详情
export function getAcquisitionDataDetail(id) {
  return request.get(`/api/acquisition/data/${id}`);
}

// 导出采集数据
export function exportAcquisitionData(ids) {
  return request.post("/api/acquisition/data/export", { ids });
}

// 删除采集数据
export function deleteAcquisitionData(id) {
  return request.delete(`/api/acquisition/data/${id}`);
}

// 将采集数据转换为客户
export function convertToCustomer(data) {
  return request.post("/api/acquisition/data/convert-to-customer", data);
}

// 获取采集统计
export function getAcquisitionStats() {
  return request.get("/api/acquisition/stats");
}