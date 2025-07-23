<template>
  <div class="page-wrapper" style="background:#f6f9fc;">
    <el-card class="card-container" style="margin-bottom:20px;">
      <div class="action-buttons">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :shortcuts="shortcuts"
          style="width:240px" />
        <el-select v-model="dimension" placeholder="维度" style="width:120px">
          <el-option label="来源" value="source" />
          <el-option label="地域" value="region" />
          <el-option label="渠道" value="channel" />
        </el-select>
        <el-radio-group v-model="period" style="margin-left:10px;">
          <el-radio-button label="7" >近7天</el-radio-button>
          <el-radio-button label="30">近30天</el-radio-button>
        </el-radio-group>
      </div>
    </el-card>

    <el-row :gutter="20">
      <el-col :xs="24" :md="12">
        <el-card class="chart-container" shadow="hover">
          <template #header>
            <span class="section-title"><el-icon><PieChart /></el-icon> 转化漏斗</span>
          </template>
          <FunnelChart :data="summary.funnel" />
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card class="chart-container" shadow="hover">
          <template #header>
            <span class="section-title"><el-icon><Histogram /></el-icon> 渠道转化</span>
          </template>
          <BarChart :data="summary.channels" />
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card class="chart-container" shadow="hover">
          <template #header>
            <span class="section-title"><el-icon><PieChart /></el-icon> 流量来源</span>
          </template>
          <PieChartCmp :data="summary.sources" />
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card class="chart-container" shadow="hover">
          <template #header>
            <span class="section-title"><el-icon><TrendCharts /></el-icon> 活跃趋势</span>
          </template>
          <LineChart :data="period === '7' ? summary.active7 : summary.active30" />
        </el-card>
      </el-col>
    </el-row>

    <el-card class="chart-container">
      <template #header>
        <span class="section-title">行为明细</span>
      </template>
      <el-table :data="tablePage" style="width:100%" v-if="tablePage.length">
        <el-table-column prop="time" label="时间" width="160" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="channel" label="来源渠道" />
        <el-table-column prop="action" label="转化动作" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <span :class="['status-badge', row.status === '成功' ? 'status-success' : 'status-error']">{{ row.status }}</span>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="无数据" />
      <div class="action-buttons" style="justify-content:flex-end;">
        <el-button type="primary" @click="exportData">导出</el-button>
        <el-pagination
          v-model:current-page="page.current"
          v-model:page-size="page.size"
          :total="filteredLogs.length"
          layout="prev, pager, next"
          background />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { PieChart, Histogram, TrendCharts } from '@element-plus/icons-vue'
import FunnelChart from '@/components/charts/FunnelChart.vue'
import BarChart from '@/components/charts/BarChart.vue'
import PieChartCmp from '@/components/charts/PieChart.vue'
import LineChart from '@/components/charts/LineChart.vue'
import summaryData from '@/mock/reportSummary.json'
import logData from '@/mock/behaviorLog.json'


const dateRange = ref('')
const dimension = ref('source')
const period = ref('7')
const summary = ref({})
const logs = ref([])

const page = ref({ current: 1, size: 5 })

const shortcuts = [
  {
    text: '近7天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setDate(end.getDate() - 6)
      return [start, end]
    }
  },
  {
    text: '近30天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setDate(end.getDate() - 29)
      return [start, end]
    }
  }
]

onMounted(() => {
  summary.value = summaryData
  logs.value = logData
})

const filteredLogs = computed(() => {
  if (!dateRange.value) return logs.value
  const [start, end] = dateRange.value
  return logs.value.filter(l => {
    const t = new Date(l.time)
    return t >= start && t <= end
  })
})

const tablePage = computed(() => {
  const start = (page.value.current - 1) * page.value.size
  return filteredLogs.value.slice(start, start + page.value.size)
})

function exportData() {
  console.log('export')
}
</script>
