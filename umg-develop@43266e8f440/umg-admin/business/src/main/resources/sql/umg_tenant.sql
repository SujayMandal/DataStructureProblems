CREATE TABLE IF NOT EXISTS `USERS_TAB` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Tenant who owns this row',
  `USER_NAME` varchar(45) NOT NULL,
  `PASSWORD` varchar(45) NOT NULL,
  `ENABLED` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Is this user is active',
  `CREATED_ON` bigint(20) NOT NULL,
  `CREATED_BY` char(36) COLLATE utf8_bin NOT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `LAST_UPDATED_BY` char(36) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`),
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT 'Stores users for each tenant.';
