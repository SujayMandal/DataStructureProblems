DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'sop.weekn.success.underreview.top.rows.hubzu.query';
DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'sop.weekn.success.underreview.all.rows.hubzu.query';

ALTER TABLE `DP_SOP_WEEKN_PARAMS`
	DROP COLUMN `INITIAL_VALUATION`,
	DROP COLUMN `LP_DOLLAR_ADJUSTMENT_REC`,
	DROP COLUMN `MODEL_VERSION`,
	DROP COLUMN `CLIENT_CODE`,
	DROP COLUMN `LP_PERCENT_ADJUSTMENT_REC`;