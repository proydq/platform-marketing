import request from "@/utils/request";

export function getNotificationList(params) {
  return request({
    url: "/v1/notifications",
    method: "get",
    params,
  });
}

export function getNotificationDetail(id) {
  return request({
    url: `/v1/notifications/${id}`,
    method: "get",
  });
}

export function markNotificationRead(id) {
  return request({
    url: `/v1/notifications/${id}/read`,
    method: "patch",
  });
}

export function deleteNotification(id) {
  return request({
    url: `/v1/notifications/${id}`,
    method: "delete",
  });
}

export function batchMarkRead(ids) {
  return request({
    url: "/v1/notifications/read",
    method: "patch",
    data: { ids },
  });
}

export function batchDelete(ids) {
  return request({
    url: "/v1/notifications",
    method: "delete",
    data: { ids },
  });
}
