<template>
  <div class="acquisition-progress">
    <!-- 圆环进度条 -->
    <div class="progress-ring" v-if="displayType === 'ring'">
      <el-progress
        type="circle"
        :percentage="percentage"
        :width="size"
        :stroke-width="strokeWidth"
        :color="progressColor"
        :show-text="showText"
      >
        <template #default="{ percentage }">
          <div class="progress-content">
            <div class="progress-value">{{ percentage }}%</div>
            <div class="progress-status">{{ statusText }}</div>
          </div>
        </template>
      </el-progress>
    </div>

    <!-- 线性进度条 -->
    <div class="progress-line" v-else-if="displayType === 'line'">
      <div class="progress-header">
        <span class="progress-label">{{ label || statusText }}</span>
        <span class="progress-percentage">{{ percentage }}%</span>
      </div>
      <el-progress
        :percentage="percentage"
        :color="progressColor"
        :stroke-width="strokeWidth"
        :show-text="false"
      />
      <div class="progress-info" v-if="showDetails">
        <span class="progress-current">{{ current }} / {{ total }}</span>
        <span class="progress-speed" v-if="speed">{{ formatSpeed(speed) }}</span>
        <span class="progress-eta" v-if="eta">{{ formatETA(eta) }}</span>
      </div>
    </div>

    <!-- 步骤进度条 -->
    <div class="progress-steps" v-else-if="displayType === 'steps'">
      <el-steps
        :active="activeStep"
        :status="stepsStatus"
        direction="vertical"
        :space="60"
      >
        <el-step
          v-for="(step, index) in steps"
          :key="index"
          :title="step.title"
          :description="step.description"
          :status="getStepStatus(index)"
        >
          <template #icon>
            <el-icon v-if="getStepStatus(index) === 'finish'"><Check /></el-icon>
            <el-icon v-else-if="getStepStatus(index) === 'error'"><Close /></el-icon>
            <el-icon v-else-if="getStepStatus(index) === 'process'">
              <Loading class="rotating" />
            </el-icon>
            <span v-else>{{ index + 1 }}</span>
          </template>
        </el-step>
      </el-steps>
    </div>

    <!-- 仪表盘样式 -->
    <div class="progress-dashboard" v-else-if="displayType === 'dashboard'">
      <el-progress
        type="dashboard"
        :percentage="percentage"
        :width="size"
        :color="progressColor"
      >
        <template #default="{ percentage }">
          <div class="dashboard-content">
            <div class="dashboard-value">{{ percentage }}%</div>
            <div class="dashboard-status">{{ statusText }}</div>
            <div class="dashboard-details" v-if="showDetails">
              <div>{{ $t('customer.acquisition.collected') }}: {{ current }}</div>
              <div>{{ $t('customer.acquisition.target') }}: {{ total }}</div>
            </div>
          </div>
        </template>
      </el-progress>
    </div>

    <!-- 错误状态显示 -->
    <div class="progress-error" v-if="status === 'failed' && error">
      <el-alert
        :title="$t('customer.acquisition.taskFailed')"
        type="error"
        :description="error"
        show-icon
        :closable="false"
      />
    </div>

    <!-- 成功状态显示 -->
    <div class="progress-success" v-if="status === 'completed' && percentage === 100">
      <el-alert
        :title="$t('customer.acquisition.taskCompleted')"
        type="success"
        :description="$t('customer.acquisition.collectionComplete', { count: current })"
        show-icon
        :closable="false"
      />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Check, Close, Loading } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

// Props
const props = defineProps({
  // 进度值 (0-100)
  progress: {
    type: Number,
    default: 0
  },
  // 任务状态
  status: {
    type: String,
    default: 'pending', // pending, running, paused, completed, failed
    validator: (value) => ['pending', 'running', 'paused', 'completed', 'failed'].includes(value)
  },
  // 显示类型
  type: {
    type: String,
    default: 'ring', // ring, line, steps, dashboard
    validator: (value) => ['ring', 'line', 'steps', 'dashboard'].includes(value)
  },
  // 当前数量
  current: {
    type: Number,
    default: 0
  },
  // 目标数量
  target: {
    type: Number,
    default: 100
  },
  // 进度条大小
  size: {
    type: Number,
    default: 80
  },
  // 线条宽度
  strokeWidth: {
    type: Number,
    default: 6
  },
  // 是否显示文本
  showText: {
    type: Boolean,
    default: true
  },
  // 是否显示详细信息
  showDetails: {
    type: Boolean,
    default: false
  },
  // 标签文本
  label: {
    type: String,
    default: ''
  },
  // 处理速度 (每秒处理数量)
  speed: {
    type: Number,
    default: 0
  },
  // 预计完成时间 (秒)
  eta: {
    type: Number,
    default: 0
  },
  // 错误信息
  error: {
    type: String,
    default: ''
  },
  // 步骤配置 (用于 steps 类型)
  steps: {
    type: Array,
    default: () => []
  }
})

