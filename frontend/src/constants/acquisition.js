/**
 * 客户获取模块的常量定义
 */

// 平台选项
export const PLATFORM_OPTIONS = [
  { value: "linkedin", label: "LinkedIn", icon: "🔗", description: "专业商务网络平台" },
  { value: "facebook", label: "Facebook", icon: "📘", description: "全球最大社交网络" },
  { value: "google_maps", label: "Google Maps", icon: "🗺️", description: "本地商家信息平台" },
  { value: "yellow_pages", label: "Yellow Pages", icon: "📞", description: "商业目录服务" },
  { value: "twitter", label: "Twitter", icon: "🐦", description: "实时信息分享平台" },
];

// 任务状态类型映射
export const STATUS_TYPES = {
  pending: "info",
  running: "warning", 
  completed: "success",
  failed: "danger",
  paused: "info",
};

// 任务类型标签映射
export const TYPE_TAG_TYPES = {
  customer: "primary",
  comment: "success",
  article: "warning",
};

// 地区选项
export const REGION_OPTIONS = [
  { value: "global", labelKey: "customer.acquisition.regionGlobal" },
  { value: "us", labelKey: "customer.acquisition.regionUS" },
  { value: "eu", labelKey: "customer.acquisition.regionEU" },
  { value: "asia", labelKey: "customer.acquisition.regionAsia" },
  { value: "china", labelKey: "customer.acquisition.regionChina" },
];

// 优先级选项
export const PRIORITY_OPTIONS = [
  { value: "low", labelKey: "customer.acquisition.priorityLow" },
  { value: "medium", labelKey: "customer.acquisition.priorityMedium" },
  { value: "high", labelKey: "customer.acquisition.priorityHigh" },
  { value: "urgent", labelKey: "customer.acquisition.priorityUrgent" },
];

// 周期选项
export const CYCLE_OPTIONS = [
  { value: "once", labelKey: "customer.acquisition.cycleOnce", descKey: "customer.acquisition.cycleOnceDesc" },
  { value: "daily", labelKey: "customer.acquisition.cycleDaily", descKey: "customer.acquisition.cycleDailyDesc" },
  { value: "weekly", labelKey: "customer.acquisition.cycleWeekly", descKey: "customer.acquisition.cycleWeeklyDesc" },
  { value: "monthly", labelKey: "customer.acquisition.cycleMonthly", descKey: "customer.acquisition.cycleMonthlyDesc" },
];

// 采集字段选项
export const FIELD_OPTIONS = [
  { value: "name", labelKey: "customer.name", icon: "User" },
  { value: "email", labelKey: "customer.email", icon: "Message" },
  { value: "phone", labelKey: "customer.phone", icon: "Phone" },
  { value: "company", labelKey: "customer.company", icon: "OfficeBuilding" },
  { value: "title", labelKey: "customer.acquisition.jobTitle", icon: "Suitcase" },
  { value: "location", labelKey: "customer.acquisition.location", icon: "Location" },
  { value: "social", labelKey: "customer.acquisition.socialProfile", icon: "Link" },
  { value: "website", labelKey: "customer.acquisition.website", icon: "Link" },
];

// 通知选项
export const NOTIFICATION_OPTIONS = [
  { value: "start", labelKey: "customer.acquisition.notifyStart" },
  { value: "complete", labelKey: "customer.acquisition.notifyComplete" },
  { value: "error", labelKey: "customer.acquisition.notifyError" },
  { value: "milestone", labelKey: "customer.acquisition.notifyMilestone" },
];

// 默认表单数据
export const DEFAULT_TASK_DATA = {
  name: "",
  description: "",
  type: "customer",
  priority: "medium", 
  platforms: ["linkedin"],
  keywords: "",
  region: "global",
  amount: 100,
  fields: ["name", "email", "company"],
  filters: "",
  cycle: "once",
  startTime: "",
  endTime: "",
  maxRetries: 3,
  timeout: 60,
  notifications: ["complete", "error"],
  notifyEmail: "",
  webhookUrl: "",
};

// 快速配置默认数据
export const DEFAULT_QUICK_DATA = {
  platforms: ["linkedin"],
  type: "customer", 
  keywords: "",
  region: "global",
  amount: 100,
  fields: ["name", "email", "company"],
  frequency: "once",
  quality: 3,
  filters: "",
};