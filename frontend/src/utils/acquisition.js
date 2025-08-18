/**
 * 客户获取模块的工具函数
 */

import { useI18n } from 'vue-i18n';
import { 
  PLATFORM_OPTIONS, 
  STATUS_TYPES, 
  TYPE_TAG_TYPES,
  REGION_OPTIONS,
  PRIORITY_OPTIONS,
  CYCLE_OPTIONS
} from '@/constants/acquisition';

/**
 * 获取平台标签显示文本
 */
export function getPlatformLabel(platform) {
  const option = PLATFORM_OPTIONS.find(p => p.value === platform);
  return option ? `${option.icon} ${option.label}` : platform;
}

/**
 * 获取平台数组（处理字符串和数组格式）
 */
export function getPlatformArray(platform) {
  if (Array.isArray(platform)) return platform;
  return platform ? platform.split(",").filter(Boolean) : [];
}

/**
 * 获取状态对应的Element Plus类型
 */
export function getStatusType(status) {
  return STATUS_TYPES[status] || "info";
}

/**
 * 获取状态显示文本（需要i18n上下文）
 */
export function getStatusLabel(status) {
  const { t } = useI18n();
  const labels = {
    pending: t("customer.acquisition.statusPending"),
    running: t("customer.acquisition.statusRunning"), 
    completed: t("customer.acquisition.statusCompleted"),
    failed: t("customer.acquisition.statusFailed"),
    paused: t("customer.acquisition.statusPaused"),
  };
  return labels[status] || status;
}

/**
 * 获取任务类型标签类型
 */
export function getTypeTagType(type) {
  return TYPE_TAG_TYPES[type] || "info";
}

/**
 * 获取任务类型显示文本
 */
export function getTypeLabel(type) {
  const { t } = useI18n();
  const types = {
    customer: t("customer.acquisition.typeCustomer"),
    comment: t("customer.acquisition.typeComment"),
    article: t("customer.acquisition.typeArticle"),
  };
  return types[type] || type;
}

/**
 * 获取周期显示文本
 */
export function getCycleLabel(cycle) {
  const { t } = useI18n();
  const labels = {
    once: t("customer.acquisition.cycleOnce"),
    daily: t("customer.acquisition.cycleDaily"),
    weekly: t("customer.acquisition.cycleWeekly"),
    monthly: t("customer.acquisition.cycleMonthly"),
  };
  return labels[cycle] || cycle;
}

/**
 * 获取进度条状态
 */
export function getProgressStatus(status) {
  if (status === 'completed') return 'success';
  if (status === 'failed') return 'exception';
  if (status === 'running') return undefined; // 默认进度条
  return 'exception';
}

/**
 * 检查是否为本地任务
 */
export function isLocalTask(taskId) {
  return taskId?.startsWith('demo-') || 
         taskId?.startsWith('task-') || 
         taskId?.startsWith('quick-');
}

/**
 * 生成任务名称
 */
export function generateTaskName(config) {
  const { t } = useI18n();
  
  const platformNames = config.platforms
    .map(p => {
      const option = PLATFORM_OPTIONS.find(opt => opt.value === p);
      return option ? option.label : p;
    })
    .join("+");

  const typeNames = {
    customer: t("customer.acquisition.typeCustomer"),
    comment: t("customer.acquisition.typeComment"), 
    article: t("customer.acquisition.typeArticle"),
  };

  return `${platformNames}_${typeNames[config.type] || config.type}_${config.amount}`;
}

/**
 * 为后端API准备数据格式
 */
export function prepareApiData(taskData) {
  return {
    ...taskData,
    // 确保数组格式
    platforms: Array.isArray(taskData.platforms) 
      ? taskData.platforms 
      : (taskData.platforms ? taskData.platforms.split(',').filter(Boolean) : []),
    fields: Array.isArray(taskData.fields)
      ? taskData.fields
      : (taskData.fields ? taskData.fields.split(',').filter(Boolean) : []),
    // 枚举值转换为大写
    type: taskData.type?.toUpperCase(),
    cycle: taskData.cycle?.toUpperCase(),
    frequency: taskData.frequency?.toUpperCase(),
    priority: taskData.priority?.toUpperCase(),
    // 移除空值
    ...Object.fromEntries(
      Object.entries(taskData).filter(([_, value]) => 
        value !== null && value !== undefined && value !== ''
      )
    )
  };
}

/**
 * 验证任务数据
 */
export function validateTaskData(taskData) {
  const errors = [];
  
  // 基本验证
  if (!taskData.name || taskData.name.trim().length < 2) {
    errors.push('任务名称至少需要2个字符');
  }
  
  if (!taskData.platforms || taskData.platforms.length === 0) {
    errors.push('请选择至少一个平台');
  }
  
  if (!taskData.keywords || taskData.keywords.trim().length < 2) {
    errors.push('请输入关键词');
  }
  
  if (!taskData.fields || taskData.fields.length === 0) {
    errors.push('请选择至少一个采集字段');
  }
  
  // 业务逻辑验证
  const keywords = taskData.keywords?.split(',').map(k => k.trim()).filter(Boolean) || [];
  if (keywords.length > 20) {
    errors.push('关键词数量不能超过20个');
  }
  
  if (keywords.some(k => k.length > 50)) {
    errors.push('单个关键词长度不能超过50个字符');
  }
  
  if (taskData.amount && (taskData.amount < 1 || taskData.amount > 50000)) {
    errors.push('目标数量必须在1-50000之间');
  }
  
  return errors;
}

/**
 * 检查用户权限（示例实现）
 */
export function hasPermission(permission) {
  // 这里应该根据实际的权限系统实现
  const userPermissions = JSON.parse(localStorage.getItem('userPermissions') || '[]');
  return userPermissions.includes(permission) || userPermissions.includes('admin');
}

/**
 * 安全地获取对象属性值
 */
export function safeGet(obj, path, defaultValue = '') {
  const keys = path.split('.');
  let result = obj;
  
  for (const key of keys) {
    if (result === null || result === undefined) {
      return defaultValue;
    }
    result = result[key];
  }
  
  return result !== null && result !== undefined ? result : defaultValue;
}

/**
 * HTML转义，防止XSS
 */
export function escapeHtml(unsafe) {
  if (typeof unsafe !== 'string') return unsafe;
  
  return unsafe
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}

/**
 * 格式化百分比（安全版本）
 */
export function formatPercentage(num, decimals = 1) {
  const percentage = Number(num);
  if (isNaN(percentage)) return '0%';
  
  return `${(percentage * 100).toFixed(decimals)}%`;
}

/**
 * 防抖函数
 */
export function debounce(func, wait, immediate = false) {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      timeout = null;
      if (!immediate) func.apply(this, args);
    };
    const callNow = immediate && !timeout;
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
    if (callNow) func.apply(this, args);
  };
}