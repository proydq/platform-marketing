import request from "@/utils/request";

/**
 * 客户分页查询
 * GET /v1/customers
 */
export function getCustomerList(params) {
  return request({
    url: "/v1/customers",
    method: "get",
    params,
  });
}

/**
 * 新增客户
 * POST /v1/customers
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
 * PUT /v1/customers/{id}
 */
export function updateCustomer(id, data) {
  return request({
    url: `/v1/customers/${id}`,
    method: "put",
    data,
  });
}

/**
 * 查询客户详情
 * GET /v1/customers/{id}
 */
export function getCustomer(id) {
  return request({
    url: `/v1/customers/${id}`,
    method: "get",
  });
}

/**
 * 删除客户
 * DELETE /v1/customers/{id}
 */
export function deleteCustomer(id) {
  return request({
    url: `/v1/customers/${id}`,
    method: "delete",
  });
}

/**
 * 修改客户状态
 * POST /v1/customers/update-status
 */
export function updateCustomerStatus(data) {
  return request({
    url: "/v1/customers/update-status",
    method: "post",
    data,
  });
}
// 导入接口
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
