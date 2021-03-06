USE umg_admin;


DROP TABLE IF EXISTS ADDRESS;
DROP TABLE IF EXISTS TRANSPORT_PARAMETERS;
DROP TABLE IF EXISTS TRANSPORT_TYPES;
DROP TABLE IF EXISTS MODEL_EXEC_PACKAGES_AUDIT;
DROP TABLE IF EXISTS MODEL_EXECUTION_ENVIRONMENTS;
DROP TABLE IF EXISTS MODEL_EXEC_PACKAGES;
DROP TABLE IF EXISTS MODEL_IMPLEMENTATION_TYPE;
DROP TABLE IF EXISTS SYNDICATED_DATA_AUDIT;
DROP TABLE IF EXISTS SYNDICATED_DATA;
DROP TABLE IF EXISTS COMMAND;
DROP TABLE IF EXISTS USERS_LOGIN_AUDIT;
DROP TABLE IF EXISTS USER_ROLES ;
DROP TABLE IF EXISTS USERS ;
DROP TABLE IF EXISTS POOL_USAGE_ORDER;
DROP TABLE IF EXISTS POOL_CRITERIA_DEF_MAPPING;
DROP TABLE IF EXISTS POOL_CRITERIA;
DROP TABLE IF EXISTS POOL;
DROP TABLE IF EXISTS TENANT_CONFIG;
DROP TABLE IF EXISTS SYSTEM_KEY;
DROP TABLE IF EXISTS TENANT;
DROP TABLE IF EXISTS SYSTEM_PARAMETER;
DROP TABLE IF EXISTS SYSTEM_PARAMETER_AUDIT;
DROP TABLE IF EXISTS REVINFO;

