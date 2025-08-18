<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import StatCard from "@/components/StatCard.vue";
import LineChart from "@/components/charts/LineChart.vue";
import { ElMessage, ElNotification } from "element-plus";
import { Plus, Refresh, View, Download, Connection } from '@element-plus/icons-vue';
import wsService from "@/utils/websocket";

import {
  getDashboardStats,
  getSendTrend,
  getCustomerTrend,
  getRecentTasks,
  getTaskDetail,
  createTask as createTaskAPI,
  updateTaskStatus,
  deleteTask,
  retryTask as retryTaskAPI,
} from "@/api/dashboard";

const stats = ref({
  totalCustomers: 1245,
  emailsSent: 328,
  openRate: 65.8,
  runningTasks: 3
});

// WebSocket 相关状态
const wsConnected = ref(false);
const realTimeUpdates = ref([]);
const liveTaskUpdates = ref(new Map());

const tasks = ref([
  {
    id: 1,
    name: "客户数据导入任务",
    status: "running",
    progress: 75,
    type: "import",
    createdAt: "2024-01-15 10:30:00",
    estimatedTime: "5分钟"
  },
  {
    id: 2,
    name: "邮件营销活动推送",
    status: "completed",
    progress: 100,
    type: "email",
    createdAt: "2024-01-15 09:15:00",
    estimatedTime: "已完成"
  },
  {
    id: 3,
    name: "WhatsApp消息群发",
    status: "pending",
    progress: 0,
    type: "whatsapp",
    createdAt: "2024-01-15 11:00:00",
    estimatedTime: "等待中"
  },
  {
    id: 4,
    name: "LinkedIn自动连接",
    status: "failed",
    progress: 45,
    type: "linkedin",
    createdAt: "2024-01-15 08:45:00",
    estimatedTime: "任务失败"
  }
]);

const drawerVisible = ref(false);
const currentTask = ref({});
const chartTab = ref("send");
const createTaskVisible = ref(false);
const newTask = ref({
  name: '',
  type: '',
  description: '',
  // 爬虫配置
  crawlConfig: {
    targetUrl: '',
    dataType: '',
    selectors: '',
    frequency: 'once'
  }
});

// 模拟邮件发送趋势数据
const sendTrend = ref([
  { name: "01-10", value: 245 },
  { name: "01-11", value: 312 },
  { name: "01-12", value: 189 },
  { name: "01-13", value: 428 },
  { name: "01-14", value: 356 },
  { name: "01-15", value: 482 },
  { name: "01-16", value: 391 }
]);

// 模拟客户增长趋势数据
const customerTrend = ref([
  { name: "01-10", value: 1120 },
  { name: "01-11", value: 1145 },
  { name: "01-12", value: 1167 },
  { name: "01-13", value: 1198 },
  { name: "01-14", value: 1223 },
  { name: "01-15", value: 1245 },
  { name: "01-16", value: 1267 }
]);

onMounted(async () => {
  try {
    // 尝试加载真实数据，如果失败则使用模拟数据
    const [statsRes, sendRes, customerRes, tasksRes] = await Promise.allSettled([
      getDashboardStats(),
      getSendTrend(),
      getCustomerTrend(),
      getRecentTasks()
    ]);
    
    if (statsRes.status === 'fulfilled') {
      stats.value = statsRes.value;
    }
    
    if (sendRes.status === 'fulfilled') {
      sendTrend.value = sendRes.value;
    }
    
    if (customerRes.status === 'fulfilled') {
      customerTrend.value = customerRes.value;
    }
    
    if (tasksRes.status === 'fulfilled') {
      tasks.value = tasksRes.value;
    }
  } catch (error) {
    console.log('使用模拟数据');
  }

  // 初始化WebSocket连接
  initWebSocket();
});

onUnmounted(() => {
  // 清理WebSocket连接
  wsService.disconnect();
});

// 初始化WebSocket连接
function initWebSocket() {
  // 连接WebSocket
  wsService.connect();
  
  // 监听连接状态
  wsService.on('connected', () => {
    wsConnected.value = true;
    ElNotification({
      title: '实时监控',
      message: '已连接到实时监控服务',
      type: 'success',
      duration: 3000
    });
  });

  wsService.on('disconnected', () => {
    wsConnected.value = false;
    ElNotification({
      title: '实时监控',
      message: '实时监控连接已断开',
      type: 'warning',
      duration: 3000
    });
  });

  // 监听任务更新
  wsService.on('task_update', (data) => {
    handleTaskUpdate(data);
  });

  // 监听营销活动更新
  wsService.on('campaign_update', (data) => {
    handleCampaignUpdate(data);
  });

  // 监听统计数据更新
  wsService.on('stats_update', (data) => {
    handleStatsUpdate(data);
  });
}

