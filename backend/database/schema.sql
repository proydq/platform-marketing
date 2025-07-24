-- SQL schema for permission module
ALTER TABLE sys_permission
    ADD COLUMN `module` varchar(255) DEFAULT NULL;
