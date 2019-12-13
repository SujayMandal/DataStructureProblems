USE `umg_admin`;

set SQL_SAFE_UPDATES = 0;

DROP TABLE IF EXISTS NOTIFICATION_TYPE;

CREATE TABLE NOTIFICATION_TYPE (
	ID CHAR(36) NOT NULL,
	TYPE VARCHAR(50) NOT NULL COMMENT 'Mail or SMS',
	DESCRIPTION VARCHAR(255) NULL DEFAULT NULL,
	CREATED_BY VARCHAR(32) NOT NULL,
	CREATED_ON BIGINT(20) NOT NULL,
	LAST_UPDATED_BY VARCHAR(32) NULL DEFAULT NULL,
	LAST_UPDATED_ON BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (ID)
);

ALTER TABLE NOTIFICATION_TYPE  ADD UNIQUE INDEX TYPE_UNIQUE (TYPE ASC);


DROP TABLE IF EXISTS NOTIFICATION_EVENT;

CREATE TABLE NOTIFICATION_EVENT (
	ID CHAR(36) NOT NULL,
	NAME VARCHAR(45) NOT NULL COMMENT 'Model Publishing Success event',
	DESCRIPTION VARCHAR(255) NOT NULL DEFAULT '',
	CLASSIFICATION VARCHAR(64) NOT NULL,
	CREATED_BY VARCHAR(32) NOT NULL,
	CREATED_ON BIGINT(20) NOT NULL,
	LAST_UPDATED_BY VARCHAR(32) NULL DEFAULT NULL,
	LAST_UPDATED_ON BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (ID)
);

ALTER TABLE NOTIFICATION_EVENT ADD UNIQUE INDEX NAME_UNIQUE (NAME ASC);


DROP TABLE IF EXISTS NOTIFICATION_EMAIL_TEMPLATE;

CREATE TABLE NOTIFICATION_EMAIL_TEMPLATE (
	ID CHAR(36) NOT NULL,
	NOTIFICATION_EVENT_ID CHAR(36) NOT NULL,
	NAME VARCHAR(64) NOT NULL,
	DESCRIPTION VARCHAR(256) NOT NULL DEFAULT '',
	BODY_DEFINITION MEDIUMBLOB NOT NULL COMMENT 'Mail Template Definition',
	SUBJECT_DEFINITION VARCHAR(256) NOT NULL,
    IS_ACTIVE INT(10) NOT NULL DEFAULT '1',
	MAJOR_VERSION INT(11) NOT NULL DEFAULT '1',
	MAIL_CONTENT_TYPE VARCHAR(64) NOT NULL COMMENT 'MIME Message Types',
	CREATED_BY VARCHAR(32) NOT NULL,
	CREATED_ON BIGINT(20) NOT NULL,
	LAST_UPDATED_BY VARCHAR(32) NULL DEFAULT NULL,
	LAST_UPDATED_ON BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (ID)
);


DROP TABLE IF EXISTS NOTIFICATION_SMS_TEMPLATE;

CREATE TABLE NOTIFICATION_SMS_TEMPLATE (
	ID CHAR(36) NOT NULL,
	NAME VARCHAR(64) NOT NULL,
	DESCRIPTION VARCHAR(256) NOT NULL DEFAULT '',
	SMS_DEFINITION BLOB NOT NULL COMMENT 'Model Publishing Success Template',
	IS_ACTIVE INT(10) NOT NULL DEFAULT '1',
	MAJOR_VERSION INT(11) NOT NULL DEFAULT '1',
	CREATED_BY VARCHAR(32) NOT NULL,
	CREATED_ON BIGINT(20) NOT NULL,
	LAST_UPDATED_BY VARCHAR(32) NULL DEFAULT NULL,
	LAST_UPDATED_ON BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (ID)
);


DROP TABLE IF EXISTS NOTIFICATION_EVENT_TEMPLATE_MAPPING;

