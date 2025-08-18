// src/api/systemSettings.js
import request from "@/utils/request";

export function getSystemSettings() {
  return request.get("/v1/system/settings");
}

export function updateBasicSettings(data) {
  return request.post("/v1/system/settings/basic", data);
}

export function updateNotifySettings(data) {
  return request.post("/v1/system/settings/notify", data);
}

export function updateSecuritySettings(data) {
  return request.post("/v1/system/settings/security", data);
}
