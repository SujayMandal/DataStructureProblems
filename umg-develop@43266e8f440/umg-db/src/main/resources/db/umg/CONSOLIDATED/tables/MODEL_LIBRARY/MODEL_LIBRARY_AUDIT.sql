CREATE TABLE MODEL_LIBRARY_AUDIT (
	ID CHARACTER(36) NOT NULL,
	TENANT_ID CHAR(36) NULL COMMENT 'Tenant code for the record',
	NAME VARCHAR(50) COMMENT 'Name of the model library, given by user as TAG.',
	DESCRIPTION VARCHAR(200) COMMENT 'Description of the model library.',
	UMG_NAME VARCHAR(100) COMMENT 'UMG provided name of the model library. TAG-MM-DD-YYYY-HH-MM',
	EXECUTION_LANGUAGE VARCHAR(25) COMMENT 'The execution language for the version', 
	EXECUTION_TYPE VARCHAR(20) COMMENT 'The execution mechanism for the version', 
	JAR_NAME VARCHAR(100) COMMENT 'The name of the JAR uploaded against the library',
	CREATED_BY CHAR(100) NOT NULL ,
  	CREATED_ON BIGINT(20) NOT NULL,
  	LAST_UPDATED_BY CHAR(100) NULL,
  	LAST_UPDATED_ON BIGINT(20) NULL,
	REV INT(11) NOT NULL,
  	REVTYPE TINYINT(4) DEFAULT NULL,
	PRIMARY KEY (ID,REV),
  	KEY IDX_MODEL_LIBRARY_AUDIT_REVINFO (REV),
  	CONSTRAINT FK_MODEL_LIBRARY_AUDIT_REVINFO FOREIGN KEY (REV) REFERENCES REVINFO (REV)
)
COMMENT='The table which contains the individual model library definitions audit table'
DEFAULT CHARSET=utf8
COLLATE=utf8_bin
ENGINE=InnoDB;
