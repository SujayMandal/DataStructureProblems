use `umg_admin`;

ALTER TABLE `SYSTEM_MODELETS` ALTER `ENVIRONMENT` DROP DEFAULT;

ALTER TABLE `SYSTEM_MODELETS` CHANGE COLUMN `ENVIRONMENT` `EXEC_LANGUAGE` VARCHAR(20) NOT NULL AFTER `PORT`,
ADD COLUMN `POOL_NAME` VARCHAR(100) NOT NULL AFTER `EXECUTION_ENVIRONMENT`;

DELETE FROM `SYSTEM_MODELETS`;