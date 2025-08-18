<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-title">
        <span class="page-icon">⚙️</span>
        系统设置
      </div>
      <div class="page-subtitle">
        配置系统基本参数、通知设置和安全选项
      </div>
      <div class="page-actions">
        <el-button class="btn-secondary" @click="refreshSettings">
          <el-icon><Refresh /></el-icon>
          刷新设置
        </el-button>
      </div>
    </div>

    <!-- 设置选项 -->
    <div class="data-table-wrapper">
      <div class="table-header">
        <div class="table-title">⚙️ 系统配置面板</div>
        <div class="table-subtitle">管理和配置系统的各项参数</div>
      </div>
      <el-tabs v-model="activeTab" class="enhanced-tabs">
        <el-tab-pane name="basic">
          <template #label>
            <span class="tab-label">
              <el-icon><Tools /></el-icon>
              基础设置
            </span>
          </template>
          <div class="form-section">
            <div class="form-card">
              <div class="form-header">
                <div class="form-title">
                  <span class="form-icon">🏢</span>
                  站点基础信息
                </div>
                <div class="form-description">配置站点名称、品牌颜色和语言设置</div>
              </div>
            <el-form :model="basicForm" label-width="100px" class="modern-form">
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
                    <el-upload
                      action="#"
                      :show-file-list="false"
                      @change="onLogoChange"
                    >
                      <el-button type="primary">{{
                        $t("settings.chooseFile")
                      }}</el-button>
                    </el-upload>
                    <img
                      v-if="basicForm.logoUrl"
                      :src="basicForm.logoUrl"
                      class="logo-preview"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item :label="$t('settings.language')">
                    <el-select v-model="basicForm.language" style="width: 100%">
                      <el-option label="中文" value="zh" />
                      <el-option label="English" value="en" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <div class="form-actions">
                <el-button type="primary" class="btn-primary" @click="saveBasic">
                  <el-icon><Check /></el-icon>
                  保存设置
                </el-button>
                <el-button class="btn-secondary" @click="resetBasic">
                  <el-icon><RefreshLeft /></el-icon>
                  重置
                </el-button>
              </div>
            </el-form>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane name="notify">
          <template #label>
            <span class="tab-label">
              <el-icon><Bell /></el-icon>
              通知设置
            </span>
          </template>
          <div class="form-section">
            <div class="form-card">
              <div class="form-header">
                <div class="form-title">
                  <span class="form-icon">🔔</span>
                  通知配置
                </div>
                <div class="form-description">配置系统通知方式和接收渠道</div>
              </div>
            <el-form
              :model="notifyForm"
              label-width="120px"
              class="modern-form"
            >
              <el-form-item :label="$t('settings.notifyEnable')">
                <el-switch v-model="notifyForm.enabled" />
              </el-form-item>
              <el-form-item :label="$t('settings.notifyType')">
                <el-select v-model="notifyForm.type" style="width: 100%">
                  <el-option label="Email" value="Email" />
                  <el-option label="SMS" value="SMS" />
                  <el-option label="Message" value="MSG" />
                </el-select>
              </el-form-item>
              <el-form-item :label="$t('settings.notifyChannel')">
                <el-input v-model="notifyForm.channel" />
              </el-form-item>
              <div class="form-actions">
                <el-button type="primary" class="btn-primary" @click="saveNotify">
                  <el-icon><Check /></el-icon>
                  保存设置
                </el-button>
                <el-button class="btn-secondary" @click="resetNotify">
                  <el-icon><RefreshLeft /></el-icon>
                  重置
                </el-button>
              </div>
            </el-form>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane name="security">
          <template #label>
            <span class="tab-label">
              <el-icon><Lock /></el-icon>
              安全设置
            </span>
          </template>
          <div class="form-section">
            <div class="form-card">
              <div class="form-header">
                <div class="form-title">
                  <span class="form-icon">🔒</span>
                  安全配置
                </div>
                <div class="form-description">配置密码强度、设备限制和两步验证</div>
              </div>
            <el-form
              :model="securityForm"
              label-width="120px"
              class="modern-form"
            >
              <el-form-item :label="$t('settings.passwordStrength')">
                <el-select
                  v-model="securityForm.passwordStrength"
                  style="width: 100%"
                >
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
              <div class="form-actions">
                <el-button type="primary" class="btn-primary" @click="saveSecurity">
                  <el-icon><Check /></el-icon>
                  保存设置
                </el-button>
                <el-button class="btn-secondary" @click="resetSecurity">
                  <el-icon><RefreshLeft /></el-icon>
                  重置
                </el-button>
              </div>
            </el-form>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { useI18n } from "vue-i18n";
import { 
  Setting, 
  Tools, 
  Bell, 
  Lock, 
  Check, 
  RefreshLeft,
  Refresh 
} from "@element-plus/icons-vue";
import {
  getSystemSettings,
  updateBasicSettings,
  updateNotifySettings,
  updateSecuritySettings,
} from "@/api/systemSettings";

const { t } = useI18n();
const activeTab = ref("basic");

// 原始设置备份，用于重置功能
const originalSettings = ref({});

const basicForm = ref({
  siteName: "",
  brandColor: "",
  logoUrl: "",
  language: "zh",
});
const notifyForm = ref({
  enabled: false,
  type: "Email",
  channel: "",
});
const securityForm = ref({
  passwordStrength: "中",
  deviceLimit: 3,
  twoFactor: false,
});

function loadSettings() {
  getSystemSettings().then((res) => {
    const data = res.data || {};
    // 保存原始设置作为备份
    originalSettings.value = {
      basic: { ...data.basic },
      notification: { ...data.notification },
      security: { ...data.security }
    };
    
    Object.assign(basicForm.value, data.basic || {});
    Object.assign(notifyForm.value, data.notification || {});
    Object.assign(securityForm.value, data.security || {});
  });
}

