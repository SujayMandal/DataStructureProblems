use umg_admin;
DROP TABLE IF EXISTS AUTHTOKEN;

CREATE TABLE `AUTHTOKEN` (
	`ID` CHAR(36) NOT NULL COLLATE 'utf8_bin',
	`TENANT_ID` CHAR(36) NOT NULL COMMENT 'Tenant ID' COLLATE 'utf8_bin',
	`AUTH_CODE` VARCHAR(64) NOT NULL COLLATE 'utf8_bin',
	`ACTIVE_FROM` BIGINT(20),	
	`ACTIVE_UNTIL` BIGINT(20),
	`STATUS` VARCHAR(100) NOT NULL,
	`COMMENT` VARCHAR(100),
	`CREATED_ON` BIGINT(20) NOT NULL,
	`CREATED_BY` VARCHAR(100) NOT NULL COLLATE 'utf8_bin',
	`LAST_UPDATED_BY` VARCHAR(100) NULL DEFAULT NULL COLLATE 'utf8_bin',
	`LAST_UPDATED_ON` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`ID`),
	INDEX `FK_AUTHTOKEN_TENANT` (`TENANT_ID`),
	CONSTRAINT `FK_AUTHTOKEN_TENANT` FOREIGN KEY (`TENANT_ID`) REFERENCES `TENANT` (`ID`) ON UPDATE NO ACTION ON DELETE NO ACTION
)
COLLATE='utf8_bin'
ENGINE=InnoDB;


/* set delimiter */

DELIMITER $$

/* remove procedure insert_authToken... */
DROP PROCEDURE IF EXISTS insert_authToken $$

/* create procedure insert_default_privileges*/ 
CREATE PROCEDURE insert_authToken ()
BEGIN
DECLARE authToken_length INT;
DECLARE x INT DEFAULT 0;
DECLARE tenantId VARCHAR(100);

/* drop temp table  TEMP_TABLE_TENANT */ 
DROP TABLE IF EXISTS TEMP_TABLE_AUTHTOKEN;
/* create temp table  TEMP_TABLE_TENANT */
create temporary table if not exists TEMP_TABLE_AUTHTOKEN
select 
	TENANT_TEMP.AUTH_TOKEN as 'AUTH_TOKEN',TENANT_TEMP.ID AS 'TENANT_ID'
from TENANT as TENANT_TEMP;

SELECT * FROM TEMP_TABLE_AUTHTOKEN;


SELECT COUNT(*) FROM TEMP_TABLE_AUTHTOKEN INTO authToken_length;

SELECT authToken_length;

WHILE x < authToken_length DO
SELECT TENANT_ID FROM TEMP_TABLE_AUTHTOKEN LIMIT x,1 INTO tenantId ;

INSERT INTO AUTHTOKEN(`ID`, `TENANT_ID`, `AUTH_CODE`,`ACTIVE_FROM`,`ACTIVE_UNTIL`,`STATUS`,`COMMENT`,`CREATED_ON`,`CREATED_BY`,`LAST_UPDATED_BY`,
`LAST_UPDATED_ON`) VALUES(uuid(),(select TTU.ID  from TENANT AS TTU WHERE tenantId=TTU.ID),(select TTU.AUTH_TOKEN  from TENANT AS TTU WHERE tenantId=TTU.ID),1464331168000,1495780768000,'Active','Tenant Onboarded',(select UNIX_TIMESTAMP()),'SYSTEM','SYSTEM',(select UNIX_TIMESTAMP()));
SET x = x+1;
END WHILE;

DROP TABLE TEMP_TABLE_AUTHTOKEN;

/*Successfully dropped temp tables*/

SELECT AUTH_CODE,TENANT_ID FROM AUTHTOKEN;

END $$

DELIMITER ;

CALL insert_authToken();

DROP PROCEDURE insert_authToken;

ALTER TABLE `TENANT`	DROP COLUMN `AUTH_TOKEN`;

commit;
