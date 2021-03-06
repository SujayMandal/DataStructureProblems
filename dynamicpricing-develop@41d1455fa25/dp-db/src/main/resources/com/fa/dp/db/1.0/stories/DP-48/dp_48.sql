

ALTER TABLE `DP_WEEKN_PARAMS`
	ADD COLUMN `FETCHED_DATE` BIGINT(20) NOT NULL AFTER `UPDATE_TIMESTAMP`,
	ADD COLUMN `FAILED_STEP_COMMAND_ID` CHAR(36) NULL AFTER `FETCHED_DATE`,
	ADD CONSTRAINT `FK_DP_WEEKN_PARAMS_COMMAND` FOREIGN KEY (`FAILED_STEP_COMMAND_ID`) REFERENCES `COMMAND` (`ID`);