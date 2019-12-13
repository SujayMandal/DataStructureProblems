USE `umg_admin`;


INSERT INTO TENANT(ID,NAME,DESCRIPTION,CODE,TENANT_TYPE,AUTH_TOKEN,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON)
VALUES
('e3e1398d-935b-11e5-9070-00ffde411c75','realtrans','realtrans','realtrans','both','8Xc3JX4oQgWElderNIx7EWpeiYl5eOoG8T7+eIm4GoSJxbB4FFJgxVx1ba8jj/O7','SYSTEM',1439969318,'SYSTEM',1439969318);


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
SELECT @system_key_db_url_val:='jdbc:mysql://10.52.90.142:3306/Ocwen';
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
('523bbb93-c7de-43a4-9cd6-e267d7feba61',@tenant_name,@system_key_db_driver,@system_key_db_driver_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('b8b7da46-8214-44aa-a453-88295c84478d',@tenant_name,@system_key_db_url,@system_key_db_url_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('65151901-bead-4e86-9872-a1562599ffbd',@tenant_name,@system_key_db_schema,@system_key_db_schema_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('1b625e5f-bef1-4389-a40f-f020f3ac62e5',@tenant_name,@system_key_db_user,@system_key_db_user_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('acbc5f80-60e0-4852-b7b4-a75ab8069025',@tenant_name,@system_key_db_password,@system_key_db_password_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('4d9e434b-497a-4781-9399-b62e2444f78b',@tenant_name,@system_key_tenant_url,@system_key_tenant_url_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('8d6ba30f-c769-4286-9d08-234ba8cd4046',@tenant_name,@system_key_tenant_some,@system_key_tenant_some_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('d57e8d48-ddbd-443e-918d-5d12c06909bb',@tenant_name,@system_key_db_max_conn_age,@system_key_db_max_conn_age_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('6af7610a-166d-4369-946a-78d46d03a1a3',@tenant_name,@system_key_db_def_auto_commit,@system_key_db_def_auto_commit_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('3f78f858-8395-4587-a81c-959cebfd1d4a',@tenant_name,@system_key_db_conn_timeout,@system_key_db_conn_timeout_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('630bbbd3-ec80-40ab-aaa9-24d3c2dcb58c',@tenant_name,@system_key_tenant_batch_enabled,@system_key_tenant_batch_enabled_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('55dc08b2-9493-4cd3-80fe-879346461483',@tenant_name,@system_key_tenant_wrapper_ftp,@system_key_tenant_wrapper_ftp_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('40070d2a-3d49-4a76-a17a-c33d0187e7d9',@tenant_name,@system_key_tenant_wrapper_ftp_host,@system_key_tenant_wrapper_ftp_host_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('d5cb363c-7c96-4d57-b139-e82942c60358',@tenant_name,@system_key_tenant_wrapper_ftp_op_folder,@system_key_tenant_wrapper_ftp_op_folder_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('5adc0c8a-5cf7-4241-aa9c-c0f5d5501645',@tenant_name,@system_key_tenant_wrapper_ftp_in_folder,@system_key_tenant_wrapper_ftp_in_folder_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('5d4c2014-cc20-451d-82f2-1294bf78db48',@tenant_name,@system_key_tenant_wrapper_ftp_arch_folder,@system_key_tenant_wrapper_ftp_arch_folder_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('b4519594-8c37-4eec-8e57-9be1fdec8035',@tenant_name,@system_key_tenant_wrapper_ftp_password,@system_key_tenant_wrapper_ftp_password_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('8ee8bfd9-3d3b-434a-8de5-6db141ba1999',@tenant_name,@system_key_tenant_wrapper_ftp_username,@system_key_tenant_wrapper_ftp_username_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('8aa8cacf-2f17-447a-898e-ba47614ea7bd',@tenant_name,@system_key_tenant_wrapper_ftp_port,@system_key_tenant_wrapper_ftp_port_val,'SYSTEM',1439969318,'SYSTEM',1439969318),
('18896f82-80fc-44dd-9e00-082430e90504',@tenant_name,@system_key_tenant_wrapper_ftp_err_folder,@system_key_tenant_wrapper_ftp_err_folder_val,'SYSTEM',1439969318,'SYSTEM',1439969318);


INSERT INTO `ADDRESS` (`ID`, `TENANT_ID`, `ADDRESS_1`, `ADDRESS_2`, `CITY`, `STATE`, `ZIP`, `COUNTRY`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb401', @tenant_name, 'realtrans-address', 'address2', 'boston', 'ny', 'zip', 'US', 'SYSTEM', 1439969318, 'SYSTEM', 1439969318);

commit;