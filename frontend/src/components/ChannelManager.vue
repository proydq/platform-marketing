<template>
  <el-dialog
    v-model="visible"
    :title="t('channels.management')"
    width="80%"
    :destroy-on-close="true"
    class="channel-manager-dialog"
  >
    <div class="channel-manager">
      <!-- 头部统计 -->
      <div class="stats-section">
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon" style="background: #409EFF20; color: #409EFF">
              <el-icon><Management /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ channelStats.total }}</div>
              <div class="stat-label">{{ t('channels.totalChannels') }}</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: #67C23A20; color: #67C23A">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ channelStats.enabled }}</div>
              <div class="stat-label">{{ t('channels.enabledChannels') }}</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: #E6A23C20; color: #E6A23C">
              <el-icon><Setting /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ channelStats.custom }}</div>
              <div class="stat-label">{{ t('channels.customChannels') }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="action-left">
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            {{ t('channels.createChannel') }}
          </el-button>
          <el-button @click="refreshChannels" :loading="loading">
            <el-icon><Refresh /></el-icon>
            {{ t('common.refresh') }}
          </el-button>
        </div>
        <div class="action-right">
          <el-select
            v-model="categoryFilter"
            :placeholder="t('channels.filterByCategory')"
            clearable
            style="width: 200px"
          >
            <el-option :label="t('common.all')" value="" />
            <el-option
              v-for="category in categories"
              :key="category.value"
              :label="t(category.labelKey)"
              :value="category.value"
            />
          </el-select>
        </div>
      </div>

      <!-- 渠道列表 -->
      <div class="channels-grid" v-loading="loading">
        <div
          v-for="channel in filteredChannels"
          :key="channel.id"
          class="channel-card"
          :class="{ 'disabled': !channel.enabled }"
        >
          <div class="channel-header">
            <div class="channel-info">
              <span class="channel-icon" :style="{ color: channel.color }">
                {{ channel.icon }}
              </span>
              <div class="channel-details">
                <div class="channel-name">{{ channel.name }}</div>
                <div class="channel-category">{{ getCategoryLabel(channel.category) }}</div>
              </div>
            </div>
            <div class="channel-status">
              <el-switch
                v-model="channel.enabled"
                @change="handleToggle(channel)"
                :disabled="channel.builtin && !channel.enabled"
              />
            </div>
          </div>

          <div class="channel-description">
            {{ channel.description }}
          </div>

          <div class="channel-config" v-if="channel.config">
            <div class="config-item">
              <el-icon><Collection /></el-icon>
              <span v-if="channel.config.supportsBatch">{{ t('channels.supportsBatch') }}</span>
              <span v-else class="disabled-feature">{{ t('channels.noBatchSupport') }}</span>
            </div>
            <div class="config-item">
              <el-icon><Document /></el-icon>
              <span v-if="channel.config.supportsTemplate">{{ t('channels.supportsTemplate') }}</span>
              <span v-else class="disabled-feature">{{ t('channels.noTemplateSupport') }}</span>
            </div>
            <div class="config-item">
              <el-icon><View /></el-icon>
              <span v-if="channel.config.supportsTracking">{{ t('channels.supportsTracking') }}</span>
              <span v-else class="disabled-feature">{{ t('channels.noTrackingSupport') }}</span>
            </div>
          </div>

          <div class="channel-actions">
            <el-button size="small" @click="handleEdit(channel)">
              <el-icon><Edit /></el-icon>
              {{ t('common.edit') }}
            </el-button>
            <el-button size="small" @click="handleTest(channel)" v-if="!channel.builtin">
              <el-icon><Connection /></el-icon>
              {{ t('channels.test') }}
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(channel)"
              v-if="!channel.builtin"
            >
              <el-icon><Delete /></el-icon>
              {{ t('common.delete') }}
            </el-button>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="!loading && filteredChannels.length === 0" class="empty-state">
          <el-empty :description="t('channels.noChannels')">
            <el-button type="primary" @click="handleCreate">
              {{ t('channels.createChannel') }}
            </el-button>
          </el-empty>
        </div>
      </div>
    </div>

    <!-- 渠道编辑对话框 -->
    <ChannelEditor
      v-model="editorVisible"
      :channel="selectedChannel"
      :is-edit="isEdit"
      @success="handleEditorSuccess"
    />
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  Management,
  Check,
  Setting,
  Plus,
  Refresh,
  Collection,
  Document,
  View,
  Edit,
  Connection,
  Delete
} from '@element-plus/icons-vue';
import { useI18n } from 'vue-i18n';
import { useChannelsStore } from '@/stores/modules/channels';
import { CHANNEL_CATEGORIES } from '@/constants/channels';
import ChannelEditor from './ChannelEditor.vue';

