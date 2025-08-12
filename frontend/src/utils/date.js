/**
 * 日期时间格式化工具函数
 */

/**
 * 格式化日期时间
 * @param {string|Date|number} date - 日期对象、时间戳或日期字符串
 * @param {string} format - 格式字符串，默认 'YYYY-MM-DD HH:mm:ss'
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) {
    return '-'
  }
  
  let dateObj
  if (typeof date === 'string') {
    dateObj = new Date(date)
  } else if (typeof date === 'number') {
    dateObj = new Date(date)
  } else if (date instanceof Date) {
    dateObj = date
  } else {
    return '-'
  }
  
  if (isNaN(dateObj.getTime())) {
    return '-'
  }
  
  const year = dateObj.getFullYear()
  const month = String(dateObj.getMonth() + 1).padStart(2, '0')
  const day = String(dateObj.getDate()).padStart(2, '0')
  const hours = String(dateObj.getHours()).padStart(2, '0')
  const minutes = String(dateObj.getMinutes()).padStart(2, '0')
  const seconds = String(dateObj.getSeconds()).padStart(2, '0')
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 格式化为相对时间（如：2小时前，3天前）
 * @param {string|Date|number} date - 日期
 * @returns {string} 相对时间字符串
 */
export function formatRelativeTime(date) {
  if (!date) {
    return '-'
  }
  
  let dateObj
  if (typeof date === 'string') {
    dateObj = new Date(date)
  } else if (typeof date === 'number') {
    dateObj = new Date(date)
  } else if (date instanceof Date) {
    dateObj = date
  } else {
    return '-'
  }
  
  if (isNaN(dateObj.getTime())) {
    return '-'
  }
  
  const now = new Date()
  const diffMs = now.getTime() - dateObj.getTime()
  const diffSeconds = Math.floor(diffMs / 1000)
  const diffMinutes = Math.floor(diffSeconds / 60)
  const diffHours = Math.floor(diffMinutes / 60)
  const diffDays = Math.floor(diffHours / 24)
  const diffMonths = Math.floor(diffDays / 30)
  const diffYears = Math.floor(diffDays / 365)
  
  if (diffSeconds < 60) {
    return '刚刚'
  } else if (diffMinutes < 60) {
    return `${diffMinutes}分钟前`
  } else if (diffHours < 24) {
    return `${diffHours}小时前`
  } else if (diffDays < 30) {
    return `${diffDays}天前`
  } else if (diffMonths < 12) {
    return `${diffMonths}个月前`
  } else {
    return `${diffYears}年前`
  }
}

/**
 * 获取日期范围的预设选项
 * @returns {Array} 日期范围选项
 */
export function getDateRangeShortcuts() {
  return [
    {
      text: '今天',
      value: () => {
        const start = new Date()
        start.setHours(0, 0, 0, 0)
        const end = new Date()
        end.setHours(23, 59, 59, 999)
        return [start, end]
      }
    },
    {
      text: '昨天',
      value: () => {
        const start = new Date()
        start.setDate(start.getDate() - 1)
        start.setHours(0, 0, 0, 0)
        const end = new Date()
        end.setDate(end.getDate() - 1)
        end.setHours(23, 59, 59, 999)
        return [start, end]
      }
    },
    {
      text: '最近7天',
      value: () => {
        const end = new Date()
        const start = new Date()
        start.setDate(start.getDate() - 6)
        start.setHours(0, 0, 0, 0)
        end.setHours(23, 59, 59, 999)
        return [start, end]
      }
    },
    {
      text: '最近30天',
      value: () => {
        const end = new Date()
        const start = new Date()
        start.setDate(start.getDate() - 29)
        start.setHours(0, 0, 0, 0)
        end.setHours(23, 59, 59, 999)
        return [start, end]
      }
    },
    {
      text: '本月',
      value: () => {
        const start = new Date()
        start.setDate(1)
        start.setHours(0, 0, 0, 0)
        const end = new Date()
        end.setMonth(end.getMonth() + 1, 0)
        end.setHours(23, 59, 59, 999)
        return [start, end]
      }
    },
    {
      text: '上月',
      value: () => {
        const start = new Date()
        start.setMonth(start.getMonth() - 1, 1)
        start.setHours(0, 0, 0, 0)
        const end = new Date()
        end.setDate(0)
        end.setHours(23, 59, 59, 999)
        return [start, end]
      }
    }
  ]
}

