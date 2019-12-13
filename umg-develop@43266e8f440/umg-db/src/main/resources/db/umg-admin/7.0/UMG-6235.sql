use umg_admin;

ET FOREIGN_KEY_CHECKS = 0;

ALTER TABLE TENANT_ROLES_MAPPING CHANGE COLUMN `tenant_code` `tenant_code` VARCHAR(10) NOT NULL COMMENT 'Tenant code used for unique identificaiton' COLLATE 'utf8_bin';

ALTER TABLE TENANT_USER_MAPPING CHANGE COLUMN `tenant_code` `tenant_code` VARCHAR(10) NOT NULL COMMENT 'Tenant code used for unique identificaiton' COLLATE 'utf8_bin';
ALTER TABLE USERS_LOGIN_AUDIT CHANGE COLUMN `tenant_code` `tenant_code` VARCHAR(10) NOT NULL COMMENT 'Tenant code used for unique identificaiton' COLLATE 'utf8_bin';
ALTER TABLE TENANT CHANGE COLUMN `CODE` `CODE` VARCHAR(10) NOT NULL COMMENT 'Tenant code used for unique identificaiton' COLLATE 'utf8_bin';

SET FOREIGN_KEY_CHECKS = 1;


Commit;
