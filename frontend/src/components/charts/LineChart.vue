<template>
  <div class="chart-wrap">
    <VChart :option="chartOptions" autoresize v-loading="loading" />
    <el-empty v-if="!loading && isEmpty" :description="t('common.noData')" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { use } from 'echarts/core'
import VChart from 'vue-echarts'
import { LineChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { useI18n } from 'vue-i18n'

use([LineChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent, CanvasRenderer])


const props = defineProps({
  title: String,
  data: { type: Array, default: () => [] },
  options: { type: Object, default: () => ({}) },
  loading: { type: Boolean, default: false }
})

const { t } = useI18n()

const isEmpty = computed(() => !props.data || props.data.length === 0)

const chartOptions = computed(() => {
  const base = {
    color: ['#409EFF', '#67C23A', '#E6A23C'],
    title: props.title ? { text: props.title } : undefined,
    tooltip: { trigger: 'axis' },
    legend: {},
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: props.data.map(d => d.name) },
    yAxis: { type: 'value' },
    series: [
      {
        type: 'line',
        data: props.data.map(d => d.value),
        smooth: true,
        symbol: 'circle',
        areaStyle: {}
      }
    ],
    animationDuration: 800
  }
  return { ...base, ...(props.options || {}) }
})
</script>

<style scoped>
.chart-wrap {
  height: 320px;
  position: relative;
}
</style>
