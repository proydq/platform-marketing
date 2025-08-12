# 接口文档（OpenAPI风格）
**项目名称：** 海外自动化营销系统  
**版本号：** V1.0  
**发布日期：** 2025-08-08  
**撰写人：** 技术架构部  

---

## 1. 接口设计原则
- **协议**：HTTP/HTTPS
- **数据格式**：JSON
- **版本控制**：URL 中包含版本号，如 `/api/v1/...`
- **鉴权**：Bearer Token（JWT）  
  请求头示例：
  ```
  Authorization: Bearer <token>
  ```
- **错误码规范**：见《错误码说明表.md》
- **分页规则**：
  - Query 参数：`page`（页码，从1开始）、`page_size`（每页条数）
  - 响应包含 `total`（总记录数）、`pages`（总页数）

---

## 2. 公共响应结构
```json
{
  "code": "0",
  "message": "success",
  "data": {}
}
```
- `code`：`0` 表示成功，其它值表示错误
- `message`：人类可读提示
- `data`：具体数据内容

---

## 3. 接口清单

### 3.1 鉴权模块

#### 3.1.1 用户登录
- **URL**：`POST /api/v1/auth/login`
- **描述**：用户登录获取 Token
- **请求参数**：
```json
{
  "username": "admin",
  "password": "123456"
}
```
- **响应参数**：
```json
{
  "code": "0",
  "message": "success",
  "data": {
    "token": "jwt-token-string",
    "expires_in": 3600
  }
}
```

#### 3.1.2 用户信息
- **URL**：`GET /api/v1/auth/me`
- **描述**：获取当前登录用户信息
- **鉴权**：需要
- **响应参数**：
```json
{
  "code": "0",
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "role": "admin",
    "email": "admin@example.com"
  }
}
```

---

### 3.2 客户管理模块

#### 3.2.1 获取客户列表
- **URL**：`GET /api/v1/customers`
- **描述**：分页获取客户信息
- **参数**：
  - `page`：页码
  - `page_size`：每页数量
  - `keyword`：搜索关键词
- **响应参数**：
```json
{
  "code": "0",
  "message": "success",
  "data": {
    "total": 120,
    "pages": 12,
    "items": [
      {
        "id": 1,
        "company_name": "ABC Ltd",
        "contact_name": "John",
        "email": "info@abc.com",
        "country": "Singapore"
      }
    ]
  }
}
```

#### 3.2.2 创建客户
- **URL**：`POST /api/v1/customers`
- **描述**：新增客户
- **请求参数**：
```json
{
  "company_name": "XYZ Ltd",
  "contact_name": "Alice",
  "email": "alice@xyz.com",
  "country": "USA"
}
```
- **响应参数**：
```json
{
  "code": "0",
  "message": "Customer created successfully",
  "data": { "id": 2 }
}
```

---

### 3.3 模板管理模块

#### 3.3.1 获取模板列表
- **URL**：`GET /api/v1/templates`
- **描述**：分页获取模板
- **参数**：
  - `page`：页码
  - `page_size`：每页数量
  - `channel`：渠道（可选）
- **响应参数**：
```json
{
  "code": "0",
  "message": "success",
  "data": {
    "total": 5,
    "items": [
      {
        "id": 1,
        "name": "Retail Email V1",
        "language": "en",
        "channel": "email"
      }
    ]
  }
}
```

#### 3.3.2 创建模板
- **URL**：`POST /api/v1/templates`
- **描述**：新增模板
- **请求参数**：
```json
{
  "name": "Retail Email V2",
  "language": "en",
  "channel": "email",
  "content": "Hello {{company_name}}, ..."
}
```

---

### 3.4 消息发送模块

#### 3.4.1 创建发送任务
- **URL**：`POST /api/v1/send-tasks`
- **描述**：批量创建发送任务
- **请求参数**：
```json
{
  "name": "Retail Campaign",
  "channel": "email",
  "segment_id": 1,
  "schedule_time": "2025-08-10T10:00:00Z",
  "priority": 5
}
```
- **响应参数**：
```json
{
  "code": "0",
  "message": "Task created",
  "data": { "id": 101 }
}
```

#### 3.4.2 查询任务状态
- **URL**：`GET /api/v1/send-tasks/{id}`
- **描述**：查询任务详情与进度
- **响应参数**：
```json
{
  "code": "0",
  "message": "success",
  "data": {
    "id": 101,
    "name": "Retail Campaign",
    "status": "running",
    "progress": 60
  }
}
```

---

### 3.5 行为跟踪模块

#### 3.5.1 获取事件记录
- **URL**：`GET /api/v1/track-events`
- **描述**：查询行为事件
- **参数**：
  - `event_type`：open/click/reply
  - `message_id`：消息ID
- **响应参数**：
```json
{
  "code": "0",
  "message": "success",
  "data": [
    {
      "id": 1,
      "message_id": 5001,
      "event_type": "open",
      "event_time": "2025-08-08T12:00:00Z"
    }
  ]
}
```

---

## 4. 版本记录
- **V1.0**：定义基础的鉴权、客户管理、模板管理、消息发送、行为跟踪接口。

---