// 处理任务更新
function handleTaskUpdate(data) {
  // 添加到实时更新列表
  realTimeUpdates.value.unshift({
    ...data,
    time: new Date().toLocaleTimeString()
  });
  
  // 保持最近20条更新
  if (realTimeUpdates.value.length > 20) {
    realTimeUpdates.value.pop();
  }

  // 更新任务状态显示
  liveTaskUpdates.value.set(data.taskId, {
    name: data.taskName,
    status: data.status,
    progress: data.progress,
    message: data.message,
    timestamp: Date.now()
  });

  // 显示通知
  ElNotification({
    title: '任务更新',
    message: `${data.taskName}: ${data.message}`,
    type: getNotificationType(data.status),
    duration: 4000
  });
}

// 处理营销活动更新
function handleCampaignUpdate(data) {
  realTimeUpdates.value.unshift({
    ...data,
    time: new Date().toLocaleTimeString()
  });
  
  if (realTimeUpdates.value.length > 20) {
    realTimeUpdates.value.pop();
  }

  ElNotification({
    title: '营销活动更新',
    message: `${data.campaignName}: ${data.message}`,
    type: getNotificationType(data.status),
    duration: 4000
  });
}

// 处理统计数据更新
function handleStatsUpdate(data) {
  if (data.data.campaigns) {
    stats.value.runningTasks = data.data.tasks.running || stats.value.runningTasks;
  }
}

// 根据状态获取通知类型
function getNotificationType(status) {
  switch (status) {
    case 'completed':
      return 'success';
    case 'failed':
      return 'error';
    case 'running':
      return 'info';
    default:
      return 'info';
  }
}

// 获取标签类型
function getTagType(status) {
  switch (status) {
    case 'completed':
      return 'success';
    case 'failed':
      return 'danger';
    case 'running':
      return 'warning';
    case 'pending':
      return 'info';
    case null:
    case undefined:
      return 'info';
    default:
      return '';
  }
}

// 获取状态文本
function getStatusText(status) {
  switch (status) {
    case 'completed':
      return '完成';
    case 'failed':
      return '失败';
    case 'running':
      return '运行中';
    case 'pending':
      return '等待中';
    case null:
    case undefined:
      return '准备中';
    default:
      return status || '未知状态';
  }
}

// 获取进度条颜色
function getProgressColor(status) {
  switch (status) {
    case 'completed':
      return '#67C23A';
    case 'failed':
      return '#F56C6C';
    case 'running':
      return '#E6A23C';
    default:
      return '#409EFF';
  }
}

// 测试邮件任务
async function testEmailCampaign() {
  try {
    await fetch('/api/monitor/simulate/email-campaign', { method: 'POST' });
    ElMessage.success('邮件营销任务模拟已启动');
  } catch (error) {
    ElMessage.error('启动模拟任务失败');
  }
}

// 测试社交媒体任务
async function testSocialMedia() {
  try {
    await fetch('/api/monitor/simulate/social-media', { method: 'POST' });
    ElMessage.success('社交媒体任务模拟已启动');
  } catch (error) {
    ElMessage.error('启动模拟任务失败');
  }
}

function viewTask(row) {
  currentTask.value = {
    ...row,
    description: `这是一个${getTaskTypeName(row.type)}任务`,
    logs: [
      { time: "10:30:00", message: "任务开始执行" },
      { time: "10:35:00", message: "数据处理中..." },
      { time: "10:40:00", message: "执行进度 50%" },
      { time: "10:45:00", message: row.status === 'completed' ? "任务完成" : "当前状态" }
    ]
  };
  drawerVisible.value = true;
}

function getTaskTypeName(type) {
  const typeMap = {
    import: "数据导入",
    email: "邮件营销",
    whatsapp: "WhatsApp营销",
    linkedin: "LinkedIn营销",
    crawl: "数据爬取"
  };
  return typeMap[type] || type;
}

function getTaskTypeIcon(type) {
  const iconMap = {
    import: "📥",
    email: "📧",
    whatsapp: "💬",
    linkedin: "💼",
    crawl: "🔍"
  };
  return iconMap[type] || "⚙️";
}

function getDataTypeName(dataType) {
  const typeMap = {
    company: "🏢 公司信息",
    contact: "👤 联系人信息",
    email: "📧 邮箱地址",
    phone: "📱 电话号码",
    product: "🛍️ 产品信息",
    news: "📰 新闻资讯",
    price: "💰 价格信息",
    other: "📊 其他数据"
  };
  return typeMap[dataType] || dataType;
}

function getFrequencyName(frequency) {
  const frequencyMap = {
    once: "仅执行一次",
    daily: "每日执行",
    weekly: "每周执行",
    monthly: "每月执行"
  };
  return frequencyMap[frequency] || frequency;
}