/**
 * 检查日期是否在指定范围内
 * @param {Date} date - 要检查的日期
 * @param {Date} startDate - 开始日期
 * @param {Date} endDate - 结束日期
 * @returns {boolean} 是否在范围内
 */
export function isDateInRange(date, startDate, endDate) {
  if (!date || !startDate || !endDate) {
    return false
  }
  
  const checkDate = new Date(date)
  const start = new Date(startDate)
  const end = new Date(endDate)
  
  return checkDate >= start && checkDate <= end
}

/**
 * 获取两个日期之间的天数差
 * @param {Date} startDate - 开始日期
 * @param {Date} endDate - 结束日期
 * @returns {number} 天数差
 */
export function getDaysDifference(startDate, endDate) {
  if (!startDate || !endDate) {
    return 0
  }
  
  const start = new Date(startDate)
  const end = new Date(endDate)
  const diffTime = Math.abs(end - start)
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  
  return diffDays
}

/**
 * 格式化时间范围
 * @param {Array} dateRange - 日期范围数组 [startDate, endDate]
 * @param {string} format - 格式字符串
 * @returns {string} 格式化后的时间范围字符串
 */
export function formatDateRange(dateRange, format = 'YYYY-MM-DD') {
  if (!dateRange || !Array.isArray(dateRange) || dateRange.length !== 2) {
    return '-'
  }
  
  const [startDate, endDate] = dateRange
  if (!startDate || !endDate) {
    return '-'
  }
  
  const formattedStart = formatDate(startDate, format)
  const formattedEnd = formatDate(endDate, format)
  
  if (formattedStart === formattedEnd) {
    return formattedStart
  }
  
  return `${formattedStart} ~ ${formattedEnd}`
}

/**
 * 获取本周的开始和结束日期
 * @param {Date} date - 基准日期，默认为当前日期
 * @returns {Array} [开始日期, 结束日期]
 */
export function getThisWeekRange(date = new Date()) {
  const today = new Date(date)
  const dayOfWeek = today.getDay()
  const diffToMonday = dayOfWeek === 0 ? -6 : 1 - dayOfWeek
  
  const startOfWeek = new Date(today)
  startOfWeek.setDate(today.getDate() + diffToMonday)
  startOfWeek.setHours(0, 0, 0, 0)
  
  const endOfWeek = new Date(startOfWeek)
  endOfWeek.setDate(startOfWeek.getDate() + 6)
  endOfWeek.setHours(23, 59, 59, 999)
  
  return [startOfWeek, endOfWeek]
}

/**
 * 获取本月的开始和结束日期
 * @param {Date} date - 基准日期，默认为当前日期
 * @returns {Array} [开始日期, 结束日期]
 */
export function getThisMonthRange(date = new Date()) {
  const today = new Date(date)
  
  const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1)
  startOfMonth.setHours(0, 0, 0, 0)
  
  const endOfMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0)
  endOfMonth.setHours(23, 59, 59, 999)
  
  return [startOfMonth, endOfMonth]
}

/**
 * 将UTC时间转换为本地时间
 * @param {string|Date} utcDate - UTC时间
 * @returns {Date} 本地时间
 */
export function utcToLocal(utcDate) {
  if (!utcDate) {
    return null
  }
  
  const date = new Date(utcDate)
  return new Date(date.getTime() - date.getTimezoneOffset() * 60000)
}

/**
 * 将本地时间转换为UTC时间
 * @param {string|Date} localDate - 本地时间
 * @returns {Date} UTC时间
 */
export function localToUtc(localDate) {
  if (!localDate) {
    return null
  }
  
  const date = new Date(localDate)
  return new Date(date.getTime() + date.getTimezoneOffset() * 60000)
}