CREATE TABLE IF NOT EXISTS REVINFO (
  REV INT(11) NOT NULL AUTO_INCREMENT,
  REVTSTMP BIGINT(20) DEFAULT NULL,
  REVBY VARCHAR(100),
  PRIMARY KEY (REV)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COLLATE=UTF8_BIN;

CREATE TABLE IF NOT EXISTS SYSTEM_KEY (
  ID CHAR(36) COLLATE UTF8_BIN NOT NULL,
  SYSTEM_KEY VARCHAR(45) COLLATE UTF8_BIN NOT NULL,
  KEY_TYPE VARCHAR(200) COLLATE UTF8_BIN NOT NULL,
  CREATED_BY CHAR(100) COLLATE UTF8_BIN NOT NULL,
  CREATED_ON BIGINT(20) NOT NULL,
  LAST_UPDATED_BY CHAR(100) COLLATE UTF8_BIN DEFAULT NULL,
  LAST_UPDATED_ON BIGINT(20) DEFAULT NULL,
  PRIMARY KEY (ID)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COLLATE=UTF8_BIN;



CREATE TABLE IF NOT EXISTS TENANT (
  ID CHAR(36) COLLATE UTF8_BIN NOT NULL,
  NAME VARCHAR(45) COLLATE UTF8_BIN NOT NULL COMMENT 'TENANT NAME',
  DESCRIPTION VARCHAR(255) COLLATE UTF8_BIN DEFAULT NULL,
  CODE VARCHAR(45) COLLATE UTF8_BIN NOT NULL COMMENT 'TENANT CODE USED FOR UNIQUE IDENTIFICAITON',
  TENANT_TYPE VARCHAR(45) COLLATE UTF8_BIN NOT NULL,
  AUTH_TOKEN VARCHAR(64 ) COLLATE UTF8_BIN NOT NULL COMMENT 'GENERATED AUTHENTICATION TOKEN',
  CREATED_BY CHAR(100) COLLATE UTF8_BIN NOT NULL,
  CREATED_ON BIGINT(20) NOT NULL,
  LAST_UPDATED_BY CHAR(100) COLLATE UTF8_BIN DEFAULT NULL,
  LAST_UPDATED_ON BIGINT(20) DEFAULT NULL,
  PRIMARY KEY (ID),
  UNIQUE KEY UN_TENANT_NAME (NAME),
  UNIQUE KEY UN_TENANT_CODE (CODE)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COLLATE=UTF8_BIN;


CREATE TABLE IF NOT EXISTS TENANT_CONFIG (
  ID CHAR(36) COLLATE UTF8_BIN NOT NULL,
  TENANT_ID CHAR(36) COLLATE UTF8_BIN NOT NULL COMMENT 'TENANT WHERE THIS CONFIG PARAMETER BELONGS TO.',
  SYSTEM_KEY_ID CHAR(36) COLLATE UTF8_BIN NOT NULL COMMENT 'CONFIG PARAMETER KEY.',
  CONFIG_VALUE VARCHAR(500) COLLATE UTF8_BIN DEFAULT NULL COMMENT 'CONFIG PARAMETER VALUE.',
  ROLE CHAR(20) COLLATE UTF8_BIN DEFAULT NULL COMMENT 'ROLE .',
  CREATED_BY CHAR(100) COLLATE UTF8_BIN NOT NULL,
  CREATED_ON BIGINT(20) NOT NULL,
  LAST_UPDATED_BY CHAR(100) COLLATE UTF8_BIN DEFAULT NULL,
  LAST_UPDATED_ON BIGINT(20) DEFAULT NULL,
  PRIMARY KEY (ID),
  UNIQUE KEY UN_TENANT_CONFIG_KEY (TENANT_ID,SYSTEM_KEY_ID) COMMENT 'KEY FOR A TENANT IS UNIQUE',
  KEY FK_TENANT_CONFIG_SYSTEM_KEY_IDX (SYSTEM_KEY_ID),
  CONSTRAINT FK_TENANT_CONFIG_SYSTEM_KEY FOREIGN KEY (SYSTEM_KEY_ID) REFERENCES SYSTEM_KEY (ID) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT FK_TENANT_CONFIG_TENANT FOREIGN KEY (TENANT_ID) REFERENCES TENANT (ID) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COLLATE=UTF8_BIN;



CREATE TABLE IF NOT EXISTS ADDRESS (
  ID CHAR(36) COLLATE UTF8_BIN NOT NULL,
  TENANT_ID CHAR(36) COLLATE UTF8_BIN NOT NULL COMMENT 'TENANT ID FOR THE ADDRESS',
  ADDRESS_1 VARCHAR(45) COLLATE UTF8_BIN NOT NULL,
  ADDRESS_2 VARCHAR(45) COLLATE UTF8_BIN DEFAULT NULL,
  CITY VARCHAR(45) COLLATE UTF8_BIN NOT NULL,
  STATE VARCHAR(45) COLLATE UTF8_BIN NOT NULL,
  ZIP VARCHAR(45) COLLATE UTF8_BIN NOT NULL,
  COUNTRY VARCHAR(45) COLLATE UTF8_BIN NOT NULL,
  CREATED_BY VARCHAR(100) COLLATE UTF8_BIN NOT NULL,
  CREATED_ON BIGINT(20) NOT NULL,
  LAST_UPDATED_BY VARCHAR(100) COLLATE UTF8_BIN DEFAULT NULL,
  LAST_UPDATED_ON BIGINT(20) DEFAULT NULL,
  PRIMARY KEY (ID),
  CONSTRAINT FK_ADDRESS_TENANT FOREIGN KEY (TENANT_ID) REFERENCES TENANT (ID) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COLLATE=UTF8_BIN;




CREATE TABLE IF NOT EXISTS MODEL_IMPLEMENTATION_TYPE (
  ID CHAR(36) NOT NULL,
  IMPLEMENTATION VARCHAR(45) NOT NULL,
  TYPE_XSD BLOB NULL,
  CREATED_BY CHAR(100) NOT NULL,
  CREATED_ON BIGINT(20) NOT NULL,
  LAST_UPDATED_BY CHAR(100) NULL,
  LAST_UPDATED_ON BIGINT(20) NULL,
  PRIMARY KEY (ID)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COLLATE=UTF8_BIN;



CREATE TABLE IF NOT EXISTS SYNDICATED_DATA(
	ID CHAR(36) COLLATE UTF8_BIN NOT NULL,
	CONTAINER_NAME VARCHAR(50) COLLATE UTF8_BIN NOT NULL, 
	DESCRIPTION VARCHAR(200) COLLATE UTF8_BIN NOT NULL, 
	VERSION_ID BIGINT(4) COLLATE UTF8_BIN, 
	VERSION_NAME VARCHAR(50) NOT NULL COLLATE UTF8_BIN,
	VERSION_DESCRIPTION VARCHAR(200) NOT NULL COLLATE UTF8_BIN,
	TABLE_NAME VARCHAR(200) COLLATE UTF8_BIN,
	VALID_FROM BIGINT(20) COLLATE UTF8_BIN, 
	VALID_TO BIGINT(20) COLLATE UTF8_BIN, 
	CREATED_BY CHAR(100) COLLATE UTF8_BIN NOT NULL,
	CREATED_ON BIGINT(20) NOT NULL,
	LAST_UPDATED_BY CHAR(100) COLLATE UTF8_BIN DEFAULT NULL,
	LAST_UPDATED_ON BIGINT(20) DEFAULT NULL,
	PRIMARY KEY (ID),
	UNIQUE KEY INDEX_CINTAINER_NAME (CONTAINER_NAME),
	CONSTRAINT UNIQUEVERSION UNIQUE(CONTAINER_NAME, VERSION_ID)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COLLATE=UTF8_BIN;


CREATE TABLE IF NOT EXISTS SYNDICATED_DATA_AUDIT(
	ID CHARACTER(36),
	CONTAINER_NAME VARCHAR(50), 
	DESCRIPTION VARCHAR(200),
	VERSION_NAME VARCHAR(50),
	VERSION_DESCRIPTION VARCHAR(200),
	VERSION_ID BIGINT, 
	TABLE_NAME VARCHAR(200),
	VALID_FROM BIGINT, 
	VALID_TO BIGINT,
	CREATED_BY VARCHAR(100) NULL DEFAULT NULL,
	CREATED_ON BIGINT(20) NULL DEFAULT NULL,
	LAST_UPDATED_BY VARCHAR(100) NULL DEFAULT NULL,
	LAST_UPDATED_ON BIGINT(20) NULL DEFAULT NULL,
	REV INTEGER NOT NULL,
	REVTYPE INTEGER,
	PRIMARY KEY (ID,REV),
	CONSTRAINT FK_SYNDICATED_DATA_AUDIT_REVINFO FOREIGN KEY (REV) REFERENCES REVINFO (REV)
);





CREATE TABLE IF NOT EXISTS USERS (
USERNAME VARCHAR(50) NOT NULL ,
PASSWORD VARCHAR(100) NOT NULL ,
ENABLED TINYINT NOT NULL DEFAULT 1 ,
TENANT_CODE VARCHAR(45) NOT NULL DEFAULT 'LOCALHOST' COMMENT 'TENANT CODE',
NAME VARCHAR(126) COMMENT 'NAME OF USER',
OFFICIAL_EMAIL VARCHAR(252) COMMENT 'OFFICIAL E-MAIL ID OF USER',
ORGANIZATION VARCHAR(126) COMMENT 'ORGANIZATION OF USER',
COMMENTS VARCHAR(252) COMMENT 'COMMENT OF USER',
CREATED_ON BIGINT(20) COMMENT 'CREATED DATE IN MILLISECONDS IN GMT',
LAST_ACTIVATED_ON BIGINT(20) COMMENT 'LAST ACTIVATED DATE OF THIS USER IN MILLISECONDS IN GMT',
LAST_DEACTIVATED_ON BIGINT(20) COMMENT 'LAST DEACTIVATED DATE OF THIS USER IN MILLISECONDS IN GMT',
PRIMARY KEY (USERNAME),
CONSTRAINT UNQ_USER_PER_TENNANT UNIQUE (USERNAME, TENANT_CODE)
)ENGINE=INNODB DEFAULT CHARSET=UTF8 COLLATE=UTF8_BIN;






CREATE TABLE IF NOT EXISTS USER_ROLES (
USERNAME VARCHAR(50) NOT NULL,
ROLE VARCHAR(25) NOT NULL,
TENANT_CODE VARCHAR(45) NOT NULL DEFAULT 'LOCALHOST' COMMENT 'TENANT CODE',
KEY FK_USERNAME_IDX (USERNAME),
CONSTRAINT FK_USERNAME FOREIGN KEY (USERNAME) REFERENCES USERS (USERNAME),
CONSTRAINT UNQ_USER_PER_ROLE_PER_TENANT UNIQUE (USERNAME, ROLE, TENANT_CODE)
)ENGINE=INNODB DEFAULT CHARSET=UTF8 COLLATE=UTF8_BIN;



-- -----------------------------------------------------
-- TABLE TRANSPORT_TYPES
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS TRANSPORT_TYPES (
  ID CHAR(36) NOT NULL,
  NAME VARCHAR(45) NOT NULL,
  CREATED_BY CHAR(36) NOT NULL COMMENT 'USER CREATED THE RECORD.',
  CREATED_ON BIGINT(20) NOT NULL COMMENT 'RECORD CREATED TIME.',
  LAST_UPDATED_BY CHAR(36) NULL COMMENT 'USER LAST UPDATED THE RECORD.',
  LAST_UPDATED_ON BIGINT(20) NULL COMMENT 'RECORD LAST UPDATED TIME.',
  PRIMARY KEY (ID))
ENGINE = INNODB;

-- -----------------------------------------------------
-- TABLE TRANSPORT_PARAMETERS
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS TRANSPORT_PARAMETERS (
  ID CHAR(36) NOT NULL,
  TRANSPORT_TYPE_ID VARCHAR(45) NOT NULL,
  PARAMETER_NAME VARCHAR(45) NOT NULL,
  DEFAULT_VALUE VARCHAR(45) NULL,
  CREATED_BY CHAR(36) NOT NULL COMMENT 'USER CREATED THE RECORD.',
  CREATED_ON BIGINT(20) NOT NULL COMMENT 'RECORD CREATED TIME.',
  LAST_UPDATED_BY CHAR(36) NULL COMMENT 'USER LAST UPDATED THE RECORD.',
  LAST_UPDATED_ON BIGINT(20) NULL COMMENT 'RECORD LAST UPDATED TIME.',
  PRIMARY KEY (ID),
  CONSTRAINT FK_TRANSPORT_PARAMETERS_TRANSPORT_TYPE FOREIGN KEY (TRANSPORT_TYPE_ID) REFERENCES TRANSPORT_TYPES (ID) ON DELETE NO ACTION ON UPDATE NO ACTION  
  )
ENGINE = INNODB;



CREATE TABLE IF NOT EXISTS SYSTEM_PARAMETER(
			ID CHAR(36),
			SYS_KEY VARCHAR(100) ,
			SYS_VALUE VARCHAR(100),
			IS_ACTIVE CHAR(1),
			CREATED_BY VARCHAR(100),
			CREATED_ON BIGINT(20),
			LAST_UPDATED_BY VARCHAR(100),
			LAST_UPDATED_ON BIGINT(20),
			PRIMARY KEY(ID)
);
CREATE TABLE IF NOT EXISTS SYSTEM_PARAMETER_AUDIT(
			ID CHAR(36),
			SYS_KEY VARCHAR(100) ,
			SYS_VALUE VARCHAR(100),
			IS_ACTIVE CHAR(1),
			CREATED_BY VARCHAR(100),
			CREATED_ON BIGINT(20),
			LAST_UPDATED_BY VARCHAR(100),
			LAST_UPDATED_ON BIGINT(20),
			REV INT(11),
			REVTYPE INT(11),
			PRIMARY KEY(ID,REV)
);



CREATE TABLE IF NOT EXISTS USERS_LOGIN_AUDIT(USERNAME varchar(50) not null,
	TENANT_CODE varchar(45) not null,
	SYS_IP_ADDRESS varchar(15) not null,
	ACCESS_ON bigint(20) not null,
	ACTIVITY varchar(126) not null,
	REASON_CODE varchar(32) not null,
	PRIMARY KEY (access_on),
	INDEX FK_USERS_LOGIN_AUDIT_KEY (USERNAME, TENANT_CODE),
	CONSTRAINT FK_USERS_LOGIN_AUDIT_KEY FOREIGN KEY (USERNAME, TENANT_CODE)
	REFERENCES USERS (USERNAME, TENANT_CODE) ON UPDATE NO ACTION ON DELETE NO ACTION
)COLLATE='utf8_bin' ENGINE=InnoDB;




CREATE TABLE IF NOT EXISTS COMMAND (
  ID CHAR(36) NOT NULL,
  NAME VARCHAR(100) NOT NULL COMMENT 'NAME OF THE COMMAND',
  DESCRIPTION VARCHAR(200) NULL COMMENT 'DESCRIPTION OF THE COMMAND',
  EXECUTION_SEQUENCE INT(36) NOT NULL COMMENT 'COMMAND SEQUENCE ID',
  PROCESS VARCHAR(100) NOT NULL COMMENT 'NAME OF THE COMMAND',
  CREATED_BY CHAR(36) NOT NULL ,
  CREATED_ON BIGINT(20) NOT NULL,
  LAST_UPDATED_BY CHAR(36) NULL,
  LAST_UPDATED_ON BIGINT(20) NULL,
  PRIMARY KEY (ID),
  UNIQUE INDEX UN_COMMAND_SEQUENCE (NAME,PROCESS)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COLLATE=UTF8_BIN;






CREATE TABLE IF NOT EXISTS MODEL_EXECUTION_ENVIRONMENTS (
	ID CHAR(36) NOT NULL COLLATE 'utf8_bin',
	NAME VARCHAR(50) NOT NULL COMMENT 'Language name and version' COLLATE 'utf8_bin',
	EXECUTION_ENVIRONMENT VARCHAR(50) NOT NULL COMMENT 'Model execution environment.' COLLATE 'utf8_bin',
	ENVIRONMENT_VERSION VARCHAR(50) NOT NULL COMMENT 'Version of the execution environment.' COLLATE 'utf8_bin',
	CREATED_BY CHAR(50) NOT NULL COLLATE 'utf8_bin',
	CREATED_ON BIGINT(20) NOT NULL,
	LAST_UPDATED_BY CHAR(50) NULL DEFAULT NULL COLLATE 'utf8_bin',
	LAST_UPDATED_ON BIGINT(20) NULL DEFAULT NULL
);


CREATE TABLE IF NOT EXISTS MODEL_EXEC_PACKAGES (
	ID CHAR(36) NOT NULL COLLATE 'utf8_bin',
	MODEL_EXEC_ENV_NAME VARCHAR(50) NOT NULL COMMENT 'Language name and version',
	PACKAGE_NAME VARCHAR(200) NOT NULL COLLATE 'utf8_bin',
	PACKAGE_FOLDER VARCHAR(200) NOT NULL COLLATE 'utf8_bin',
	PACKAGE_VERSION VARCHAR(50) NOT NULL COLLATE 'utf8_bin',
	PACKAGE_TYPE VARCHAR(50) NOT NULL COLLATE 'utf8_bin',
	COMPILED_OS VARCHAR(50) NOT NULL COLLATE 'utf8_bin',
	CREATED_BY CHAR(50) NOT NULL COLLATE 'utf8_bin',
	CREATED_ON BIGINT(20) NOT NULL,
	LAST_UPDATED_BY CHAR(50) NULL DEFAULT NULL COLLATE 'utf8_bin',
	LAST_UPDATED_ON BIGINT(20) NULL DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS MODEL_EXEC_PACKAGES_AUDIT (
	ID CHAR(36) NOT NULL COLLATE 'utf8_bin',
	 MODEL_EXEC_ENV_NAME VARCHAR(50) NOT NULL COMMENT 'Language name and version',
	PACKAGE_NAME VARCHAR(200) NULL DEFAULT NULL COLLATE 'utf8_bin',
	PACKAGE_FOLDER VARCHAR(200) NULL DEFAULT NULL COLLATE 'utf8_bin',
	PACKAGE_VERSION VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8_bin',
	PACKAGE_TYPE VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8_bin',
	COMPILED_OS VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8_bin',
	REV INT(11) NOT NULL,
	REVTYPE TINYINT(4) NULL DEFAULT NULL,
	CREATED_BY CHAR(50) NULL DEFAULT NULL COLLATE 'utf8_bin',
	CREATED_ON BIGINT(20) NULL DEFAULT NULL,
	LAST_UPDATED_BY CHAR(50) NULL DEFAULT NULL COLLATE 'utf8_bin',
	LAST_UPDATED_ON BIGINT(20) NULL DEFAULT NULL
);




CREATE TABLE IF NOT EXISTS POOL (
  ID varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'Pool id',
  POOL_NAME varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'Priority Pool Name',
  POOL_DESCRIPTION varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT 'Priority Pool Description',
  IS_DEFAULT_POOL boolean NOT NULL COMMENT 'Flag fog default pool',
  ENVIRONMENT varchar(32) NOT NULL COMMENT 'No of Modelelts allocated to this pool',
  POOL_STATUS varchar(32) NULL COMMENT 'Status of pool (IN_PROGRESS, etc, will be used for batch)',
  MODELET_COUNT int NOT NULL COMMENT 'No of Modelelts allocated to this pool',
  MODELET_CAPACITY varchar(32) NOT NULL COMMENT 'Max Heap size of Modelet',
  PRIORITY int NOT NULL COMMENT 'Priority of pool',
  INDEX UNIQUE_POOL_NAME (POOL_NAME),
  UNIQUE INDEX UNIQUE_POOL_PRIORITY (ENVIRONMENT, PRIORITY),
   PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;



CREATE TABLE IF NOT EXISTS POOL_CRITERIA (
  ID varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'pool criteria id',
  CRITERIA_NAME varchar(100) COLLATE utf8_bin NOT NULL COMMENT 'pool criteria name',
  UNIQUE INDEX UNIQUE_POOL_CRITERIA (CRITERIA_NAME),
   PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Priority pool criterias';


CREATE TABLE IF NOT EXISTS POOL_CRITERIA_DEF_MAPPING (
  ID varchar(50) COLLATE utf8_bin NOT NULL,
  POOL_ID varchar(50) COLLATE utf8_bin NOT NULL,
   POOL_CRITERIA_VALUE varchar(255) NOT NULL,
  PRIMARY KEY (ID),
  UNIQUE INDEX UNIQUE_POOL_CRITERIA_DEF_MAPPING (POOL_ID),
  UNIQUE KEY FK_UNIQUE_CRITERIA (POOL_ID,POOL_CRITERIA_VALUE),
  CONSTRAINT FK_POOL_ID FOREIGN KEY (POOL_ID) REFERENCES POOL (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


CREATE TABLE IF NOT EXISTS POOL_USAGE_ORDER (
  ID varchar(50) COLLATE utf8_bin NOT NULL,
  POOL_ID varchar(50) COLLATE utf8_bin NOT NULL,
  POOL_USAGE_ID varchar(50) COLLATE utf8_bin NOT NULL,
  POOL_TRY_ORDER int(10) NOT NULL,
  PRIMARY KEY (ID),
  KEY FKPOOL_ID (POOL_ID),
   UNIQUE INDEX UNIQUE_POOL_USAGE_ORDER (POOL_ID, POOL_USAGE_ID),
  CONSTRAINT FKPOOL_ID FOREIGN KEY (POOL_ID) REFERENCES POOL (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

