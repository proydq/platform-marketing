<template>
  <el-dialog
    v-model="visible"
    :title="isEdit ? t('channels.editChannel') : t('channels.createChannel')"
    width="600px"
    :destroy-on-close="true"
    class="channel-editor-dialog"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="120px"
      class="channel-form"
    >
      <el-form-item :label="t('channels.channelName')" prop="name">
        <el-input
          v-model="form.name"
          :placeholder="t('channels.channelNamePlaceholder')"
          maxlength="50"
          show-word-limit
        />
      </el-form-item>

      <el-form-item :label="t('channels.category')" prop="category">
        <el-select
          v-model="form.category"
          :placeholder="t('channels.selectCategory')"
          style="width: 100%"
        >
          <el-option
            v-for="category in categories"
            :key="category.value"
            :label="t(category.labelKey)"
            :value="category.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item :label="t('channels.icon')" prop="icon">
        <div class="icon-selector">
          <el-input
            v-model="form.icon"
            :placeholder="t('channels.iconPlaceholder')"
            style="width: 200px"
          />
          <div class="icon-preview">
            <span v-if="form.icon" :style="{ fontSize: '24px', color: form.color }">
              {{ form.icon }}
            </span>
          </div>
        </div>
      </el-form-item>

      <el-form-item :label="t('channels.color')" prop="color">
        <el-color-picker
          v-model="form.color"
          :predefine="predefineColors"
          show-alpha
        />
      </el-form-item>

      <el-form-item :label="t('channels.description')">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="3"
          :placeholder="t('channels.descriptionPlaceholder')"
          maxlength="200"
          show-word-limit
        />
      </el-form-item>

      <el-form-item :label="t('channels.features')">
        <div class="feature-checkboxes">
          <el-checkbox v-model="form.config.supportsBatch">
            {{ t('channels.supportsBatch') }}
          </el-checkbox>
          <el-checkbox v-model="form.config.supportsTemplate">
            {{ t('channels.supportsTemplate') }}
          </el-checkbox>
          <el-checkbox v-model="form.config.supportsTracking">
            {{ t('channels.supportsTracking') }}
          </el-checkbox>
        </div>
      </el-form-item>

      <el-form-item v-if="!isEdit" :label="t('channels.enabled')">
        <el-switch v-model="form.enabled" />
      </el-form-item>

      <!-- API配置 -->
      <el-divider>{{ t('channels.apiConfig') }}</el-divider>
      
      <el-form-item :label="t('channels.apiEndpoint')">
        <el-input
          v-model="form.apiConfig.endpoint"
          :placeholder="t('channels.apiEndpointPlaceholder')"
        />
      </el-form-item>

      <el-form-item :label="t('channels.apiKey')">
        <el-input
          v-model="form.apiConfig.apiKey"
          type="password"
          :placeholder="t('channels.apiKeyPlaceholder')"
          show-password
        />
      </el-form-item>

      <el-form-item :label="t('channels.timeout')">
        <el-input-number
          v-model="form.apiConfig.timeout"
          :min="1000"
          :max="60000"
          :step="1000"
          controls-position="right"
          style="width: 200px"
        />
        <span style="margin-left: 8px; color: #909399;">ms</span>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">
          {{ t('common.cancel') }}
        </el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">
          {{ t('common.save') }}
        </el-button>
        <el-button 
          v-if="isEdit && !channel?.builtin"
          type="success" 
          @click="handleTest"
          :loading="testing"
        >
          {{ t('channels.test') }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import { useI18n } from 'vue-i18n';
import { useChannelsStore } from '@/stores/modules/channels';
import { CHANNEL_CATEGORIES } from '@/constants/channels';

const { t } = useI18n();
const channelsStore = useChannelsStore();

// Props
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  channel: {
    type: Object,
    default: null
  },
  isEdit: {
    type: Boolean,
    default: false
  }
});

// Emits
const emit = defineEmits(['update:modelValue', 'success']);

// 响应式数据
const formRef = ref();
const saving = ref(false);
const testing = ref(false);

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
});

const categories = CHANNEL_CATEGORIES;

