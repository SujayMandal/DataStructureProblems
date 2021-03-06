USE umg_admin;

ALTER TABLE MODEL_EXECUTION_ENVIRONMENTS DROP COLUMN TENANT_ID;

ALTER TABLE MODEL_EXECUTION_ENVIRONMENTS ADD COLUMN `NAME` VARCHAR(50) NOT NULL COMMENT 'Languga name and version' COLLATE 'utf8_bin';

UPDATE MODEL_EXECUTION_ENVIRONMENTS SET NAME='Matlab-7.16' where EXECUTION_ENVIRONMENT='Matlab' and ENVIRONMENT_VERSION='7.16';

UPDATE MODEL_EXECUTION_ENVIRONMENTS SET NAME='R-3.1.2' where EXECUTION_ENVIRONMENT='R' and ENVIRONMENT_VERSION='3.1.2';

ALTER TABLE MODEL_EXEC_PACKAGES DROP COLUMN  TENANT_ID;

ALTER TABLE MODEL_EXEC_PACKAGES DROP COLUMN  MODEL_EXEC_ENV_ID;

ALTER TABLE MODEL_EXEC_PACKAGES ADD COLUMN MODEL_EXEC_ENV_NAME VARCHAR(50) NOT NULL COMMENT 'Languga name and version' COLLATE 'utf8_bin';

ALTER TABLE MODEL_EXEC_PACKAGES_AUDIT DROP COLUMN  TENANT_ID;

ALTER TABLE MODEL_EXEC_PACKAGES_AUDIT DROP COLUMN  MODEL_EXEC_ENV_ID;

ALTER TABLE MODEL_EXEC_PACKAGES_AUDIT ADD COLUMN MODEL_EXEC_ENV_NAME VARCHAR(50) NOT NULL COMMENT 'Languga name and version' COLLATE 'utf8_bin';

UPDATE MODEL_EXEC_PACKAGES SET MODEL_EXEC_ENV_NAME='R-3.1.2'

UPDATE MODEL_EXEC_PACKAGES_AUDIT SET MODEL_EXEC_ENV_NAME='R-3.1.2'

commit;