<template>
  <div class="status-distribution-chart" :style="{ height: height + 'px' }">
    <div ref="chartRef" class="chart-container"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

// Props
const props = defineProps({
  data: {
    type: Array,
    default: () => []
  },
  height: {
    type: Number,
    default: 300
  },
  loading: {
    type: Boolean,
    default: false
  },
  chartType: {
    type: String,
    default: 'doughnut', // pie, doughnut, bar, funnel
    validator: (value) => ['pie', 'doughnut', 'bar', 'funnel'].includes(value)
  }
})

// 响应式数据
const chartRef = ref()
let chart = null

// 生命周期
onMounted(() => {
  nextTick(() => {
    initChart()
  })
})

onUnmounted(() => {
  if (chart) {
    chart.dispose()
    chart = null
  }
})

// 监听器
watch(() => props.data, (newData) => {
  updateChart(newData)
}, { deep: true })

watch(() => props.loading, (newLoading) => {
  if (chart) {
    if (newLoading) {
      chart.showLoading({
        text: t('common.loading'),
        color: '#409eff',
        textColor: '#000',
        maskColor: 'rgba(255, 255, 255, 0.8)',
        zlevel: 0
      })
    } else {
      chart.hideLoading()
    }
  }
})

watch(() => props.chartType, () => {
  updateChart(props.data)
})

// 方法
const initChart = () => {
  if (!chartRef.value) return

  chart = echarts.init(chartRef.value)
  
  // 初始配置
  const option = getChartOption(props.data)
  chart.setOption(option)

  // 响应式调整
  window.addEventListener('resize', handleResize)
  
  // 初始加载状态
  if (props.loading) {
    chart.showLoading()
  }
}

const updateChart = (data) => {
  if (!chart) return
  
  const option = getChartOption(data)
  chart.setOption(option, true)
}

const getChartOption = (data) => {
  if (!data || data.length === 0) {
    return getEmptyChartOption()
  }

  // 处理数据
  const processedData = data.map((item, index) => ({
    name: getStatusLabel(item.status || item.name || `Status ${index + 1}`),
    value: item.value || item.count || 0,
    status: item.status || item.name
  }))

  // 计算总数
  const total = processedData.reduce((sum, item) => sum + item.value, 0)

  // 状态颜色配置
  const statusColors = {
    'active': '#67c23a',
    'inactive': '#909399', 
    'pending': '#e6a23c',
    'prospect': '#409eff',
    'customer': '#f56c6c',
    'lead': '#5470c6',
    'qualified': '#91cc75',
    'converted': '#73c0de'
  }

  const colors = processedData.map(item => 
    statusColors[item.status] || statusColors[item.status?.toLowerCase()] || '#c9cdd4'
  )

  if (props.chartType === 'bar') {
    return getBarChartOption(processedData, colors, total)
  } else if (props.chartType === 'funnel') {
    return getFunnelChartOption(processedData, colors, total)
  } else {
    return getPieChartOption(processedData, colors, total)
  }
}

const getPieChartOption = (data, colors, total) => {
  return {
    title: {
      text: t('customer.analytics.statusDistribution'),
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold',
        color: '#303133'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: function(params) {
        const percentage = ((params.value / total) * 100).toFixed(1)
        return `
          <div style="margin: 0px 0 0; line-height:1;">
            <div style="margin: 0px 0 0; line-height:1;">
              ${params.marker}
              <span style="font-size:14px; color:#666; font-weight:400; margin-left:2px">
                ${params.name}
              </span>
              <br/>
              <span style="font-size:14px; color:#666; font-weight:400; margin-left:2px">
                ${t('customer.analytics.customerCount')}: ${params.value.toLocaleString()}
              </span>
              <br/>
              <span style="font-size:14px; color:#666; font-weight:400; margin-left:2px">
                ${t('customer.analytics.percentage')}: ${percentage}%
              </span>
            </div>
          </div>
        `
      }
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      textStyle: {
        color: '#606266',
        fontSize: 12
      },
      formatter: function(name) {
        const item = data.find(d => d.name === name)
        if (item) {
          const percentage = ((item.value / total) * 100).toFixed(1)
          return `${name} (${percentage}%)`
        }
        return name
      }
    },
    color: colors,
    series: [
      {
        name: t('customer.analytics.statusDistribution'),
        type: 'pie',
        radius: props.chartType === 'doughnut' ? ['40%', '70%'] : '60%',
        center: ['35%', '50%'],
        data: data,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        label: {
          show: props.chartType !== 'doughnut',
          position: 'outside',
          formatter: function(params) {
            const percentage = ((params.value / total) * 100).toFixed(1)
            return `${params.name}\n${percentage}%`
          },
          fontSize: 12,
          color: '#606266'
        },
        labelLine: {
          show: props.chartType !== 'doughnut',
          length: 15,
          length2: 10,
          lineStyle: {
            color: '#e4e7ed'
          }
        }
      }
    ],
    animationEasing: 'cubicOut',
    animationDuration: 1000
  }
}

