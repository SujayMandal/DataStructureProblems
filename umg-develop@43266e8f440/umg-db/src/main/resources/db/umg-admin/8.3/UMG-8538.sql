USE umg_admin;
INSERT INTO SYSTEM_PARAMETER
	(id,sys_key,sys_value,is_active,created_by,created_on,last_updated_by,last_updated_on)
		VALUES(UUID(),'MODEL_PUBLISH_STATUS_UPDATE_URL','localhost:8088/umg-admin','Y','system',UNIX_TIMESTAMP(CURDATE()),'system',UNIX_TIMESTAMP(CURDATE()));
commit;