// src/api/systemSettings.js
import request from "@/utils/request";

export function getSystemSettings() {
  return request.get("/api/system/settings");
}

export function updateBasicSettings(data) {
  return request.post("/api/system/settings/basic", data);
}

export function updateNotifySettings(data) {
  return request.post("/api/system/settings/notify", data);
}

export function updateSecuritySettings(data) {
  return request.post("/api/system/settings/security", data);
}
