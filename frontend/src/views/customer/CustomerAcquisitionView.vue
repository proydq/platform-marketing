<template>
  <div class="acquisition-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-text">
          <h1>{{ $t("customer.acquisition.title") }}</h1>
          <p>{{ $t("customer.acquisition.description") }}</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" size="large" @click="handleCreateTask">
            <el-icon><Plus /></el-icon>
            {{ $t("customer.acquisition.createTask") }}
          </el-button>
          <el-button size="large" @click="showPreview">
            <el-icon><View /></el-icon>
            {{ $t("customer.acquisition.previewData") }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon color="#409eff"><Management /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ taskStats.total }}</div>
          <div class="stat-label">{{ $t("customer.acquisition.totalTasks") }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon color="#67c23a"><VideoPlay /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ taskStats.running }}</div>
          <div class="stat-label">{{ $t("customer.acquisition.runningTasks") }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon color="#f56c6c"><Download /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ taskStats.collected }}</div>
          <div class="stat-label">{{ $t("customer.acquisition.totalCollected") }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon color="#e6a23c"><TrendCharts /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ taskStats.successRate }}%</div>
          <div class="stat-label">{{ $t("customer.acquisition.avgSuccessRate") }}</div>
        </div>
      </div>
    </div>

    <!-- 快速配置区域 -->
    <el-card class="quick-config-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-icon">
            <el-icon><Lightning /></el-icon>
          </div>
          <div class="header-info">
            <h3>{{ $t("customer.acquisition.quickConfig") }}</h3>
            <p>{{ $t("customer.acquisition.quickConfigDesc") }}</p>
          </div>
          <el-switch 
            v-model="quickConfigExpanded" 
            :active-text="$t('common.expand')"
            :inactive-text="$t('common.collapse')"
          />
        </div>
      </template>

      <el-collapse-transition>
        <div v-show="quickConfigExpanded">
          <QuickAcquisitionForm @submit="handleQuickAcquisition" />
        </div>
      </el-collapse-transition>
    </el-card>

    <!-- 任务管理区域 -->
    <el-card class="task-management-card" shadow="hover">
      <template #header>
        <div class="task-header">
          <div class="header-left">
            <div class="header-icon">
              <el-icon><List /></el-icon>
            </div>
            <div class="header-info">
              <h3>{{ $t("customer.acquisition.taskManagement") }}</h3>
              <p>{{ $t("customer.acquisition.taskManagementDesc") }}</p>
            </div>
          </div>
          <div class="header-right">
            <div class="filters">
              <el-select 
                v-model="statusFilter" 
                :placeholder="$t('common.filterByStatus')"
                clearable
                style="width: 120px"
                @change="handleFilterChange"
              >
                <el-option :label="$t('common.all')" value="" />
                <el-option :label="$t('customer.acquisition.statusPending')" value="pending" />
                <el-option :label="$t('customer.acquisition.statusRunning')" value="running" />
                <el-option :label="$t('customer.acquisition.statusCompleted')" value="completed" />
                <el-option :label="$t('customer.acquisition.statusPaused')" value="paused" />
                <el-option :label="$t('customer.acquisition.statusFailed')" value="failed" />
              </el-select>
              <el-select 
                v-model="platformFilter" 
                :placeholder="$t('customer.acquisition.filterByPlatform')"
                clearable
                style="width: 120px; margin-left: 8px"
                @change="handleFilterChange"
              >
                <el-option :label="$t('common.all')" value="" />
                <el-option 
                  v-for="platform in platformOptions"
                  :key="platform.value"
                  :label="platform.label"
                  :value="platform.value"
                />
              </el-select>
            </div>
            <div class="task-actions">
              <el-button @click="refreshTasks" :loading="loading">
                <el-icon><Refresh /></el-icon>
                {{ $t("common.refresh") }}
              </el-button>
              <el-dropdown @command="handleBatchAction" trigger="click">
                <el-button>
                  {{ $t("common.batchActions") }}
                  <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="start" :disabled="!hasSelectedTasks">
                      <el-icon><VideoPlay /></el-icon>
                      {{ $t("customer.acquisition.batchStart") }}
                    </el-dropdown-item>
                    <el-dropdown-item command="pause" :disabled="!hasSelectedTasks">
                      <el-icon><VideoPause /></el-icon>
                      {{ $t("customer.acquisition.batchPause") }}
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" divided :disabled="!hasSelectedTasks">
                      <el-icon><Delete /></el-icon>
                      {{ $t("customer.acquisition.batchDelete") }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </div>
      </template>

      <!-- 视图切换 -->
      <div class="view-controls">
        <div class="view-toggles">
          <el-radio-group v-model="viewMode" size="small">
            <el-radio-button value="table">
              <el-icon><Grid /></el-icon>
              {{ $t("customer.acquisition.tableView") }}
            </el-radio-button>
            <el-radio-button value="card">
              <el-icon><Menu /></el-icon>
              {{ $t("customer.acquisition.cardView") }}
            </el-radio-button>
          </el-radio-group>
        </div>
        <div class="task-search">
          <el-input
            v-model="searchKeyword"
            :placeholder="$t('customer.acquisition.searchTasks')"
            clearable
            style="width: 300px"
            @input="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
      </div>

      <!-- 表格视图 -->
      <div v-if="viewMode === 'table'" v-loading="loading">
        <el-table
          ref="taskTable"
          :data="filteredTaskList"
          stripe
          style="width: 100%"
          :empty-text="$t('common.noData')"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          
          <el-table-column prop="name" :label="$t('customer.acquisition.taskName')" min-width="200">
            <template #default="{ row }">
              <div class="task-name-cell">
                <div class="task-title">{{ row.name }}</div>
                <div class="task-meta">
                  <el-tag size="small" :type="getTypeTagType(row.type)">
                    {{ getTypeLabel(row.type) }}
                  </el-tag>
                  <span class="created-time">{{ formatDate(row.createTime, 'MM-DD HH:mm') }}</span>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="platforms" :label="$t('customer.acquisition.platform')" width="180">
            <template #default="{ row }">
              <div class="platform-tags">
                <el-tag
                  v-for="platform in getPlatformArray(row.platform).slice(0, 2)"
                  :key="platform"
                  size="small"
                  class="platform-tag"
                >
                  {{ getPlatformLabel(platform) }}
                </el-tag>
                <el-tag 
                  v-if="getPlatformArray(row.platform).length > 2"
                  size="small"
                  type="info"
                >
                  +{{ getPlatformArray(row.platform).length - 2 }}
                </el-tag>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="status" :label="$t('common.status')" width="120" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small" effect="dark">
                {{ getStatusLabel(row.status) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="progress" :label="$t('customer.acquisition.progress')" width="150" align="center">
            <template #default="{ row }">
              <div class="progress-cell">
                <el-progress 
                  :percentage="row.progress || 0" 
                  :stroke-width="6"
                  :show-text="false"
                  :status="getProgressStatus(row.status)"
                />
                <span class="progress-text">{{ row.progress || 0 }}%</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="collected" :label="$t('customer.acquisition.collected')" width="120" align="center">
            <template #default="{ row }">
              <div class="collected-cell">
                <div class="collected-number">{{ row.collectedCount || 0 }}</div>
                <div class="target-number">/ {{ row.amount || 0 }}</div>
              </div>
            </template>
          </el-table-column>

          <el-table-column :label="$t('common.actions')" width="180" fixed="right" align="center">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-tooltip :content="$t('customer.acquisition.start')" placement="top">
                  <el-button
                    size="small"
                    type="success"
                    :disabled="row.status === 'running'"
                    @click="handleStartTask(row)"
                    circle
                  >
                    <el-icon><VideoPlay /></el-icon>
                  </el-button>
                </el-tooltip>

                <el-tooltip :content="$t('customer.acquisition.pause')" placement="top">
                  <el-button
                    size="small"
                    type="warning"
                    :disabled="row.status !== 'running'"
                    @click="handleStopTask(row)"
                    circle
                  >
                    <el-icon><VideoPause /></el-icon>
                  </el-button>
                </el-tooltip>

                <el-dropdown trigger="click" @command="(command) => handleTaskAction(command, row)">
                  <el-button size="small" circle>
                    <el-icon><More /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="edit">
                        <el-icon><Edit /></el-icon>
                        {{ $t("common.edit") }}
                      </el-dropdown-item>
                      <el-dropdown-item command="details">
                        <el-icon><View /></el-icon>
                        {{ $t("customer.acquisition.viewDetails") }}
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
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 卡片视图 -->
      <div v-else v-loading="loading" class="task-cards">
        <div class="cards-grid">
          <TaskCard
            v-for="task in filteredTaskList"
            :key="task.id"
            :task="task"
            @start="handleStartTask"
            @stop="handleStopTask"
            @edit="handleEditTask"
            @view="handleViewDetails"
            @export="handleExportData"
            @delete="handleDeleteTask"
            @duplicate="handleDuplicateTask"
          />
        </div>
        
        <div v-if="!filteredTaskList?.length" class="empty-state">
          <el-empty :description="$t('customer.acquisition.noTasks')" />
        </div>
      </div>

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
    <TaskDetails v-model="taskDetailVisible" :task-id="selectedTaskId" />

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
import { ref, reactive, onMounted, computed, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Refresh,
  Plus,
  View,
  Edit,
  Delete,
  VideoPlay,
  VideoPause,
  More,
  Download,
  Management,
  TrendCharts,
  Lightning,
  List,
  ArrowDown,
  Grid,
  Menu,
  Search,
  DocumentCopy,
} from "@element-plus/icons-vue";
import { useI18n } from "vue-i18n";
import { useCustomerStore } from "@/stores/modules/customer";
import { formatDate } from "@/utils/date";
import { debounce } from "lodash-es";
import QuickAcquisitionForm from "./components/QuickAcquisitionForm.vue";
import AcquisitionTaskForm from "./components/AcquisitionTaskForm.vue";
import AcquisitionProgress from "./components/AcquisitionProgress.vue";
import TaskDetails from "./components/TaskDetails.vue";
import DataPreview from "./components/DataPreview.vue";
import TaskCard from "./components/TaskCard.vue";

const { t } = useI18n();
const customerStore = useCustomerStore();

// 响应式数据
const loading = ref(false);
const taskFormVisible = ref(false);
const taskDetailVisible = ref(false);
const previewVisible = ref(false);
const previewLoading = ref(false);
const isEditTask = ref(false);
const selectedTask = ref(null);
const selectedTaskId = ref(null);
const previewData = ref([]);

// 新增的响应式数据
const quickConfigExpanded = ref(true);
const viewMode = ref('table'); // 'table' | 'card'
const statusFilter = ref('');
const platformFilter = ref('');
const searchKeyword = ref('');
const selectedTasks = ref([]);
const taskTable = ref();

// 分页数据
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0,
});

// 平台选项
const platformOptions = [
  { label: "LinkedIn", value: "linkedin", icon: "🔗" },
  { label: "Facebook", value: "facebook", icon: "📘" },
  { label: "Google Maps", value: "google_maps", icon: "🗺️" },
  { label: "Yellow Pages", value: "yellow_pages", icon: "📞" },
  { label: "Twitter", value: "twitter", icon: "🐦" },
];

// 计算属性
const taskList = computed(() => customerStore.acquisitionTasks);

// 任务统计
const taskStats = computed(() => {
  const tasks = taskList.value || [];
  return {
    total: tasks.length,
    running: tasks.filter(t => t.status === 'running').length,
    collected: tasks.reduce((sum, t) => sum + (t.collectedCount || 0), 0),
    successRate: tasks.length > 0 
      ? Math.round(tasks.reduce((sum, t) => sum + (t.successRate || 0), 0) / tasks.length)
      : 0
  };
});

// 过滤后的任务列表
const filteredTaskList = computed(() => {
  let filtered = taskList.value || [];
  
  // 状态过滤
  if (statusFilter.value) {
    filtered = filtered.filter(task => task.status === statusFilter.value);
  }
  
  // 平台过滤
  if (platformFilter.value) {
    filtered = filtered.filter(task => {
      const platforms = getPlatformArray(task.platform);
      return platforms.includes(platformFilter.value);
    });
  }
  
  // 搜索过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    filtered = filtered.filter(task => 
      task.name?.toLowerCase().includes(keyword) ||
      task.keywords?.toLowerCase().includes(keyword) ||
      task.description?.toLowerCase().includes(keyword)
    );
  }
  
  return filtered;
});

