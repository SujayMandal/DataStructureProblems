CREATE TABLE MODEL_LIBRARY (
	ID CHARACTER(36) NOT NULL,
	TENANT_ID CHAR(36) NOT NULL COMMENT 'Tenant code for the record',
	NAME VARCHAR(50) NOT NULL COMMENT 'Name of the model library, given by user as TAG.',
	DESCRIPTION VARCHAR(200) NOT NULL COMMENT 'Description of the model library.',
	UMG_NAME VARCHAR(100) NOT NULL COMMENT 'UMG provided name of the model library. TAG-MM-DD-YYYY-HH-MM',
	EXECUTION_LANGUAGE VARCHAR(25) NOT NULL COMMENT 'The execution language for the version', 
	EXECUTION_TYPE VARCHAR(20) NOT NULL COMMENT 'The execution mechanism for the version', 
	JAR_NAME VARCHAR(100) NOT NULL COMMENT 'The name of the JAR uploaded against the library',
	CREATED_BY CHAR(36) NOT NULL, 
	CREATED_ON BIGINT NOT NULL,
	LAST_UPDATED_BY CHAR(36) NULL, 
	LAST_UPDATED_ON BIGINT NULL,
	PRIMARY KEY (ID),
	UNIQUE INDEX `UN_MODEL_LIBRARY_UMG_NAME` (`UMG_NAME`)
)
COMMENT='The table which contains the individual model library definitions'
DEFAULT CHARSET=utf8
COLLATE='utf8_bin'
ENGINE=InnoDB;