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
import { BarChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent, TitleComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { useI18n } from 'vue-i18n'

use([BarChart, GridComponent, TooltipComponent, LegendComponent, TitleComponent, CanvasRenderer])


const props = defineProps({
  title: String,
  data: { type: Array, default: () => [] },
  options: { type: Object, default: () => ({}) },
  horizontal: { type: Boolean, default: false },
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
    xAxis: props.horizontal ? { type: 'value' } : { type: 'category', data: props.data.map(d => d.name) },
    yAxis: props.horizontal ? { type: 'category', data: props.data.map(d => d.name) } : { type: 'value' },
    series: [
      {
        type: 'bar',
        data: props.data.map(d => d.value),
        barWidth: '50%'
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
