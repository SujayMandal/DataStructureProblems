CREATE TABLE UMG_VERSION (
  ID CHAR(36) NOT NULL,
  TENANT_ID CHAR(36) NOT NULL COMMENT 'Tenant code for the record',
  NAME VARCHAR(100) NOT NULL COMMENT 'Name of the UMG verison',
  DESCRIPTION VARCHAR(250) NOT NULL COMMENT 'Description of UMG Version',
  MAJOR_VERSION INT(10) NOT NULL COMMENT 'Major version',
  MINOR_VERSION INT(10) NOT NULL COMMENT 'Minor version',
  MAPPING_ID CHAR(36) NOT NULL COMMENT 'Mapping used by this version',
  MODEL_LIBRARY_ID CHAR(36) NOT NULL COMMENT 'MODEL library used by this version',
  STATUS VARCHAR(20),
  VERSION_DESCRIPTION VARCHAR(200) NOT NULL COMMENT 'Description of major or minor version',
  PUBLISHED_ON  BIGINT(20) COMMENT 'UMG version published time.',
  PUBLISHED_BY CHAR(36) COMMENT 'User who published the umg version',
  DEACTIVATED_ON BIGINT COMMENT 'UMG version deactivated time.',
  DEACTIVATED_BY CHAR(36) COMMENT 'User who deactivated the umg version',
  CREATED_BY CHAR(36) NOT NULL COMMENT 'User created the record.',
  CREATED_ON BIGINT(20) NOT NULL COMMENT 'Record created time.',
  LAST_UPDATED_BY CHAR(36) NULL COMMENT 'User last updated the record.',
  LAST_UPDATED_ON BIGINT(20) NULL COMMENT 'Record last updated time.',
  PRIMARY KEY (ID),
  UNIQUE INDEX UN_UMG_VERSION (TENANT_ID,NAME,MAJOR_VERSION,MINOR_VERSION),
  INDEX FK_IDX_UMG_VERSION_MAPPING (MAPPING_ID),
  INDEX FK_IDX_UMG_VERSION_MODEL_LIBRARY (MODEL_LIBRARY_ID),
  CONSTRAINT FK_UMG_VERSION_MAPPING FOREIGN KEY (MAPPING_ID) REFERENCES MAPPING(ID)  ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT FK_UMG_VERSION_MODEL_LIBRARY FOREIGN KEY (MODEL_LIBRARY_ID) REFERENCES MODEL_LIBRARY(ID)  ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;