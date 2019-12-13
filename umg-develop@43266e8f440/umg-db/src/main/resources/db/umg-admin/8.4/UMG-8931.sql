USE umg_admin;

INSERT INTO SYSTEM_PARAMETER
	(id,sys_key,DESCRIPTION,sys_value,is_active,created_by,created_on,last_updated_by,last_updated_on)
		VALUES(UUID(),'BATCH_MODELET_POLLING_INTERVAL','Modelet polling interval timespan in millisecond','50000','Y','system',UNIX_TIMESTAMP(CURDATE()),'system',UNIX_TIMESTAMP(CURDATE()));

commit;
		
USE `Ocwen`;

ALTER TABLE `BATCH_TRANSACTION` ADD COLUMN `NOT_PICKED_COUNT` bigint(20) NULL DEFAULT 0 AFTER `FAIL_COUNT`;

commit;