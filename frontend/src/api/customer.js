import request from "@/utils/request";

// ========== 客户管理 API ==========

/**
 * 客户分页查询
 * GET /api/v1/customers
 */
export function getCustomerList(params) {
  return request({
    url: "/v1/customers",
    method: "get",
    params,
  });
}

/**
 * 获取客户详情
 * GET /api/v1/customers/{id}
 */
export function getCustomer(id) {
  return request({
    url: `/api/v1/customers/${id}`,
    method: "get",
  });
}

/**
 * 新增客户
 * POST /api/v1/customers
 */
export function createCustomer(data) {
  return request({
    url: "/v1/customers",
    method: "post",
    data,
  });
}

/**
 * 编辑客户
 * PUT /api/v1/customers/{id}
 */
export function updateCustomer(id, data) {
  return request({
    url: `/api/v1/customers/${id}`,
    method: "put",
    data,
  });
}

/**
 * 删除客户
 * DELETE /api/v1/customers/{id}
 */
export function deleteCustomer(id) {
  return request({
    url: `/api/v1/customers/${id}`,
    method: "delete",
  });
}

/**
 * 修改客户状态
 * PUT /api/v1/customers/{id}/status
 */
export function updateCustomerStatus(id, status) {
  return request({
    url: `/api/v1/customers/${id}/status`,
    method: "put",
    data: { status },
  });
}

/**
 * 导入客户数据
 * POST /api/v1/customers/import
 */
export function importCustomers(file) {
  const formData = new FormData();
  formData.append("file", file);

  return request({
    url: "/v1/customers/import",
    method: "post",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

/**
 * 导出客户数据
 * POST /api/v1/customers/export
 */
export function exportCustomers(params) {
  return request({
    url: "/v1/customers/export",
    method: "post",
    data: params,
    responseType: 'blob',
  });
}

/**
 * 获取客户来源选项
 * GET /api/v1/customers/sources
 */
export function getCustomerSources() {
  return request({
    url: "/v1/customers/sources",
    method: "get",
  });
}

// ========== 客户获取/采集 API ==========

/**
 * 获取采集任务列表
 * GET /api/acquisition/tasks
 */
export function getAcquisitionTasks(params) {
  return request({
    url: "/v1/acquisition/tasks",
    method: "get",
    params,
  });
}

/**
 * 获取采集任务详情
 * GET /api/acquisition/tasks/{id}
 */
export function getAcquisitionTask(id) {
  return request({
    url: `/api/acquisition/tasks/${id}`,
    method: "get",
  });
}

/**
 * 创建采集任务
 * POST /api/acquisition/tasks
 */
export function createAcquisitionTask(data) {
  return request({
    url: "/v1/acquisition/tasks",
    method: "post",
    data,
  });
}

/**
 * 更新采集任务
 * PUT /api/acquisition/tasks/{id}
 */
export function updateAcquisitionTask(id, data) {
  return request({
    url: `/api/acquisition/tasks/${id}`,
    method: "put",
    data,
  });
}

/**
 * 删除采集任务
 * DELETE /api/acquisition/tasks/{id}
 */
export function deleteAcquisitionTask(id) {
  return request({
    url: `/api/acquisition/tasks/${id}`,
    method: "delete",
  });
}

/**
 * 启动采集任务
 * POST /api/acquisition/tasks/{id}/start
 */
export function startAcquisitionTask(id) {
  return request({
    url: `/api/acquisition/tasks/${id}/start`,
    method: "post",
  });
}

/**
 * 停止采集任务
 * POST /api/acquisition/tasks/{id}/stop
 */
export function stopAcquisitionTask(id) {
  return request({
    url: `/api/acquisition/tasks/${id}/stop`,
    method: "post",
  });
}

/**
 * 创建快速采集任务
 * POST /api/acquisition/tasks/quick
 */
export function createQuickAcquisition(data) {
  return request({
    url: "/v1/acquisition/tasks/quick",
    method: "post",
    data,
  });
}

/**
 * 获取采集预览数据
 * GET /api/acquisition/tasks/preview
 */
export function getAcquisitionPreview(params) {
  return request({
    url: "/v1/acquisition/tasks/preview",
    method: "get",
    params,
  });
}

/**
 * 导出任务数据
 * GET /api/acquisition/tasks/{id}/export
 */
export function exportTaskData(id) {
  return request({
    url: `/api/acquisition/tasks/${id}/export`,
    method: "get",
    responseType: 'blob',
  });
}

// ========== 客户分析 API ==========

/**
 * 获取客户分析概览统计
 * GET /api/v1/customer-analytics/overview
 */
export function getCustomerAnalytics(params) {
  return request({
    url: "/v1/customer-analytics/overview",
    method: "get",
    params,
  });
}

/**
 * 获取客户增长趋势数据
 * GET /api/v1/customer-analytics/growth-trend
 */
export function getGrowthTrendData(params) {
  return request({
    url: "/v1/customer-analytics/growth-trend",
    method: "get",
    params,
  });
}

/**
 * 获取客户来源分布数据
 * GET /api/v1/customer-analytics/source-distribution
 */
export function getSourceDistributionData(params) {
  return request({
    url: "/v1/customer-analytics/source-distribution",
    method: "get",
    params,
  });
}

/**
 * 获取客户状态分布数据
 * GET /api/v1/customer-analytics/status-distribution
 */
export function getStatusDistributionData(params) {
  return request({
    url: "/v1/customer-analytics/status-distribution",
    method: "get",
    params,
  });
}

/**
 * 获取转化率数据
 * GET /api/v1/customer-analytics/conversion-rate
 */
export function getConversionRateData(params) {
  return request({
    url: "/v1/customer-analytics/conversion-rate",
    method: "get",
    params,
  });
}

/**
 * 获取分析表格数据
 * GET /api/v1/customer-analytics/table-data
 */
export function getAnalyticsTableData(params) {
  return request({
    url: "/v1/customer-analytics/table-data",
    method: "get",
    params,
  });
}

/**
 * 导出分析报告
 * POST /api/v1/customer-analytics/export
 */
export function exportAnalyticsReport(params) {
  return request({
    url: "/v1/customer-analytics/export",
    method: "post",
    data: params,
    responseType: 'blob',
  });
}