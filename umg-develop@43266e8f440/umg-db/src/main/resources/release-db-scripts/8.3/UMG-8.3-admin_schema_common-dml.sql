USE umg_admin;

INSERT INTO SYSTEM_PARAMETER
	(id,sys_key,sys_value,is_active,created_by,created_on,last_updated_by,last_updated_on)
		VALUES(UUID(),'MODEL_PUBLISH_STATUS_UPDATE_URL','localhost:8088/umg-admin','Y','system',UNIX_TIMESTAMP(CURDATE()),'system',UNIX_TIMESTAMP(CURDATE()));

UPDATE TENANT_CONFIG SET CONFIG_VALUE = 'false' WHERE SYSTEM_KEY_ID = (SELECT ID FROM SYSTEM_KEY WHERE SYSTEM_KEY = 'FTP');
		
commit;