// 是否有选中的任务
const hasSelectedTasks = computed(() => selectedTasks.value.length > 0);

// 生命周期
onMounted(() => {
  fetchTasks();
});

// 方法
const fetchTasks = async () => {
  loading.value = true;
  try {
    await customerStore.fetchAcquisitionTasks({
      page: pagination.page - 1,
      size: pagination.size,
    });
    pagination.total = customerStore.acquisitionTotal;
  } catch (error) {
    ElMessage.error(t("common.fetchError"));
  } finally {
    loading.value = false;
  }
};

const refreshTasks = () => {
  fetchTasks();
};

const handleCreateTask = () => {
  isEditTask.value = false;
  selectedTask.value = null;
  taskFormVisible.value = true;
};

const handleEditTask = (task) => {
  isEditTask.value = true;
  selectedTask.value = { ...task };
  taskFormVisible.value = true;
};

const handleDeleteTask = async (task) => {
  try {
    await ElMessageBox.confirm(
      t("customer.acquisition.deleteTaskConfirm", { name: task.name }),
      t("common.warning"),
      {
        confirmButtonText: t("common.confirm"),
        cancelButtonText: t("common.cancel"),
        type: "warning",
      }
    );

    await customerStore.deleteAcquisitionTask(task.id);
    ElMessage.success(t("common.deleteSuccess"));
    await fetchTasks();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(t("common.deleteError"));
    }
  }
};

