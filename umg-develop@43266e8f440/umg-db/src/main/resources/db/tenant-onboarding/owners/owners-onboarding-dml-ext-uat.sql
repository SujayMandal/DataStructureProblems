USE `umg_admin`;


INSERT INTO TENANT(ID,NAME,DESCRIPTION,CODE,TENANT_TYPE,AUTH_TOKEN,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON)
VALUES
('1081d3c2-3c6b-11e6-b0ae-00ffde411c75','owners','owners','owners','both','5n5ojNHcCxiD2gvuvP40D5n2fy7dwaIJacgXV2osb9nIosMtk6L/0Ij1hoUgJUe1','SYSTEM',1439969318,'SYSTEM',1439969318);


SELECT @tenant_name:=(SELECT ID FROM TENANT WHERE CODE='realtrans');
SELECT @system_key_db_driver:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='DRIVER');
SELECT @system_key_db_url:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='URL');
SELECT @system_key_db_schema:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='SCHEMA');
SELECT @system_key_db_user:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='USER');
SELECT @system_key_db_password:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='PASSWORD');
SELECT @system_key_db_max_conn_age:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='maxConnectionAge');
SELECT @system_key_db_def_auto_commit:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='defaultAutoCommit');
SELECT @system_key_db_conn_timeout:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='connectionTimeout');

SELECT @system_key_tenant_url:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='TENANT' AND SYSTEM_KEY='URL');
SELECT @system_key_tenant_some:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='TENANT' AND SYSTEM_KEY='SOME');
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
SELECT @system_key_db_url_val:='jdbc:mysql://10.52.82.208:3306/Ocwen';
SELECT @system_key_db_schema_val:='Ocwen';
SELECT @system_key_db_user_val:='umguat';
SELECT @system_key_db_password_val:='uat#2014';
SELECT @system_key_tenant_url_val:='http://10.52.82.198:8080';
SELECT @system_key_tenant_some_val:='test';

			
SELECT @system_key_db_max_conn_age_val:='0';			
SELECT @system_key_db_def_auto_commit_val:='true';			
SELECT @system_key_db_conn_timeout_val:='10000';	


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
('71b401c6-3c58-11e6-b0ae-00ffde411c75',@tenant_name,@system_key_db_driver,@system_key_db_driver_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('7c0a156a-3c58-11e6-b0ae-00ffde411c75',@tenant_name,@system_key_db_url,@system_key_db_url_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('87f76a2f-3c58-11e6-b0ae-00ffde411c75',@tenant_name,@system_key_db_schema,@system_key_db_schema_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('8e2e6674-3c58-11e6-b0ae-00ffde411c75',@tenant_name,@system_key_db_user,@system_key_db_user_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('94250933-3c58-11e6-b0ae-00ffde411c75',@tenant_name,@system_key_db_password,@system_key_db_password_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('9ad8c8c7-3c58-11e6-b0ae-00ffde411c75',@tenant_name,@system_key_tenant_url,@system_key_tenant_url_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('a11db267-3c58-11e6-b0ae-00ffde411c75',@tenant_name,@system_key_tenant_some,@system_key_tenant_some_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('a6ce5a03-3c58-11e6-b0ae-00ffde411c75',@tenant_name,@system_key_db_max_conn_age,@system_key_db_max_conn_age_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('b1e8579f-3c58-11e6-b0ae-00ffde411c75',@tenant_name,@system_key_db_def_auto_commit,@system_key_db_def_auto_commit_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('b9da7a55-3c58-11e6-b0ae-00ffde411c75',@tenant_name,@system_key_db_conn_timeout,@system_key_db_conn_timeout_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('c0635bc6-3c58-11e6-b0ae-00ffde411c75',@tenant_name,@system_key_tenant_batch_enabled,@system_key_tenant_batch_enabled_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('5fa2513c-3c59-11e6-b0ae-00ffde411c75',@tenant_name,@system_key_tenant_wrapper_ftp,@system_key_tenant_wrapper_ftp_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('678a9666-3c59-11e6-b0ae-00ffde411c75',@tenant_name,@system_key_tenant_wrapper_ftp_host,@system_key_tenant_wrapper_ftp_host_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('6d4dff89-3c59-11e6-b0ae-00ffde411c75',@tenant_name,@system_key_tenant_wrapper_ftp_op_folder,@system_key_tenant_wrapper_ftp_op_folder_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('73339ae7-3c59-11e6-b0ae-00ffde411c75',@tenant_name,@system_key_tenant_wrapper_ftp_in_folder,@system_key_tenant_wrapper_ftp_in_folder_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('7921df2d-3c59-11e6-b0ae-00ffde411c75',@tenant_name,@system_key_tenant_wrapper_ftp_arch_folder,@system_key_tenant_wrapper_ftp_arch_folder_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('8026b08e-3c59-11e6-b0ae-00ffde411c75',@tenant_name,@system_key_tenant_wrapper_ftp_password,@system_key_tenant_wrapper_ftp_password_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('87e5273b-3c59-11e6-b0ae-00ffde411c75',@tenant_name,@system_key_tenant_wrapper_ftp_username,@system_key_tenant_wrapper_ftp_username_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('8d9fbf24-3c59-11e6-b0ae-00ffde411c75',@tenant_name,@system_key_tenant_wrapper_ftp_port,@system_key_tenant_wrapper_ftp_port_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('93ee99f8-3c59-11e6-b0ae-00ffde411c75',@tenant_name,@system_key_tenant_wrapper_ftp_err_folder,@system_key_tenant_wrapper_ftp_err_folder_val,'SYSTEM',1439969318,'SYSTEM',1439969318);


INSERT INTO `ADDRESS` (`ID`, `TENANT_ID`, `ADDRESS_1`, `ADDRESS_2`, `CITY`, `STATE`, `ZIP`, `COUNTRY`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
VALUES ('0853a25d-3c58-11e6-b0ae-00ffde411c75', @tenant_name, 'owners-address', 'address2', 'boston', 'ny', 'zip', 'US', 'SYSTEM', 1439969318, 'SYSTEM', 1439969318);

commit;