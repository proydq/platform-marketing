<template>
  <el-drawer
    v-model="visible"
    :title="$t('customer.acquisition.taskDetails')"
    size="600px"
    :destroy-on-close="true"
  >
    <div v-loading="loading" class="task-details-container">
      <template v-if="taskData">
        <!-- 任务基本信息 -->
        <div class="detail-section">
          <h3 class="section-title">{{ $t('customer.acquisition.basicInfo') }}</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item :label="$t('customer.acquisition.taskName')">
              <span class="detail-value">{{ taskData.name }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('customer.acquisition.status')">
              <el-tag :type="getStatusType(taskData.status)" size="small">
                {{ getStatusLabel(taskData.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('customer.acquisition.type')">
              <el-tag type="info" size="small">
                {{ getTypeLabel(taskData.type) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('customer.acquisition.priority')">
              <el-tag :type="getPriorityType(taskData.priority)" size="small">
                {{ getPriorityLabel(taskData.priority) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('customer.acquisition.platform')" :span="2">
              <div class="platform-tags">
                <el-tag
                  v-for="platform in getPlatformArray(taskData.platform)"
                  :key="platform"
                  size="small"
                  class="platform-tag"
                >
                  {{ getPlatformLabel(platform) }}
                </el-tag>
              </div>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('customer.acquisition.cycle')">
              <span class="detail-value">{{ getCycleLabel(taskData.cycle) }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('common.createTime')">
              <span class="detail-value">{{ formatDate(taskData.createTime) }}</span>
            </el-descriptions-item>
            <el-descriptions-item
              v-if="taskData.description"
              :label="$t('customer.acquisition.description')"
              :span="2"
            >
              <span class="detail-value">{{ taskData.description }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 进度统计 -->
        <div class="detail-section">
          <h3 class="section-title">{{ $t('customer.acquisition.progressStats') }}</h3>
          <div class="progress-container">
            <AcquisitionProgress
              :progress="taskData.progress || 0"
              :status="taskData.status"
              :current="taskData.collectedCount || 0"
              :target="taskData.amount || 100"
              type="dashboard"
              :size="120"
              :show-details="true"
            />
          </div>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-value">{{ taskData.amount || 0 }}</div>
              <div class="stat-label">{{ $t('customer.acquisition.targetAmount') }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ taskData.collectedCount || 0 }}</div>
              <div class="stat-label">{{ $t('customer.acquisition.collected') }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ taskData.validCount || 0 }}</div>
              <div class="stat-label">{{ $t('customer.acquisition.valid') }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ formatPercentage(taskData.successRate || 0) }}</div>
              <div class="stat-label">{{ $t('customer.acquisition.successRate') }}</div>
            </div>
          </div>
        </div>

        <!-- 配置信息 -->
        <div class="detail-section">
          <h3 class="section-title">{{ $t('customer.acquisition.configInfo') }}</h3>
          <el-descriptions :column="1" border>
            <el-descriptions-item :label="$t('customer.acquisition.keywords')">
              <span class="detail-value">{{ taskData.keywords || '-' }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('customer.acquisition.region')">
              <span class="detail-value">{{ getRegionLabel(taskData.region) }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('customer.acquisition.fields')">
              <div class="fields-tags">
                <el-tag
                  v-for="field in getFieldsArray(taskData.fields)"
                  :key="field"
                  size="small"
                  type="info"
                  class="field-tag"
                >
                  {{ getFieldLabel(field) }}
                </el-tag>
              </div>
            </el-descriptions-item>
            <el-descriptions-item
              v-if="taskData.filters"
              :label="$t('customer.acquisition.filters')"
            >
              <span class="detail-value">{{ taskData.filters }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 执行历史 -->
        <div class="detail-section">
          <h3 class="section-title">{{ $t('customer.acquisition.executionHistory') }}</h3>
          <el-timeline v-if="executionHistory.length > 0">
            <el-timeline-item
              v-for="record in executionHistory"
              :key="record.id"
              :timestamp="formatDate(record.createdAt)"
              placement="top"
            >
              <div class="history-content">
                <div class="history-type">
                  <el-tag size="small" :type="getHistoryType(record.type)">
                    {{ getHistoryLabel(record.type) }}
                  </el-tag>
                </div>
                <div class="history-message">{{ record.message }}</div>
                <div v-if="record.details" class="history-details">
                  {{ record.details }}
                </div>
              </div>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else :description="$t('customer.acquisition.noHistory')" />
        </div>

        <!-- 错误信息 -->
        <div v-if="taskData.status === 'failed' && taskData.error" class="detail-section">
          <h3 class="section-title">{{ $t('customer.acquisition.errorInfo') }}</h3>
          <el-alert
            :title="$t('customer.acquisition.taskFailed')"
            type="error"
            :description="taskData.error"
            show-icon
            :closable="false"
          />
        </div>
      </template>
    </div>

    <template #footer>
      <div class="drawer-footer">
        <el-button @click="visible = false">{{ $t('common.close') }}</el-button>
        <el-button
          v-if="taskData && ['pending', 'paused'].includes(taskData.status)"
          type="success"
          @click="handleStartTask"
        >
          <el-icon><VideoPlay /></el-icon>
          {{ $t('customer.acquisition.start') }}
        </el-button>
        <el-button
          v-if="taskData && taskData.status === 'running'"
          type="warning"
          @click="handlePauseTask"
        >
          <el-icon><VideoPause /></el-icon>
          {{ $t('customer.acquisition.pause') }}
        </el-button>
        <el-button type="primary" @click="handleEditTask">
          <el-icon><Edit /></el-icon>
          {{ $t('common.edit') }}
        </el-button>
        <el-button @click="handleExportData">
          <el-icon><Download /></el-icon>
          {{ $t('customer.acquisition.exportData') }}
        </el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { VideoPlay, VideoPause, Edit, Download } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import { useCustomerStore } from '@/stores/modules/customer'
import { formatDate } from '@/utils/date'
import { formatPercentage } from '@/utils/format'
import AcquisitionProgress from './AcquisitionProgress.vue'

const { t } = useI18n()
const customerStore = useCustomerStore()

// Props
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  taskId: {
    type: [String, Number],
    default: null
  }
})

// Emits
const emit = defineEmits(['update:modelValue', 'edit', 'start', 'pause', 'export'])

// 响应式数据
const loading = ref(false)
const taskData = ref(null)
const executionHistory = ref([])

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 平台选项
const platformOptions = [
  { value: 'linkedin', label: 'LinkedIn', icon: '🔗' },
  { value: 'facebook', label: 'Facebook', icon: '📘' },
  { value: 'google_maps', label: 'Google Maps', icon: '🗺️' },
  { value: 'yellow_pages', label: 'Yellow Pages', icon: '📞' },
  { value: 'twitter', label: 'Twitter', icon: '🐦' }
]

// 地区选项
const regionOptions = {
  global: t('customer.acquisition.regionGlobal'),
  us: t('customer.acquisition.regionUS'),
  eu: t('customer.acquisition.regionEU'),
  asia: t('customer.acquisition.regionAsia'),
  china: t('customer.acquisition.regionChina')
}

// 字段选项
const fieldOptions = {
  name: t('customer.name'),
  email: t('customer.email'),
  phone: t('customer.phone'),
  company: t('customer.company'),
  title: t('customer.acquisition.jobTitle'),
  location: t('customer.acquisition.location'),
  social: t('customer.acquisition.socialProfile'),
  website: t('customer.acquisition.website')
}

// 监听器
watch([() => props.taskId, visible], async ([newTaskId, newVisible]) => {
  if (newVisible && newTaskId) {
    await loadTaskDetails(newTaskId)
  }
})

// 方法
const loadTaskDetails = async (taskId) => {
  loading.value = true
  try {
    // 加载任务基本信息
    taskData.value = await customerStore.fetchAcquisitionTask(taskId)
    
    // 加载执行历史（模拟数据）
    await loadExecutionHistory(taskId)
  } catch (error) {
    ElMessage.error(t('customer.acquisition.loadDetailsError'))
    console.error('加载任务详情失败:', error)
  } finally {
    loading.value = false
  }
}

const loadExecutionHistory = async (taskId) => {
  try {
    // 这里应该调用实际的API
    // executionHistory.value = await customerStore.fetchTaskHistory(taskId)
    
    // 模拟数据
    const historyTypes = ['start', 'progress', 'success', 'warning', 'error']
    executionHistory.value = Array.from({ length: 8 }, (_, index) => ({
      id: index + 1,
      type: historyTypes[Math.floor(Math.random() * historyTypes.length)],
      message: getRandomHistoryMessage(),
      details: Math.random() > 0.7 ? getRandomHistoryDetails() : null,
      createdAt: new Date(Date.now() - Math.random() * 7 * 24 * 60 * 60 * 1000)
    })).sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
  } catch (error) {
    console.error('加载执行历史失败:', error)
  }
}

const getRandomHistoryMessage = () => {
  const messages = [
    t('customer.acquisition.historyTaskStarted'),
    t('customer.acquisition.historyConnecting'),
    t('customer.acquisition.historyCollecting'),
    t('customer.acquisition.historyProcessing'),
    t('customer.acquisition.historyCompleted'),
    t('customer.acquisition.historyPaused'),
    t('customer.acquisition.historyResumed'),
    t('customer.acquisition.historyError')
  ]
  return messages[Math.floor(Math.random() * messages.length)]
}

const getRandomHistoryDetails = () => {
  const details = [
    t('customer.acquisition.detailsConnected', { count: Math.floor(Math.random() * 100) }),
    t('customer.acquisition.detailsCollected', { count: Math.floor(Math.random() * 50) }),
    t('customer.acquisition.detailsProcessed', { count: Math.floor(Math.random() * 30) }),
    t('customer.acquisition.detailsFiltered', { count: Math.floor(Math.random() * 10) })
  ]
  return details[Math.floor(Math.random() * details.length)]
}

// 辅助方法
const getPlatformArray = (platform) => {
  if (Array.isArray(platform)) return platform
  return platform ? platform.split(',').filter(Boolean) : []
}

const getPlatformLabel = (platform) => {
  const option = platformOptions.find(p => p.value === platform)
  return option ? `${option.icon} ${option.label}` : platform
}

const getFieldsArray = (fields) => {
  if (Array.isArray(fields)) return fields
  return fields ? fields.split(',').filter(Boolean) : []
}

const getFieldLabel = (field) => {
  return fieldOptions[field] || field
}

const getRegionLabel = (region) => {
  return regionOptions[region] || region
}

const getStatusType = (status) => {
  const types = {
    pending: 'info',
    running: 'warning',
    completed: 'success',
    failed: 'danger',
    paused: 'info'
  }
  return types[status] || 'info'
}

const getStatusLabel = (status) => {
  const labels = {
    pending: t('customer.acquisition.statusPending'),
    running: t('customer.acquisition.statusRunning'),
    completed: t('customer.acquisition.statusCompleted'),
    failed: t('customer.acquisition.statusFailed'),
    paused: t('customer.acquisition.statusPaused')
  }
  return labels[status] || status
}

const getTypeLabel = (type) => {
  const types = {
    customer: t('customer.acquisition.typeCustomer'),
    comment: t('customer.acquisition.typeComment'),
    article: t('customer.acquisition.typeArticle')
  }
  return types[type] || type
}

const getPriorityType = (priority) => {
  const types = {
    low: 'info',
    medium: '',
    high: 'warning',
    urgent: 'danger'
  }
  return types[priority] || ''
}

const getPriorityLabel = (priority) => {
  const labels = {
    low: t('customer.acquisition.priorityLow'),
    medium: t('customer.acquisition.priorityMedium'),
    high: t('customer.acquisition.priorityHigh'),
    urgent: t('customer.acquisition.priorityUrgent')
  }
  return labels[priority] || priority
}

const getCycleLabel = (cycle) => {
  const labels = {
    once: t('customer.acquisition.cycleOnce'),
    daily: t('customer.acquisition.cycleDaily'),
    weekly: t('customer.acquisition.cycleWeekly'),
    monthly: t('customer.acquisition.cycleMonthly')
  }
  return labels[cycle] || cycle
}

const getHistoryType = (type) => {
  const types = {
    start: 'primary',
    progress: 'info',
    success: 'success',
    warning: 'warning',
    error: 'danger'
  }
  return types[type] || 'info'
}

const getHistoryLabel = (type) => {
  const labels = {
    start: t('customer.acquisition.historyTypeStart'),
    progress: t('customer.acquisition.historyTypeProgress'),
    success: t('customer.acquisition.historyTypeSuccess'),
    warning: t('customer.acquisition.historyTypeWarning'),
    error: t('customer.acquisition.historyTypeError')
  }
  return labels[type] || type
}

// 事件处理
const handleStartTask = () => {
  emit('start', taskData.value)
}

const handlePauseTask = () => {
  emit('pause', taskData.value)
}

const handleEditTask = () => {
  emit('edit', taskData.value)
}

const handleExportData = () => {
  emit('export', taskData.value)
}
</script>

<style scoped>
.task-details-container {
  padding: 0 8px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: 100%;
  position: relative;
}

.task-details-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.02) 0%, rgba(118, 75, 162, 0.02) 100%);
  z-index: -1;
}

.detail-section {
  margin-bottom: 36px;
  background: rgba(255, 255, 255, 0.95);
  padding: 28px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.3);
  position: relative;
  overflow: hidden;
}

.detail-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.02) 0%, rgba(118, 75, 162, 0.02) 100%);
  z-index: -1;
}

.section-title {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  position: relative;
  padding-left: 16px;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

.detail-value {
  color: #374151;
  font-size: 15px;
  font-weight: 500;
}

.platform-tags,
.fields-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 12px;
  background: rgba(248, 250, 252, 0.7);
  border-radius: 10px;
  border: 1px solid rgba(226, 232, 240, 0.5);
}

.platform-tag,
.field-tag {
  margin: 0;
  border-radius: 8px;
  font-weight: 500;
  padding: 6px 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.2s ease;
}

.platform-tag:hover,
.field-tag:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.progress-container {
  display: flex;
  justify-content: center;
  margin-bottom: 28px;
  padding: 20px;
  background: rgba(248, 250, 252, 0.5);
  border-radius: 12px;
  border: 1px solid rgba(226, 232, 240, 0.5);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 24px 16px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border-radius: 16px;
  border: 2px solid transparent;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.stat-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: -1;
}

.stat-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.15);
  border-color: #667eea;
}

