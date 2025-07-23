<template>
  <div class="page-wrapper" style="background:#f6f9fc;">
    <el-card class="card-container">
      <h2 style="margin-bottom:20px;"><el-icon><Setting /></el-icon> {{ $t('settings.title') }}</h2>
      <el-tabs v-model="activeTab">
        <el-tab-pane :label="$t('settings.basic')" name="basic">
          <el-card shadow="never" style="margin-bottom:20px;">
            <el-form :model="basicForm" label-width="90px" class="form-section">
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item :label="$t('settings.siteName')">
                    <el-input v-model="basicForm.siteName" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item :label="$t('settings.brandColor')">
                    <el-color-picker v-model="basicForm.brandColor" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item :label="$t('settings.logo')">
                    <el-upload action="#" :show-file-list="false" @change="onLogoChange">
                      <el-button type="primary">{{ $t('settings.chooseFile') }}</el-button>
                    </el-upload>
                    <img v-if="basicForm.logoUrl" :src="basicForm.logoUrl" style="height:40px;margin-left:10px" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item :label="$t('settings.language')">
                    <el-select v-model="basicForm.language" style="width:100%">
                      <el-option label="中文" value="zh" />
                      <el-option label="English" value="en" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <div class="action-buttons" style="justify-content:flex-end;">
                <el-button type="primary" @click="saveBasic">{{ $t('settings.save') }}</el-button>
              </div>
            </el-form>
          </el-card>
        </el-tab-pane>
        <el-tab-pane :label="$t('settings.notify')" name="notify">
          <el-card shadow="never" style="margin-bottom:20px;">
            <el-form :model="notifyForm" label-width="120px" class="form-section">
            <el-form-item :label="$t('settings.notifyEnable')">
                <el-switch v-model="notifyForm.enabled" />
              </el-form-item>
            <el-form-item :label="$t('settings.notifyType')">
                <el-select v-model="notifyForm.type" style="width:100%">
                  <el-option label="Email" value="Email" />
                  <el-option label="SMS" value="SMS" />
                  <el-option label="Message" value="MSG" />
                </el-select>
              </el-form-item>
            <el-form-item :label="$t('settings.notifyChannel')">
                <el-input v-model="notifyForm.channel" />
              </el-form-item>
              <div class="action-buttons" style="justify-content:flex-end;">
                <el-button type="primary" @click="saveNotify">{{ $t('settings.save') }}</el-button>
              </div>
            </el-form>
          </el-card>
        </el-tab-pane>
        <el-tab-pane :label="$t('settings.security')" name="security">
          <el-card shadow="never">
            <el-form :model="securityForm" label-width="120px" class="form-section">
              <el-form-item :label="$t('settings.passwordStrength')">
                <el-select v-model="securityForm.passwordStrength" style="width:100%">
                  <el-option label="Low" value="弱" />
                  <el-option label="Medium" value="中" />
                  <el-option label="High" value="强" />
                </el-select>
              </el-form-item>
              <el-form-item :label="$t('settings.deviceLimit')">
                <el-input-number v-model="securityForm.deviceLimit" :min="1" />
              </el-form-item>
              <el-form-item :label="$t('settings.twoFactor')">
                <el-switch v-model="securityForm.twoFactor" />
              </el-form-item>
              <div class="action-buttons" style="justify-content:flex-end;">
                <el-button type="primary" @click="saveSecurity">{{ $t('settings.save') }}</el-button>
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
import { useI18n } from 'vue-i18n'
import { Setting } from '@element-plus/icons-vue'
import data from '../mock/settings.json'

const activeTab = ref('basic')
const basicForm = ref({ ...data.basic })
const notifyForm = ref({ ...data.notification })
const securityForm = ref({ ...data.security })
const { t } = useI18n()

function onLogoChange(upload) {
  const file = upload.raw
  if (file) {
    basicForm.value.logoUrl = URL.createObjectURL(file)
  }
}

function saveBasic() {
  ElMessage.success(t('settings.saveSuccess'))
}
function saveNotify() {
  ElMessage.success(t('settings.saveSuccess'))
}
function saveSecurity() {
  ElMessage.success(t('settings.saveSuccess'))
}
</script>
