
-- COMMAND table rollback

SET @CREATED_BY = 'SYSTEM';

SET @CREATED_ON =  UNIX_TIMESTAMP() * 1000;

INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopWeekNFetchData', 'Data Fetch Failure', 1, 'SOP_WEEKN', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopWeek0DuplicateFilter', 'DUPLICATE_FILTERING', 1, 'SOP_WEEK0_OCN', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopWeek0AssetValueFilter', 'ASSETVALUE_FILTERING', 2, 'SOP_WEEK0_OCN', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopweek0ModeledBenchmarkCriteria', 'RULE_80_20', 3, 'SOP_WEEK0_OCN', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopWeek0DuplicateFilter', 'DUPLICATE_FILTERING', 1, 'SOP_WEEK0_NRZ', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopWeek0AssetValueFilter', 'ASSETVALUE_FILTERING', 2, 'SOP_WEEK0_NRZ', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopweek0ModeledBenchmarkCriteria', 'RULE_80_20', 3, 'SOP_WEEK0_NRZ', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);

INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopWeekNAssignmentFilter', 'Benchmark', 2, 'SOP_WEEKN_OCN', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopWeekNSuccessfulUnderreviewFilter', 'Successful / Underreview', 3, 'SOP_WEEKN_OCN', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopWeekNActiveListingsFilter', 'Active Listing', 4, 'SOP_WEEKN_OCN', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopWeekNPast12CyclesFilter', 'Past 12 Cycles', 5, 'SOP_WEEKN_OCN', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopWeekNOddListingsFilter', 'Odd Listing', 6, 'SOP_WEEKN_OCN', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopWeekNSSPmiFilter', 'SS & PMI', 8, 'SOP_WEEKN_OCN', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopWeekNVacantFilter', 'Vacant', 9, 'SOP_WEEKN_OCN', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopWeekNRAIntegrarion', 'Model Failure', 10, 'SOP_WEEKN_OCN', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopWeekNStateFilter', 'Unsupported State', 7, 'SOP_WEEKN_OCN', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);

INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopWeekNAssignmentFilter', 'Benchmark', 2, 'SOP_WEEKN_NRZ', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopWeekNSuccessfulUnderreviewFilter', 'Successful / Underreview', 3, 'SOP_WEEKN_NRZ', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopWeekNActiveListingsFilter', 'Active Listing', 4, 'SOP_WEEKN_NRZ', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopWeekNPast12CyclesFilter', 'Past 12 Cycles', 5, 'SOP_WEEKN_NRZ', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopWeekNOddListingsFilter', 'Odd Listing', 6, 'SOP_WEEKN_NRZ', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopWeekNSSPmiFilter', 'SS & PMI', 8, 'SOP_WEEKN_NRZ', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopWeekNVacantFilter', 'Vacant', 9, 'SOP_WEEKN_NRZ', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopWeekNRAIntegrarion', 'Model Failure', 10, 'SOP_WEEKN_NRZ', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
	VALUES (UUID(), 'sopWeekNStateFilter', 'Unsupported State', 7, 'SOP_WEEKN_NRZ', 1, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);


-- SOP WEEK0 ROLLBACK

DROP TABLE IF EXISTS `DP_SOP_WEEK0_PARAMS`;

DROP TABLE IF EXISTS `DP_SOP_WEEK0_PRCS_STATUS`;

CREATE TABLE `DP_SOP_WEEK0_PRCS_STATUS` (
	`ID` CHAR(36) NOT NULL DEFAULT '',
	`INPUT_FILE_NAME` VARCHAR(200) NOT NULL DEFAULT '',
	`STATUS` VARCHAR(100) NOT NULL,
	`CREATED_BY` VARCHAR(100) NOT NULL,
	`CREATED_ON` BIGINT(20) NOT NULL,
	`LAST_UPDATED_BY` VARCHAR(100) NULL DEFAULT NULL,
	`LAST_UPDATED_ON` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`ID`)
)ENGINE=InnoDB;

