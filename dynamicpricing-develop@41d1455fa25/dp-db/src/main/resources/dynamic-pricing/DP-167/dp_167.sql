

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


DROP TABLE IF EXISTS `DP_SOP_WEEKN_PARAMS_ORIGINAL`;
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



DROP TABLE IF EXISTS `DP_SOP_WEEKN_PARAMS`;
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