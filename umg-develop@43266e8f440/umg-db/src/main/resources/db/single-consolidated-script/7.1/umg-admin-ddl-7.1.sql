-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.6.13 - MySQL Community Server (GPL)
-- Server OS:                    Win32
-- HeidiSQL Version:             9.3.0.4984
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Dumping database structure for umg_admin
CREATE DATABASE IF NOT EXISTS `umg_admin` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `umg_admin`;


-- Dumping structure for table umg_admin.address
CREATE TABLE IF NOT EXISTS `ADDRESS` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Tenant ID for the address',
  `ADDRESS_1` varchar(200) COLLATE utf8_bin NOT NULL,
  `ADDRESS_2` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `CITY` varchar(45) COLLATE utf8_bin NOT NULL,
  `STATE` varchar(45) COLLATE utf8_bin NOT NULL,
  `ZIP` varchar(6) COLLATE utf8_bin NOT NULL,
  `COUNTRY` varchar(45) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ADDRESS_TENANT` (`TENANT_ID`),
  CONSTRAINT `FK_ADDRESS_TENANT` FOREIGN KEY (`TENANT_ID`) REFERENCES `TENANT` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.authtoken
CREATE TABLE IF NOT EXISTS `AUTHTOKEN` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Tenant ID',
  `AUTH_CODE` varchar(64) COLLATE utf8_bin NOT NULL,
  `ACTIVE_FROM` bigint(20) DEFAULT NULL,
  `ACTIVE_UNTIL` bigint(20) DEFAULT NULL,
  `STATUS` varchar(100) COLLATE utf8_bin NOT NULL,
  `COMMENT` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `CREATED_BY` varchar(100) COLLATE utf8_bin NOT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_AUTHTOKEN_TENANT` (`TENANT_ID`),
  CONSTRAINT `FK_AUTHTOKEN_TENANT` FOREIGN KEY (`TENANT_ID`) REFERENCES `TENANT` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.command
