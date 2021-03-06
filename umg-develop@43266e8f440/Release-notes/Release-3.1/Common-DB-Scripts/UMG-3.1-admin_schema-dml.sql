use umg_admin;

UPDATE POOL_CRITERIA_DEF_MAPPING SET POOL_CRITERIA_VALUE = '#tenantCode# = HUBZU & #executionEnvironment# = R & #executionEnvironmentVersion# = 3.1.2 & #transactionRequestType# = ANY & #modelName# = hubzuweek0 & #modelVersion# = ANY & #transactionRequestMode# = ANY' where pool_id = 5;
	
INSERT INTO `POOL_CRITERIA` (`ID`, `CRITERIA_NAME`, `CRITERIA_PRIORITY`) VALUES
	('1', 'TENANT', 5),
	('2', 'ENVIRONMENT', 1),
	('3', 'ENVIRONMENT_VERSION', 2),
	('4', 'TRANSACTION_TYPE', 6),
	('5', 'TRANSACTION_MODE', 7),
	('6', 'MODEL', 3),
	('7', 'MODEL_VERSION', 4);
	
UPDATE `POOL_CRITERIA_DEF_MAPPING` p
SET p.POOL_CRITERIA_VALUE = 
REPLACE (POOL_CRITERIA_VALUE, 'tenantCode', 'TENANT' );

UPDATE `POOL_CRITERIA_DEF_MAPPING` p
SET p.POOL_CRITERIA_VALUE = 
REPLACE (POOL_CRITERIA_VALUE, 'executionEnvironmentVersion', 'ENVIRONMENT_VERSION' );

UPDATE `POOL_CRITERIA_DEF_MAPPING` p
SET p.POOL_CRITERIA_VALUE = 
REPLACE (POOL_CRITERIA_VALUE, 'executionEnvironment', 'ENVIRONMENT' );

UPDATE `POOL_CRITERIA_DEF_MAPPING` p
SET p.POOL_CRITERIA_VALUE = 
REPLACE (POOL_CRITERIA_VALUE, 'transactionRequestType', 'TRANSACTION_TYPE' );

UPDATE `POOL_CRITERIA_DEF_MAPPING` p
SET p.POOL_CRITERIA_VALUE = 
REPLACE (POOL_CRITERIA_VALUE, 'modelName', 'MODEL' );

UPDATE `POOL_CRITERIA_DEF_MAPPING` p
SET p.POOL_CRITERIA_VALUE = 
REPLACE (POOL_CRITERIA_VALUE, 'modelVersion', 'MODEL_VERSION' );

UPDATE `POOL_CRITERIA_DEF_MAPPING` p
SET p.POOL_CRITERIA_VALUE = 
REPLACE (POOL_CRITERIA_VALUE, 'transactionRequestMode', 'TRANSACTION_MODE' );



UPDATE `MODEL_EXECUTION_ENVIRONMENTS` SET `ENVIRONMENT_VERSION`='3.2.1' WHERE  `NAME`='R-3.1.2';
UPDATE `MODEL_EXECUTION_ENVIRONMENTS` SET `NAME`='R-3.2.1' WHERE  `NAME`='R-3.1.2';
UPDATE `MODEL_EXEC_PACKAGES` SET `MODEL_EXEC_ENV_NAME`='R-3.2.1' WHERE `MODEL_EXEC_ENV_NAME`='R-3.1.2';
UPDATE `MODEL_EXEC_PACKAGES_AUDIT`  SET `MODEL_EXEC_ENV_NAME`='R-3.2.1' WHERE `MODEL_EXEC_ENV_NAME`='R-3.1.2';
UPDATE `POOL_CRITERIA_DEF_MAPPING` p SET p.POOL_CRITERIA_VALUE = REPLACE (POOL_CRITERIA_VALUE, '3.1.2', '3.2.1' );
UPDATE `COMMAND` SET `EXECUTION_SEQUENCE`=5 WHERE  `NAME`='symanticCheckModelIOXml';
UPDATE `COMMAND` SET `EXECUTION_SEQUENCE`=6 WHERE  `NAME`='validateModelIOXml';

update POOL set MODELET_COUNT = 6 where POOL_NAME = 'MATLAB_DEFAULT';
update POOL set MODELET_COUNT = 2 where POOL_NAME = 'MATLAB_BATCH_POOL';
update POOL set MODELET_COUNT = 2 where POOL_NAME = 'HUBZU_WEEK_N';
update POOL set MODELET_COUNT = 2 where POOL_NAME = 'R_DEFAULT_POOL';
update POOL set MODELET_COUNT = 2 where POOL_NAME = 'HUBZU_WEEK_0';

COMMIT;