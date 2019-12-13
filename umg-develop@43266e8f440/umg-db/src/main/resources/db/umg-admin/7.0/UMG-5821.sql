use umg_admin;

INSERT INTO `ROLES` (`ROLE`, `Id`) VALUES ('ROLE_MODELER', uuid());
INSERT INTO `ROLES` (`ROLE`, `Id`) VALUES ('ROLE_TENANT_USER', uuid());
/* set delimiter */
DELIMITER $$

/* remove procedure insert_default_privileges if exists... */
DROP PROCEDURE IF EXISTS insert_default_privileges $$

/* create procedure insert_default_privileges*/ 
CREATE PROCEDURE insert_default_privileges ()
BEGIN


DECLARE tenant_length INT;
DECLARE role_length INT;
DECLARE permissions_length INT;
DECLARE x INT DEFAULT 0;
DECLARE y INT DEFAULT 0;


/* drop temp table  TEMP_TABLE_TENANT */ 
DROP TABLE IF EXISTS TEMP_TABLE_TENANT;
/* create temp table  TEMP_TABLE_TENANT */
create temporary table if not exists TEMP_TABLE_TENANT
select 
	TENANT_TEMP.CODE as 'TENANT_CODE'	
from umg_admin.TENANT as TENANT_TEMP;

/* drop temp table  TEMP_TABLE_ROLES */
DROP TABLE IF EXISTS TEMP_TABLE_ROLES;
/* create temp table  TEMP_TABLE_TENANT */
create temporary table if not exists TEMP_TABLE_ROLES
select 
	ROLES_TEMP.ID as 'ROLE_ID'	,ROLES_TEMP.ROLE AS 'ROLE'
from umg_admin.ROLES as ROLES_TEMP WHERE(ROLES_TEMP.ROLE= 'ROLE_ADMIN' OR ROLES_TEMP.ROLE='ROLE_MODELER' OR ROLES_TEMP.ROLE='ROLE_TENANT_USER');

/* drop temp table  TEMP_TABLE_PERMISSIONS */
DROP TABLE IF EXISTS TEMP_TABLE_PERMISSIONS;
/* create temp table  TEMP_TABLE_PERMISSIONS */
create temporary table if not exists TEMP_TABLE_PERMISSIONS
select 
	PERMISSIONS_TEMP.ID as 'PERMISSION_ID'	
from umg_admin.PERMISSIONS as PERMISSIONS_TEMP;

/* drop temp table  TEMP_TABLE_TENANT_ROLES */
DROP TABLE IF EXISTS TEMP_TABLE_TENANT_ROLES;
/* create temp table  TEMP_TABLE_TENANT_ROLES */
create temporary table if not exists TEMP_TABLE_TENANT_ROLES
select 
	TENANT_ROLES_TEMP.id as 'TENANT_ROLE_ID'
from umg_admin.TENANT_ROLES_MAPPING as TENANT_ROLES_TEMP,umg_admin.ROLES AS ROLES WHERE ROLES.ID=TENANT_ROLES_TEMP.roles_id AND ROLES.ROLE IN('ROLE_ADMIN','ROLE_MODELER') GROUP BY ROLES.ROLE;

SELECT COUNT(*) FROM TEMP_TABLE_TENANT INTO tenant_length;
SELECT COUNT(*) FROM TEMP_TABLE_ROLES INTO role_length;
SELECT COUNT(*) FROM TEMP_TABLE_PERMISSIONS INTO permissions_length;
WHILE x < tenant_length DO
WHILE y < role_length DO
INSERT INTO TENANT_ROLES_MAPPING(`Id`, `roles_id`, `tenant_code`) VALUES(uuid(),(select ROLE_ID  from TEMP_TABLE_ROLES LIMIT y,1),(select TENANT_CODE  from TEMP_TABLE_TENANT LIMIT x,1));
SET y = y+1;
END WHILE;
SET x = x+1;
SET y = 0;
END WHILE;

/* Tenant roles created successfully*/ 
/* Started creating Permissions  for the roles ROLE_ADMIN */ 

DROP TABLE IF EXISTS TEMP_TABLE_TENANT_ROLES;
create temporary table if not exists TEMP_TABLE_TENANT_ROLES
select 
	trm.Id as 'TENANT_ROLE_ID'
from TENANT_ROLES_MAPPING as trm,ROLES AS r WHERE r.ID=trm.roles_id AND r.ROLE IN('ROLE_ADMIN');

SET x = 0;
SET y = 0;
SELECT COUNT(*) FROM TEMP_TABLE_TENANT_ROLES INTO role_length;
WHILE x < permissions_length DO
WHILE y < role_length DO
    	INSERT INTO `PERMISSION_ROLES_MAPPING` (`id`, `tenant_roles_map_id`, `permission_id`) VALUES (uuid(), 
	(select TENANT_ROLE_ID from TEMP_TABLE_TENANT_ROLES LIMIT y,1),
	(select PERMISSION_ID from TEMP_TABLE_PERMISSIONS LIMIT x,1));
SET y=y+1;
END WHILE;
SET y = 0;
SET x=x+1;
END WHILE;
SET y = 0;
SET x = 0;