// 预定义颜色
const predefineColors = [
  '#409EFF',
  '#67C23A',
  '#E6A23C',
  '#F56C6C',
  '#909399',
  '#07C160',
  '#25D366',
  '#1DA1F2',
  '#FF5722',
  '#9C27B0'
];

// 表单数据
const form = reactive({
  id: null,
  name: '',
  category: 'custom',
  icon: '🔗',
  color: '#409EFF',
  description: '',
  enabled: true,
  builtin: false,
  config: {
    supportsBatch: false,
    supportsTemplate: false,
    supportsTracking: false
  },
  apiConfig: {
    endpoint: '',
    apiKey: '',
    timeout: 30000
  }
});

// 表单验证规则
const rules = {
  name: [
    { required: true, message: t('channels.nameRequired'), trigger: 'blur' },
    { min: 2, max: 50, message: t('channels.nameLength'), trigger: 'blur' }
  ],
  category: [
    { required: true, message: t('channels.categoryRequired'), trigger: 'change' }
  ],
  icon: [
    { required: true, message: t('channels.iconRequired'), trigger: 'blur' }
  ],
  color: [
    { required: true, message: t('channels.colorRequired'), trigger: 'change' }
  ]
};

// 监听 props 变化
watch(
  () => [props.modelValue, props.channel],
  ([newVisible, newChannel]) => {
    if (newVisible && newChannel && props.isEdit) {
      nextTick(() => {
        resetForm(newChannel);
      });
    } else if (newVisible && !props.isEdit) {
      nextTick(() => {
        resetForm();
      });
    }
  },
  { immediate: true }
);

// 重置表单
const resetForm = (channelData = null) => {
  if (channelData) {
    Object.assign(form, {
      ...channelData,
      config: { ...channelData.config },
      apiConfig: { ...channelData.apiConfig }
    });
  } else {
    Object.assign(form, {
      id: null,
      name: '',
      category: 'custom',
      icon: '🔗',
      color: '#409EFF',
      description: '',
      enabled: true,
      builtin: false,
      config: {
        supportsBatch: false,
        supportsTemplate: false,
        supportsTracking: false
      },
      apiConfig: {
        endpoint: '',
        apiKey: '',
        timeout: 30000
      }
    });
  }
  
  // 清除验证状态
  if (formRef.value) {
    formRef.value.clearValidate();
  }
};

// 处理取消
const handleCancel = () => {
  visible.value = false;
};

// 处理保存
const handleSave = async () => {
  if (!formRef.value) return;
  
  try {
    const valid = await formRef.value.validate();
    if (!valid) return;
  } catch (error) {
    return;
  }
  
  saving.value = true;
  try {
    if (props.isEdit) {
      await channelsStore.updateChannel(form.id, form);
      ElMessage.success(t('channels.updateSuccess'));
    } else {
      await channelsStore.createChannel(form);
      ElMessage.success(t('channels.createSuccess'));
    }
    
    emit('success');
    visible.value = false;
  } catch (error) {
    ElMessage.error(
      props.isEdit 
        ? t('channels.updateError') 
        : t('channels.createError')
    );
  } finally {
    saving.value = false;
  }
};

// 处理测试连接
const handleTest = async () => {
  testing.value = true;
  try {
    const result = await channelsStore.testChannel(form.id, {
      apiConfig: form.apiConfig,
      config: form.config
    });
    
    if (result.success) {
      ElMessage.success(t('channels.testSuccess'));
    } else {
      ElMessage.warning(result.message || t('channels.testFailed'));
    }
  } catch (error) {
    ElMessage.error(t('channels.testError'));
  } finally {
    testing.value = false;
  }
};
</script>

<style scoped>
.channel-form {
  padding: 20px 0;
}

.icon-selector {
  display: flex;
  align-items: center;
  gap: 12px;
}

.icon-preview {
  width: 40px;
  height: 40px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}

.feature-checkboxes {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #303133;
}

:deep(.el-divider) {
  margin: 20px 0;
}

:deep(.el-divider__text) {
  font-weight: 600;
  color: #409EFF;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .channel-editor-dialog {
    width: 90% !important;
  }
  
  .icon-selector {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>