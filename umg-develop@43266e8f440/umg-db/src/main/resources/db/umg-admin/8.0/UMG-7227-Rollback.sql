use `umg_admin`;

ALTER TABLE `MODEL_EXEC_PACKAGES` DROP COLUMN `EXECUTION_ENVIRONMENT`;

ALTER TABLE `MODEL_EXEC_PACKAGES_AUDIT`  DROP COLUMN `EXECUTION_ENVIRONMENT`;


ALTER TABLE MODEL_EXEC_PACKAGES DROP INDEX  `PACKAGE_FOLDER_VERSION_ENV_CONSTRAINT`;

ALTER TABLE MODEL_EXEC_PACKAGES ADD UNIQUE INDEX  PACKAGE_FOLDER_VERSION_CONSTRAINT(`PACKAGE_FOLDER`,`PACKAGE_VERSION`);