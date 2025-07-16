<template>
  <div class="page-wrapper" style="background:#f6f9fc;">
    <el-card class="card-container">
      <h2 style="margin-bottom:20px;"><el-icon><Setting /></el-icon> 系统设置</h2>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基础设置" name="basic">
          <el-card shadow="never" style="margin-bottom:20px;">
            <el-form :model="basicForm" label-width="90px" class="form-section">
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="站点名称">
                    <el-input v-model="basicForm.siteName" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="品牌颜色">
                    <el-color-picker v-model="basicForm.brandColor" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="LOGO 上传">
                    <el-upload action="#" :show-file-list="false" @change="onLogoChange">
                      <el-button type="primary">选择文件</el-button>
                    </el-upload>
                    <img v-if="basicForm.logoUrl" :src="basicForm.logoUrl" style="height:40px;margin-left:10px" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="语言选择">
                    <el-select v-model="basicForm.language" style="width:100%">
                      <el-option label="中文" value="zh-CN" />
                      <el-option label="English" value="en" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <div class="action-buttons" style="justify-content:flex-end;">
                <el-button type="primary" @click="saveBasic">保存</el-button>
              </div>
            </el-form>
          </el-card>
        </el-tab-pane>
        <el-tab-pane label="通知设置" name="notify">
          <el-card shadow="never" style="margin-bottom:20px;">
            <el-form :model="notifyForm" label-width="120px" class="form-section">
              <el-form-item label="是否开启通知">
                <el-switch v-model="notifyForm.enabled" />
              </el-form-item>
              <el-form-item label="默认通知类型">
                <el-select v-model="notifyForm.type" style="width:100%">
                  <el-option label="Email" value="Email" />
                  <el-option label="SMS" value="SMS" />
                  <el-option label="站内信" value="站内信" />
                </el-select>
              </el-form-item>
              <el-form-item label="发送通道">
                <el-input v-model="notifyForm.channel" />
              </el-form-item>
              <div class="action-buttons" style="justify-content:flex-end;">
                <el-button type="primary" @click="saveNotify">保存</el-button>
              </div>
            </el-form>
          </el-card>
        </el-tab-pane>
        <el-tab-pane label="安全设置" name="security">
          <el-card shadow="never">
            <el-form :model="securityForm" label-width="120px" class="form-section">
              <el-form-item label="密码强度要求">
                <el-select v-model="securityForm.passwordStrength" style="width:100%">
                  <el-option label="弱" value="弱" />
                  <el-option label="中" value="中" />
                  <el-option label="强" value="强" />
                </el-select>
              </el-form-item>
              <el-form-item label="允许登录设备数">
                <el-input-number v-model="securityForm.deviceLimit" :min="1" />
              </el-form-item>
              <el-form-item label="两步验证开关">
                <el-switch v-model="securityForm.twoFactor" />
              </el-form-item>
              <div class="action-buttons" style="justify-content:flex-end;">
                <el-button type="primary" @click="saveSecurity">保存</el-button>
              </div>
            </el-form>
          </el-card>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Setting } from '@element-plus/icons-vue'
import data from '../mock/settings.json'

const activeTab = ref('basic')
const basicForm = ref({ ...data.basic })
const notifyForm = ref({ ...data.notification })
const securityForm = ref({ ...data.security })

function onLogoChange(upload) {
  const file = upload.raw
  if (file) {
    basicForm.value.logoUrl = URL.createObjectURL(file)
  }
}

function saveBasic() {
  ElMessage.success('基础设置已更新')
}
function saveNotify() {
  ElMessage.success('通知设置已更新')
}
function saveSecurity() {
  ElMessage.success('安全设置已更新')
}
</script>