// 计算属性
const displayType = computed(() => props.type)

const percentage = computed(() => {
  if (props.target === 0) return 0
  return Math.min(Math.round((props.current / props.target) * 100), 100)
})

const total = computed(() => props.target)

const current = computed(() => props.current)

const statusText = computed(() => {
  const statusMap = {
    pending: t('customer.acquisition.statusPending'),
    running: t('customer.acquisition.statusRunning'),
    paused: t('customer.acquisition.statusPaused'),
    completed: t('customer.acquisition.statusCompleted'),
    failed: t('customer.acquisition.statusFailed')
  }
  return statusMap[props.status] || props.status
})

const progressColor = computed(() => {
  if (props.status === 'failed') return '#f56c6c'
  if (props.status === 'completed') return '#67c23a'
  if (props.status === 'paused') return '#e6a23c'
  if (props.status === 'running') return '#409eff'
  return '#909399'
})

const activeStep = computed(() => {
  if (!props.steps.length) return 0
  
  if (props.status === 'completed') return props.steps.length
  if (props.status === 'failed') return Math.floor(props.steps.length * percentage.value / 100)
  if (props.status === 'running') return Math.floor(props.steps.length * percentage.value / 100)
  
  return 0
})

const stepsStatus = computed(() => {
  if (props.status === 'failed') return 'error'
  if (props.status === 'completed') return 'finish'
  return 'process'
})

// 方法
const getStepStatus = (index) => {
  if (props.status === 'failed' && index === activeStep.value) return 'error'
  if (index < activeStep.value) return 'finish'
  if (index === activeStep.value && props.status === 'running') return 'process'
  return 'wait'
}

const formatSpeed = (speed) => {
  if (speed < 1) return `${(speed * 60).toFixed(1)}/min`
  return `${speed.toFixed(1)}/s`
}

const formatETA = (eta) => {
  if (eta < 60) return `${eta}s`
  if (eta < 3600) return `${Math.floor(eta / 60)}m ${eta % 60}s`
  const hours = Math.floor(eta / 3600)
  const minutes = Math.floor((eta % 3600) / 60)
  return `${hours}h ${minutes}m`
}

// 为步骤类型提供默认步骤
const defaultSteps = computed(() => {
  if (props.steps.length > 0) return props.steps
  
  return [
    {
      title: t('customer.acquisition.stepInitialize'),
      description: t('customer.acquisition.stepInitializeDesc')
    },
    {
      title: t('customer.acquisition.stepConnect'),
      description: t('customer.acquisition.stepConnectDesc')
    },
    {
      title: t('customer.acquisition.stepCollect'),
      description: t('customer.acquisition.stepCollectDesc')
    },
    {
      title: t('customer.acquisition.stepProcess'),
      description: t('customer.acquisition.stepProcessDesc')
    },
    {
      title: t('customer.acquisition.stepComplete'),
      description: t('customer.acquisition.stepCompleteDesc')
    }
  ]
})
</script>

<style scoped>
.acquisition-progress {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  padding: 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.3);
  position: relative;
  overflow: hidden;
}

.acquisition-progress::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.03) 0%, rgba(118, 75, 162, 0.03) 100%);
  z-index: -1;
}

/* 圆环进度条 */
.progress-ring {
  display: flex;
  justify-content: center;
  padding: 16px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.4);
}

.progress-content {
  text-align: center;
  padding: 8px;
}

.progress-value {
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  line-height: 1.2;
  margin-bottom: 4px;
}

.progress-status {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
  opacity: 0.8;
}

