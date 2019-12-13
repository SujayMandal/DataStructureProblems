USE `umg_admin`;

UPDATE SYSTEM_PARAMETER SET SYS_VALUE='RMV' WHERE  sys_key='model-exception-error-code-pattern';

COMMIT;