CREATE TABLE `DP_SOP_WEEK0_PARAMS_ORIGINAL` (
	`ID` CHAR(36) NOT NULL,
	`ASSET_NUMBER` VARCHAR(100) NULL DEFAULT NULL,
	`STATE` VARCHAR(2) NULL DEFAULT NULL,
	`PROPERTY_TYPE` VARCHAR(10) NULL DEFAULT NULL,
	`STATUS` VARCHAR(50) NULL DEFAULT NULL,
	`ASSET_VALUE` DECIMAL(10,0) NULL DEFAULT NULL,
	`AV_SET_DATE` VARCHAR(10) NULL DEFAULT NULL,
	`REO_DATE` VARCHAR(10) NOT NULL,
	`LIST_PRICE` DECIMAL(19,2) NULL DEFAULT NULL,
	`CLASSIFICATION` VARCHAR(10) NULL DEFAULT NULL,
	`CREATED_BY` VARCHAR(100) NOT NULL,
	`CREATED_ON` BIGINT(20) NOT NULL,
	`LAST_UPDATED_BY` VARCHAR(100) NULL DEFAULT NULL,
	`LAST_UPDATED_ON` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`ID`)
)ENGINE=InnoDB;

CREATE TABLE `DP_SOP_WEEK0_PARAMS` (
	`ID` CHAR(36) NOT NULL,
	`DP_SOP_WEEK0_FILE_ID` CHAR(36) NOT NULL,
	`ASSET_NUMBER` VARCHAR(100) NOT NULL,
	`STATE` VARCHAR(2) NOT NULL,
	`PROPERTY_TYPE` VARCHAR(10) NOT NULL,
	`STATUS` VARCHAR(50) NOT NULL,
	`ASSET_VALUE` DECIMAL(10,0) NOT NULL,
	`AV_SET_DATE` VARCHAR(10) NOT NULL,
	`REO_DATE` VARCHAR(10) NOT NULL,
	`LIST_PRICE` DECIMAL(10,0) NOT NULL,
	`CLASSIFICATION` VARCHAR(10) NOT NULL,
	`ELIGIBLE` VARCHAR(100) NULL DEFAULT NULL,
	`NOTES` VARCHAR(500) NULL DEFAULT NULL,
	`ERROR_DETAIL` VARCHAR(1000) NULL DEFAULT NULL,
	`SOP_WEEK0_ORIGINAL_ID` CHAR(36) NULL DEFAULT NULL,
	`FAILED_STEP_COMMAND_ID` CHAR(36) NULL DEFAULT NULL,
	`CREATED_BY` VARCHAR(100) NOT NULL,
	`CREATED_ON` BIGINT(20) NOT NULL,
	`LAST_UPDATED_BY` VARCHAR(100) NULL DEFAULT NULL,
	`LAST_UPDATED_ON` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`ID`),
	CONSTRAINT `DP_SOP_INPUT_FILE_ID_FK` FOREIGN KEY (`DP_SOP_WEEK0_FILE_ID`) REFERENCES `DP_SOP_WEEK0_PRCS_STATUS` (`ID`),
	CONSTRAINT `FAILED_STEP_COMMAND_ID_FK` FOREIGN KEY (`FAILED_STEP_COMMAND_ID`) REFERENCES `COMMAND` (`ID`),
	CONSTRAINT `SOP_WEEK0_ID_FK` FOREIGN KEY (`SOP_WEEK0_ORIGINAL_ID`) REFERENCES `DP_SOP_WEEK0_PARAMS_ORIGINAL` (`ID`)
)ENGINE=InnoDB;


ALTER TABLE `DP_SOP_WEEK0_PARAMS`
	ADD COLUMN `ASSIGNMENT` VARCHAR(100) NULL AFTER `CLASSIFICATION`;
	
ALTER TABLE `DP_SOP_WEEK0_PARAMS`
	ADD COLUMN `ASSIGNMENT_DATE` BIGINT(20) NULL DEFAULT NULL AFTER `ASSIGNMENT`;
	
