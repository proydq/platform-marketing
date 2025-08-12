# 海外自动化营销系统

基于Vue3 + Element Plus + Java Spring Boot开发的海外自动化营销系统。

## 功能特性

### 核心模块
- **数据采集模块**: 支持Excel/CSV导入、API对接、爬虫采集
- **客户管理模块**: 客户信息库、画像标签、分组管理、去重合并
- **内容生成模块**: 模板库、AI智能生成、多语言支持
- **渠道触达模块**: 邮件群发、WhatsApp推送、LinkedIn消息
- **跟踪分析模块**: 行为跟踪、效果统计、A/B测试
- **系统管理模块**: 用户权限、通知提醒、数据备份

### 技术栈
- **前端**: Vue 3 + TypeScript + Element Plus + Pinia + Vue Router
- **后端**: Java 17 + Spring Boot 3.2 + Spring Security + JPA/Hibernate
- **数据库**: MySQL 8.x + Redis
- **工具**: Maven + Vite + ECharts

## 快速开始

### 环境要求
- Node.js 18+
- Java 17+
- MySQL 8.x
- Redis 6.x

### 1. 数据库设置
```sql
CREATE DATABASE marketing_system CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

### 2. 启动后端
```bash
cd marketing-backend
# 配置 application.yml 中的数据库连接
mvn spring-boot:run
```
后端将运行在 http://localhost:8080

### 3. 启动前端
```bash
cd marketing-frontend
npm install
npm run dev
```
前端将运行在 http://localhost:3000

### 4. 默认账号
- 用户名: `admin`
- 密码: `admin123`

## 项目结构

### 前端结构
```
marketing-frontend/
├── src/
│   ├── api/          # API接口
│   ├── components/   # 公共组件
│   ├── views/        # 页面组件
│   ├── stores/       # Pinia状态管理
│   ├── types/        # TypeScript类型定义
│   ├── utils/        # 工具函数
│   └── assets/       # 静态资源
├── public/           # 公共资源
└── package.json      # 依赖配置
```

### 后端结构
```
marketing-backend/
├── src/main/java/com/marketing/
│   ├── controller/   # 控制器
│   ├── service/      # 业务逻辑
│   ├── repository/   # 数据访问
│   ├── entity/       # 实体类
│   ├── dto/          # 数据传输对象
│   ├── config/       # 配置类
│   └── security/     # 安全相关
├── src/main/resources/
│   └── application.yml  # 应用配置
└── pom.xml             # Maven依赖
```

## API文档

### 鉴权接口
- `POST /api/v1/auth/login` - 用户登录
- `GET /api/v1/auth/me` - 获取当前用户信息
- `POST /api/v1/auth/logout` - 用户登出

### 客户管理
- `GET /api/v1/customers` - 获取客户列表
- `POST /api/v1/customers` - 创建客户
- `PUT /api/v1/customers/{id}` - 更新客户
- `DELETE /api/v1/customers/{id}` - 删除客户

### 模板管理
- `GET /api/v1/templates` - 获取模板列表
- `POST /api/v1/templates` - 创建模板
- `PUT /api/v1/templates/{id}` - 更新模板
- `DELETE /api/v1/templates/{id}` - 删除模板

### 营销活动
- `GET /api/v1/send-tasks` - 获取活动列表
- `POST /api/v1/send-tasks` - 创建活动
- `POST /api/v1/send-tasks/{id}/start` - 启动活动
- `POST /api/v1/send-tasks/{id}/pause` - 暂停活动
- `POST /api/v1/send-tasks/{id}/cancel` - 取消活动

## 部署说明

### 生产环境配置
1. 修改 `application-prod.yml` 中的数据库连接配置
2. 配置 SMTP 邮件服务器信息
3. 配置 Redis 连接信息
4. 修改 JWT 密钥
5. 设置 CORS 允许的域名

### Docker部署
```bash
# 构建并启动所有服务
docker-compose up -d
```

## 开发计划

### V1.0 (当前版本)
- ✅ 基础框架搭建
- ✅ 用户认证与授权
- ✅ 客户管理功能
- ✅ 模板管理功能
- ✅ 营销活动管理
- ✅ 数据分析展示

### V1.5 (计划中)
- 🔄 WhatsApp Business API 集成
- 🔄 LinkedIn 自动化脚本
- 🔄 A/B 测试功能
- 🔄 高级数据分析

### V2.0 (未来规划)
- ⏳ AI 智能推荐
- ⏳ 全渠道分析
- ⏳ CRM 系统集成
- ⏳ 多租户支持

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情