const handleStartTask = async (task) => {
  try {
    await customerStore.startAcquisitionTask(task.id);
    ElMessage.success(t("customer.acquisition.taskStarted"));
    await fetchTasks();
  } catch (error) {
    ElMessage.error(t("customer.acquisition.startTaskError"));
  }
};

const handleStopTask = async (task) => {
  try {
    await customerStore.stopAcquisitionTask(task.id);
    ElMessage.success(t("customer.acquisition.taskStopped"));
    await fetchTasks();
  } catch (error) {
    ElMessage.error(t("customer.acquisition.stopTaskError"));
  }
};

const handleViewDetails = (task) => {
  selectedTaskId.value = task.id;
  taskDetailVisible.value = true;
};

const handleExportData = async (task) => {
  try {
    await customerStore.exportTaskData(task.id);
    ElMessage.success(t("customer.acquisition.exportSuccess"));
  } catch (error) {
    ElMessage.error(t("customer.acquisition.exportError"));
  }
};

const handleQuickAcquisition = async (config) => {
  try {
    await customerStore.createQuickAcquisition(config);
    ElMessage.success(t("customer.acquisition.quickAcquisitionCreated"));
    await fetchTasks();
  } catch (error) {
    ElMessage.error(t("customer.acquisition.quickAcquisitionError"));
  }
};