async function retryTask(task) {
  try {
    // 调用API重启任务
    await retryTaskAPI(task.id);
    
    // 更新本地状态
    task.status = 'running';
    task.progress = 0;
    task.estimatedTime = '重新执行中';
    
    ElMessage.success(`重新启动任务：${task.name}`);
  } catch (error) {
    console.error('重启任务失败:', error);
    
    // API调用失败时，仍然更新本地状态（用于开发阶段）
    task.status = 'running';
    task.progress = 0;
    task.estimatedTime = '重新执行中';
    
    ElMessage.success(`重新启动任务：${task.name}（本地模式）`);
  }
}

function showCreateTaskDialog() {
  createTaskVisible.value = true;
}

function refreshData() {
  // 模拟刷新数据
  ElMessage.success('数据已刷新');
  
  // 这里可以重新获取数据
  // 例如：重新调用 onMounted 中的 API 请求
}

function getDataTypeColor(dataType) {
  const colorMap = {
    company: 'primary',
    contact: 'success',
    email: 'warning',
    phone: 'info',
    product: 'danger',
    news: '',
    price: 'warning'
  };
  return colorMap[dataType] || '';
}

function getCrawlSamples(task) {
  // 模拟采集到的数据样本
  const sampleData = {
    company: [
      {
        name: 'OpenAI',
        description: 'AI research and deployment company',
        foundedYear: '2015',
        industry: 'Artificial Intelligence',
        employees: '500-1000',
        location: 'San Francisco, CA'
      },
      {
        name: 'Microsoft',
        description: 'Technology corporation',
        foundedYear: '1975',
        industry: 'Software',
        employees: '200000+',
        location: 'Redmond, WA'
      }
    ],
    contact: [
      {
        name: '张三',
        title: '产品经理',
        company: '科技公司',
        email: 'zhangsan@example.com',
        phone: '+86 138-0000-0000',
        location: '北京'
      },
      {
        name: 'John Smith',
        title: 'Software Engineer',
        company: 'Tech Corp',
        email: 'john@example.com',
        phone: '+1 555-0123',
        location: 'San Francisco'
      }
    ],
    email: [
      { emails: ['contact@example.com', 'sales@example.com'] },
      { emails: ['info@company.com', 'support@company.com'] }
    ],
    product: [
      {
        name: 'iPhone 15 Pro',
        price: '¥8,999',
        category: '智能手机',
        description: '专业级拍摄系统'
      }
    ]
  };
  
  return sampleData[task.crawlConfig?.dataType] || sampleData.company;
}

function viewAllCrawlData(task) {
  // 跳转到数据采集中心查看全部数据
  ElMessage.info('正在跳转到数据采集中心...');
  // 这里可以使用 router.push('/customer/data-import') 跳转
}

function exportCrawlData(task) {
  ElMessage.success(`正在导出 ${task.name} 的采集数据...`);
}

function addCrawlDataToCustomers(task) {
  ElMessage.success(`正在将 ${task.name} 的数据添加到客户库...`);
}

async function createTask() {
  if (!newTask.value.name || !newTask.value.type) {
    ElMessage.warning('请填写任务名称和类型');
    return;
  }
  
  // 爬虫任务额外验证
  if (newTask.value.type === 'crawl') {
    if (!newTask.value.crawlConfig.targetUrl || !newTask.value.crawlConfig.dataType) {
      ElMessage.warning('请填写目标网址和数据类型');
      return;
    }
    
    // 验证URL格式
    try {
      new URL(newTask.value.crawlConfig.targetUrl);
    } catch {
      ElMessage.warning('请输入有效的网址格式');
      return;
    }
  }
  
  try {
    // 准备API数据
    const taskData = {
      name: newTask.value.name,
      type: newTask.value.type,
      description: newTask.value.description,
      // 如果是爬虫任务，包含爬虫配置
      ...(newTask.value.type === 'crawl' && { 
        crawlConfig: { 
          targetUrl: newTask.value.crawlConfig.targetUrl,
          dataType: newTask.value.crawlConfig.dataType,
          selectors: newTask.value.crawlConfig.selectors,
          frequency: newTask.value.crawlConfig.frequency
        } 
      })
    };

    // 调用API创建任务
    const response = await createTaskAPI(taskData);
    
    // 如果API调用成功，从响应中获取任务数据
    const createdTask = response.data || {
      id: Date.now(),
      ...taskData,
      status: 'pending',
      progress: 0,
      createdAt: new Date().toISOString(),
      estimatedTime: '待执行'
    };
    
    // 添加到任务列表
    tasks.value.unshift(createdTask);
    ElMessage.success(`任务"${createdTask.name}"创建成功`);
    
  } catch (error) {
    console.error('创建任务失败:', error);
    
    // API调用失败时，仍然创建本地任务（用于开发阶段）
    const localTask = {
      id: Date.now(),
      name: newTask.value.name,
      type: newTask.value.type,
      description: newTask.value.description,
      status: 'pending',
      progress: 0,
      createdAt: new Date().toISOString(),
      estimatedTime: '待执行',
      // 如果是爬虫任务，保存爬虫配置
      ...(newTask.value.type === 'crawl' && { crawlConfig: { ...newTask.value.crawlConfig } })
    };
    
    tasks.value.unshift(localTask);
    ElMessage.success(`任务"${localTask.name}"创建成功（本地模式）`);
  }
  
  // 重置表单
  newTask.value = {
    name: '',
    type: '',
    description: '',
    crawlConfig: {
      targetUrl: '',
      dataType: '',
      selectors: '',
      frequency: 'once'
    }
  };
  createTaskVisible.value = false;
}
</script>

