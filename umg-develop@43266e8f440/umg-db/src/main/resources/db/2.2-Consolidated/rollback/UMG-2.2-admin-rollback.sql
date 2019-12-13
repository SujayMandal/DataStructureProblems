use `umg_admin`;

SELECT @tenant_ocwen:=(SELECT ID FROM TENANT WHERE CODE='Ocwen');

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


Delete from TENANT_CONFIG where TENANT_ID = @tenant_ocwen AND SYSTEM_KEY_ID in (@system_key_ftp_enable, @system_key_ftp_host, @system_key_ftp_output_folder, @system_key_ftp_input_folder, @system_key_ftp_archive_folder, @system_key_ftp_password, @system_key_ftp_user_name, @system_key_ftp_error_folder, @system_key_ftp_port,@system_key_conn_timeout,@system_key_max_conn_age,@system_key_default_auto_commit);

SELECT @tenant_equator:=(SELECT ID FROM TENANT WHERE CODE='equator');

Delete from TENANT_CONFIG where TENANT_ID = @tenant_equator AND SYSTEM_KEY_ID in (@system_key_ftp_enable, @system_key_ftp_host, @system_key_ftp_output_folder, @system_key_ftp_input_folder, @system_key_ftp_archive_folder, @system_key_ftp_password, @system_key_ftp_user_name, @system_key_ftp_error_folder, @system_key_ftp_port,@system_key_conn_timeout,@system_key_max_conn_age,@system_key_default_auto_commit);

Delete from SYSTEM_KEY where  ID in (@system_key_ftp_enable, @system_key_ftp_host, @system_key_ftp_output_folder, @system_key_ftp_input_folder, @system_key_ftp_archive_folder, @system_key_ftp_password, @system_key_ftp_user_name, @system_key_ftp_error_folder, @system_key_ftp_port,@system_key_conn_timeout,@system_key_max_conn_age,@system_key_default_auto_commit);

Delete from SYSTEM_PARAMETER where sys_key = 'ftp-deploy-api';
Delete from SYSTEM_PARAMETER where sys_key = 'ftp-undeploy-api';
Delete from SYSTEM_PARAMETER where SYS_KEY = 'umg-admin-schema';
Delete from SYSTEM_PARAMETER where SYS_KEY = 'RECORD_LIMIT';

ALTER TABLE `SYNDICATED_DATA` DROP INDEX `uniqueVersion`;

COMMIT;




