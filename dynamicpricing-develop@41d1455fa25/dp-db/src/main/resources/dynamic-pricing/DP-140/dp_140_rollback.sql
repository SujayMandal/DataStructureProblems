


UPDATE `COMMAND` SET `ACTIVE`=1 WHERE `NAME`='weekNPast12CyclesFilter' AND `PROCESS`='WEEKN_NRZ';

ALTER TABLE `DP_WEEKN_PRCS_STATUS`
	CHANGE COLUMN `INPUT_FILE_NAME` `INPUT_FILE_NAME` VARCHAR(50) NOT NULL DEFAULT '' AFTER `ID`;


