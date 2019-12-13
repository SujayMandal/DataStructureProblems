

ALTER TABLE `DP_WEEKN_PARAMS`
	CHANGE COLUMN `DP_WEEKN_PRCS_STATUS_ID` `DP_PROCESS_FILE_ID` CHAR(36) NULL DEFAULT NULL AFTER `ASSET_NUMBER`,
	ADD COLUMN `FETCHED_DATE` BIGINT(20) NOT NULL AFTER `UPDATE_TIMESTAMP`;
	
ALTER TABLE `DP_WEEKN_PRCS_STATUS`
	ALTER `FETCHED_DATE` DROP DEFAULT;
ALTER TABLE `DP_WEEKN_PRCS_STATUS`
	CHANGE COLUMN `FETCHED_DATE` `EXECUTE_TIMESTAMP` VARCHAR(50) NULL AFTER `PROCESS`;
	
DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'INITIAL_HUBZU_QUERY';
DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'HUBZU_QUERY';
DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'SOP_HUBZU_QUERY';
DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'RECENT_STATUS_HUBZU_QUERY';
DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'STAGE5_QUERY';
DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'SS_PMI_HUBZU_QUERY';

UPDATE `COMMAND` SET `EXECUTION_SEQUENCE` = 4 WHERE `NAME` = 'weekNAssignmentFilter';
UPDATE `COMMAND` SET `EXECUTION_SEQUENCE` = 5 WHERE `NAME` = 'weekNZipStateFilter';
UPDATE `COMMAND` SET `EXECUTION_SEQUENCE` = 6 WHERE `NAME` = 'weekNSSPmiFilter';
UPDATE `COMMAND` SET `EXECUTION_SEQUENCE` = 7 WHERE `NAME` = 'weekNSOPFilter';
UPDATE `COMMAND` SET `EXECUTION_SEQUENCE` = 10 WHERE `NAME` = 'weekNRAIntegrarion';
UPDATE `COMMAND` SET `EXECUTION_SEQUENCE` = 11 WHERE `NAME` = 'weekNOutputFileCreate';
UPDATE `COMMAND` SET `EXECUTION_SEQUENCE` = 12 WHERE `NAME` = 'weekNEmailIntegration';

DELETE FROM `COMMAND` WHERE `NAME` = 'weekNFetchData';

INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`) VALUES
(UUID(), 'weekNInitializeData', 'PREPARE_WEEKN_DATA', 1, 'WEEKN', TRUE, 'SYSTEM', UNIX_TIMESTAMP(NOW()));
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`) VALUES
(UUID(), 'weekNRRClassification', 'RR_CLASSIFICATION_DB', 2, 'WEEKN', TRUE, 'SYSTEM', UNIX_TIMESTAMP(NOW()));
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`) VALUES
(UUID(), 'weekNEligibilityFilter', 'ELIGIBILITY_FILTERING', 3, 'WEEKN_OCN', TRUE, 'SYSTEM', UNIX_TIMESTAMP(NOW()));
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`) VALUES
(UUID(), 'weekNEligibilityFilter', 'ELIGIBILITY_FILTERING', 3, 'WEEKN_NRZ', TRUE, 'SYSTEM', UNIX_TIMESTAMP(NOW()));
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`) VALUES
(UUID(), 'weekNHubzuDBCall', 'HUBZU_DB_CALL', 8, 'WEEKN_OCN', TRUE, 'SYSTEM', UNIX_TIMESTAMP(NOW()));
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`) VALUES
(UUID(), 'weekNHubzuDBCall', 'HUBZU_DB_CALL', 8, 'WEEKN_NRZ', TRUE, 'SYSTEM', UNIX_TIMESTAMP(NOW()));
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`) VALUES
(UUID(), 'weekNStage5DBCall', 'STAGE5_DB_CALL', 9, 'WEEKN_OCN', TRUE, 'SYSTEM', UNIX_TIMESTAMP(NOW()));
INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`) VALUES
(UUID(), 'weekNStage5DBCall', 'STAGE5_DB_CALL', 9, 'WEEKN_NRZ', TRUE, 'SYSTEM', UNIX_TIMESTAMP(NOW()));
