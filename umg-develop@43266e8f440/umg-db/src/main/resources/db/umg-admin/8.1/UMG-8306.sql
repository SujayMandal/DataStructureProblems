USE umg_admin;

UPDATE POOL_CRITERIA_DEF_MAPPING SET POOL_CRITERIA_VALUE = replace(POOL_CRITERIA_VALUE,'& #EXECUTION_LANGUAGE_VERSION# = 3.2.1 ','');

UPDATE POOL_CRITERIA_DEF_MAPPING SET POOL_CRITERIA_VALUE = replace(POOL_CRITERIA_VALUE,'& #EXECUTION_LANGUAGE_VERSION# = 2013 ','');

UPDATE POOL_CRITERIA_DEF_MAPPING SET POOL_CRITERIA_VALUE = replace(POOL_CRITERIA_VALUE,'& #EXECUTION_LANGUAGE_VERSION# = 7.16 ',''); 

ALTER TABLE `MODEL_EXECUTION_ENVIRONMENTS`	ADD COLUMN `IS_ACTIVE` CHAR(1) NOT NULL DEFAULT 'F' AFTER `NAME`;

ALTER TABLE `MODEL_EXECUTION_ENVIRONMENTS`	SET `IS_ACTIVE`='T' WHERE EXECUTION_ENVIRONMENT='Excel';

ALTER TABLE `MODEL_EXECUTION_ENVIRONMENTS`	SET `IS_ACTIVE`='T' WHERE EXECUTION_ENVIRONMENT='Matlab';

INSERT INTO `MODEL_EXECUTION_ENVIRONMENTS` (`ID`, `EXECUTION_ENVIRONMENT`, `ENVIRONMENT_VERSION`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`, `NAME`, `IS_ACTIVE`) VALUES (UUID(), 'R', '3.3.2', 'nagamani.basa', 1479800282, 'nagamani.basa', 1479800292, 'R-3.3.2', 'T');

ALTER TABLE `MODEL_EXEC_PACKAGES` 
DROP INDEX  `UNIQUE_PACKAGE_NAME`,	
ADD UNIQUE INDEX `UNIQUE_PACKAGE_NAME` (`PACKAGE_NAME`, `MODEL_EXEC_ENV_NAME`);

ALTER TABLE `MODEL_EXEC_PACKAGES`	
DROP INDEX `PACKAGE_FOLDER_VERSION_ENV_CONSTRAINT`,
ADD UNIQUE INDEX `PACKAGE_FOLDER_VERSION_ENV_CONSTRAINT` (`PACKAGE_FOLDER`, `PACKAGE_VERSION`, `EXECUTION_ENVIRONMENT`, `MODEL_EXEC_ENV_NAME`);
	
DELETE FROM `POOL_CRITERIA` WHERE  `CRITERIA_NAME`='EXECUTION_LANGUAGE_VERSION';
UPDATE `POOL_CRITERIA` SET `CRITERIA_PRIORITY`=2 WHERE  `CRITERIA_NAME`='EXECUTION_ENVIRONMENT';
UPDATE `POOL_CRITERIA` SET `CRITERIA_PRIORITY`=3 WHERE  `CRITERIA_NAME`='MODEL';
UPDATE `POOL_CRITERIA` SET `CRITERIA_PRIORITY`=4 WHERE  `CRITERIA_NAME`='MODEL_VERSION';
UPDATE `POOL_CRITERIA` SET `CRITERIA_PRIORITY`=5 WHERE  `CRITERIA_NAME`='TENANT';
UPDATE `POOL_CRITERIA` SET `CRITERIA_PRIORITY`=6 WHERE  `CRITERIA_NAME`='TRANSACTION_MODE';
UPDATE `POOL_CRITERIA` SET `CRITERIA_PRIORITY`=7 WHERE  `CRITERIA_NAME`='TRANSACTION_TYPE';
UPDATE `POOL_CRITERIA` SET `CRITERIA_PRIORITY`=8 WHERE  `CRITERIA_NAME`='CHANNEL';



commit;