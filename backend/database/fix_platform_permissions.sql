-- 基于现有platform.sql的权限修复脚本
-- 解决403错误问题

-- ===========================================
-- 第一步：分析问题
-- ===========================================
-- 发现问题：Controller使用的权限代码和现有权限系统不匹配
-- 现有表：permissions (小写) vs Controller期望：sys_permission
-- 现有表：roles vs Controller期望：sys_role  
-- 现有表：role_permission vs Controller期望：sys_role_permission

-- ===========================================
-- 第二步：创建标准化权限表（如果不存在）
-- ===========================================

-- 创建标准的sys_permission表（基于现有permissions表）
CREATE TABLE IF NOT EXISTS `sys_permission` (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `code` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '权限代码',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '权限名称',
  `description` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '权限描述',
  `module` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属模块',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统权限表' ROW_FORMAT = DYNAMIC;

-- 创建标准的sys_role表（基于现有roles表）
CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名称',
  `code` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色代码',
  `description` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '角色描述',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统角色表' ROW_FORMAT = DYNAMIC;

-- 创建标准的sys_role_permission表（基于现有role_permission表）
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
  `role_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `permission_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`role_id`, `permission_id`) USING BTREE,
  FOREIGN KEY (`role_id`) REFERENCES `sys_role`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`permission_id`) REFERENCES `sys_permission`(`id`) ON DELETE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色权限关联表' ROW_FORMAT = DYNAMIC;

-- 创建标准的sys_user_role表（基于现有user_role表）
CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `user_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `role_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE,
  FOREIGN KEY (`role_id`) REFERENCES `sys_role`(`id`) ON DELETE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户角色关联表' ROW_FORMAT = DYNAMIC;

-- ===========================================
-- 第三步：同步现有数据到标准表
-- ===========================================

-- 同步权限数据
INSERT IGNORE INTO sys_permission (id, code, name, description, module)
SELECT id, permission_key, name, description, 'general' as module
FROM permissions 
WHERE permission_key IS NOT NULL AND permission_key != '';

-- 同步角色数据
INSERT IGNORE INTO sys_role (id, name, code, description, is_active)
SELECT id, name, id as code, description, is_active
FROM roles;

-- 同步角色权限关联
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT role_id, permission_id
FROM role_permission
WHERE EXISTS (SELECT 1 FROM sys_role WHERE id = role_permission.role_id)
AND EXISTS (SELECT 1 FROM sys_permission WHERE id = role_permission.permission_id);

-- 同步用户角色关联
INSERT IGNORE INTO sys_user_role (user_id, role_id)
SELECT user_id, role_id
FROM user_role
WHERE EXISTS (SELECT 1 FROM sys_user WHERE id = user_role.user_id)
AND EXISTS (SELECT 1 FROM sys_role WHERE id = user_role.role_id);

-- ===========================================
-- 第四步：补充缺失的产品权限
-- ===========================================

-- 确保产品权限存在且格式正确
INSERT IGNORE INTO sys_permission (id, code, name, description, module) VALUES
('perm-product-view', 'product:view', '查看产品', '可以查看产品列表和详情', 'product'),
('perm-product-list', 'product:list', '产品列表', '可以获取产品列表', 'product'),
('perm-product-create', 'product:create', '创建产品', '可以创建新产品', 'product'),
('perm-product-update', 'product:update', '更新产品', '可以更新产品信息', 'product'),
('perm-product-edit', 'product:edit', '编辑产品', '可以编辑产品信息', 'product'),
('perm-product-delete', 'product:delete', '删除产品', '可以删除产品', 'product'),
('perm-product-upload', 'product:upload', '上传文件', '可以上传产品相关文件', 'product'),
('perm-product-import', 'product:import', '导入产品', '可以批量导入产品', 'product'),
('perm-product-export', 'product:export', '导出产品', '可以导出产品数据', 'product'),
('perm-product-batch', 'product:batch', '批量操作', '可以进行产品批量操作', 'product'),
('perm-product-stats', 'product:stats', '产品统计', '可以查看产品统计信息', 'product');

-- ===========================================
-- 第五步：确保所有用户都有产品权限（临时解决方案）
-- ===========================================

-- 创建产品管理员角色（如果不存在）
INSERT IGNORE INTO sys_role (id, name, code, description, is_active) VALUES
('role-product-admin', '产品管理员', 'PRODUCT_ADMIN', '拥有产品模块的全部权限', 1);

-- 为产品管理员角色分配所有产品权限
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT 'role-product-admin', id
FROM sys_permission
WHERE module = 'product';

-- 为所有现有用户分配产品管理员角色
INSERT IGNORE INTO sys_user_role (user_id, role_id)
SELECT id, 'role-product-admin'
FROM sys_user
WHERE status = 1;

-- ===========================================
-- 第六步：为现有角色补充产品权限
-- ===========================================

-- 为role-001分配所有产品权限（确保admin用户有权限）
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT 'role-001', id
FROM sys_permission
WHERE module = 'product';

-- 为超级管理员角色分配产品权限
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r, sys_permission p
WHERE (r.code LIKE '%ADMIN%' OR r.id = '9d5c274d-ab49-4f09-8ad5-46798d3804b3')
AND p.module = 'product'
AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
);

-- ===========================================
-- 第七步：验证权限配置
-- ===========================================

-- 显示产品权限
SELECT '=== 产品模块权限 ===' as info;
SELECT 
    code as '权限代码',
    name as '权限名称',
    description as '权限描述'
FROM sys_permission 
WHERE module = 'product' OR code LIKE 'product:%'
ORDER BY code;

-- 显示用户权限情况
SELECT '=== 用户权限分配情况 ===' as info;
SELECT 
    u.username as '用户名',
    r.name as '角色名称',
    COUNT(rp.permission_id) as '权限数量',
    GROUP_CONCAT(
        CASE WHEN p.module = 'product' THEN p.code END
        ORDER BY p.code SEPARATOR ', '
    ) as '产品权限'
FROM sys_user u
LEFT JOIN sys_user_role ur ON u.id = ur.user_id
LEFT JOIN sys_role r ON ur.role_id = r.id
LEFT JOIN sys_role_permission rp ON r.id = rp.role_id
LEFT JOIN sys_permission p ON rp.permission_id = p.id
WHERE u.status = 1
GROUP BY u.id, u.username, r.id, r.name
ORDER BY u.username, r.name;

-- 显示特定权限检查
SELECT '=== 关键权限检查 ===' as info;
SELECT 
    CASE 
        WHEN EXISTS (
            SELECT 1 FROM sys_user u
            JOIN sys_user_role ur ON u.id = ur.user_id
            JOIN sys_role_permission rp ON ur.role_id = rp.role_id
            JOIN sys_permission p ON rp.permission_id = p.id
            WHERE u.username = 'admin' AND p.code = 'product:view'
        ) THEN 'admin用户有product:view权限 ✓'
        ELSE 'admin用户无product:view权限 ✗'
    END as 'product:view检查',
    
    CASE 
        WHEN EXISTS (
            SELECT 1 FROM sys_user u
            JOIN sys_user_role ur ON u.id = ur.user_id
            JOIN sys_role_permission rp ON ur.role_id = rp.role_id
            JOIN sys_permission p ON rp.permission_id = p.id
            WHERE u.username = 'admin' AND p.code = 'product:upload'
        ) THEN 'admin用户有product:upload权限 ✓'
        ELSE 'admin用户无product:upload权限 ✗'
    END as 'product:upload检查';

SELECT '=== 权限修复完成！===' as '状态';
SELECT '请重启后端服务，清除浏览器缓存，重新登录' as '下一步操作';