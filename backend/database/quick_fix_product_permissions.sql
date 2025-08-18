-- 快速修复产品权限 - 基于现有权限系统
-- 在现有的permissions表中添加产品权限

-- ===========================================
-- 第一步：在现有permissions表中添加产品权限
-- ===========================================

-- 添加产品相关权限到现有permissions表
INSERT IGNORE INTO `permissions` (id, permission_key, name, description, is_active) VALUES
('perm-product-view-new', 'product:view', '查看产品', '可以查看产品列表和详情', 1),
('perm-product-create-new', 'product:create', '创建产品', '可以创建新产品', 1),
('perm-product-upload-new', 'product:upload', '上传文件', '可以上传产品相关文件', 1),
('perm-product-update-new', 'product:update', '更新产品', '可以更新产品信息', 1),
('perm-product-delete-new', 'product:delete', '删除产品', '可以删除产品', 1);

-- ===========================================
-- 第二步：为admin用户的角色分配产品权限
-- ===========================================

-- 为role-001（admin角色）分配新的产品权限
INSERT IGNORE INTO `role_permission` (role_id, permission_id) VALUES
('role-001', 'perm-product-view-new'),
('role-001', 'perm-product-create-new'),
('role-001', 'perm-product-upload-new'),
('role-001', 'perm-product-update-new'),
('role-001', 'perm-product-delete-new');

-- 为超级管理员角色也分配产品权限
INSERT IGNORE INTO `role_permission` (role_id, permission_id) VALUES
('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-product-view-new'),
('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-product-create-new'),
('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-product-upload-new'),
('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-product-update-new'),
('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-product-delete-new');

-- ===========================================
-- 第三步：验证权限配置
-- ===========================================

-- 验证admin用户的产品权限
SELECT '=== admin用户产品权限检查 ===' as info;
SELECT 
    u.username as '用户名',
    p.permission_key as '权限代码',
    p.name as '权限名称'
FROM sys_user u
JOIN user_role ur ON u.id = ur.user_id
JOIN role_permission rp ON ur.role_id = rp.role_id
JOIN permissions p ON rp.permission_id = p.id
WHERE u.username = 'admin' 
AND p.permission_key LIKE 'product:%'
ORDER BY p.permission_key;

-- 显示所有产品权限
SELECT '=== 所有产品权限 ===' as info;
SELECT 
    id as '权限ID',
    permission_key as '权限代码',
    name as '权限名称',
    description as '权限描述'
FROM permissions 
WHERE permission_key LIKE 'product:%'
ORDER BY permission_key;

SELECT '=== 快速权限修复完成！===' as '状态';
SELECT '请重启后端服务并重新登录测试' as '下一步操作';