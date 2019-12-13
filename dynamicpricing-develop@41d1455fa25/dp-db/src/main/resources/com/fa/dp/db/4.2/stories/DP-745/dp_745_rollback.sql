DELETE FROM `RA_TNT_SYSTEM_PARAMETERS` WHERE `SYS_KEY` ='qa.sop.daily.report.schedule.status';

DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'sop.weekn.qa.report.to.list';

DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'sop.weekN.dp.email.qa.report.file.subject';

DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'sop.weekn.qa.report.from';

DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'sop.weekn.qa.report.cc.list';

DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'sop.weekN.qa.report.email.body';

DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'sop.weekN.qa.report.error.email.body';

DELETE FROM `COMMAND` WHERE `NAME`='weekNEmailIntegrationSOP';

SET @PROCESS = 'SOP_QA_REPORT';

DELETE FROM `COMMAND` WHERE `PROCESS` = @PROCESS AND `NAME` = 'sopQaReportFetchData';

DELETE FROM `COMMAND` WHERE `PROCESS` = @PROCESS AND `NAME` = 'sopQaReportAssignmentFilter';

DELETE FROM `COMMAND` WHERE `PROCESS` = @PROCESS AND `NAME` = 'sopQaReportPast12CyclesFilter';

DELETE FROM `COMMAND` WHERE `PROCESS` = @PROCESS AND `NAME` = 'sopQaReportStateFilter';

DELETE FROM `COMMAND` WHERE `PROCESS` = @PROCESS AND `NAME` = 'sopQaReportSSPmiFilter';