const showPreview = async () => {
  previewVisible.value = true;
  previewLoading.value = true;
  try {
    previewData.value = await customerStore.getPreviewData();
  } catch (error) {
    ElMessage.error(t("customer.acquisition.previewError"));
  } finally {
    previewLoading.value = false;
  }
};

const handlePageChange = (page) => {
  pagination.page = page;
  fetchTasks();
};

const handleSizeChange = (size) => {
  pagination.size = size;
  pagination.page = 1;
  fetchTasks();
};

const handleTaskFormSuccess = () => {
  fetchTasks();
};

// 辅助方法
const getPlatformArray = (platform) => {
  if (Array.isArray(platform)) return platform;
  return platform ? platform.split(",").filter(Boolean) : [];
};

const getPlatformLabel = (platform) => {
  const option = platformOptions.find((p) => p.value === platform);
  return option ? `${option.icon} ${option.label}` : platform;
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

const getCycleLabel = (cycle) => {
  const labels = {
    once: t("customer.acquisition.cycleOnce"),
    daily: t("customer.acquisition.cycleDaily"),
    weekly: t("customer.acquisition.cycleWeekly"),
    monthly: t("customer.acquisition.cycleMonthly"),
  };
  return labels[cycle] || cycle;
};

// 新增的辅助方法
const getTypeTagType = (type) => {
  const types = {
    customer: "primary",
    comment: "success", 
    article: "warning"
  };
  return types[type] || "info";
};

const getProgressStatus = (status) => {
  if (status === 'completed') return 'success';
  if (status === 'failed') return 'exception';
  if (status === 'running') return undefined; // 默认进度条
  return 'exception';
};

// 搜索处理 (防抖)
const handleSearch = debounce(() => {
  // 搜索已通过计算属性实现
}, 300);

// 过滤器变化处理
const handleFilterChange = () => {
  // 过滤已通过计算属性实现
};

// 选择变化处理
const handleSelectionChange = (selection) => {
  selectedTasks.value = selection;
};

// 任务操作处理
const handleTaskAction = (command, task) => {
  switch (command) {
    case 'edit':
      handleEditTask(task);
      break;
    case 'details':
      handleViewDetails(task);
      break;
    case 'duplicate':
      handleDuplicateTask(task);
      break;
    case 'export':
      handleExportData(task);
      break;
    case 'delete':
      handleDeleteTask(task);
      break;
  }
};

// 批量操作处理
const handleBatchAction = async (command) => {
  if (!selectedTasks.value.length) {
    ElMessage.warning(t("customer.acquisition.pleaseSelectTasks"));
    return;
  }

  try {
    await ElMessageBox.confirm(
      t("customer.acquisition.batchActionConfirm", { 
        action: t(`customer.acquisition.batch${command.charAt(0).toUpperCase() + command.slice(1)}`),
        count: selectedTasks.value.length 
      }),
      t("common.warning"),
      {
        confirmButtonText: t("common.confirm"),
        cancelButtonText: t("common.cancel"),
        type: "warning",
      }
    );

    const taskIds = selectedTasks.value.map(task => task.id);
    
    switch (command) {
      case 'start':
        await Promise.all(taskIds.map(id => customerStore.startAcquisitionTask(id)));
        ElMessage.success(t("customer.acquisition.batchStartSuccess"));
        break;
      case 'pause':
        await Promise.all(taskIds.map(id => customerStore.stopAcquisitionTask(id)));
        ElMessage.success(t("customer.acquisition.batchPauseSuccess"));
        break;
      case 'delete':
        await Promise.all(taskIds.map(id => customerStore.deleteAcquisitionTask(id)));
        ElMessage.success(t("customer.acquisition.batchDeleteSuccess"));
        break;
    }
    
    selectedTasks.value = [];
    await fetchTasks();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(t("customer.acquisition.batchActionError"));
    }
  }
};

