-- 完全回滚脚本 - 恢复到初始工作状态

-- 1. 删除所有产品相关的权限和角色分配
DELETE FROM `role_permission` WHERE `permission_id` IN (
    SELECT `id` FROM `sys_permission` WHERE `code` LIKE 'product:%'
);

DELETE FROM `sys_permission` WHERE `code` LIKE 'product:%';

-- 2. 删除任何临时添加的产品权限
DELETE FROM `role_permission` WHERE `permission_id` LIKE 'perm-product%';
DELETE FROM `sys_permission` WHERE `id` LIKE 'perm-product%';

-- 3. 确保admin用户的基本角色分配正确
DELETE FROM `user_role` WHERE `user_id` IN (SELECT `id` FROM `user` WHERE `username` = 'admin');

INSERT INTO `user_role` (`user_id`, `role_id`) 
SELECT u.id, '9d5c274d-ab49-4f09-8ad5-46798d3804b3'
FROM `user` u 
WHERE u.username = 'admin';

-- 4. 验证恢复结果
SELECT 'Admin用户角色检查' as '检查项',
CASE 
    WHEN EXISTS (
        SELECT 1 FROM user u
        JOIN user_role ur ON u.id = ur.user_id
        WHERE u.username = 'admin' AND ur.role_id = '9d5c274d-ab49-4f09-8ad5-46798d3804b3'
    ) THEN 'Admin有超级管理员角色 ✓'
    ELSE 'Admin缺少角色 ✗'
END as '状态';

-- 5. 清理可能的缓存问题
FLUSH PRIVILEGES;

-- 6. 显示当前admin用户状态
SELECT 
    u.username as '用户名',
    u.enabled as '启用状态',
    r.name as '角色名'
FROM user u
LEFT JOIN user_role ur ON u.id = ur.user_id  
LEFT JOIN role r ON ur.role_id = r.id
WHERE u.username = 'admin';