<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-title">
        <span class="page-icon">📊</span>
        营销数据概览
      </div>
      <div class="page-subtitle">
        实时监控营销活动表现，掌握客户互动数据和任务执行状态
      </div>
      <div class="page-actions">
        <!-- WebSocket连接状态指示器 -->
        <div class="ws-status-indicator" :class="{ 'connected': wsConnected }">
          <el-icon><Connection /></el-icon>
          <span>{{ wsConnected ? '实时监控已连接' : '连接中...' }}</span>
        </div>
        
        <el-button type="primary" class="btn-primary" @click="showCreateTaskDialog">
          <el-icon><Plus /></el-icon>
          创建新任务
        </el-button>
        <el-button class="btn-secondary" @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card fade-in">
        <div class="stat-header">
          <div class="stat-icon" style="background: linear-gradient(135deg, #dbeafe, #93c5fd); color: #1e40af;">
            👥
          </div>
          <div class="stat-trend positive">
            +12%
          </div>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ (stats.totalCustomers || 0).toLocaleString() }}</div>
          <div class="stat-label">总客户数</div>
        </div>
      </div>

      <div class="stat-card fade-in" style="animation-delay: 0.1s;">
        <div class="stat-header">
          <div class="stat-icon" style="background: linear-gradient(135deg, #d1fae5, #86efac); color: #059669;">
            📧
          </div>
          <div class="stat-trend positive">
            +8%
          </div>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ (stats.emailsSent || 0).toLocaleString() }}</div>
          <div class="stat-label">今日邮件发送</div>
        </div>
      </div>

      <div class="stat-card fade-in" style="animation-delay: 0.2s;">
        <div class="stat-header">
          <div class="stat-icon" style="background: linear-gradient(135deg, #fef3c7, #fcd34d); color: #d97706;">
            📊
          </div>
          <div class="stat-trend positive">
            +15%
          </div>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ (stats.openRate || 0) }}%</div>
          <div class="stat-label">邮件打开率</div>
        </div>
      </div>

      <div class="stat-card fade-in" style="animation-delay: 0.3s;">
        <div class="stat-header">
          <div class="stat-icon" style="background: linear-gradient(135deg, #ede9fe, #c4b5fd); color: #7c3aed;">
            ⚙️
          </div>
          <div class="stat-trend neutral">
            {{ (stats.runningTasks || 0) }}个
          </div>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ (stats.runningTasks || 0) }}</div>
          <div class="stat-label">运行中任务</div>
        </div>
      </div>
    </div>

    <!-- 实时监控面板 -->
    <div class="data-table-wrapper" v-if="wsConnected">
      <div class="table-header">
        <div class="table-title">🔄 实时监控</div>
        <div class="table-subtitle">营销任务和活动的实时执行状态更新</div>
        <div class="table-actions">
          <el-button size="small" @click="testEmailCampaign">模拟邮件任务</el-button>
          <el-button size="small" @click="testSocialMedia">模拟社交媒体</el-button>
        </div>
      </div>
      
      <div class="realtime-panel">
        <!-- 实时任务状态 -->
        <div class="live-tasks" v-if="liveTaskUpdates.size > 0">
          <h3>当前活跃任务</h3>
          <div class="live-task-grid">
            <div 
              v-for="[taskId, task] in liveTaskUpdates" 
              :key="taskId"
              class="live-task-card"
              :class="`status-${task.status}`"
            >
              <div class="task-name">{{ task.name }}</div>
              <div class="task-status-info">
                <el-tag :type="getTagType(task.status)">{{ getStatusText(task.status) }}</el-tag>
                <div class="task-progress">
                  <el-progress 
                    :percentage="task.progress" 
                    :show-text="false" 
                    :stroke-width="4"
                    :color="getProgressColor(task.status)"
                  />
                  <span class="progress-text">{{ task.progress }}%</span>
                </div>
              </div>
              <div class="task-message">{{ task.message }}</div>
            </div>
          </div>
        </div>

        <!-- 实时更新日志 -->
        <div class="realtime-logs">
          <h3>实时更新日志</h3>
          <div class="logs-container">
            <div 
              v-for="update in realTimeUpdates" 
              :key="update.timestamp" 
              class="log-entry"
              :class="`log-${update.type}`"
            >
              <div class="log-time">{{ update.time }}</div>
              <div class="log-content">
                <div class="log-title">
                  <el-icon v-if="update.type === 'task_update'">⚙️</el-icon>
                  <el-icon v-else-if="update.type === 'campaign_update'">📢</el-icon>
                  <span>{{ update.taskName || update.campaignName }}</span>
                </div>
                <div class="log-message">{{ update.message }}</div>
              </div>
              <div class="log-status">
                <el-tag size="small" :type="getTagType(update.status)">
                  {{ getStatusText(update.status) }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="data-table-wrapper">
      <div class="table-header">
        <div class="table-title">📈 数据趋势分析</div>
        <div class="table-subtitle">营销活动数据趋势，帮助您了解业务发展状况</div>
      </div>
      <el-tabs v-model="chartTab" class="enhanced-tabs">
        <el-tab-pane label="📧 邮件发送量" name="send">
          <div style="padding: var(--spacing-6);">
            <LineChart :data="sendTrend" :loading="false" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="👥 客户增长" name="customer">
          <div style="padding: var(--spacing-6);">
            <LineChart :data="customerTrend" :loading="false" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 任务列表 -->
    <div class="data-table-wrapper">
      <div class="table-header">
        <div class="table-title">⚙️ 任务调度中心</div>
        <div class="table-subtitle">实时监控营销任务执行状态，确保营销活动顺利进行</div>
        <div class="table-actions">
          <el-button type="primary" class="btn-primary" size="small" @click="showCreateTaskDialog">
            <el-icon><Plus /></el-icon>
            新建任务
          </el-button>
        </div>
      </div>
      
      <div class="task-grid">
        <div 
          v-for="task in tasks" 
          :key="task.id"
          class="task-card"
          :class="`task-${task.status}`"
          @click="viewTask(task)"
        >
          <div class="task-header">
            <div class="task-icon">{{ getTaskTypeIcon(task.type) }}</div>
            <div class="task-status">
              <el-tag 
                :type="task.status === 'completed' ? 'success' : 
                       task.status === 'running' ? 'warning' : 
                       task.status === 'failed' ? 'danger' : 'info'"
                size="small"
                effect="plain"
              >
                {{ getStatusText(task.status) }}
              </el-tag>
            </div>
          </div>
          
          <div class="task-content">
            <h4 class="task-name" :title="task.name">{{ task.name }}</h4>
            <p class="task-time">{{ task.createdAt }}</p>
            
            <div class="task-progress">
              <el-progress 
                :percentage="task.progress" 
                :color="task.status === 'failed' ? '#F56C6C' : '#409EFF'"
                :status="task.status === 'completed' ? 'success' : task.status === 'failed' ? 'exception' : ''"
                :stroke-width="6"
              />
              <span class="progress-text">{{ task.estimatedTime }}</span>
            </div>
          </div>
          
          <div class="task-actions" @click.stop>
            <el-button v-if="task.status === 'failed'" type="text" size="small" @click="retryTask(task)">
              <el-icon><Refresh /></el-icon>
              重试
            </el-button>
            <el-button type="text" size="small" @click="viewTask(task)">
              <el-icon><View /></el-icon>
              详情
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 任务详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="任务详情" size="40%">
      <div class="task-detail">
        <div class="detail-header">
          <div class="task-info">
            <div class="task-icon-large">{{ getTaskTypeIcon(currentTask.type) }}</div>
            <div class="task-meta">
              <h3>{{ currentTask.name }}</h3>
              <p class="task-type">{{ getTaskTypeName(currentTask.type) }}</p>
              <el-tag 
                :type="currentTask.status === 'completed' ? 'success' : 
                       currentTask.status === 'running' ? 'warning' : 
                       currentTask.status === 'failed' ? 'danger' : 'info'"
              >
                {{ getStatusText(currentTask.status) }}
              </el-tag>
            </div>
          </div>
          
          <div class="progress-circle">
            <el-progress 
              type="circle" 
              :percentage="currentTask.progress" 
              :color="currentTask.status === 'failed' ? '#F56C6C' : '#409EFF'"
              :status="currentTask.status === 'completed' ? 'success' : currentTask.status === 'failed' ? 'exception' : ''"
            />
          </div>
        </div>
        
        <el-divider />
        
        <div class="task-description">
          <h4>任务描述</h4>
          <p>{{ currentTask.description }}</p>
        </div>

        <!-- 爬虫配置详情 -->
        <div v-if="currentTask.type === 'crawl' && currentTask.crawlConfig" class="crawl-config">
          <h4>🕷️ 爬虫配置</h4>
          <div class="config-grid">
            <div class="config-item">
              <span class="config-label">目标网址:</span>
              <a :href="currentTask.crawlConfig.targetUrl" target="_blank" class="config-value link">
                {{ currentTask.crawlConfig.targetUrl }}
              </a>
            </div>
            <div class="config-item">
              <span class="config-label">数据类型:</span>
              <el-tag size="small" type="info">{{ getDataTypeName(currentTask.crawlConfig.dataType) }}</el-tag>
            </div>
            <div class="config-item" v-if="currentTask.crawlConfig.selectors">
              <span class="config-label">CSS选择器:</span>
              <code class="config-value">{{ currentTask.crawlConfig.selectors }}</code>
            </div>
            <div class="config-item">
              <span class="config-label">执行频率:</span>
              <span class="config-value">{{ getFrequencyName(currentTask.crawlConfig.frequency) }}</span>
            </div>
          </div>
          
          <!-- 采集数据预览 -->
          <div v-if="currentTask.status === 'completed'" class="crawl-data-preview">
            <h4>📊 采集数据预览</h4>
            <div class="data-summary">
              <el-tag type="success" size="small">✅ 成功采集 {{ currentTask.crawlResults?.totalRecords || 15 }} 条数据</el-tag>
              <el-button type="text" size="small" @click="viewAllCrawlData(currentTask)">
                <el-icon><View /></el-icon>
                查看全部数据
              </el-button>
            </div>
            
            <div class="sample-data">
              <div class="sample-item" v-for="(sample, index) in getCrawlSamples(currentTask)" :key="index">
                <div class="sample-header">
                  <span class="sample-index">#{{ index + 1 }}</span>
                  <el-tag size="small" :type="getDataTypeColor(currentTask.crawlConfig.dataType)">
                    {{ getDataTypeName(currentTask.crawlConfig.dataType) }}
                  </el-tag>
                </div>
                <div class="sample-content">
                  <pre>{{ JSON.stringify(sample, null, 2) }}</pre>
                </div>
              </div>
            </div>
            
            <div class="data-actions">
              <el-button type="primary" size="small" @click="exportCrawlData(currentTask)">
                <el-icon><Download /></el-icon>
                导出数据
              </el-button>
              <el-button size="small" @click="addCrawlDataToCustomers(currentTask)">
                <el-icon><Plus /></el-icon>
                添加到客户库
              </el-button>
            </div>
          </div>
        </div>
        
        <div class="task-timeline">
          <h4>执行日志</h4>
          <el-timeline>
            <el-timeline-item 
              v-for="log in currentTask.logs" 
              :key="log.time"
              :timestamp="log.time"
            >
              {{ log.message }}
            </el-timeline-item>
          </el-timeline>
        </div>
        
        <div class="task-steps">
          <h4>执行步骤</h4>
          <el-steps 
            :active="currentTask.progress === 100 ? 3 : currentTask.progress > 0 ? 2 : 1"
            :process-status="currentTask.status === 'failed' ? 'error' : 'process'"
            finish-status="success"
            align-center
          >
            <el-step title="任务创建" description="初始化任务参数" />
            <el-step title="执行中" description="正在处理数据" />
            <el-step title="完成" description="任务执行完毕" />
          </el-steps>
        </div>
      </div>
    </el-drawer>

    <!-- 创建任务对话框 -->
    <el-dialog v-model="createTaskVisible" title="创建新任务" width="500px">
      <el-form :model="newTask" label-width="80px">
        <el-form-item label="任务名称" required>
          <el-input v-model="newTask.name" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="任务类型" required>
          <el-select v-model="newTask.type" placeholder="请选择任务类型" style="width: 100%">
            <el-option label="数据导入" value="import" />
            <el-option label="邮件营销" value="email" />
            <el-option label="WhatsApp营销" value="whatsapp" />
            <el-option label="LinkedIn营销" value="linkedin" />
            <el-option label="数据爬取" value="crawl" />
          </el-select>
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input 
            v-model="newTask.description" 
            type="textarea" 
            rows="3"
            placeholder="请输入任务描述（可选）"
          />
        </el-form-item>

        <!-- 爬虫配置 - 只有选择数据爬取时显示 -->
        <template v-if="newTask.type === 'crawl'">
          <el-divider content-position="left">
            <span style="color: #409eff; font-weight: 600;">🕷️ 爬虫配置</span>
          </el-divider>
          
          <el-form-item label="目标网址" required>
            <el-input 
              v-model="newTask.crawlConfig.targetUrl" 
              placeholder="请输入要爬取的网站URL，如：https://example.com"
            />
          </el-form-item>
          
          <el-form-item label="数据类型" required>
            <el-select v-model="newTask.crawlConfig.dataType" placeholder="选择要爬取的数据类型" style="width: 100%">
              <el-option label="🏢 公司信息" value="company" />
              <el-option label="👤 联系人信息" value="contact" />
              <el-option label="📧 邮箱地址" value="email" />
              <el-option label="📱 电话号码" value="phone" />
              <el-option label="🛍️ 产品信息" value="product" />
              <el-option label="📰 新闻资讯" value="news" />
              <el-option label="💰 价格信息" value="price" />
              <el-option label="📊 其他数据" value="other" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="CSS选择器">
            <el-input 
              v-model="newTask.crawlConfig.selectors" 
              placeholder="CSS选择器，如：.product-name, .price, .contact-info"
            />
            <div style="font-size: 12px; color: #909399; margin-top: 4px;">
              💡 提示：用逗号分隔多个选择器，留空则自动识别
            </div>
          </el-form-item>
          
          <el-form-item label="执行频率">
            <el-radio-group v-model="newTask.crawlConfig.frequency">
              <el-radio value="once">仅执行一次</el-radio>
              <el-radio value="daily">每日执行</el-radio>
              <el-radio value="weekly">每周执行</el-radio>
              <el-radio value="monthly">每月执行</el-radio>
            </el-radio-group>
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="createTaskVisible = false">取消</el-button>
        <el-button type="primary" @click="createTask">创建任务</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
/* 使用全局设计系统，仅保留必要的组件特定样式 */

.task-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--spacing-5);
  margin-top: var(--spacing-4);
  padding: var(--spacing-6);
}

