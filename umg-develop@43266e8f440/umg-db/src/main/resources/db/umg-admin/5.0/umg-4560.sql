use umg_admin;

INSERT INTO COMMAND (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`) VALUES ('A37E5EFF-9CBF-4FC0-8F25-B71C5318FB22', 'validateReportTemplate', 'Validate Report Template', '13', 'CREATE', 'SYSTEM', '12354856456');
INSERT INTO COMMAND (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`) VALUES ('A37E5EFF-9CBF-4FC0-8F25-B71C5318FB23', 'saveReportTemplate', 'Save Report Template', '14', 'CREATE', 'SYSTEM', '12354856456');
INSERT INTO COMMAND (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`) VALUES ('A37E5EFF-9CBF-4FC0-8F25-B71C5318FB24', 'generateModelReport', 'Generate Report Template (this should be last command in the sequence)', '15', 'CREATE', 'SYSTEM', '12354856456');

UPDATE COMMAND SET EXECUTION_SEQUENCE='10' WHERE NAME = 'validateReportTemplate';
UPDATE COMMAND SET EXECUTION_SEQUENCE='11' WHERE NAME = 'saveReportTemplate';
UPDATE COMMAND SET EXECUTION_SEQUENCE='12' WHERE NAME = 'generateTestInput';
UPDATE COMMAND SET EXECUTION_SEQUENCE='13' WHERE NAME = 'testVersion';
UPDATE COMMAND SET EXECUTION_SEQUENCE='14' WHERE NAME = 'deleteRModelArtifacts';
UPDATE COMMAND SET EXECUTION_SEQUENCE='15' WHERE NAME = 'generateModelReport';

INSERT INTO SYSTEM_PARAMETER (`ID`, `SYS_KEY`, `DESCRIPTION`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('911DD83E-C514-466A-8768-DED5DAADCC29', 'baseReportURL', 'Base Report URL', 'https://ra-rel26-test.altidev.net/umg-api', 'Y', 'system', '1417439330020', 'system', '1417439330020');

COMMIT;