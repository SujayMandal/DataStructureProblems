-- UMG1279
CREATE TABLE MIGRATION_LOG (
  ID CHAR(36) NOT NULL,
  TENANT_ID CHAR(36) NOT NULL COMMENT 'Tenant code for the record',
  VERSION_ID CHAR(36) COMMENT 'Version ID which is imported / exported',
  MIGRATION_TYPE VARCHAR(10) NOT NULL COMMENT 'Specifies whether it was an import / export entry',
  VERSION_DATA BLOB COMMENT 'Version Details',
  STATUS VARCHAR(20) NOT NULL COMMENT 'Migration Status',
  CREATED_BY CHAR(36) NOT NULL ,
  CREATED_ON BIGINT(20) NOT NULL,
  LAST_UPDATED_BY CHAR(36) NULL,
  LAST_UPDATED_ON BIGINT(20) NULL,
  PRIMARY KEY (ID),
  CONSTRAINT FK_VERSION FOREIGN KEY (VERSION_ID) REFERENCES UMG_VERSION(ID) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- UMG1286
ALTER TABLE UMG_RUNTIME_TRANSACTION ADD COLUMN ERROR_CODE CHAR(10) COMMENT 'Error code received while execution Model.';
ALTER TABLE UMG_RUNTIME_TRANSACTION ADD COLUMN ERROR_DESCRIPTION  VARCHAR(500) COLLATE utf8_bin DEFAULT NULL COMMENT 'Error description while Model execution.';

ALTER TABLE UMG_RUNTIME_TRANSACTION_AUDIT ADD COLUMN ERROR_CODE CHAR(10) COMMENT 'Error code received while execution Model.';
ALTER TABLE UMG_RUNTIME_TRANSACTION_AUDIT ADD COLUMN ERROR_DESCRIPTION  VARCHAR(500) COLLATE utf8_bin DEFAULT NULL COMMENT 'Error description while Model execution.';

-- UMG-BlobToMBlob.sql CHANGES

ALTER TABLE MAPPING 
ADD COLUMN `TEMP_MODEL_IO` MEDIUMBLOB NULL AFTER `STATUS`;

update MAPPING set TEMP_MODEL_IO=model_io_data ;

ALTER TABLE MAPPING 
DROP COLUMN `MODEL_IO_DATA`;


ALTER TABLE MAPPING 
CHANGE COLUMN `TEMP_MODEL_IO` `MODEL_IO_DATA` MEDIUMBLOB;


ALTER TABLE `MAPPING` 
CHANGE COLUMN `MODEL_IO_DATA` `MODEL_IO_DATA` MEDIUMBLOB NULL DEFAULT NULL AFTER `MODEL_ID`;

ALTER TABLE `MAPPING_AUDIT` 
ADD COLUMN `TEMP_MODEL_IO` MEDIUMBLOB NULL AFTER `STATUS`;

update MAPPING_AUDIT set TEMP_MODEL_IO=model_io_data ;

ALTER TABLE `MAPPING_AUDIT` 
DROP COLUMN `MODEL_IO_DATA`;


ALTER TABLE `MAPPING_AUDIT` 
CHANGE COLUMN `TEMP_MODEL_IO` `MODEL_IO_DATA` MEDIUMBLOB;


ALTER TABLE `MAPPING_AUDIT` 
CHANGE COLUMN `MODEL_IO_DATA` `MODEL_IO_DATA` MEDIUMBLOB NULL DEFAULT NULL AFTER `MODEL_ID`;



ALTER TABLE MAPPING_INPUT 
ADD COLUMN `TEMP_MODEL_IO` MEDIUMBLOB NULL ;

update MAPPING_INPUT set TEMP_MODEL_IO=MAPPING_DATA ;

ALTER TABLE MAPPING_INPUT 
DROP COLUMN `MAPPING_DATA`;


ALTER TABLE MAPPING_INPUT 
CHANGE COLUMN `TEMP_MODEL_IO` `MAPPING_DATA` MEDIUMBLOB;


ALTER TABLE MAPPING_INPUT 
CHANGE COLUMN `MAPPING_DATA` `MAPPING_DATA` MEDIUMBLOB NULL DEFAULT NULL AFTER `MAPPING_ID`;



ALTER TABLE MAPPING_INPUT_AUDIT
ADD COLUMN `TEMP_MODEL_IO` MEDIUMBLOB NULL ;

update MAPPING_INPUT_AUDIT set TEMP_MODEL_IO=MAPPING_DATA ;

ALTER TABLE MAPPING_INPUT_AUDIT 
DROP COLUMN `MAPPING_DATA`;


ALTER TABLE MAPPING_INPUT_AUDIT 
CHANGE COLUMN `TEMP_MODEL_IO` `MAPPING_DATA` MEDIUMBLOB;


ALTER TABLE MAPPING_INPUT_AUDIT 
CHANGE COLUMN `MAPPING_DATA` `MAPPING_DATA` MEDIUMBLOB NULL DEFAULT NULL AFTER `MAPPING_ID`;



ALTER TABLE MAPPING_OUTPUT
ADD COLUMN `TEMP_MODEL_IO` MEDIUMBLOB NULL ;

update MAPPING_OUTPUT set TEMP_MODEL_IO=MAPPING_DATA ;

ALTER TABLE MAPPING_OUTPUT
DROP COLUMN `MAPPING_DATA`;


ALTER TABLE MAPPING_OUTPUT
CHANGE COLUMN `TEMP_MODEL_IO` `MAPPING_DATA` MEDIUMBLOB;


ALTER TABLE MAPPING_OUTPUT
CHANGE COLUMN `MAPPING_DATA` `MAPPING_DATA` MEDIUMBLOB NULL DEFAULT NULL AFTER `MAPPING_ID`;




ALTER TABLE MAPPING_OUTPUT_AUDIT
ADD COLUMN `TEMP_MODEL_IO` MEDIUMBLOB NULL ;

update MAPPING_OUTPUT_AUDIT set TEMP_MODEL_IO=MAPPING_DATA ;

ALTER TABLE MAPPING_OUTPUT_AUDIT
DROP COLUMN `MAPPING_DATA`;


ALTER TABLE MAPPING_OUTPUT_AUDIT
CHANGE COLUMN `TEMP_MODEL_IO` `MAPPING_DATA` MEDIUMBLOB;


ALTER TABLE MAPPING_OUTPUT_AUDIT
CHANGE COLUMN `MAPPING_DATA` `MAPPING_DATA` MEDIUMBLOB NULL DEFAULT NULL AFTER `MAPPING_ID`;

-- UMG-BlobToMblobTIDDefinition.sql

-- MAPPING_INPUT

ALTER TABLE `MAPPING_INPUT` 
ADD COLUMN `TENANT_TEMP_IO` MEDIUMBLOB NULL AFTER `TENANT_INTF_SYS_DEFINITION`;

update MAPPING_INPUT set TENANT_TEMP_IO=TENANT_INTERFACE_DEFINITION ;

ALTER TABLE `MAPPING_INPUT` 
DROP COLUMN `TENANT_INTERFACE_DEFINITION`;

ALTER TABLE `MAPPING_INPUT` 
CHANGE COLUMN `TENANT_TEMP_IO` `TENANT_INTERFACE_DEFINITION` MEDIUMBLOB NULL DEFAULT NULL ;

ALTER TABLE `MAPPING_INPUT` 
CHANGE COLUMN `TENANT_INTERFACE_DEFINITION` `TENANT_INTERFACE_DEFINITION` MEDIUMBLOB NULL DEFAULT NULL AFTER `MAPPING_DATA`;

--
ALTER TABLE `MAPPING_INPUT` 
ADD COLUMN `TENANT_TEMP_SYS_DEF` MEDIUMBLOB NULL AFTER `TENANT_INTF_SYS_DEFINITION`;

update MAPPING_INPUT set TENANT_TEMP_SYS_DEF=TENANT_INTF_SYS_DEFINITION ;

ALTER TABLE MAPPING_INPUT
DROP COLUMN `TENANT_INTF_SYS_DEFINITION`;

ALTER TABLE `MAPPING_INPUT` 
CHANGE COLUMN `TENANT_TEMP_SYS_DEF` `TENANT_INTF_SYS_DEFINITION` MEDIUMBLOB NULL DEFAULT NULL ;

-- MAPPING_INPUT_AUDIT

ALTER TABLE `MAPPING_INPUT_AUDIT` 
ADD COLUMN `TENANT_TEMP_IO` MEDIUMBLOB NULL AFTER `TENANT_INTERFACE_DEFINITION`;

update MAPPING_INPUT_AUDIT set TENANT_TEMP_IO=TENANT_INTERFACE_DEFINITION ;

ALTER TABLE `MAPPING_INPUT_AUDIT` 
DROP COLUMN `TENANT_INTERFACE_DEFINITION`;

ALTER TABLE `MAPPING_INPUT_AUDIT` 
CHANGE COLUMN `TENANT_TEMP_IO` `TENANT_INTERFACE_DEFINITION` MEDIUMBLOB NULL DEFAULT NULL ;


--

ALTER TABLE `MAPPING_INPUT_AUDIT` 
ADD COLUMN `TENANT_TEMP_SYS_DEF` MEDIUMBLOB NULL AFTER `TENANT_INTF_SYS_DEFINITION`;

update MAPPING_INPUT_AUDIT set TENANT_TEMP_SYS_DEF=TENANT_INTF_SYS_DEFINITION ;

ALTER TABLE `MAPPING_INPUT_AUDIT` 
DROP COLUMN `TENANT_INTF_SYS_DEFINITION`;

ALTER TABLE `MAPPING_INPUT_AUDIT` 
CHANGE COLUMN `TENANT_TEMP_SYS_DEF` `TENANT_INTF_SYS_DEFINITION` MEDIUMBLOB NULL DEFAULT NULL ;


-- MAPPING_OUTPUT

ALTER TABLE `MAPPING_OUTPUT` 
ADD COLUMN `TENANT_TEMP_IO` MEDIUMBLOB NULL AFTER `TENANT_INTERFACE_DEFINITION`;


update MAPPING_OUTPUT set TENANT_TEMP_IO=TENANT_INTERFACE_DEFINITION ;


ALTER TABLE `MAPPING_OUTPUT` 
DROP COLUMN `TENANT_INTERFACE_DEFINITION`;

ALTER TABLE `MAPPING_OUTPUT` 
CHANGE COLUMN `TENANT_TEMP_IO` `TENANT_INTERFACE_DEFINITION` MEDIUMBLOB NULL DEFAULT NULL ;


-- MAPPING_OUTPUT_AUDIT


ALTER TABLE `MAPPING_OUTPUT_AUDIT` 
ADD COLUMN `TENANT_TEMP_IO` MEDIUMBLOB NULL AFTER `TENANT_INTERFACE_DEFINITION`;


update MAPPING_OUTPUT_AUDIT set TENANT_TEMP_IO=TENANT_INTERFACE_DEFINITION ;


ALTER TABLE `MAPPING_OUTPUT_AUDIT` 
DROP COLUMN `TENANT_INTERFACE_DEFINITION`;

ALTER TABLE `MAPPING_OUTPUT_AUDIT` 
CHANGE COLUMN `TENANT_TEMP_IO` `TENANT_INTERFACE_DEFINITION` MEDIUMBLOB NULL DEFAULT NULL ;

-- UMG-RunTimeTransactionBlob.sql
ALTER TABLE `UMG_RUNTIME_TRANSACTION`
                CHANGE COLUMN `ERROR_DESCRIPTION` `ERROR_DESCRIPTION` VARCHAR(1500) NULL DEFAULT NULL COMMENT 'Error description while Model execution.' COLLATE 'utf8_bin' AFTER `ERROR_CODE`;
                
ALTER TABLE `UMG_RUNTIME_TRANSACTION_AUDIT`
                CHANGE COLUMN `ERROR_DESCRIPTION` `ERROR_DESCRIPTION` VARCHAR(1500) NULL DEFAULT NULL COMMENT 'Error description while Model execution.' COLLATE 'utf8_bin' AFTER `ERROR_CODE`;

				
-- UMG_RUNTIME_TRANSACTION

ALTER TABLE `UMG_RUNTIME_TRANSACTION` 
ADD COLUMN `TEMP_TENANT_INPUT` MEDIUMBLOB NULL AFTER `TENANT_INPUT`;


update UMG_RUNTIME_TRANSACTION set TEMP_TENANT_INPUT=TENANT_INPUT ;


ALTER TABLE `UMG_RUNTIME_TRANSACTION` 
DROP COLUMN `TENANT_INPUT`;

ALTER TABLE `UMG_RUNTIME_TRANSACTION` 
CHANGE COLUMN `TEMP_TENANT_INPUT` `TENANT_INPUT` MEDIUMBLOB NULL DEFAULT NULL ;

ALTER TABLE `UMG_RUNTIME_TRANSACTION` 
CHANGE COLUMN `TENANT_INPUT` `TENANT_INPUT` MEDIUMBLOB NOT NULL ;

--
ALTER TABLE `UMG_RUNTIME_TRANSACTION` 
ADD COLUMN `TEMP_MODEL_INPUT` MEDIUMBLOB NULL AFTER `MODEL_INPUT`;

update UMG_RUNTIME_TRANSACTION set TEMP_MODEL_INPUT=MODEL_INPUT ;

ALTER TABLE `UMG_RUNTIME_TRANSACTION` 
DROP COLUMN `MODEL_INPUT`;

ALTER TABLE `UMG_RUNTIME_TRANSACTION` 
CHANGE COLUMN `TEMP_MODEL_INPUT` `MODEL_INPUT` MEDIUMBLOB NOT NULL ;

-- UMG_RUNTIME_TRANSACTION_AUDIT

ALTER TABLE `UMG_RUNTIME_TRANSACTION_AUDIT` 
ADD COLUMN `TEMP_TENANT_INPUT` MEDIUMBLOB NULL AFTER `TENANT_INPUT`;


update UMG_RUNTIME_TRANSACTION_AUDIT set TEMP_TENANT_INPUT=TENANT_INPUT ;


ALTER TABLE `UMG_RUNTIME_TRANSACTION_AUDIT` 
DROP COLUMN `TENANT_INPUT`;

ALTER TABLE `UMG_RUNTIME_TRANSACTION_AUDIT` 
CHANGE COLUMN `TEMP_TENANT_INPUT` `TENANT_INPUT` MEDIUMBLOB NULL DEFAULT NULL ;

ALTER TABLE `UMG_RUNTIME_TRANSACTION_AUDIT` 
CHANGE COLUMN `TENANT_INPUT` `TENANT_INPUT` MEDIUMBLOB NOT NULL ;

--
ALTER TABLE `UMG_RUNTIME_TRANSACTION_AUDIT` 
ADD COLUMN `TEMP_MODEL_INPUT` MEDIUMBLOB NULL AFTER `MODEL_INPUT`;

update UMG_RUNTIME_TRANSACTION_AUDIT set TEMP_MODEL_INPUT=MODEL_INPUT ;

ALTER TABLE `UMG_RUNTIME_TRANSACTION_AUDIT` 
DROP COLUMN `MODEL_INPUT`;

ALTER TABLE `UMG_RUNTIME_TRANSACTION_AUDIT` 
CHANGE COLUMN `TEMP_MODEL_INPUT` `MODEL_INPUT` MEDIUMBLOB NOT NULL ;




























