<template>
  <div class="growth-trend-chart" :style="{ height: height + 'px' }">
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
  const totalCustomers = data.map(item => item.totalCustomers || item.total || 0)
  const newCustomers = data.map(item => item.newCustomers || item.new || 0)
  const activeCustomers = data.map(item => item.activeCustomers || item.active || 0)

  return {
    title: {
      text: t('customer.analytics.growthTrend'),
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
                  ${param.value.toLocaleString()}
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
        t('customer.analytics.totalCustomers'),
        t('customer.analytics.newCustomers'),
        t('customer.analytics.activeCustomers')
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
      top: '15%',
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
        name: t('customer.analytics.totalCustomers'),
        type: 'line',
        data: totalCustomers,
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: {
          width: 4,
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 1, y2: 0,
            colorStops: [{
              offset: 0, color: '#667eea'
            }, {
              offset: 1, color: '#764ba2'
            }]
          }
        },
        itemStyle: {
          color: '#667eea',
          shadowBlur: 6,
          shadowColor: 'rgba(102, 126, 234, 0.3)'
        },
        areaStyle: {
          opacity: 0.15,
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [{
              offset: 0, color: 'rgba(102, 126, 234, 0.3)'
            }, {
              offset: 1, color: 'rgba(102, 126, 234, 0.05)'
            }]
          }
        }
      },
      {
        name: t('customer.analytics.newCustomers'),
        type: 'line',
        data: newCustomers,
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: {
          width: 4,
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 1, y2: 0,
            colorStops: [{
              offset: 0, color: '#10b981'
            }, {
              offset: 1, color: '#059669'
            }]
          }
        },
        itemStyle: {
          color: '#10b981',
          shadowBlur: 6,
          shadowColor: 'rgba(16, 185, 129, 0.3)'
        },
        areaStyle: {
          opacity: 0.15,
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [{
              offset: 0, color: 'rgba(16, 185, 129, 0.3)'
            }, {
              offset: 1, color: 'rgba(16, 185, 129, 0.05)'
            }]
          }
        }
      },
      {
        name: t('customer.analytics.activeCustomers'),
        type: 'line',
        data: activeCustomers,
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: {
          width: 4,
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 1, y2: 0,
            colorStops: [{
              offset: 0, color: '#f59e0b'
            }, {
              offset: 1, color: '#d97706'
            }]
          }
        },
        itemStyle: {
          color: '#f59e0b',
          shadowBlur: 6,
          shadowColor: 'rgba(245, 158, 11, 0.3)'
        },
        areaStyle: {
          opacity: 0.15,
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [{
              offset: 0, color: 'rgba(245, 158, 11, 0.3)'
            }, {
              offset: 1, color: 'rgba(245, 158, 11, 0.05)'
            }]
          }
        }
      }
    ],
    animationEasing: 'cubicOut',
    animationDuration: 1000
  }
}

const getEmptyChartOption = () => {
  return {
    title: {
      text: t('customer.analytics.growthTrend'),
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

const handleResize = () => {
  if (chart) {
    chart.resize()
  }
}
</script>

<style scoped>
.growth-trend-chart {
  width: 100%;
  position: relative;
  padding: 16px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(248, 250, 252, 0.95) 100%);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  overflow: hidden;
}

.growth-trend-chart::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.02) 0%, rgba(118, 75, 162, 0.02) 100%);
  z-index: -1;
}

.growth-trend-chart:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.12);
}

.chart-container {
  width: 100%;
  height: 100%;
  border-radius: 12px;
  overflow: hidden;
}

/* 动画效果 */
@keyframes fadeInScale {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.growth-trend-chart {
  animation: fadeInScale 0.6s ease-out;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .growth-trend-chart {
    padding: 12px;
    border-radius: 12px;
  }
}

@media (max-width: 480px) {
  .growth-trend-chart {
    padding: 8px;
    border-radius: 10px;
  }
}
</style>