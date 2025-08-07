<template>
  <div class="analytics-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>{{ $t('customer.analytics.title') }}</h2>
      <p>{{ $t('customer.analytics.description') }}</p>
    </div>

    <!-- 时间范围选择 -->
    <el-card class="filter-card" shadow="never">
      <div class="filter-row">
        <div class="filter-section">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            :range-separator="$t('common.to')"
            :start-placeholder="$t('common.startDate')"
            :end-placeholder="$t('common.endDate')"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
          
          <el-select
            v-model="selectedSource"
            :placeholder="$t('customer.analytics.selectSource')"
            clearable
            style="width: 150px; margin-left: 12px"
            @change="handleSourceChange"
          >
            <el-option :label="$t('common.all')" value="" />
            <el-option v-for="source in sourceOptions" :key="source" :label="source" :value="source" />
          </el-select>
        </div>
        
        <div class="action-buttons">
          <el-button @click="refreshData">
            <el-icon><Refresh /></el-icon>
            {{ $t('common.refresh') }}
          </el-button>
          <el-button type="primary" @click="exportReport">
            <el-icon><Download /></el-icon>
            {{ $t('customer.analytics.exportReport') }}
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <el-card class="stat-card" v-loading="statsLoading">
        <div class="stat-content">
          <div class="stat-value">{{ formatNumber(overviewStats.totalCustomers) }}</div>
          <div class="stat-label">{{ $t('customer.analytics.totalCustomers') }}</div>
          <div class="stat-change" :class="getChangeClass(overviewStats.customerGrowth)">
            <el-icon><TrendCharts /></el-icon>
            {{ formatPercentage(overviewStats.customerGrowth) }}
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" v-loading="statsLoading">
        <div class="stat-content">
          <div class="stat-value">{{ formatNumber(overviewStats.activeCustomers) }}</div>
          <div class="stat-label">{{ $t('customer.analytics.activeCustomers') }}</div>
          <div class="stat-change" :class="getChangeClass(overviewStats.activeGrowth)">
            <el-icon><TrendCharts /></el-icon>
            {{ formatPercentage(overviewStats.activeGrowth) }}
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" v-loading="statsLoading">
        <div class="stat-content">
          <div class="stat-value">{{ formatNumber(overviewStats.newCustomers) }}</div>
          <div class="stat-label">{{ $t('customer.analytics.newCustomers') }}</div>
          <div class="stat-change" :class="getChangeClass(overviewStats.newGrowth)">
            <el-icon><TrendCharts /></el-icon>
            {{ formatPercentage(overviewStats.newGrowth) }}
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" v-loading="statsLoading">
        <div class="stat-content">
          <div class="stat-value">{{ formatPercentage(overviewStats.acquisitionRate) }}</div>
          <div class="stat-label">{{ $t('customer.analytics.acquisitionRate') }}</div>
          <div class="stat-change" :class="getChangeClass(overviewStats.acquisitionGrowth)">
            <el-icon><TrendCharts /></el-icon>
            {{ formatPercentage(overviewStats.acquisitionGrowth) }}
          </div>
        </div>
      </el-card>
    </div>

    <!-- 图表区域 -->
    <div class="charts-grid">
      <!-- 客户增长趋势 -->
      <el-card class="chart-card">
        <template #header>
          <h3>{{ $t('customer.analytics.growthTrend') }}</h3>
        </template>
        <div class="chart-container" v-loading="chartsLoading">
          <GrowthTrendChart :data="growthTrendData" :height="300" />
        </div>
      </el-card>

      <!-- 客户来源分布 -->
      <el-card class="chart-card">
        <template #header>
          <h3>{{ $t('customer.analytics.sourceDistribution') }}</h3>
        </template>
        <div class="chart-container" v-loading="chartsLoading">
          <SourceDistributionChart :data="sourceDistributionData" :height="300" />
        </div>
      </el-card>

      <!-- 客户状态分布 -->
      <el-card class="chart-card">
        <template #header>
          <h3>{{ $t('customer.analytics.statusDistribution') }}</h3>
        </template>
        <div class="chart-container" v-loading="chartsLoading">
          <StatusDistributionChart :data="statusDistributionData" :height="300" />
        </div>
      </el-card>

      <!-- 获取转化率 -->
      <el-card class="chart-card">
        <template #header>
          <h3>{{ $t('customer.analytics.conversionRate') }}</h3>
        </template>
        <div class="chart-container" v-loading="chartsLoading">
          <ConversionRateChart :data="conversionRateData" :height="300" />
        </div>
      </el-card>
    </div>

    <!-- 详细数据表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="table-header">
          <h3>{{ $t('customer.analytics.detailData') }}</h3>
          <div class="table-actions">
            <el-select
              v-model="tableView"
              style="width: 150px"
              @change="handleTableViewChange"
            >
              <el-option 
                :label="$t('customer.analytics.bySource')" 
                value="source" 
              />
              <el-option 
                :label="$t('customer.analytics.byStatus')" 
                value="status" 
              />
              <el-option 
                :label="$t('customer.analytics.byDate')" 
                value="date" 
              />
            </el-select>
          </div>
        </div>
      </template>

      <el-table
        :data="tableData"
        v-loading="tableLoading"
        stripe
        style="width: 100%"
        :empty-text="$t('common.noData')"
      >
        <el-table-column 
          :prop="getTableColumnProp('name')" 
          :label="getTableColumnLabel('name')" 
          min-width="150"
        />
        <el-table-column 
          :prop="getTableColumnProp('count')" 
          :label="$t('customer.analytics.customerCount')" 
          width="120" 
          align="center"
        >
          <template #default="{ row }">
            {{ formatNumber(row.count) }}
          </template>
        </el-table-column>
        <el-table-column 
          :prop="getTableColumnProp('percentage')" 
          :label="$t('customer.analytics.percentage')" 
          width="120" 
          align="center"
        >
          <template #default="{ row }">
            {{ formatPercentage(row.percentage) }}
          </template>
        </el-table-column>
        <el-table-column 
          :prop="getTableColumnProp('growth')" 
          :label="$t('customer.analytics.growth')" 
          width="120" 
          align="center"
        >
          <template #default="{ row }">
            <span :class="getChangeClass(row.growth)">
              {{ formatPercentage(row.growth) }}
            </span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Refresh, 
  Download, 
  TrendCharts
} from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import { useCustomerStore } from '@/stores/modules/customer'
import { formatNumber, formatPercentage } from '@/utils/format'
import GrowthTrendChart from './components/charts/GrowthTrendChart.vue'
import SourceDistributionChart from './components/charts/SourceDistributionChart.vue'
import StatusDistributionChart from './components/charts/StatusDistributionChart.vue'
import ConversionRateChart from './components/charts/ConversionRateChart.vue'