SELECT 'Permissions created successfully for the roles ROLE_ADMIN';

/* Permissions created successfully for the roles ROLE_ADMIN*/ 


/* Started creating Permissions  for the roles ROLE_MODELER*/ 

DROP TABLE IF EXISTS TEMP_TABLE_TENANT_ROLES;
create temporary table if not exists TEMP_TABLE_TENANT_ROLES
select 
	trm.Id as 'TENANT_ROLE_ID'
from TENANT_ROLES_MAPPING as trm,ROLES AS r WHERE r.ID=trm.roles_id AND r.ROLE IN('ROLE_MODELER');

DROP TABLE IF EXISTS TEMP_TABLE_PERMISSIONS;
create temporary table if not exists TEMP_TABLE_PERMISSIONS
select 
	PERMISSIONS_TEMP.ID as 'PERMISSION_ID'	
from PERMISSIONS as PERMISSIONS_TEMP WHERE PERMISSIONS_TEMP.permission NOT IN( 'Notifications.Add','Notifications.Manage');

SET x = 0;
SET y = 0;
SELECT COUNT(*) FROM TEMP_TABLE_TENANT_ROLES INTO role_length;
WHILE x < permissions_length-2 DO
WHILE y < role_length DO
    	INSERT INTO `PERMISSION_ROLES_MAPPING` (`id`, `tenant_roles_map_id`, `permission_id`) VALUES (uuid(), 
	(select TENANT_ROLE_ID from TEMP_TABLE_TENANT_ROLES LIMIT y,1),
	(select PERMISSION_ID from TEMP_TABLE_PERMISSIONS LIMIT x,1));
SET y=y+1;
END WHILE;
SET y = 0;
SET x=x+1;
END WHILE;
SET y = 0;
SET x = 0;

SELECT 'Permissions created successfully for the roles ROLE_MODELER';

/* Permissions created successfully for the roles ROLE_ADMIN ROLE_MODELER*/ 


SELECT 'Started creating Permissions  for the roles ROLE_TENANT_USER*';

DROP TABLE IF EXISTS TEMP_TABLE_TENANT_ROLES;
create temporary table if not exists TEMP_TABLE_TENANT_ROLES
select 
	TENANT_ROLES_TEMP.Id as 'TENANT_ROLE_ID'
from TENANT_ROLES_MAPPING as TENANT_ROLES_TEMP,ROLES AS ROLES WHERE ROLES.Id=TENANT_ROLES_TEMP.roles_id AND ROLES.ROLE IN('ROLE_TENANT_USER');

DROP TABLE IF EXISTS TEMP_TABLE_PERMISSIONS;
create temporary table if not exists TEMP_TABLE_PERMISSIONS
select 
	PERMISSIONS_TEMP.ID as 'PERMISSION_ID'	
from PERMISSIONS as PERMISSIONS_TEMP WHERE PERMISSIONS_TEMP.permission IN( 'Model.Manage',
 'Model.Manage.ExportVersionAPI',
 'Model.Manage.View', 
 'Model.Manage.View.DownloadReleaseNotes',
 'Lookup.Manage', 
 'Dashboard.BatchBulk', 
 'Dashboard.BatchBulk.DownloadIO',
 'Dashboard.BatchBulk.TerminateBatch',
 'Dashboard.BatchBulk.Upload', 
 'Dashboard.Transaction', 
 'Dashboard.Transaction.AdvancedSearch', 
 'Dashboard.Transaction.DownloadExcelUsageReport', 
 'Dashboard.Transaction.DownloadIOExcel',
 'Dashboard.Transaction.DownloadIOJson',
 'Dashboard.Transaction.DownloadModelIO',
 'Dashboard.Transaction.DownloadReport', 
 'Dashboard.Transaction.DownloadTenantIO',
 'Dashboard.Transaction.PayloadField',
 'Dashboard.Transaction.Re-run');

SET x = 0;
SELECT COUNT(*) FROM TEMP_TABLE_PERMISSIONS INTO permissions_length;
SELECT COUNT(*) FROM TEMP_TABLE_TENANT_ROLES INTO role_length;
SELECT role_length;
WHILE x < permissions_length DO
WHILE y < role_length DO
    	INSERT INTO `PERMISSION_ROLES_MAPPING` (`id`, `tenant_roles_map_id`, `permission_id`) VALUES (uuid(), 
	(select TENANT_ROLE_ID from TEMP_TABLE_TENANT_ROLES LIMIT y,1),
	(select PERMISSION_ID from TEMP_TABLE_PERMISSIONS LIMIT x,1));
SET y=y+1;
END WHILE;
SET y = 0;
SET x = x+1;
END WHILE;

SELECT 'Permissions created successfully for the roles ROLE_TENANT_USER';


/*Dropping temp tables*/

DROP TABLE TEMP_TABLE_PERMISSIONS;
DROP TABLE TEMP_TABLE_TENANT_ROLES;
DROP TABLE TEMP_TABLE_TENANT;
DROP TABLE TEMP_TABLE_ROLES;
/*Successfully dropped temp tables*/

END $$

DELIMITER ;

CALL insert_default_privileges();

DROP PROCEDURE insert_default_privileges;

commit;
