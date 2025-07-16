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
import { PieChart as Pie, LegendComponent, TooltipComponent, TitleComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { useI18n } from 'vue-i18n'

use([Pie, LegendComponent, TooltipComponent, TitleComponent, CanvasRenderer])

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
    title: props.title ? { text: props.title, left: 'center' } : undefined,
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        data: props.data,
        emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.5)' } }
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
