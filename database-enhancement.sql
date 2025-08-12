-- 海外自动化营销系统 - 功能增强SQL脚本
-- 运行此脚本以添加AI内容生成、WhatsApp/LinkedIn集成、邮件跟踪等新功能

-- AI内容生成模板表
CREATE TABLE IF NOT EXISTS message_templates (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL COMMENT '模板名称',
    type VARCHAR(50) NOT NULL COMMENT '模板类型: EMAIL, WHATSAPP, LINKEDIN',
    industry VARCHAR(100) NOT NULL COMMENT '适用行业',
    language VARCHAR(10) NOT NULL COMMENT '语言代码: en, es, fr, zh等',
    subject VARCHAR(500) NOT NULL COMMENT '邮件主题或消息标题',
    content TEXT NOT NULL COMMENT '模板内容，支持变量占位符',
    variables TEXT COMMENT 'JSON格式的变量说明',
    category VARCHAR(100) COMMENT '模板分类：开场白、跟进邮件、产品介绍等',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    usage_count INT DEFAULT 0 COMMENT '使用次数',
    success_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '成功率',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_type_language (type, language),
    INDEX idx_industry_type (industry, type),
    INDEX idx_category (category),
    INDEX idx_success_rate (success_rate DESC)
) COMMENT = 'AI内容生成模板表';

-- 邮件跟踪表
CREATE TABLE IF NOT EXISTS email_tracking (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tracking_id VARCHAR(100) UNIQUE NOT NULL COMMENT '邮件唯一追踪ID',
    campaign_id BIGINT COMMENT '营销活动ID',
    customer_id BIGINT COMMENT '客户ID',
    email_address VARCHAR(255) NOT NULL COMMENT '邮箱地址',
    subject VARCHAR(500) COMMENT '邮件主题',
    sent_at TIMESTAMP NULL COMMENT '发送时间',
    delivered_at TIMESTAMP NULL COMMENT '送达时间',
    opened_at TIMESTAMP NULL COMMENT '首次打开时间',
    first_click_at TIMESTAMP NULL COMMENT '首次点击时间',
    replied_at TIMESTAMP NULL COMMENT '回复时间',
    bounced_at TIMESTAMP NULL COMMENT '退回时间',
    unsubscribed_at TIMESTAMP NULL COMMENT '取消订阅时间',
    open_count INT DEFAULT 0 COMMENT '打开次数',
    click_count INT DEFAULT 0 COMMENT '点击次数',
    status VARCHAR(50) DEFAULT 'CREATED' COMMENT '状态: SENT, DELIVERED, OPENED, CLICKED, REPLIED, BOUNCED, UNSUBSCRIBED',
    user_agent VARCHAR(500) COMMENT '用户代理',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    device_type VARCHAR(20) COMMENT '设备类型: DESKTOP, MOBILE, TABLET',
    location VARCHAR(255) COMMENT '地理位置信息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tracking_id (tracking_id),
    INDEX idx_campaign_id (campaign_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_email_address (email_address),
    INDEX idx_status (status),
    INDEX idx_sent_at (sent_at),
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE SET NULL
) COMMENT = '邮件跟踪表';

-- 链接跟踪表
CREATE TABLE IF NOT EXISTS link_tracking (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tracking_id VARCHAR(100) NOT NULL COMMENT '关联邮件追踪ID',
    original_url VARCHAR(1000) NOT NULL COMMENT '原始链接',
    short_url VARCHAR(255) UNIQUE NOT NULL COMMENT '短链接标识符',
    clicked_at TIMESTAMP NULL COMMENT '首次点击时间',
    click_count INT DEFAULT 0 COMMENT '点击次数',
    unique_clicks INT DEFAULT 0 COMMENT '去重点击数',
    user_agent VARCHAR(500) COMMENT '用户代理',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    referrer VARCHAR(500) COMMENT '来源页面',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tracking_id (tracking_id),
    INDEX idx_short_url (short_url),
    INDEX idx_clicked_at (clicked_at)
) COMMENT = '链接跟踪表';

-- WhatsApp消息记录表
CREATE TABLE IF NOT EXISTS whatsapp_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT COMMENT '客户ID',
    campaign_id BIGINT COMMENT '营销活动ID',
    phone_number VARCHAR(20) NOT NULL COMMENT '手机号码',
    message_type VARCHAR(50) NOT NULL COMMENT '消息类型: TEXT, TEMPLATE, MEDIA',
    template_name VARCHAR(255) COMMENT '模板名称',
    content TEXT NOT NULL COMMENT '消息内容',
    status VARCHAR(50) DEFAULT 'PENDING' COMMENT '状态: PENDING, SENT, DELIVERED, READ, FAILED',
    message_id VARCHAR(255) COMMENT 'WhatsApp消息ID',
    sent_at TIMESTAMP NULL COMMENT '发送时间',
    delivered_at TIMESTAMP NULL COMMENT '送达时间',
    read_at TIMESTAMP NULL COMMENT '读取时间',
    error_message TEXT COMMENT '错误信息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_customer_id (customer_id),
    INDEX idx_campaign_id (campaign_id),
    INDEX idx_phone_number (phone_number),
    INDEX idx_status (status),
    INDEX idx_sent_at (sent_at),
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE SET NULL
) COMMENT = 'WhatsApp消息记录表';

