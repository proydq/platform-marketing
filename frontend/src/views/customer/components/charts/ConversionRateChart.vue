<template>
  <div class="conversion-rate-chart" :style="{ height: height + 'px' }">
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
    default: 'line', // line, bar, area
    validator: (value) => ['line', 'bar', 'area'].includes(value)
  },
  showTrend: {
    type: Boolean,
    default: true
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
  const dates = data.map(item => item.date || item.period)
  const leadToProspect = data.map(item => parseFloat((item.leadToProspect || 0).toFixed(2)))
  const prospectToCustomer = data.map(item => parseFloat((item.prospectToCustomer || 0).toFixed(2)))
  const overallConversion = data.map(item => parseFloat((item.overallConversion || 0).toFixed(2)))

  const baseOption = {
    title: {
      text: t('customer.analytics.conversionRate'),
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
        type: 'cross',
        crossStyle: {
          color: '#999'
        }
      },
      formatter: function(params) {
        let tooltipContent = `<div style="margin: 0px 0 0; line-height:1;">
          <div style="margin: 0px 0 0; line-height:1;">
            <div style="font-size:14px; color:#666; font-weight:400; line-height:1;">
              ${params[0].axisValue}
            </div>
          </div>
        `
        
        params.forEach(param => {
          tooltipContent += `
            <div style="margin: 5px 0 0; line-height:1;">
              <div style="margin: 0px 0 0; line-height:1;">
                ${param.marker}
                <span style="font-size:14px; color:#666; font-weight:400; margin-left:2px">
                  ${param.seriesName}
                </span>
                <span style="float:right; margin-left:20px; font-size:14px; color:#666; font-weight:900">
                  ${param.value}%
                </span>
                <div style="clear:both"></div>
              </div>
            </div>
          `
        })
        
        tooltipContent += '</div>'
        return tooltipContent
      }
    },
    legend: {
      data: [
        t('customer.analytics.leadToProspect'),
        t('customer.analytics.prospectToCustomer'),
        t('customer.analytics.overallConversion')
      ],
      top: 30,
      textStyle: {
        color: '#606266'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '8%',
      top: '18%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisPointer: {
        type: 'shadow'
      },
      axisLine: {
        lineStyle: {
          color: '#e4e7ed'
        }
      },
      axisLabel: {
        color: '#606266',
        fontSize: 12
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
        formatter: '{value}%'
      },
      splitLine: {
        lineStyle: {
          color: '#f0f2f5'
        }
      },
      max: function(value) {
        return Math.ceil(value.max / 10) * 10
      }
    },
    animationEasing: 'cubicOut',
    animationDuration: 1000
  }

  if (props.chartType === 'bar') {
    baseOption.series = [
      {
        name: t('customer.analytics.leadToProspect'),
        type: 'bar',
        data: leadToProspect,
        itemStyle: {
          color: '#409eff'
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{c}%',
          fontSize: 12,
          color: '#606266'
        }
      },
      {
        name: t('customer.analytics.prospectToCustomer'),
        type: 'bar',
        data: prospectToCustomer,
        itemStyle: {
          color: '#67c23a'
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{c}%',
          fontSize: 12,
          color: '#606266'
        }
      },
      {
        name: t('customer.analytics.overallConversion'),
        type: 'bar',
        data: overallConversion,
        itemStyle: {
          color: '#e6a23c'
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{c}%',
          fontSize: 12,
          color: '#606266'
        }
      }
    ]
  } else {
    // Line or Area chart
    const isArea = props.chartType === 'area'
    
    baseOption.series = [
      {
        name: t('customer.analytics.leadToProspect'),
        type: 'line',
        data: leadToProspect,
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: {
          width: 3,
          color: '#409eff'
        },
        itemStyle: {
          color: '#409eff'
        },
        areaStyle: isArea ? {
          opacity: 0.2,
          color: '#409eff'
        } : undefined
      },
      {
        name: t('customer.analytics.prospectToCustomer'),
        type: 'line',
        data: prospectToCustomer,
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: {
          width: 3,
          color: '#67c23a'
        },
        itemStyle: {
          color: '#67c23a'
        },
        areaStyle: isArea ? {
          opacity: 0.2,
          color: '#67c23a'
        } : undefined
      },
      {
        name: t('customer.analytics.overallConversion'),
        type: 'line',
        data: overallConversion,
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: {
          width: 3,
          color: '#e6a23c'
        },
        itemStyle: {
          color: '#e6a23c'
        },
        areaStyle: isArea ? {
          opacity: 0.2,
          color: '#e6a23c'
        } : undefined
      }
    ]

    // 添加趋势线
    if (props.showTrend && data.length > 2) {
      const trendData = calculateTrendLine(overallConversion)
      baseOption.series.push({
        name: t('customer.analytics.trendLine'),
        type: 'line',
        data: trendData,
        lineStyle: {
          width: 2,
          color: '#f56c6c',
          type: 'dashed'
        },
        itemStyle: {
          color: '#f56c6c'
        },
        symbol: 'none',
        silent: true,
        animation: false
      })
    }
  }

  // 添加平均线
  const avgConversion = overallConversion.reduce((sum, val) => sum + val, 0) / overallConversion.length
  baseOption.series.push({
    name: t('customer.analytics.averageLine'),
    type: 'line',
    data: Array(dates.length).fill(parseFloat(avgConversion.toFixed(2))),
    lineStyle: {
      width: 1,
      color: '#909399',
      type: 'dotted'
    },
    itemStyle: {
      color: '#909399'
    },
    symbol: 'none',
    silent: true,
    animation: false
  })

  return baseOption
}

const getEmptyChartOption = () => {
  return {
    title: {
      text: t('customer.analytics.conversionRate'),
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

const calculateTrendLine = (data) => {
  const n = data.length
  if (n < 2) return []
  
  let sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0
  
  for (let i = 0; i < n; i++) {
    sumX += i
    sumY += data[i]
    sumXY += i * data[i]
    sumX2 += i * i
  }
  
  const slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX)
  const intercept = (sumY - slope * sumX) / n
  
  return data.map((_, index) => parseFloat((intercept + slope * index).toFixed(2)))
}

const handleResize = () => {
  if (chart) {
    chart.resize()
  }
}
</script>

<style scoped>
.conversion-rate-chart {
  width: 100%;
  position: relative;
}

.chart-container {
  width: 100%;
  height: 100%;
}
</style>