ALTER TABLE `DP_SOP_WEEK0_PARAMS`
	ADD COLUMN `UPLOAD_FLAG` CHAR(15) NULL AFTER `DP_SOP_WEEK0_FILE_ID`;
	
ALTER TABLE `DP_SOP_WEEK0_PARAMS_ORIGINAL`
	ADD COLUMN `ASSIGNMENT` VARCHAR(100) NULL DEFAULT NULL AFTER `CLASSIFICATION`;
	

-- SOP WEEKN ROLLBACK

DROP TABLE IF EXISTS `DP_SOP_WEEKN_PARAMS`;

DROP TABLE IF EXISTS `DP_SOP_WEEKN_PRCS_STATUS`;

CREATE TABLE `DP_SOP_WEEKN_PRCS_STATUS` (
	`ID` CHAR(36) NOT NULL DEFAULT '',
	`INPUT_FILE_NAME` VARCHAR(200) NOT NULL DEFAULT '',
	`STATUS` VARCHAR(100) NOT NULL,
	`FETCHED_DATE` BIGINT(20) NULL DEFAULT NULL,
	`EMAIL_TIMESTAMP` VARCHAR(50) NULL DEFAULT NULL,
	`TO_LIST` VARCHAR(500) NULL DEFAULT NULL,
	`CC_LIST` VARCHAR(500) NULL DEFAULT NULL,
	`CREATED_BY` VARCHAR(100) NOT NULL,
	`CREATED_ON` BIGINT(20) NOT NULL,
	`LAST_UPDATED_BY` VARCHAR(100) NULL DEFAULT NULL,
	`LAST_UPDATED_ON` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`ID`)
)ENGINE=InnoDB;

CREATE TABLE `DP_SOP_WEEKN_PARAMS_ORIGINAL` (
	`ID` CHAR(36) NOT NULL,
	`ASSET_NUMBER` VARCHAR(100) NOT NULL,
	`RBID_PROP_ID_VC_PK` VARCHAR(50) NULL DEFAULT NULL,
	`CLASSIFICATION` VARCHAR(50) NULL DEFAULT NULL,
	`STATE` VARCHAR(2) NULL DEFAULT NULL,
	`ZIPCODE` VARCHAR(20) NULL DEFAULT NULL,
	`CLIENT_CODE` VARCHAR(20) NULL DEFAULT NULL,
	`PRIVATE_MORTGAGE_INSURANCE` VARCHAR(20) NULL DEFAULT NULL,
	`VACANT` VARCHAR(20) NULL DEFAULT NULL,	
	`CREATED_BY` VARCHAR(100) NOT NULL,
	`CREATED_ON` BIGINT(20) NOT NULL,
	`LAST_UPDATED_BY` VARCHAR(100) NULL DEFAULT NULL,
	`LAST_UPDATED_ON` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`ID`)
)ENGINE=InnoDB;