-- LinkedIn连接记录表
CREATE TABLE IF NOT EXISTS linkedin_connections (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT COMMENT '客户ID',
    campaign_id BIGINT COMMENT '营销活动ID',
    linkedin_profile_id VARCHAR(255) NOT NULL COMMENT 'LinkedIn档案ID',
    profile_url VARCHAR(500) COMMENT 'LinkedIn档案链接',
    connection_status VARCHAR(50) DEFAULT 'PENDING' COMMENT '连接状态: PENDING, CONNECTED, DECLINED, WITHDRAWN',
    message_sent BOOLEAN DEFAULT FALSE COMMENT '是否发送过消息',
    connection_message TEXT COMMENT '连接请求消息',
    sent_at TIMESTAMP NULL COMMENT '发送时间',
    connected_at TIMESTAMP NULL COMMENT '连接建立时间',
    last_interaction_at TIMESTAMP NULL COMMENT '最后互动时间',
    notes TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_customer_id (customer_id),
    INDEX idx_campaign_id (campaign_id),
    INDEX idx_linkedin_profile_id (linkedin_profile_id),
    INDEX idx_connection_status (connection_status),
    INDEX idx_sent_at (sent_at),
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE SET NULL
) COMMENT = 'LinkedIn连接记录表';

-- A/B测试配置表
CREATE TABLE IF NOT EXISTS ab_test_configs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    campaign_id BIGINT NOT NULL COMMENT '营销活动ID',
    test_name VARCHAR(255) NOT NULL COMMENT '测试名称',
    test_type VARCHAR(50) NOT NULL COMMENT '测试类型: SUBJECT, CONTENT, TIMING, SENDER',
    variant_a_content TEXT NOT NULL COMMENT 'A版本内容',
    variant_b_content TEXT NOT NULL COMMENT 'B版本内容',
    split_ratio DECIMAL(3,2) DEFAULT 0.50 COMMENT '流量分配比例',
    status VARCHAR(50) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE, PAUSED, COMPLETED',
    winner VARCHAR(10) COMMENT '获胜版本: A, B',
    significance_level DECIMAL(5,4) DEFAULT 0.0500 COMMENT '显著性水平',
    start_date TIMESTAMP NOT NULL COMMENT '开始时间',
    end_date TIMESTAMP NULL COMMENT '结束时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_campaign_id (campaign_id),
    INDEX idx_status (status),
    INDEX idx_test_type (test_type)
) COMMENT = 'A/B测试配置表';

