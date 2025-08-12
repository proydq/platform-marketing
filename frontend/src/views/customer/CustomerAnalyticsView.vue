<template>
  <div class="analytics-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>{{ $t("customer.analytics.title") }}</h2>
      <p>{{ $t("customer.analytics.description") }}</p>
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
            <el-option
              v-for="source in sourceOptions"
              :key="source"
              :label="source"
              :value="source"
            />
          </el-select>
        </div>

        <div class="action-buttons">
          <el-button @click="refreshData">
            <el-icon><Refresh /></el-icon>
            {{ $t("common.refresh") }}
          </el-button>
          <el-button type="primary" @click="exportReport">
            <el-icon><Download /></el-icon>
            {{ $t("customer.analytics.exportReport") }}
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <el-card class="stat-card" v-loading="statsLoading">
        <div class="stat-content">
          <div class="stat-value">
            {{ formatNumber(overviewStats.totalCustomers) }}
          </div>
          <div class="stat-label">
            {{ $t("customer.analytics.totalCustomers") }}
          </div>
          <div
            class="stat-change"
            :class="getChangeClass(overviewStats.customerGrowth)"
          >
            <el-icon><TrendCharts /></el-icon>
            {{ formatPercentage(overviewStats.customerGrowth) }}
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" v-loading="statsLoading">
        <div class="stat-content">
          <div class="stat-value">
            {{ formatNumber(overviewStats.activeCustomers) }}
          </div>
          <div class="stat-label">
            {{ $t("customer.analytics.activeCustomers") }}
          </div>
          <div
            class="stat-change"
            :class="getChangeClass(overviewStats.activeGrowth)"
          >
            <el-icon><TrendCharts /></el-icon>
            {{ formatPercentage(overviewStats.activeGrowth) }}
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" v-loading="statsLoading">
        <div class="stat-content">
          <div class="stat-value">
            {{ formatNumber(overviewStats.newCustomers) }}
          </div>
          <div class="stat-label">
            {{ $t("customer.analytics.newCustomers") }}
          </div>
          <div
            class="stat-change"
            :class="getChangeClass(overviewStats.newGrowth)"
          >
            <el-icon><TrendCharts /></el-icon>
            {{ formatPercentage(overviewStats.newGrowth) }}
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" v-loading="statsLoading">
        <div class="stat-content">
          <div class="stat-value">
            {{ formatPercentage(overviewStats.acquisitionRate) }}
          </div>
          <div class="stat-label">
            {{ $t("customer.analytics.acquisitionRate") }}
          </div>
          <div
            class="stat-change"
            :class="getChangeClass(overviewStats.acquisitionGrowth)"
          >
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
          <h3>{{ $t("customer.analytics.growthTrend") }}</h3>
        </template>
        <div class="chart-container" v-loading="chartsLoading">
          <GrowthTrendChart :data="growthTrendData" :height="300" />
        </div>
      </el-card>

      <!-- 客户来源分布 -->
      <el-card class="chart-card">
        <template #header>
          <h3>{{ $t("customer.analytics.sourceDistribution") }}</h3>
        </template>
        <div class="chart-container" v-loading="chartsLoading">
          <SourceDistributionChart
            :data="sourceDistributionData"
            :height="300"
          />
        </div>
      </el-card>

      <!-- 客户状态分布 -->
      <el-card class="chart-card">
        <template #header>
          <h3>{{ $t("customer.analytics.statusDistribution") }}</h3>
        </template>
        <div class="chart-container" v-loading="chartsLoading">
          <StatusDistributionChart
            :data="statusDistributionData"
            :height="300"
          />
        </div>
      </el-card>

      <!-- 获取转化率 -->
      <el-card class="chart-card">
        <template #header>
          <h3>{{ $t("customer.analytics.conversionRate") }}</h3>
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
          <h3>{{ $t("customer.analytics.detailData") }}</h3>
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
import { ref, reactive, onMounted, computed, watch } from "vue";
import { ElMessage } from "element-plus";
import { Refresh, Download, TrendCharts } from "@element-plus/icons-vue";
import { useI18n } from "vue-i18n";
import { useCustomerStore } from "@/stores/modules/customer";
import { formatNumber, formatPercentage } from "@/utils/format";
import GrowthTrendChart from "./components/charts/GrowthTrendChart.vue";
import SourceDistributionChart from "./components/charts/SourceDistributionChart.vue";
import StatusDistributionChart from "./components/charts/StatusDistributionChart.vue";
import ConversionRateChart from "./components/charts/ConversionRateChart.vue";