const { t } = useI18n();
const channelsStore = useChannelsStore();

// Props
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
});

// Emits
const emit = defineEmits(['update:modelValue', 'channelsUpdated']);

// 响应式数据
const loading = ref(false);
const categoryFilter = ref('');
const editorVisible = ref(false);
const selectedChannel = ref(null);
const isEdit = ref(false);

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
});

const categories = CHANNEL_CATEGORIES;

// 计算属性
const channelStats = computed(() => channelsStore.stats);

const filteredChannels = computed(() => {
  let channels = channelsStore.channels;
  
  if (categoryFilter.value) {
    channels = channels.filter(c => c.category === categoryFilter.value);
  }
  
  return channels;
});

// 生命周期
onMounted(() => {
  loadChannels();
});

// 方法
const loadChannels = async () => {
  loading.value = true;
  try {
    await channelsStore.fetchChannels();
    await channelsStore.fetchChannelStats();
  } catch (error) {
    ElMessage.error(t('channels.loadError'));
  } finally {
    loading.value = false;
  }
};

const refreshChannels = () => {
  loadChannels();
};

const getCategoryLabel = (category) => {
  const cat = categories.find(c => c.value === category);
  return cat ? t(cat.labelKey) : category;
};

const handleCreate = () => {
  selectedChannel.value = null;
  isEdit.value = false;
  editorVisible.value = true;
};

const handleEdit = (channel) => {
  selectedChannel.value = { ...channel };
  isEdit.value = true;
  editorVisible.value = true;
};

const handleToggle = async (channel) => {
  try {
    await channelsStore.toggleChannel(channel.id, channel.enabled);
    ElMessage.success(
      channel.enabled 
        ? t('channels.channelEnabled') 
        : t('channels.channelDisabled')
    );
    emit('channelsUpdated');
  } catch (error) {
    ElMessage.error(t('channels.toggleError'));
    // 恢复原状态
    channel.enabled = !channel.enabled;
  }
};

const handleTest = async (channel) => {
  const loading = ElMessage.loading(t('channels.testing'));
  try {
    const result = await channelsStore.testChannel(channel.id);
    loading.close();
    
    if (result.success) {
      ElMessage.success(t('channels.testSuccess'));
    } else {
      ElMessage.warning(result.message || t('channels.testFailed'));
    }
  } catch (error) {
    loading.close();
    ElMessage.error(t('channels.testError'));
  }
};

const handleDelete = async (channel) => {
  try {
    await ElMessageBox.confirm(
      t('channels.deleteConfirm', { name: channel.name }),
      t('common.warning'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      }
    );

    await channelsStore.deleteChannel(channel.id);
    ElMessage.success(t('common.deleteSuccess'));
    emit('channelsUpdated');
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(t('common.deleteError'));
    }
  }
};

const handleEditorSuccess = () => {
  loadChannels();
  emit('channelsUpdated');
};
</script>

<style scoped>
.channel-manager {
  padding: 20px 0;
}

.stats-section {
  margin-bottom: 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: white;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.action-left {
  display: flex;
  gap: 12px;
}

.channels-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  min-height: 400px;
}

.channel-card {
  background: white;
  border: 1px solid #ebeef5;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
  position: relative;
}

.channel-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.channel-card.disabled {
  opacity: 0.6;
  background: #fafafa;
}

.channel-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.channel-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.channel-icon {
  font-size: 24px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 8px;
}

.channel-details {
  flex: 1;
}

.channel-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.channel-category {
  font-size: 12px;
  color: #909399;
}

.channel-description {
  color: #606266;
  font-size: 14px;
  line-height: 1.4;
  margin-bottom: 16px;
  min-height: 40px;
}

.channel-config {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.config-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.config-item .el-icon {
  color: #409eff;
}

.disabled-feature {
  color: #c0c4cc;
}

.channel-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.empty-state {
  grid-column: 1 / -1;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
}

:deep(.channel-manager-dialog) {
  .el-dialog__body {
    padding: 20px;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .channels-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .action-bar {
    flex-direction: column;
    gap: 12px;
  }
}
</style>