-- A/B测试结果表
CREATE TABLE IF NOT EXISTS ab_test_results (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ab_test_id BIGINT NOT NULL COMMENT 'A/B测试ID',
    variant VARCHAR(10) NOT NULL COMMENT '版本: A, B',
    emails_sent INT DEFAULT 0 COMMENT '发送邮件数',
    emails_opened INT DEFAULT 0 COMMENT '打开邮件数',
    emails_clicked INT DEFAULT 0 COMMENT '点击邮件数',
    emails_replied INT DEFAULT 0 COMMENT '回复邮件数',
    open_rate DECIMAL(5,4) DEFAULT 0.0000 COMMENT '打开率',
    click_rate DECIMAL(5,4) DEFAULT 0.0000 COMMENT '点击率',
    reply_rate DECIMAL(5,4) DEFAULT 0.0000 COMMENT '回复率',
    conversion_rate DECIMAL(5,4) DEFAULT 0.0000 COMMENT '转化率',
    statistical_significance DECIMAL(5,4) COMMENT '统计显著性',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_ab_test_id (ab_test_id),
    INDEX idx_variant (variant),
    FOREIGN KEY (ab_test_id) REFERENCES ab_test_configs(id) ON DELETE CASCADE
) COMMENT = 'A/B测试结果表';

-- 客户行为分析表
CREATE TABLE IF NOT EXISTS customer_behavior_analytics (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    behavior_type VARCHAR(50) NOT NULL COMMENT '行为类型: EMAIL_OPEN, EMAIL_CLICK, WEBSITE_VISIT, FORM_SUBMIT',
    behavior_data JSON COMMENT '行为数据JSON',
    session_id VARCHAR(255) COMMENT '会话ID',
    page_url VARCHAR(1000) COMMENT '页面URL',
    referrer VARCHAR(1000) COMMENT '来源页面',
    user_agent VARCHAR(500) COMMENT '用户代理',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    location VARCHAR(255) COMMENT '地理位置',
    device_type VARCHAR(20) COMMENT '设备类型',
    browser VARCHAR(50) COMMENT '浏览器',
    os VARCHAR(50) COMMENT '操作系统',
    occurred_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '行为发生时间',
    INDEX idx_customer_id (customer_id),
    INDEX idx_behavior_type (behavior_type),
    INDEX idx_occurred_at (occurred_at),
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
) COMMENT = '客户行为分析表';

-- 营销活动效果统计表
CREATE TABLE IF NOT EXISTS campaign_performance_stats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    campaign_id BIGINT NOT NULL COMMENT '营销活动ID',
    stat_date DATE NOT NULL COMMENT '统计日期',
    emails_sent INT DEFAULT 0 COMMENT '邮件发送数',
    emails_delivered INT DEFAULT 0 COMMENT '邮件送达数',
    emails_opened INT DEFAULT 0 COMMENT '邮件打开数',
    emails_clicked INT DEFAULT 0 COMMENT '邮件点击数',
    emails_replied INT DEFAULT 0 COMMENT '邮件回复数',
    emails_bounced INT DEFAULT 0 COMMENT '邮件退回数',
    emails_unsubscribed INT DEFAULT 0 COMMENT '取消订阅数',
    whatsapp_sent INT DEFAULT 0 COMMENT 'WhatsApp发送数',
    whatsapp_delivered INT DEFAULT 0 COMMENT 'WhatsApp送达数',
    whatsapp_read INT DEFAULT 0 COMMENT 'WhatsApp已读数',
    linkedin_connections INT DEFAULT 0 COMMENT 'LinkedIn连接数',
    linkedin_messages INT DEFAULT 0 COMMENT 'LinkedIn消息数',
    website_visits INT DEFAULT 0 COMMENT '网站访问数',
    conversions INT DEFAULT 0 COMMENT '转化数',
    revenue DECIMAL(10,2) DEFAULT 0.00 COMMENT '收入',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_campaign_date (campaign_id, stat_date),
    INDEX idx_campaign_id (campaign_id),
    INDEX idx_stat_date (stat_date)
) COMMENT = '营销活动效果统计表';

