-- -----------------------------------------------------
-- UMG_ADMIN SCHEMA CHANGES
-- -----------------------------------------------------


INSERT INTO SYSTEM_KEY(ID,SYSTEM_KEY,KEY_TYPE,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON) VALUES ('15a69810-e665-12e3-b687-98654f4fc15c','BATCH_ENABLED','TENANT','SYSTEM',1401344421,'SYSTEM',1401344421);

-- -----------------------------------------------------
-- Table `TRANSPORT_TYPES`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `TRANSPORT_TYPES` (
  ID CHAR(36) NOT NULL,
  NAME VARCHAR(45) NOT NULL,
  CREATED_BY CHAR(36) NOT NULL COMMENT 'User created the record.',
  CREATED_ON BIGINT(20) NOT NULL COMMENT 'Record created time.',
  LAST_UPDATED_BY CHAR(36) NULL COMMENT 'User last updated the record.',
  LAST_UPDATED_ON BIGINT(20) NULL COMMENT 'Record last updated time.',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `TRANSPORT_PARAMETERS`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `TRANSPORT_PARAMETERS` (
  ID CHAR(36) NOT NULL,
  TRANSPORT_TYPE_ID VARCHAR(45) NOT NULL,
  PARAMETER_NAME VARCHAR(45) NOT NULL,
  DEFAULT_VALUE VARCHAR(45) NULL,
  CREATED_BY CHAR(36) NOT NULL COMMENT 'User created the record.',
  CREATED_ON BIGINT(20) NOT NULL COMMENT 'Record created time.',
  LAST_UPDATED_BY CHAR(36) NULL COMMENT 'User last updated the record.',
  LAST_UPDATED_ON BIGINT(20) NULL COMMENT 'Record last updated time.',
  PRIMARY KEY (`ID`),
  CONSTRAINT `FK_TRANSPORT_PARAMETERS_TRANSPORT_TYPE` FOREIGN KEY (`TRANSPORT_TYPE_ID`) REFERENCES `TRANSPORT_TYPES` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION  
  )
ENGINE = InnoDB;