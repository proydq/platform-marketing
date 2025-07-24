import request from "@/utils/request";

// 获取内容生成分页列表
export function getContentGenerationList(params) {
  return request({
    url: "/v1/content-generation",
    method: "get",
    params,
  });
}

// 获取单条详情
export function getContentGenerationDetail(id) {
  return request({
    url: `/v1/content-generation/${id}`,
    method: "get",
  });
}

// 创建内容
export function createContentGeneration(data) {
  return request({
    url: "/v1/content-generation",
    method: "post",
    data,
  });
}

// 更新内容
export function updateContentGeneration(id, data) {
  return request({
    url: `/v1/content-generation/${id}`,
    method: "put",
    data,
  });
}

// 删除内容
export function deleteContentGeneration(id) {
  return request({
    url: `/v1/content-generation/${id}`,
    method: "delete",
  });
}
