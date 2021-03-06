use umg_admin;

UPDATE `MODEL_EXECUTION_ENVIRONMENTS` SET `ENVIRONMENT_VERSION`='3.2.1' WHERE  `NAME`='R-3.1.2';
UPDATE `MODEL_EXECUTION_ENVIRONMENTS` SET `NAME`='R-3.2.1' WHERE  `NAME`='R-3.1.2';
UPDATE `MODEL_EXEC_PACKAGES` SET `MODEL_EXEC_ENV_NAME`='R-3.2.1' WHERE `MODEL_EXEC_ENV_NAME`='R-3.1.2';
UPDATE `MODEL_EXEC_PACKAGES_AUDIT`  SET `MODEL_EXEC_ENV_NAME`='R-3.2.1' WHERE `MODEL_EXEC_ENV_NAME`='R-3.1.2';
UPDATE `POOL_CRITERIA_DEF_MAPPING` p SET p.POOL_CRITERIA_VALUE = REPLACE (POOL_CRITERIA_VALUE, '3.1.2', '3.2.1' );
ALTER TABLE `MODEL_EXECUTION_ENVIRONMENTS` ADD UNIQUE INDEX `NAME` (`NAME`);

ALTER TABLE `MODEL_EXEC_PACKAGES` ADD CONSTRAINT PACKAGE_FOLDER_VERSION_CONSTRAINT UNIQUE(`PACKAGE_FOLDER`, `PACKAGE_VERSION`);

commit;