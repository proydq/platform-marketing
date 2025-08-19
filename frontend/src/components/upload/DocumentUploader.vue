<template>
  <div class="document-uploader">
    <el-upload
      v-model:file-list="internalFileList"
      :action="uploadUrl"
      :headers="uploadHeaders"
      :before-upload="beforeUpload"
      :on-success="handleSuccess"
      :on-error="handleError"
      :on-remove="handleRemove"
      :limit="maxCount"
      :accept="accept"
      :multiple="maxCount > 1"
      :auto-upload="autoUpload"
      class="document-upload"
    >
      <el-button type="primary">
        <el-icon><UploadFilled /></el-icon>
        {{ t('common.uploadDocument') }}
      </el-button>
      <template #tip>
        <div class="upload-tip">
          {{ t('upload.documentTip', { size: Math.round(maxSize / 1024 / 1024) }) }}
        </div>
      </template>
    </el-upload>
    
    <div v-if="internalFileList.length > 0" class="document-list">
      <div 
        v-for="file in internalFileList" 
        :key="file.uid"
        class="document-item"
        :class="{ uploading: file.status === 'uploading' }"
      >
        <div class="document-icon">
          <el-icon><Document /></el-icon>
        </div>
        <div class="document-info">
          <div class="document-name">{{ file.name }}</div>
          <div class="document-meta">
            <span v-if="file.size" class="file-size">{{ formatFileSize(file.size) }}</span>
            <span class="file-status" :class="file.status">{{ getStatusText(file.status) }}</span>
          </div>
          <el-progress 
            v-if="file.status === 'uploading'" 
            :percentage="file.percentage || 0"
            :stroke-width="4"
          />
        </div>
        <div class="document-actions">
          <el-button 
            v-if="file.url && file.status === 'success'"
            type="text" 
            size="small"
            @click="downloadFile(file)"
          >
            <el-icon><Download /></el-icon>
          </el-button>
          <el-button 
            type="text" 
            size="small"
            @click="handleRemove(file)"
            class="remove-btn"
          >
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { useI18n } from 'vue-i18n';
import { UploadFilled, Document, Download, Delete } from '@element-plus/icons-vue';

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  maxCount: {
    type: Number,
    default: 5
  },
  maxSize: {
    type: Number,
    default: 10 * 1024 * 1024 // 10MB
  },
  accept: {
    type: String,
    default: '.pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.txt,.rtf'
  },
  autoUpload: {
    type: Boolean,
    default: true
  }
});

const emit = defineEmits(['update:modelValue', 'change']);
const { t } = useI18n();

const internalFileList = ref([]);

const uploadUrl = ref('/api/v1/products/upload');
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token');
  return token ? {
    'Authorization': `Bearer ${token}`
  } : {};
});

watch(() => props.modelValue, (newVal) => {
  if (newVal && Array.isArray(newVal)) {
    internalFileList.value = newVal.map((item, index) => ({
      uid: index,
      name: item.name || `document-${index}`,
      status: 'success',
      url: item.url || item,
      size: item.size
    }));
  }
}, { immediate: true });

function beforeUpload(file) {
  // 检查文件大小
  if (file.size > props.maxSize) {
    ElMessage.error(t('upload.fileTooLarge', { size: Math.round(props.maxSize / 1024 / 1024) }));
    return false;
  }
  
  return true;
}

function handleSuccess(response, file) {
  if (response.code === 0 && response.data?.url) {
    file.url = response.data.url;
    file.status = 'success';
    updateModelValue();
    ElMessage.success(t('upload.uploadSuccess'));
  } else {
    file.status = 'fail';
    ElMessage.error(response.message || t('upload.uploadFailed'));
  }
}

function handleError(error, file) {
  file.status = 'fail';
  ElMessage.error(t('upload.uploadFailed'));
}

function handleRemove(file) {
  const index = internalFileList.value.findIndex(item => item.uid === file.uid);
  if (index > -1) {
    internalFileList.value.splice(index, 1);
    updateModelValue();
  }
}

function updateModelValue() {
  const documents = internalFileList.value
    .filter(file => file.url && file.status === 'success')
    .map(file => ({
      name: file.name,
      url: file.url,
      size: file.size
    }));
  emit('update:modelValue', documents);
  emit('change', documents);
}

function formatFileSize(bytes) {
  if (!bytes) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

function getStatusText(status) {
  const statusMap = {
    uploading: t('upload.uploading'),
    success: t('upload.uploadSuccess'),
    fail: t('upload.uploadFailed'),
    ready: t('upload.ready')
  };
  return statusMap[status] || status;
}

function downloadFile(file) {
  if (file.url) {
    const link = document.createElement('a');
    link.href = file.url;
    link.download = file.name;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }
}
</script>

<style scoped>
.document-uploader {
  width: 100%;
}

.upload-tip {
  color: #909399;
  font-size: 12px;
  margin-top: 8px;
}

.document-list {
  margin-top: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
}

.document-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f1f5f9;
  transition: background-color 0.3s ease;
}

.document-item:last-child {
  border-bottom: none;
}

.document-item:hover {
  background-color: #f8fafc;
}

.document-item.uploading {
  background-color: #f0f9ff;
}

.document-icon {
  margin-right: 12px;
  color: #6b7280;
  font-size: 24px;
}

.document-info {
  flex: 1;
  min-width: 0;
}

.document-name {
  font-size: 14px;
  font-weight: 500;
  color: #1f2937;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.document-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 4px;
}

.file-status {
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 11px;
}

.file-status.success {
  background-color: #d1fae5;
  color: #059669;
}

.file-status.uploading {
  background-color: #dbeafe;
  color: #2563eb;
}

.file-status.fail {
  background-color: #fee2e2;
  color: #dc2626;
}

.document-actions {
  display: flex;
  gap: 4px;
}

.remove-btn {
  color: #f56c6c !important;
}

.remove-btn:hover {
  background-color: #fef2f2 !important;
}

/* Element Plus 样式覆盖 */
:deep(.el-upload) {
  width: 100%;
}

:deep(.el-upload-dragger) {
  width: 100%;
  height: auto;
  padding: 20px;
}

:deep(.el-progress) {
  margin-top: 4px;
}

:deep(.el-progress-bar__outer) {
  background-color: #f1f5f9;
}
</style>