.task-card {
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-5);
  background: var(--bg-primary);
  cursor: pointer;
  transition: all var(--transition-duration) var(--transition-timing);
  position: relative;
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.task-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
  border-color: var(--primary-color);
}

.task-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--border-primary);
  transition: all var(--transition-duration) var(--transition-timing);
}

.task-card.task-running::before {
  background: var(--warning-color);
}

.task-card.task-completed::before {
  background: var(--success-color);
}

.task-card.task-failed::before {
  background: var(--danger-color);
}

.task-card.task-pending::before {
  background: var(--info-color);
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-4);
}

.task-icon {
  font-size: var(--text-xl);
  width: 48px;
  height: 48px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-tertiary);
}

.task-content {
  margin-bottom: var(--spacing-4);
}

.task-name {
  margin: 0 0 var(--spacing-2) 0;
  font-size: var(--text-base);
  font-weight: 600;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.task-time {
  margin: 0 0 var(--spacing-3) 0;
  font-size: var(--text-xs);
  color: var(--text-secondary);
}

.task-progress {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
  margin-bottom: var(--spacing-3);
}

.progress-text {
  font-size: var(--text-xs);
  color: var(--text-secondary);
  white-space: nowrap;
}

.task-actions {
  display: flex;
  gap: var(--spacing-2);
  justify-content: flex-end;
}

/* 任务详情样式 */
.task-detail {
  padding: var(--spacing-5);
}

.detail-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: var(--spacing-6);
}

