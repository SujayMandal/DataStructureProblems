

ALTER TABLE `DP_WEEK0_PARAMS`
	DROP COLUMN `ASSIGNMENT_DATE`;

	
ALTER TABLE `DP_WEEK0_PARAMS`
	ADD COLUMN `ASSIGNMENT_DATE` BIGINT(20) NULL DEFAULT NULL AFTER `PROPERTY_TYPE`;
	

ALTER TABLE `DP_WEEK0_PARAMS_ORIGINAL`
	DROP COLUMN `ASSIGNMENT_DATE`;
	
ALTER TABLE `DP_WEEK0_PARAMS_ORIGINAL`
	ADD COLUMN `ASSIGNMENT_DATE` BIGINT(20) NULL DEFAULT NULL AFTER `ASSIGNMENT`;
	
ALTER TABLE `DP_WEEKN_PARAMS`
	DROP COLUMN `DELIVERY_DATE`;

ALTER TABLE `DP_WEEKN_PARAMS`
	ADD COLUMN `DELIVERY_DATE` BIGINT(20) NULL DEFAULT NULL AFTER `MODEL_VERSION`;

ALTER TABLE `DP_WEEKN_PARAMS_ORIGINAL`
	DROP COLUMN `DELIVERY_DATE`;
	
ALTER TABLE `DP_WEEKN_PARAMS_ORIGINAL`
	ADD COLUMN `DELIVERY_DATE` BIGINT(20) NULL DEFAULT NULL AFTER `MODEL_VERSION`;
	