const { t } = useI18n();
const customerStore = useCustomerStore();

// 响应式数据
const statsLoading = ref(false);
const chartsLoading = ref(false);
const tableLoading = ref(false);
const dateRange = ref([]);
const selectedSource = ref("");
const tableView = ref("source");

// 统计数据
const overviewStats = reactive({
  totalCustomers: 0,
  customerGrowth: 0,
  activeCustomers: 0,
  activeGrowth: 0,
  newCustomers: 0,
  newGrowth: 0,
  acquisitionRate: 0,
  acquisitionGrowth: 0,
});

// 图表数据
const growthTrendData = ref([]);
const sourceDistributionData = ref([]);
const statusDistributionData = ref([]);
const conversionRateData = ref([]);

// 表格数据
const tableData = ref([]);

// 计算属性
const sourceOptions = computed(() => customerStore.sourcesOptions);

// 生命周期
onMounted(() => {
  // 初始化日期范围为最近30天
  const endDate = new Date();
  const startDate = new Date();
  startDate.setDate(startDate.getDate() - 30);

  dateRange.value = [
    startDate.toISOString().split("T")[0],
    endDate.toISOString().split("T")[0],
  ];

  loadAllData();
});

// 监听器
watch([dateRange, selectedSource], () => {
  loadAllData();
});

// 方法
const loadAllData = async () => {
  await Promise.all([loadOverviewStats(), loadChartsData(), loadTableData()]);
};

const loadOverviewStats = async () => {
  statsLoading.value = true;
  try {
    const params = {
      startDate: dateRange.value[0],
      endDate: dateRange.value[1],
      source: selectedSource.value,
    };
    const stats = await customerStore.getCustomerAnalytics(params);
    Object.assign(overviewStats, stats);
  } catch (error) {
    ElMessage.error(t("customer.analytics.loadStatsError"));
  } finally {
    statsLoading.value = false;
  }
};

const loadChartsData = async () => {
  chartsLoading.value = true;
  try {
    const params = {
      startDate: dateRange.value[0],
      endDate: dateRange.value[1],
      source: selectedSource.value,
    };

    const [
      growthTrend,
      sourceDistribution,
      statusDistribution,
      conversionRate,
    ] = await Promise.all([
      customerStore.getGrowthTrendData(params),
      customerStore.getSourceDistributionData(params),
      customerStore.getStatusDistributionData(params),
      customerStore.getConversionRateData(params),
    ]);

    growthTrendData.value = growthTrend;
    sourceDistributionData.value = sourceDistribution;
    statusDistributionData.value = statusDistribution;
    conversionRateData.value = conversionRate;
  } catch (error) {
    ElMessage.error(t("customer.analytics.loadChartsError"));
  } finally {
    chartsLoading.value = false;
  }
};

const loadTableData = async () => {
  tableLoading.value = true;
  try {
    const params = {
      startDate: dateRange.value[0],
      endDate: dateRange.value[1],
      source: selectedSource.value,
      groupBy: tableView.value,
    };
    tableData.value = await customerStore.getAnalyticsTableData(params);
  } catch (error) {
    ElMessage.error(t("customer.analytics.loadTableError"));
  } finally {
    tableLoading.value = false;
  }
};

const handleDateChange = () => {
  // 日期变化会触发 watch
};

const handleSourceChange = () => {
  // 来源变化会触发 watch
};

const handleTableViewChange = () => {
  loadTableData();
};

const refreshData = () => {
  loadAllData();
};

const exportReport = async () => {
  try {
    const params = {
      startDate: dateRange.value[0],
      endDate: dateRange.value[1],
      source: selectedSource.value,
    };
    await customerStore.exportAnalyticsReport(params);
    ElMessage.success(t("customer.analytics.exportSuccess"));
  } catch (error) {
    ElMessage.error(t("customer.analytics.exportError"));
  }
};

// 辅助方法
const getChangeClass = (value) => {
  if (value > 0) return "positive-change";
  if (value < 0) return "negative-change";
  return "neutral-change";
};

const getTableColumnProp = (column) => {
  const props = {
    name:
      tableView.value === "source"
        ? "source"
        : tableView.value === "status"
        ? "status"
        : "date",
    count: "count",
    percentage: "percentage",
    growth: "growth",
  };
  return props[column];
};

