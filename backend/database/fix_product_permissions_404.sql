-- 修复产品权限404错误
-- 确保所有产品相关权限正确分配给超级管理员角色

-- 1. 插入产品相关权限（如果不存在）
INSERT IGNORE INTO `sys_permission` (`id`, `code`, `name`, `description`, `resource_type`, `resource_id`, `operation`, `conditions`, `data_scope`, `enabled`, `created_at`, `updated_at`, `created_by`, `updated_by`, `remark`) VALUES
('perm-product-view', 'product:view', '查看产品', '查看产品列表和详情', 'product', NULL, 'view', NULL, NULL, 1, NOW(), NOW(), 'system', 'system', '产品查看权限'),
('perm-product-create', 'product:create', '创建产品', '创建新产品', 'product', NULL, 'create', NULL, NULL, 1, NOW(), NOW(), 'system', 'system', '产品创建权限'),
('perm-product-update', 'product:update', '编辑产品', '编辑产品信息', 'product', NULL, 'update', NULL, NULL, 1, NOW(), NOW(), 'system', 'system', '产品编辑权限'),
('perm-product-delete', 'product:delete', '删除产品', '删除产品', 'product', NULL, 'delete', NULL, NULL, 1, NOW(), NOW(), 'system', 'system', '产品删除权限'),
('perm-product-upload', 'product:upload', '上传文件', '上传产品相关文件', 'product', NULL, 'upload', NULL, NULL, 1, NOW(), NOW(), 'system', 'system', '产品文件上传权限'),
('perm-product-category-view', 'product:category:view', '查看产品分类', '查看产品分类', 'product', NULL, 'view', NULL, NULL, 1, NOW(), NOW(), 'system', 'system', '产品分类查看权限'),
('perm-product-category-manage', 'product:category:manage', '管理产品分类', '创建、编辑、删除产品分类', 'product', NULL, 'manage', NULL, NULL, 1, NOW(), NOW(), 'system', 'system', '产品分类管理权限');

-- 2. 将产品权限分配给超级管理员角色
INSERT IGNORE INTO `role_permission` (`role_id`, `permission_id`) VALUES
('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-product-view'),
('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-product-create'),
('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-product-update'),
('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-product-delete'),
('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-product-upload'),
('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-product-category-view'),
('9d5c274d-ab49-4f09-8ad5-46798d3804b3', 'perm-product-category-manage');

-- 3. 也给role-001分配权限（如果存在）
INSERT IGNORE INTO `role_permission` (`role_id`, `permission_id`) VALUES
('role-001', 'perm-product-view'),
('role-001', 'perm-product-create'),
('role-001', 'perm-product-update'),
('role-001', 'perm-product-delete'),
('role-001', 'perm-product-upload'),
('role-001', 'perm-product-category-view'),
('role-001', 'perm-product-category-manage');

-- 4. 验证权限分配情况
SELECT 
    '权限分配检查' as '检查项',
    CASE 
        WHEN EXISTS (
            SELECT 1 FROM role_permission rp 
            JOIN sys_permission p ON rp.permission_id = p.id 
            WHERE rp.role_id = '9d5c274d-ab49-4f09-8ad5-46798d3804b3' 
            AND p.code = 'product:upload'
        ) THEN '超级管理员有product:upload权限 ✓'
        ELSE '超级管理员无product:upload权限 ✗'
    END as '检查结果';

-- 5. 显示当前用户权限
SELECT 
    u.username as '用户名',
    r.name as '角色名',
    p.code as '权限代码',
    p.name as '权限名称'
FROM user u
JOIN user_role ur ON u.id = ur.user_id
JOIN role r ON ur.role_id = r.id
JOIN role_permission rp ON r.id = rp.role_id
JOIN sys_permission p ON rp.permission_id = p.id
WHERE p.code LIKE 'product:%'
ORDER BY u.username, p.code;