/* 线性进度条 */
.progress-line {
  width: 100%;
  background: rgba(255, 255, 255, 0.9);
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.progress-label {
  font-size: 15px;
  color: #374151;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.progress-label::before {
  content: '';
  width: 4px;
  height: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

.progress-percentage {
  font-size: 16px;
  color: #1e293b;
  font-weight: 700;
  padding: 6px 12px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-radius: 8px;
  border: 1px solid rgba(102, 126, 234, 0.2);
}

.progress-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding: 12px 16px;
  background: rgba(248, 250, 252, 0.7);
  border-radius: 10px;
  font-size: 13px;
  color: #64748b;
  border: 1px solid rgba(226, 232, 240, 0.5);
}

.progress-current {
  font-weight: 600;
  color: #374151;
}

.progress-speed,
.progress-eta {
  font-style: italic;
  padding: 4px 8px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 6px;
  border: 1px solid rgba(226, 232, 240, 0.5);
}

/* 步骤进度条 */
.progress-steps {
  width: 100%;
  max-width: 450px;
  background: rgba(255, 255, 255, 0.9);
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

/* 仪表盘样式 */
.progress-dashboard {
  display: flex;
  justify-content: center;
  background: rgba(255, 255, 255, 0.9);
  padding: 24px;
  border-radius: 20px;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.4);
  position: relative;
}

.progress-dashboard::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  border-radius: 20px;
  z-index: -1;
}

.dashboard-content {
  text-align: center;
  padding: 12px;
}

.dashboard-value {
  font-size: 24px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  line-height: 1.2;
  margin-bottom: 8px;
}

.dashboard-status {
  font-size: 16px;
  color: #374151;
  font-weight: 600;
  margin: 12px 0;
}

.dashboard-details {
  font-size: 14px;
  color: #64748b;
  background: rgba(248, 250, 252, 0.7);
  padding: 12px 16px;
  border-radius: 10px;
  border: 1px solid rgba(226, 232, 240, 0.5);
}

.dashboard-details > div {
  margin: 4px 0;
  padding: 2px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 状态显示 */
.progress-error,
.progress-success {
  width: 100%;
  margin-top: 16px;
  animation: slideInUp 0.5s ease-out;
}

:deep(.el-alert) {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

:deep(.el-alert--error) {
  background: linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%);
  border-left: 4px solid #ef4444;
}

:deep(.el-alert--success) {
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  border-left: 4px solid #22c55e;
}

/* Element Plus 进度条样式增强 */
:deep(.el-progress-circle) {
  filter: drop-shadow(0 4px 12px rgba(0, 0, 0, 0.1));
}

:deep(.el-progress__text) {
  font-weight: 700 !important;
}

:deep(.el-progress-bar__outer) {
  border-radius: 10px;
  background: rgba(226, 232, 240, 0.3);
  overflow: hidden;
}

:deep(.el-progress-bar__inner) {
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

:deep(.el-steps) {
  background: transparent;
}

:deep(.el-step__head) {
  color: #374151;
}

:deep(.el-step__title) {
  font-weight: 600;
  color: #374151;
}

:deep(.el-step__description) {
  color: #64748b;
  line-height: 1.5;
}

:deep(.el-step__head.is-finish) {
  border-color: #22c55e;
  color: #22c55e;
}

:deep(.el-step__head.is-process) {
  border-color: #667eea;
  color: #667eea;
}

:deep(.el-step__head.is-error) {
  border-color: #ef4444;
  color: #ef4444;
}

/* 动画效果 */
.rotating {
  animation: rotate 2s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
}

.progress-ring,
.progress-line,
.progress-dashboard,
.progress-steps {
  animation: slideInUp 0.6s ease-out;
}

/* 脉冲效果用于活跃状态 */
.progress-ring.active {
  animation: pulse 2s ease-in-out infinite;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .acquisition-progress {
    padding: 20px;
    gap: 16px;
  }
  
  .progress-line,
  .progress-dashboard,
  .progress-steps {
    padding: 16px;
  }
  
  .progress-info {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
    padding: 12px;
  }
  
  .progress-info > * {
    width: 100%;
    text-align: left;
  }
  
  .progress-speed,
  .progress-eta {
    margin-top: 4px;
  }
}

@media (max-width: 480px) {
  .acquisition-progress {
    padding: 16px;
    gap: 12px;
  }
  
  .progress-ring {
    padding: 12px;
  }
  
  .dashboard-value {
    font-size: 20px;
  }
  
  .dashboard-status {
    font-size: 14px;
  }
  
  .dashboard-details {
    font-size: 12px;
    padding: 8px 12px;
  }
  
  .progress-steps {
    max-width: 100%;
    padding: 12px;
  }
  
  .progress-value {
    font-size: 16px;
  }
  
  .progress-percentage {
    font-size: 14px;
    padding: 4px 8px;
  }
  
  .progress-label {
    font-size: 14px;
  }
}
</style>