const getTableColumnLabel = (column) => {
  const labels = {
    name:
      tableView.value === "source"
        ? t("customer.analytics.source")
        : tableView.value === "status"
        ? t("customer.analytics.status")
        : t("customer.analytics.date"),
  };
  return labels[column];
};
</script>

<style scoped>
.analytics-container {
  padding: 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

.analytics-container::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    135deg,
    rgba(102, 126, 234, 0.02) 0%,
    rgba(118, 75, 162, 0.02) 100%
  );
  z-index: -1;
}

.page-header {
  margin-bottom: 32px;
  text-align: center;
  padding: 32px;
  background: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.95) 0%,
    rgba(248, 250, 252, 0.95) 100%
  );
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.3);
  position: relative;
  overflow: hidden;
}

.page-header::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    135deg,
    rgba(102, 126, 234, 0.03) 0%,
    rgba(118, 75, 162, 0.03) 100%
  );
  z-index: -1;
}

.page-header h2 {
  margin: 0 0 12px 0;
  font-size: 32px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: 0.5px;
}

.page-header p {
  margin: 0;
  color: #64748b;
  font-size: 16px;
  font-weight: 500;
  opacity: 0.8;
}

.filter-card {
  margin-bottom: 32px;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  overflow: hidden;
}

.filter-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
  padding: 24px 28px;
}

.filter-section {
  display: flex;
  align-items: center;
  gap: 16px;
  background: rgba(248, 250, 252, 0.7);
  padding: 16px 20px;
  border-radius: 16px;
  border: 1px solid rgba(226, 232, 240, 0.5);
}

.action-buttons {
  display: flex;
  gap: 16px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.stat-card {
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.95) 0%,
    rgba(248, 250, 252, 0.95) 100%
  );
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(10px);
}

.stat-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    135deg,
    rgba(102, 126, 234, 0.03) 0%,
    rgba(118, 75, 162, 0.03) 100%
  );
  z-index: -1;
}

.stat-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 48px rgba(102, 126, 234, 0.15);
}

.stat-card:hover::before {
  background: linear-gradient(
    135deg,
    rgba(102, 126, 234, 0.08) 0%,
    rgba(118, 75, 162, 0.08) 100%
  );
}

.stat-content {
  text-align: center;
  padding: 32px 24px;
  position: relative;
}

.stat-value {
  font-size: 40px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 12px;
  letter-spacing: -0.5px;
}

.stat-label {
  font-size: 16px;
  color: #64748b;
  margin-bottom: 16px;
  font-weight: 600;
  opacity: 0.8;
}

.stat-change {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 15px;
  font-weight: 700;
  padding: 8px 16px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(8px);
}

.positive-change {
  color: #10b981;
  background: linear-gradient(
    135deg,
    rgba(16, 185, 129, 0.1) 0%,
    rgba(5, 150, 105, 0.1) 100%
  );
  border-color: rgba(16, 185, 129, 0.2);
}

.negative-change {
  color: #ef4444;
  background: linear-gradient(
    135deg,
    rgba(239, 68, 68, 0.1) 0%,
    rgba(220, 38, 38, 0.1) 100%
  );
  border-color: rgba(239, 68, 68, 0.2);
}

.neutral-change {
  color: #64748b;
  background: linear-gradient(
    135deg,
    rgba(100, 116, 139, 0.1) 0%,
    rgba(71, 85, 105, 0.1) 100%
  );
  border-color: rgba(100, 116, 139, 0.2);
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(480px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.chart-card,
.table-card {
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.95) 0%,
    rgba(248, 250, 252, 0.95) 100%
  );
  backdrop-filter: blur(10px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.chart-card::before,
.table-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    135deg,
    rgba(102, 126, 234, 0.02) 0%,
    rgba(118, 75, 162, 0.02) 100%
  );
  z-index: -1;
}

.chart-card:hover,
.table-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.12);
}

.chart-card h3,
.table-card h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.chart-container {
  min-height: 320px;
  width: 100%;
  padding: 16px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 16px;
  margin-top: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
  padding: 24px 28px;
  background: linear-gradient(
    135deg,
    rgba(248, 250, 252, 0.7) 0%,
    rgba(241, 245, 249, 0.7) 100%
  );
  border-bottom: 1px solid rgba(226, 232, 240, 0.5);
  margin: -1px;
  border-radius: 20px 20px 0 0;
}

