<template>
  <div class="task-card" :class="getCardClass(task.status)">
    <div class="card-header">
      <div class="task-info">
        <h3 class="task-title">{{ task.name }}</h3>
        <div class="task-meta">
          <el-tag size="small" :type="getTypeTagType(task.type)">
            {{ getTypeLabel(task.type) }}
          </el-tag>
          <span class="created-time">{{ formatDate(task.createTime) }}</span>
        </div>
      </div>
      <div class="task-status">
        <el-tag :type="getStatusType(task.status)" size="small" effect="dark">
          {{ getStatusLabel(task.status) }}
        </el-tag>
      </div>
    </div>

    <div class="card-body">
      <!-- 平台标签 -->
      <div class="platform-section">
        <div class="section-label">{{ $t('customer.acquisition.platform') }}</div>
        <div class="platform-tags">
          <el-tag
            v-for="platform in getPlatformArray(task.platform).slice(0, 3)"
            :key="platform"
            size="small"
            class="platform-tag"
          >
            {{ getPlatformLabel(platform) }}
          </el-tag>
          <el-tag 
            v-if="getPlatformArray(task.platform).length > 3"
            size="small"
            type="info"
          >
            +{{ getPlatformArray(task.platform).length - 3 }}
          </el-tag>
        </div>
      </div>

      <!-- 进度信息 -->
      <div class="progress-section">
        <div class="progress-header">
          <span class="section-label">{{ $t('customer.acquisition.progress') }}</span>
          <span class="progress-text">{{ task.progress || 0 }}%</span>
        </div>
        <el-progress 
          :percentage="task.progress || 0" 
          :stroke-width="8"
          :show-text="false"
          :status="getProgressStatus(task.status)"
          class="progress-bar"
        />
      </div>

      <!-- 采集统计 -->
      <div class="stats-section">
        <div class="stat-item">
          <div class="stat-value">{{ task.collectedCount || 0 }}</div>
          <div class="stat-label">{{ $t('customer.acquisition.collected') }}</div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-value">{{ task.amount || 0 }}</div>
          <div class="stat-label">{{ $t('customer.acquisition.target') }}</div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-value">{{ task.successRate || 0 }}%</div>
          <div class="stat-label">{{ $t('customer.acquisition.successRate') }}</div>
        </div>
      </div>

      <!-- 关键词 -->
      <div v-if="task.keywords" class="keywords-section">
        <div class="section-label">{{ $t('customer.acquisition.keywords') }}</div>
        <div class="keywords">
          <el-tag
            v-for="keyword in getKeywords(task.keywords)"
            :key="keyword"
            size="small"
            type="info"
            effect="plain"
          >
            {{ keyword }}
          </el-tag>
        </div>
      </div>
    </div>

    <div class="card-footer">
      <div class="action-buttons">
        <el-tooltip :content="$t('customer.acquisition.start')" placement="top">
          <el-button
            size="small"
            type="success"
            :disabled="task.status === 'running'"
            @click="$emit('start', task)"
            circle
          >
            <el-icon><VideoPlay /></el-icon>
          </el-button>
        </el-tooltip>

        <el-tooltip :content="$t('customer.acquisition.pause')" placement="top">
          <el-button
            size="small"
            type="warning"
            :disabled="task.status !== 'running'"
            @click="$emit('stop', task)"
            circle
          >
            <el-icon><VideoPause /></el-icon>
          </el-button>
        </el-tooltip>

        <el-tooltip :content="$t('customer.acquisition.viewDetails')" placement="top">
          <el-button
            size="small"
            type="primary"
            @click="$emit('view', task)"
            circle
          >
            <el-icon><View /></el-icon>
          </el-button>
        </el-tooltip>

        <el-dropdown trigger="click" @command="handleCommand">
          <el-button size="small" circle>
            <el-icon><More /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="edit">
                <el-icon><Edit /></el-icon>
                {{ $t("common.edit") }}
              </el-dropdown-item>
              <el-dropdown-item command="duplicate">
                <el-icon><DocumentCopy /></el-icon>
                {{ $t("customer.acquisition.duplicate") }}
              </el-dropdown-item>
              <el-dropdown-item command="export">
                <el-icon><Download /></el-icon>
                {{ $t("customer.acquisition.exportData") }}
              </el-dropdown-item>
              <el-dropdown-item command="delete" divided>
                <el-icon><Delete /></el-icon>
                {{ $t("common.delete") }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 运行状态指示器 -->
    <div v-if="task.status === 'running'" class="running-indicator">
      <div class="pulse-dot"></div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { formatDate } from '@/utils/date'
import {
  VideoPlay,
  VideoPause,
  View,
  More,
  Edit,
  DocumentCopy,
  Download,
  Delete
} from '@element-plus/icons-vue'

const { t } = useI18n()

// Props
const props = defineProps({
  task: {
    type: Object,
    required: true
  }
})

// Emits
const emit = defineEmits(['start', 'stop', 'edit', 'view', 'export', 'delete', 'duplicate'])

// 平台选项
const platformOptions = [
  { label: "LinkedIn", value: "linkedin", icon: "🔗" },
  { label: "Facebook", value: "facebook", icon: "📘" },
  { label: "Google Maps", value: "google_maps", icon: "🗺️" },
  { label: "Yellow Pages", value: "yellow_pages", icon: "📞" },
  { label: "Twitter", value: "twitter", icon: "🐦" },
];

// 方法
const getCardClass = (status) => {
  return {
    'card-running': status === 'running',
    'card-completed': status === 'completed',
    'card-failed': status === 'failed',
    'card-paused': status === 'paused'
  }
}

const getTypeTagType = (type) => {
  const types = {
    customer: "primary",
    comment: "success", 
    article: "warning"
  };
  return types[type] || "info";
};

const getTypeLabel = (type) => {
  const types = {
    customer: t("customer.acquisition.typeCustomer"),
    comment: t("customer.acquisition.typeComment"),
    article: t("customer.acquisition.typeArticle"),
  };
  return types[type] || type;
};

const getStatusType = (status) => {
  const types = {
    pending: "info",
    running: "warning",
    completed: "success",
    failed: "danger",
    paused: "info",
  };
  return types[status] || "info";
};

const getStatusLabel = (status) => {
  const labels = {
    pending: t("customer.acquisition.statusPending"),
    running: t("customer.acquisition.statusRunning"),
    completed: t("customer.acquisition.statusCompleted"),
    failed: t("customer.acquisition.statusFailed"),
    paused: t("customer.acquisition.statusPaused"),
  };
  return labels[status] || status;
};

const getProgressStatus = (status) => {
  if (status === 'completed') return 'success';
  if (status === 'failed') return 'exception';
  if (status === 'running') return undefined; // 默认进度条
  return 'exception';
};

const getPlatformArray = (platform) => {
  if (Array.isArray(platform)) return platform;
  return platform ? platform.split(",").filter(Boolean) : [];
};

const getPlatformLabel = (platform) => {
  const option = platformOptions.find((p) => p.value === platform);
  return option ? `${option.icon} ${option.label}` : platform;
};

const getKeywords = (keywords) => {
  if (!keywords) return []
  return keywords.split(',').map(k => k.trim()).filter(Boolean).slice(0, 3)
}

const handleCommand = (command) => {
  emit(command, props.task)
}
</script>

<style scoped>
.task-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(248, 250, 252, 0.95) 100%);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.task-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.02) 0%, rgba(118, 75, 162, 0.02) 100%);
  z-index: -1;
}

