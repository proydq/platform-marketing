import request from "@/utils/request";

export function uploadImage(data) {
  return request({
    url: "/file-info/uploadFile",
    method: "post",
    data,
    headers: { "Content-Type": "multipart/form-data" },
  });
}

export function sendEmail(data) {
  return request({
    url: "/v1/email/send",
    method: "post",
    data,
  });
}

export function sendTestEmail(data) {
  return request({
    url: "/v1/email/test",
    method: "post",
    data,
  });
}

export function getEmailRecords() {
  return request({
    url: "/v1/email-campaign/records",
    method: "get",
  });
}

// 获取邮件发送记录
export function getEmailSendRecords(page = 1, size = 10) {
  return request({
    url: "/v1/email/records",
    method: "get",
    params: { page, size }
  });
}
export function uploadRecipientsCSV(file) {
  const formData = new FormData();
  formData.append("file", file);
  return request({
    url: "/v1/email/upload-recipients",
    method: "post",
    data: formData,
    headers: { "Content-Type": "multipart/form-data" },
  });
}
