use umg_admin;

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

commit;