CREATE TABLE NOTIFICATION_EVENT_TEMPLATE_MAPPING (
	ID CHAR(36) NOT NULL,
	NAME VARCHAR(45) NOT NULL, 	
	NOTIFICATION_EVENT_ID CHAR(36) NOT NULL,
	NOTIFICATION_TEMPLATE_ID CHAR(36) NOT NULL,
	NOTIFICATION_TYPE_ID CHAR(36) NOT NULL,
	TENANT_ID VARCHAR(45) NOT NULL,
	TO_ADDRESS VARCHAR(1500) NULL DEFAULT NULL,
	FROM_ADDRESS VARCHAR(256) NULL DEFAULT NULL,
	CC_ADDRESS VARCHAR(256) NULL DEFAULT NULL,
	BCC_ADDRESS VARCHAR(256) NULL DEFAULT NULL,
	MOBILE BIGINT(20) NULL DEFAULT NULL,
	CREATED_BY VARCHAR(32) NOT NULL,
	CREATED_ON BIGINT(20) NOT NULL,
	LAST_UPDATED_BY VARCHAR(32) NULL DEFAULT NULL,
	LAST_UPDATED_ON BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (ID)
);


DROP TABLE if exists USER_ROLES;

DROP TABLE if exists `ROLES`;

CREATE TABLE `ROLES` (
	`ROLE` VARCHAR(100) NOT NULL COMMENT 'role name' COLLATE 'utf8_bin',
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
	

DROP TABLE if exists `PERMISSION_ROLES_MAPPING`;

DROP TABLE if exists `PERMISSIONS`;

DROP TABLE if exists `TENANT_USER_TENANT_ROLE_MAPPING`;

DROP TABLE if exists  `TENANT_ROLES_MAPPING`;

DROP TABLE if exists  `TENANT_USER_MAPPING`;




CREATE TABLE `TENANT_USER_MAPPING` (
	`Id` VARCHAR(50) NOT NULL COLLATE 'utf8_bin',
	`user_id` VARCHAR(50) NOT NULL COMMENT 'id from the users table for a user' COLLATE 'utf8_bin',
	`tenant_code` VARCHAR(50) NOT NULL COMMENT 'tenant_code from the tenant table' COLLATE 'utf8_bin',
	PRIMARY KEY (`Id`),
	UNIQUE INDEX `UN_user_id_tenant_id` (`user_id`, `tenant_code`),
	INDEX `FK_tenant_user_mapping_tenant_code` (`tenant_code`),
	CONSTRAINT `FK_tenant_user_mapping_tenant_code` FOREIGN KEY (`tenant_code`) REFERENCES `TENANT` (`CODE`),
	CONSTRAINT `FK_tenant_user_mapping_users_id` FOREIGN KEY (`user_id`) REFERENCES `USERS` (`Id`)
);

CREATE TABLE `TENANT_ROLES_MAPPING` (
	`Id` VARCHAR(50) NOT NULL COLLATE 'utf8_bin',
	`roles_id` VARCHAR(50) NOT NULL COMMENT 'id from the roles table for a role' COLLATE 'utf8_bin',
	`tenant_code` VARCHAR(50) NOT NULL COMMENT 'tenant_code from the tenant table' COLLATE 'utf8_bin',
	PRIMARY KEY (`Id`),
	UNIQUE INDEX `UN_role_id_tnt_code_unq` (`roles_id`, `tenant_code`),
	INDEX `FK_user_roles_mapping_user_roles` (`roles_id`),
	INDEX `FK_tenant_roles_mapping_tenant` (`tenant_code`),
	CONSTRAINT `FK_tenant_roles_mapping_tenant` FOREIGN KEY (`tenant_code`) REFERENCES `TENANT` (`CODE`),
	CONSTRAINT `FK_user_roles_mapping_user_roles` FOREIGN KEY (`roles_id`) REFERENCES `ROLES` (`Id`)
);

CREATE TABLE `TENANT_USER_TENANT_ROLE_MAPPING` (
	`Id` VARCHAR(50) NOT NULL COLLATE 'utf8_bin',
	`tenant_user_map_id` VARCHAR(50) NOT NULL COMMENT 'id from the TENANT_USER_MAPPING table for a user mapped to tenant' COLLATE 'utf8_bin',
	`tenant_role_map_id` VARCHAR(50) NOT NULL COMMENT 'id from the TENANT_ROLES_MAPPING table for a role mapped to tenant' COLLATE 'utf8_bin',
	PRIMARY KEY (`Id`),
	CONSTRAINT `FK_tenant_user_role_mapping_tenant_roles_mapping` FOREIGN KEY (`tenant_role_map_id`) REFERENCES `TENANT_ROLES_MAPPING` (`Id`),
	CONSTRAINT `FK_tenant_user_role_mapping_tenant_user_mapping` FOREIGN KEY (`tenant_user_map_id`) REFERENCES `TENANT_USER_MAPPING` (`Id`),
	INDEX `FK_tenant_user_role_mapping_tenant_user_mapping` (`tenant_user_map_id`),
	INDEX `FK_tenant_user_role_mapping_tenant_roles_mapping` (`tenant_role_map_id`),
	UNIQUE INDEX `UN_tnt_usr_tnt_role_unq` (`tenant_user_map_id`, `tenant_role_map_id`)
);

CREATE TABLE `PERMISSIONS` (
	`Id` VARCHAR(50) NOT NULL COLLATE 'utf8_bin',
	`permission` VARCHAR(100) NOT NULL COMMENT 'permission name' COLLATE 'utf8_bin',
	`permission_type` VARCHAR(50) NOT NULL COMMENT 'permission type can be any of two values page/action' COLLATE 'utf8_bin',
	`ui_element_id` VARCHAR(500) NULL DEFAULT NULL COMMENT 'unique ID of UI element from html page' COLLATE 'utf8_bin',
	PRIMARY KEY (`Id`)
);


CREATE TABLE `PERMISSION_ROLES_MAPPING` (
	`id` VARCHAR(50) NOT NULL COLLATE 'utf8_bin',
	`tenant_roles_map_id` VARCHAR(50) NOT NULL COMMENT 'id from the TENANT_ROLES_MAPPING table for a role mapped to tenant' COLLATE 'utf8_bin',
	`permission_id` VARCHAR(50) NOT NULL COMMENT 'id from permission table for a permission' COLLATE 'utf8_bin',
	PRIMARY KEY (`id`),
	CONSTRAINT `FK_permission_roles_mapping_tenant_roles_mapping` FOREIGN KEY (`tenant_roles_map_id`) REFERENCES `TENANT_ROLES_MAPPING` (`Id`),
	CONSTRAINT `FK_permission_roles_mapping_user_permissions` FOREIGN KEY (`permission_id`) REFERENCES `PERMISSIONS` (`Id`),
	INDEX `FK_permission_roles_mapping_user_permissions` (`permission_id`),
	INDEX `FK_permission_roles_mapping_tenant_roles_mapping` (`tenant_roles_map_id`),
	UNIQUE INDEX `UN_perm_id_tnt_rol_map_uniq` (`tenant_roles_map_id`, `permission_id`)
);


/* set delimiter */
DELIMITER $$

/* remove procedure insert_default_privileges if exists... */
DROP PROCEDURE IF EXISTS insert_default_privileges $$

/* create procedure insert_default_privileges*/ 
CREATE PROCEDURE insert_default_privileges ()
BEGIN


DECLARE tenant_length INT;
DECLARE role_length INT;
DECLARE permissions_length INT;
DECLARE x INT DEFAULT 0;
DECLARE y INT DEFAULT 0;


/* drop temp table  TEMP_TABLE_TENANT */ 
DROP TABLE IF EXISTS TEMP_TABLE_TENANT;
/* create temp table  TEMP_TABLE_TENANT */
create temporary table if not exists TEMP_TABLE_TENANT
select 
	TENANT_TEMP.CODE as 'TENANT_CODE'	
from TENANT as TENANT_TEMP;

/* drop temp table  TEMP_TABLE_ROLES */
DROP TABLE IF EXISTS TEMP_TABLE_ROLES;
/* create temp table  TEMP_TABLE_TENANT */
create temporary table if not exists TEMP_TABLE_ROLES
select 
	ROLES_TEMP.ID as 'ROLE_ID'	,ROLES_TEMP.ROLE AS 'ROLE'
from ROLES as ROLES_TEMP WHERE(ROLES_TEMP.ROLE= 'ROLE_ADMIN' OR ROLES_TEMP.ROLE='ROLE_MODELER' OR ROLES_TEMP.ROLE='ROLE_TENANT_USER');

/* drop temp table  TEMP_TABLE_PERMISSIONS */
DROP TABLE IF EXISTS TEMP_TABLE_PERMISSIONS;
/* create temp table  TEMP_TABLE_PERMISSIONS */
create temporary table if not exists TEMP_TABLE_PERMISSIONS
select 
	PERMISSIONS_TEMP.ID as 'PERMISSION_ID'	
from PERMISSIONS as PERMISSIONS_TEMP;

/* drop temp table  TEMP_TABLE_TENANT_ROLES */
DROP TABLE IF EXISTS TEMP_TABLE_TENANT_ROLES;
/* create temp table  TEMP_TABLE_TENANT_ROLES */
create temporary table if not exists TEMP_TABLE_TENANT_ROLES
select 
	TENANT_ROLES_TEMP.id as 'TENANT_ROLE_ID'
from TENANT_ROLES_MAPPING as TENANT_ROLES_TEMP,ROLES AS ROLES WHERE ROLES.ID=TENANT_ROLES_TEMP.roles_id AND ROLES.ROLE IN('ROLE_ADMIN','ROLE_MODELER') GROUP BY ROLES.ROLE;

SELECT COUNT(*) FROM TEMP_TABLE_TENANT INTO tenant_length;
SELECT COUNT(*) FROM TEMP_TABLE_ROLES INTO role_length;
SELECT COUNT(*) FROM TEMP_TABLE_PERMISSIONS INTO permissions_length;
WHILE x < tenant_length DO
WHILE y < role_length DO
INSERT INTO TENANT_ROLES_MAPPING(`Id`, `roles_id`, `tenant_code`) VALUES(uuid(),(select ROLE_ID  from TEMP_TABLE_ROLES LIMIT y,1),(select TENANT_CODE  from TEMP_TABLE_TENANT LIMIT x,1));
SET y = y+1;
END WHILE;
SET x = x+1;
SET y = 0;
END WHILE;

/* Tenant roles created successfully*/ 

/* Started creating Permissions  for the roles ROLE_ADMIN */ 

SELECT 'Started creating Permissions  for the roles ROLE_ADMIN';

DROP TABLE IF EXISTS TEMP_TABLE_TENANT_ROLES;
create temporary table if not exists TEMP_TABLE_TENANT_ROLES
select 
	trm.Id as 'TENANT_ROLE_ID'
from TENANT_ROLES_MAPPING as trm,ROLES AS r WHERE r.ID=trm.roles_id AND r.ROLE IN('ROLE_ADMIN');

SET x = 0;
SET y = 0;
SELECT COUNT(*) FROM TEMP_TABLE_TENANT_ROLES INTO role_length;
WHILE x < permissions_length DO
WHILE y < role_length DO
    	INSERT INTO `PERMISSION_ROLES_MAPPING` (`id`, `tenant_roles_map_id`, `permission_id`) VALUES (uuid(), 
	(select TENANT_ROLE_ID from TEMP_TABLE_TENANT_ROLES LIMIT y,1),
	(select PERMISSION_ID from TEMP_TABLE_PERMISSIONS LIMIT x,1));
SET y=y+1;
END WHILE;
SET y = 0;
SET x=x+1;
END WHILE;
SET y = 0;
SET x = 0;

SELECT 'Permissions created successfully for the roles ROLE_ADMIN';

/* Permissions created successfully for the roles ROLE_ADMIN*/ 


/* Started creating Permissions  for the roles ROLE_MODELER*/ 

SELECT 'Started creating Permissions  for the roles ROLE_MODELER';

DROP TABLE IF EXISTS TEMP_TABLE_TENANT_ROLES;
create temporary table if not exists TEMP_TABLE_TENANT_ROLES
select 
	trm.Id as 'TENANT_ROLE_ID'
from TENANT_ROLES_MAPPING as trm,ROLES AS r WHERE r.ID=trm.roles_id AND r.ROLE IN('ROLE_MODELER');

DROP TABLE IF EXISTS TEMP_TABLE_PERMISSIONS;
create temporary table if not exists TEMP_TABLE_PERMISSIONS
select 
	PERMISSIONS_TEMP.ID as 'PERMISSION_ID'	
from PERMISSIONS as PERMISSIONS_TEMP WHERE PERMISSIONS_TEMP.permission NOT IN( 'Notifications.Add','Notifications.Manage');

SET x = 0;
SET y = 0;
SELECT COUNT(*) FROM TEMP_TABLE_TENANT_ROLES INTO role_length;
WHILE x < permissions_length-2 DO
WHILE y < role_length DO
    	INSERT INTO `PERMISSION_ROLES_MAPPING` (`id`, `tenant_roles_map_id`, `permission_id`) VALUES (uuid(), 
	(select TENANT_ROLE_ID from TEMP_TABLE_TENANT_ROLES LIMIT y,1),
	(select PERMISSION_ID from TEMP_TABLE_PERMISSIONS LIMIT x,1));
SET y=y+1;
END WHILE;
SET y = 0;
SET x=x+1;
END WHILE;
SET y = 0;
SET x = 0;

SELECT 'Permissions created successfully for the roles ROLE_MODELER';

/* Permissions created successfully for the roles ROLE_ADMIN ROLE_MODELER*/ 


SELECT 'Started creating Permissions  for the roles ROLE_TENANT_USER';

DROP TABLE IF EXISTS TEMP_TABLE_TENANT_ROLES;
create temporary table if not exists TEMP_TABLE_TENANT_ROLES
select 
	TENANT_ROLES_TEMP.Id as 'TENANT_ROLE_ID'
from TENANT_ROLES_MAPPING as TENANT_ROLES_TEMP,ROLES AS ROLES WHERE ROLES.Id=TENANT_ROLES_TEMP.roles_id AND ROLES.ROLE IN('ROLE_TENANT_USER');

DROP TABLE IF EXISTS TEMP_TABLE_PERMISSIONS;
create temporary table if not exists TEMP_TABLE_PERMISSIONS
select 
	PERMISSIONS_TEMP.ID as 'PERMISSION_ID'	
from PERMISSIONS as PERMISSIONS_TEMP WHERE PERMISSIONS_TEMP.permission IN( 'Model.Manage',
 'Model.Manage.ExportVersionAPI',
 'Model.Manage.View', 
 'Model.Manage.View.DownloadReleaseNotes',
 'Lookup.Manage', 
 'Dashboard.BatchBulk', 
 'Dashboard.BatchBulk.DownloadIO',
 'Dashboard.BatchBulk.TerminateBatch',
 'Dashboard.BatchBulk.Upload', 
 'Dashboard.Transaction', 
 'Dashboard.Transaction.AdvancedSearch', 
 'Dashboard.Transaction.DownloadExcelUsageReport', 
 'Dashboard.Transaction.DownloadIOExcel',
 'Dashboard.Transaction.DownloadIOJson',
 'Dashboard.Transaction.DownloadModelIO',
 'Dashboard.Transaction.DownloadReport', 
 'Dashboard.Transaction.DownloadTenantIO',
 'Dashboard.Transaction.PayloadField',
 'Dashboard.Transaction.Re-run');

SET x = 0;
SELECT COUNT(*) FROM TEMP_TABLE_PERMISSIONS INTO permissions_length;
SELECT COUNT(*) FROM TEMP_TABLE_TENANT_ROLES INTO role_length;
SELECT role_length;
WHILE x < permissions_length DO
WHILE y < role_length DO
    	INSERT INTO `PERMISSION_ROLES_MAPPING` (`id`, `tenant_roles_map_id`, `permission_id`) VALUES (uuid(), 
	(select TENANT_ROLE_ID from TEMP_TABLE_TENANT_ROLES LIMIT y,1),
	(select PERMISSION_ID from TEMP_TABLE_PERMISSIONS LIMIT x,1));
SET y=y+1;
END WHILE;
SET y = 0;
SET x = x+1;
END WHILE;

SELECT 'Permissions created successfully for the roles ROLE_TENANT_USER';

/*Dropping temp tables*/

DROP TABLE TEMP_TABLE_PERMISSIONS;
DROP TABLE TEMP_TABLE_TENANT_ROLES;
DROP TABLE TEMP_TABLE_TENANT;
DROP TABLE TEMP_TABLE_ROLES;
/*Successfully dropped temp tables*/

END $$

DELIMITER ;


ALTER TABLE ADDRESS MODIFY ADDRESS_1 VARCHAR(200) NOT NULL ; 
ALTER TABLE ADDRESS MODIFY ADDRESS_2 VARCHAR(200) ;
ALTER TABLE ADDRESS MODIFY ZIP VARCHAR(6) NOT NULL;

ALTER TABLE `SYSTEM_KEY` ADD UNIQUE INDEX `SYSTEM_KEY` (`SYSTEM_KEY`, `KEY_TYPE`);

DROP TABLE IF EXISTS SYSTEM_MODELETS;

CREATE TABLE `SYSTEM_MODELETS` (
                `HOST_NAME` CHAR(36) NOT NULL,
                `PORT` INT(10) NOT NULL,
                `ENVIRONMENT` VARCHAR(20) NOT NULL,
                `MEMBER_HOST` VARCHAR(36) NOT NULL,
                PRIMARY KEY (`HOST_NAME`, `PORT`)
)
COMMENT='Stores all modelet configurations'
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB;


DROP TABLE IF EXISTS AUTHTOKEN;

CREATE TABLE `AUTHTOKEN` (
	`ID` CHAR(36) NOT NULL COLLATE 'utf8_bin',
	`TENANT_ID` CHAR(36) NOT NULL COMMENT 'Tenant ID' COLLATE 'utf8_bin',
	`AUTH_CODE` VARCHAR(64) NOT NULL COLLATE 'utf8_bin',
	`ACTIVE_FROM` BIGINT(20),	
	`ACTIVE_UNTIL` BIGINT(20),
	`STATUS` VARCHAR(100) NOT NULL,
	`COMMENT` VARCHAR(100),
	`CREATED_ON` BIGINT(20) NOT NULL,
	`CREATED_BY` VARCHAR(100) NOT NULL COLLATE 'utf8_bin',
	`LAST_UPDATED_BY` VARCHAR(100) NULL DEFAULT NULL COLLATE 'utf8_bin',
	`LAST_UPDATED_ON` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`ID`),
	INDEX `FK_AUTHTOKEN_TENANT` (`TENANT_ID`),
	CONSTRAINT `FK_AUTHTOKEN_TENANT` FOREIGN KEY (`TENANT_ID`) REFERENCES `TENANT` (`ID`) ON UPDATE NO ACTION ON DELETE NO ACTION
)
COLLATE='utf8_bin'
ENGINE=InnoDB;


