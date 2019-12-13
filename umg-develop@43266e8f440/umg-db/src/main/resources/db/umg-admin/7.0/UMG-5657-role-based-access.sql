
use umg_admin;

DROP TABLE if exists USER_ROLES;
DROP TABLE if exists ROLES;
CREATE TABLE `ROLES` (
	`ROLE` VARCHAR(100) NOT NULL COLLATE 'utf8_bin',
	`Id` CHAR(36) NOT NULL COLLATE 'utf8_bin',
	PRIMARY KEY (`Id`),
	UNIQUE INDEX `Id` (`Id`),
	UNIQUE INDEX `ROLE` (`ROLE`)
);


ALTER TABLE `USERS_LOGIN_AUDIT` DROP FOREIGN KEY `FK_USERS_LOGIN_AUDIT_KEY`;
    
ALTER TABLE `USERS_LOGIN_AUDIT` DROP INDEX `FK_USERS_LOGIN_AUDIT_KEY`;

ALTER TABLE `USERS` DROP INDEX `unq_user_per_tennant`;

ALTER TABLE `USERS` DROP PRIMARY KEY;

ALTER TABLE `USERS` DROP COLUMN `TENANT_CODE`;

ALTER TABLE `USERS` ADD COLUMN `Id` CHAR(36) NOT NULL FIRST;

update USERS set Id=(SELECT uuid());

ALTER TABLE `USERS` ADD PRIMARY KEY (`Id`);

ALTER TABLE `USERS` ADD UNIQUE INDEX `username` (`username`);

ALTER TABLE `USERS_LOGIN_AUDIT` ADD CONSTRAINT `FK_users_login_audit_users` FOREIGN KEY (`username`) REFERENCES `USERS` (`username`);
	
ALTER TABLE `USERS_LOGIN_AUDIT` ALTER `tenant_code` DROP DEFAULT;
	
ALTER TABLE `USERS_LOGIN_AUDIT` CHANGE COLUMN `tenant_code` `tenant_code` VARCHAR(45) NULL COLLATE 'utf8_bin' AFTER `username`;

ALTER TABLE `USERS` ADD COLUMN `sys_admin` ENUM('true','false') NOT NULL DEFAULT 'false' COMMENT 'to set user as sys-admin' AFTER `NAME`;

	
	
DROP TABLE IF EXISTS PERMISSION_ROLES_MAPPING;

DROP TABLE IF EXISTS PERMISSIONS;

DROP TABLE IF EXISTS USER_ROLES_TENANT_MAPPING;

DROP TABLE IF EXISTS TENANT_ROLES_MAPPING;

DROP TABLE IF EXISTS TENANT_USER_MAPPING;

DROP TABLE IF EXISTS  TENANT_USER_TENANT_ROLE_MAPPING;


CREATE TABLE `TENANT_USER_MAPPING` (
	`Id` VARCHAR(50) NOT NULL COLLATE 'utf8_bin',
	`user_id` VARCHAR(50) NOT NULL COLLATE 'utf8_bin',
	`tenant_code` VARCHAR(50) NOT NULL COLLATE 'utf8_bin',
	PRIMARY KEY (`Id`),
	UNIQUE INDEX `user_id_tenant_id` (`user_id`, `tenant_code`),
	INDEX `FK_tenant_user_mapping_tenant_code` (`tenant_code`),
	CONSTRAINT `FK_tenant_user_mapping_tenant_code` FOREIGN KEY (`tenant_code`) REFERENCES `TENANT` (`CODE`),
	CONSTRAINT `FK_tenant_user_mapping_users_id` FOREIGN KEY (`user_id`) REFERENCES `USERS` (`Id`)
);

CREATE TABLE `TENANT_ROLES_MAPPING` (
	`Id` VARCHAR(50) NOT NULL COLLATE 'utf8_bin',
	`roles_id` VARCHAR(50) NOT NULL COLLATE 'utf8_bin',
	`tenant_code` VARCHAR(50) NOT NULL COLLATE 'utf8_bin',
	PRIMARY KEY (`Id`),
	UNIQUE INDEX `role_id_tnt_code_unq` (`roles_id`, `tenant_code`),
	INDEX `FK_user_roles_mapping_user_roles` (`roles_id`),
	INDEX `FK_tenant_roles_mapping_tenant` (`tenant_code`),
	CONSTRAINT `FK_tenant_roles_mapping_tenant` FOREIGN KEY (`tenant_code`) REFERENCES `TENANT` (`CODE`),
	CONSTRAINT `FK_user_roles_mapping_user_roles` FOREIGN KEY (`roles_id`) REFERENCES `ROLES` (`Id`)
);

CREATE TABLE `TENANT_USER_TENANT_ROLE_MAPPING` (
	`Id` VARCHAR(50) NOT NULL COLLATE 'utf8_bin',
	`tenant_user_map_id` VARCHAR(50) NOT NULL COLLATE 'utf8_bin',
	`tenant_role_map_id` VARCHAR(50) NOT NULL COLLATE 'utf8_bin',
	PRIMARY KEY (`Id`),
	CONSTRAINT `FK_tenant_user_role_mapping_tenant_roles_mapping` FOREIGN KEY (`tenant_role_map_id`) REFERENCES `TENANT_ROLES_MAPPING` (`Id`),
	CONSTRAINT `FK_tenant_user_role_mapping_tenant_user_mapping` FOREIGN KEY (`tenant_user_map_id`) REFERENCES `TENANT_USER_MAPPING` (`Id`),
	INDEX `FK_tenant_user_role_mapping_tenant_user_mapping` (`tenant_user_map_id`),
	INDEX `FK_tenant_user_role_mapping_tenant_roles_mapping` (`tenant_role_map_id`),
	UNIQUE INDEX `tnt_usr_tnt_role_unq` (`tenant_user_map_id`, `tenant_role_map_id`)
);

CREATE TABLE `PERMISSIONS` (
	`Id` VARCHAR(50) NOT NULL COLLATE 'utf8_bin',
	`permission` VARCHAR(100) NOT NULL COLLATE 'utf8_bin',
	`permission_type` VARCHAR(50) NOT NULL COLLATE 'utf8_bin',
	`ui_element_id` VARCHAR(500) NULL DEFAULT NULL COLLATE 'utf8_bin',
	PRIMARY KEY (`Id`)
);


CREATE TABLE `PERMISSION_ROLES_MAPPING` (
	`id` VARCHAR(50) NOT NULL COLLATE 'utf8_bin',
	`tenant_roles_map_id` VARCHAR(50) NOT NULL COLLATE 'utf8_bin',
	`permission_id` VARCHAR(50) NOT NULL COLLATE 'utf8_bin',
	PRIMARY KEY (`id`),
	CONSTRAINT `FK_permission_roles_mapping_tenant_roles_mapping` FOREIGN KEY (`tenant_roles_map_id`) REFERENCES `TENANT_ROLES_MAPPING` (`Id`),
	CONSTRAINT `FK_permission_roles_mapping_user_permissions` FOREIGN KEY (`permission_id`) REFERENCES `PERMISSIONS` (`Id`),
	INDEX `FK_permission_roles_mapping_user_permissions` (`permission_id`),
	INDEX `FK_permission_roles_mapping_tenant_roles_mapping` (`tenant_roles_map_id`),
	UNIQUE INDEX `perm_id_tnt_rol_map_uniq` (`tenant_roles_map_id`, `permission_id`)
);


COMMIT;