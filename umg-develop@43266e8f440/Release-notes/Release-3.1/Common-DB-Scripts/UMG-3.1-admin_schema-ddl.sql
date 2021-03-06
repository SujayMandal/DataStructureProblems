use umg_admin;

SET SQL_SAFE_UPDATES = 0;


DROP TABLE IF EXISTS `POOL_CRITERIA`;
CREATE TABLE IF NOT EXISTS `POOL_CRITERIA` (
  `ID` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'pool criteria id',
  `CRITERIA_NAME` varchar(100) COLLATE utf8_bin NOT NULL COMMENT 'pool criteria name',
  `CRITERIA_PRIORITY` int(11) NOT NULL COMMENT 'citeria priority used in sorting the pool definition for selection',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UNIQUE_POOL_CRITERIA` (`CRITERIA_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Priority pool criterias';

ALTER TABLE MODEL_EXECUTION_ENVIRONMENTS ADD PRIMARY KEY(id);

ALTER TABLE MODEL_EXEC_PACKAGES ADD PRIMARY KEY(id);

ALTER TABLE `SYSTEM_PARAMETER` CHANGE COLUMN `SYS_VALUE` `SYS_VALUE` VARCHAR(1500) NULL DEFAULT NULL COLLATE 'utf8_bin' AFTER `SYS_KEY`;

ALTER TABLE `SYSTEM_PARAMETER_AUDIT` CHANGE COLUMN `SYS_VALUE` `SYS_VALUE` VARCHAR(1500) NULL DEFAULT NULL COLLATE 'utf8_bin' AFTER `SYS_KEY`;

ALTER TABLE `MODEL_EXECUTION_ENVIRONMENTS` ADD UNIQUE INDEX `NAME` (`NAME`);

ALTER TABLE `MODEL_EXEC_PACKAGES` ADD CONSTRAINT PACKAGE_FOLDER_VERSION_CONSTRAINT UNIQUE(`PACKAGE_FOLDER`, `PACKAGE_VERSION`);

ALTER TABLE `MODEL_EXEC_PACKAGES` ADD UNIQUE INDEX `UNIQUE_PACKAGE_NAME` (`PACKAGE_NAME`);

ALTER TABLE `SYSTEM_PARAMETER` ADD COLUMN `DESCRIPTION` VARCHAR(500) NULL DEFAULT NULL AFTER `SYS_KEY`;

ALTER TABLE `SYSTEM_PARAMETER_AUDIT` ADD COLUMN `DESCRIPTION` VARCHAR(500) NULL DEFAULT NULL AFTER `SYS_KEY`;

COMMIT;