.stat-item:hover::before {
  opacity: 0.05;
}

.stat-value {
  font-size: 24px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 6px;
}

.stat-label {
  font-size: 13px;
  color: #64748b;
  font-weight: 600;
  opacity: 0.8;
}

.history-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 12px;
  border: 1px solid rgba(226, 232, 240, 0.5);
  transition: all 0.3s ease;
}

.history-content:hover {
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.history-type {
  display: flex;
  align-items: center;
  gap: 8px;
}

.history-message {
  color: #374151;
  font-size: 15px;
  line-height: 1.5;
  font-weight: 500;
}

.history-details {
  color: #64748b;
  font-size: 13px;
  line-height: 1.5;
  font-style: italic;
  padding: 8px 12px;
  background: rgba(248, 250, 252, 0.8);
  border-radius: 8px;
  border-left: 3px solid #667eea;
}

.drawer-footer {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  padding: 24px 0 0 0;
  border-top: 2px solid rgba(102, 126, 234, 0.1);
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px 16px 0 0;
  margin: 20px -8px 0 -8px;
  padding-left: 28px;
  padding-right: 28px;
}

/* Element Plus 组件样式增强 */
:deep(.el-drawer) {
  border-radius: 16px 0 0 16px;
  overflow: hidden;
}

:deep(.el-drawer__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 24px 28px;
  margin-bottom: 0;
  border: none;
}

:deep(.el-drawer__title) {
  color: white;
  font-weight: 700;
  font-size: 18px;
}

:deep(.el-drawer__close-btn) {
  color: white;
  font-size: 20px;
}

:deep(.el-drawer__close-btn:hover) {
  color: rgba(255, 255, 255, 0.8);
}

:deep(.el-descriptions) {
  background: transparent;
}

:deep(.el-descriptions__header) {
  display: none;
}

:deep(.el-descriptions__body) {
  background: transparent;
}

:deep(.el-descriptions__table) {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

:deep(.el-descriptions__label) {
  font-weight: 600;
  color: #374151;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  padding: 16px 20px;
  border: 1px solid rgba(226, 232, 240, 0.5);
}

:deep(.el-descriptions__content) {
  background: rgba(255, 255, 255, 0.9);
  padding: 16px 20px;
  border: 1px solid rgba(226, 232, 240, 0.5);
}

:deep(.el-timeline) {
  padding: 0;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  padding: 16px;
  border: 1px solid rgba(226, 232, 240, 0.5);
}

:deep(.el-timeline-item__timestamp) {
  color: #64748b;
  font-size: 13px;
  font-weight: 500;
}

:deep(.el-timeline-item__node) {
  border-width: 2px;
}

:deep(.el-timeline-item__wrapper) {
  padding-left: 32px;
}

:deep(.el-timeline-item__content) {
  margin-top: 0;
}

:deep(.el-alert) {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  margin-top: 16px;
}

:deep(.el-alert--error) {
  background: linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%);
  border-left: 4px solid #ef4444;
}

:deep(.el-empty) {
  background: rgba(255, 255, 255, 0.7);
  border-radius: 12px;
  padding: 32px;
  border: 1px solid rgba(226, 232, 240, 0.5);
}

:deep(.el-button) {
  border-radius: 10px;
  font-weight: 600;
  padding: 12px 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
}

:deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(102, 126, 234, 0.4);
}

