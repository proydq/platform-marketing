# 数据库ER图
**项目名称：** 海外自动化营销系统  
**版本号：** V1.0  
**发布日期：** 2025-08-08  
**撰写人：** 数据架构部  

---

## 1. ER 图（Mermaid）

```mermaid
erDiagram
    CUSTOMER {
        bigint id PK "客户ID"
        string company_name "公司名称"
        string contact_name "联系人"
        string email "邮箱"
        string phone "电话"
        string country "国家"
        string industry "行业"
        datetime created_at "创建时间"
        datetime updated_at "更新时间"
    }

    CUSTOMER_TAG {
        bigint id PK "标签ID"
        bigint customer_id FK "客户ID"
        string tag_name "标签名称"
    }

    SEGMENT {
        bigint id PK "分组ID"
        string name "分组名称"
        string description "分组描述"
        datetime created_at "创建时间"
    }

    SEGMENT_CUSTOMER {
        bigint id PK "记录ID"
        bigint segment_id FK "分组ID"
        bigint customer_id FK "客户ID"
    }

    TEMPLATE {
        bigint id PK "模板ID"
        string name "模板名称"
        text content "模板内容"
        string language "语言"
        datetime created_at "创建时间"
    }

    MESSAGE {
        bigint id PK "消息ID"
        bigint customer_id FK "客户ID"
        bigint template_id FK "模板ID"
        string channel "渠道（email/whatsapp/linkedin）"
        text content "发送内容"
        string status "状态（pending/sent/failed）"
        datetime scheduled_at "计划发送时间"
        datetime sent_at "发送时间"
    }

    TRACK_EVENT {
        bigint id PK "事件ID"
        bigint message_id FK "消息ID"
        string event_type "事件类型（open/click/reply）"
        datetime event_time "事件时间"
    }

    USER {
        bigint id PK "用户ID"
        string username "用户名"
        string password_hash "密码哈希"
        string role "角色"
        datetime created_at "创建时间"
    }

    ROLE_PERMISSION {
        bigint id PK "记录ID"
        string role "角色"
        string permission "权限标识"
    }

    %% 关系定义
    CUSTOMER ||--o{ CUSTOMER_TAG : "有"
    CUSTOMER ||--o{ SEGMENT_CUSTOMER : "属于"
    SEGMENT ||--o{ SEGMENT_CUSTOMER : "包含"
    CUSTOMER ||--o{ MESSAGE : "接收"
    TEMPLATE ||--o{ MESSAGE : "生成"
    MESSAGE ||--o{ TRACK_EVENT : "产生"
    USER ||--o{ ROLE_PERMISSION : "拥有"
```

---

## 2. 模型说明
- **CUSTOMER**：存储客户基础信息，是整个系统的核心数据实体。
- **CUSTOMER_TAG**：多标签机制支持客户画像维度化。
- **SEGMENT** / **SEGMENT_CUSTOMER**：支持客户分组与批量营销策略。
- **TEMPLATE**：存储营销内容模板，支持多语言。
- **MESSAGE**：记录每条发送任务及状态。
- **TRACK_EVENT**：记录消息的用户行为反馈。
- **USER** / **ROLE_PERMISSION**：实现 RBAC 权限控制。

---

## 3. 设计特点
- **多对多关系**：客户与标签、客户与分组均为多对多。
- **可扩展字段**：预留 JSON 字段扩展（如客户自定义属性）。
- **审计字段**：所有表均包含 `created_at`、`updated_at`。
- **外键约束**：保证数据一致性，防止孤立记录。

---