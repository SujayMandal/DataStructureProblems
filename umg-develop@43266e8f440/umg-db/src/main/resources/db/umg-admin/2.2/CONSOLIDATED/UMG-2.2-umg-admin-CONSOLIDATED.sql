-- USE umg_admin;

INSERT INTO SYSTEM_KEY (`ID`, `SYSTEM_KEY`, `KEY_TYPE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('92F88437-8F5F-4FC7-81CE-B0F88137A4B4','FTP','TENANT_WRAPPER','SYSTEM',1401344421,'SYSTEM',1401344421);
INSERT INTO SYSTEM_KEY (`ID`, `SYSTEM_KEY`, `KEY_TYPE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('1A2F140D-B8C1-4CE7-9857-8092C5964653', 'FTP_HOST', 'FTP_BATCH_WRAPPER', 'SYSTEM', 1401344421, 'SYSTEM', 1401344421);
INSERT INTO SYSTEM_KEY (`ID`, `SYSTEM_KEY`, `KEY_TYPE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('1A2F140D-B8C1-4CE7-9857-8092C8512653', 'FTP_OUTPUT_FOLDER', 'FTP_BATCH_WRAPPER', 'SYSTEM', 1401344421, 'SYSTEM', 1401344421);
INSERT INTO SYSTEM_KEY (`ID`, `SYSTEM_KEY`, `KEY_TYPE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('6EDF46E9-288D-4787-B993-3B5A3B2C2019', 'FTP_INPUT_FOLDER', 'FTP_BATCH_WRAPPER', 'SYSTEM', 1401344421, 'SYSTEM', 1401344421);
INSERT INTO SYSTEM_KEY (`ID`, `SYSTEM_KEY`, `KEY_TYPE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('C96144C4-21FA-462E-B9A6-4E1A23E95365', 'FTP_ARCHIVE_FOLDER', 'FTP_BATCH_WRAPPER', 'SYSTEM', 1401344421, 'SYSTEM', 1401344421);
INSERT INTO SYSTEM_KEY (`ID`, `SYSTEM_KEY`, `KEY_TYPE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('C96144C4-21FA-462E-B9A6-4E1A23E98659', 'FTP_PASSWORD', 'FTP_BATCH_WRAPPER', 'SYSTEM', 1401344421, 'SYSTEM', 1401344421);
INSERT INTO SYSTEM_KEY (`ID`, `SYSTEM_KEY`, `KEY_TYPE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('F6F8B3E8-9F5B-42EA-A679-852DFC0C86FB', 'FTP_USER_NAME', 'FTP_BATCH_WRAPPER', 'SYSTEM', 1401344421, 'SYSTEM', 1401344421);
INSERT INTO SYSTEM_KEY (`ID`, `SYSTEM_KEY`, `KEY_TYPE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('F6F8B3E8-9F5B-42EA-A679-925DFC0C86FB', 'FTP_ERROR_FOLDER', 'FTP_BATCH_WRAPPER', 'SYSTEM', 1401344421, 'SYSTEM', 1401344421);
INSERT INTO SYSTEM_KEY (`ID`, `SYSTEM_KEY`, `KEY_TYPE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('F6F8B3E8-9F5B-42EA-A680-925DFC0C86FB', 'FTP_PORT', 'FTP_BATCH_WRAPPER', 'SYSTEM', 1401344421, 'SYSTEM', 1401344421);

INSERT INTO SYSTEM_PARAMETER(id,sys_key,sys_value,is_active,created_by,created_on,last_updated_by,last_updated_on) VALUES('523bd356-f69b-49a1-92d5-171945dd16dd','ftp-deploy-api','/api/batch/ftp/deploy','Y','system',1415354329490,'system',1415354329490);
INSERT INTO SYSTEM_PARAMETER(id,sys_key,sys_value,is_active,created_by,created_on,last_updated_by,last_updated_on) VALUES('9bc66734-403f-4adb-b37b-a42756f0eb7c','ftp-undeploy-api','/api/batch/ftp/undeploy','Y','system',1415354329490,'system',1415354329490);

ALTER TABLE SYNDICATED_DATA ADD CONSTRAINT uniqueVersion UNIQUE(CONTAINER_NAME, VERSION_ID);