

ALTER TABLE `DP_WEEKN_PARAMS`
	DROP COLUMN `FETCHED_DATE`,
	DROP COLUMN `FAILED_STEP_COMMAND_ID`,
	DROP FOREIGN KEY `FK_DP_WEEKN_PARAMS_COMMAND`;