// 复制任务
const handleDuplicateTask = async (task) => {
  try {
    const duplicatedTask = {
      ...task,
      id: undefined,
      name: `${task.name} (copy)`,
      status: 'pending',
      progress: 0,
      collectedCount: 0,
      createdAt: undefined
    };
    
    await customerStore.createAcquisitionTask(duplicatedTask);
    ElMessage.success(t("customer.acquisition.duplicateSuccess"));
    await fetchTasks();
  } catch (error) {
    ElMessage.error(t("customer.acquisition.duplicateError"));
  }
};
</script>

<style scoped>
.acquisition-container {
  padding: 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: 100vh;
  position: relative;
}

.acquisition-container::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.02) 0%, rgba(118, 75, 162, 0.02) 100%);
  z-index: -1;
}

/* 页面头部样式 */
.page-header {
  margin-bottom: 32px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(248, 250, 252, 0.95) 100%);
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
}

.header-text h1 {
  margin: 0 0 12px 0;
  font-size: 32px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: 0.5px;
}

.header-text p {
  margin: 0;
  color: #64748b;
  font-size: 16px;
  font-weight: 500;
  opacity: 0.8;
}

.header-actions {
  display: flex;
  gap: 12px;
  flex-shrink: 0;
}

/* 统计卡片样式 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(248, 250, 252, 0.95) 100%);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.15);
}

.stat-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-icon .el-icon {
  font-size: 24px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 4px;
  letter-spacing: -0.5px;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

/* 卡片样式 */
.quick-config-card,
.task-management-card {
  margin-bottom: 32px;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(248, 250, 252, 0.95) 100%);
  backdrop-filter: blur(10px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.quick-config-card:hover,
.task-management-card:hover {
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.12);
  transform: translateY(-2px);
}

