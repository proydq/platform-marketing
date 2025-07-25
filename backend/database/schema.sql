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