SET FOREIGN_KEY_CHECKS = 0;

ALTER TABLE TENANT_ROLES_MAPPING CHANGE COLUMN `tenant_code` `tenant_code` VARCHAR(10) NOT NULL COMMENT 'Tenant code used for unique identificaiton' COLLATE 'utf8_bin';

ALTER TABLE TENANT_USER_MAPPING CHANGE COLUMN `tenant_code` `tenant_code` VARCHAR(10) NOT NULL COMMENT 'Tenant code used for unique identificaiton' COLLATE 'utf8_bin';

ALTER TABLE USERS_LOGIN_AUDIT CHANGE COLUMN `tenant_code` `tenant_code` VARCHAR(10) NOT NULL COMMENT 'Tenant code used for unique identificaiton' COLLATE 'utf8_bin';

ALTER TABLE TENANT CHANGE COLUMN `CODE` `CODE` VARCHAR(10) NOT NULL COMMENT 'Tenant code used for unique identificaiton' COLLATE 'utf8_bin';

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `DESCRIPTION`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('E9F564AC-7FE3-48F1-8188-BFA5034C1AA6', 'MODELET_EXEC_TIME_LIMIT', 'LIMIT FOR MODEL EXECUTION TIME', 300, 'Y', 'SYSTEM', 1465809887, 'SYSTEM', 1465809901);

INSERT INTO `SYSTEM_PARAMETER_AUDIT` (`ID`, `SYS_KEY`, `DESCRIPTION`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('E9F564AC-7FE3-48F1-8188-BFA5034C1AA6', 'MODELET_EXEC_TIME_LIMIT', 'LIMIT FOR MODEL EXECUTION TIME', 300, 'Y', 'SYSTEM', 1465809887, 'SYSTEM', 1465809901);

COMMIT;