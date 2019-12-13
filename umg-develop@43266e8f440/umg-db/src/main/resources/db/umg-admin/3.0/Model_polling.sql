USE umg_admin;


-- SYSTEM PARAMETERS

SET SQL_SAFE_UPDATES = 0;

SET @MATLAB_MAX_MODELET_COUNT = '8';
SET @R_MAX_MODELET_COUNT = '6';

SET @MATLAB_MIN_MODELET_COUNT = '4';
SET @R_MIN_MODELET_COUNT = '2';

DELETE FROM SYSTEM_PARAMETER WHERE id in ('094B30C4-9778-4A12-8EE6-81723C958CB7', '094B30C4-9778-4A12-8EE6-81723C958CB8', '094B30C4-9778-4A12-8EE6-81723C958CB9', '094B30C4-9778-4A12-8EE6-81723C958CC1', '094B30C4-9778-4A12-8EE6-81723C958CC2', '094B30C4-9778-4A12-8EE6-81723C958CC3');

INSERT INTO SYSTEM_PARAMETER (id, sys_key, sys_value, is_active, created_by, created_on, last_updated_by, last_updated_on) VALUES
	('094B30C4-9778-4A12-8EE6-81723C958CB7', 'MATLAB_MAX_MODELET_COUNT', @MATLAB_MAX_MODELET_COUNT, 'Y', 'system', 1417439330020, 'system', 1417439330020);

INSERT INTO SYSTEM_PARAMETER (id, sys_key, sys_value, is_active, created_by, created_on, last_updated_by, last_updated_on) VALUES
	('094B30C4-9778-4A12-8EE6-81723C958CB8', 'R_MAX_MODELET_COUNT', @R_MAX_MODELET_COUNT, 'Y', 'system', 1417439330020, 'system', 1417439330020);

	
INSERT INTO SYSTEM_PARAMETER (id, sys_key, sys_value, is_active, created_by, created_on, last_updated_by, last_updated_on) VALUES
	('094B30C4-9778-4A12-8EE6-81723C958CC1', 'MATLAB_MIN_MODELET_COUNT', @MATLAB_MIN_MODELET_COUNT, 'Y', 'system', 1417439330020, 'system', 1417439330020);

INSERT INTO SYSTEM_PARAMETER (id, sys_key, sys_value, is_active, created_by, created_on, last_updated_by, last_updated_on) VALUES
	('094B30C4-9778-4A12-8EE6-81723C958CC2', 'R_MIN_MODELET_COUNT', @R_MIN_MODELET_COUNT, 'Y', 'system', 1417439330020, 'system', 1417439330020);


-- POOL TABLE
DELETE FROM POOL_USAGE_ORDER;
DELETE FROM POOL_CRITERIA_DEF_MAPPING;
DELETE FROM POOL;


ALTER TABLE POOL DROP COLUMN IS_BATCH_POOL;
ALTER TABLE POOL DROP COLUMN BATCH_STATUS;

ALTER TABLE POOL ADD COLUMN IS_DEFAULT_POOL boolean NOT NULL COMMENT 'Flag fog default pool';
ALTER TABLE POOL ADD COLUMN ENVIRONMENT varchar(32) NOT NULL COMMENT 'No of Modelelts allocated to this pool';
ALTER TABLE POOL ADD COLUMN POOL_STATUS varchar(32) NULL COMMENT 'Status of pool (IN_PROGRESS, etc, will be used for batch)';
ALTER TABLE POOL ADD COLUMN MODELET_COUNT int NOT NULL COMMENT 'No of Modelelts allocated to this pool';
ALTER TABLE POOL ADD COLUMN MODELET_CAPACITY varchar(32) NOT NULL COMMENT 'Max Heap size of Modelet';
ALTER TABLE POOL ADD COLUMN PRIORITY int NOT NULL COMMENT 'Priority of pool'; 

ALTER TABLE POOL ADD UNIQUE INDEX UNIQUE_POOL_NAME (POOL_NAME);
ALTER TABLE POOL ADD UNIQUE INDEX UNIQUE_POOL_PRIORITY (ENVIRONMENT, PRIORITY);


# Matlab Pools
INSERT INTO POOL (ID, POOL_NAME, POOL_DESCRIPTION, IS_DEFAULT_POOL, ENVIRONMENT, MODELET_COUNT, MODELET_CAPACITY, POOL_STATUS, PRIORITY) 
	VALUES ('1', 'EQUATOR', 'Pool for Equator to call any Matlab model', false, 'Matlab', 2, '1GB - Linux 64 bit', null, 1);

INSERT INTO POOL (ID, POOL_NAME, POOL_DESCRIPTION, IS_DEFAULT_POOL, ENVIRONMENT, MODELET_COUNT, MODELET_CAPACITY, POOL_STATUS, PRIORITY) 
	VALUES ('2', 'MATLAB_BATCH_POOL', 'Pool for all Matlab batch transactions', false, 'Matlab', 2, '1GB - Linux 64 bit', null, 2);

INSERT INTO POOL (ID, POOL_NAME, POOL_DESCRIPTION, IS_DEFAULT_POOL, ENVIRONMENT, MODELET_COUNT, MODELET_CAPACITY, POOL_STATUS, PRIORITY) 
	VALUES ('3', 'MATLAB_DEFAULT_POOL', 'Default Pool for MATLAB Online', true, 'Matlab', 4, '1GB - Linux 64 bit', null, 3);



