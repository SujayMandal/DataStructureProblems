CREATE TABLE UMG_RUNTIME_TRANSACTION (
  ID CHAR(36) NOT NULL,
  TENANT_ID CHAR(36) NOT NULL COMMENT 'Tenant code for the record',
  CLIENT_TRANSACTION_ID VARCHAR(50) NOT NULL COMMENT 'Transaction Number',
  LIBRARY_NAME VARCHAR(50) NOT NULL COMMENT 'Name of the model library, given by user as TAG.',
  VERSION_NAME VARCHAR(100) NOT NULL COMMENT 'umg version used for the transaction - Tenant Model Name in UI',
  MAJOR_VERSION INT(10) NOT NULL COMMENT 'Major version',
  MINOR_VERSION INT(10) NOT NULL COMMENT 'Minor version',
  STATUS VARCHAR(10) NOT NULL COMMENT 'Transaction Status',
  TENANT_INPUT BLOB NOT NULL COMMENT 'The actual tenant input.',
  TENANT_OUTPUT MEDIUMBLOB NOT NULL COMMENT 'The actual tenant output.',
  MODEL_INPUT BLOB NOT NULL COMMENT 'The actual input for the model.',
  MODEL_OUTPUT MEDIUMBLOB NOT NULL COMMENT 'The actual output from the model.',
  RUN_AS_OF_DATE BIGINT(20) NOT NULL COMMENT 'Run Date From',
  RUNTIME_CALL_START BIGINT(20) NULL COMMENT 'Runtime Call Start Time',
  RUNTIME_CALL_END BIGINT(20) NULL COMMENT 'Runtime Call End Time',
  MODEL_CALL_START BIGINT(20) NULL COMMENT 'Model Call Start Time',
  MODEL_CALL_END BIGINT(20) NULL COMMENT 'Model Call End Time',
  IS_TEST TINYINT NOT NULL COMMENT 'Transaction run during test bed',
  CREATED_BY CHAR(36) NOT NULL COMMENT 'User created the record.',
  CREATED_ON BIGINT(20) NOT NULL COMMENT 'Record created time.',
  LAST_UPDATED_BY CHAR(36) NULL COMMENT 'User last updated the record.',
  LAST_UPDATED_ON BIGINT(20) NULL COMMENT 'Record last updated time.',
  PRIMARY KEY (ID)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
