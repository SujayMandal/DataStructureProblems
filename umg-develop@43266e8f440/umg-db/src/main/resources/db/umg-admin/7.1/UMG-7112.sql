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

UPDATE NOTIFICATION_EMAIL_TEMPLATE SET BODY_DEFINITION='<html><body style="font-family: Calibri;">Hi,<br><br>Modelet $modeletHost:$port has been restarted in $environment environment. Please find further details below:<ul><li>Current Pool Name: $poolName</li><li>New Pool Name: $newPoolName</li><li>Restart Time: $modeletRestartTime</li><li>Model Name: $loadedModel & Model Version: $loadedModelVersion prior to restart</li><li>Model Name: $modelToLoad & Model Version: $modelVersionToLoad requested model</li><li>Transactio ID: $transactionId</li><li>Transaction Run Date: $transactionRunDate</li><li>Restart Reason: $reason</li><li># Transaction Processed since last Restart: $execLimit</li><li>Transaction Restart Set-up Count: $restartCount</li></ul><b>Regards,</b><br><b>REALAnalytics Team</b></body></html>' WHERE  NAME='MODELET RESTART TEMPLATE';

commit;