# R pools	
INSERT INTO POOL (ID, POOL_NAME, POOL_DESCRIPTION, IS_DEFAULT_POOL, ENVIRONMENT, MODELET_COUNT, MODELET_CAPACITY, POOL_STATUS, PRIORITY) 
	VALUES ('4', 'HUBZU_WEEK_ZERO', 'Pool for HUBZU Zero Week Model', false, 'R', 2, '4GB - Linux 64 bit', null, 1);

INSERT INTO POOL (ID, POOL_NAME, POOL_DESCRIPTION, IS_DEFAULT_POOL, ENVIRONMENT, MODELET_COUNT, MODELET_CAPACITY, POOL_STATUS, PRIORITY) 
	VALUES ('5', 'HUBZU_WEEK_N', 'Pool for HUBZU 1-N Week Model', false, 'R', 2, '4GB - Linux 64 bit', null, 2);

INSERT INTO POOL (ID, POOL_NAME, POOL_DESCRIPTION, IS_DEFAULT_POOL, ENVIRONMENT, MODELET_COUNT, MODELET_CAPACITY, POOL_STATUS, PRIORITY) 
	VALUES ('6', 'R_DEFAULT_POOL', 'Default Pool for R', true, 'R', 2, '4GB - Linux 64 bit', null, 3);




-- CRITERIA TABLE
ALTER TABLE POOL_CRITERIA DROP COLUMN CRITERIA_ORDER;

ALTER TABLE POOL_CRITERIA ADD UNIQUE INDEX UNIQUE_POOL_CRITERIA (CRITERIA_NAME);

INSERT INTO POOL_CRITERIA (ID, CRITERIA_NAME) VALUES ('5', 'TRANSACTION_MODE');
INSERT INTO POOL_CRITERIA (ID, CRITERIA_NAME) VALUES ('6', 'MODEL');
INSERT INTO POOL_CRITERIA (ID, CRITERIA_NAME) VALUES ('7', 'MODEL_VERSION');




-- POOL CRITERIA MAPPING

ALTER TABLE POOL_CRITERIA_DEF_MAPPING DROP COLUMN POOL_CRITERIA_VALUE;
ALTER TABLE POOL_CRITERIA_DEF_MAPPING ADD COLUMN POOL_CRITERIA_VALUE varchar(512) NOT NULL;

ALTER TABLE POOL_CRITERIA_DEF_MAPPING ADD UNIQUE INDEX UNIQUE_POOL_CRITERIA_DEF_MAPPING (POOL_ID);

INSERT INTO POOL_CRITERIA_DEF_MAPPING (ID, POOL_ID, POOL_CRITERIA_VALUE) VALUES 
	('1', '1', '#tenantCode# = EQUATOR & #executionEnvironment# = MATLAB & #executionEnvironmentVersion# = 7.16 & #transactionRequestType# = ONLINE & #modelName# = ANY & #modelVersion# = ANY & #transactionRequestMode# = ANY');

INSERT INTO POOL_CRITERIA_DEF_MAPPING (ID, POOL_ID, POOL_CRITERIA_VALUE) VALUES 
	('2', '2', '#tenantCode# = ANY & #executionEnvironment# = MATLAB & #executionEnvironmentVersion# = 7.16 & #transactionRequestType# = BATCH & #modelName# = ANY & #modelVersion# = ANY & #transactionRequestMode# = ANY'); 

INSERT INTO POOL_CRITERIA_DEF_MAPPING (ID, POOL_ID, POOL_CRITERIA_VALUE) VALUES 
	('3', '3', '#tenantCode# = ANY & #executionEnvironment# = MATLAB & #executionEnvironmentVersion# = 7.16 & #transactionRequestType# = ONLINE & #modelName# = ANY & #modelVersion# = ANY & #transactionRequestMode# = ANY'); 

INSERT INTO POOL_CRITERIA_DEF_MAPPING (ID, POOL_ID, POOL_CRITERIA_VALUE) VALUES
	('4', '4', '#tenantCode# = HUBZU & #executionEnvironment# = R & #executionEnvironmentVersion# = 3.1.2 & #transactionRequestType# = ANY & #modelName# = HUBZU_1 & #modelVersion# = 1.0 & #transactionRequestMode# = ANY');

INSERT INTO POOL_CRITERIA_DEF_MAPPING (ID, POOL_ID, POOL_CRITERIA_VALUE) VALUES
	('5', '5', '#tenantCode# = HUBZU & #executionEnvironment# = R & #executionEnvironmentVersion# = 3.1.2 & #transactionRequestType# = ANY & #modelName# = HUBZU_2 & #modelVersion# = 2.0 & #transactionRequestMode# = ANY');
		
INSERT INTO POOL_CRITERIA_DEF_MAPPING (ID, POOL_ID, POOL_CRITERIA_VALUE) VALUES
	('6', '6', '#tenantCode# = HUBZU & #executionEnvironment# = R & #executionEnvironmentVersion# = 3.1.2 & #transactionRequestType# = ONLINE & #modelName# = ANY & #modelVersion# = ANY & #transactionRequestMode# = ANY');


-- POOL_USAGE_ORDER


ALTER TABLE POOL_USAGE_ORDER ADD UNIQUE INDEX UNIQUE_POOL_USAGE_ORDER (POOL_ID, POOL_USAGE_ID);

INSERT INTO POOL_USAGE_ORDER (ID, POOL_ID, POOL_USAGE_ID, POOL_TRY_ORDER) VALUES
	('1', '1', '1', 1),
	('2', '1', '2', 2),
	('3', '2', '2', 1),
	('4', '3', '3', 1),
	('5', '3', '2', 2),
	('6', '4', '4', 1),
	('7', '5', '5', 1),
	('8', '6', '6', 1);


COMMIT;