import request from "@/utils/request";

// === 模板相关 ===
export function getTemplates(params) {
  return request({
    url: "/v1/email-campaign/templates",
    method: "get",
    params,
  });
}

export function createTemplate(data) {
  return request({
    url: "/v1/email-campaign/templates",
    method: "post",
    data,
  });
}

export function updateTemplate(id, data) {
  return request({
    url: `/v1/email-campaign/templates/${id}`,
    method: "put",
    data,
  });
}

export function deleteTemplate(id) {
  return request({
    url: `/v1/email-campaign/templates/${id}`,
    method: "delete",
  });
}

// === 发送记录 ===
export function getSendRecords(params) {
  return request({
    url: "/v1/email-campaign/records",
    method: "get",
    params,
  });
}

export function createSendRecord(data) {
  return request({
    url: "/v1/email-campaign/records",
    method: "post",
    data,
  });
}

// === 测试发送 ===
export function sendTestEmail(email) {
  return request({
    url: "/v1/email-campaign/test-send",
    method: "post",
    data: { email },
  });
}
