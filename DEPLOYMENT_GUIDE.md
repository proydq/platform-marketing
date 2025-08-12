# 海外自动化营销系统 - 部署配置指南

## 系统概述

本系统是一个完整的海外自动化营销平台，包含以下核心功能：

### ✅ 已实现的功能模块
1. **客户管理系统** - 客户信息管理、获取、分析
2. **AI内容生成** - 基于OpenAI的智能内容生成和多语言翻译
3. **多渠道营销** - 邮件、WhatsApp、LinkedIn集成
4. **数据跟踪分析** - 邮件打开、点击、回复等行为追踪
5. **多语言支持** - 中英文界面，支持10+语言内容生成
6. **权限管理** - 完整的用户角色权限体系
7. **任务调度** - 自动化营销任务执行

## 技术架构

### 后端技术栈
- **框架**: Spring Boot 2.5.14 + Java 9
- **数据库**: MySQL 8.x
- **依赖管理**: Maven
- **API设计**: RESTful API

### 前端技术栈  
- **框架**: Vue 3 + Vite
- **UI组件**: Element Plus
- **状态管理**: Pinia
- **图表库**: ECharts
- **编辑器**: Quill

### 第三方集成
- **AI服务**: OpenAI GPT API
- **消息服务**: WhatsApp Business API
- **社交网络**: LinkedIn API
- **邮件服务**: SMTP (QQ邮箱)

## 环境配置

### 1. 数据库配置

```sql
-- 1. 执行基础数据库初始化
mysql -u root -p < database-init.sql

-- 2. 执行功能增强脚本
mysql -u root -p < database-enhancement.sql
```

### 2. 后端配置

#### 2.1 修改 `application.yml`

```yaml
# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/platform?useSSL=false&serverTimezone=UTC&characterEncoding=utf8
    username: your_db_username
    password: your_db_password

# 邮件配置
  mail:
    host: smtp.gmail.com  # 或其他SMTP服务
    port: 587
    username: your_email@gmail.com
    password: your_app_password
```

#### 2.2 环境变量配置

```bash
# AI服务配置
export OPENAI_API_KEY="your-openai-api-key"
export GOOGLE_TRANSLATE_API_KEY="your-google-translate-key"

# WhatsApp配置
export WHATSAPP_API_TOKEN="your-whatsapp-token"
export WHATSAPP_PHONE_NUMBER_ID="your-phone-number-id"
export WHATSAPP_WEBHOOK_TOKEN="your-webhook-token"

# LinkedIn配置
export LINKEDIN_CLIENT_ID="your-linkedin-client-id"
export LINKEDIN_CLIENT_SECRET="your-linkedin-client-secret"
export LINKEDIN_REDIRECT_URI="http://your-domain.com/api/linkedin/callback"
```

### 3. 前端配置

#### 3.1 安装依赖
```bash
cd frontend
npm install
```

#### 3.2 环境配置
创建 `.env.local` 文件：
```
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_TITLE=海外自动化营销系统
```

## 部署步骤

### 1. 后端部署

```bash
# 1. 编译后端
cd backend
mvn clean package -DskipTests

# 2. 启动应用
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### 2. 前端部署

```bash
# 1. 构建前端
cd frontend  
npm run build

# 2. 部署到Web服务器
# 将 dist/ 目录内容部署到 Nginx 或其他Web服务器
```

### 3. Nginx配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    # 前端静态文件
    location / {
        root /var/www/marketing-system/frontend;
        try_files $uri $uri/ /index.html;
    }
    
    # 后端API代理
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
    
    # 邮件跟踪像素
    location /api/tracking/ {
        proxy_pass http://localhost:8080/api/tracking/;
        add_header Cache-Control "no-cache, no-store, must-revalidate";
    }
}
```

## API密钥获取指南