.task-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.15);
}

.task-card.card-running {
  border-left: 4px solid #f59e0b;
}

.task-card.card-completed {
  border-left: 4px solid #10b981;
}

.task-card.card-failed {
  border-left: 4px solid #ef4444;
}

.task-card.card-paused {
  border-left: 4px solid #64748b;
}

/* 卡片头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.task-info {
  flex: 1;
}

.task-title {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1.2;
}

.task-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.created-time {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
}

.task-status {
  flex-shrink: 0;
}

/* 卡片主体 */
.card-body {
  margin-bottom: 20px;
}

.section-label {
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 8px;
}

.platform-section,
.progress-section,
.stats-section,
.keywords-section {
  margin-bottom: 20px;
}

.platform-tags,
.keywords {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.platform-tag {
  margin: 0;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.progress-text {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}

.progress-bar {
  margin-bottom: 4px;
}

.stats-section {
  display: flex;
  align-items: center;
  background: rgba(248, 250, 252, 0.7);
  padding: 16px;
  border-radius: 12px;
  border: 1px solid rgba(226, 232, 240, 0.5);
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-value {
  font-size: 20px;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 4px;
  letter-spacing: -0.5px;
}

.stat-label {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
}

.stat-divider {
  width: 1px;
  height: 32px;
  background: linear-gradient(to bottom, transparent, #e2e8f0, transparent);
  margin: 0 16px;
}

/* 卡片底部 */
.card-footer {
  padding-top: 16px;
  border-top: 1px solid rgba(226, 232, 240, 0.5);
}

.action-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
}

/* 运行状态指示器 */
.running-indicator {
  position: absolute;
  top: 16px;
  right: 16px;
}

.pulse-dot {
  width: 8px;
  height: 8px;
  background: #f59e0b;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(245, 158, 11, 0.7);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(245, 158, 11, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(245, 158, 11, 0);
  }
}

/* Element Plus 样式重写 */
:deep(.el-progress-bar__outer) {
  border-radius: 6px;
  background-color: rgba(226, 232, 240, 0.5);
}

:deep(.el-progress-bar__inner) {
  border-radius: 6px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
}

:deep(.el-button.is-circle) {
  width: 32px;
  height: 32px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .task-card {
    padding: 20px;
  }

  .task-title {
    font-size: 16px;
  }

  .stats-section {
    padding: 12px;
  }

  .stat-item {
    text-align: center;
  }

  .stat-value {
    font-size: 18px;
  }

  .action-buttons {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>