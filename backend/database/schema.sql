-- SQL schema for permission module
ALTER TABLE sys_permission
    ADD COLUMN `module` varchar(255) DEFAULT NULL;

-- table for role and menu mapping
CREATE TABLE IF NOT EXISTS role_menu (
    role_id varchar(36) NOT NULL,
    menu_id varchar(36) NOT NULL,
    PRIMARY KEY (role_id, menu_id)
);

-- table for user and role mapping (if not exists)
CREATE TABLE IF NOT EXISTS user_role (
    user_id varchar(36) NOT NULL,
    role_id varchar(36) NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

-- Customer acquisition task table
CREATE TABLE IF NOT EXISTS customer_acquisition_task (
    id varchar(36) NOT NULL PRIMARY KEY,
    name varchar(255) NOT NULL COMMENT '任务名称',
    type varchar(50) NOT NULL COMMENT '采集类型: customer, comment, article',
    platforms varchar(500) NOT NULL COMMENT '平台列表，逗号分隔',
    keywords text COMMENT '关键词',
    region varchar(50) COMMENT '地区',
    amount int DEFAULT 100 COMMENT '目标数量',
    fields varchar(500) COMMENT '采集字段，逗号分隔',
    frequency varchar(50) DEFAULT 'once' COMMENT '执行频率: once, daily, weekly, monthly',
    quality int DEFAULT 3 COMMENT '质量要求 1-5',
    filters text COMMENT '过滤条件',
    status varchar(50) DEFAULT 'pending' COMMENT '状态: pending, running, completed, failed, paused',
    progress decimal(5,2) DEFAULT 0.00 COMMENT '进度百分比',
    collected_count int DEFAULT 0 COMMENT '已采集数量',
    valid_count int DEFAULT 0 COMMENT '有效数量',
    success_rate decimal(5,2) DEFAULT 0.00 COMMENT '成功率',
    error_message text COMMENT '错误信息',
    created_by varchar(36) COMMENT '创建人ID',
    created_at timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    started_at timestamp NULL COMMENT '开始时间',
    completed_at timestamp NULL COMMENT '完成时间',
    next_run_time timestamp NULL COMMENT '下次运行时间（用于定时任务）',
    INDEX idx_created_by (created_by),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) COMMENT='客户获取任务表';

-- Customer acquisition result table
CREATE TABLE IF NOT EXISTS customer_acquisition_result (
    id varchar(36) NOT NULL PRIMARY KEY,
    task_id varchar(36) NOT NULL COMMENT '任务ID',
    platform varchar(50) NOT NULL COMMENT '来源平台',
    name varchar(255) COMMENT '客户姓名',
    email varchar(255) COMMENT '邮箱',
    phone varchar(255) COMMENT '电话',
    company varchar(255) COMMENT '公司',
    title varchar(255) COMMENT '职位',
    location varchar(255) COMMENT '地址',
    website varchar(500) COMMENT '网站',
    social_profile varchar(500) COMMENT '社交资料',
    industry varchar(255) COMMENT '行业',
    company_size varchar(100) COMMENT '公司规模',
    description text COMMENT '描述',
    raw_data json COMMENT '原始数据',
    status varchar(50) DEFAULT 'pending' COMMENT '状态: pending, valid, invalid, duplicate',
    created_at timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_task_id (task_id),
    INDEX idx_platform (platform),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (task_id) REFERENCES customer_acquisition_task(id) ON DELETE CASCADE
) COMMENT='客户获取结果表';

-- Customer acquisition execution log table
CREATE TABLE IF NOT EXISTS customer_acquisition_log (
    id varchar(36) NOT NULL PRIMARY KEY,
    task_id varchar(36) NOT NULL COMMENT '任务ID',
    type varchar(50) NOT NULL COMMENT '日志类型: start, progress, success, warning, error',
    message varchar(1000) NOT NULL COMMENT '日志消息',
    details text COMMENT '详细信息',
    created_at timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_task_id (task_id),
    INDEX idx_type (type),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (task_id) REFERENCES customer_acquisition_task(id) ON DELETE CASCADE
) COMMENT='客户获取执行日志表';