:deep(.el-button--success) {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  box-shadow: 0 4px 16px rgba(16, 185, 129, 0.3);
}

:deep(.el-button--success:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(16, 185, 129, 0.4);
}

:deep(.el-button--warning) {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  box-shadow: 0 4px 16px rgba(245, 158, 11, 0.3);
}

:deep(.el-button--warning:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(245, 158, 11, 0.4);
}

:deep(.el-button--default) {
  background: rgba(255, 255, 255, 0.9);
  color: #64748b;
  border: 1px solid rgba(100, 116, 139, 0.2);
  backdrop-filter: blur(8px);
}

:deep(.el-button--default:hover) {
  background: rgba(255, 255, 255, 1);
  color: #374151;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 动画效果 */
@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.detail-section {
  animation: fadeInUp 0.6s ease-out;
}

.detail-section:nth-child(1) { animation-delay: 0.1s; }
.detail-section:nth-child(2) { animation-delay: 0.2s; }
.detail-section:nth-child(3) { animation-delay: 0.3s; }
.detail-section:nth-child(4) { animation-delay: 0.4s; }
.detail-section:nth-child(5) { animation-delay: 0.5s; }

.stat-item:nth-child(1) { animation: fadeInUp 0.6s ease-out 0.2s both; }
.stat-item:nth-child(2) { animation: fadeInUp 0.6s ease-out 0.3s both; }
.stat-item:nth-child(3) { animation: fadeInUp 0.6s ease-out 0.4s both; }
.stat-item:nth-child(4) { animation: fadeInUp 0.6s ease-out 0.5s both; }

.history-content {
  animation: slideInRight 0.6s ease-out;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .task-details-container {
    padding: 0 4px;
  }
  
  .detail-section {
    padding: 20px;
    margin-bottom: 24px;
  }
  
  .section-title {
    font-size: 16px;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }
  
  .stat-item {
    padding: 16px 12px;
  }
  
  .stat-value {
    font-size: 20px;
  }
  
  .drawer-footer {
    flex-wrap: wrap;
    gap: 12px;
    padding: 16px 20px 0 20px;
    margin: 16px -4px 0 -4px;
  }
  
  :deep(.el-button) {
    flex: 1;
    min-width: 0;
  }
}

@media (max-width: 480px) {
  .detail-section {
    padding: 16px;
    border-radius: 12px;
  }
  
  .section-title {
    font-size: 15px;
    padding-left: 12px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .stat-item {
    padding: 12px 8px;
  }
  
  .stat-value {
    font-size: 18px;
  }
  
  .platform-tags,
  .fields-tags {
    gap: 6px;
    padding: 8px;
  }
  
  .platform-tag,
  .field-tag {
    padding: 4px 8px;
    font-size: 12px;
  }
  
  .history-content {
    padding: 12px;
  }
  
  .progress-container {
    padding: 12px;
  }
  
  .drawer-footer {
    flex-direction: column;
    gap: 8px;
  }
  
  :deep(.el-button) {
    width: 100%;
    justify-content: center;
  }
}
</style>