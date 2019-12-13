use umg_admin;

ALTER TABLE `MODEL_EXECUTION_ENVIRONMENTS`	ADD COLUMN `IS_ACTIVE` CHAR(1) NOT NULL DEFAULT 'F' AFTER `NAME`;

ALTER TABLE `MODEL_EXEC_PACKAGES` 
DROP INDEX  `UNIQUE_PACKAGE_NAME`,	
ADD UNIQUE INDEX `UNIQUE_PACKAGE_NAME` (`PACKAGE_NAME`, `MODEL_EXEC_ENV_NAME`);

ALTER TABLE `MODEL_EXEC_PACKAGES`	
DROP INDEX `PACKAGE_FOLDER_VERSION_ENV_CONSTRAINT`,
ADD UNIQUE INDEX `PACKAGE_FOLDER_VERSION_ENV_CONSTRAINT` (`PACKAGE_FOLDER`, `PACKAGE_VERSION`, `EXECUTION_ENVIRONMENT`, `MODEL_EXEC_ENV_NAME`,`PACKAGE_TYPE`);

ALTER TABLE `SYSTEM_MODELETS` ADD COLUMN `R_SERVE_PORT` INT(10) NULL DEFAULT '0' AFTER `POOL_NAME` ;

ALTER TABLE `SYSTEM_MODELETS` ADD COLUMN `R_MODE` VARCHAR(10) NULL DEFAULT 'rJava' AFTER `R_SERVE_PORT`;

commit;

