use ocwen;

UPDATE `MEDIATE_MODEL_LIBRARY` SET `MODEL_EXEC_ENV_NAME`='R-3.2.1';
UPDATE `MEDIATE_MODEL_LIBRARY_AUDIT` SET `MODEL_EXEC_ENV_NAME`='R-3.2.1';
UPDATE `MODEL_LIBRARY` SET `MODEL_EXEC_ENV_NAME`='R-3.2.1' WHERE EXECUTION_LANGUAGE='R';
UPDATE `MODEL_LIBRARY_AUDIT` SET `MODEL_EXEC_ENV_NAME`='R-3.2.1' WHERE EXECUTION_LANGUAGE='R';

commit;