.task-info {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-4);
  flex: 1;
}

.task-icon-large {
  font-size: var(--text-4xl);
  width: 80px;
  height: 80px;
  border-radius: var(--radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-tertiary);
}

.task-meta h3 {
  margin: 0 0 var(--spacing-2) 0;
  color: var(--text-primary);
  font-size: var(--text-xl);
  font-weight: 600;
}

.task-type {
  margin: 0 0 var(--spacing-3) 0;
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.progress-circle {
  flex-shrink: 0;
}

.task-description h4,
.task-timeline h4,
.task-steps h4 {
  margin: 0 0 var(--spacing-3) 0;
  color: var(--text-primary);
  font-size: var(--text-base);
  font-weight: 600;
}

.task-description p {
  margin: 0 0 var(--spacing-6) 0;
  color: var(--text-secondary);
  line-height: 1.6;
}

.task-timeline {
  margin-bottom: var(--spacing-8);
}

/* 爬虫配置样式 */
.crawl-config {
  margin-bottom: var(--spacing-6);
}

.crawl-config h4 {
  margin: 0 0 var(--spacing-3) 0;
  color: var(--text-primary);
  font-size: var(--text-base);
  font-weight: 600;
}

.config-grid {
  display: grid;
  gap: var(--spacing-3);
}

.config-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  padding: var(--spacing-2) 0;
  border-bottom: 1px solid var(--border-extra-light);
}

