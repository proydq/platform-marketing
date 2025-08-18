<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-title">
        <span class="page-icon">🕷️</span>
        爬虫数据管理
      </div>
      <div class="page-subtitle">
        查看和管理爬取到的客户数据，包括联系信息、公司信息等
      </div>
      <div class="page-actions">
        <el-button type="primary" class="btn-primary" @click="exportData">
          <el-icon><Download /></el-icon>
          导出数据
        </el-button>
        <el-button class="btn-secondary" @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-section">
      <el-form :model="filters" inline class="filter-form">
        <el-form-item label="任务名称">
          <el-select v-model="filters.taskId" placeholder="选择任务" clearable style="width: 200px;">
            <el-option
              v-for="task in tasks"
              :key="task.id"
              :label="task.name"
              :value="task.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="数据来源">
          <el-select v-model="filters.platform" placeholder="选择平台" clearable style="width: 150px;">
            <el-option label="LinkedIn" value="linkedin" />
            <el-option label="Google Maps" value="google_maps" />
            <el-option label="企查查" value="qichacha" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="数据状态">
          <el-select v-model="filters.status" placeholder="选择状态" clearable style="width: 120px;">
            <el-option label="有效" value="valid" />
            <el-option label="无效" value="invalid" />
            <el-option label="待验证" value="pending" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 数据统计 -->
    <div class="stats-grid">
      <div class="stat-card fade-in">
        <div class="stat-header">
          <div class="stat-icon" style="background: linear-gradient(135deg, #dbeafe, #93c5fd); color: #1e40af;">
            📊
          </div>
          <div class="stat-trend">总计</div>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.total.toLocaleString() }}</div>
          <div class="stat-label">总数据量</div>
        </div>
      </div>

      <div class="stat-card fade-in" style="animation-delay: 0.1s;">
        <div class="stat-header">
          <div class="stat-icon" style="background: linear-gradient(135deg, #d1fae5, #86efac); color: #059669;">
            ✅
          </div>
          <div class="stat-trend positive">{{ ((stats.valid / stats.total) * 100).toFixed(1) }}%</div>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.valid.toLocaleString() }}</div>
          <div class="stat-label">有效数据</div>
        </div>
      </div>

      <div class="stat-card fade-in" style="animation-delay: 0.2s;">
        <div class="stat-header">
          <div class="stat-icon" style="background: linear-gradient(135deg, #fef3c7, #fcd34d); color: #d97706;">
            🔍
          </div>
          <div class="stat-trend">今日</div>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.today.toLocaleString() }}</div>
          <div class="stat-label">今日新增</div>
        </div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="data-table-wrapper">
      <div class="table-header">
        <div class="table-title">🗂️ 爬取数据列表</div>
        <div class="table-subtitle">展示所有爬取到的客户数据，支持批量操作和数据导出</div>
        <div class="table-actions">
          <el-button 
            type="danger" 
            size="small" 
            :disabled="selectedRows.length === 0"
            @click="batchDelete"
          >
            <el-icon><Delete /></el-icon>
            批量删除 ({{ selectedRows.length }})
          </el-button>
          <el-button 
            type="success" 
            size="small" 
            :disabled="selectedRows.length === 0"
            @click="batchAddToCustomers"
          >
            <el-icon><Plus /></el-icon>
            批量加入客户库 ({{ selectedRows.length }})
          </el-button>
        </div>
      </div>

      <el-table 
        :data="tableData" 
        style="width: 100%"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column prop="name" label="姓名/公司" min-width="150">
          <template #default="{ row }">
            <div class="name-cell">
              <div class="main-name">{{ row.name || row.company || '未知' }}</div>
              <div class="sub-name" v-if="row.name && row.company">{{ row.company }}</div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="email" label="邮箱" width="200">
          <template #default="{ row }">
            <span v-if="row.email" class="email-link">{{ row.email }}</span>
            <span v-else class="no-data">-</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="phone" label="电话" width="140">
          <template #default="{ row }">
            <span v-if="row.phone">{{ row.phone }}</span>
            <span v-else class="no-data">-</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="title" label="职位" width="120">
          <template #default="{ row }">
            <span v-if="row.title">{{ row.title }}</span>
            <span v-else class="no-data">-</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="platform" label="来源" width="100">
          <template #default="{ row }">
            <el-tag :type="getPlatformTagType(row.platform)" size="small">
              {{ getPlatformName(row.platform) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createdAt" label="采集时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="viewDetail(row)">
              <el-icon><View /></el-icon>
              详情
            </el-button>
            <el-button type="text" size="small" @click="addToCustomers(row)">
              <el-icon><Plus /></el-icon>
              加入客户库
            </el-button>
            <el-button type="text" size="small" danger @click="deleteData(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="数据详情" width="600px">
      <div v-if="currentRow">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="姓名">{{ currentRow.name || '未知' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ currentRow.email || '无' }}</el-descriptions-item>
          <el-descriptions-item label="电话">{{ currentRow.phone || '无' }}</el-descriptions-item>
          <el-descriptions-item label="公司">{{ currentRow.company || '无' }}</el-descriptions-item>
          <el-descriptions-item label="职位">{{ currentRow.title || '无' }}</el-descriptions-item>
          <el-descriptions-item label="地址">{{ currentRow.location || '无' }}</el-descriptions-item>
          <el-descriptions-item label="数据来源">
            <el-tag :type="getPlatformTagType(currentRow.platform)" size="small">
              {{ getPlatformName(currentRow.platform) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="数据状态">
            <el-tag :type="getStatusTagType(currentRow.status)" size="small">
              {{ getStatusName(currentRow.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="采集时间">{{ formatTime(currentRow.createdAt) }}</el-descriptions-item>
          <el-descriptions-item label="其他信息" v-if="currentRow.metadata">
            <pre>{{ JSON.stringify(currentRow.metadata, null, 2) }}</pre>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Download, Refresh, Search, View, Delete, Plus
} from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const selectedRows = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const detailVisible = ref(false)
const currentRow = ref(null)
const tasks = ref([])

// 筛选条件
const filters = reactive({
  taskId: '',
  platform: '',
  status: ''
})

// 统计数据
const stats = ref({
  total: 0,
  valid: 0,
  today: 0
})

// 模拟数据
const mockData = [
  {
    id: '1',
    name: '张三',
    email: 'zhangsan@example.com',
    phone: '+86 138-0000-0001',
    company: '科技有限公司',
    title: '产品经理',
    location: '北京市朝阳区',
    platform: 'linkedin',
    status: 'valid',
    createdAt: new Date().toISOString(),
    metadata: { source_url: 'https://linkedin.com/in/zhangsan' }
  },
  {
    id: '2',
    name: '李四',
    email: 'lisi@company.com',
    phone: '+86 139-0000-0002',
    company: '互联网科技公司',
    title: '技术总监',
    location: '上海市浦东新区',
    platform: 'google_maps',
    status: 'valid',
    createdAt: new Date(Date.now() - 86400000).toISOString(),
    metadata: { business_hours: '9:00-18:00' }
  },
  {
    id: '3',
    name: '王五',
    email: '',
    phone: '+86 136-0000-0003',
    company: '传统制造业',
    title: '销售经理',
    location: '广州市天河区',
    platform: 'qichacha',
    status: 'pending',
    createdAt: new Date(Date.now() - 172800000).toISOString(),
  }
]

const mockTasks = [
  { id: '1', name: 'LinkedIn高管采集' },
  { id: '2', name: 'Google Maps商户信息' },
  { id: '3', name: '企查查企业数据' }
]

// 方法
const loadData = async () => {
  loading.value = true
  try {
    // 这里应该调用实际的API
    // const response = await getCrawlData(filters, currentPage.value, pageSize.value)
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 应用筛选
    let filteredData = [...mockData]
    if (filters.platform) {
      filteredData = filteredData.filter(item => item.platform === filters.platform)
    }
    if (filters.status) {
      filteredData = filteredData.filter(item => item.status === filters.status)
    }
    
    tableData.value = filteredData
    total.value = filteredData.length
    
    // 更新统计数据
    stats.value = {
      total: mockData.length,
      valid: mockData.filter(item => item.status === 'valid').length,
      today: mockData.filter(item => {
        const today = new Date().toDateString()
        return new Date(item.createdAt).toDateString() === today
      }).length
    }
    
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const loadTasks = async () => {
  // 模拟加载任务列表
  tasks.value = mockTasks
}

const refreshData = () => {
  loadData()
}

const resetFilters = () => {
  filters.taskId = ''
  filters.platform = ''
  filters.status = ''
  loadData()
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadData()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadData()
}

const viewDetail = (row) => {
  currentRow.value = row
  detailVisible.value = true
}

const addToCustomers = (row) => {
  ElMessage.success(`已将 ${row.name || row.company} 添加到客户库`)
}

const batchAddToCustomers = () => {
  ElMessage.success(`已将 ${selectedRows.value.length} 条数据添加到客户库`)
  selectedRows.value = []
}

const deleteData = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除 ${row.name || row.company} 的数据吗？`,
      '确认删除',
      {
        type: 'warning',
      }
    )
    
    const index = tableData.value.findIndex(item => item.id === row.id)
    if (index > -1) {
      tableData.value.splice(index, 1)
      total.value--
      ElMessage.success('删除成功')
    }
  } catch {
    // 用户取消删除
  }
}

const batchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 条数据吗？`,
      '批量删除',
      {
        type: 'warning',
      }
    )
    
    const selectedIds = selectedRows.value.map(row => row.id)
    tableData.value = tableData.value.filter(item => !selectedIds.includes(item.id))
    total.value = tableData.value.length
    selectedRows.value = []
    
    ElMessage.success('批量删除成功')
  } catch {
    // 用户取消删除
  }
}

const exportData = () => {
  ElMessage.success('数据导出功能开发中...')
}

// 辅助方法
const getPlatformName = (platform) => {
  const nameMap = {
    linkedin: 'LinkedIn',
    google_maps: 'Google',
    qichacha: '企查查',
    other: '其他'
  }
  return nameMap[platform] || platform
}

const getPlatformTagType = (platform) => {
  const typeMap = {
    linkedin: 'primary',
    google_maps: 'success',
    qichacha: 'warning',
    other: ''
  }
  return typeMap[platform] || ''
}

const getStatusName = (status) => {
  const nameMap = {
    valid: '有效',
    invalid: '无效',
    pending: '待验证'
  }
  return nameMap[status] || status
}

const getStatusTagType = (status) => {
  const typeMap = {
    valid: 'success',
    invalid: 'danger',
    pending: 'warning'
  }
  return typeMap[status] || ''
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadTasks()
  loadData()
})
</script>

<style scoped>
.filter-section {
  background: var(--bg-primary);
  padding: var(--spacing-5);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-primary);
  margin-bottom: var(--spacing-6);
}

.filter-form .el-form-item {
  margin-bottom: 0;
}

.name-cell {
  display: flex;
  flex-direction: column;
}

.main-name {
  font-weight: 600;
  color: var(--text-primary);
}

.sub-name {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 2px;
}

.email-link {
  color: var(--primary-color);
  cursor: pointer;
}

.email-link:hover {
  text-decoration: underline;
}

.no-data {
  color: var(--text-placeholder);
  font-style: italic;
}

.pagination {
  margin-top: var(--spacing-5);
  text-align: center;
}

pre {
  background: var(--bg-tertiary);
  padding: var(--spacing-3);
  border-radius: var(--radius-base);
  font-size: 12px;
  max-height: 200px;
  overflow-y: auto;
}
</style>