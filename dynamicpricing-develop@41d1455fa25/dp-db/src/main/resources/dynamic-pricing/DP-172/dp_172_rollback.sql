

UPDATE `COMMAND` SET `ACTIVE`=FALSE WHERE `NAME`='weekNAssignmentFilter' and `PROCESS`='WEEKN_NRZ';

UPDATE `COMMAND` SET `EXECUTION_SEQUENCE`= 2 WHERE  `NAME`='weekNActiveListingsFilter';
UPDATE `COMMAND` SET `EXECUTION_SEQUENCE`= 3 WHERE  `NAME`='weekNPast12CyclesFilter';
UPDATE `COMMAND` SET `EXECUTION_SEQUENCE`= 4 WHERE  `NAME`='weekNOddListingsFilter';
UPDATE `COMMAND` SET `EXECUTION_SEQUENCE`= 5 WHERE  `NAME`='weekNAssignmentFilter';