/*
 Navicat Premium Dump SQL

 Source Server         : 本地链接
 Source Server Type    : MySQL
 Source Server Version : 50723 (5.7.23)
 Source Host           : 127.0.0.1:12100
 Source Schema         : platform

 Target Server Type    : MySQL
 Target Server Version : 50723 (5.7.23)
 File Encoding         : 65001

 Date: 18/08/2025 10:52:51
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_generated_content
-- ----------------------------
DROP TABLE IF EXISTS `ai_generated_content`;
CREATE TABLE `ai_generated_content`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `content_type` enum('email_subject','email_body','social_post','product_desc','ad_copy') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '内容类型',
  `prompt` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '输入提示',
  `generated_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '生成的内容',
  `language` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'en' COMMENT '语言',
  `industry` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '行业',
  `target_country` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '目标国家',
  `tone` enum('formal','casual','professional','friendly') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'professional' COMMENT '语调风格',
  `quality_score` decimal(3, 2) NULL DEFAULT NULL COMMENT '质量评分 (0.00-1.00)',
  `used` tinyint(1) NULL DEFAULT 0 COMMENT '是否已使用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_content_type`(`content_type`) USING BTREE,
  INDEX `idx_created_at`(`created_at`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'AI生成内容记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_generated_content
-- ----------------------------

-- ----------------------------
-- Table structure for ai_recommendations
-- ----------------------------
DROP TABLE IF EXISTS `ai_recommendations`;
CREATE TABLE `ai_recommendations`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `customer_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户ID',
  `recommendation_type` enum('product','channel','send_time','content_type') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '推荐类型',
  `recommended_items` json NOT NULL COMMENT '推荐项目列表',
  `confidence_score` decimal(3, 2) NULL DEFAULT NULL COMMENT '置信度 (0.00-1.00)',
  `reasoning` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '推荐理由',
  `applied` tinyint(1) NULL DEFAULT 0 COMMENT '是否已应用',
  `feedback_score` int(11) NULL DEFAULT NULL COMMENT '用户反馈评分 (1-5)',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_customer_id`(`customer_id`) USING BTREE,
  INDEX `idx_type`(`recommendation_type`) USING BTREE,
  INDEX `idx_confidence`(`confidence_score`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '智能推荐记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_recommendations
-- ----------------------------

-- ----------------------------
-- Table structure for behavior_log
-- ----------------------------
DROP TABLE IF EXISTS `behavior_log`;
CREATE TABLE `behavior_log`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键',
  `customer_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '客户ID',
  `action` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '行为操作',
  `page` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源页面',
  `ip` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '访问IP',
  `ua` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'User Agent',
  `time` datetime NOT NULL COMMENT '操作时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_customer`(`customer_id`) USING BTREE,
  INDEX `idx_time`(`time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '行为追踪日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of behavior_log
-- ----------------------------
INSERT INTO `behavior_log` VALUES ('log-001', 'cust-1001', '登录系统', '/login', '192.168.0.101', NULL, '2025-07-24 09:15:00', '2025-07-24 11:42:50', '2025-07-24 11:42:50');
INSERT INTO `behavior_log` VALUES ('log-002', 'cust-1002', '浏览产品页', '/product/abc123', '192.168.0.102', NULL, '2025-07-24 09:45:23', '2025-07-24 11:42:50', '2025-07-24 11:42:50');
INSERT INTO `behavior_log` VALUES ('log-003', 'cust-1001', '提交订单', '/checkout', '192.168.0.101', NULL, '2025-07-24 10:03:15', '2025-07-24 11:42:50', '2025-07-24 11:42:50');
INSERT INTO `behavior_log` VALUES ('log-004', 'cust-1003', '下载报告', '/report/download', '192.168.0.103', NULL, '2025-07-24 11:10:47', '2025-07-24 11:42:50', '2025-07-24 11:42:50');
INSERT INTO `behavior_log` VALUES ('log-005', 'cust-1002', '搜索关键词', '/search?kw=touch+display', '192.168.0.102', NULL, '2025-07-24 12:21:08', '2025-07-24 11:42:50', '2025-07-24 11:42:50');

-- ----------------------------
-- Table structure for content_campaign
-- ----------------------------
DROP TABLE IF EXISTS `content_campaign`;
CREATE TABLE `content_campaign`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模板名称',
  `channels` json NOT NULL COMMENT '推送渠道（如 email/social）',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `contents` json NULL COMMENT '内容片段（富文本数组）',
  `cycle` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'once' COMMENT '执行周期 once/daily/weekly',
  `status` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'pending' COMMENT '状态 pending/running/success/paused',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '内容生成模板' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of content_campaign
-- ----------------------------
INSERT INTO `content_campaign` VALUES ('cc-001', '新产品发布模板', '[\"email\", \"social\"]', '2025-07-25 10:00:00', '2025-07-25 18:00:00', '[\"<p>尊敬的客户，欢迎关注我们的新产品。</p>\", \"<p>点击了解更多详情。</p>\"]', 'once', 'pending', '2025-07-24 11:10:34', '2025-07-24 11:10:34');
INSERT INTO `content_campaign` VALUES ('cc-002', '促销活动内容', '[\"email\", \"wechat\"]', '2025-07-26 09:00:00', '2025-07-26 20:00:00', '[\"<p>限时特惠活动上线！</p>\", \"<p>转发有奖，快来参与。</p>\"]', 'daily', 'running', '2025-07-24 11:10:34', '2025-07-24 11:10:34');
INSERT INTO `content_campaign` VALUES ('cc-003', '品牌故事传播', '[\"social\", \"custom\"]', '2025-07-27 08:00:00', '2025-07-27 22:00:00', '[\"<p>每一个品牌背后都有一个动人的故事。</p>\"]', 'weekly', 'paused', '2025-07-24 11:10:34', '2025-07-24 11:10:34');

-- ----------------------------
-- Table structure for customer_collect
-- ----------------------------
DROP TABLE IF EXISTS `customer_collect`;
CREATE TABLE `customer_collect`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键',
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '客户姓名',
  `phone` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱地址',
  `company` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公司名称',
  `job_title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '职位',
  `source` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源渠道',
  `notes` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `platform` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `cycle` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `fields` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `amount` int(11) NULL DEFAULT 0,
  `status` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `progress` int(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of customer_collect
-- ----------------------------

-- ----------------------------
-- Table structure for customer_scoring
-- ----------------------------
DROP TABLE IF EXISTS `customer_scoring`;
CREATE TABLE `customer_scoring`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `customer_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户ID',
  `score` int(11) NULL DEFAULT 0 COMMENT '客户评分 (0-100)',
  `score_breakdown` json NULL COMMENT '评分明细 {\"basic_info\": 20, \"interaction\": 30, \"depth\": 30, \"intent\": 20}',
  `grade` enum('S','A','B','C') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'C' COMMENT '客户分级',
  `last_calculated` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后计算时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_customer_scoring`(`customer_id`) USING BTREE,
  INDEX `idx_score`(`score`) USING BTREE,
  INDEX `idx_grade`(`grade`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客户评分表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of customer_scoring
-- ----------------------------

-- ----------------------------
-- Table structure for customer_tag_relations
-- ----------------------------
DROP TABLE IF EXISTS `customer_tag_relations`;
CREATE TABLE `customer_tag_relations`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `customer_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户ID',
  `tag_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标签ID',
  `assigned_by` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分配人ID',
  `assigned_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_customer_tag`(`customer_id`, `tag_id`) USING BTREE,
  INDEX `idx_customer_id`(`customer_id`) USING BTREE,
  INDEX `idx_tag_id`(`tag_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客户标签关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of customer_tag_relations
-- ----------------------------

-- ----------------------------
-- Table structure for customer_tags
-- ----------------------------
DROP TABLE IF EXISTS `customer_tags`;
CREATE TABLE `customer_tags`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标签名称',
  `color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '#409EFF' COMMENT '标签颜色',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签描述',
  `tag_type` enum('system','custom','behavior','industry') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'custom' COMMENT '标签类型',
  `customer_count` int(11) NULL DEFAULT 0 COMMENT '使用该标签的客户数量',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tag_name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客户标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of customer_tags
-- ----------------------------

-- ----------------------------
-- Table structure for dashboard_metrics
-- ----------------------------
DROP TABLE IF EXISTS `dashboard_metrics`;
CREATE TABLE `dashboard_metrics`  (
  `id` char(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `total_customers` int(11) NULL DEFAULT 0,
  `emails_sent_today` int(11) NULL DEFAULT 0,
  `open_rate` decimal(5, 2) NULL DEFAULT 0.00,
  `running_tasks` int(11) NULL DEFAULT 0,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dashboard_metrics
-- ----------------------------
INSERT INTO `dashboard_metrics` VALUES ('metrics-001', 1582, 242, 35.60, 4, '2025-07-24 12:00:08');

-- ----------------------------
-- Table structure for data_sources
-- ----------------------------
DROP TABLE IF EXISTS `data_sources`;
CREATE TABLE `data_sources`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据源名称',
  `source_type` enum('api','csv','excel','crm','social','web_scraping') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据源类型',
  `platform` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '平台名称 (linkedin, facebook, google_maps, etc.)',
  `connection_config` json NOT NULL COMMENT '连接配置',
  `field_mapping` json NULL COMMENT '字段映射配置',
  `sync_frequency` enum('manual','hourly','daily','weekly') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'manual' COMMENT '同步频率',
  `last_sync_at` datetime NULL DEFAULT NULL COMMENT '最后同步时间',
  `sync_status` enum('idle','running','success','failed') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'idle' COMMENT '同步状态',
  `enabled` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_source_name`(`name`) USING BTREE,
  INDEX `idx_type`(`source_type`) USING BTREE,
  INDEX `idx_enabled`(`enabled`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据源配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_sources
-- ----------------------------

-- ----------------------------
-- Table structure for data_sync_logs
-- ----------------------------
DROP TABLE IF EXISTS `data_sync_logs`;
CREATE TABLE `data_sync_logs`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `source_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据源ID',
  `sync_type` enum('full','incremental') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'incremental' COMMENT '同步类型',
  `records_total` int(11) NULL DEFAULT 0 COMMENT '总记录数',
  `records_success` int(11) NULL DEFAULT 0 COMMENT '成功记录数',
  `records_failed` int(11) NULL DEFAULT 0 COMMENT '失败记录数',
  `errors` json NULL COMMENT '错误详情',
  `started_at` datetime NOT NULL COMMENT '开始时间',
  `completed_at` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `status` enum('running','completed','failed','cancelled') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'running' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_source_id`(`source_id`) USING BTREE,
  INDEX `idx_started_at`(`started_at`) USING BTREE,
  CONSTRAINT `fk_sync_logs_source` FOREIGN KEY (`source_id`) REFERENCES `data_sources` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据同步日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_sync_logs
-- ----------------------------

-- ----------------------------
-- Table structure for email_campaign_record
-- ----------------------------
DROP TABLE IF EXISTS `email_campaign_record`;
CREATE TABLE `email_campaign_record`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '邮件标题',
  `template_id` bigint(20) NULL DEFAULT NULL COMMENT '模板ID',
  `groups` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收件人分组（逗号分隔）',
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '发送正文',
  `status` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'pending' COMMENT '发送状态',
  `total_count` int(11) NULL DEFAULT 0 COMMENT '总发送人数',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of email_campaign_record
-- ----------------------------
INSERT INTO `email_campaign_record` VALUES (1, '新品邮件发送任务', 1, '全部客户', '<p>欢迎了解我们最新的产品！</p>', 'success', 120, '2025-07-24 10:16:28');
INSERT INTO `email_campaign_record` VALUES (2, '促销邮件测试', 2, '潜在客户,VIP', '<p>限时优惠，马上抢购！</p>', 'pending', 0, '2025-07-24 10:16:28');

-- ----------------------------
-- Table structure for email_campaign_template
-- ----------------------------
DROP TABLE IF EXISTS `email_campaign_template`;
CREATE TABLE `email_campaign_template`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模板名称',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板描述',
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '模板内容',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of email_campaign_template
-- ----------------------------
INSERT INTO `email_campaign_template` VALUES (1, '新品介绍模板', '用于新品发布通知', '<p>欢迎了解我们最新的产品！</p>', '2025-07-24 10:16:28');
INSERT INTO `email_campaign_template` VALUES (2, '促销活动模板', '用于限时折扣活动', '<p>限时优惠，马上抢购！</p>', '2025-07-24 10:16:28');

-- ----------------------------
-- Table structure for marketing_campaign
-- ----------------------------
DROP TABLE IF EXISTS `marketing_campaign`;
CREATE TABLE `marketing_campaign`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键 ID',
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '活动名称',
  `status` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'pending',
  `channel` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `description` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '活动描述',
  `created_by` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '营销活动表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of marketing_campaign
-- ----------------------------
INSERT INTO `marketing_campaign` VALUES ('08d2ea9c-5f0d-45a9-af36-7c46d169c515', '11111', 'paused', 'social', NULL, NULL, NULL, NULL, '2025-07-23 07:27:37', '2025-07-23 08:29:15');
INSERT INTO `marketing_campaign` VALUES ('3b2ebdd6-cc5b-4cae-a3f1-f71002721d0a', '测试', 'pending', 'email', '2025-07-14 08:00:00', '2025-07-23 08:00:00', NULL, NULL, '2025-07-25 01:36:14', '2025-07-25 01:36:14');

-- ----------------------------
-- Table structure for marketing_channels
-- ----------------------------
DROP TABLE IF EXISTS `marketing_channels`;
CREATE TABLE `marketing_channels`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '渠道名称',
  `channel_type` enum('email','sms','whatsapp','linkedin','facebook','twitter','wechat','custom') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '渠道类型',
  `category` enum('digital','social','mobile','messaging','traditional','custom') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'custom' COMMENT '渠道分类',
  `icon` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '?' COMMENT '渠道图标',
  `color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '#409EFF' COMMENT '渠道颜色',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道描述',
  `api_config` json NULL COMMENT 'API配置信息',
  `features` json NULL COMMENT '功能特性配置',
  `enabled` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `builtin` tinyint(1) NULL DEFAULT 0 COMMENT '是否内置渠道',
  `sort_order` int(11) NULL DEFAULT 0 COMMENT '排序',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_channel_name`(`name`) USING BTREE,
  INDEX `idx_type`(`channel_type`) USING BTREE,
  INDEX `idx_enabled`(`enabled`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '营销渠道表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of marketing_channels
-- ----------------------------

-- ----------------------------
-- Table structure for permissions
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `created_at` datetime(6) NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_7lcb6glmvwlro3p2w2cewxtvd`(`code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of permissions
-- ----------------------------

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '产品名称',
  `sku` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '产品编码',
  `category_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类ID',
  `brand` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '品牌',
  `short_description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '简短描述',
  `description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '详细描述',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'draft' COMMENT '状态: draft, published, archived',
  `featured` tinyint(1) NULL DEFAULT 0 COMMENT '是否推荐',
  `tags` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签，逗号分隔',
  `seo_keywords` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'SEO关键词',
  `images` json NULL COMMENT '产品图片URLs',
  `videos` json NULL COMMENT '产品视频信息',
  `documents` json NULL COMMENT '产品文档信息',
  `prices` json NULL COMMENT '多币种价格信息',
  `specifications` json NULL COMMENT '产品规格信息',
  `languages` json NULL COMMENT '多语言内容',
  `view_count` int(11) NULL DEFAULT 0 COMMENT '浏览次数',
  `sort_order` int(11) NULL DEFAULT 0 COMMENT '排序',
  `created_by` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人ID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `sku`(`sku`) USING BTREE,
  INDEX `idx_sku`(`sku`) USING BTREE,
  INDEX `idx_category_id`(`category_id`) USING BTREE,
  INDEX `idx_brand`(`brand`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_featured`(`featured`) USING BTREE,
  INDEX `idx_created_by`(`created_by`) USING BTREE,
  INDEX `idx_created_at`(`created_at`) USING BTREE,
  INDEX `idx_sort_order`(`sort_order`) USING BTREE,
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `product_category` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '产品信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------

-- ----------------------------
-- Table structure for product_category
-- ----------------------------
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `parent_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父级分类ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类描述',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类图标',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类图片',
  `sort_order` int(11) NULL DEFAULT 0 COMMENT '排序',
  `level` tinyint(4) NULL DEFAULT 1 COMMENT '层级',
  `path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '层级路径',
  `product_count` int(11) NULL DEFAULT 0 COMMENT '产品数量',
  `enabled` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id`) USING BTREE,
  INDEX `idx_sort_order`(`sort_order`) USING BTREE,
  INDEX `idx_level`(`level`) USING BTREE,
  INDEX `idx_enabled`(`enabled`) USING BTREE,
  CONSTRAINT `fk_product_category_parent` FOREIGN KEY (`parent_id`) REFERENCES `product_category` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '产品分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_category
-- ----------------------------
INSERT INTO `product_category` VALUES ('cat_chemical', NULL, '化工产品', '化工原料和产品', NULL, NULL, 4, 1, '/化工产品', 0, 1, '2025-08-12 15:22:06', '2025-08-12 15:22:06');
INSERT INTO `product_category` VALUES ('cat_electronics', NULL, '电子产品', '各类电子产品分类', NULL, NULL, 1, 1, '/电子产品', 0, 1, '2025-08-12 15:22:06', '2025-08-12 15:22:06');
INSERT INTO `product_category` VALUES ('cat_electronics_computer', 'cat_electronics', '计算机设备', '电脑、服务器等设备', NULL, NULL, 1, 2, '/电子产品/计算机设备', 0, 1, '2025-08-12 15:22:06', '2025-08-12 15:22:06');
INSERT INTO `product_category` VALUES ('cat_electronics_mobile', 'cat_electronics', '移动设备', '手机、平板等移动设备', NULL, NULL, 2, 2, '/电子产品/移动设备', 0, 1, '2025-08-12 15:22:06', '2025-08-12 15:22:06');
INSERT INTO `product_category` VALUES ('cat_machinery', NULL, '机械设备', '各类机械设备分类', NULL, NULL, 2, 1, '/机械设备', 0, 1, '2025-08-12 15:22:06', '2025-08-12 15:22:06');
INSERT INTO `product_category` VALUES ('cat_machinery_industrial', 'cat_machinery', '工业设备', '工业生产设备', NULL, NULL, 1, 2, '/机械设备/工业设备', 0, 1, '2025-08-12 15:22:06', '2025-08-12 15:22:06');
INSERT INTO `product_category` VALUES ('cat_textile', NULL, '纺织品', '纺织品和服装分类', NULL, NULL, 3, 1, '/纺织品', 0, 1, '2025-08-12 15:22:06', '2025-08-12 15:22:06');

-- ----------------------------
-- Table structure for product_i18n
-- ----------------------------
DROP TABLE IF EXISTS `product_i18n`;
CREATE TABLE `product_i18n`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `product_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '产品ID',
  `language_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '语言代码 (en, zh, fr, de, es, etc.)',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '产品描述',
  `features` json NULL COMMENT '产品特性列表',
  `specifications` json NULL COMMENT '产品规格',
  `keywords` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'SEO关键词',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_product_lang`(`product_id`, `language_code`) USING BTREE,
  INDEX `idx_language`(`language_code`) USING BTREE,
  CONSTRAINT `fk_product_i18n_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '产品多语言内容表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_i18n
-- ----------------------------

-- ----------------------------
-- Table structure for product_pricing
-- ----------------------------
DROP TABLE IF EXISTS `product_pricing`;
CREATE TABLE `product_pricing`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `product_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '产品ID',
  `currency_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '货币代码 (USD, EUR, GBP, CNY)',
  `price` decimal(15, 4) NOT NULL COMMENT '产品价格',
  `price_type` enum('retail','wholesale','promotional','quote') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'retail' COMMENT '价格类型',
  `valid_from` date NULL DEFAULT NULL COMMENT '有效开始日期',
  `valid_to` date NULL DEFAULT NULL COMMENT '有效结束日期',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_product_currency_type`(`product_id`, `currency_code`, `price_type`) USING BTREE,
  INDEX `idx_currency`(`currency_code`) USING BTREE,
  CONSTRAINT `fk_product_pricing_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '产品价格管理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_pricing
-- ----------------------------

-- ----------------------------
-- Table structure for recent_task
-- ----------------------------
DROP TABLE IF EXISTS `recent_task`;
CREATE TABLE `recent_task`  (
  `id` char(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `progress` int(11) NULL DEFAULT 0,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of recent_task
-- ----------------------------
INSERT INTO `recent_task` VALUES ('92bf8cd7-0b4c-4831-bfcd-9ac4f5a05248', '1w312e', NULL, 0, '2025-08-14 08:00:18');
INSERT INTO `recent_task` VALUES ('ce6d23cb-69de-4526-9016-0937adc37362', '111', NULL, 0, '2025-08-14 08:23:17');
INSERT INTO `recent_task` VALUES ('task-001', '7月客户邮件群发', 'running', 65, '2025-07-22 10:00:00');
INSERT INTO `recent_task` VALUES ('task-002', '调研问卷分发', 'completed', 100, '2025-07-20 08:30:00');
INSERT INTO `recent_task` VALUES ('task-003', '新客户激活提醒', 'pending', 0, '2025-07-24 09:00:00');

-- ----------------------------
-- Table structure for role_menu
-- ----------------------------
DROP TABLE IF EXISTS `role_menu`;
CREATE TABLE `role_menu`  (
  `role_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色ID',
  `menu_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role_menu
-- ----------------------------
INSERT INTO `role_menu` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'menu-001');
INSERT INTO `role_menu` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'menu-002');
INSERT INTO `role_menu` VALUES ('role-001', 'menu-001');
INSERT INTO `role_menu` VALUES ('role-001', 'menu-002');
INSERT INTO `role_menu` VALUES ('role-001', 'menu-003');
INSERT INTO `role_menu` VALUES ('role-001', 'menu-005');
INSERT INTO `role_menu` VALUES ('role-001', 'menu-006');
INSERT INTO `role_menu` VALUES ('role-001', 'menu-007');
INSERT INTO `role_menu` VALUES ('role-001', 'menu-008');
INSERT INTO `role_menu` VALUES ('role-001', 'menu-009');
INSERT INTO `role_menu` VALUES ('role-001', 'menu-010');
INSERT INTO `role_menu` VALUES ('role-001', 'menu-011');
INSERT INTO `role_menu` VALUES ('role-001', 'menu-012');
INSERT INTO `role_menu` VALUES ('role-001', 'menu-014');
INSERT INTO `role_menu` VALUES ('role-001', 'menu-015');
INSERT INTO `role_menu` VALUES ('role-001', 'menu-0151');
INSERT INTO `role_menu` VALUES ('role-001', 'menu-0152');
INSERT INTO `role_menu` VALUES ('role-001', 'menu-0153');
INSERT INTO `role_menu` VALUES ('role-001', 'menu-016');
INSERT INTO `role_menu` VALUES ('role-001', 'menu-017');
INSERT INTO `role_menu` VALUES ('role-001', 'menu-018');
INSERT INTO `role_menu` VALUES ('role-001', 'menu-019');
INSERT INTO `role_menu` VALUES ('role-001', 'menu-020');
INSERT INTO `role_menu` VALUES ('role-001', 'menu-024');

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission`  (
  `role_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `permission_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`role_id`, `permission_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role_permission
-- ----------------------------
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'dir-100');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'dir-101');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'dir-102');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'dir-103');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'dir-104');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'dir-105');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'dir-106');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'dir-108');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'dir-109');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'dir-110');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'dir-111');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'dir-112');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'dir-113');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'dir-114');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-001');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-002');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-003');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-004');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-006');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-007');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-008');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-009');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-010');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-011');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-012');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-014');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-015');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-016');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-101');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-102');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-103');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-104');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-105');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-106');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-107');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-201');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-202');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-203');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-204');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-205');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-206');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-301');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-302');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-303');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-304');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-305');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-306');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-307');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-401');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-402');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-403');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-404');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-405');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-406');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-407');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-501');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-502');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-503');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-504');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-505');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-601');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-602');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-603');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-604');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-605');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-606');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-607');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-608');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-701');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-702');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-703');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-704');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-705');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-706');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-707');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-708');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-709');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-710');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-711');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-712');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-713');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-801');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-802');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-803');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-804');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-901');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-902');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-903');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-904');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-905');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-906');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-907');
INSERT INTO `role_permission` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-908');
INSERT INTO `role_permission` VALUES ('f51e9101-dbce-45a9-9028-5fa9db7872f3', 'perm-003');
INSERT INTO `role_permission` VALUES ('f51e9101-dbce-45a9-9028-5fa9db7872f3', 'perm-014');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-001');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-002');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-003');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-004');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-006');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-007');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-008');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-009');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-010');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-011');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-012');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-014');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-015');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-016');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-017');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-020');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-101');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-102');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-103');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-104');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-105');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-106');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-107');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-201');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-202');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-203');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-204');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-205');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-206');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-301');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-302');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-303');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-304');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-305');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-306');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-307');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-401');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-402');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-403');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-404');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-405');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-406');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-407');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-501');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-502');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-503');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-504');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-505');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-506');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-601');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-602');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-603');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-604');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-605');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-606');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-607');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-608');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-701');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-702');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-703');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-704');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-705');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-706');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-707');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-708');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-709');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-710');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-711');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-712');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-713');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-801');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-802');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-803');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-804');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-901');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-902');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-903');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-904');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-905');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-906');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-907');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-908');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-909');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-910');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-911');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-912');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-913');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-914');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-915');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-916');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-917');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-918');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-919');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-920');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-921');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-922');
INSERT INTO `role_permission` VALUES ('role-001', 'perm-923');

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of roles
-- ----------------------------

-- ----------------------------
-- Table structure for scheduled_task
-- ----------------------------
DROP TABLE IF EXISTS `scheduled_task`;
CREATE TABLE `scheduled_task`  (
  `id` char(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务 ID',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务名称',
  `description` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '任务描述',
  `cycle` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '执行周期（每天/每周/每月）',
  `start_time` datetime NULL DEFAULT NULL COMMENT '起始时间',
  `enabled` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `status` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'pending' COMMENT '状态（pending/running/paused/success/error）',
  `actions` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `tags` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `last_run` datetime NULL DEFAULT NULL COMMENT '上次执行时间',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '营销任务调度表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of scheduled_task
-- ----------------------------
INSERT INTO `scheduled_task` VALUES ('task-001', '7月客户邮件群发', '发送促销邮件给活跃客户', '每天', '2025-07-20 09:00:00', 1, 'running', '发送邮件', '[\"促销\",\"邮件\"]', '2025-07-23 09:00:00', '2025-07-24 12:06:21');
INSERT INTO `scheduled_task` VALUES ('task-002', '月度报告生成', '定期生成营销分析报告', '每月', '2025-07-01 00:00:00', 0, 'success', '[\"生成报表\"]', '[\"统计\", \"分析\"]', '2025-07-01 00:00:00', '2025-07-24 12:06:21');
INSERT INTO `scheduled_task` VALUES ('task-003', '社交媒体推广', '自动发布宣传图文到Facebook和X平台', '每周', '2025-07-15 10:00:00', 0, 'paused', '[\"社媒发布\"]', '[\"社交\", \"品牌\"]', '2025-07-21 10:00:00', '2025-07-24 12:06:21');

-- ----------------------------
-- Table structure for social_account
-- ----------------------------
DROP TABLE IF EXISTS `social_account`;
CREATE TABLE `social_account`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `platform` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `access_token` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `note` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `status` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '已绑定',
  `bind_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of social_account
-- ----------------------------
INSERT INTO `social_account` VALUES ('acc-001', 'Facebook', '品牌官方号111', 'fb-token-123', '用于Facebook推广111', NULL, NULL);
INSERT INTO `social_account` VALUES ('acc-003', 'Twitter', '新品发布号', 'tw-token-789', '同步新品动态', '已绑定', '2025-07-03 18:30:00');

-- ----------------------------
-- Table structure for social_push_log
-- ----------------------------
DROP TABLE IF EXISTS `social_push_log`;
CREATE TABLE `social_push_log`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `account_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `status` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `account_id`(`account_id`) USING BTREE,
  CONSTRAINT `social_push_log_ibfk_1` FOREIGN KEY (`account_id`) REFERENCES `social_account` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of social_push_log
-- ----------------------------
INSERT INTO `social_push_log` VALUES ('log-001', 'acc-001', '发布了新产品宣传图', '成功', '2025-07-05 10:05:00');
INSERT INTO `social_push_log` VALUES ('log-002', 'acc-001', '同步官网限时活动', '失败', '2025-07-06 09:30:00');
INSERT INTO `social_push_log` VALUES ('log-004', 'acc-003', '推送新品介绍推文', '成功', '2025-07-06 21:00:00');
INSERT INTO `social_push_log` VALUES ('log-005', 'acc-003', '上传视频失败', '失败', '2025-07-07 08:00:00');

-- ----------------------------
-- Table structure for sys_customer
-- ----------------------------
DROP TABLE IF EXISTS `sys_customer`;
CREATE TABLE `sys_customer`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键 ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户名称',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户邮箱',
  `phone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `source` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户来源',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'active' COMMENT '状态（active/inactive）',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_customer
-- ----------------------------
INSERT INTO `sys_customer` VALUES ('59d0f1ff-59d0-48db-a454-01b0a7fb5305', '王五', 'wangwu@example.com', '13711112222', NULL, 'active', '潜在客户', '2025-08-04 02:18:39', '2025-08-04 02:18:39');
INSERT INTO `sys_customer` VALUES ('68fc6f4a-50fe-4715-90f5-fdc925530dec', '测试', '111111111111111111', '111', '1111', 'active', '11111111111', '2025-07-23 02:07:19', '2025-07-23 02:51:00');
INSERT INTO `sys_customer` VALUES ('74e251c9-9816-41f3-aff8-379b9bcbbd4a', '李四', 'lisi@example.com', '13900000000', NULL, 'active', '已联系', '2025-08-04 02:18:39', '2025-08-04 02:18:39');
INSERT INTO `sys_customer` VALUES ('92d0e2e5-2f06-4090-90ea-63853bac3930', '张三', 'zhangsan@example.com', '13800000000', NULL, 'active', '重点客户', '2025-08-04 02:18:39', '2025-08-04 02:18:39');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键 ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `parent_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父级菜单 ID',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由路径',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '前端组件路径',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单图标',
  `order_num` int(11) NULL DEFAULT 0 COMMENT '排序号',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'menu' COMMENT '菜单类型 menu|button',
  `permission` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '绑定权限码',
  `hidden` tinyint(1) NULL DEFAULT 0 COMMENT '是否隐藏',
  `keep_alive` tinyint(1) NULL DEFAULT 0 COMMENT '是否缓存',
  `is_external` tinyint(1) NULL DEFAULT 0 COMMENT '是否外链',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态（1启用，0禁用）',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('menu-001', '数据总览', NULL, '/dashboard', 'DashboardView', 'Odometer', 1, 'menu', NULL, 0, 0, 0, 1, NULL, '2025-07-22 17:52:01', '2025-08-13 13:50:56');
INSERT INTO `sys_menu` VALUES ('menu-002', '营销活动管理', NULL, '/campaign-center', 'CampaignCenterView', 'Promotion', 2, 'menu', NULL, 0, 0, 0, 1, NULL, '2025-07-22 17:52:01', '2025-08-13 13:16:07');
INSERT INTO `sys_menu` VALUES ('menu-003', '通知中心', NULL, '/notifications', 'NotificationCenterView', 'Bell', 3, 'menu', NULL, 0, 0, 0, 0, NULL, '2025-07-22 17:52:01', '2025-08-13 13:16:07');
INSERT INTO `sys_menu` VALUES ('menu-005', '客户采集', NULL, '/customer-crawl', 'CustomerCrawlView', 'Upload', 5, 'menu', NULL, 0, 0, 0, 1, NULL, '2025-07-22 17:52:01', '2025-08-14 16:37:54');
INSERT INTO `sys_menu` VALUES ('menu-006', '客户管理', NULL, '/customer-manage', 'CustomerManageView', 'User', 6, 'menu', NULL, 0, 0, 0, 1, NULL, '2025-07-22 17:52:01', '2025-07-22 17:52:01');
INSERT INTO `sys_menu` VALUES ('menu-007', '邮件营销', NULL, '/email-marketing', 'EmailMarketingView', 'Message', 7, 'menu', NULL, 0, 0, 0, 1, NULL, '2025-07-22 17:52:01', '2025-08-14 16:37:52');
INSERT INTO `sys_menu` VALUES ('menu-008', '社交营销', NULL, '/social-media', 'SocialMediaView', 'Share2', 8, 'menu', NULL, 0, 0, 0, 0, NULL, '2025-07-22 17:52:01', '2025-08-13 13:16:07');
INSERT INTO `sys_menu` VALUES ('menu-009', '任务调度', NULL, '/task-schedule', 'TaskScheduleView', 'Timer', 9, 'menu', NULL, 0, 0, 0, 0, NULL, '2025-07-22 17:52:01', '2025-08-13 13:16:07');
INSERT INTO `sys_menu` VALUES ('menu-010', '数据分析', NULL, '/behavior-track', 'BehaviorTrackView', 'View', 10, 'menu', NULL, 0, 0, 0, 1, NULL, '2025-07-22 17:52:01', '2025-08-13 13:16:07');
INSERT INTO `sys_menu` VALUES ('menu-011', '报表统计', NULL, '/reports', 'ReportsView', 'Histogram', 11, 'menu', NULL, 0, 0, 0, 0, NULL, '2025-07-22 17:52:01', '2025-07-24 14:20:47');
INSERT INTO `sys_menu` VALUES ('menu-012', 'AI营销助手', NULL, '/content-generate', 'ContentGenerateView', 'Document', 12, 'menu', NULL, 0, 0, 0, 1, NULL, '2025-07-22 17:52:01', '2025-08-13 13:20:59');
INSERT INTO `sys_menu` VALUES ('menu-014', '权限管理', 'menu-013', '/permission', 'PermissionView', 'Lock', 1, 'menu', NULL, 0, 0, 0, 1, NULL, '2025-07-22 17:52:01', '2025-07-22 17:52:01');
INSERT INTO `sys_menu` VALUES ('menu-015', '菜单管理', 'menu-013', '/system/menu', 'MenuManagement', 'Menu', 2, 'menu', NULL, 0, 0, 0, 1, NULL, '2025-07-22 17:52:01', '2025-07-24 16:54:46');
INSERT INTO `sys_menu` VALUES ('menu-0151', '新增菜单', 'menu-015', NULL, NULL, NULL, 1, 'button', 'menu:create', 0, 0, 0, 1, NULL, '2025-07-22 17:52:01', '2025-07-22 17:52:01');
INSERT INTO `sys_menu` VALUES ('menu-0152', '编辑菜单', 'menu-015', NULL, NULL, NULL, 2, 'button', 'menu:update', 0, 0, 0, 1, NULL, '2025-07-22 17:52:01', '2025-07-22 17:52:01');
INSERT INTO `sys_menu` VALUES ('menu-0153', '删除菜单', 'menu-015', NULL, NULL, NULL, 3, 'button', 'menu:delete', 0, 0, 0, 1, NULL, '2025-07-22 17:52:01', '2025-07-22 17:52:01');
INSERT INTO `sys_menu` VALUES ('menu-0154', '启用/禁用菜单', 'menu-015', NULL, NULL, NULL, 4, 'button', 'menu:status', 0, 0, 0, 1, NULL, '2025-07-22 17:52:01', '2025-07-22 17:52:01');
INSERT INTO `sys_menu` VALUES ('menu-016', '系统设置', 'menu-013', '/settings', 'SettingsView', 'Tool', 3, 'menu', NULL, 0, 0, 0, 1, NULL, '2025-07-22 17:52:01', '2025-07-22 17:52:01');
INSERT INTO `sys_menu` VALUES ('menu-017', '产品内容中心', NULL, '/product/center', 'ProductCenterView', 'Package', 13, 'menu', NULL, 0, 0, 0, 1, NULL, '2025-08-12 15:33:25', '2025-08-13 13:16:07');
INSERT INTO `sys_menu` VALUES ('menu-018', '客户标签', 'menu-006', '/customer/tags', 'CustomerTagsView', 'Tag', 1, 'menu', NULL, 0, 0, 0, 1, NULL, '2025-08-12 15:33:25', '2025-08-12 15:33:25');
INSERT INTO `sys_menu` VALUES ('menu-019', '数据导入', 'menu-006', '/customer/import', 'DataImportView', 'Upload', 2, 'menu', NULL, 0, 0, 0, 1, NULL, '2025-08-12 15:33:25', '2025-08-12 15:33:25');
INSERT INTO `sys_menu` VALUES ('menu-020', 'WhatsApp营销', NULL, '/whatsapp', 'WhatsAppView', 'MessageSquare', 14, 'menu', NULL, 0, 0, 0, 0, NULL, '2025-08-12 15:33:25', '2025-08-13 13:16:07');
INSERT INTO `sys_menu` VALUES ('menu-024', '爬虫数据管理', 'menu-019', '/customer/crawl-data', 'CrawlDataView', NULL, 0, 'menu', NULL, 0, 0, 0, 1, NULL, '2025-08-14 16:55:05', '2025-08-14 16:55:05');

-- ----------------------------
-- Table structure for sys_notification
-- ----------------------------
DROP TABLE IF EXISTS `sys_notification`;
CREATE TABLE `sys_notification`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '通知标题',
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '通知内容',
  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '通知类型，如 message、task、system',
  `status` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '通知状态，如 read、unread',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT NULL,
  `creator_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人ID',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `link` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '通知跳转链接',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_notification
-- ----------------------------
INSERT INTO `sys_notification` VALUES ('notif-001', '系统更新通知', '平台将在今晚进行维护', 'system', 'read', '2025-07-24 09:32:29', NULL, NULL, '2025-07-24 09:33:23', NULL);

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `group_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `parent_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `method` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `status` tinyint(1) NULL DEFAULT 1,
  `created_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `updated_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT NULL,
  `updated_at` datetime NULL DEFAULT NULL,
  `module` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属模块',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES ('dir-100', 'permission:dir', '权限管理', '模块目录：权限管理', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('dir-101', 'user:dir', '用户管理', '模块目录：用户管理', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('dir-102', 'role:dir', '角色管理', '模块目录：角色管理', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('dir-103', 'menu:dir', '菜单管理', '模块目录：菜单管理', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('dir-104', 'customer:dir', '客户管理', '模块目录：客户管理', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('dir-105', 'campaign:dir', '营销活动', '模块目录：营销活动', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('dir-106', 'notification:dir', '通知中心', '模块目录：通知中心', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('dir-107', 'customer-collect:dir', '客户采集', '模块目录：客户采集', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('dir-108', 'email-campaign:dir', '邮件营销', '模块目录：邮件营销', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('dir-109', 'social-account:dir', '社交营销', '模块目录：社交营销', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('dir-110', 'content-generation:dir', '内容生成', '模块目录：内容生成', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('dir-111', 'behavior-log:dir', '行为追踪', '模块目录：行为追踪', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('dir-112', 'dashboard:dir', '控制台', '模块目录：控制台', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('dir-113', 'task:dir', '任务调度', '模块目录：任务调度', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('dir-114', 'settings:dir', '系统设置', '模块目录：系统设置', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('dir-115', 'product:dir', '产品中心', '模块目录：产品中心', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, '2025-08-13 15:13:51', '2025-08-13 15:13:51', NULL);
INSERT INTO `sys_permission` VALUES ('perm-001', 'permission:list', '查看权限树', '可以查看权限菜单结构', NULL, NULL, 'dir-100', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-002', 'user:add', '添加用户', '可以添加新用户', NULL, NULL, 'dir-101', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-003', 'role:view', '查看角色列表', '允许访问角色管理模块', NULL, NULL, 'dir-102', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-004', 'user:list', '查看用户列表', '', NULL, NULL, 'dir-101', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-006', 'user:update', '用户角色分配', '', NULL, NULL, 'dir-101', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-007', 'role:list', '查看角色列表', '', NULL, NULL, 'dir-102', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-008', 'user:create', '新增用户', '允许添加新用户', NULL, NULL, 'dir-101', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-009', 'role:create', '新增角色', '允许创建系统角色', NULL, NULL, 'dir-102', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-010', 'role:update', '角色权限分配', '允许为角色分配权限', NULL, NULL, 'dir-102', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-011', 'role:delete', '删除角色', '允许删除系统角色', NULL, NULL, 'dir-102', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-012', 'user:delete', '删除用户', '允许删除系统用户', NULL, NULL, 'dir-101', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-014', 'user:detail', '查看用户详情', '查看用户完整信息', NULL, NULL, 'dir-101', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-015', 'user:reset-password', '重置用户密码', '管理员可重置用户密码', NULL, NULL, 'dir-101', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-016', 'user:view', '用户角色列表', '获取用户的角色列表', NULL, NULL, 'dir-101', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-101', 'menu:list', '菜单列表', '允许查看菜单分页数据', NULL, NULL, 'dir-103', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-102', 'menu:tree', '菜单树查询', '允许查看菜单树结构', NULL, NULL, 'dir-103', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-103', 'menu:create', '新增菜单', '允许创建新菜单', NULL, NULL, 'dir-103', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-104', 'menu:update', '编辑菜单', '允许修改菜单信息', NULL, NULL, 'dir-103', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-105', 'menu:delete', '删除菜单', '允许删除菜单', NULL, NULL, 'dir-103', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-106', 'menu:view', '查看菜单详情', '允许查看菜单详细信息', NULL, NULL, 'dir-103', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-107', 'menu:status', '启用禁用菜单', '允许切换菜单启用状态', NULL, NULL, 'dir-103', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-201', 'customer:list', '客户列表', '查看客户分页列表', NULL, NULL, 'dir-104', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-202', 'customer:view', '客户详情', '查看客户详细信息', NULL, NULL, 'dir-104', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-203', 'customer:create', '新增客户', '创建客户信息', NULL, NULL, 'dir-104', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-204', 'customer:update', '编辑客户', '修改客户信息', NULL, NULL, 'dir-104', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-205', 'customer:delete', '删除客户', '删除客户信息', NULL, NULL, 'dir-104', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-206', 'customer:status', '启用/禁用客户', '切换客户状态', NULL, NULL, 'dir-104', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-301', 'campaign:list', '营销活动列表', '查看营销活动分页数据', NULL, NULL, 'dir-105', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-302', 'campaign:detail', '营销活动详情', '查看单条营销活动详情', NULL, NULL, 'dir-105', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-303', 'campaign:create', '新增营销活动', '创建新的营销活动', NULL, NULL, 'dir-105', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-304', 'campaign:update', '编辑营销活动', '修改营销活动信息', NULL, NULL, 'dir-105', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-305', 'campaign:delete', '删除营销活动', '删除指定营销活动', NULL, NULL, 'dir-105', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-306', 'campaign:publish', '发布营销活动', '允许将营销活动状态设置为 running', NULL, NULL, 'dir-105', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-307', 'campaign:toggle', '暂停/恢复营销活动', '允许在 paused 与 running 间切换活动状态', NULL, NULL, 'dir-105', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-401', 'notification:list', '通知列表', '查看通知分页列表', NULL, NULL, 'dir-106', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-402', 'notification:view', '通知详情', '查看通知详细信息', NULL, NULL, 'dir-106', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-403', 'notification:create', '创建通知', '新增系统通知', NULL, NULL, 'dir-106', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-404', 'notification:mark-read', '标记已读', '单条通知设为已读', NULL, NULL, 'dir-106', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-405', 'notification:batch-read', '批量已读', '多条通知设为已读', NULL, NULL, 'dir-106', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-406', 'notification:delete', '删除通知', '删除单条通知', NULL, NULL, 'dir-106', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-407', 'notification:batch-delete', '批量删除', '批量删除通知', NULL, NULL, 'dir-106', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-408', 'crawl:data:view', '查看爬虫数据', '查看和浏览爬取到的客户数据', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-409', 'crawl:data:export', '导出爬虫数据', '导出爬虫采集的数据到文件', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-410', 'crawl:data:delete', '删除爬虫数据', '删除无效或不需要的爬虫数据', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-501', 'customer-collect:list', '客户采集列表', '查看客户采集分页列表', NULL, NULL, 'dir-104', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-502', 'customer-collect:view', '客户采集详情', '查看客户详细信息', NULL, NULL, 'dir-104', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-503', 'customer-collect:create', '新增客户采集', '新增客户线索信息', NULL, NULL, 'dir-104', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-504', 'customer-collect:delete', '删除客户采集', '删除单个客户采集', NULL, NULL, 'dir-104', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-505', 'customer-collect:batch-delete', '批量删除客户采集', '批量删除客户采集信息', NULL, NULL, 'dir-104', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-506', 'customer-collect:update', '修改客户采集', '编辑客户采集信息', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-601', 'email-campaign:template:list', '邮件模板列表', '查看邮件模板分页列表', NULL, NULL, 'dir-108', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-602', 'email-campaign:template:create', '新建邮件模板', '新建邮件内容模板', NULL, NULL, 'dir-108', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-603', 'email-campaign:template:update', '修改邮件模板', '修改邮件内容模板', NULL, NULL, 'dir-108', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-604', 'email-campaign:template:delete', '删除邮件模板', '删除邮件内容模板', NULL, NULL, 'dir-108', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-605', 'email-campaign:record:list', '发送记录列表', '查看邮件发送记录', NULL, NULL, 'dir-108', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-606', 'email-campaign:record:create', '创建发送任务', '执行邮件群发任务', NULL, NULL, 'dir-108', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-607', 'email-campaign:test-send', '测试发送邮件', '发送测试邮件给指定地址', NULL, NULL, 'dir-108', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-608', 'email-campaign:record:send', '立即发送邮件', '立即发送邮件任务', NULL, NULL, 'dir-108', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-701', 'social-account:list', '社交账号列表', '查看社交账号分页列表', NULL, NULL, 'dir-109', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-702', 'social-account:view', '社交账号详情', '查看单个社交账号', NULL, NULL, 'dir-109', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-703', 'social-account:create', '添加社交账号', '新增社交平台账号', NULL, NULL, 'dir-109', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-704', 'social-account:update', '编辑社交账号', '修改社交账号信息', NULL, NULL, 'dir-109', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-705', 'social-account:delete', '删除社交账号', '解绑并删除社交账号', NULL, NULL, 'dir-109', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-706', 'social-account:logs', '社交推送日志', '查看社交推送日志列表', NULL, NULL, 'dir-109', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-707', 'content-generation:list', '内容生成列表', '查看内容生成模板列表', NULL, NULL, 'dir-110', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-708', 'content-generation:view', '查看内容模板', '查看内容模板详情', NULL, NULL, 'dir-110', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-709', 'content-generation:create', '新增内容模板', '创建新的内容模板', NULL, NULL, 'dir-110', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-710', 'content-generation:update', '更新内容模板', '修改已有内容模板', NULL, NULL, 'dir-110', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-711', 'content-generation:delete', '删除内容模板', '删除指定内容模板', NULL, NULL, 'dir-110', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-712', 'behavior-log:list', '行为日志列表', '查看行为日志分页列表', NULL, NULL, 'dir-111', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-713', 'behavior-log:view', '行为日志详情', '查看单条行为日志的详细信息', NULL, NULL, 'dir-111', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-801', 'dashboard:stats', '查看统计数据', '查看控制台核心指标', NULL, NULL, 'dir-112', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-802', 'dashboard:trend', '查看趋势图', '查看邮件发送和客户增长趋势', NULL, NULL, 'dir-112', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-803', 'dashboard:tasks', '查看任务列表', '查看最近任务表格数据', NULL, NULL, 'dir-112', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-804', 'dashboard:task-view', '查看任务详情', '查看任务抽屉详情', NULL, NULL, 'dir-112', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-805', 'dashboard:task-create', '创建任务', '允许在控制台创建新任务', NULL, NULL, 'dir-112', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-901', 'task:list', '查看任务列表', '获取分页+过滤任务数据', NULL, NULL, 'dir-113', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-902', 'task:create', '创建任务', '新建任务', NULL, NULL, 'dir-113', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-903', 'task:update', '编辑任务', '编辑已有任务', NULL, NULL, 'dir-113', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-904', 'task:view', '查看任务详情', '获取单个任务详情', NULL, NULL, 'dir-113', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-905', 'task:delete', '删除任务', '删除指定任务', NULL, NULL, 'dir-113', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-906', 'task:toggle', '启用/停用任务', '切换任务启用状态', NULL, NULL, 'dir-113', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-907', 'settings:view', '查看系统设置', '查看系统设置页的内容', NULL, NULL, 'dir-114', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-908', 'settings:update', '修改系统设置', '保存系统设置中的基础信息、通知配置、安全策略', NULL, NULL, 'dir-114', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-909', 'product:list', '产品列表', '查看产品分页列表', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-910', 'product:view', '产品详情', '查看产品详细信息', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-911', 'product:create', '新增产品', '新增产品记录', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-912', 'product:update', '修改产品', '编辑产品记录', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-913', 'product:delete', '删除产品', '删除产品记录', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-914', 'product:upload', '上传产品文件', '上传产品图片/视频/PDF规格书', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-915', 'whatsapp:list', 'WhatsApp列表', '查看WhatsApp营销列表', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-916', 'whatsapp:send', 'WhatsApp发送', '发送WhatsApp消息', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-917', 'whatsapp:template', 'WhatsApp模板', '管理WhatsApp模板', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-918', 'customer:tag:list', '客户标签列表', '查看客户标签列表', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-919', 'customer:tag:create', '新增客户标签', '创建客户标签', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-920', 'customer:tag:update', '修改客户标签', '编辑客户标签', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-921', 'customer:tag:delete', '删除客户标签', '删除客户标签', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-922', 'customer:import:upload', '数据导入上传', '上传客户数据文件', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-923', 'customer:import:process', '数据导入处理', '处理导入的客户数据', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_permission` VALUES ('perm-product-batch', 'product:batch', '批量操作', '可以进行产品批量操作', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 'product');
INSERT INTO `sys_permission` VALUES ('perm-product-edit', 'product:edit', '编辑产品', '可以编辑产品信息', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 'product');
INSERT INTO `sys_permission` VALUES ('perm-product-export', 'product:export', '导出产品', '可以导出产品数据', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 'product');
INSERT INTO `sys_permission` VALUES ('perm-product-import', 'product:import', '导入产品', '可以批量导入产品', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 'product');
INSERT INTO `sys_permission` VALUES ('perm-product-stats', 'product:stats', '产品统计', '可以查看产品统计信息', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 'product');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `status` tinyint(1) NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('9d5c274d-ab49-4f09-8ad5-46798d3804b3', '测试', '1111', 1);
INSERT INTO `sys_role` VALUES ('role-001', '超级管理员', '拥有系统全部权限', 1);

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`  (
  `role_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `permission_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`role_id`, `permission_id`) USING BTREE,
  INDEX `permission_id`(`permission_id`) USING BTREE,
  CONSTRAINT `sys_role_permission_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `sys_role_permission_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `sys_permission` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '启用状态 1启用 0禁用',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('13efcf2a-13d7-4172-876e-7686f7fce04d', '1314521', '$2a$10$ht82nSw2pOy5lQn6vlUCsui1x2nG5RBVR041sG/eD6ft1rY0GL3Qq', 'admin@qq.com', '2025-07-24 08:53:43', 1);
INSERT INTO `sys_user` VALUES ('user-001', 'admin', '$2a$10$n8Or7YhneIP8gnRhvhFkteYbNdVYv10Gk03fiZlz5wp41aE59EV/6', NULL, '2025-07-25 09:23:40', 1);

-- ----------------------------
-- Table structure for system_settings
-- ----------------------------
DROP TABLE IF EXISTS `system_settings`;
CREATE TABLE `system_settings`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `site_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `brand_color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `logo_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `language` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `notify_enabled` tinyint(1) NULL DEFAULT NULL,
  `notify_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `notify_channel` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `password_strength` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `device_limit` int(11) NULL DEFAULT NULL,
  `two_factor` tinyint(1) NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `basic_settings` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `notification_settings` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `security_settings` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_settings
-- ----------------------------
INSERT INTO `system_settings` VALUES ('default', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-07-24 15:28:11', '2025-08-14 08:03:56', '{\"siteName\":\"11111\",\"brandColor\":\"\",\"logoUrl\":\"\",\"language\":\"zh\"}', '{}', '{\"passwordStrength\":\"中\",\"deviceLimit\":3,\"twoFactor\":true}');
INSERT INTO `system_settings` VALUES ('settings', '营销平台', '#409EFF', '', 'zh', 0, 'Email', '', '中', 1, 0, '2025-07-24 14:10:36', '2025-07-24 14:10:36', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `user_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `role_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES ('13efcf2a-13d7-4172-876e-7686f7fce04d', '9d5c274d-ab49-4f09-8ad5-46798d3804b3');
INSERT INTO `user_role` VALUES ('484c45d9-b98f-4b8f-863b-7fdcad97c0a8', 'role-001');
INSERT INTO `user_role` VALUES ('648c3765-103a-4acd-98b5-cedd463534c0', 'role-001');
INSERT INTO `user_role` VALUES ('dc87aab7-ddf8-4f6c-8d24-fed38b36d092', 'f51e9101-dbce-45a9-9028-5fa9db7872f3');
INSERT INTO `user_role` VALUES ('e9b763ad-c080-4707-9555-119f30cf5136', 'role-001');
INSERT INTO `user_role` VALUES ('user-001', 'role-001');

-- ----------------------------
-- Table structure for whatsapp_accounts
-- ----------------------------
DROP TABLE IF EXISTS `whatsapp_accounts`;
CREATE TABLE `whatsapp_accounts`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `business_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '企业名称',
  `phone_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '手机号码',
  `access_token` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '访问令牌',
  `webhook_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Webhook URL',
  `status` enum('active','inactive','suspended') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'inactive' COMMENT '账号状态',
  `verified` tinyint(1) NULL DEFAULT 0 COMMENT '是否已验证',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_phone_number`(`phone_number`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'WhatsApp账号配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of whatsapp_accounts
-- ----------------------------

-- ----------------------------
-- Table structure for whatsapp_send_logs
-- ----------------------------
DROP TABLE IF EXISTS `whatsapp_send_logs`;
CREATE TABLE `whatsapp_send_logs`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `account_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'WhatsApp账号ID',
  `template_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板ID',
  `recipient_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '接收方手机号',
  `message_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '消息内容',
  `message_type` enum('template','text','media') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'template' COMMENT '消息类型',
  `send_status` enum('pending','sent','delivered','read','failed') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'pending' COMMENT '发送状态',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '错误信息',
  `sent_at` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `delivered_at` datetime NULL DEFAULT NULL COMMENT '送达时间',
  `read_at` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account_id`(`account_id`) USING BTREE,
  INDEX `idx_recipient`(`recipient_phone`) USING BTREE,
  INDEX `idx_status`(`send_status`) USING BTREE,
  CONSTRAINT `fk_whatsapp_logs_account` FOREIGN KEY (`account_id`) REFERENCES `whatsapp_accounts` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'WhatsApp发送记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of whatsapp_send_logs
-- ----------------------------

-- ----------------------------
-- Table structure for whatsapp_templates
-- ----------------------------
DROP TABLE IF EXISTS `whatsapp_templates`;
CREATE TABLE `whatsapp_templates`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `account_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'WhatsApp账号ID',
  `template_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板名称',
  `language_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '语言代码',
  `category` enum('marketing','utility','authentication') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'marketing' COMMENT '模板类别',
  `header_type` enum('text','image','video','document') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头部类型',
  `header_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '头部内容',
  `body_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '正文内容',
  `footer_content` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '底部内容',
  `button_config` json NULL COMMENT '按钮配置',
  `status` enum('pending','approved','rejected') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'pending' COMMENT '审核状态',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account_id`(`account_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  CONSTRAINT `fk_whatsapp_templates_account` FOREIGN KEY (`account_id`) REFERENCES `whatsapp_accounts` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'WhatsApp消息模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of whatsapp_templates
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
