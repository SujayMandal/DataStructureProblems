USE `umg_admin`;


INSERT INTO TENANT(ID,NAME,DESCRIPTION,CODE,TENANT_TYPE,AUTH_TOKEN,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON)
VALUES
('84fe0bdd-5045-4625-9904-ad102f52ab78','Hubzu','Hubzu','hubzu','both','0WsffjPAFFw0KoyqT0u862TidCJEj0lg7u+um+QV6ExKnEgOuxlm6yckMo/Y1fOa','SYSTEM',1439969318,'SYSTEM',1439969318);


SELECT @tenant_name:=(SELECT ID FROM TENANT WHERE CODE='hubzu');
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
SELECT @system_key_db_url_val:='jdbc:mysql://10.52.90.142:3306/ocwen';
SELECT @system_key_db_schema_val:='ocwen';
SELECT @system_key_db_user_val:='umgprod';
SELECT @system_key_db_password_val:='Prod#2014';
SELECT @system_key_tenant_url_val:='http://10.52.90.133';
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
('adfe99db-30cb-4674-80ff-0d7622eb4186',@tenant_name,@system_key_db_driver,@system_key_db_driver_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('6151ee29-a078-4eeb-a02f-263b80d10904',@tenant_name,@system_key_db_url,@system_key_db_url_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('1b1f3f88-d693-4c8c-8cf5-54bc2bd7beee',@tenant_name,@system_key_db_schema,@system_key_db_schema_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('e55565a3-bd99-4b3e-8839-ce0389b983cc',@tenant_name,@system_key_db_user,@system_key_db_user_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('508e685c-558c-4b65-b101-58fe8ca1b17f',@tenant_name,@system_key_db_password,@system_key_db_password_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('a71c7ab8-1c72-4fd7-8a94-63fd4265d2db',@tenant_name,@system_key_tenant_url,@system_key_tenant_url_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('f38edb5a-2b39-46b8-8b42-58b985ebba17',@tenant_name,@system_key_tenant_some,@system_key_tenant_some_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('28c1e2f9-2cd3-403f-8487-d439d9e38b16',@tenant_name,@system_key_db_max_conn_age,@system_key_db_max_conn_age_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('0c1b43ff-f846-47ea-88b1-2408baf8acd0',@tenant_name,@system_key_db_def_auto_commit,@system_key_db_def_auto_commit_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('f299b248-c419-4e2c-b032-fe7dbaeae5fc',@tenant_name,@system_key_db_conn_timeout,@system_key_db_conn_timeout_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('5de4595c-d153-4d76-a174-733e72c72d77',@tenant_name,@system_key_tenant_batch_enabled,@system_key_tenant_batch_enabled_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('b13b61bf-7e21-49c4-893f-e606a6f00034',@tenant_name,@system_key_tenant_wrapper_ftp,@system_key_tenant_wrapper_ftp_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('17276ed7-4f38-4d7b-ba33-40336d74d10b',@tenant_name,@system_key_tenant_wrapper_ftp_host,@system_key_tenant_wrapper_ftp_host_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('ad320520-cd53-42cf-985e-69d9246e0009',@tenant_name,@system_key_tenant_wrapper_ftp_op_folder,@system_key_tenant_wrapper_ftp_op_folder_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('04914a97-a3ac-464e-bd67-60757ccf59d1',@tenant_name,@system_key_tenant_wrapper_ftp_in_folder,@system_key_tenant_wrapper_ftp_in_folder_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('a8a68107-b130-408a-84a5-3f9b929f432d',@tenant_name,@system_key_tenant_wrapper_ftp_arch_folder,@system_key_tenant_wrapper_ftp_arch_folder_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('5e4eedce-d6c8-4952-abc7-d31585e126e8',@tenant_name,@system_key_tenant_wrapper_ftp_password,@system_key_tenant_wrapper_ftp_password_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('189cf00f-ba91-45a6-b1ae-ff123325fe88',@tenant_name,@system_key_tenant_wrapper_ftp_username,@system_key_tenant_wrapper_ftp_username_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('dacc5239-90b9-4bf3-a917-e1388db2cbdc',@tenant_name,@system_key_tenant_wrapper_ftp_port,@system_key_tenant_wrapper_ftp_port_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('c05306df-844d-4f0c-bc9a-e8becf76ac1d',@tenant_name,@system_key_tenant_wrapper_ftp_err_folder,@system_key_tenant_wrapper_ftp_err_folder_val,'SYSTEM',1439969318,'SYSTEM',1439969318);



INSERT INTO `ADDRESS` (`ID`, `TENANT_ID`, `ADDRESS_1`, `ADDRESS_2`, `CITY`, `STATE`, `ZIP`, `COUNTRY`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
VALUES ('c02ee5a3-5557-4f78-b43e-3a1e4d21e629', @tenant_name, 'hubzu-address', 'address2', 'boston', 'ny', 'zip', 'US', 'SYSTEM', 1439969318, 'SYSTEM', 1439969318);

commit;