use `Ocwen`;

ALTER TABLE UMG_RUNTIME_TRANSACTION	 DROP COLUMN `MODEL_EXEC_ENV_NAME`;

ALTER TABLE UMG_RUNTIME_TRANSACTION DROP COLUMN `R_SERVE_PORT`;

commit;
