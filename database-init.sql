-- Platform Marketing System Database Initialization Script
-- 海外营销系统数据库初始化脚本

-- Set character set and collation
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ========== 基础表结构 ==========

-- 用户表 (如果不存在)
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` varchar(36) NOT NULL PRIMARY KEY,
    `username` varchar(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` varchar(255) NOT NULL COMMENT '密码',
    `email` varchar(100) COMMENT '邮箱',
    `phone` varchar(20) COMMENT '电话',
    `real_name` varchar(50) COMMENT '真实姓名',
    `avatar` varchar(255) COMMENT '头像URL',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_username` (`username`),
    INDEX `idx_email` (`email`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- 角色表 (如果不存在)
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id` varchar(36) NOT NULL PRIMARY KEY,
    `name` varchar(50) NOT NULL COMMENT '角色名称',
    `code` varchar(50) NOT NULL UNIQUE COMMENT '角色编码',
    `description` varchar(255) COMMENT '描述',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_code` (`code`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';

-- 权限表 (如果不存在)
CREATE TABLE IF NOT EXISTS `sys_permission` (
    `id` varchar(36) NOT NULL PRIMARY KEY,
    `name` varchar(100) NOT NULL COMMENT '权限名称',
    `code` varchar(100) NOT NULL UNIQUE COMMENT '权限编码',
    `type` varchar(20) NOT NULL COMMENT '权限类型',
    `module` varchar(255) DEFAULT NULL COMMENT '模块',
    `description` varchar(255) COMMENT '描述',
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_code` (`code`),
    INDEX `idx_type` (`type`),
    INDEX `idx_module` (`module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统权限表';

-- 菜单表 (如果不存在)
CREATE TABLE IF NOT EXISTS `sys_menu` (
    `id` varchar(36) NOT NULL PRIMARY KEY,
    `parent_id` varchar(36) DEFAULT NULL COMMENT '父级ID',
    `name` varchar(50) NOT NULL COMMENT '菜单名称',
    `path` varchar(200) COMMENT '路由路径',
    `component` varchar(200) COMMENT '组件路径',
    `icon` varchar(100) COMMENT '图标',
    `sort_order` int DEFAULT 0 COMMENT '排序',
    `type` tinyint DEFAULT 0 COMMENT '类型 0-目录 1-菜单 2-按钮',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_parent_id` (`parent_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统菜单表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `user_id` varchar(36) NOT NULL,
    `role_id` varchar(36) NOT NULL,
    PRIMARY KEY (`user_id`, `role_id`),
    CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
    `role_id` varchar(36) NOT NULL,
    `permission_id` varchar(36) NOT NULL,
    PRIMARY KEY (`role_id`, `permission_id`),
    CONSTRAINT `fk_role_permission_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_role_permission_permission` FOREIGN KEY (`permission_id`) REFERENCES `sys_permission` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
    `role_id` varchar(36) NOT NULL,
    `menu_id` varchar(36) NOT NULL,
    PRIMARY KEY (`role_id`, `menu_id`),
    CONSTRAINT `fk_role_menu_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_role_menu_menu` FOREIGN KEY (`menu_id`) REFERENCES `sys_menu` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

-- ========== 客户管理相关表 ==========

-- 客户表
CREATE TABLE IF NOT EXISTS `customer` (
    `id` varchar(36) NOT NULL PRIMARY KEY,
    `name` varchar(100) NOT NULL COMMENT '客户姓名',
    `email` varchar(255) COMMENT '邮箱',
    `phone` varchar(50) COMMENT '电话',
    `company` varchar(255) COMMENT '公司名称',
    `title` varchar(100) COMMENT '职位',
    `industry` varchar(100) COMMENT '行业',
    `location` varchar(255) COMMENT '地址',
    `website` varchar(500) COMMENT '网站',
    `social_profile` text COMMENT '社交资料 JSON',
    `source` varchar(100) COMMENT '来源',
    `status` varchar(50) DEFAULT 'active' COMMENT '状态: active, inactive, pending, prospect, customer',
    `tags` varchar(500) COMMENT '标签，逗号分隔',
    `notes` text COMMENT '备注',
    `created_by` varchar(36) COMMENT '创建人ID',
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_name` (`name`),
    INDEX `idx_email` (`email`),
    INDEX `idx_company` (`company`),
    INDEX `idx_source` (`source`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_by` (`created_by`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户信息表';

-- 客户获取任务表
CREATE TABLE IF NOT EXISTS `customer_acquisition_task` (
    `id` varchar(36) NOT NULL PRIMARY KEY,
    `name` varchar(255) NOT NULL COMMENT '任务名称',
    `type` varchar(50) NOT NULL COMMENT '采集类型: customer, comment, article',
    `platforms` varchar(500) NOT NULL COMMENT '平台列表，逗号分隔',
    `keywords` text COMMENT '关键词',
    `region` varchar(50) COMMENT '地区',
    `amount` int DEFAULT 100 COMMENT '目标数量',
    `fields` varchar(500) COMMENT '采集字段，逗号分隔',
    `frequency` varchar(50) DEFAULT 'once' COMMENT '执行频率: once, daily, weekly, monthly',
    `quality` int DEFAULT 3 COMMENT '质量要求 1-5',
    `filters` text COMMENT '过滤条件',
    `status` varchar(50) DEFAULT 'pending' COMMENT '状态: pending, running, completed, failed, paused',
    `progress` decimal(5,2) DEFAULT 0.00 COMMENT '进度百分比',
    `collected_count` int DEFAULT 0 COMMENT '已采集数量',
    `valid_count` int DEFAULT 0 COMMENT '有效数量',
    `success_rate` decimal(5,2) DEFAULT 0.00 COMMENT '成功率',
    `error_message` text COMMENT '错误信息',
    `config` json COMMENT '任务配置 JSON',
    `created_by` varchar(36) COMMENT '创建人ID',
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `started_at` timestamp NULL COMMENT '开始时间',
    `completed_at` timestamp NULL COMMENT '完成时间',
    `next_run_time` timestamp NULL COMMENT '下次运行时间（用于定时任务）',
    INDEX `idx_created_by` (`created_by`),
    INDEX `idx_status` (`status`),
    INDEX `idx_type` (`type`),
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_next_run_time` (`next_run_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户获取任务表';

-- 客户获取结果表
CREATE TABLE IF NOT EXISTS `customer_acquisition_result` (
    `id` varchar(36) NOT NULL PRIMARY KEY,
    `task_id` varchar(36) NOT NULL COMMENT '任务ID',
    `platform` varchar(50) NOT NULL COMMENT '来源平台',
    `name` varchar(255) COMMENT '客户姓名',
    `email` varchar(255) COMMENT '邮箱',
    `phone` varchar(255) COMMENT '电话',
    `company` varchar(255) COMMENT '公司',
    `title` varchar(255) COMMENT '职位',
    `location` varchar(255) COMMENT '地址',
    `website` varchar(500) COMMENT '网站',
    `social_profile` varchar(500) COMMENT '社交资料',
    `industry` varchar(255) COMMENT '行业',
    `company_size` varchar(100) COMMENT '公司规模',
    `description` text COMMENT '描述',
    `raw_data` json COMMENT '原始数据',
    `status` varchar(50) DEFAULT 'pending' COMMENT '状态: pending, valid, invalid, duplicate',
    `quality_score` decimal(3,2) DEFAULT 0.00 COMMENT '数据质量评分',
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_task_id` (`task_id`),
    INDEX `idx_platform` (`platform`),
    INDEX `idx_status` (`status`),
    INDEX `idx_quality_score` (`quality_score`),
    INDEX `idx_created_at` (`created_at`),
    CONSTRAINT `fk_acquisition_result_task` FOREIGN KEY (`task_id`) REFERENCES `customer_acquisition_task` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户获取结果表';

-- 客户获取执行日志表
CREATE TABLE IF NOT EXISTS `customer_acquisition_log` (
    `id` varchar(36) NOT NULL PRIMARY KEY,
    `task_id` varchar(36) NOT NULL COMMENT '任务ID',
    `type` varchar(50) NOT NULL COMMENT '日志类型: start, progress, success, warning, error',
    `message` varchar(1000) NOT NULL COMMENT '日志消息',
    `details` text COMMENT '详细信息',
    `data` json COMMENT '相关数据',
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_task_id` (`task_id`),
    INDEX `idx_type` (`type`),
    INDEX `idx_created_at` (`created_at`),
    CONSTRAINT `fk_acquisition_log_task` FOREIGN KEY (`task_id`) REFERENCES `customer_acquisition_task` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户获取执行日志表';

-- ========== 营销活动相关表 ==========

-- 营销活动表
CREATE TABLE IF NOT EXISTS `marketing_campaign` (
    `id` varchar(36) NOT NULL PRIMARY KEY,
    `name` varchar(255) NOT NULL COMMENT '活动名称',
    `type` varchar(50) NOT NULL COMMENT '活动类型: email, social, ads',
    `status` varchar(50) DEFAULT 'draft' COMMENT '状态: draft, active, paused, completed, cancelled',
    `description` text COMMENT '活动描述',
    `target_audience` text COMMENT '目标受众',
    `start_date` datetime COMMENT '开始时间',
    `end_date` datetime COMMENT '结束时间',
    `budget` decimal(10,2) DEFAULT 0.00 COMMENT '预算',
    `spent` decimal(10,2) DEFAULT 0.00 COMMENT '已花费',
    `impressions` int DEFAULT 0 COMMENT '展示次数',
    `clicks` int DEFAULT 0 COMMENT '点击次数',
    `conversions` int DEFAULT 0 COMMENT '转化次数',
    `config` json COMMENT '活动配置',
    `created_by` varchar(36) COMMENT '创建人ID',
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_type` (`type`),
    INDEX `idx_status` (`status`),
    INDEX `idx_start_date` (`start_date`),
    INDEX `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='营销活动表';

-- ========== 邮件营销相关表 ==========

-- 邮件模板表
CREATE TABLE IF NOT EXISTS `email_template` (
    `id` varchar(36) NOT NULL PRIMARY KEY,
    `name` varchar(255) NOT NULL COMMENT '模板名称',
    `subject` varchar(500) NOT NULL COMMENT '邮件主题',
    `content` longtext NOT NULL COMMENT '邮件内容',
    `type` varchar(50) DEFAULT 'marketing' COMMENT '类型: marketing, transactional, newsletter',
    `status` varchar(50) DEFAULT 'active' COMMENT '状态: active, inactive',
    `tags` varchar(500) COMMENT '标签',
    `created_by` varchar(36) COMMENT '创建人ID',
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_type` (`type`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮件模板表';

-- 邮件发送记录表
CREATE TABLE IF NOT EXISTS `email_send_record` (
    `id` varchar(36) NOT NULL PRIMARY KEY,
    `campaign_id` varchar(36) COMMENT '活动ID',
    `template_id` varchar(36) COMMENT '模板ID',
    `recipient_email` varchar(255) NOT NULL COMMENT '收件人邮箱',
    `recipient_name` varchar(255) COMMENT '收件人姓名',
    `subject` varchar(500) NOT NULL COMMENT '邮件主题',
    `content` longtext COMMENT '邮件内容',
    `status` varchar(50) DEFAULT 'pending' COMMENT '状态: pending, sent, delivered, bounced, failed',
    `sent_at` timestamp NULL COMMENT '发送时间',
    `delivered_at` timestamp NULL COMMENT '送达时间',
    `opened_at` timestamp NULL COMMENT '打开时间',
    `clicked_at` timestamp NULL COMMENT '点击时间',
    `error_message` text COMMENT '错误信息',
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_campaign_id` (`campaign_id`),
    INDEX `idx_template_id` (`template_id`),
    INDEX `idx_recipient_email` (`recipient_email`),
    INDEX `idx_status` (`status`),
    INDEX `idx_sent_at` (`sent_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮件发送记录表';

-- ========== 社交媒体相关表 ==========

-- 社交账户表
CREATE TABLE IF NOT EXISTS `social_account` (
    `id` varchar(36) NOT NULL PRIMARY KEY,
    `platform` varchar(50) NOT NULL COMMENT '平台: facebook, twitter, linkedin, instagram',
    `account_name` varchar(255) NOT NULL COMMENT '账户名称',
    `account_id` varchar(255) NOT NULL COMMENT '平台账户ID',
    `access_token` text COMMENT '访问令牌',
    `refresh_token` text COMMENT '刷新令牌',
    `expires_at` timestamp NULL COMMENT '令牌过期时间',
    `status` varchar(50) DEFAULT 'active' COMMENT '状态: active, inactive, expired',
    `followers_count` int DEFAULT 0 COMMENT '粉丝数',
    `posts_count` int DEFAULT 0 COMMENT '发布数',
    `config` json COMMENT '账户配置',
    `created_by` varchar(36) COMMENT '创建人ID',
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_platform` (`platform`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_by` (`created_by`),
    UNIQUE KEY `uk_platform_account` (`platform`, `account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社交账户表';

-- 社交推送日志表
CREATE TABLE IF NOT EXISTS `social_push_log` (
    `id` varchar(36) NOT NULL PRIMARY KEY,
    `account_id` varchar(36) NOT NULL COMMENT '社交账户ID',
    `campaign_id` varchar(36) COMMENT '活动ID',
    `content` text NOT NULL COMMENT '推送内容',
    `media_urls` text COMMENT '媒体文件URLs',
    `post_id` varchar(255) COMMENT '平台帖子ID',
    `status` varchar(50) DEFAULT 'pending' COMMENT '状态: pending, published, failed',
    `scheduled_at` timestamp NULL COMMENT '计划发布时间',
    `published_at` timestamp NULL COMMENT '实际发布时间',
    `likes_count` int DEFAULT 0 COMMENT '点赞数',
    `comments_count` int DEFAULT 0 COMMENT '评论数',
    `shares_count` int DEFAULT 0 COMMENT '分享数',
    `reach_count` int DEFAULT 0 COMMENT '触达数',
    `error_message` text COMMENT '错误信息',
    `created_by` varchar(36) COMMENT '创建人ID',
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_account_id` (`account_id`),
    INDEX `idx_campaign_id` (`campaign_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_scheduled_at` (`scheduled_at`),
    INDEX `idx_published_at` (`published_at`),
    CONSTRAINT `fk_social_push_account` FOREIGN KEY (`account_id`) REFERENCES `social_account` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社交推送日志表';

-- ========== 系统设置相关表 ==========

-- 系统设置表
CREATE TABLE IF NOT EXISTS `system_settings` (
    `id` varchar(36) NOT NULL PRIMARY KEY,
    `category` varchar(100) NOT NULL COMMENT '设置分类',
    `key_name` varchar(100) NOT NULL COMMENT '设置键名',
    `key_value` text COMMENT '设置值',
    `description` varchar(500) COMMENT '描述',
    `type` varchar(50) DEFAULT 'string' COMMENT '数据类型: string, number, boolean, json',
    `is_public` tinyint(1) DEFAULT 0 COMMENT '是否公开',
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_category_key` (`category`, `key_name`),
    INDEX `idx_category` (`category`),
    INDEX `idx_is_public` (`is_public`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统设置表';

-- ========== 通知相关表 ==========

-- 通知表
CREATE TABLE IF NOT EXISTS `notification` (
    `id` varchar(36) NOT NULL PRIMARY KEY,
    `title` varchar(255) NOT NULL COMMENT '通知标题',
    `content` text COMMENT '通知内容',
    `type` varchar(50) DEFAULT 'info' COMMENT '类型: info, success, warning, error',
    `category` varchar(100) COMMENT '分类',
    `target_type` varchar(50) DEFAULT 'user' COMMENT '目标类型: user, role, all',
    `target_id` varchar(36) COMMENT '目标ID',
    `sender_id` varchar(36) COMMENT '发送者ID',
    `is_read` tinyint(1) DEFAULT 0 COMMENT '是否已读',
    `read_at` timestamp NULL COMMENT '阅读时间',
    `url` varchar(500) COMMENT '跳转链接',
    `data` json COMMENT '扩展数据',
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_target` (`target_type`, `target_id`),
    INDEX `idx_sender_id` (`sender_id`),
    INDEX `idx_is_read` (`is_read`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- ========== 任务调度相关表 ==========

-- 调度任务表
CREATE TABLE IF NOT EXISTS `scheduled_task` (
    `id` varchar(36) NOT NULL PRIMARY KEY,
    `name` varchar(255) NOT NULL COMMENT '任务名称',
    `description` text COMMENT '任务描述',
    `type` varchar(50) NOT NULL COMMENT '任务类型',
    `cron_expression` varchar(100) COMMENT 'Cron表达式',
    `class_name` varchar(500) COMMENT '执行类名',
    `method_name` varchar(100) COMMENT '执行方法',
    `parameters` text COMMENT '参数',
    `status` varchar(50) DEFAULT 'inactive' COMMENT '状态: active, inactive, running, error',
    `last_run_time` timestamp NULL COMMENT '上次执行时间',
    `next_run_time` timestamp NULL COMMENT '下次执行时间',
    `last_result` text COMMENT '上次执行结果',
    `retry_count` int DEFAULT 0 COMMENT '重试次数',
    `max_retries` int DEFAULT 3 COMMENT '最大重试次数',
    `timeout` int DEFAULT 300 COMMENT '超时时间(秒)',
    `created_by` varchar(36) COMMENT '创建人ID',
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_type` (`type`),
    INDEX `idx_status` (`status`),
    INDEX `idx_next_run_time` (`next_run_time`),
    INDEX `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='调度任务表';

-- ========== 行为追踪相关表 ==========

-- 行为日志表
CREATE TABLE IF NOT EXISTS `behavior_log` (
    `id` varchar(36) NOT NULL PRIMARY KEY,
    `user_id` varchar(36) COMMENT '用户ID',
    `session_id` varchar(100) COMMENT '会话ID',
    `ip_address` varchar(50) COMMENT 'IP地址',
    `user_agent` varchar(1000) COMMENT '用户代理',
    `action` varchar(100) NOT NULL COMMENT '行为动作',
    `resource` varchar(200) COMMENT '资源/页面',
    `method` varchar(10) COMMENT '请求方法',
    `url` varchar(1000) COMMENT '请求URL',
    `parameters` text COMMENT '请求参数',
    `response_status` int COMMENT '响应状态码',
    `response_time` int COMMENT '响应时间(毫秒)',
    `duration` int COMMENT '停留时间(秒)',
    `referrer` varchar(1000) COMMENT '来源页面',
    `device_type` varchar(50) COMMENT '设备类型',
    `browser` varchar(100) COMMENT '浏览器',
    `os` varchar(100) COMMENT '操作系统',
    `location` varchar(200) COMMENT '地理位置',
    `data` json COMMENT '扩展数据',
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_session_id` (`session_id`),
    INDEX `idx_action` (`action`),
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_ip_address` (`ip_address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='行为日志表';

-- ========== 内容生成相关表 ==========

-- 内容生成表
CREATE TABLE IF NOT EXISTS `content_generation` (
    `id` varchar(36) NOT NULL PRIMARY KEY,
    `title` varchar(500) NOT NULL COMMENT '内容标题',
    `type` varchar(50) NOT NULL COMMENT '内容类型: article, email, social, ad',
    `language` varchar(10) DEFAULT 'zh' COMMENT '语言',
    `tone` varchar(50) DEFAULT 'professional' COMMENT '语调: professional, casual, friendly, formal',
    `length` varchar(50) DEFAULT 'medium' COMMENT '长度: short, medium, long',
    `keywords` varchar(1000) COMMENT '关键词',
    `target_audience` varchar(500) COMMENT '目标受众',
    `prompt` text NOT NULL COMMENT '生成提示',
    `generated_content` longtext COMMENT '生成的内容',
    `status` varchar(50) DEFAULT 'pending' COMMENT '状态: pending, generating, completed, failed',
    `model` varchar(100) COMMENT 'AI模型',
    `parameters` json COMMENT '生成参数',
    `quality_score` decimal(3,2) DEFAULT 0.00 COMMENT '质量评分',
    `usage_tokens` int DEFAULT 0 COMMENT '使用的token数量',
    `cost` decimal(10,4) DEFAULT 0.0000 COMMENT '成本',
    `error_message` text COMMENT '错误信息',
    `created_by` varchar(36) COMMENT '创建人ID',
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_type` (`type`),
    INDEX `idx_status` (`status`),
    INDEX `idx_language` (`language`),
    INDEX `idx_created_by` (`created_by`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容生成表';

-- ========== 数据分析相关视图 ==========

-- 客户分析视图
CREATE OR REPLACE VIEW `v_customer_analytics` AS
SELECT 
    DATE(created_at) as date,
    COUNT(*) as total_customers,
    COUNT(CASE WHEN status = 'active' THEN 1 END) as active_customers,
    COUNT(CASE WHEN DATE(created_at) = CURDATE() THEN 1 END) as new_customers_today,
    source,
    status
FROM customer
WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
GROUP BY DATE(created_at), source, status;

-- 营销活动效果视图
CREATE OR REPLACE VIEW `v_campaign_performance` AS
SELECT 
    c.id,
    c.name,
    c.type,
    c.status,
    c.budget,
    c.spent,
    c.impressions,
    c.clicks,
    c.conversions,
    CASE WHEN c.impressions > 0 THEN (c.clicks / c.impressions) * 100 ELSE 0 END as ctr,
    CASE WHEN c.clicks > 0 THEN (c.conversions / c.clicks) * 100 ELSE 0 END as conversion_rate,
    CASE WHEN c.conversions > 0 THEN c.spent / c.conversions ELSE 0 END as cost_per_conversion
FROM marketing_campaign c
WHERE c.status != 'draft';

-- ========== 初始化数据 ==========

-- 创建超级管理员角色
INSERT IGNORE INTO `sys_role` (`id`, `name`, `code`, `description`) VALUES
('super_admin_role_id', '超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限');

-- 创建基础权限
INSERT IGNORE INTO `sys_permission` (`id`, `name`, `code`, `type`, `module`, `description`) VALUES
('perm_customer_list', '客户列表', 'customer:list', 'menu', 'customer', '查看客户列表'),
('perm_customer_view', '客户详情', 'customer:view', 'button', 'customer', '查看客户详情'),
('perm_customer_create', '创建客户', 'customer:create', 'button', 'customer', '创建新客户'),
('perm_customer_update', '更新客户', 'customer:update', 'button', 'customer', '更新客户信息'),
('perm_customer_delete', '删除客户', 'customer:delete', 'button', 'customer', '删除客户'),
('perm_customer_status', '客户状态', 'customer:status', 'button', 'customer', '修改客户状态'),
('perm_acquisition_list', '获取任务列表', 'acquisition:list', 'menu', 'acquisition', '查看客户获取任务列表'),
('perm_acquisition_view', '获取任务详情', 'acquisition:view', 'button', 'acquisition', '查看获取任务详情'),
('perm_acquisition_create', '创建获取任务', 'acquisition:create', 'button', 'acquisition', '创建客户获取任务'),
('perm_acquisition_update', '更新获取任务', 'acquisition:update', 'button', 'acquisition', '更新获取任务'),
('perm_acquisition_delete', '删除获取任务', 'acquisition:delete', 'button', 'acquisition', '删除获取任务'),
('perm_acquisition_control', '控制获取任务', 'acquisition:control', 'button', 'acquisition', '启动停止获取任务');

-- 创建基础菜单
INSERT IGNORE INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `sort_order`, `type`, `status`) VALUES
('menu_customer', NULL, '客户中心', '/customer', 'Layout', 'users', 1, 0, 1),
('menu_customer_list', 'menu_customer', '客户管理', '/customer/list', 'customer/CustomerListView', 'user', 1, 1, 1),
('menu_customer_acquisition', 'menu_customer', '客户获取', '/customer/acquisition', 'customer/CustomerAcquisitionView', 'download', 2, 1, 1),
('menu_customer_analytics', 'menu_customer', '客户分析', '/customer/analytics', 'customer/CustomerAnalyticsView', 'chart-bar', 3, 1, 1),
('menu_campaign', NULL, '营销活动', '/campaign', 'Layout', 'megaphone', 2, 0, 1),
('menu_email', NULL, '邮件营销', '/email', 'Layout', 'mail', 3, 0, 1),
('menu_social', NULL, '社交营销', '/social', 'Layout', 'share', 4, 0, 1),
('menu_content', NULL, '内容生成', '/content', 'Layout', 'edit', 5, 0, 1),
('menu_system', NULL, '系统管理', '/system', 'Layout', 'settings', 9, 0, 1);

-- 设置默认系统配置
INSERT IGNORE INTO `system_settings` (`id`, `category`, `key_name`, `key_value`, `description`, `type`, `is_public`) VALUES
('setting_site_name', 'basic', 'site_name', '海外营销系统', '站点名称', 'string', 1),
('setting_site_logo', 'basic', 'site_logo', '/logo.png', '站点LOGO', 'string', 1),
('setting_default_language', 'basic', 'default_language', 'zh', '默认语言', 'string', 1),
('setting_timezone', 'basic', 'timezone', 'Asia/Shanghai', '时区设置', 'string', 0),
('setting_email_enabled', 'email', 'enabled', 'true', '是否启用邮件功能', 'boolean', 0),
('setting_smtp_host', 'email', 'smtp_host', '', 'SMTP服务器地址', 'string', 0),
('setting_smtp_port', 'email', 'smtp_port', '587', 'SMTP端口', 'number', 0),
('setting_smtp_username', 'email', 'smtp_username', '', 'SMTP用户名', 'string', 0),
('setting_smtp_password', 'email', 'smtp_password', '', 'SMTP密码', 'string', 0);

SET FOREIGN_KEY_CHECKS = 1;

-- 完成初始化
SELECT 'Database initialization completed successfully!' as result;