

ALTER TABLE `DP_WEEK0_PARAMS`
	CHANGE COLUMN `ASSIGNMENT_DATE` `ASSIGNMENT_DATE` VARCHAR(10) NULL DEFAULT NULL AFTER `PROPERTY_TYPE`;
	
ALTER TABLE `DP_WEEK0_PARAMS_ORIGINAL`
	CHANGE COLUMN `ASSIGNMENT_DATE` `ASSIGNMENT_DATE` VARCHAR(255) NULL DEFAULT NULL AFTER `ASSIGNMENT`;
	
ALTER TABLE `DP_WEEKN_PARAMS`
	CHANGE COLUMN `DELIVERY_DATE` `DELIVERY_DATE` VARCHAR(50) NULL DEFAULT NULL AFTER `MODEL_VERSION`;
	
ALTER TABLE `DP_WEEKN_PARAMS_ORIGINAL`
	CHANGE COLUMN `DELIVERY_DATE` `DELIVERY_DATE` VARCHAR(50) NULL DEFAULT NULL AFTER `MODEL_VERSION`;
