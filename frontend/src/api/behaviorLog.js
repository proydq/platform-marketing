import request from "@/utils/request";

// 获取行为日志分页列表（支持操作类型过滤和关键词搜索）
export function getBehaviorLogList(params) {
  return request({
    url: "/v1/behavior-log",
    method: "get",
    params,
  });
}

// 获取单条行为日志详情
export function getBehaviorLogDetail(id) {
  return request({
    url: `/v1/behavior-log/${id}`,
    method: "get",
  });
}