CREATE TABLE IF NOT EXISTS `COMMAND` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(100) COLLATE utf8_bin NOT NULL COMMENT 'Name of the command',
  `DESCRIPTION` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT 'Description of the command',
  `EXECUTION_SEQUENCE` int(36) NOT NULL COMMENT 'Command sequence id',
  `PROCESS` varchar(100) COLLATE utf8_bin NOT NULL COMMENT 'Name of the command',
  `CREATED_BY` char(36) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(36) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UN_COMMAND_SEQUENCE` (`NAME`,`PROCESS`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.model_execution_environments
CREATE TABLE IF NOT EXISTS `MODEL_EXECUTION_ENVIRONMENTS` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `EXECUTION_ENVIRONMENT` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'Model execution environment.',
  `ENVIRONMENT_VERSION` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'Version of the execution environment.',
  `CREATED_BY` char(50) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(50) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `NAME` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'Language name and version',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NAME` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT COMMENT='Table to store Model execution environment and version';

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.model_exec_packages
CREATE TABLE IF NOT EXISTS `MODEL_EXEC_PACKAGES` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `PACKAGE_NAME` varchar(200) COLLATE utf8_bin NOT NULL,
  `PACKAGE_FOLDER` varchar(200) COLLATE utf8_bin NOT NULL,
  `PACKAGE_VERSION` varchar(50) COLLATE utf8_bin NOT NULL,
  `PACKAGE_TYPE` varchar(50) COLLATE utf8_bin NOT NULL,
  `COMPILED_OS` varchar(50) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` char(50) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(50) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `MODEL_EXEC_ENV_NAME` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'Language name and version',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PACKAGE_FOLDER_VERSION_CONSTRAINT` (`PACKAGE_FOLDER`,`PACKAGE_VERSION`),
  UNIQUE KEY `UNIQUE_PACKAGE_NAME` (`PACKAGE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT COMMENT='Table to store model execution environment base packages';

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.model_exec_packages_audit
CREATE TABLE IF NOT EXISTS `MODEL_EXEC_PACKAGES_AUDIT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `PACKAGE_NAME` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `PACKAGE_FOLDER` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `PACKAGE_VERSION` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `PACKAGE_TYPE` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `COMPILED_OS` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `CREATED_BY` char(50) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_ON` bigint(20) DEFAULT NULL,
  `LAST_UPDATED_BY` char(50) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `MODEL_EXEC_ENV_NAME` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'Language name and version'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT COMMENT='Table to store model execution environment base packages';

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.model_implementation_type
CREATE TABLE IF NOT EXISTS `MODEL_IMPLEMENTATION_TYPE` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `IMPLEMENTATION` varchar(45) COLLATE utf8_bin NOT NULL,
  `TYPE_XSD` blob,
  `CREATED_BY` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.notification_email_template
CREATE TABLE IF NOT EXISTS `NOTIFICATION_EMAIL_TEMPLATE` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `NOTIFICATION_EVENT_ID` char(36) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(64) COLLATE utf8_bin NOT NULL,
  `DESCRIPTION` varchar(256) COLLATE utf8_bin NOT NULL DEFAULT '',
  `BODY_DEFINITION` mediumblob NOT NULL COMMENT 'Mail Template Definition',
  `SUBJECT_DEFINITION` varchar(256) COLLATE utf8_bin NOT NULL,
  `IS_ACTIVE` int(10) NOT NULL DEFAULT '1',
  `MAJOR_VERSION` int(11) NOT NULL DEFAULT '1',
  `MAIL_CONTENT_TYPE` varchar(64) COLLATE utf8_bin NOT NULL COMMENT 'MIME Message Types',
  `CREATED_BY` varchar(32) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.notification_event
CREATE TABLE IF NOT EXISTS `NOTIFICATION_EVENT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(45) COLLATE utf8_bin NOT NULL COMMENT 'Model Publishing Success event',
  `DESCRIPTION` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `CLASSIFICATION` varchar(64) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` varchar(32) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NAME_UNIQUE` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.notification_event_template_mapping
CREATE TABLE IF NOT EXISTS `NOTIFICATION_EVENT_TEMPLATE_MAPPING` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(45) COLLATE utf8_bin NOT NULL,
  `NOTIFICATION_EVENT_ID` char(36) COLLATE utf8_bin NOT NULL,
  `NOTIFICATION_TEMPLATE_ID` char(36) COLLATE utf8_bin NOT NULL,
  `NOTIFICATION_TYPE_ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` varchar(45) COLLATE utf8_bin NOT NULL,
  `TO_ADDRESS` varchar(1500) COLLATE utf8_bin DEFAULT NULL,
  `FROM_ADDRESS` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `CC_ADDRESS` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `BCC_ADDRESS` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `MOBILE` bigint(20) DEFAULT NULL,
  `CREATED_BY` varchar(32) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.notification_sms_template
CREATE TABLE IF NOT EXISTS `NOTIFICATION_SMS_TEMPLATE` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(64) COLLATE utf8_bin NOT NULL,
  `DESCRIPTION` varchar(256) COLLATE utf8_bin NOT NULL DEFAULT '',
  `SMS_DEFINITION` blob NOT NULL COMMENT 'Model Publishing Success Template',
  `IS_ACTIVE` int(10) NOT NULL DEFAULT '1',
  `MAJOR_VERSION` int(11) NOT NULL DEFAULT '1',
  `CREATED_BY` varchar(32) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.notification_type
CREATE TABLE IF NOT EXISTS `NOTIFICATION_TYPE` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TYPE` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'Mail or SMS',
  `DESCRIPTION` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_BY` varchar(32) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `TYPE_UNIQUE` (`TYPE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.permissions
CREATE TABLE IF NOT EXISTS `PERMISSIONS` (
  `Id` varchar(50) COLLATE utf8_bin NOT NULL,
  `permission` varchar(100) COLLATE utf8_bin NOT NULL COMMENT 'permission name',
  `permission_type` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'permission type can be any of two values page/action',
  `ui_element_id` varchar(500) COLLATE utf8_bin DEFAULT NULL COMMENT 'unique ID of UI element from html page',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.permission_roles_mapping
CREATE TABLE IF NOT EXISTS `PERMISSION_ROLES_MAPPING` (
  `id` varchar(50) COLLATE utf8_bin NOT NULL,
  `tenant_roles_map_id` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'id from the TENANT_ROLES_MAPPING table for a role mapped to tenant',
  `permission_id` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'id from permission table for a permission',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UN_perm_id_tnt_rol_map_uniq` (`tenant_roles_map_id`,`permission_id`),
  KEY `FK_permission_roles_mapping_user_permissions` (`permission_id`),
  KEY `FK_permission_roles_mapping_tenant_roles_mapping` (`tenant_roles_map_id`),
  CONSTRAINT `FK_permission_roles_mapping_tenant_roles_mapping` FOREIGN KEY (`tenant_roles_map_id`) REFERENCES `TENANT_ROLES_MAPPING` (`Id`),
  CONSTRAINT `FK_permission_roles_mapping_user_permissions` FOREIGN KEY (`permission_id`) REFERENCES `PERMISSIONS` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.pool
CREATE TABLE IF NOT EXISTS `POOL` (
  `ID` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'Pool id',
  `POOL_NAME` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'Priority Pool Name',
  `POOL_DESCRIPTION` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT 'Priority Pool Description',
  `IS_DEFAULT_POOL` tinyint(1) NOT NULL COMMENT 'Flag fog default pool',
  `ENVIRONMENT` varchar(32) COLLATE utf8_bin NOT NULL COMMENT 'No of Modelelts allocated to this pool',
  `POOL_STATUS` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT 'Status of pool (IN_PROGRESS, etc, will be used for batch)',
  `MODELET_COUNT` int(11) NOT NULL COMMENT 'No of Modelelts allocated to this pool',
  `MODELET_CAPACITY` varchar(32) COLLATE utf8_bin NOT NULL COMMENT 'Max Heap size of Modelet',
  `PRIORITY` int(11) NOT NULL COMMENT 'Priority of pool',
  `WAIT_TIMEOUT` int(50) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UNIQUE_POOL_NAME` (`POOL_NAME`),
  UNIQUE KEY `UNIQUE_POOL_PRIORITY` (`ENVIRONMENT`,`PRIORITY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.pool_criteria
CREATE TABLE IF NOT EXISTS `POOL_CRITERIA` (
  `ID` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'pool criteria id',
  `CRITERIA_NAME` varchar(100) COLLATE utf8_bin NOT NULL COMMENT 'pool criteria name',
  `CRITERIA_PRIORITY` int(11) NOT NULL COMMENT 'citeria priority used in sorting the pool definition for selection',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UNIQUE_POOL_CRITERIA` (`CRITERIA_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Priority pool criterias';

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.pool_criteria_def_mapping
CREATE TABLE IF NOT EXISTS `POOL_CRITERIA_DEF_MAPPING` (
  `ID` varchar(50) COLLATE utf8_bin NOT NULL,
  `POOL_ID` varchar(50) COLLATE utf8_bin NOT NULL,
  `POOL_CRITERIA_VALUE` varchar(512) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `FK_UNIQUE_CRITERIA` (`POOL_ID`),
  UNIQUE KEY `UNIQUE_POOL_CRITERIA_DEF_MAPPING` (`POOL_ID`),
  CONSTRAINT `FK_POOL_ID` FOREIGN KEY (`POOL_ID`) REFERENCES `POOL` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.pool_usage_order
CREATE TABLE IF NOT EXISTS `POOL_USAGE_ORDER` (
  `ID` varchar(50) COLLATE utf8_bin NOT NULL,
  `POOL_ID` varchar(50) COLLATE utf8_bin NOT NULL,
  `POOL_USAGE_ID` varchar(50) COLLATE utf8_bin NOT NULL,
  `POOL_TRY_ORDER` int(10) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UNIQUE_POOL_USAGE_ORDER` (`POOL_ID`,`POOL_USAGE_ID`),
  KEY `FKPOOL_ID` (`POOL_ID`),
  CONSTRAINT `FKPOOL_ID` FOREIGN KEY (`POOL_ID`) REFERENCES `POOL` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.revinfo
CREATE TABLE IF NOT EXISTS `REVINFO` (
  `REV` int(11) NOT NULL AUTO_INCREMENT,
  `REVTSTMP` bigint(20) DEFAULT NULL,
  `REVBY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.roles
CREATE TABLE IF NOT EXISTS `ROLES` (
  `ROLE` varchar(100) COLLATE utf8_bin NOT NULL COMMENT 'role name',
  `Id` char(36) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `Id` (`Id`),
  UNIQUE KEY `ROLE` (`ROLE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.syndicated_data
CREATE TABLE IF NOT EXISTS `SYNDICATED_DATA` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `CONTAINER_NAME` varchar(50) COLLATE utf8_bin NOT NULL,
  `DESCRIPTION` varchar(200) COLLATE utf8_bin NOT NULL,
  `VERSION_ID` bigint(4) DEFAULT NULL,
  `VERSION_NAME` varchar(50) COLLATE utf8_bin NOT NULL,
  `VERSION_DESCRIPTION` varchar(200) COLLATE utf8_bin NOT NULL,
  `TABLE_NAME` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `VALID_FROM` bigint(20) DEFAULT NULL,
  `VALID_TO` bigint(20) DEFAULT NULL,
  `CREATED_BY` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `uniqueVersion` (`CONTAINER_NAME`,`VERSION_ID`),
  KEY `INDEX_CINTAINER_NAME` (`CONTAINER_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.syndicated_data_audit
CREATE TABLE IF NOT EXISTS `SYNDICATED_DATA_AUDIT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL DEFAULT '',
  `CONTAINER_NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `DESCRIPTION` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `VERSION_NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `VERSION_DESCRIPTION` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `VERSION_ID` bigint(20) DEFAULT NULL,
  `TABLE_NAME` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `VALID_FROM` bigint(20) DEFAULT NULL,
  `VALID_TO` bigint(20) DEFAULT NULL,
  `CREATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_ON` bigint(20) DEFAULT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`,`REV`),
  KEY `FK_SYNDICATED_DATA_AUDIT_REVINFO` (`REV`),
  CONSTRAINT `FK_SYNDICATED_DATA_AUDIT_REVINFO` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.system_key
CREATE TABLE IF NOT EXISTS `SYSTEM_KEY` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `SYSTEM_KEY` varchar(45) COLLATE utf8_bin NOT NULL,
  `KEY_TYPE` varchar(200) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `SYSTEM_KEY` (`SYSTEM_KEY`,`KEY_TYPE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.system_modelets
CREATE TABLE IF NOT EXISTS `SYSTEM_MODELETS` (
  `HOST_NAME` char(36) NOT NULL,
  `PORT` int(10) NOT NULL,
  `ENVIRONMENT` varchar(20) NOT NULL,
  `MEMBER_HOST` varchar(36) NOT NULL,
  PRIMARY KEY (`HOST_NAME`,`PORT`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Stores all modelet configurations';

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.system_parameter
CREATE TABLE IF NOT EXISTS `SYSTEM_PARAMETER` (
  `ID` char(36) COLLATE utf8_bin NOT NULL DEFAULT '',
  `SYS_KEY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `DESCRIPTION` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `SYS_VALUE` varchar(1500) COLLATE utf8_bin DEFAULT NULL,
  `IS_ACTIVE` char(1) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_ON` bigint(20) DEFAULT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.system_parameter_audit
CREATE TABLE IF NOT EXISTS `SYSTEM_PARAMETER_AUDIT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL DEFAULT '',
  `SYS_KEY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `DESCRIPTION` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `SYS_VALUE` varchar(1500) COLLATE utf8_bin DEFAULT NULL,
  `IS_ACTIVE` char(1) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_ON` bigint(20) DEFAULT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`,`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.tenant
CREATE TABLE IF NOT EXISTS `TENANT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(45) COLLATE utf8_bin NOT NULL COMMENT 'Tenant name',
  `DESCRIPTION` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CODE` varchar(10) COLLATE utf8_bin NOT NULL COMMENT 'Tenant code used for unique identificaiton',
  `TENANT_TYPE` varchar(45) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UN_TENANT_NAME` (`NAME`),
  UNIQUE KEY `UN_TENANT_CODE` (`CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.tenant_config
CREATE TABLE IF NOT EXISTS `TENANT_CONFIG` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Tenant where this config parameter belongs to.',
  `SYSTEM_KEY_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Config parameter key.',
  `CONFIG_VALUE` varchar(500) COLLATE utf8_bin DEFAULT NULL COMMENT 'Config parameter value.',
  `CREATED_BY` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `ROLE` char(20) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UN_TENANT_CONFIG_KEY` (`TENANT_ID`,`SYSTEM_KEY_ID`) COMMENT 'Key for a tenant is unique',
  KEY `FK_TENANT_CONFIG_SYSTEM_KEY_idx` (`SYSTEM_KEY_ID`),
  CONSTRAINT `FK_TENANT_CONFIG_SYSTEM_KEY` FOREIGN KEY (`SYSTEM_KEY_ID`) REFERENCES `SYSTEM_KEY` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_TENANT_CONFIG_TENANT` FOREIGN KEY (`TENANT_ID`) REFERENCES `TENANT` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.tenant_roles_mapping
CREATE TABLE IF NOT EXISTS `TENANT_ROLES_MAPPING` (
  `Id` varchar(50) COLLATE utf8_bin NOT NULL,
  `roles_id` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'id from the roles table for a role',
  `tenant_code` varchar(10) COLLATE utf8_bin NOT NULL COMMENT 'Tenant code used for unique identificaiton',
  PRIMARY KEY (`Id`),
  UNIQUE KEY `UN_role_id_tnt_code_unq` (`roles_id`,`tenant_code`),
  KEY `FK_user_roles_mapping_user_roles` (`roles_id`),
  KEY `FK_tenant_roles_mapping_tenant` (`tenant_code`),
  CONSTRAINT `FK_tenant_roles_mapping_tenant` FOREIGN KEY (`tenant_code`) REFERENCES `TENANT` (`CODE`),
  CONSTRAINT `FK_user_roles_mapping_user_roles` FOREIGN KEY (`roles_id`) REFERENCES `ROLES` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.tenant_user_mapping
CREATE TABLE IF NOT EXISTS `TENANT_USER_MAPPING` (
  `Id` varchar(50) COLLATE utf8_bin NOT NULL,
  `user_id` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'id from the users table for a user',
  `tenant_code` varchar(10) COLLATE utf8_bin NOT NULL COMMENT 'Tenant code used for unique identificaiton',
  PRIMARY KEY (`Id`),
  UNIQUE KEY `UN_user_id_tenant_id` (`user_id`,`tenant_code`),
  KEY `FK_tenant_user_mapping_tenant_code` (`tenant_code`),
  CONSTRAINT `FK_tenant_user_mapping_tenant_code` FOREIGN KEY (`tenant_code`) REFERENCES `TENANT` (`CODE`),
  CONSTRAINT `FK_tenant_user_mapping_users_id` FOREIGN KEY (`user_id`) REFERENCES `USERS` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.tenant_user_tenant_role_mapping
CREATE TABLE IF NOT EXISTS `TENANT_USER_TENANT_ROLE_MAPPING` (
  `Id` varchar(50) COLLATE utf8_bin NOT NULL,
  `tenant_user_map_id` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'id from the TENANT_USER_MAPPING table for a user mapped to tenant',
  `tenant_role_map_id` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'id from the TENANT_ROLES_MAPPING table for a role mapped to tenant',
  PRIMARY KEY (`Id`),
  UNIQUE KEY `UN_tnt_usr_tnt_role_unq` (`tenant_user_map_id`,`tenant_role_map_id`),
  KEY `FK_tenant_user_role_mapping_tenant_user_mapping` (`tenant_user_map_id`),
  KEY `FK_tenant_user_role_mapping_tenant_roles_mapping` (`tenant_role_map_id`),
  CONSTRAINT `FK_tenant_user_role_mapping_tenant_roles_mapping` FOREIGN KEY (`tenant_role_map_id`) REFERENCES `TENANT_ROLES_MAPPING` (`Id`),
  CONSTRAINT `FK_tenant_user_role_mapping_tenant_user_mapping` FOREIGN KEY (`tenant_user_map_id`) REFERENCES `TENANT_USER_MAPPING` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.transport_parameters
CREATE TABLE IF NOT EXISTS `TRANSPORT_PARAMETERS` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TRANSPORT_TYPE_ID` varchar(45) COLLATE utf8_bin NOT NULL,
  `PARAMETER_NAME` varchar(45) COLLATE utf8_bin NOT NULL,
  `DEFAULT_VALUE` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_BY` char(36) COLLATE utf8_bin NOT NULL COMMENT 'User created the record.',
  `CREATED_ON` bigint(20) NOT NULL COMMENT 'Record created time.',
  `LAST_UPDATED_BY` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'User last updated the record.',
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL COMMENT 'Record last updated time.',
  PRIMARY KEY (`ID`),
  KEY `FK_TRANSPORT_PARAMETERS_TRANSPORT_TYPE` (`TRANSPORT_TYPE_ID`),
  CONSTRAINT `FK_TRANSPORT_PARAMETERS_TRANSPORT_TYPE` FOREIGN KEY (`TRANSPORT_TYPE_ID`) REFERENCES `TRANSPORT_TYPES` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.transport_types
CREATE TABLE IF NOT EXISTS `TRANSPORT_TYPES` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(45) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` char(36) COLLATE utf8_bin NOT NULL COMMENT 'User created the record.',
  `CREATED_ON` bigint(20) NOT NULL COMMENT 'Record created time.',
  `LAST_UPDATED_BY` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'User last updated the record.',
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL COMMENT 'Record last updated time.',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.users
CREATE TABLE IF NOT EXISTS `USERS` (
  `Id` char(36) COLLATE utf8_bin NOT NULL,
  `USERNAME` varchar(50) COLLATE utf8_bin NOT NULL,
  `PASSWORD` varchar(100) COLLATE utf8_bin NOT NULL,
  `ENABLED` tinyint(4) NOT NULL DEFAULT '1',
  `NAME` varchar(126) COLLATE utf8_bin DEFAULT NULL COMMENT 'Name of User',
  `sys_admin` enum('true','false') COLLATE utf8_bin NOT NULL DEFAULT 'false' COMMENT 'to set user as sys-admin',
  `OFFICIAL_EMAIL` varchar(252) COLLATE utf8_bin DEFAULT NULL COMMENT 'Official E-mail ID of User',
  `ORGANIZATION` varchar(126) COLLATE utf8_bin DEFAULT NULL COMMENT 'Organization of User',
  `COMMENTS` varchar(252) COLLATE utf8_bin DEFAULT NULL COMMENT 'Comment of User',
  `CREATED_ON` bigint(20) DEFAULT NULL COMMENT 'Created Date in milliseconds in GMT',
  `LAST_ACTIVATED_ON` bigint(20) DEFAULT NULL COMMENT 'Last Activated Date of this User in milliseconds in GMT',
  `LAST_DEACTIVATED_ON` bigint(20) DEFAULT NULL COMMENT 'Last Deactivated Date of this User in milliseconds in GMT',
  PRIMARY KEY (`Id`),
  UNIQUE KEY `username` (`USERNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.


-- Dumping structure for table umg_admin.users_login_audit
CREATE TABLE IF NOT EXISTS `USERS_LOGIN_AUDIT` (
  `USERNAME` varchar(50) COLLATE utf8_bin NOT NULL,
  `tenant_code` varchar(10) COLLATE utf8_bin NOT NULL COMMENT 'Tenant code used for unique identificaiton',
  `SYS_IP_ADDRESS` varchar(15) COLLATE utf8_bin NOT NULL,
  `ACCESS_ON` bigint(20) NOT NULL,
  `ACTIVITY` varchar(126) COLLATE utf8_bin NOT NULL,
  `REASON_CODE` varchar(32) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ACCESS_ON`),
  KEY `FK_users_login_audit_users` (`USERNAME`),
  CONSTRAINT `FK_users_login_audit_users` FOREIGN KEY (`USERNAME`) REFERENCES `USERS` (`USERNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Data exporting was unselected.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;



CREATE TABLE `MODELET_RESTART_CONFIG` (
	`ID` VARCHAR(36) NOT NULL COLLATE 'utf8_bin',
	`TENANT_ID` VARCHAR(10) NULL DEFAULT NULL COLLATE 'utf8_bin',
	`MODELNAME_VERSION` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8_bin',
	`RESTART_COUNT` INT(5) NULL DEFAULT NULL,
	`CREATED_BY` VARCHAR(100) NULL DEFAULT NULL COLLATE 'utf8_bin',
	`CREATED_ON` BIGINT(20) NULL DEFAULT NULL,
	`LAST_UPDATED_BY` VARCHAR(100) NULL DEFAULT NULL COLLATE 'utf8_bin',
	`LAST_UPDATED_ON` BIGINT(20) NULL DEFAULT NULL,
	INDEX `FK_modelet_restart_tenant` (`TENANT_ID`),
	PRIMARY KEY (`ID`)
)
COLLATE='utf8_bin'
ENGINE=InnoDB;



CREATE TABLE `MODELET_RESTART_CONFIG_AUDIT` (
	`ID` VARCHAR(36) NULL DEFAULT NULL COLLATE 'utf8_bin',
	`TENANT_ID` VARCHAR(10) NULL DEFAULT NULL COLLATE 'utf8_bin',
	`MODELNAME_VERSION` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8_bin',
	`RESTART_COUNT` INT(5) NULL DEFAULT NULL,
	`CREATED_BY` VARCHAR(100) NULL DEFAULT NULL COLLATE 'utf8_bin',
	`CREATED_ON` BIGINT(20) NULL DEFAULT NULL,
	`LAST_UPDATED_BY` VARCHAR(100) NULL DEFAULT NULL COLLATE 'utf8_bin',
	`LAST_UPDATED_ON` BIGINT(20) NULL DEFAULT NULL,
	`REV` INT(11) NOT NULL,
	`REVTYPE` TINYINT(4) NULL DEFAULT NULL
)
COLLATE='utf8_bin'
ENGINE=InnoDB;