const { t } = useI18n()
const customerStore = useCustomerStore()

// 响应式数据
const statsLoading = ref(false)
const chartsLoading = ref(false)
const tableLoading = ref(false)
const dateRange = ref([])
const selectedSource = ref('')
const tableView = ref('source')

// 统计数据
const overviewStats = reactive({
  totalCustomers: 0,
  customerGrowth: 0,
  activeCustomers: 0,
  activeGrowth: 0,
  newCustomers: 0,
  newGrowth: 0,
  acquisitionRate: 0,
  acquisitionGrowth: 0
})

// 图表数据
const growthTrendData = ref([])
const sourceDistributionData = ref([])
const statusDistributionData = ref([])
const conversionRateData = ref([])

// 表格数据
const tableData = ref([])

// 计算属性
const sourceOptions = computed(() => customerStore.sourcesOptions)

// 生命周期
onMounted(() => {
  // 初始化日期范围为最近30天
  const endDate = new Date()
  const startDate = new Date()
  startDate.setDate(startDate.getDate() - 30)
  
  dateRange.value = [
    startDate.toISOString().split('T')[0],
    endDate.toISOString().split('T')[0]
  ]
  
  loadAllData()
})

// 监听器
watch([dateRange, selectedSource], () => {
  loadAllData()
})