const getBarChartOption = (data, colors, total) => {
  return {
    title: {
      text: t('customer.analytics.statusDistribution'),
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold',
        color: '#303133'
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: function(params) {
        const param = params[0]
        const percentage = ((param.value / total) * 100).toFixed(1)
        return `
          <div style="margin: 0px 0 0; line-height:1;">
            <div style="margin: 0px 0 0; line-height:1;">
              <div style="font-size:14px; color:#666; font-weight:400; line-height:1;">
                ${param.name}
              </div>
              <div style="margin: 5px 0 0; line-height:1;">
                ${param.marker}
                <span style="font-size:14px; color:#666; font-weight:400; margin-left:2px">
                  ${t('customer.analytics.customerCount')}: ${param.value.toLocaleString()}
                </span>
                <br/>
                <span style="font-size:14px; color:#666; font-weight:400; margin-left:2px">
                  ${t('customer.analytics.percentage')}: ${percentage}%
                </span>
              </div>
            </div>
          </div>
        `
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '8%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.name),
      axisLine: {
        lineStyle: {
          color: '#e4e7ed'
        }
      },
      axisLabel: {
        color: '#606266',
        fontSize: 12,
        interval: 0,
        rotate: data.length > 5 ? 30 : 0
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        lineStyle: {
          color: '#e4e7ed'
        }
      },
      axisLabel: {
        color: '#606266',
        fontSize: 12,
        formatter: function(value) {
          if (value >= 1000000) {
            return (value / 1000000).toFixed(1) + 'M'
          } else if (value >= 1000) {
            return (value / 1000).toFixed(1) + 'K'
          }
          return value
        }
      },
      splitLine: {
        lineStyle: {
          color: '#f0f2f5'
        }
      }
    },
    series: [
      {
        name: t('customer.analytics.customerCount'),
        type: 'bar',
        data: data.map((item, index) => ({
          value: item.value,
          itemStyle: {
            color: colors[index]
          }
        })),
        barWidth: '60%',
        label: {
          show: true,
          position: 'top',
          formatter: function(params) {
            const percentage = ((params.value / total) * 100).toFixed(1)
            return `${percentage}%`
          },
          fontSize: 12,
          color: '#606266'
        }
      }
    ],
    animationEasing: 'cubicOut',
    animationDuration: 1000
  }
}

const getFunnelChartOption = (data, colors, total) => {
  // 漏斗图数据需要按值降序排列
  const sortedData = [...data].sort((a, b) => b.value - a.value)
  
  return {
    title: {
      text: t('customer.analytics.statusDistribution'),
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold',
        color: '#303133'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: function(params) {
        const percentage = ((params.value / total) * 100).toFixed(1)
        const conversionRate = params.dataIndex === 0 ? 100 : 
          ((params.value / sortedData[params.dataIndex - 1].value) * 100).toFixed(1)
        
        return `
          <div style="margin: 0px 0 0; line-height:1;">
            <div style="margin: 0px 0 0; line-height:1;">
              ${params.marker}
              <span style="font-size:14px; color:#666; font-weight:400; margin-left:2px">
                ${params.name}
              </span>
              <br/>
              <span style="font-size:14px; color:#666; font-weight:400; margin-left:2px">
                ${t('customer.analytics.customerCount')}: ${params.value.toLocaleString()}
              </span>
              <br/>
              <span style="font-size:14px; color:#666; font-weight:400; margin-left:2px">
                ${t('customer.analytics.percentage')}: ${percentage}%
              </span>
              <br/>
              <span style="font-size:14px; color:#666; font-weight:400; margin-left:2px">
                ${t('customer.analytics.conversionRate')}: ${conversionRate}%
              </span>
            </div>
          </div>
        `
      }
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      textStyle: {
        color: '#606266',
        fontSize: 12
      }
    },
    color: colors,
    series: [
      {
        name: t('customer.analytics.statusDistribution'),
        type: 'funnel',
        left: '10%',
        width: '60%',
        maxSize: '80%',
        label: {
          show: true,
          position: 'inside',
          formatter: function(params) {
            const percentage = ((params.value / total) * 100).toFixed(1)
            return `${params.name}\n${params.value}\n(${percentage}%)`
          },
          fontSize: 12,
          color: '#fff'
        },
        itemStyle: {
          borderColor: '#fff',
          borderWidth: 2
        },
        emphasis: {
          label: {
            fontSize: 14
          }
        },
        data: sortedData
      }
    ],
    animationEasing: 'cubicOut',
    animationDuration: 1000
  }
}

const getEmptyChartOption = () => {
  return {
    title: {
      text: t('customer.analytics.statusDistribution'),
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold',
        color: '#303133'
      }
    },
    graphic: {
      type: 'text',
      left: 'center',
      top: 'middle',
      style: {
        text: t('common.noData'),
        fontSize: 14,
        fill: '#909399'
      }
    }
  }
}

const getStatusLabel = (status) => {
  const statusLabels = {
    'active': t('customer.status.active'),
    'inactive': t('customer.status.inactive'),
    'pending': t('customer.status.pending'),
    'prospect': t('customer.status.prospect'),
    'customer': t('customer.status.customer'),
    'lead': t('customer.status.lead'),
    'qualified': t('customer.status.qualified'),
    'converted': t('customer.status.converted')
  }
  
  return statusLabels[status] || statusLabels[status?.toLowerCase()] || status
}

const handleResize = () => {
  if (chart) {
    chart.resize()
  }
}
</script>

<style scoped>
.status-distribution-chart {
  width: 100%;
  position: relative;
}

.chart-container {
  width: 100%;
  height: 100%;
}
</style>