# 营销中心重构命令

## 目标
重构营销中心，整合所有营销相关功能，创建统一的营销管理平台。

## 执行步骤

### 1. 分析现有营销相关文件
分析并整合以下营销功能：
- `frontend/src/views/CampaignCenterView.vue` (营销活动中心)
- `frontend/src/views/EmailMarketingView.vue` (邮件营销)  
- `frontend/src/views/SocialMediaView.vue` (社交媒体营销)
- `frontend/src/api/marketing.js` (营销API接口)
- `frontend/src/api/email.js` (邮件API接口)
- `frontend/src/api/social.js` (社交媒体API接口)

### 2. 创建新的营销模块结构
```
frontend/src/views/marketing/
├── MarketingDashboardView.vue   # 营销总览仪表板
├── CampaignListView.vue         # 营销活动列表
├── CampaignCreateView.vue       # 创建营销活动
├── EmailCampaignView.vue        # 邮件营销活动
├── SocialCampaignView.vue       # 社交媒体营销活动
├── ContentGenerationView.vue    # 内容生成工具
└── components/                  # 营销模块专用组件
    ├── CampaignForm.vue
    ├── EmailTemplate.vue
    ├── SocialPostPreview.vue
    └── ContentEditor.vue
```

### 3. API接口统一
- 制定统一的API命名约定（使用 `/api/v1/marketing/` 前缀）
- 统一响应格式和错误处理
- 整合分散的营销相关API调用：
  ```javascript
  // 统一营销API结构
  /api/v1/marketing/campaigns          # 营销活动
  /api/v1/marketing/emails            # 邮件营销
  /api/v1/marketing/social            # 社交媒体
  /api/v1/marketing/content           # 内容生成
  /api/v1/marketing/analytics         # 营销分析
  ```

### 4. 移除Mock数据依赖
- 识别并移除所有Mock数据引用
- 连接真实的后端API接口
- 实现适当的加载状态和错误处理

### 5. 优化请求处理
- 重构请求拦截器，统一处理认证和错误
- 实现统一的响应数据处理格式
- 添加请求缓存机制以优化性能

### 6. 状态管理优化
创建营销专用的Pinia store：
```javascript
// stores/marketing.js
export const useMarketingStore = defineStore('marketing', {
  state: () => ({
    campaigns: [],
    emailTemplates: [],
    socialAccounts: [],
    analytics: {}
  }),
  actions: {
    // 统一的营销数据管理方法
  }
})
```

## 重构重点

### API统一规范
- **命名约定**: 使用RESTful风格，统一前缀
- **响应格式**: 统一返回结构 `{ code, data, message }`
- **错误处理**: 统一错误码和错误信息格式
- **认证方式**: 统一JWT token处理

### 组件重构
- 提取可复用的营销组件
- 统一组件props和events命名
- 实现组件的懒加载
- 优化组件性能

### 数据流优化
- 使用Pinia进行状态管理
- 实现数据缓存策略
- 优化API请求频率
- 实现乐观更新

## 注意事项
- 保持向后兼容性，确保现有功能正常
- 逐步迁移，避免一次性大改动
- 测试每个功能模块的完整性
- 更新相关文档和注释

## 验证清单
- [ ] 所有营销功能正常工作
- [ ] API接口调用成功
- [ ] 数据加载和保存正常
- [ ] 错误处理机制有效
- [ ] 用户权限控制正确
- [ ] 响应式布局适配
- [ ] 国际化文本正确
- [ ] 性能满足要求