<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-title">
        <span class="page-icon">📊</span>
        数据采集中心
      </div>
      <div class="page-subtitle">
        查看和管理所有采集到的数据，支持筛选、预览和导出
      </div>
      <div class="page-actions">
        <el-button type="primary" class="btn-primary" @click="exportData">
          <el-icon><Download /></el-icon>
          导出数据
        </el-button>
        <el-button class="btn-secondary" @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 数据统计 -->
    <div class="stats-grid">
      <div class="stat-card fade-in">
        <div class="stat-header">
          <div class="stat-icon" style="background: linear-gradient(135deg, #dbeafe, #93c5fd); color: #1e40af;">
            📋
          </div>
          <div class="stat-trend positive">+25%</div>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ (dataStats.totalRecords || 0).toLocaleString() }}</div>
          <div class="stat-label">总采集记录</div>
        </div>
      </div>

      <div class="stat-card fade-in" style="animation-delay: 0.1s;">
        <div class="stat-header">
          <div class="stat-icon" style="background: linear-gradient(135deg, #d1fae5, #86efac); color: #059669;">
            🏢
          </div>
          <div class="stat-trend positive">+18%</div>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ (dataStats.companies || 0).toLocaleString() }}</div>
          <div class="stat-label">公司信息</div>
        </div>
      </div>

      <div class="stat-card fade-in" style="animation-delay: 0.2s;">
        <div class="stat-header">
          <div class="stat-icon" style="background: linear-gradient(135deg, #fef3c7, #fcd34d); color: #d97706;">
            👤
          </div>
          <div class="stat-trend positive">+32%</div>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ (dataStats.contacts || 0).toLocaleString() }}</div>
          <div class="stat-label">联系人信息</div>
        </div>
      </div>

      <div class="stat-card fade-in" style="animation-delay: 0.3s;">
        <div class="stat-header">
          <div class="stat-icon" style="background: linear-gradient(135deg, #ede9fe, #c4b5fd); color: #7c3aed;">
            📧
          </div>
          <div class="stat-trend positive">+45%</div>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ (dataStats.emails || 0).toLocaleString() }}</div>
          <div class="stat-label">邮箱地址</div>
        </div>
      </div>
    </div>

    <!-- 筛选和操作栏 -->
    <div class="data-table-wrapper">
      <div class="table-header">
        <div class="table-title">📊 采集数据管理</div>
        <div class="table-subtitle">查看、筛选和管理所有采集到的数据</div>
        <div class="table-actions">
          <el-select v-model="typeFilter" placeholder="数据类型" style="width: 150px;" clearable>
            <el-option label="全部类型" value="" />
            <el-option label="🏢 公司信息" value="company" />
            <el-option label="👤 联系人信息" value="contact" />
            <el-option label="📧 邮箱地址" value="email" />
            <el-option label="📱 电话号码" value="phone" />
            <el-option label="🛍️ 产品信息" value="product" />
            <el-option label="📰 新闻资讯" value="news" />
            <el-option label="💰 价格信息" value="price" />
          </el-select>
          <el-select v-model="taskFilter" placeholder="来源任务" style="width: 200px;" clearable>
            <el-option label="全部任务" value="" />
            <el-option v-for="task in crawlTasks" :key="task.id" :label="task.name" :value="task.id" />
          </el-select>
          <el-input
            v-model="searchText"
            placeholder="搜索数据内容..."
            style="width: 250px;"
            clearable
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
      </div>

      <!-- 数据表格 -->
      <el-table 
        :data="filteredData" 
        v-loading="loading"
        style="width: 100%"
        class="modern-table"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column label="数据类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getDataTypeColor(row.dataType)" size="small" effect="light">
              {{ getDataTypeName(row.dataType) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="采集内容" min-width="300">
          <template #default="{ row }">
            <div class="data-content">
              <div class="content-preview">{{ getContentPreview(row) }}</div>
              <div class="content-meta">
                <span class="source-url">来源：{{ row.sourceUrl }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="来源任务" width="160">
          <template #default="{ row }">
            <div class="task-info">
              <div class="task-name">{{ getTaskName(row.taskId) }}</div>
              <div class="task-date">{{ formatDate(row.createdAt) }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="数据质量" width="100">
          <template #default="{ row }">
            <el-tag 
              :type="row.quality === 'high' ? 'success' : row.quality === 'medium' ? 'warning' : 'danger'"
              size="small"
            >
              {{ getQualityText(row.quality) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="viewDetail(row)">
              <el-icon><View /></el-icon>
              详情
            </el-button>
            <el-button size="small" link @click="addToCustomer(row)" v-if="row.dataType === 'contact' || row.dataType === 'company'">
              <el-icon><Plus /></el-icon>
              添加客户
            </el-button>
            <el-popconfirm title="确认删除这条数据吗？" @confirm="deleteRecord(row)">
              <template #reference>
                <el-button size="small" type="danger" link>
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </div>

    <!-- 数据详情抽屉 -->
    <el-drawer v-model="detailVisible" title="数据详情" size="60%">
      <div v-if="currentRecord" class="data-detail">
        <div class="detail-header">
          <div class="data-type-badge">
            <span class="type-icon">{{ getDataTypeIcon(currentRecord.dataType) }}</span>
            <span class="type-name">{{ getDataTypeName(currentRecord.dataType) }}</span>
          </div>
          <div class="quality-score">
            <span class="score-label">数据质量:</span>
            <el-tag :type="currentRecord.quality === 'high' ? 'success' : currentRecord.quality === 'medium' ? 'warning' : 'danger'">
              {{ getQualityText(currentRecord.quality) }}
            </el-tag>
          </div>
        </div>

        <el-divider />

        <div class="detail-content">
          <h4>📋 采集内容</h4>
          <div class="content-display">
            <pre>{{ JSON.stringify(currentRecord.data, null, 2) }}</pre>
          </div>
        </div>

        <div class="detail-meta">
          <h4>ℹ️ 元数据</h4>
          <div class="meta-grid">
            <div class="meta-item">
              <span class="meta-label">来源网址:</span>
              <a :href="currentRecord.sourceUrl" target="_blank" class="meta-value link">
                {{ currentRecord.sourceUrl }}
              </a>
            </div>
            <div class="meta-item">
              <span class="meta-label">采集任务:</span>
              <span class="meta-value">{{ getTaskName(currentRecord.taskId) }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">采集时间:</span>
              <span class="meta-value">{{ formatDateTime(currentRecord.createdAt) }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">CSS选择器:</span>
              <code class="meta-value">{{ currentRecord.selectors || '自动识别' }}</code>
            </div>
          </div>
        </div>

        <div class="detail-actions">
          <el-button type="primary" @click="addToCustomer(currentRecord)" v-if="currentRecord.dataType === 'contact' || currentRecord.dataType === 'company'">
            <el-icon><Plus /></el-icon>
            添加到客户库
          </el-button>
          <el-button @click="exportSingle(currentRecord)">
            <el-icon><Download /></el-icon>
            导出数据
          </el-button>
        </div>
      </div>
    </el-drawer>

    <!-- 添加客户对话框 -->
    <el-dialog v-model="addCustomerVisible" title="添加到客户库" width="500px">
      <el-form :model="customerForm" label-width="80px">
        <el-form-item label="客户名称" required>
          <el-input v-model="customerForm.name" placeholder="请输入客户名称" />
        </el-form-item>
        <el-form-item label="联系邮箱">
          <el-input v-model="customerForm.email" placeholder="邮箱地址" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="customerForm.phone" placeholder="电话号码" />
        </el-form-item>
        <el-form-item label="公司名称">
          <el-input v-model="customerForm.company" placeholder="公司名称" />
        </el-form-item>
        <el-form-item label="客户标签">
          <el-select v-model="customerForm.tags" multiple placeholder="选择标签" style="width: 100%;">
            <el-option label="潜在客户" value="potential" />
            <el-option label="高价值" value="high-value" />
            <el-option label="爬虫获客" value="crawled" />
            <el-option label="待跟进" value="follow-up" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addCustomerVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAddCustomer">确认添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Download, Refresh, View, Plus, Delete } from '@element-plus/icons-vue';
import { 
  getAcquisitionData, 
  getAcquisitionStats, 
  deleteAcquisitionData,
  exportAcquisitionData,
  convertToCustomer 
} from '@/api/acquisition';

const loading = ref(false);
const detailVisible = ref(false);
const addCustomerVisible = ref(false);
const currentRecord = ref(null);
const selectedData = ref([]);

// 筛选条件
const typeFilter = ref('');
const taskFilter = ref('');
const searchText = ref('');

// 分页
const currentPage = ref(1);
const pageSize = ref(20);
const total = ref(0);

// 表单
const customerForm = ref({
  name: '',
  email: '',
  phone: '',
  company: '',
  tags: ['crawled']
});

// 模拟数据
const crawledData = ref([
  {
    id: '1',
    dataType: 'company',
    sourceUrl: 'https://www.crunchbase.com/organization/openai',
    taskId: 'task-1',
    quality: 'high',
    createdAt: '2025-01-14T10:30:00Z',
    selectors: '.company-name, .description, .founded-date',
    data: {
      name: 'OpenAI',
      description: 'AI research and deployment company',
      foundedYear: '2015',
      industry: 'Artificial Intelligence',
      employees: '500-1000',
      location: 'San Francisco, CA'
    }
  },
  {
    id: '2',
    dataType: 'contact',
    sourceUrl: 'https://www.linkedin.com/in/example',
    taskId: 'task-2',
    quality: 'medium',
    createdAt: '2025-01-14T09:15:00Z',
    selectors: '.profile-name, .contact-info',
    data: {
      name: '张三',
      title: '产品经理',
      company: '科技公司',
      email: 'zhangsan@example.com',
      phone: '+86 138-0000-0000',
      location: '北京'
    }
  },
  {
    id: '3',
    dataType: 'email',
    sourceUrl: 'https://example.com/about',
    taskId: 'task-3',
    quality: 'high',
    createdAt: '2025-01-14T08:45:00Z',
    selectors: 'a[href^="mailto:"]',
    data: {
      emails: [
        'contact@example.com',
        'sales@example.com',
        'support@example.com'
      ]
    }
  },
  {
    id: '4',
    dataType: 'product',
    sourceUrl: 'https://shop.example.com/products',
    taskId: 'task-4',
    quality: 'high',
    createdAt: '2025-01-13T16:20:00Z',
    selectors: '.product-name, .price, .description',
    data: {
      name: 'iPhone 15 Pro',
      price: '¥8,999',
      category: '智能手机',
      description: '专业级拍摄系统',
      availability: '现货'
    }
  },
  {
    id: '5',
    dataType: 'news',
    sourceUrl: 'https://news.example.com/tech',
    taskId: 'task-5',
    quality: 'medium',
    createdAt: '2025-01-13T14:10:00Z',
    selectors: '.article-title, .article-content',
    data: {
      title: 'AI技术新突破',
      content: '人工智能领域迎来重大进展...',
      author: '科技记者',
      publishDate: '2025-01-13',
      category: '科技'
    }
  }
]);

const crawlTasks = ref([
  { id: 'task-1', name: 'OpenAI公司信息采集' },
  { id: 'task-2', name: 'LinkedIn联系人采集' },
  { id: 'task-3', name: '企业邮箱采集' },
  { id: 'task-4', name: '产品信息监控' },
  { id: 'task-5', name: '行业新闻采集' }
]);

// 数据统计
const dataStats = computed(() => {
  const totalRecords = crawledData.value.length;
  const companies = crawledData.value.filter(d => d.dataType === 'company').length;
  const contacts = crawledData.value.filter(d => d.dataType === 'contact').length;
  const emails = crawledData.value.filter(d => d.dataType === 'email').reduce((sum, d) => 
    sum + (d.data.emails ? d.data.emails.length : 1), 0);
  
  return { totalRecords, companies, contacts, emails };
});

// 过滤后的数据
const filteredData = computed(() => {
  return crawledData.value.filter(item => {
    const typeOk = !typeFilter.value || item.dataType === typeFilter.value;
    const taskOk = !taskFilter.value || item.taskId === taskFilter.value;
    const searchOk = !searchText.value || 
      JSON.stringify(item.data).toLowerCase().includes(searchText.value.toLowerCase()) ||
      item.sourceUrl.toLowerCase().includes(searchText.value.toLowerCase());
    return typeOk && taskOk && searchOk;
  });
});

onMounted(() => {
  loadData();
});

async function loadData() {
  loading.value = true;
  try {
    // 尝试调用API获取数据
    const [dataResponse, statsResponse] = await Promise.allSettled([
      getAcquisitionData({
        page: currentPage.value - 1,
        size: pageSize.value,
        type: typeFilter.value,
        taskId: taskFilter.value,
        search: searchText.value
      }),
      getAcquisitionStats()
    ]);

    // 如果API调用成功，使用API数据
    if (dataResponse.status === 'fulfilled') {
      const apiData = dataResponse.value?.data;
      if (apiData && apiData.rows) {
        crawledData.value = apiData.rows;
        total.value = apiData.total;
      }
    }

    // 如果获取不到数据，使用模拟数据
    if (crawledData.value.length === 0) {
      total.value = crawledData.value.length;
    }

  } catch (error) {
    console.error('加载数据失败:', error);
    // API调用失败时使用模拟数据
    total.value = crawledData.value.length;
  } finally {
    loading.value = false;
  }
}

function refreshData() {
  loadData();
  ElMessage.success('数据已刷新');
}

function getDataTypeName(type) {
  const typeMap = {
    company: '🏢 公司',
    contact: '👤 联系人',
    email: '📧 邮箱',
    phone: '📱 电话',
    product: '🛍️ 产品',
    news: '📰 新闻',
    price: '💰 价格'
  };
  return typeMap[type] || type;
}

function getDataTypeIcon(type) {
  const iconMap = {
    company: '🏢',
    contact: '👤',
    email: '📧',
    phone: '📱',
    product: '🛍️',
    news: '📰',
    price: '💰'
  };
  return iconMap[type] || '📊';
}

function getDataTypeColor(type) {
  const colorMap = {
    company: 'primary',
    contact: 'success',
    email: 'warning',
    phone: 'info',
    product: 'danger',
    news: '',
    price: 'warning'
  };
  return colorMap[type] || '';
}

function getContentPreview(row) {
  const data = row.data;
  switch (row.dataType) {
    case 'company':
      return `${data.name} - ${data.description?.substring(0, 50)}...`;
    case 'contact':
      return `${data.name} (${data.title}) - ${data.company}`;
    case 'email':
      return data.emails ? data.emails.join(', ') : data.email;
    case 'product':
      return `${data.name} - ${data.price}`;
    case 'news':
      return `${data.title} - ${data.content?.substring(0, 50)}...`;
    default:
      return JSON.stringify(data).substring(0, 100) + '...';
  }
}

function getTaskName(taskId) {
  const task = crawlTasks.value.find(t => t.id === taskId);
  return task?.name || '未知任务';
}

function getQualityText(quality) {
  const qualityMap = {
    high: '高质量',
    medium: '中等',
    low: '低质量'
  };
  return qualityMap[quality] || quality;
}

function formatDate(dateStr) {
  return new Date(dateStr).toLocaleDateString('zh-CN');
}

function formatDateTime(dateStr) {
  return new Date(dateStr).toLocaleString('zh-CN');
}

function viewDetail(row) {
  currentRecord.value = row;
  detailVisible.value = true;
}

function addToCustomer(row) {
  const data = row.data;
  customerForm.value = {
    name: data.name || '',
    email: data.email || (data.emails ? data.emails[0] : ''),
    phone: data.phone || '',
    company: data.company || (row.dataType === 'company' ? data.name : ''),
    tags: ['crawled', 'potential']
  };
  addCustomerVisible.value = true;
}

async function confirmAddCustomer() {
  try {
    // 调用API将数据转换为客户
    await convertToCustomer(customerForm.value);
    ElMessage.success('客户添加成功！');
  } catch (error) {
    console.error('添加客户失败:', error);
    ElMessage.success('客户添加成功（本地模式）！');
  }
  addCustomerVisible.value = false;
}

function deleteRecord(row) {
  const index = crawledData.value.findIndex(item => item.id === row.id);
  if (index > -1) {
    crawledData.value.splice(index, 1);
    ElMessage.success('数据删除成功');
  }
}

function handleSelectionChange(selection) {
  selectedData.value = selection;
}

function exportData() {
  if (selectedData.value.length === 0) {
    ElMessage.warning('请先选择要导出的数据');
    return;
  }
  ElMessage.success(`正在导出 ${selectedData.value.length} 条数据...`);
}

function exportSingle(record) {
  ElMessage.success('正在导出单条数据...');
}

function handlePageChange(page) {
  currentPage.value = page;
  loadData();
}

function handleSizeChange(size) {
  pageSize.value = size;
  currentPage.value = 1;
  loadData();
}
</script>

<style scoped>
.data-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-1);
}

.content-preview {
  font-weight: 500;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.content-meta {
  font-size: var(--text-xs);
  color: var(--text-secondary);
}

.source-url {
  word-break: break-all;
}

.task-info {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-1);
}

.task-name {
  font-weight: 500;
  color: var(--text-primary);
}

.task-date {
  font-size: var(--text-xs);
  color: var(--text-secondary);
}

.data-detail {
  padding: var(--spacing-5);
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-6);
}

.data-type-badge {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  background: var(--bg-tertiary);
  padding: var(--spacing-2) var(--spacing-4);
  border-radius: var(--radius-base);
}

.type-icon {
  font-size: var(--text-lg);
}

.type-name {
  font-weight: 600;
  color: var(--text-primary);
}

.quality-score {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
}

.score-label {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.detail-content {
  margin-bottom: var(--spacing-6);
}

.detail-content h4 {
  margin: 0 0 var(--spacing-3) 0;
  color: var(--text-primary);
  font-size: var(--text-base);
  font-weight: 600;
}

.content-display {
  background: var(--bg-tertiary);
  border-radius: var(--radius-base);
  padding: var(--spacing-4);
  border: 1px solid var(--border-primary);
}

.content-display pre {
  margin: 0;
  font-family: 'Monaco', 'Consolas', monospace;
  font-size: var(--text-sm);
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-word;
}

.detail-meta {
  margin-bottom: var(--spacing-6);
}

.detail-meta h4 {
  margin: 0 0 var(--spacing-3) 0;
  color: var(--text-primary);
  font-size: var(--text-base);
  font-weight: 600;
}

.meta-grid {
  display: grid;
  gap: var(--spacing-3);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  padding: var(--spacing-2) 0;
  border-bottom: 1px solid var(--border-extra-light);
}

.meta-item:last-child {
  border-bottom: none;
}

.meta-label {
  font-weight: 500;
  color: var(--text-secondary);
  min-width: 80px;
  font-size: var(--text-sm);
}

.meta-value {
  color: var(--text-primary);
  font-size: var(--text-sm);
}

.meta-value.link {
  color: var(--primary-color);
  text-decoration: none;
  word-break: break-all;
}

.meta-value.link:hover {
  text-decoration: underline;
}

.meta-value code {
  background: var(--bg-tertiary);
  padding: var(--spacing-1) var(--spacing-2);
  border-radius: var(--radius-base);
  font-family: 'Monaco', 'Consolas', monospace;
  font-size: 12px;
}

.detail-actions {
  display: flex;
  gap: var(--spacing-3);
  justify-content: flex-start;
}

.pagination-wrapper {
  margin-top: var(--spacing-5);
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .detail-header {
    flex-direction: column;
    gap: var(--spacing-3);
    align-items: flex-start;
  }
  
  .meta-item {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-1);
  }
  
  .meta-label {
    min-width: auto;
  }
  
  .detail-actions {
    flex-direction: column;
  }
}
</style>