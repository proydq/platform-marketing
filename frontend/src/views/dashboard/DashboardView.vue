<script setup>
import { ref, onMounted, computed } from "vue";
import { useI18n } from "vue-i18n";
import {
  TrendCharts,
  Management,
  VideoPlay,
  Connection,
  View,
  Refresh,
  More,
  Download
} from "@element-plus/icons-vue";
import StatCard from "@/components/StatCard.vue";
import LineChart from "@/components/charts/LineChart.vue";

import {
  getDashboardStats,
  getSendTrend,
  getCustomerTrend,
  getRecentTasks,
  getTaskDetail,
} from "@/api/dashboard";

const { t } = useI18n();

const loading = ref(false);
const stats = ref({
  totalCustomers: 0,
  emailsSent: 0,
  openRate: 0,
  runningTasks: 0
});
const tasks = ref([]);
const drawerVisible = ref(false);
const currentTask = ref({});
const chartTab = ref("send");
const sendTrend = ref([]);
const customerTrend = ref([]);

// 计算属性
const dashboardStats = computed(() => [
  {
    title: "总客户数",
    value: stats.value.totalCustomers || 0,
    icon: "👥",
    color: "neon-blue",
    trend: "+12%"
  },
  {
    title: "今日邮件发送",
    value: stats.value.emailsSent || 0,
    icon: "📧",
    color: "neon-green",
    trend: "+8%"
  },
  {
    title: "邮件打开率",
    value: `${stats.value.openRate || 0}%`,
    icon: "📊",
    color: "neon-purple",
    trend: "+5%"
  },
  {
    title: "运行中任务",
    value: stats.value.runningTasks || 0,
    icon: "⚡",
    color: "neon-pink",
    trend: "+3"
  }
]);

onMounted(() => {
  fetchDashboardData();
});

const fetchDashboardData = async () => {
  loading.value = true;
  try {
    const [statsRes, sendRes, customerRes, tasksRes] = await Promise.all([
      getDashboardStats(),
      getSendTrend(),
      getCustomerTrend(),
      getRecentTasks()
    ]);
    
    stats.value = statsRes;
    sendTrend.value = sendRes;
    customerTrend.value = customerRes;
    tasks.value = tasksRes;
  } catch (error) {
    console.error('Failed to fetch dashboard data:', error);
  } finally {
    loading.value = false;
  }
};

function viewTask(row) {
  getTaskDetail(row.id).then((res) => {
    currentTask.value = res;
    drawerVisible.value = true;
  });
}

const refreshData = () => {
  fetchDashboardData();
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
    pending: "待执行",
    running: "执行中",
    completed: "已完成", 
    failed: "失败",
    paused: "已暂停",
  };
  return labels[status] || status;
};
</script>

