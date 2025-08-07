# 客户模块重构命令

## 目标
重构客户管理模块，整合客户获取和客户管理功能，创建统一的客户管理体验。

## 执行步骤

### 1. 分析现有结构
分析现有的客户相关文件：
- `frontend/src/views/CustomerCrawlView.vue` (客户获取页面)
- `frontend/src/views/CustomerManageView.vue` (客户管理页面)
- `frontend/src/api/customer.js` (客户API接口)
- `frontend/src/components/customer/` (客户相关组件)

### 2. 创建新的客户模块结构
```
frontend/src/views/customer/
├── CustomerListView.vue         # 主列表页面（整合原管理功能）
├── CustomerAcquisitionView.vue  # 客户获取页面（重构原爬取功能）
├── CustomerAnalyticsView.vue    # 客户分析页面（新增）
└── components/                  # 客户模块专用组件
    ├── CustomerForm.vue
    ├── CustomerTable.vue
    └── AcquisitionSettings.vue
```

### 3. API接口整合
- 统一客户相关API的命名规范
- 整合客户获取和管理的API调用
- 优化数据结构和响应格式

### 4. 更新路由配置
- 重新组织客户模块的路由结构
- 实现嵌套路由以支持子功能
- 更新导航菜单配置

### 5. 数据流优化
- 使用Pinia存储客户状态
- 实现客户数据的缓存机制
- 优化组件间的数据传递

## 注意事项
- 保持现有功能的完整性，只进行结构重组
- 确保所有API调用正常工作
- 更新相关的国际化配置
- 测试权限控制是否正常

## 验证清单
- [ ] 客户列表正常显示
- [ ] 客户获取功能正常
- [ ] 客户编辑/删除功能正常
- [ ] 路由跳转正确
- [ ] 权限控制有效
- [ ] 国际化文本正确