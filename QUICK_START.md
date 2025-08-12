# 快速启动指南

## Vue前端项目问题修复

### 问题分析
原项目由于依赖复杂和配置问题导致无法正常启动，现已修复并提供简化版本。

### 解决方案
1. **简化依赖**: 移除了复杂的Element Plus自动导入配置
2. **修复TypeScript配置**: 使用简化的tsconfig.json
3. **创建简化版组件**: 提供基本的登录和仪表盘页面

## 启动步骤

### 1. 启动前端 (简化版)
```bash
cd marketing-frontend
npm install
npm run dev
```

前端将在 http://localhost:3000 启动 (如果端口被占用会自动选择其他端口)

### 2. 默认登录信息
- 用户名: `admin`
- 密码: `admin123`

## 项目结构说明

### 当前可用文件
- `src/App-simple.vue` - 简化版应用组件
- `src/views/Login-simple.vue` - 登录页面
- `src/views/Dashboard-simple.vue` - 仪表盘页面
- `src/router/index-simple.ts` - 简化路由配置
- `src/main-simple.ts` - 简化启动文件

### 完整版文件 (需要修复)
- `src/App.vue` - 完整版应用 (带Element Plus)
- `src/views/Login.vue` - 完整登录页面
- `src/views/Dashboard.vue` - 完整仪表盘
- `src/views/Customers.vue` - 客户管理页面
- `src/views/Templates.vue` - 模板管理页面
- `src/views/Campaigns.vue` - 营销活动页面
- `src/views/Analytics.vue` - 数据分析页面

## 功能验证

### 简化版功能
- ✅ 项目正常启动
- ✅ 基本路由功能
- ✅ 登录验证
- ✅ 页面导航

### 如需使用完整版
需要进一步修复以下问题：
1. Element Plus依赖配置
2. i18n国际化配置
3. ECharts图表依赖
4. Pinia状态管理配置

## 后端启动 (Java)

### 前提条件
1. 安装Java 17+
2. 安装MySQL 8.x并创建数据库
3. 配置application.yml中的数据库连接

### 启动命令
```bash
cd marketing-backend
mvn spring-boot:run
```

后端API将在 http://localhost:8080 启动

## 下一步计划
1. 修复完整版Element Plus集成
2. 恢复所有管理页面功能
3. 集成后端API调用
4. 完善数据可视化图表