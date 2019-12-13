use `umg_admin`;

INSERT INTO SYSTEM_KEY (`ID`, `SYSTEM_KEY`, `KEY_TYPE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
VALUES ('05be454a-e6f9-11e3-a68a-82687f4fcx15', 'maxIdleTime', 'DATABASE', 'SYSTEM', '1401344421', 'SYSTEM', '1401344421');

INSERT INTO `TENANT` (`ID`, `NAME`, `DESCRIPTION`, `CODE`, `TENANT_TYPE`, `AUTH_TOKEN`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
VALUES 
('a1f46295-ef62-11e5-9106-00ffbc73cbd1', 'Rent range', 'Rent Range tenant', 'rentrange', 'both', '7KGTVz6sDfQpxqDGa3oS29eRRNW9uHfXQaI3HDvj3+EBMPFS+zHvEExJB5acubC7', 'SYSTEM', 1401344421, 'SYSTEM', 1401344421);

SELECT @tenant_name:=(SELECT ID FROM TENANT WHERE CODE='rentrange');

SELECT @system_key_db_driver:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='DRIVER');
SELECT @system_key_db_url:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='URL');
SELECT @system_key_db_schema:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='SCHEMA');
SELECT @system_key_db_user:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='USER');
SELECT @system_key_db_password:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='PASSWORD');
SELECT @system_key_db_max_conn_age:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='maxConnectionAge');
SELECT @system_key_db_def_auto_commit:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='defaultAutoCommit');
SELECT @system_key_db_conn_timeout:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='connectionTimeout');
SELECT @system_key_maxIdleTime:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='maxIdleTime');
SELECT @system_key_minPoolSize:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='minPoolSize');
SELECT @system_key_maxPoolSize:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='maxPoolSize');
SELECT @system_key_tenant_url:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='TENANT' AND SYSTEM_KEY='URL');
SELECT @system_key_tenant_batch_enabled:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='TENANT' AND SYSTEM_KEY='BATCH_ENABLED');
SELECT @system_key_tenant_wrapper_ftp:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='TENANT_WRAPPER' AND SYSTEM_KEY='FTP');
SELECT @system_key_tenant_wrapper_ftp_host:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='FTP_BATCH_WRAPPER' AND SYSTEM_KEY='FTP_HOST');
SELECT @system_key_tenant_wrapper_ftp_op_folder:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='FTP_BATCH_WRAPPER' AND SYSTEM_KEY='FTP_OUTPUT_FOLDER');
SELECT @system_key_tenant_wrapper_ftp_in_folder:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='FTP_BATCH_WRAPPER' AND SYSTEM_KEY='FTP_INPUT_FOLDER');
SELECT @system_key_tenant_wrapper_ftp_arch_folder:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='FTP_BATCH_WRAPPER' AND SYSTEM_KEY='FTP_ARCHIVE_FOLDER');
SELECT @system_key_tenant_wrapper_ftp_password:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='FTP_BATCH_WRAPPER' AND SYSTEM_KEY='FTP_PASSWORD');
SELECT @system_key_tenant_wrapper_ftp_username:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='FTP_BATCH_WRAPPER' AND SYSTEM_KEY='FTP_USER_NAME');
SELECT @system_key_tenant_wrapper_ftp_port:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='FTP_BATCH_WRAPPER' AND SYSTEM_KEY='FTP_PORT');
SELECT @system_key_tenant_wrapper_ftp_err_folder:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='FTP_BATCH_WRAPPER' AND SYSTEM_KEY='FTP_ERROR_FOLDER');

SELECT @system_key_db_driver_val:='com.mysql.jdbc.Driver';
SELECT @system_key_db_url_val:='jdbc:mysql://10.52.82.138:3306/Ocwen';
SELECT @system_key_db_schema_val:='Ocwen';
SELECT @system_key_db_user_val:='umguat';
SELECT @system_key_db_password_val:='Uat#2014';

			
SELECT @system_key_db_max_conn_age_val:='0';			
SELECT @system_key_db_def_auto_commit_val:='true';			
SELECT @system_key_db_conn_timeout_val:='10000';	
SELECT @system_key_maxIdleTime_val:='28200';
SELECT @system_key_minPoolSize_val:='3';
SELECT @system_key_maxPoolSize_val:='20';

SELECT @system_key_tenant_url_val:='http://10.52.82.134:8080';
SELECT @system_key_tenant_some_val:='test';

SELECT @system_key_tenant_batch_enabled_val:='true';			
SELECT @system_key_tenant_wrapper_ftp_val:='true';			
SELECT @system_key_tenant_wrapper_ftp_host_val:='';		
SELECT @system_key_tenant_wrapper_ftp_op_folder_val:='ouput';		
SELECT @system_key_tenant_wrapper_ftp_in_folder_val:='input';		
SELECT @system_key_tenant_wrapper_ftp_arch_folder_val:='archive';	
SELECT @system_key_tenant_wrapper_ftp_password_val:='';		
SELECT @system_key_tenant_wrapper_ftp_username_val:='';		
SELECT @system_key_tenant_wrapper_ftp_port_val:='';		
SELECT @system_key_tenant_wrapper_ftp_err_folder_val:='error';



delete from TENANT_CONFIG where TENANT_ID=@tenant_name;

INSERT INTO TENANT_CONFIG(ID,TENANT_ID,SYSTEM_KEY_ID,CONFIG_VALUE,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON)
VALUES
('2ac83c1d-ef6a-11e5-9106-00ffbc73cbd1',@tenant_name,@system_key_db_driver,@system_key_db_driver_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('938ce5d7-ef6a-11e5-9106-00ffbc73cbd1',@tenant_name,@system_key_db_url,@system_key_db_url_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('af39ee58-ef6a-11e5-9106-00ffbc73cbd1',@tenant_name,@system_key_db_schema,@system_key_db_schema_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('c811236b-ef6a-11e5-9106-00ffbc73cbd1',@tenant_name,@system_key_db_user,@system_key_db_user_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('cf93e263-ef6a-11e5-9106-00ffbc73cbd1',@tenant_name,@system_key_db_password,@system_key_db_password_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('e2c862b8-ef6a-11e5-9106-00ffbc73cbd1',@tenant_name,@system_key_db_max_conn_age,@system_key_db_max_conn_age_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('f2770e3e-ef6a-11e5-9106-00ffbc73cbd1',@tenant_name,@system_key_db_def_auto_commit,@system_key_db_def_auto_commit_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('040a6df2-ef6b-11e5-9106-00ffbc73cbd1',@tenant_name,@system_key_db_conn_timeout,@system_key_db_conn_timeout_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('15c9aefd-ef6b-11e5-9106-00ffbc73cbd1',@tenant_name,@system_key_maxIdleTime,@system_key_maxIdleTime_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('9a705bc0-ef6b-11e5-9106-00ffbc73cbd1',@tenant_name,@system_key_minPoolSize,@system_key_minPoolSize_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('a2e07862-ef6b-11e5-9106-00ffbc73cbd1',@tenant_name,@system_key_maxPoolSize,@system_key_maxPoolSize_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('bb15f6f0-ef6b-11e5-9106-00ffbc73cbd1',@tenant_name,@system_key_tenant_url,@system_key_tenant_url_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('c7d931df-ef6b-11e5-9106-00ffbc73cbd1',@tenant_name,@system_key_tenant_batch_enabled,@system_key_tenant_batch_enabled_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('486bc5fe-ef6c-11e5-9106-00ffbc73cbd1',@tenant_name,@system_key_tenant_wrapper_ftp,@system_key_tenant_wrapper_ftp_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('4c9cdfbc-ef6c-11e5-9106-00ffbc73cbd1',@tenant_name,@system_key_tenant_wrapper_ftp_host,@system_key_tenant_wrapper_ftp_host_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('53843a05-ef6c-11e5-9106-00ffbc73cbd1',@tenant_name,@system_key_tenant_wrapper_ftp_op_folder,@system_key_tenant_wrapper_ftp_op_folder_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('56cd4bf3-ef6c-11e5-9106-00ffbc73cbd1',@tenant_name,@system_key_tenant_wrapper_ftp_in_folder,@system_key_tenant_wrapper_ftp_in_folder_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('5a6fa311-ef6c-11e5-9106-00ffbc73cbd1',@tenant_name,@system_key_tenant_wrapper_ftp_arch_folder,@system_key_tenant_wrapper_ftp_arch_folder_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('67b5ac57-ef6c-11e5-9106-00ffbc73cbd1',@tenant_name,@system_key_tenant_wrapper_ftp_password,@system_key_tenant_wrapper_ftp_password_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('6b6072d0-ef6c-11e5-9106-00ffbc73cbd1',@tenant_name,@system_key_tenant_wrapper_ftp_username,@system_key_tenant_wrapper_ftp_username_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('6f1e5c2c-ef6c-11e5-9106-00ffbc73cbd1',@tenant_name,@system_key_tenant_wrapper_ftp_port,@system_key_tenant_wrapper_ftp_port_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('72853f17-ef6c-11e5-9106-00ffbc73cbd1',@tenant_name,@system_key_tenant_wrapper_ftp_err_folder,@system_key_tenant_wrapper_ftp_err_folder_val,'SYSTEM',1439969318,'SYSTEM',1439969318);

INSERT INTO `ADDRESS` (`ID`, `TENANT_ID`, `ADDRESS_1`, `ADDRESS_2`, `CITY`, `STATE`, `ZIP`, `COUNTRY`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
VALUES ('7af986e5-ef6c-11e5-9106-00ffbc73cbd1', @tenant_name, 'rentrange-address', 'address2', 'boston', 'ny', 'zip', 'US', 'SYSTEM', 1439969318, 'SYSTEM', 1439969318);

commit;



