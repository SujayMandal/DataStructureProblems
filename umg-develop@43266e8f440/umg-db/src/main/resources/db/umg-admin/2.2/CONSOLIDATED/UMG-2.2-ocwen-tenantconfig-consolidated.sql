-- USE umg_admin;

SELECT @tenant_ocwen:=(SELECT ID FROM TENANT WHERE CODE='ocwen');

SELECT @system_key_ftp_enable:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='TENANT_WRAPPER' AND SYSTEM_KEY='FTP');
SELECT @system_key_ftp_host:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='FTP_BATCH_WRAPPER' AND SYSTEM_KEY='FTP_HOST');
SELECT @system_key_ftp_output_folder:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='FTP_BATCH_WRAPPER' AND SYSTEM_KEY='FTP_OUTPUT_FOLDER');
SELECT @system_key_ftp_input_folder:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='FTP_BATCH_WRAPPER' AND SYSTEM_KEY='FTP_INPUT_FOLDER');
SELECT @system_key_ftp_archive_folder:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='FTP_BATCH_WRAPPER' AND SYSTEM_KEY='FTP_ARCHIVE_FOLDER');
SELECT @system_key_ftp_password:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='FTP_BATCH_WRAPPER' AND SYSTEM_KEY='FTP_PASSWORD');
SELECT @system_key_ftp_user_name:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='FTP_BATCH_WRAPPER' AND SYSTEM_KEY='FTP_USER_NAME');
SELECT @system_key_ftp_error_folder:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='FTP_BATCH_WRAPPER' AND SYSTEM_KEY='FTP_ERROR_FOLDER');
SELECT @system_key_ftp_port:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='FTP_BATCH_WRAPPER' AND SYSTEM_KEY='FTP_PORT');

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
('0329ef2f-a1b3-4998-97b6-c2501888f728',@tenant_ocwen,@system_key_ftp_enable,@system_key_ftp_enable_val,'SYSTEM',1401344421,'SYSTEM',1401344421),
('755fbf30-c749-42ba-bb6b-7d1f353fd443',@tenant_ocwen,@system_key_ftp_host,@system_key_ftp_host_val,'SYSTEM',1401344421,'SYSTEM',1401344421),
('7e0bb3f7-92ca-42d0-980f-558143813b8f',@tenant_ocwen,@system_key_ftp_output_folder,@system_key_ftp_output_folder_val,'SYSTEM',1401344421,'SYSTEM',1401344421),
('a66e9423-96bd-4178-b640-74ccf13011d5',@tenant_ocwen,@system_key_ftp_input_folder,@system_key_ftp_input_folder_val,'SYSTEM',1401344421,'SYSTEM',1401344421),
('f9fda5e5-4956-4cff-93e1-b31579ae3c5d',@tenant_ocwen,@system_key_ftp_archive_folder,@system_key_ftp_archive_folder_val,'SYSTEM',1401344421,'SYSTEM',1401344421),
('5b66168e-cce7-42d1-9002-c6097f7c78be',@tenant_ocwen,@system_key_ftp_password,@system_key_ftp_password_val,'SYSTEM',1401344421,'SYSTEM',1401344421),
('92c59d89-d285-4d8b-9bae-49e986079aac',@tenant_ocwen,@system_key_ftp_user_name,@system_key_ftp_user_name_val,'SYSTEM',1401344421,'SYSTEM',1401344421),
('216151f0-0ffb-4679-9b69-ec0b1417b6bf',@tenant_ocwen,@system_key_ftp_error_folder,@system_key_ftp_error_folder_val,'SYSTEM',1401344421,'SYSTEM',1401344421),
('4a4b27c7-4b14-44da-9c3b-4f77687e13a4',@tenant_ocwen,@system_key_ftp_port,@system_key_ftp_port_val,'SYSTEM',1401344421,'SYSTEM',1401344421);

COMMIT;