// 方法
const loadAllData = async () => {
  await Promise.all([
    loadOverviewStats(),
    loadChartsData(),
    loadTableData()
  ])
}

const loadOverviewStats = async () => {
  statsLoading.value = true
  try {
    const params = {
      startDate: dateRange.value[0],
      endDate: dateRange.value[1],
      source: selectedSource.value
    }
    const stats = await customerStore.getCustomerAnalytics(params)
    Object.assign(overviewStats, stats)
  } catch (error) {
    ElMessage.error(t('customer.analytics.loadStatsError'))
  } finally {
    statsLoading.value = false
  }
}

const loadChartsData = async () => {
  chartsLoading.value = true
  try {
    const params = {
      startDate: dateRange.value[0],
      endDate: dateRange.value[1],
      source: selectedSource.value
    }
    
    const [growthTrend, sourceDistribution, statusDistribution, conversionRate] = await Promise.all([
      customerStore.getGrowthTrendData(params),
      customerStore.getSourceDistributionData(params),
      customerStore.getStatusDistributionData(params),
      customerStore.getConversionRateData(params)
    ])
    
    growthTrendData.value = growthTrend
    sourceDistributionData.value = sourceDistribution
    statusDistributionData.value = statusDistribution
    conversionRateData.value = conversionRate
  } catch (error) {
    ElMessage.error(t('customer.analytics.loadChartsError'))
  } finally {
    chartsLoading.value = false
  }
}

const loadTableData = async () => {
  tableLoading.value = true
  try {
    const params = {
      startDate: dateRange.value[0],
      endDate: dateRange.value[1],
      source: selectedSource.value,
      groupBy: tableView.value
    }
    tableData.value = await customerStore.getAnalyticsTableData(params)
  } catch (error) {
    ElMessage.error(t('customer.analytics.loadTableError'))
  } finally {
    tableLoading.value = false
  }
}

const handleDateChange = () => {
  // 日期变化会触发 watch
}

const handleSourceChange = () => {
  // 来源变化会触发 watch
}

const handleTableViewChange = () => {
  loadTableData()
}

const refreshData = () => {
  loadAllData()
}

const exportReport = async () => {
  try {
    const params = {
      startDate: dateRange.value[0],
      endDate: dateRange.value[1],
      source: selectedSource.value
    }
    await customerStore.exportAnalyticsReport(params)
    ElMessage.success(t('customer.analytics.exportSuccess'))
  } catch (error) {
    ElMessage.error(t('customer.analytics.exportError'))
  }
}

// 辅助方法
const getChangeClass = (value) => {
  if (value > 0) return 'positive-change'
  if (value < 0) return 'negative-change'
  return 'neutral-change'
}

const getTableColumnProp = (column) => {
  const props = {
    name: tableView.value === 'source' ? 'source' : 
          tableView.value === 'status' ? 'status' : 'date',
    count: 'count',
    percentage: 'percentage',
    growth: 'growth'
  }
  return props[column]
}

const getTableColumnLabel = (column) => {
  const labels = {
    name: tableView.value === 'source' ? t('customer.analytics.source') :
          tableView.value === 'status' ? t('customer.analytics.status') :
          t('customer.analytics.date')
  }
  return labels[column]
}
</script>

<style scoped>
.analytics-container {
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

.filter-card {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.filter-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.filter-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.stat-content {
  text-align: center;
  padding: 20px 16px;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
}

.stat-change {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 14px;
  font-weight: 600;
}

.positive-change {
  color: #67c23a;
}

.negative-change {
  color: #f56c6c;
}

.neutral-change {
  color: #909399;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card,
.table-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.chart-card h3,
.table-card h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.chart-container {
  min-height: 300px;
  width: 100%;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .analytics-container {
    padding: 12px;
  }
  
  .filter-row {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-section,
  .action-buttons {
    justify-content: center;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .charts-grid {
    grid-template-columns: 1fr;
  }
  
  .table-header {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>