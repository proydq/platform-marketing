<template>
  <div class="video-uploader">
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
      list-type="picture-card"
      class="video-upload"
    >
      <el-icon class="upload-icon">
        <VideoPlay />
      </el-icon>
      <div class="upload-text">{{ t('common.uploadVideo') }}</div>
    </el-upload>
    
    <!-- 视频预览对话框 -->
    <el-dialog v-model="previewVisible" :title="t('common.preview')" width="800px">
      <video 
        v-if="previewVideo" 
        :src="previewVideo" 
        controls 
        style="width: 100%; max-height: 400px;"
      ></video>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { useI18n } from 'vue-i18n';
import { VideoPlay } from '@element-plus/icons-vue';

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  maxCount: {
    type: Number,
    default: 3
  },
  maxSize: {
    type: Number,
    default: 50 * 1024 * 1024 // 50MB
  },
  accept: {
    type: String,
    default: '.mp4,.avi,.mov,.wmv,.flv,.webm'
  },
  autoUpload: {
    type: Boolean,
    default: true
  }
});

const emit = defineEmits(['update:modelValue', 'change']);
const { t } = useI18n();

const internalFileList = ref([]);
const previewVisible = ref(false);
const previewVideo = ref('');

const uploadUrl = ref('/api/product/upload');
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
      name: item.name || `video-${index}`,
      status: 'success',
      url: item.url || item
    }));
  }
}, { immediate: true });

function beforeUpload(file) {
  const isValidType = ['video/mp4', 'video/avi', 'video/quicktime', 'video/x-msvideo', 'video/x-flv', 'video/webm'].includes(file.type);
  if (!isValidType) {
    ElMessage.error(t('upload.invalidVideoType'));
    return false;
  }
  
  if (file.size > props.maxSize) {
    ElMessage.error(t('upload.fileTooLarge', { size: Math.round(props.maxSize / 1024 / 1024) }));
    return false;
  }
  
  return true;
}

function handleSuccess(response, file) {
  if (response.success) {
    file.url = response.data.url;
    updateModelValue();
    ElMessage.success(t('upload.uploadSuccess'));
  } else {
    ElMessage.error(response.message || t('upload.uploadFailed'));
    handleRemove(file);
  }
}

function handleError(error, file) {
  ElMessage.error(t('upload.uploadFailed'));
  handleRemove(file);
}

function handleRemove(file) {
  const index = internalFileList.value.findIndex(item => item.uid === file.uid);
  if (index > -1) {
    internalFileList.value.splice(index, 1);
    updateModelValue();
  }
}

function updateModelValue() {
  const videos = internalFileList.value
    .filter(file => file.url && file.status !== 'uploading')
    .map(file => ({
      name: file.name,
      url: file.url,
      size: file.size
    }));
  emit('update:modelValue', videos);
  emit('change', videos);
}

function handlePreview(file) {
  previewVideo.value = file.url;
  previewVisible.value = true;
}
</script>

<style scoped>
.video-uploader {
  width: 100%;
}

.upload-icon {
  font-size: 28px;
  color: #8c939d;
  margin-bottom: 8px;
}

.upload-text {
  color: #8c939d;
  font-size: 14px;
}

:deep(.el-upload--picture-card) {
  width: 120px;
  height: 120px;
  background-color: #fafafa;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}

:deep(.el-upload-list__item) {
  width: 120px;
  height: 120px;
  margin: 0 8px 8px 0;
  border-radius: 6px;
  overflow: hidden;
  position: relative;
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}

:deep(.el-upload-list__item::before) {
  content: '🎬';
  font-size: 24px;
  margin-bottom: 8px;
}

:deep(.el-upload-list__item-name) {
  position: absolute;
  bottom: 8px;
  left: 8px;
  right: 8px;
  font-size: 12px;
  color: #606266;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>