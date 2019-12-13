
-- Rollback to Newly Added Column into DYNAMIC_PRICING_FILE_PRCS_STATUS  Table for PHH Loan Output File --
ALTER TABLE `DYNAMIC_PRICING_FILE_PRCS_STATUS`	DROP COLUMN `PHH_OUTPUT_FILE_NAME`;

DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'phh.minimum.asset.value';
DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'phh.maximum.asset.value';
DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'phh.dp.auth.token';
DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'phh.pricemode.input';
DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'phh.week0Vacant.minor.version';
DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'phh.week0Vacant.major.version';
DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'phh.week0Vacant.tenant.code';
DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'phh.week0Vacant.model.name';

DELETE FROM `COMMAND` WHERE `PROCESS` = 'WEEK0_PHH';























