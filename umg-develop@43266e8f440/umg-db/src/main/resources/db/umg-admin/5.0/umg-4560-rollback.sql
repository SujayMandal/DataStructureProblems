use umg_admin;

DELETE FROM COMMAND where ID in ('A37E5EFF-9CBF-4FC0-8F25-B71C5318FB22', 'A37E5EFF-9CBF-4FC0-8F25-B71C5318FB23', 'A37E5EFF-9CBF-4FC0-8F25-B71C5318FB24');

UPDATE COMMAND SET EXECUTION_SEQUENCE='10' WHERE NAME = 'generateTestInput';
UPDATE COMMAND SET EXECUTION_SEQUENCE='11' WHERE NAME = 'testVersion';
UPDATE COMMAND SET EXECUTION_SEQUENCE='12' WHERE NAME = 'deleteRModelArtifacts';

commit;