/* Element Plus 组件样式增强 */
:deep(.el-card) {
  border: none;
  box-shadow: none;
}

:deep(.el-card__header) {
  background: transparent;
  padding: 24px 28px;
}

:deep(.el-card__body) {
  padding: 0 28px 28px 28px;
}

:deep(.el-date-picker) {
  border-radius: 12px;
}

:deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  border: 1px solid #e2e8f0;
  background: rgba(255, 255, 255, 0.9);
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.15);
  border-color: #667eea;
  background: rgba(255, 255, 255, 1);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.25);
  border-color: #667eea;
  background: rgba(255, 255, 255, 1);
}

:deep(.el-select .el-input__wrapper) {
  border-radius: 12px;
}

:deep(.el-button) {
  border-radius: 12px;
  font-weight: 600;
  padding: 12px 24px;
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

:deep(.el-table) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.3);
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
}

:deep(.el-table__body tr) {
  background: rgba(255, 255, 255, 0.7);
  transition: all 0.2s ease;
}

:deep(.el-table__body tr:hover) {
  background: rgba(102, 126, 234, 0.05);
}

:deep(.el-table__body td) {
  border: none;
  padding: 14px 12px;
}

/* 动画效果 */
@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
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

.analytics-container {
  animation: fadeIn 0.8s ease-out;
}

.page-header {
  animation: slideInUp 0.8s ease-out 0.1s both;
}

.filter-card {
  animation: slideInUp 0.8s ease-out 0.2s both;
}

.stat-card:nth-child(1) {
  animation: slideInUp 0.6s ease-out 0.3s both;
}
.stat-card:nth-child(2) {
  animation: slideInUp 0.6s ease-out 0.4s both;
}
.stat-card:nth-child(3) {
  animation: slideInUp 0.6s ease-out 0.5s both;
}
.stat-card:nth-child(4) {
  animation: slideInUp 0.6s ease-out 0.6s both;
}

.chart-card:nth-child(1) {
  animation: slideInRight 0.6s ease-out 0.7s both;
}
.chart-card:nth-child(2) {
  animation: slideInRight 0.6s ease-out 0.8s both;
}
.chart-card:nth-child(3) {
  animation: slideInRight 0.6s ease-out 0.9s both;
}
.chart-card:nth-child(4) {
  animation: slideInRight 0.6s ease-out 1s both;
}

.table-card {
  animation: slideInUp 0.8s ease-out 1.1s both;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .analytics-container {
    padding: 16px;
  }

  .page-header {
    padding: 24px 20px;
    margin-bottom: 24px;
  }

  .page-header h2 {
    font-size: 28px;
  }

  .page-header p {
    font-size: 14px;
  }

  .filter-card {
    margin-bottom: 24px;
  }

  .filter-row {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
    padding: 20px;
  }

  .filter-section {
    flex-direction: column;
    gap: 12px;
    padding: 16px;
  }

  .action-buttons {
    justify-content: center;
    gap: 12px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
    gap: 20px;
    margin-bottom: 24px;
  }

  .stat-content {
    padding: 24px 20px;
  }

  .stat-value {
    font-size: 32px;
  }

  .charts-grid {
    grid-template-columns: 1fr;
    gap: 20px;
    margin-bottom: 24px;
  }

  .chart-container {
    min-height: 280px;
    padding: 12px;
  }

  .table-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
    padding: 20px;
  }

  :deep(.el-card__body) {
    padding: 0 20px 20px 20px;
  }

  :deep(.el-button) {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .analytics-container {
    padding: 12px;
  }

  .page-header {
    padding: 20px 16px;
    border-radius: 16px;
  }

  .page-header h2 {
    font-size: 24px;
  }

  .filter-card,
  .chart-card,
  .table-card {
    border-radius: 16px;
  }

  .stats-grid {
    gap: 16px;
  }

  .stat-content {
    padding: 20px 16px;
  }

  .stat-value {
    font-size: 28px;
  }

  .stat-label {
    font-size: 14px;
  }

  .charts-grid {
    gap: 16px;
  }

  .chart-container {
    min-height: 240px;
    padding: 8px;
  }

  .chart-card h3,
  .table-card h3 {
    font-size: 16px;
  }

  :deep(.el-card__header) {
    padding: 16px 20px;
  }

  :deep(.el-card__body) {
    padding: 0 16px 16px 16px;
  }
}
</style>
