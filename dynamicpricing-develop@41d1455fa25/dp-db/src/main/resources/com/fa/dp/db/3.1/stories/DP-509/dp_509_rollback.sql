
ALTER TABLE `WEEKN_DAILY_QA_REPORT`
	CHANGE COLUMN `PREVIOUS_LIST_START_DATE` `PREVIOUS_LIST_STRT_DATE` VARCHAR(55) NULL DEFAULT NULL AFTER `LIST_TYPE_ID_VC_FK`,
	CHANGE COLUMN `PREVIOUS_LIST_END_DATE` `PREVIOUS_LIST_END_DATE` VARCHAR(55) NULL DEFAULT NULL AFTER `PREVIOUS_LIST_STRT_DATE`,
	CHANGE COLUMN `CURRENT_LIST_START_DATE` `CURRENT_LIST_STRT_DATE` VARCHAR(55) NULL DEFAULT NULL AFTER `PREVIOUS_LIST_PRICE`,
	CHANGE COLUMN `CURRENT_LIST_END_DATE` `CURRENT_LIST_END_DATE` VARCHAR(55) NULL DEFAULT NULL AFTER `CURRENT_LIST_STRT_DATE`;
	
ALTER TABLE `WEEKN_DAILY_QA_REPORT`
	DROP COLUMN `OLD_PROP_ID`;
	
ALTER TABLE `WEEKN_DAILY_QA_REPORT`
	DROP COLUMN `OLD_LOAN_NUMBER`;