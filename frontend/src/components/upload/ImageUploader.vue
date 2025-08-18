<template>
  <div class="image-uploader">
    <div class="upload-area">
      <el-upload
        v-model:file-list="internalFileList"
        :action="uploadUrl"
        :headers="uploadHeaders"
        :before-upload="beforeUpload"
        :on-success="handleSuccess"
        :on-error="handleError"
        :on-remove="handleRemove"
        :on-change="handleChange"
        :limit="maxCount"
        :accept="accept"
        :multiple="maxCount > 1"
        :auto-upload="autoUpload"
        list-type="picture-card"
      >
        <el-icon class="upload-icon">
          <Plus />
        </el-icon>
        <div class="upload-text">{{ t('common.uploadImage') }}</div>
      </el-upload>
    </div>
    
    <div v-if="draggable && internalFileList.length > 1" class="sort-tips">
      {{ t('common.dragToSort') }}
    </div>
    
    <!-- 图片预览对话框 -->
    <el-dialog v-model="previewVisible" :title="t('common.preview')" width="600px">
      <img :src="previewImage" style="width: 100%; height: auto;" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { useI18n } from 'vue-i18n';
import { Plus } from '@element-plus/icons-vue';

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
    default: 5 * 1024 * 1024 // 5MB
  },
  accept: {
    type: String,
    default: '.jpg,.jpeg,.png,.gif,.webp'
  },
  autoUpload: {
    type: Boolean,
    default: true
  },
  draggable: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:modelValue', 'change']);
const { t } = useI18n();

const internalFileList = ref([]);
const previewVisible = ref(false);
const previewImage = ref('');

// 上传相关配置
const uploadUrl = ref('/api/product/upload');
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token');
  return token ? {
    'Authorization': `Bearer ${token}`
  } : {};
});

// 监听外部数据变化
watch(() => props.modelValue, (newVal) => {
  if (newVal && Array.isArray(newVal)) {
    internalFileList.value = newVal.map((url, index) => ({
      uid: index,
      name: `image-${index}`,
      status: 'success',
      url: url
    }));
  }
}, { immediate: true });

// 上传前验证
function beforeUpload(file) {
  // 检查文件类型
  const isValidType = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'].includes(file.type);
  if (!isValidType) {
    ElMessage.error(t('upload.invalidImageType'));
    return false;
  }
  
  // 检查文件大小
  if (file.size > props.maxSize) {
    ElMessage.error(t('upload.fileTooLarge', { size: Math.round(props.maxSize / 1024 / 1024) }));
    return false;
  }
  
  return true;
}

// 上传成功
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

// 上传失败
function handleError(error, file) {
  ElMessage.error(t('upload.uploadFailed'));
  handleRemove(file);
}

// 文件变化
function handleChange(file, fileList) {
  internalFileList.value = fileList;
  if (!props.autoUpload) {
    // 手动上传模式，直接使用本地预览
    if (file.raw) {
      const reader = new FileReader();
      reader.onload = (e) => {
        file.url = e.target.result;
        updateModelValue();
      };
      reader.readAsDataURL(file.raw);
    }
  }
}

// 移除文件
function handleRemove(file) {
  const index = internalFileList.value.findIndex(item => item.uid === file.uid);
  if (index > -1) {
    internalFileList.value.splice(index, 1);
    updateModelValue();
  }
}

// 更新外部数据
function updateModelValue() {
  const urls = internalFileList.value
    .filter(file => file.url && file.status !== 'uploading')
    .map(file => file.url);
  emit('update:modelValue', urls);
  emit('change', urls);
}

// 预览图片
function handlePreview(file) {
  previewImage.value = file.url;
  previewVisible.value = true;
}

// 手动上传
function upload() {
  const uploadingFiles = internalFileList.value.filter(file => 
    file.status === 'ready' && file.raw
  );
  
  uploadingFiles.forEach(file => {
    const formData = new FormData();
    formData.append('file', file.raw);
    
    // 这里可以添加实际的上传逻辑
    // 示例：使用fetch或axios上传
  });
}

// 清空所有文件
function clear() {
  internalFileList.value = [];
  updateModelValue();
}

// 暴露方法给父组件
defineExpose({
  upload,
  clear
});
</script>

<style scoped>
.image-uploader {
  width: 100%;
}

.upload-area {
  margin-bottom: 16px;
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

.sort-tips {
  color: #909399;
  font-size: 12px;
  text-align: center;
  margin-top: 8px;
}

/* Element Plus 样式覆盖 */
:deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
  background-color: #fafafa;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  box-sizing: border-box;
  cursor: pointer;
  vertical-align: top;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  transition: border-color 0.3s ease;
}

:deep(.el-upload--picture-card:hover) {
  border-color: #409EFF;
  color: #409EFF;
}

:deep(.el-upload-list--picture-card) {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

:deep(.el-upload-list__item) {
  width: 100px;
  height: 100px;
  margin: 0;
  border-radius: 6px;
  overflow: hidden;
}

:deep(.el-upload-list__item-thumbnail) {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

:deep(.el-upload-list__item-actions) {
  position: absolute;
  width: 100%;
  height: 100%;
  left: 0;
  top: 0;
  cursor: default;
  text-align: center;
  color: #fff;
  opacity: 0;
  font-size: 20px;
  background-color: rgba(0, 0, 0, 0.5);
  transition: opacity 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

:deep(.el-upload-list__item:hover .el-upload-list__item-actions) {
  opacity: 1;
}

:deep(.el-upload-list__item-actions .el-icon) {
  cursor: pointer;
  transition: transform 0.3s;
}

:deep(.el-upload-list__item-actions .el-icon:hover) {
  transform: scale(1.2);
}
</style>