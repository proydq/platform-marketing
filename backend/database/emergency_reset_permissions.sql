-- 紧急重置权限，确保登录功能正常

-- 1. 删除可能有问题的产品权限
DELETE FROM `role_permission` WHERE `permission_id` LIKE 'perm-product-%';
DELETE FROM `sys_permission` WHERE `id` LIKE 'perm-product-%';

-- 2. 确保基本权限存在且正确
-- 检查admin用户是否有基本角色
SELECT 
    '用户角色检查' as '检查项',
    CASE 
        WHEN EXISTS (
            SELECT 1 FROM user u
            JOIN user_role ur ON u.id = ur.user_id
            WHERE u.username = 'admin'
        ) THEN 'admin用户有角色分配 ✓'
        ELSE 'admin用户无角色分配 ✗'
    END as '检查结果';

-- 3. 如果admin没有角色，重新分配超级管理员角色
INSERT IGNORE INTO `user_role` (`user_id`, `role_id`) 
SELECT u.id, '9d5c274d-ab49-4f09-8ad5-46798d3804b3'
FROM `user` u 
WHERE u.username = 'admin'
AND NOT EXISTS (
    SELECT 1 FROM user_role ur 
    WHERE ur.user_id = u.id 
    AND ur.role_id = '9d5c274d-ab49-4f09-8ad5-46798d3804b3'
);

-- 4. 验证登录相关的基本权限
SELECT 
    u.username as '用户名',
    u.id as '用户ID',
    r.name as '角色名',
    r.id as '角色ID'
FROM user u
LEFT JOIN user_role ur ON u.id = ur.user_id
LEFT JOIN role r ON ur.role_id = r.id
WHERE u.username = 'admin';

-- 5. 显示当前系统状态
SELECT 
    (SELECT COUNT(*) FROM user WHERE username = 'admin') as 'Admin用户数',
    (SELECT COUNT(*) FROM user_role ur JOIN user u ON ur.user_id = u.id WHERE u.username = 'admin') as 'Admin角色分配数',
    (SELECT COUNT(*) FROM role WHERE id = '9d5c274d-ab49-4f09-8ad5-46798d3804b3') as '超级管理员角色数',
    (SELECT COUNT(*) FROM role_permission WHERE role_id = '9d5c274d-ab49-4f09-8ad5-46798d3804b3') as '超级管理员权限数';