/**
 * 营销渠道管理常量
 */

// 默认内置渠道（系统预设，不可删除）
export const BUILT_IN_CHANNELS = [
  {
    id: 'email',
    name: 'Email',
    nameKey: 'channels.email',
    icon: '📧',
    color: '#409EFF',
    category: 'digital',
    description: '电子邮件营销',
    builtin: true,
    enabled: true,
    config: {
      supportsBatch: true,
      supportsTemplate: true,
      supportsTracking: true
    }
  },
  {
    id: 'social',
    name: 'Social Media',
    nameKey: 'channels.social',
    icon: '📱',
    color: '#67C23A',
    category: 'social',
    description: '社交媒体营销',
    builtin: true,
    enabled: true,
    config: {
      supportsBatch: true,
      supportsTemplate: true,
      supportsTracking: true
    }
  },
  {
    id: 'wechat',
    name: 'WeChat',
    nameKey: 'channels.wechat',
    icon: '💬',
    color: '#07C160',
    category: 'social',
    description: '微信营销',
    builtin: true,
    enabled: true,
    config: {
      supportsBatch: true,
      supportsTemplate: true,
      supportsTracking: false
    }
  },
  {
    id: 'sms',
    name: 'SMS',
    nameKey: 'channels.sms',
    icon: '💬',
    color: '#E6A23C',
    category: 'mobile',
    description: '短信营销',
    builtin: true,
    enabled: true,
    config: {
      supportsBatch: true,
      supportsTemplate: true,
      supportsTracking: true
    }
  },
  {
    id: 'push',
    name: 'Push Notification',
    nameKey: 'channels.push',
    icon: '🔔',
    color: '#F56C6C',
    category: 'mobile',
    description: '推送通知',
    builtin: true,
    enabled: true,
    config: {
      supportsBatch: true,
      supportsTemplate: true,
      supportsTracking: true
    }
  },
  {
    id: 'whatsapp',
    name: 'WhatsApp',
    nameKey: 'channels.whatsapp',
    icon: '📞',
    color: '#25D366',
    category: 'messaging',
    description: 'WhatsApp营销',
    builtin: true,
    enabled: true,
    config: {
      supportsBatch: true,
      supportsTemplate: true,
      supportsTracking: false
    }
  }
];

// 渠道分类
export const CHANNEL_CATEGORIES = [
  { value: 'digital', label: 'Digital Marketing', labelKey: 'channels.categories.digital' },
  { value: 'social', label: 'Social Media', labelKey: 'channels.categories.social' },
  { value: 'mobile', label: 'Mobile Marketing', labelKey: 'channels.categories.mobile' },
  { value: 'messaging', label: 'Messaging', labelKey: 'channels.categories.messaging' },
  { value: 'traditional', label: 'Traditional Media', labelKey: 'channels.categories.traditional' },
  { value: 'custom', label: 'Custom', labelKey: 'channels.categories.custom' }
];

// 默认自定义渠道模板
export const CUSTOM_CHANNEL_TEMPLATE = {
  id: null,
  name: '',
  nameKey: null,
  icon: '🎯',
  color: '#909399',
  category: 'custom',
  description: '',
  builtin: false,
  enabled: true,
  config: {
    supportsBatch: true,
    supportsTemplate: true,
    supportsTracking: true
  },
  settings: {
    apiEndpoint: '',
    authMethod: 'none', // none, apikey, oauth
    apiKey: '',
    webhookUrl: '',
    maxBatchSize: 1000
  }
};

// 渠道图标选项
export const CHANNEL_ICONS = [
  '📧', '📱', '💬', '🔔', '📞', '🎯', '📺', '📻', '📰', '🏪',
  '🌐', '💻', '📊', '🎪', '🎨', '🔥', '⚡', '🚀', '💎', '🎁'
];

// 渠道颜色选项
export const CHANNEL_COLORS = [
  '#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399',
  '#07C160', '#25D366', '#1DA1F2', '#E1306C', '#FF6B35',
  '#4267B2', '#FF0000', '#00D4AA', '#8B5CF6', '#F59E0B'
];