// 刷新设置功能
function refreshSettings() {
  ElMessage.info("正在刷新设置...");
  loadSettings();
  ElMessage.success("设置已刷新");
}

// 重置功能
function resetBasic() {
  Object.assign(basicForm.value, originalSettings.value.basic || {});
  ElMessage.info("已重置基础设置");
}

function resetNotify() {
  Object.assign(notifyForm.value, originalSettings.value.notification || {});
  ElMessage.info("已重置通知设置");
}

function resetSecurity() {
  Object.assign(securityForm.value, originalSettings.value.security || {});
  ElMessage.info("已重置安全设置");
}

onMounted(loadSettings);

function onLogoChange(upload) {
  const file = upload.raw;
  if (file) {
    basicForm.value.logoUrl = URL.createObjectURL(file);
    // 后端保存逻辑建议用 file 上传组件配合接口
  }
}

function saveBasic() {
  updateBasicSettings(basicForm.value).then(() => {
    ElMessage.success("基础设置保存成功");
    // 更新原始设置备份
    originalSettings.value.basic = { ...basicForm.value };
  }).catch(() => {
    ElMessage.error("基础设置保存失败");
  });
}

function saveNotify() {
  updateNotifySettings(notifyForm.value).then(() => {
    ElMessage.success("通知设置保存成功");
    // 更新原始设置备份
    originalSettings.value.notification = { ...notifyForm.value };
  }).catch(() => {
    ElMessage.error("通知设置保存失败");
  });
}

function saveSecurity() {
  updateSecuritySettings(securityForm.value).then(() => {
    ElMessage.success("安全设置保存成功");
    // 更新原始设置备份
    originalSettings.value.security = { ...securityForm.value };
  }).catch(() => {
    ElMessage.error("安全设置保存失败");
  });
}
</script>

<style scoped>
/* 使用全局设计系统，仅保留必要的组件特定样式 */

/* 标签页样式 */
.tab-label {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
}

.tab-label .el-icon {
  font-size: 14px;
}

/* 表单卡片样式 */
.form-section {
  padding: var(--spacing-6);
}

.form-card {
  background: var(--bg-primary);
  border: 1px solid var(--border-secondary);
  border-radius: var(--radius-lg);
  padding: var(--spacing-6);
  margin-bottom: var(--spacing-4);
}

.form-header {
  margin-bottom: var(--spacing-6);
  padding-bottom: var(--spacing-4);
  border-bottom: 1px solid var(--border-tertiary);
}

.form-title {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
  font-size: var(--font-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-2);
}

.form-icon {
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.form-description {
  font-size: var(--font-sm);
  color: var(--text-secondary);
  line-height: 1.5;
}

/* 现代表单样式 */
.modern-form {
  margin-top: var(--spacing-4);
}

.modern-form .el-form-item {
  margin-bottom: var(--spacing-5);
}

.modern-form .el-form-item__label {
  font-weight: var(--font-medium);
  color: var(--text-primary);
}

/* 表单操作按钮 */
.form-actions {
  display: flex;
  gap: var(--spacing-3);
  justify-content: flex-end;
  margin-top: var(--spacing-8);
  padding-top: var(--spacing-6);
  border-top: 1px solid var(--border-tertiary);
}

/* Logo 图片样式 */
.logo-preview {
  height: 40px;
  margin-left: var(--spacing-3);
  border-radius: var(--radius-sm);
  border: 1px solid var(--border-secondary);
}

/* Element Plus 样式覆盖 */
:deep(.el-tabs__item) {
  padding: 0 var(--spacing-5);
  font-weight: var(--font-medium);
}

:deep(.el-tabs__item.is-active) {
  color: var(--primary-color);
}

:deep(.el-form-item__label) {
  color: var(--text-primary) !important;
  font-weight: var(--font-medium) !important;
}

:deep(.el-input__wrapper) {
  border-radius: var(--radius-md);
  transition: all var(--transition-base);
}

:deep(.el-input__wrapper:hover) {
  border-color: var(--primary-color);
}

:deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px var(--primary-bg-subtle);
}

:deep(.el-select .el-input__wrapper) {
  transition: all var(--transition-base);
}

:deep(.el-select .el-input__wrapper:hover) {
  border-color: var(--primary-color);
}

:deep(.el-switch) {
  --el-switch-on-color: var(--success-color);
}

:deep(.el-color-picker__trigger) {
  border-radius: var(--radius-md);
  border: 1px solid var(--border-secondary);
  transition: all var(--transition-base);
}

:deep(.el-color-picker__trigger:hover) {
  border-color: var(--primary-color);
}

:deep(.el-upload) {
  width: 100%;
}

:deep(.el-upload .el-button) {
  border-radius: var(--radius-md);
}

/* 数字输入框样式 */
:deep(.el-input-number) {
  width: 100%;
}

:deep(.el-input-number .el-input__wrapper) {
  border-radius: var(--radius-md);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .form-section {
    padding: var(--spacing-4);
  }
  
  .form-card {
    padding: var(--spacing-4);
  }
  
  .form-actions {
    flex-direction: column;
    align-items: stretch;
  }
  
  .form-actions .el-button {
    width: 100%;
  }
  
  :deep(.el-row) {
    --el-row-gutter: 12px;
  }
  
  :deep(.el-col) {
    margin-bottom: var(--spacing-3);
  }
}

/* 特殊状态样式 */
.form-card:hover {
  border-color: var(--border-primary);
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-base);
}
</style>
