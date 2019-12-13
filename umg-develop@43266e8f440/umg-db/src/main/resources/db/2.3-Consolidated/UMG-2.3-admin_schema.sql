use umg_admin;

alter table USERS add column TENANT_CODE varchar(45) not null default 'localhost' comment 'Tenant Code';
alter table USERS add constraint unq_user_per_tennant unique (username, TENANT_CODE);


alter table USERS 
add column NAME varchar(126) comment 'Name of User',
add column OFFICIAL_EMAIL varchar(252) comment 'Official E-mail ID of User',
add column ORGANIZATION varchar(126) comment 'Organization of User',
add column COMMENTS varchar(252) comment 'Comment of User',
add column CREATED_ON bigint(20) comment 'Created Date in milliseconds in GMT',
add column LAST_ACTIVATED_ON bigint(20) comment 'Last Activated Date of this User in milliseconds in GMT',
add column LAST_DEACTIVATED_ON bigint(20) comment 'Last Deactivated Date of this User in milliseconds in GMT';



alter table USER_ROLES drop column user_role_id;
alter table USER_ROLES drop index uni_username_role;
alter table USER_ROLES add column TENANT_CODE varchar(45) not null default 'localhost' comment 'Tenant Code';
alter table USER_ROLES add constraint unq_user_per_role_per_tenant unique (username, ROLE, TENANT_CODE);

drop table if exists USERS_LOGIN_AUDIT;

create table USERS_LOGIN_AUDIT(username varchar(50) not null,
tenant_code varchar(45) not null,
sys_ip_address varchar(15) not null,
access_on bigint(20) not null,
activity varchar(126) not null,
reason_code varchar(32) not null,
PRIMARY KEY (access_on),
CONSTRAINT FK_USERS_LOGIN_AUDIT_KEY FOREIGN KEY (username, tenant_code) REFERENCES USERS (username, tenant_code) ON DELETE NO ACTION ON UPDATE NO ACTION);


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

INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('15c27bfe-4491-41a8-98d5-276735f36bc9', 'createModel', 'Create model definition.', 5, 'CREATE', 'SYSTEM', 12354856456, NULL, NULL);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('22d294bf-85b9-4d60-8e0f-ed0d8a36da2a', 'symanticCheckModelIOXml', 'Symantic validation of uploaded model definiition file.', 4, 'CREATE', 'SYSTEM', 12354856456, NULL, NULL);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('28483ea2-740d-4b3c-b858-27afd0326944', 'createModelLibrary', 'Create model library command.', 2, 'CREATE', 'SYSTEM', 12354856456, NULL, NULL);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('46d54377-5e5e-4cdf-a0cd-4b11ef5c096b', 'testVersion', 'Test version.', 9, 'CREATE', 'SYSTEM', 12354856456, NULL, NULL);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('5c6c2228-5e25-4d6b-b989-0e41b00c4eec', 'generateTestInput', 'Generate test input json.', 8, 'CREATE', 'SYSTEM', 12354856456, NULL, NULL);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('5eb9aede-1c06-40ea-bd33-bd436b314816', 'createMapping', 'Create default Mapping.', 6, 'CREATE', 'SYSTEM', 12354856456, NULL, NULL);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('640f5323-1f7d-4d33-98c2-b753291fc375', 'createVersion', 'Create default version.', 7, 'CREATE', 'SYSTEM', 12354856456, NULL, NULL);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('9f7a95a9-dfad-489b-a1bd-420bf589c743', 'validateLibraryChecksum', 'Validate checksum of the uploaded model library jar file.', 1, 'CREATE', 'SYSTEM', 12354856456, NULL, NULL);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('ebfed01f-30a8-460c-95fc-3f7215df2f65', 'validateModelIOXml', 'Valiadate uploaded model definition file..', 3, 'CREATE', 'SYSTEM', 12354856456, NULL, NULL);

commit;