CREATE TABLE `DP_SOP_WEEKN_PARAMS` (
  `ID` char(36) NOT NULL,
  `ASSET_NUMBER` varchar(100) NOT NULL,
  `DP_SOP_WEEKN_FILE_ID` char(36) DEFAULT NULL,
  `CLASSIFICATION` varchar(50) DEFAULT NULL,
  `STATE` varchar(2) DEFAULT NULL,
  `ZIPCODE` VARCHAR(20) NULL DEFAULT NULL,
  `CLIENT_CODE` VARCHAR(20) NULL DEFAULT NULL,
  `PRIVATE_MORTGAGE_INSURANCE` VARCHAR(20) NULL DEFAULT NULL,
  `VACANT` VARCHAR(20) NULL DEFAULT NULL,
  `ELIGIBLE` VARCHAR(15) NULL,
  `ASSIGNMENT` VARCHAR(15) NULL,
  `EXCLUSION_REASON` VARCHAR(100) NULL,
  `RBID_PROP_ID_VC_PK` VARCHAR(50) NULL DEFAULT NULL,
  `LIST_PRCE_NT` DECIMAL(20,0) NULL DEFAULT NULL,
  `LIST_STRT_DATE_DT_NN` VARCHAR(50) NULL DEFAULT NULL,
  `LIST_END_DATE_DT_NN` VARCHAR(50) NULL DEFAULT NULL,
  `LIST_STTS_DTLS_VC` VARCHAR(50) NULL DEFAULT NULL,
  `DATE_OF_LAST_REDUCTION` VARCHAR(50) NULL DEFAULT NULL,
  `MOST_RECENT_LIST_END_DATE` varchar(50) DEFAULT NULL,
  `MOST_RECENT_LIST_STATUS` varchar(50) DEFAULT NULL,
  `MOST_RECENT_PROPERTY_STATUS` varchar(50) DEFAULT NULL,
  `MOST_RECENT_LIST_PRICE` decimal(20,0) DEFAULT NULL,
  `LP_PERCENT_ADJUSTMENT_REC` decimal(10,0) DEFAULT NULL,
  `LP_DOLLAR_ADJUSTMENT_REC` decimal(10,0) DEFAULT NULL,
  `MODEL_VERSION` varchar(50) DEFAULT NULL,
  `DELIVERY_DATE` varchar(50) DEFAULT NULL,
  `UPDATE_TIMESTAMP` varchar(50) DEFAULT NULL,
  `SOP_WEEKN_ID` CHAR(36) NULL DEFAULT NULL,
  `FAILED_STEP_COMMAND_ID` CHAR(36) NULL DEFAULT NULL,
  `CREATED_BY` varchar(100) NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(100) DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  CONSTRAINT `DP_SOP_WEEKN_FILE_ID_FK` FOREIGN KEY (`DP_SOP_WEEKN_FILE_ID`) REFERENCES `DP_SOP_WEEKN_PRCS_STATUS` (`ID`),
  CONSTRAINT `FK_SOP_WEEKN_PARAMS_COMMAND` FOREIGN KEY (`FAILED_STEP_COMMAND_ID`) REFERENCES `COMMAND` (`ID`),
  CONSTRAINT `SOP_WEEKN_ID_FK` FOREIGN KEY (`SOP_WEEKN_ID`) REFERENCES `DP_SOP_WEEKN_PARAMS_ORIGINAL` (`ID`)
)ENGINE=InnoDB;
	
CREATE TABLE IF NOT EXISTS `DP_SOP_WEEKN_INTG_AUDITS` (
  `ID` char(36) NOT NULL,
  `EVENT_TYPE` varchar(100) DEFAULT NULL,
  `DP_SOP_WEEKN_FILE_ID` char(36) DEFAULT NULL,
  `RECORD_ID` char(36) NOT NULL,
  `STATUS` varchar(100) DEFAULT NULL,
  `ERROR_DESCRIPTION` varchar(100) DEFAULT NULL,
  `START_TIME` bigint(20) DEFAULT NULL,
  `END_TIME` bigint(20) DEFAULT NULL,
  `CREATED_BY` varchar(100) DEFAULT NULL,
  `CREATED_ON` bigint(20) DEFAULT NULL,
  `LAST_UPDATED_BY` varchar(100) DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `DP_SOP_WEEKN_PARAMS_FK` (`RECORD_ID`),
  CONSTRAINT `DP_SOP_WEEKN_PARAMS_FK` FOREIGN KEY (`RECORD_ID`) REFERENCES `DP_SOP_WEEKN_PARAMS` (`ID`)
)ENGINE=InnoDB;

ALTER TABLE `DP_SOP_WEEKN_PRCS_STATUS`
	ADD COLUMN `OCN_OUTPUT_FILE_NAME` VARCHAR(500) NOT NULL AFTER `CC_LIST`,
	ADD COLUMN `NRZ_OUTPUT_FILE_NAME` VARCHAR(500) NOT NULL AFTER `OCN_OUTPUT_FILE_NAME`;
	
ALTER TABLE `DP_SOP_WEEKN_PARAMS`
	ADD COLUMN `SELLER_OCCUPIED_PROPERTY` VARCHAR(50) NULL DEFAULT NULL AFTER `RBID_PROP_ID_VC_PK`;
	