.config-item:last-child {
  border-bottom: none;
}

.config-label {
  font-weight: 500;
  color: var(--text-secondary);
  min-width: 80px;
  font-size: var(--text-sm);
}

.config-value {
  color: var(--text-primary);
  font-size: var(--text-sm);
}

.config-value.link {
  color: var(--primary-color);
  text-decoration: none;
  word-break: break-all;
}

.config-value.link:hover {
  text-decoration: underline;
}

.config-value code {
  background: var(--bg-tertiary);
  padding: var(--spacing-1) var(--spacing-2);
  border-radius: var(--radius-base);
  font-family: 'Monaco', 'Consolas', monospace;
  font-size: 12px;
}

/* 爬虫数据预览样式 */
.crawl-data-preview {
  margin-top: var(--spacing-6);
  padding-top: var(--spacing-5);
  border-top: 1px solid var(--border-extra-light);
}

.crawl-data-preview h4 {
  margin: 0 0 var(--spacing-3) 0;
  color: var(--text-primary);
  font-size: var(--text-base);
  font-weight: 600;
}

.data-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-4);
  padding: var(--spacing-3);
  background: var(--bg-tertiary);
  border-radius: var(--radius-base);
  border: 1px solid var(--border-extra-light);
}

.sample-data {
  max-height: 300px;
  overflow-y: auto;
  margin-bottom: var(--spacing-4);
}