<template>
  <div class="om-page-container">
    <!-- 页面头部 -->
    <div class="om-page-header">
      <div class="om-page-header__content">
        <div class="om-page-header__text">
          <h1>{{ t("menu.dashboard") }}</h1>
          <p>实时监控系统运行状态和核心业务指标</p>
        </div>
        <div class="om-page-header__actions">
          <el-button 
            type="primary" 
            size="large" 
            @click="refreshData"
            :loading="loading"
          >
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
          <el-button size="large">
            <el-icon><Download /></el-icon>
            导出报告
          </el-button>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="om-stats-grid">
      <div 
        v-for="(stat, index) in dashboardStats" 
        :key="index"
        :class="[
          'om-stat-card',
          `om-stat-card--${stat.color}`
        ]"
      >
        <div class="om-stat-card__icon">
          <span>{{ stat.icon }}</span>
        </div>
        <div class="om-stat-card__content">
          <div class="om-stat-card__value">{{ stat.value }}</div>
          <div class="om-stat-card__label">{{ stat.title }}</div>
          <div class="om-stat-card__trend om-stat-card__trend--up">
            <span>{{ stat.trend }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表卡片 -->
    <div class="om-content-card">
      <div class="om-content-card__header">
        <div class="om-content-card__header-left">
          <div class="om-content-card__icon">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="om-content-card__info">
            <h3>数据趋势分析</h3>
            <p>查看关键指标的变化趋势和数据走向</p>
          </div>
        </div>
        <div class="om-content-card__header-right">
          <el-radio-group v-model="chartTab" size="small">
            <el-radio-button value="send">邮件发送量</el-radio-button>
            <el-radio-button value="customer">客户增长</el-radio-button>
          </el-radio-group>
        </div>
      </div>
      <div class="om-content-card__body">
        <div v-if="chartTab === 'send'" v-loading="loading">
          <LineChart :data="sendTrend" />
        </div>
        <div v-if="chartTab === 'customer'" v-loading="loading">
          <LineChart :data="customerTrend" />
        </div>
      </div>
    </div>

    <!-- 任务列表卡片 -->
    <div class="om-content-card">
      <div class="om-content-card__header">
        <div class="om-content-card__header-left">
          <div class="om-content-card__icon">
            <el-icon><Management /></el-icon>
          </div>
          <div class="om-content-card__info">
            <h3>最近任务</h3>
            <p>查看最新的任务执行状态和进度</p>
          </div>
        </div>
        <div class="om-content-card__header-right">
          <el-button size="small" @click="refreshData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </div>
      <div class="om-content-card__body">
        <div class="om-table-container">
          <el-table 
            :data="tasks" 
            style="width: 100%"
            v-loading="loading"
            :empty-text="'暂无任务数据'"
          >
            <el-table-column
              prop="name"
              label="任务名称"
              min-width="200"
              show-overflow-tooltip
            >
              <template #default="{ row }">
                <div class="task-name-cell">
                  <div class="task-title">{{ row.name }}</div>
                  <div class="task-meta">
                    <span class="created-time">{{ row.createTime }}</span>
                  </div>
                </div>
              </template>
            </el-table-column>

            <el-table-column prop="status" label="状态" width="120" align="center">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small" effect="dark">
                  {{ getStatusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column
              prop="progress"
              label="进度"
              width="120"
              align="center"
            >
              <template #default="{ row }">
                <div class="progress-cell">
                  <el-progress 
                    :percentage="row.progress || 0" 
                    :stroke-width="6"
                    :show-text="false"
                    :status="row.progress === 100 ? 'success' : undefined"
                  />
                  <span class="progress-text">{{ row.progress || 0 }}%</span>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="操作" width="120" align="center">
              <template #default="{ row }">
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="viewTask(row)"
                  text
                >
                  <el-icon><View /></el-icon>
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>

    <!-- 任务详情抽屉 -->
    <el-drawer 
      v-model="drawerVisible" 
      title="任务详情" 
      size="400px"
      direction="rtl"
    >
      <div class="task-detail">
        <div class="task-detail__field">
          <label>任务名称</label>
          <span>{{ currentTask.name }}</span>
        </div>
        <div class="task-detail__field">
          <label>当前状态</label>
          <el-tag :type="getStatusType(currentTask.status)" effect="dark">
            {{ getStatusLabel(currentTask.status) }}
          </el-tag>
        </div>
        <div class="task-detail__field">
          <label>执行进度</label>
          <el-progress 
            :percentage="currentTask.progress || 0"
            :status="currentTask.progress === 100 ? 'success' : undefined"
          />
        </div>
        <div class="task-detail__steps">
          <label>执行阶段</label>
          <el-steps
            :active="currentTask.progress === 100 ? 3 : currentTask.progress > 0 ? 2 : 1"
            finish-status="success"
            direction="vertical"
          >
            <el-step title="任务创建" description="任务已创建并加入队列" />
            <el-step title="正在执行" description="任务正在后台执行中" />
            <el-step title="执行完成" description="任务已成功完成" />
          </el-steps>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<style scoped>
/* 任务名称单元格 */
.task-name-cell {
  padding: var(--om-space-2) 0;
}

.task-title {
  font-weight: var(--om-font-semibold);
  color: var(--om-text-primary);
  margin-bottom: var(--om-space-1);
  font-size: var(--om-font-sm);
  line-height: var(--om-line-tight);
}

.task-meta {
  display: flex;
  align-items: center;
  gap: var(--om-space-2);
}

.created-time {
  font-size: var(--om-font-xs);
  color: var(--om-text-tertiary);
}

/* 进度单元格 */
.progress-cell {
  display: flex;
  align-items: center;
  gap: var(--om-space-2);
}

.progress-text {
  font-size: var(--om-font-xs);
  font-weight: var(--om-font-semibold);
  color: var(--om-text-primary);
  min-width: 35px;
}

/* 任务详情样式 */
.task-detail {
  padding: var(--om-space-4);
}

.task-detail__field {
  margin-bottom: var(--om-space-6);
}

.task-detail__field label {
  display: block;
  font-size: var(--om-font-sm);
  font-weight: var(--om-font-semibold);
  color: var(--om-text-secondary);
  margin-bottom: var(--om-space-2);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.task-detail__field span {
  font-size: var(--om-font-md);
  color: var(--om-text-primary);
  font-weight: var(--om-font-medium);
}

.task-detail__steps {
  margin-top: var(--om-space-8);
}

.task-detail__steps label {
  display: block;
  font-size: var(--om-font-sm);
  font-weight: var(--om-font-semibold);
  color: var(--om-text-secondary);
  margin-bottom: var(--om-space-4);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .om-stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: var(--om-space-4);
  }
}

@media (max-width: 480px) {
  .om-stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
