USE umg_admin;

SELECT @tenant_ocwen:=(SELECT ID FROM TENANT WHERE CODE='equator');

SELECT @system_key_ftp_enable:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='TENANT_WRAPPER' AND SYSTEM_KEY='FTP');
SELECT @system_key_ftp_host:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='FTP_BATCH_WRAPPER' AND SYSTEM_KEY='FTP_HOST');
SELECT @system_key_ftp_output_folder:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='FTP_BATCH_WRAPPER' AND SYSTEM_KEY='FTP_OUTPUT_FOLDER');
SELECT @system_key_ftp_input_folder:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='FTP_BATCH_WRAPPER' AND SYSTEM_KEY='FTP_INPUT_FOLDER');
SELECT @system_key_ftp_archive_folder:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='FTP_BATCH_WRAPPER' AND SYSTEM_KEY='FTP_ARCHIVE_FOLDER');
SELECT @system_key_ftp_password:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='FTP_BATCH_WRAPPER' AND SYSTEM_KEY='FTP_PASSWORD');
SELECT @system_key_ftp_user_name:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='FTP_BATCH_WRAPPER' AND SYSTEM_KEY='FTP_USER_NAME');
SELECT @system_key_ftp_error_folder:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='FTP_BATCH_WRAPPER' AND SYSTEM_KEY='FTP_ERROR_FOLDER');
SELECT @system_key_ftp_port:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='FTP_BATCH_WRAPPER' AND SYSTEM_KEY='FTP_PORT');

SELECT @system_key_conn_timeout:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='connectionTimeout');
SELECT @system_key_max_conn_age:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='maxConnectionAge');
SELECT @system_key_default_auto_commit:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='defaultAutoCommit');

SELECT @system_key_conn_timeout_val:='10000';
SELECT @system_key_max_conn_age_vale:='0';
SELECT @system_key_default_auto_commit_value:='true';

SELECT @system_key_ftp_enable_val:='false';
SELECT @system_key_ftp_host_val:='';
SELECT @system_key_ftp_output_folder_val:='output';
SELECT @system_key_ftp_input_folder_val:='input';
SELECT @system_key_ftp_archive_folder_val:='archive';
SELECT @system_key_ftp_password_val:='';
SELECT @system_key_ftp_user_name_val:='';
SELECT @system_key_ftp_error_folder_val:='error';
SELECT @system_key_ftp_port_val:='221';

INSERT INTO TENANT_CONFIG(ID,TENANT_ID,SYSTEM_KEY_ID,CONFIG_VALUE,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON)
VALUES
('0bb02250-8ed4-4d14-8e4c-8ec1cf58f2ec',@tenant_ocwen,@system_key_ftp_enable,@system_key_ftp_enable_val,'SYSTEM',1401344421,'SYSTEM',1401344421),
('c8fcd1a2-5e1b-405d-ad75-e17c2812ecb3',@tenant_ocwen,@system_key_ftp_host,@system_key_ftp_host_val,'SYSTEM',1401344421,'SYSTEM',1401344421),
('b4071d69-9c91-4dbb-a2a1-1b581d704085',@tenant_ocwen,@system_key_ftp_output_folder,@system_key_ftp_output_folder_val,'SYSTEM',1401344421,'SYSTEM',1401344421),
('72bc0134-54f0-48dc-be25-3c66b8aa7190',@tenant_ocwen,@system_key_ftp_input_folder,@system_key_ftp_input_folder_val,'SYSTEM',1401344421,'SYSTEM',1401344421),
('0b835a44-45b5-44b7-b297-f2ebbdc4c38a',@tenant_ocwen,@system_key_ftp_archive_folder,@system_key_ftp_archive_folder_val,'SYSTEM',1401344421,'SYSTEM',1401344421),
('67d2396f-2532-4a56-b46c-1d6919699bd6',@tenant_ocwen,@system_key_ftp_password,@system_key_ftp_password_val,'SYSTEM',1401344421,'SYSTEM',1401344421),
('89a46f49-b10c-48dd-af96-fc73dbc508c7',@tenant_ocwen,@system_key_ftp_user_name,@system_key_ftp_user_name_val,'SYSTEM',1401344421,'SYSTEM',1401344421),
('aca45361-2b5f-48a1-bd4c-beff4cff4008',@tenant_ocwen,@system_key_ftp_error_folder,@system_key_ftp_error_folder_val,'SYSTEM',1401344421,'SYSTEM',1401344421),
('06523f08-541a-46ad-bebd-f564df31f566',@tenant_ocwen,@system_key_ftp_port,@system_key_ftp_port_val,'SYSTEM',1401344421,'SYSTEM',1401344421);

INSERT INTO `TENANT_CONFIG` (`ID`, `TENANT_ID`, `SYSTEM_KEY_ID`, `CONFIG_VALUE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`, `ROLE`) 
VALUES ('e4ee6a41-a26f-45d4-b233-023b7d9c34b2', @tenant_ocwen, @system_key_conn_timeout, @system_key_conn_timeout_val, 'SYSTEM', 1401344421, 'SYSTEM', 1401344421, NULL);

INSERT INTO `TENANT_CONFIG` (`ID`, `TENANT_ID`, `SYSTEM_KEY_ID`, `CONFIG_VALUE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`, `ROLE`) 
VALUES ('d4d8b268-da66-4fc8-8b66-c3467366e76b', @tenant_ocwen, @system_key_max_conn_age, @system_key_max_conn_age_vale, 'SYSTEM', 1401344421, 'SYSTEM', 1401344421, NULL);

INSERT INTO `TENANT_CONFIG` (`ID`, `TENANT_ID`, `SYSTEM_KEY_ID`, `CONFIG_VALUE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`, `ROLE`) 
VALUES ('d2e9c894-24bd-4313-8dab-a8276bdc124a', @tenant_ocwen, @system_key_default_auto_commit, @system_key_default_auto_commit_value, 'SYSTEM', 1401344421, 'SYSTEM', 1401344421, NULL);


COMMIT;