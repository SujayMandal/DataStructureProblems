ALTER TABLE `DP_WEEKN_PARAMS` DROP COLUMN `PRIOR_RECOMMENDED`;

ALTER TABLE `DP_WEEKN_PARAMS_ORIGINAL` DROP COLUMN `PRIOR_RECOMMENDED`;

DELETE FROM `RA_TNT_SYSTEM_PARAMETERS` WHERE `SYS_KEY` = 'weekn.days.before.selected.date';