-- 插入示例AI模板数据
INSERT INTO message_templates (name, type, industry, language, subject, content, category, variables) VALUES
('科技产品开场白-英文', 'EMAIL', 'Technology', 'en', 'Innovative Solutions for {company_name}', 
'Dear {customer_name},

I hope this email finds you well. I''ve been researching companies in the {industry} sector and was impressed by {company_name}''s work in the field.

We specialize in providing cutting-edge technology solutions that have helped companies like yours increase efficiency by up to 40%. I would love to discuss how our innovative platform could benefit your operations.

Would you be available for a brief 15-minute call this week to explore potential collaboration opportunities?

Best regards,
{sender_name}', 'opening_line', 
'{"customer_name": "客户姓名", "company_name": "公司名称", "industry": "行业", "sender_name": "发送人姓名"}'),

('制造业跟进邮件-英文', 'EMAIL', 'Manufacturing', 'en', 'Following up on our conversation - {company_name}',
'Hi {customer_name},

Thank you for taking the time to speak with me last week about {company_name}''s manufacturing processes.

As discussed, I wanted to follow up with some additional information about how our automation solutions have helped similar companies in the manufacturing sector achieve:
• 25% reduction in production costs
• 40% increase in operational efficiency  
• Significant improvement in quality control

I''ve attached a case study from a client in your industry who saw remarkable results within 3 months of implementation.

Would you like to schedule a demo to see how this could work for {company_name}?

Best regards,
{sender_name}', 'follow_up',
'{"customer_name": "客户姓名", "company_name": "公司名称", "sender_name": "发送人姓名"}'),

('科技产品开场白-中文', 'EMAIL', 'Technology', 'zh', '为{company_name}提供创新技术解决方案', 
'尊敬的{customer_name}，

您好！希望这封邮件能在您百忙中抽时间阅读。

我一直在关注{industry}领域的优秀企业，{company_name}在行业中的表现让我印象深刻。

我们专注于为企业提供尖端技术解决方案，已经帮助众多类似企业提高了40%的运营效率。我非常希望能与您探讨我们的创新平台如何为贵公司带来价值。

您这周是否有15分钟时间进行简短的电话沟通？我相信会是一次有价值的交流。

期待您的回复！

此致
敬礼

{sender_name}', 'opening_line',
'{"customer_name": "客户姓名", "company_name": "公司名称", "industry": "行业", "sender_name": "发送人姓名"}'),

('WhatsApp问候模板', 'WHATSAPP', 'General', 'en', '',
'Hello {customer_name}! 👋

I hope you''re having a great day. I''m reaching out from {company_name} regarding our conversation about {topic}.

We have some exciting updates that I think would interest you. Would you be available for a quick chat?

Thanks!', 'greeting',
'{"customer_name": "客户姓名", "company_name": "公司名称", "topic": "话题"}'),

('LinkedIn连接请求', 'LINKEDIN', 'General', 'en', '',
'Hi {customer_name},

I came across your profile and was impressed by your experience in {industry}. I''d love to connect and explore potential collaboration opportunities between our companies.

Looking forward to connecting!

Best,
{sender_name}', 'connection_request',
'{"customer_name": "客户姓名", "industry": "行业", "sender_name": "发送人姓名"}');

-- 创建索引以优化查询性能
CREATE INDEX idx_email_tracking_campaign_status ON email_tracking(campaign_id, status);
CREATE INDEX idx_email_tracking_opened_at ON email_tracking(opened_at) WHERE opened_at IS NOT NULL;
CREATE INDEX idx_link_tracking_clicked_at ON link_tracking(clicked_at) WHERE clicked_at IS NOT NULL;
CREATE INDEX idx_whatsapp_customer_status ON whatsapp_messages(customer_id, status);
CREATE INDEX idx_linkedin_customer_status ON linkedin_connections(customer_id, connection_status);

