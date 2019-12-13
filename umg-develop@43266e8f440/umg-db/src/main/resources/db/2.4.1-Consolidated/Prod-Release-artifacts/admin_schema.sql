use umg_admin;

alter table USERS add column TENANT_CODE varchar(45) not null default 'ocwen' comment 'Tenant Code';
alter table USERS add constraint UNQ_USER_PER_TENNANT unique (USERNAME, TENANT_CODE);

alter table USERS 
add column NAME varchar(126) comment 'Name of User',
add column OFFICIAL_EMAIL varchar(252) comment 'Official E-mail ID of User',
add column ORGANIZATION varchar(126) comment 'Organization of User',
add column COMMENTS varchar(252) comment 'Comment of User',
add column CREATED_ON bigint(20) comment 'Created Date in milliseconds in GMT',
add column LAST_ACTIVATED_ON bigint(20) comment 'Last Activated Date of this User in milliseconds in GMT',
add column LAST_DEACTIVATED_ON bigint(20) comment 'Last Deactivated Date of this User in milliseconds in GMT';

alter table USER_ROLES drop column USER_ROLE_ID;
alter table USER_ROLES drop index UNI_USERNAME_ROLE;
alter table USER_ROLES add column TENANT_CODE varchar(45) not null default 'ocwen' comment 'Tenant Code';
alter table USER_ROLES add constraint UNQ_USER_PER_ROLE_PER_TENANT unique (USERNAME, ROLE, TENANT_CODE);

drop table if exists USERS_LOGIN_AUDIT;

create table USERS_LOGIN_AUDIT(USERNAME varchar(50) not null,
TENANT_CODE varchar(45) not null,
SYS_IP_ADDRESS varchar(15) not null,
ACCESS_ON bigint(20) not null,
ACTIVITY varchar(126) not null,
REASON_CODE varchar(32) not null,
PRIMARY KEY (access_on),
INDEX `FK_USERS_LOGIN_AUDIT_KEY` (`USERNAME`, `TENANT_CODE`),
CONSTRAINT `FK_USERS_LOGIN_AUDIT_KEY` FOREIGN KEY (`USERNAME`, `TENANT_CODE`)
REFERENCES `USERS` (`USERNAME`, `TENANT_CODE`) ON UPDATE NO ACTION ON DELETE NO ACTION
)COLLATE='utf8_bin' ENGINE=InnoDB;


DROP TABLE IF EXISTS COMMAND;

