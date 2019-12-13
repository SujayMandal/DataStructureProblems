

UPDATE `COMMAND` SET `EXECUTION_SEQUENCE`= 3  WHERE  `NAME`='weekNPast12CyclesFilter';
UPDATE `COMMAND` SET `EXECUTION_SEQUENCE`= 4  WHERE  `NAME`='weekNOddListingsFilter';
UPDATE `COMMAND` SET `EXECUTION_SEQUENCE`= 5 WHERE  `NAME`='weekNAssignmentFilter';
UPDATE `COMMAND` SET `EXECUTION_SEQUENCE`= 6 WHERE  `NAME`='weekNZipStateFilter';
UPDATE `COMMAND` SET `EXECUTION_SEQUENCE`= 7 WHERE  `NAME`='weekNSSPmiFilter';
UPDATE `COMMAND` SET `EXECUTION_SEQUENCE`= 8 WHERE  `NAME`='weekNSOPFilter';
UPDATE `COMMAND` SET `EXECUTION_SEQUENCE`= 9 WHERE  `NAME`='weekNRAIntegrarion';
UPDATE `COMMAND` SET `EXECUTION_SEQUENCE`= 10 WHERE  `NAME`='weekNOutputFileCreate';
UPDATE `COMMAND` SET `EXECUTION_SEQUENCE`= 11 WHERE  `NAME`='weekNEmailIntegration';

INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES 
(UUID(), 'weekNActiveListingsFilter', 'ACTIVE_LISTINGS_FILTERING', 2, 'WEEKN_OCN', TRUE, 'SYSTEM', UNIX_TIMESTAMP(NOW()), NULL, NULL),
(UUID(), 'weekNActiveListingsFilter', 'ACTIVE_LISTINGS_FILTERING', 2, 'WEEKN_NRZ', TRUE, 'SYSTEM', UNIX_TIMESTAMP(NOW()), NULL, NULL);