### 1. OpenAI API
1. 访问 [OpenAI Platform](https://platform.openai.com/)
2. 注册并创建API密钥
3. 设置使用限制和账单信息

### 2. WhatsApp Business API
1. 创建 [Meta for Developers](https://developers.facebook.com/) 账号
2. 申请 WhatsApp Business API 访问权限
3. 配置 Webhook 和验证令牌

### 3. LinkedIn API
1. 在 [LinkedIn Developer Portal](https://developer.linkedin.com/) 创建应用
2. 配置OAuth 2.0重定向URI
3. 申请相应的API权限范围

## 功能测试清单

### ✅ 基础功能测试
- [ ] 用户登录/注册
- [ ] 权限管理功能
- [ ] 客户数据导入/导出
- [ ] 邮件模板管理

### ✅ AI功能测试
- [ ] AI内容生成（需配置OpenAI API）
- [ ] 多语言翻译功能
- [ ] 模板推荐系统

### ✅ 营销渠道测试
- [ ] 邮件发送功能
- [ ] WhatsApp消息发送（需配置API）
- [ ] LinkedIn集成（需配置API）

### ✅ 分析功能测试
- [ ] 邮件打开/点击跟踪
- [ ] 营销活动统计报表
- [ ] 客户行为分析

## 性能优化建议

### 1. 数据库优化
- 定期清理过期的跟踪数据
- 为大表创建分区（按日期）
- 监控慢查询并优化索引

### 2. 缓存策略
```java
// 建议添加Redis缓存
@Cacheable("templates")
public List<MessageTemplate> getRecommendedTemplates(String type, String industry) {
    // ...
}
```

### 3. 异步处理
```java
// 大批量操作使用异步处理
@Async
public CompletableFuture<Void> sendBulkEmails(List<Customer> customers) {
    // ...
}
```

## 监控和日志

### 1. 应用监控
- 使用 Actuator 端点监控应用健康状态
- 配置 Grafana + Prometheus 进行性能监控

### 2. 日志管理
- 配置 Logback 进行结构化日志记录
- 使用 ELK Stack 进行日志分析

### 3. 告警配置
- API调用失败告警
- 数据库连接异常告警
- 第三方服务不可用告警

## 安全配置

### 1. HTTPS配置
```nginx
# 强制HTTPS
server {
    listen 80;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl;
    ssl_certificate /path/to/certificate.crt;
    ssl_certificate_key /path/to/private.key;
    # ...
}
```

### 2. API安全
- 实现JWT token认证
- 配置CORS策略
- 添加请求频率限制

### 3. 数据安全
- 敏感数据加密存储
- 定期备份数据库
- 实施数据访问审计

## 故障排查

### 1. 常见问题
- **数据库连接失败**: 检查连接配置和防火墙设置
- **AI服务超时**: 检查OpenAI API密钥和网络连接
- **WhatsApp消息发送失败**: 验证API token和电话号码格式
- **邮件跟踪不工作**: 检查域名配置和像素图片访问权限

### 2. 日志位置
- 应用日志: `logs/application.log`
- 错误日志: `logs/error.log`
- 访问日志: Nginx access.log

### 3. 健康检查端点
- 应用状态: `GET /actuator/health`
- 数据库连接: `GET /actuator/db`
- 第三方服务: `GET /actuator/integrations`

## 版本升级

### 1. 数据库迁移
```sql
-- 执行增量SQL脚本
source /path/to/migration-v1.1.sql;
```

### 2. 应用更新
```bash
# 备份当前版本
cp backend.jar backend.jar.backup

# 更新应用
java -jar new-backend.jar

# 验证功能
curl -f http://localhost:8080/actuator/health
```

### 3. 回滚计划
- 保留前一版本的jar包和数据库备份
- 制定快速回滚步骤和验证方案

---

## 联系支持

如遇到部署问题，请查看：
1. 项目文档目录中的其他技术文档
2. 系统日志文件
3. API接口文档

祝您部署成功！🚀