.sample-item {
  margin-bottom: var(--spacing-3);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-base);
  overflow: hidden;
}

.sample-item:last-child {
  margin-bottom: 0;
}

.sample-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-2) var(--spacing-3);
  background: var(--bg-tertiary);
  border-bottom: 1px solid var(--border-extra-light);
}

.sample-index {
  font-size: var(--text-xs);
  color: var(--text-secondary);
  font-weight: 600;
}

.sample-content {
  padding: var(--spacing-3);
  background: var(--bg-primary);
}

.sample-content pre {
  margin: 0;
  font-family: 'Monaco', 'Consolas', monospace;
  font-size: 11px;
  line-height: 1.4;
  white-space: pre-wrap;
  word-break: break-word;
  color: var(--text-primary);
}

.data-actions {
  display: flex;
  gap: var(--spacing-2);
  justify-content: flex-start;
}

/* WebSocket连接状态指示器 */
.ws-status-indicator {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  padding: var(--spacing-2) var(--spacing-3);
  background: var(--bg-secondary);
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  font-size: 13px;
  color: #6b7280;
  margin-right: var(--spacing-3);
}

.ws-status-indicator.connected {
  background: #f0f9ff;
  border-color: #3b82f6;
  color: #1d4ed8;
}

.ws-status-indicator.connected .el-icon {
  color: #10b981;
}

/* 实时监控面板 */
.realtime-panel {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-6);
  padding: var(--spacing-6);
}

.live-tasks h3,
.realtime-logs h3 {
  margin: 0 0 var(--spacing-4) 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

/* 活跃任务网格 */
.live-task-grid {
  display: grid;
  gap: var(--spacing-3);
}

.live-task-card {
  padding: var(--spacing-4);
  background: var(--bg-primary);
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  transition: all 0.2s;
}

.live-task-card.status-running {
  border-color: #f59e0b;
  background: #fffbeb;
}

.live-task-card.status-completed {
  border-color: #10b981;
  background: #f0fff4;
}

.live-task-card.status-failed {
  border-color: #ef4444;
  background: #fef2f2;
}

.task-name {
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-2);
}

.task-status-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-2);
}

.task-progress {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  flex: 1;
  margin-left: var(--spacing-3);
}

.progress-text {
  font-size: 12px;
  color: var(--text-secondary);
  min-width: 35px;
}

.task-message {
  font-size: 13px;
  color: var(--text-secondary);
}

/* 实时日志 */
.logs-container {
  max-height: 300px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2);
}

.log-entry {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-3);
  padding: var(--spacing-3);
  background: var(--bg-primary);
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  transition: all 0.2s;
}

.log-entry:hover {
  border-color: #d1d5db;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.log-time {
  font-size: 12px;
  color: var(--text-secondary);
  white-space: nowrap;
  min-width: 60px;
}

.log-content {
  flex: 1;
}

.log-title {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: var(--spacing-1);
}

.log-message {
  font-size: 13px;
  color: var(--text-secondary);
}

.log-status {
  align-self: flex-start;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .task-grid {
    grid-template-columns: 1fr;
  }
  
  .detail-header {
    flex-direction: column;
    gap: var(--spacing-5);
  }
  
  .progress-circle {
    align-self: center;
  }
  
  .config-item {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-1);
  }
  
  .config-label {
    min-width: auto;
  }

  .realtime-panel {
    grid-template-columns: 1fr;
    gap: var(--spacing-4);
  }

  .ws-status-indicator {
    display: none; /* 在移动端隐藏 */
  }

  .page-actions {
    flex-direction: column;
    align-items: stretch;
    gap: var(--spacing-2);
  }
}
</style>
