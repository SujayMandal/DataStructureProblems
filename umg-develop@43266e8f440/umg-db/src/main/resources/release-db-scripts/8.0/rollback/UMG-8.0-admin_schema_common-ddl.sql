use `umg_admin`;

ALTER TABLE `POOL`
CHANGE COLUMN `EXECUTION_LANGUAGE` `ENVIRONMENT` VARCHAR(32) NOT NULL COMMENT 'Execution language of modelet' COLLATE 'utf8_bin' AFTER `IS_DEFAULT_POOL`;

ALTER TABLE `POOL` DROP COLUMN `EXECUTION_ENVIRONMENT`;

ALTER TABLE `MODEL_EXEC_PACKAGES` DROP COLUMN `EXECUTION_ENVIRONMENT`;

ALTER TABLE `MODEL_EXEC_PACKAGES_AUDIT`  DROP COLUMN `EXECUTION_ENVIRONMENT`;

ALTER TABLE MODEL_EXEC_PACKAGES DROP INDEX  `PACKAGE_FOLDER_VERSION_ENV_CONSTRAINT`;

ALTER TABLE MODEL_EXEC_PACKAGES ADD UNIQUE INDEX  PACKAGE_FOLDER_VERSION_CONSTRAINT(`PACKAGE_FOLDER`,`PACKAGE_VERSION`);

ALTER TABLE `SYSTEM_MODELETS` DROP COLUMN `EXECUTION_ENVIRONMENT`;

ALTER TABLE `POOL` DROP INDEX  `UNIQUE_POOL_PRIORITY`;
ALTER TABLE `POOL` ADD UNIQUE INDEX  UNIQUE_POOL_PRIORITY(`PRIORITY`,`EXECUTION_LANGUAGE`);

ALTER TABLE `SYSTEM_MODELETS` ADD PRIMARY KEY (`HOST_NAME`);
	
ALTER TABLE `SYSTEM_MODELETS` ALTER `EXEC_LANGUAGE` DROP DEFAULT;

ALTER TABLE `SYSTEM_MODELETS`CHANGE COLUMN `EXEC_LANGUAGE` `ENVIRONMENT` VARCHAR(20) NOT NULL AFTER `PORT`,	
DROP COLUMN `POOL_NAME`;

commit;