CREATE TABLE COMMAND (
  ID CHAR(36) NOT NULL,
  NAME VARCHAR(100) NOT NULL COMMENT 'Name of the command',
  DESCRIPTION VARCHAR(200) NULL COMMENT 'Description of the command',
  EXECUTION_SEQUENCE INT(36) NOT NULL COMMENT 'Command sequence id',
  PROCESS VARCHAR(100) NOT NULL COMMENT 'Name of the command',
  CREATED_BY CHAR(36) NOT NULL ,
  CREATED_ON BIGINT(20) NOT NULL,
  LAST_UPDATED_BY CHAR(36) NULL,
  LAST_UPDATED_ON BIGINT(20) NULL,
  PRIMARY KEY (ID),
  UNIQUE INDEX UN_COMMAND_SEQUENCE (NAME,PROCESS)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('3528F744-40A1-4779-89C6-9B5595E9D089', 'validateRManifestFile', 'Validating of Manifest file', 1, 'CREATE', 'SYSTEM', 12354856456, NULL, NULL);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('9f7a95a9-dfad-489b-a1bd-420bf589c743', 'validateLibraryChecksum', 'Validate checksum of the uploaded model library jar file.', 2, 'CREATE', 'SYSTEM', 12354856456, NULL, NULL);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('28483ea2-740d-4b3c-b858-27afd0326944', 'createModelLibrary', 'Create model library command.', 3, 'CREATE', 'SYSTEM', 12354856456, NULL, NULL);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('3528F744-40A1-4779-89C6-9B5595E9D019', 'convertExcelToXml', 'Conversion of Excel to Xml', 4, 'CREATE', 'SYSTEM', 12354856456, NULL, NULL);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('ebfed01f-30a8-460c-95fc-3f7215df2f65', 'validateModelIOXml', 'Valiadate uploaded model definition file..', 5, 'CREATE', 'SYSTEM', 12354856456, NULL, NULL);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('22d294bf-85b9-4d60-8e0f-ed0d8a36da2a', 'symanticCheckModelIOXml', 'Symantic validation of uploaded model definiition file.', 6, 'CREATE', 'SYSTEM', 12354856456, NULL, NULL);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('15c27bfe-4491-41a8-98d5-276735f36bc9', 'createModel', 'Create model definition.', 7, 'CREATE', 'SYSTEM', 12354856456, NULL, NULL);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('46d54377-5e5e-4cdf-a0cd-4b11ef5c096b', 'testVersion', 'Test version.', 11, 'CREATE', 'SYSTEM', 12354856456, NULL, NULL);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('5c6c2228-5e25-4d6b-b989-0e41b00c4eec', 'generateTestInput', 'Generate test input json.', 10, 'CREATE', 'SYSTEM', 12354856456, NULL, NULL);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('5eb9aede-1c06-40ea-bd33-bd436b314816', 'createMapping', 'Create default Mapping.', 8, 'CREATE', 'SYSTEM', 12354856456, NULL, NULL);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('640f5323-1f7d-4d33-98c2-b753291fc375', 'createVersion', 'Create default version.', 9, 'CREATE', 'SYSTEM', 12354856456, NULL, NULL);

DROP TABLE IF EXISTS POOL_USAGE_ORDER;
DROP TABLE IF EXISTS POOL_CRITERIA_DEF_MAPPING;
DROP TABLE IF EXISTS POOL;
DROP TABLE IF EXISTS POOL_CRITERIA;


CREATE TABLE POOL (
  ID varchar(50) NOT NULL COMMENT 'Pool id',
  POOL_NAME varchar(50) NOT NULL COMMENT 'Priority Pool Name',
  POOL_DESCRIPTION varchar(100) DEFAULT NULL COMMENT 'Priority Pool Description',
  IS_BATCH_POOL boolean DEFAULT false COMMENT 'Flag to indicate whether it is for batch pool or not',
  BATCH_STATUS varchar(25) DEFAULT NULL COMMENT 'Batch Status like SUCCESS, ERROR etc',
  PRIMARY KEY (ID)
);


INSERT INTO POOL (ID, POOL_NAME, IS_BATCH_POOL, POOL_DESCRIPTION) VALUES
	('1', 'ONLINE_POOL', false, 'POOL OF MODELETS FOR ONLINE REQUETS'),
	('2', 'BATCH_POOL', true, 'POOL OF MODELETS FOR BATCH REQUETS'),
	('3', 'DEFAULT_POOL', false, 'POOL OF DEFAULT MODELETS');


CREATE TABLE POOL_CRITERIA (
  ID varchar(50) NOT NULL COMMENT 'pool criteria id',
  CRITERIA_NAME varchar(100) NOT NULL COMMENT 'pool criteria name',
  CRITERIA_ORDER int(10) NOT NULL COMMENT 'criteria order',
  PRIMARY KEY (ID)
) COMMENT='Priority pool criterias';


INSERT INTO POOL_CRITERIA (ID, CRITERIA_NAME, CRITERIA_ORDER) VALUES
	('1', 'TENANT', 1),
	('2', 'ENVIRONMENT', 2),
	('3', 'ENVIRONMENT_VERSION', 3),
	('4', 'TRANSACTION_TYPE', 4);


CREATE TABLE POOL_CRITERIA_DEF_MAPPING (
  ID varchar(50) NOT NULL,
  POOL_ID varchar(50) NOT NULL,
  POOL_CRITERIA_VALUE varchar(200) NOT NULL,
  PRIMARY KEY (ID),
  UNIQUE KEY FK_UNIQUE_CRITERIA (POOL_ID,POOL_CRITERIA_VALUE),
  CONSTRAINT FK_POOL_ID FOREIGN KEY (POOL_ID) REFERENCES POOL (ID)
);

INSERT INTO POOL_CRITERIA_DEF_MAPPING (ID, POOL_ID, POOL_CRITERIA_VALUE) VALUES
    ('1', '1', 'TENANT = OCWEN AND ENVIRONMENT = MATLAB AND ENVIRONMENT_VERSION = 7.16 AND TRANSACTION_TYPE = ONLINE'),
	('2', '2', 'TENANT = OCWEN AND ENVIRONMENT = MATLAB AND ENVIRONMENT_VERSION = 7.16 AND TRANSACTION_TYPE = BATCH'),
	('3', '1', 'TENANT = OCWEN AND ENVIRONMENT = R AND ENVIRONMENT_VERSION = 3.1.2 AND TRANSACTION_TYPE = ONLINE'),
	('4', '2', 'TENANT = OCWEN AND ENVIRONMENT = R AND ENVIRONMENT_VERSION = 3.1.2 AND TRANSACTION_TYPE = BATCH'),
	('5', '1', 'TENANT = EQUATOR AND ENVIRONMENT = MATLAB AND ENVIRONMENT_VERSION = 7.16 AND TRANSACTION_TYPE = ONLINE'),
	('6', '2', 'TENANT = EQUATOR AND ENVIRONMENT = MATLAB AND ENVIRONMENT_VERSION = 7.16 AND TRANSACTION_TYPE = BATCH'),
	('7', '1', 'TENANT = EQUATOR AND ENVIRONMENT = R AND ENVIRONMENT_VERSION = 3.1.2 AND TRANSACTION_TYPE = ONLINE'),
	('8', '2', 'TENANT = EQUATOR AND ENVIRONMENT = R AND ENVIRONMENT_VERSION = 3.1.2 AND TRANSACTION_TYPE = BATCH');


CREATE TABLE IF NOT EXISTS POOL_USAGE_ORDER (
  ID varchar(50) NOT NULL,
  POOL_ID varchar(50) NOT NULL,
  POOL_USAGE_ID varchar(50) NOT NULL,
  POOL_TRY_ORDER int(10) NOT NULL,
  PRIMARY KEY (ID),
  KEY FKPOOL_ID (POOL_ID),
  CONSTRAINT FKPOOL_ID FOREIGN KEY (POOL_ID) REFERENCES POOL (ID)
);

INSERT INTO POOL_USAGE_ORDER (ID, POOL_ID, POOL_USAGE_ID, POOL_TRY_ORDER) VALUES
	('1', '1', '1', 1),
	('2', '1', '2', 2),
	('3', '1', '3', 3),

	('4', '2', '2', 1),
	('5', '2', '3', 2);

COMMIT;