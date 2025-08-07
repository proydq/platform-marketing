<template>
  <div class="acquisition-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>{{ $t('customer.acquisition.title') }}</h2>
      <p>{{ $t('customer.acquisition.description') }}</p>
    </div>

    <!-- 快速配置区域 -->
    <el-card class="quick-config-card" shadow="never">
      <template #header>
        <div class="card-header">
          <h3>{{ $t('customer.acquisition.quickConfig') }}</h3>
          <p>{{ $t('customer.acquisition.quickConfigDesc') }}</p>
        </div>
      </template>
      
      <QuickAcquisitionForm @submit="handleQuickAcquisition" />
    </el-card>

    <!-- 任务管理区域 -->
    <el-card class="task-management-card">
      <template #header>
        <div class="task-header">
          <h3>{{ $t('customer.acquisition.taskManagement') }}</h3>
          <div class="task-actions">
            <el-button @click="refreshTasks">
              <el-icon><Refresh /></el-icon>
              {{ $t('common.refresh') }}
            </el-button>
            <el-button type="primary" @click="handleCreateTask">
              <el-icon><Plus /></el-icon>
              {{ $t('customer.acquisition.createTask') }}
            </el-button>
            <el-button @click="showPreview">
              <el-icon><View /></el-icon>
              {{ $t('customer.acquisition.previewData') }}
            </el-button>
          </div>
        </div>
      </template>

      <!-- 任务列表 -->
      <el-table
        :data="taskList"
        v-loading="loading"
        stripe
        style="width: 100%"
        :empty-text="$t('common.noData')"
      >
        <el-table-column prop="name" :label="$t('customer.acquisition.taskName')" min-width="180" />
        
        <el-table-column prop="platform" :label="$t('customer.acquisition.platform')" width="150">
          <template #default="{ row }">
            <div class="platform-tags">
              <el-tag 
                v-for="platform in getPlatformArray(row.platform)" 
                :key="platform" 
                size="small" 
                class="platform-tag"
              >
                {{ getPlatformLabel(platform) }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="type" :label="$t('customer.acquisition.type')" width="120">
          <template #default="{ row }">
            <el-tag type="info" size="small">
              {{ getTypeLabel(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" :label="$t('common.status')" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="progress" :label="$t('customer.acquisition.progress')" width="120" align="center">
          <template #default="{ row }">
            <AcquisitionProgress :progress="row.progress" :status="row.status" />
          </template>
        </el-table-column>
        
        <el-table-column prop="cycle" :label="$t('customer.acquisition.cycle')" width="100">
          <template #default="{ row }">
            {{ getCycleLabel(row.cycle) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="amount" :label="$t('customer.acquisition.targetAmount')" width="100" align="center" />
        
        <el-table-column prop="collectedCount" :label="$t('customer.acquisition.collectedCount')" width="100" align="center" />
        
        <el-table-column prop="createTime" :label="$t('common.createTime')" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column :label="$t('common.actions')" width="200" fixed="right">
          <template #default="{ row }">
            <el-button 
              size="small" 
              :disabled="row.status === 'running'"
              @click="handleStartTask(row)"
            >
              <el-icon><VideoPlay /></el-icon>
              {{ $t('customer.acquisition.start') }}
            </el-button>
            
            <el-button 
              size="small" 
              :disabled="row.status !== 'running'"
              @click="handleStopTask(row)"
            >
              <el-icon><VideoPause /></el-icon>
              {{ $t('customer.acquisition.stop') }}
            </el-button>
            
            <el-dropdown trigger="click">
              <el-button size="small">
                <el-icon><More /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleEditTask(row)">
                    <el-icon><Edit /></el-icon>
                    {{ $t('common.edit') }}
                  </el-dropdown-item>
                  <el-dropdown-item @click="handleViewDetails(row)">
                    <el-icon><View /></el-icon>
                    {{ $t('customer.acquisition.viewDetails') }}
                  </el-dropdown-item>
                  <el-dropdown-item @click="handleExportData(row)">
                    <el-icon><Download /></el-icon>
                    {{ $t('customer.acquisition.exportData') }}
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleDeleteTask(row)">
                    <el-icon><Delete /></el-icon>
                    {{ $t('common.delete') }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 任务表单对话框 -->
    <AcquisitionTaskForm
      v-model="taskFormVisible"
      :task="selectedTask"
      :is-edit="isEditTask"
      @success="handleTaskFormSuccess"
    />

    <!-- 任务详情抽屉 -->
    <TaskDetails
      v-model="taskDetailVisible"
      :task-id="selectedTaskId"
    />

    <!-- 数据预览对话框 -->
    <el-dialog 
      v-model="previewVisible" 
      :title="$t('customer.acquisition.previewData')" 
      width="80%"
      :destroy-on-close="true"
    >
      <DataPreview :data="previewData" :loading="previewLoading" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Refresh, 
  Plus, 
  View, 
  Edit, 
  Delete, 
  VideoPlay, 
  VideoPause, 
  More,
  Download
} from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import { useCustomerStore } from '@/stores/modules/customer'
import { formatDate } from '@/utils/date'
import QuickAcquisitionForm from './components/QuickAcquisitionForm.vue'
import AcquisitionTaskForm from './components/AcquisitionTaskForm.vue'
import AcquisitionProgress from './components/AcquisitionProgress.vue'
import TaskDetails from './components/TaskDetails.vue'
import DataPreview from './components/DataPreview.vue'

const { t } = useI18n()
const customerStore = useCustomerStore()

// 响应式数据
const loading = ref(false)
const taskFormVisible = ref(false)
const taskDetailVisible = ref(false)
const previewVisible = ref(false)
const previewLoading = ref(false)
const isEditTask = ref(false)
const selectedTask = ref(null)
const selectedTaskId = ref(null)
const previewData = ref([])

// 分页数据
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 平台选项
const platformOptions = [
  { label: 'LinkedIn', value: 'linkedin', icon: '🔗' },
  { label: 'Facebook', value: 'facebook', icon: '📘' },
  { label: 'Google Maps', value: 'google_maps', icon: '🗺️' },
  { label: 'Yellow Pages', value: 'yellow_pages', icon: '📞' },
  { label: 'Twitter', value: 'twitter', icon: '🐦' }
]

// 计算属性
const taskList = computed(() => customerStore.acquisitionTasks)

// 生命周期
onMounted(() => {
  fetchTasks()
})

// 方法
const fetchTasks = async () => {
  loading.value = true
  try {
    await customerStore.fetchAcquisitionTasks({
      page: pagination.page - 1,
      size: pagination.size
    })
    pagination.total = customerStore.acquisitionTotal
  } catch (error) {
    ElMessage.error(t('common.fetchError'))
  } finally {
    loading.value = false
  }
}

const refreshTasks = () => {
  fetchTasks()
}

const handleCreateTask = () => {
  isEditTask.value = false
  selectedTask.value = null
  taskFormVisible.value = true
}

const handleEditTask = (task) => {
  isEditTask.value = true
  selectedTask.value = { ...task }
  taskFormVisible.value = true
}

const handleDeleteTask = async (task) => {
  try {
    await ElMessageBox.confirm(
      t('customer.acquisition.deleteTaskConfirm', { name: task.name }),
      t('common.warning'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      }
    )
    
    await customerStore.deleteAcquisitionTask(task.id)
    ElMessage.success(t('common.deleteSuccess'))
    await fetchTasks()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(t('common.deleteError'))
    }
  }
}

const handleStartTask = async (task) => {
  try {
    await customerStore.startAcquisitionTask(task.id)
    ElMessage.success(t('customer.acquisition.taskStarted'))
    await fetchTasks()
  } catch (error) {
    ElMessage.error(t('customer.acquisition.startTaskError'))
  }
}

const handleStopTask = async (task) => {
  try {
    await customerStore.stopAcquisitionTask(task.id)
    ElMessage.success(t('customer.acquisition.taskStopped'))
    await fetchTasks()
  } catch (error) {
    ElMessage.error(t('customer.acquisition.stopTaskError'))
  }
}

const handleViewDetails = (task) => {
  selectedTaskId.value = task.id
  taskDetailVisible.value = true
}

const handleExportData = async (task) => {
  try {
    await customerStore.exportTaskData(task.id)
    ElMessage.success(t('customer.acquisition.exportSuccess'))
  } catch (error) {
    ElMessage.error(t('customer.acquisition.exportError'))
  }
}

const handleQuickAcquisition = async (config) => {
  try {
    await customerStore.createQuickAcquisition(config)
    ElMessage.success(t('customer.acquisition.quickAcquisitionCreated'))
    await fetchTasks()
  } catch (error) {
    ElMessage.error(t('customer.acquisition.quickAcquisitionError'))
  }
}

const showPreview = async () => {
  previewVisible.value = true
  previewLoading.value = true
  try {
    previewData.value = await customerStore.getPreviewData()
  } catch (error) {
    ElMessage.error(t('customer.acquisition.previewError'))
  } finally {
    previewLoading.value = false
  }
}

const handlePageChange = (page) => {
  pagination.page = page
  fetchTasks()
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  fetchTasks()
}

const handleTaskFormSuccess = () => {
  fetchTasks()
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

const getTypeLabel = (type) => {
  const types = {
    customer: t('customer.acquisition.typeCustomer'),
    comment: t('customer.acquisition.typeComment'), 
    article: t('customer.acquisition.typeArticle')
  }
  return types[type] || type
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

const getCycleLabel = (cycle) => {
  const labels = {
    once: t('customer.acquisition.cycleOnce'),
    daily: t('customer.acquisition.cycleDaily'),
    weekly: t('customer.acquisition.cycleWeekly'),
    monthly: t('customer.acquisition.cycleMonthly')
  }
  return labels[cycle] || cycle
}
</script>

<style scoped>
.acquisition-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.page-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.quick-config-card,
.task-management-card {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header h3,
.task-header h3 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.card-header p {
  margin: 0;
  color: #909399;
  font-size: 13px;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 16px;
}

.task-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.platform-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.platform-tag {
  margin: 0;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .acquisition-container {
    padding: 12px;
  }
  
  .task-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .task-actions {
    justify-content: center;
  }
  
  .platform-tags {
    justify-content: flex-start;
  }
}
</style>