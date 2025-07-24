import request from "@/utils/request";

// 获取社交账号列表（支持分页、搜索）
export function getSocialAccounts(params) {
  return request({
    url: "/v1/social-account",
    method: "get",
    params,
  });
}

// 获取社交账号详情
export function getSocialAccountById(id) {
  return request({
    url: `/v1/social-account/${id}`,
    method: "get",
  });
}

// 新增社交账号
export function createSocialAccount(data) {
  return request({
    url: "/v1/social-account",
    method: "post",
    data,
  });
}

// 更新社交账号
export function updateSocialAccount(id, data) {
  return request({
    url: `/v1/social-account/${id}`,
    method: "put",
    data,
  });
}

// 删除社交账号
export function deleteSocialAccount(id) {
  return request({
    url: `/v1/social-account/${id}`,
    method: "delete",
  });
}

// 获取推送日志列表（支持分页）
export function getPushLogs(params) {
  return request({
    url: "/v1/social-log",
    method: "get",
    params,
  });
}
