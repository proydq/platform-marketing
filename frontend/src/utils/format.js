/**
 * 格式化工具函数
 */

/**
 * 格式化数字，添加千分位分隔符
 * @param {number} num - 要格式化的数字
 * @param {number} decimals - 小数位数
 * @returns {string} 格式化后的数字字符串
 */
export function formatNumber(num, decimals = 0) {
  if (num === null || num === undefined || isNaN(num)) {
    return '0'
  }
  
  const number = Number(num)
  return number.toLocaleString('en-US', {
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals
  })
}

/**
 * 格式化百分比
 * @param {number} num - 要格式化的数字（小数形式，如 0.15 表示 15%）
 * @param {number} decimals - 小数位数
 * @returns {string} 格式化后的百分比字符串
 */
export function formatPercentage(num, decimals = 1) {
  if (num === null || num === undefined || isNaN(num)) {
    return '0%'
  }
  
  const percentage = Number(num)
  return `${(percentage * 100).toFixed(decimals)}%`
}

/**
 * 格式化货币
 * @param {number} amount - 金额
 * @param {string} currency - 货币符号
 * @param {number} decimals - 小数位数
 * @returns {string} 格式化后的货币字符串
 */
export function formatCurrency(amount, currency = '$', decimals = 2) {
  if (amount === null || amount === undefined || isNaN(amount)) {
    return `${currency}0.00`
  }
  
  const number = Number(amount)
  const formattedNumber = number.toLocaleString('en-US', {
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals
  })
  
  return `${currency}${formattedNumber}`
}

/**
 * 格式化文件大小
 * @param {number} bytes - 字节数
 * @param {number} decimals - 小数位数
 * @returns {string} 格式化后的文件大小字符串
 */
export function formatFileSize(bytes, decimals = 2) {
  if (bytes === 0 || bytes === null || bytes === undefined) {
    return '0 Bytes'
  }
  
  const k = 1024
  const dm = decimals < 0 ? 0 : decimals
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
  
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i]
}

/**
 * 格式化时长（秒转换为人类可读格式）
 * @param {number} seconds - 秒数
 * @returns {string} 格式化后的时长字符串
 */
export function formatDuration(seconds) {
  if (seconds === null || seconds === undefined || isNaN(seconds)) {
    return '0s'
  }
  
  const sec = Math.floor(Number(seconds))
  
  if (sec < 60) {
    return `${sec}s`
  } else if (sec < 3600) {
    const minutes = Math.floor(sec / 60)
    const remainingSeconds = sec % 60
    return remainingSeconds > 0 ? `${minutes}m ${remainingSeconds}s` : `${minutes}m`
  } else if (sec < 86400) {
    const hours = Math.floor(sec / 3600)
    const minutes = Math.floor((sec % 3600) / 60)
    return minutes > 0 ? `${hours}h ${minutes}m` : `${hours}h`
  } else {
    const days = Math.floor(sec / 86400)
    const hours = Math.floor((sec % 86400) / 3600)
    return hours > 0 ? `${days}d ${hours}h` : `${days}d`
  }
}

/**
 * 格式化数字为紧凑形式（如 1.2K, 1.5M）
 * @param {number} num - 要格式化的数字
 * @param {number} decimals - 小数位数
 * @returns {string} 格式化后的紧凑数字字符串
 */
export function formatCompactNumber(num, decimals = 1) {
  if (num === null || num === undefined || isNaN(num)) {
    return '0'
  }
  
  const number = Number(num)
  
  if (Math.abs(number) < 1000) {
    return number.toString()
  }
  
  const units = ['', 'K', 'M', 'B', 'T']
  const unitIndex = Math.floor(Math.log10(Math.abs(number)) / 3)
  const unitValue = Math.pow(1000, unitIndex)
  const formattedNumber = (number / unitValue).toFixed(decimals)
  
  return `${formattedNumber}${units[unitIndex]}`
}

/**
 * 格式化增长率（带正负号和颜色类）
 * @param {number} rate - 增长率（小数形式）
 * @param {number} decimals - 小数位数
 * @returns {object} 包含文本和CSS类名的对象
 */
export function formatGrowthRate(rate, decimals = 1) {
  if (rate === null || rate === undefined || isNaN(rate)) {
    return {
      text: '0%',
      class: 'neutral'
    }
  }
  
  const percentage = Number(rate) * 100
  const sign = percentage > 0 ? '+' : ''
  const text = `${sign}${percentage.toFixed(decimals)}%`
  
  let className = 'neutral'
  if (percentage > 0) {
    className = 'positive'
  } else if (percentage < 0) {
    className = 'negative'
  }
  
  return {
    text,
    class: className
  }
}

/**
 * 截断文本并添加省略号
 * @param {string} text - 要截断的文本
 * @param {number} maxLength - 最大长度
 * @returns {string} 截断后的文本
 */
export function truncateText(text, maxLength = 50) {
  if (!text || typeof text !== 'string') {
    return ''
  }
  
  if (text.length <= maxLength) {
    return text
  }
  
  return text.substring(0, maxLength - 3) + '...'
}

/**
 * 高亮搜索关键词
 * @param {string} text - 原文本
 * @param {string} keyword - 搜索关键词
 * @returns {string} 高亮后的HTML字符串
 */
export function highlightKeyword(text, keyword) {
  if (!text || !keyword) {
    return text || ''
  }
  
  const regex = new RegExp(`(${keyword})`, 'gi')
  return text.replace(regex, '<mark class="highlight">$1</mark>')
}