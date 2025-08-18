import request from "@/utils/request";

// 获取客户采集分页列表
export function getCustomerList(params) {
  return request({
    url: "/v1/customer-collect",
    method: "get",
    params,
  });
}

// 获取客户详情
export function getCustomerDetail(id) {
  return request({
    url: `/api/customer-collect/${id}`,
    method: "get",
  });
}

// 新增客户
export function createCustomer(data) {
  return request({
    url: "/v1/customer-collect",
    method: "post",
    data,
  });
}

// 删除客户
export function deleteCustomer(id) {
  return request({
    url: `/api/customer-collect/${id}`,
    method: "delete",
  });
}

// 批量删除客户
export function batchDeleteCustomer(ids) {
  return request({
    url: "/v1/customer-collect",
    method: "delete",
    data: ids,
  });
}
export function updateCustomer(id, data) {
  return request.put(`/api/customer-collect/${id}`, data);
}