.card-header,
.task-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 0;
}

.header-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.header-icon .el-icon {
  font-size: 18px;
}

.header-info h3 {
  margin: 0 0 4px 0;
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
}

.header-info p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
}

.task-header {
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.filters {
  display: flex;
  align-items: center;
  gap: 8px;
}

.task-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

/* 视图控制样式 */
.view-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px 0;
  border-bottom: 1px solid rgba(226, 232, 240, 0.5);
}

.view-toggles {
  display: flex;
  align-items: center;
  gap: 12px;
}

.task-search {
  flex-shrink: 0;
}

/* 表格样式增强 */
.task-name-cell {
  padding: 8px 0;
}

.task-title {
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 4px;
  font-size: 14px;
  line-height: 1.3;
}

.task-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.created-time {
  font-size: 12px;
  color: #64748b;
}

.platform-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.platform-tag {
  margin: 0;
  font-size: 11px;
}

.progress-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.progress-text {
  font-size: 12px;
  font-weight: 600;
  color: #1e293b;
  min-width: 35px;
}

.collected-cell {
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: 2px;
}

.collected-number {
  font-weight: 700;
  color: #1e293b;
  font-size: 16px;
}

.target-number {
  font-size: 12px;
  color: #64748b;
}

.action-buttons {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

/* 卡片视图样式 */
.task-cards {
  margin-top: 24px;
}

.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 24px;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 300px;
}

/* 分页样式 */
.pagination-wrapper {
  margin-top: 32px;
  display: flex;
  justify-content: center;
  padding-top: 24px;
  border-top: 1px solid rgba(226, 232, 240, 0.5);
}

/* Element Plus 组件样式增强 */
:deep(.el-card) {
  border: none;
}

:deep(.el-card__header) {
  background: transparent;
  padding: 28px 28px 0 28px;
  border-bottom: none;
}

:deep(.el-card__body) {
  padding: 24px 28px 28px 28px;
}

:deep(.el-table) {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: none;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(8px);
}

:deep(.el-table__header) {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
}

:deep(.el-table__header th) {
  background: transparent;
  border: none;
  color: #374151;
  font-weight: 600;
  padding: 16px 12px;
  font-size: 13px;
}

:deep(.el-table__body tr) {
  background: rgba(255, 255, 255, 0.5);
  transition: all 0.2s ease;
}

:deep(.el-table__body tr:hover) {
  background: rgba(102, 126, 234, 0.08);
}

:deep(.el-table__body td) {
  border: none;
  padding: 12px;
  vertical-align: middle;
}

:deep(.el-progress-bar__outer) {
  border-radius: 4px;
  background-color: rgba(226, 232, 240, 0.6);
}

:deep(.el-progress-bar__inner) {
  border-radius: 4px;
}

:deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

:deep(.el-button--primary:hover) {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

:deep(.el-button--success) {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border: none;
}

:deep(.el-button--warning) {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  border: none;
}

:deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid #e2e8f0;
  background: rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

:deep(.el-select .el-input__wrapper) {
  border-radius: 8px;
}

:deep(.el-dropdown-menu) {
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
}

:deep(.el-pagination) {
  background: rgba(255, 255, 255, 0.8);
  padding: 16px 24px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  backdrop-filter: blur(10px);
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.acquisition-container {
  animation: fadeInUp 0.6s ease-out;
}

.page-header {
  animation: slideInRight 0.8s ease-out 0.1s both;
}

.stats-grid .stat-card:nth-child(1) {
  animation: fadeInUp 0.6s ease-out 0.2s both;
}
.stats-grid .stat-card:nth-child(2) {
  animation: fadeInUp 0.6s ease-out 0.3s both;
}
.stats-grid .stat-card:nth-child(3) {
  animation: fadeInUp 0.6s ease-out 0.4s both;
}
.stats-grid .stat-card:nth-child(4) {
  animation: fadeInUp 0.6s ease-out 0.5s both;
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