-- 创建视图以便于统计查询
CREATE VIEW v_campaign_email_stats AS
SELECT 
    campaign_id,
    COUNT(*) as total_sent,
    COUNT(delivered_at) as delivered,
    COUNT(opened_at) as opened,
    COUNT(first_click_at) as clicked,
    COUNT(replied_at) as replied,
    COUNT(bounced_at) as bounced,
    COUNT(unsubscribed_at) as unsubscribed,
    ROUND(COUNT(opened_at) * 100.0 / COUNT(*), 2) as open_rate,
    ROUND(COUNT(first_click_at) * 100.0 / COUNT(*), 2) as click_rate,
    ROUND(COUNT(replied_at) * 100.0 / COUNT(*), 2) as reply_rate
FROM email_tracking 
WHERE campaign_id IS NOT NULL
GROUP BY campaign_id;

-- 创建存储过程用于更新营销活动统计
DELIMITER $$
CREATE PROCEDURE UpdateCampaignDailyStats(IN p_campaign_id BIGINT, IN p_stat_date DATE)
BEGIN
    INSERT INTO campaign_performance_stats (
        campaign_id, stat_date, emails_sent, emails_delivered, emails_opened, 
        emails_clicked, emails_replied, emails_bounced, emails_unsubscribed
    )
    SELECT 
        p_campaign_id,
        p_stat_date,
        COUNT(*) as emails_sent,
        COUNT(delivered_at) as emails_delivered,
        COUNT(opened_at) as emails_opened,
        COUNT(first_click_at) as emails_clicked,
        COUNT(replied_at) as emails_replied,
        COUNT(bounced_at) as emails_bounced,
        COUNT(unsubscribed_at) as emails_unsubscribed
    FROM email_tracking 
    WHERE campaign_id = p_campaign_id 
      AND DATE(sent_at) = p_stat_date
    ON DUPLICATE KEY UPDATE
        emails_sent = VALUES(emails_sent),
        emails_delivered = VALUES(emails_delivered),
        emails_opened = VALUES(emails_opened),
        emails_clicked = VALUES(emails_clicked),
        emails_replied = VALUES(emails_replied),
        emails_bounced = VALUES(emails_bounced),
        emails_unsubscribed = VALUES(emails_unsubscribed),
        updated_at = CURRENT_TIMESTAMP;
END$$
DELIMITER ;

-- 系统设置表添加新的配置项
INSERT INTO system_settings (setting_key, setting_value, setting_type, description) VALUES
('ai.openai.enabled', 'true', 'BOOLEAN', '是否启用OpenAI内容生成'),
('ai.openai.model', 'gpt-3.5-turbo', 'STRING', 'OpenAI模型名称'),
('ai.translation.enabled', 'true', 'BOOLEAN', '是否启用自动翻译'),
('ai.translation.provider', 'openai', 'STRING', '翻译服务提供商'),
('whatsapp.enabled', 'false', 'BOOLEAN', '是否启用WhatsApp集成'),
('whatsapp.rate.limit', '50', 'NUMBER', 'WhatsApp消息发送频率限制(条/小时)'),
('linkedin.enabled', 'false', 'BOOLEAN', '是否启用LinkedIn集成'),
('linkedin.rate.limit', '20', 'NUMBER', 'LinkedIn操作频率限制(次/小时)'),
('tracking.pixel.enabled', 'true', 'BOOLEAN', '是否启用邮件像素追踪'),
('tracking.links.enabled', 'true', 'BOOLEAN', '是否启用链接追踪'),
('analytics.retention.days', '365', 'NUMBER', '分析数据保留天数')
ON DUPLICATE KEY UPDATE 
    setting_value = VALUES(setting_value),
    updated_at = CURRENT_TIMESTAMP;