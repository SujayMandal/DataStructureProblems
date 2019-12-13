/*
SQLyog Community v11.01 (32 bit)
MySQL - 5.6.17 : Database - umg_admin
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
DROP DATABASE /*!32312 IF EXISTS*/ `umg_admin`;
DROP DATABASE /*!32312 IF EXISTS*/ `Ocwen`;

CREATE DATABASE /*!32312 IF NOT EXISTS*/`umg_admin` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`Ocwen` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;


/*
SQLyog Community v11.01 (32 bit)
MySQL - 5.6.17 : Database - umg_admin
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`umg_admin` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;

USE `umg_admin`;

DROP TABLE IF EXISTS `REVINFO`;

CREATE TABLE `REVINFO` (
  `REV` int(11) NOT NULL AUTO_INCREMENT,
  `REVTSTMP` bigint(20) DEFAULT NULL,
  `REVBY` VARCHAR(100),
  PRIMARY KEY (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

/*
SQLyog Community v11.01 (32 bit)
MySQL - 5.6.17 : Database - umg_admin
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`umg_admin` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;


/*Table structure for table `SYSTEM_KEY` */

DROP TABLE IF EXISTS `SYSTEM_KEY`;

CREATE TABLE `SYSTEM_KEY` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `SYSTEM_KEY` varchar(45) COLLATE utf8_bin NOT NULL,
  `KEY_TYPE` varchar(200) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` char(36) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(36) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;


/*
SQLyog Community v11.01 (32 bit)
MySQL - 5.6.17 : Database - umg_admin
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`umg_admin` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;

/*Table structure for table `TENANT` */

DROP TABLE IF EXISTS `TENANT`;

CREATE TABLE `TENANT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(45) COLLATE utf8_bin NOT NULL COMMENT 'Tenant name',
  `DESCRIPTION` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CODE` varchar(45) COLLATE utf8_bin NOT NULL COMMENT 'Tenant code used for unique identificaiton',
  `TENANT_TYPE` varchar(45) COLLATE utf8_bin NOT NULL,
  `AUTH_TOKEN` varchar(64 ) COLLATE utf8_bin NOT NULL COMMENT 'Generated Authentication Token',
  `CREATED_BY` char(36) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(36) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UN_TENANT_NAME` (`NAME`),
  UNIQUE KEY `UN_TENANT_CODE` (`CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

/*
SQLyog Community v11.01 (32 bit)
MySQL - 5.6.17 : Database - umg_admin
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`umg_admin` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;


/*Table structure for table `TENANT_CONFIG` */

DROP TABLE IF EXISTS `TENANT_CONFIG`;

CREATE TABLE `TENANT_CONFIG` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Tenant where this config parameter belongs to.',
  `SYSTEM_KEY_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Config parameter key.',
  `CONFIG_VALUE` varchar(500) COLLATE utf8_bin DEFAULT NULL COMMENT 'Config parameter value.',
  `CREATED_BY` char(36) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(36) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UN_TENANT_CONFIG_KEY` (`TENANT_ID`,`SYSTEM_KEY_ID`) COMMENT 'Key for a tenant is unique',
  KEY `FK_TENANT_CONFIG_SYSTEM_KEY_idx` (`SYSTEM_KEY_ID`),
  CONSTRAINT `FK_TENANT_CONFIG_SYSTEM_KEY` FOREIGN KEY (`SYSTEM_KEY_ID`) REFERENCES `SYSTEM_KEY` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
debug2: channel 0: window 994590 sent adjust 53986 (`TENANT_ID`) REFERENCES `TENANT` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

/*
SQLyog Community v11.01 (32 bit)
MySQL - 5.6.17 : Database - umg_admin
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`umg_admin` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;

/*Table structure for table `ADDRESS` */

DROP TABLE IF EXISTS `ADDRESS`;

CREATE TABLE `ADDRESS` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Tenant ID for the address',
  `ADDRESS_1` varchar(45) COLLATE utf8_bin NOT NULL,
  `ADDRESS_2` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `CITY` varchar(45) COLLATE utf8_bin NOT NULL,
  `STATE` varchar(45) COLLATE utf8_bin NOT NULL,
  `ZIP` varchar(45) COLLATE utf8_bin NOT NULL,
  `COUNTRY` varchar(45) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` char(36) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(36) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  CONSTRAINT `FK_ADDRESS_TENANT` FOREIGN KEY (`TENANT_ID`) REFERENCES `TENANT` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

/*
SQLyog Community v11.01 (32 bit)
MySQL - 5.6.17 : Database - umg_admin
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`umg_admin` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;

/*Table structure for table `MODEL_IMPLEMENTATION_TYPE` */

DROP TABLE IF EXISTS `MODEL_IMPLEMENTATION_TYPE`;

CREATE TABLE IF NOT EXISTS `MODEL_IMPLEMENTATION_TYPE` (
  `ID` CHAR(36) NOT NULL,
  `IMPLEMENTATION` VARCHAR(45) NOT NULL,
  `TYPE_XSD` BLOB NULL,
  `CREATED_BY` CHAR(36) NOT NULL,
  `CREATED_ON` BIGINT(20) NOT NULL,
  `LAST_UPDATED_BY` CHAR(36) NULL,
  `LAST_UPDATED_ON` BIGINT(20) NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;


/*
SQLyog Community v11.01 (32 bit)
MySQL - 5.6.17 : Database - umg_admin
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/umg_admin /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;


DROP TABLE IF EXISTS SYNDICATED_DATA;

CREATE table SYNDICATED_DATA(
        ID char(36) COLLATE utf8_bin NOT NULL,
        CONTAINER_NAME varchar(50) COLLATE utf8_bin NOT NULL,
        DESCRIPTION varchar(200) COLLATE utf8_bin NOT NULL,
        VERSION_ID bigint(4) COLLATE utf8_bin,
        VERSION_NAME VARCHAR(50) NOT NULL COLLATE utf8_bin,
        VERSION_DESCRIPTION VARCHAR(200) NOT NULL COLLATE utf8_bin,
        TABLE_NAME varchar(200) COLLATE utf8_bin,
        VALID_FROM bigint(20) COLLATE utf8_bin,
        VALID_TO bigint(20) COLLATE utf8_bin,
        CREATED_BY char(100) COLLATE utf8_bin NOT NULL,
        CREATED_ON bigint(20) NOT NULL,
        LAST_UPDATED_BY char(100) COLLATE utf8_bin DEFAULT NULL,
        LAST_UPDATED_ON bigint(20) DEFAULT NULL,
        PRIMARY KEY (ID)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS SYNDICATED_DATA_AUDIT;

CREATE TABLE SYNDICATED_DATA_AUDIT(
        ID CHARACTER(36),
        CONTAINER_NAME VARCHAR(50),
        DESCRIPTION VARCHAR(200),
        VERSION_NAME VARCHAR(50),
        VERSION_DESCRIPTION VARCHAR(200),
        VERSION_ID BIGINT,
        TABLE_NAME VARCHAR(200),
        VALID_FROM BIGINT,
        VALID_TO BIGINT,
        CREATED_BY VARCHAR(100) NULL DEFAULT NULL,
        CREATED_ON BIGINT(20) NULL DEFAULT NULL,
        LAST_UPDATED_BY VARCHAR(100) NULL DEFAULT NULL,
        LAST_UPDATED_ON BIGINT(20) NULL DEFAULT NULL,
        REV INTEGER NOT NULL,
        REVTYPE INTEGER,
        PRIMARY KEY (ID,REV),
        CONSTRAINT FK_SYNDICATED_DATA_AUDIT_REVINFO FOREIGN KEY (REV) REFERENCES REVINFO (REV)
);



/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

create index INDEX_CINTAINER_NAME on SYNDICATED_DATA(CONTAINER_NAME);


DELETE FROM TENANT_CONFIG;
DELETE FROM ADDRESS;
DELETE FROM TENANT;
DELETE FROM SYSTEM_KEY;
DELETE FROM SYNDICATED_DATA;


ALTER TABLE ADDRESS modify COLUMN CREATED_BY VARCHAR(100)  COLLATE utf8_bin NOT NULL;
ALTER TABLE ADDRESS modify COLUMN LAST_UPDATED_BY VARCHAR(100)   COLLATE utf8_bin DEFAULT NULL;
ALTER TABLE MODEL_IMPLEMENTATION_TYPE modify COLUMN CREATED_BY VARCHAR(100)  COLLATE utf8_bin NOT NULL;
ALTER TABLE MODEL_IMPLEMENTATION_TYPE modify COLUMN LAST_UPDATED_BY VARCHAR(100)   COLLATE utf8_bin DEFAULT NULL;
ALTER TABLE SYNDICATED_DATA modify COLUMN LAST_UPDATED_BY VARCHAR(100)   COLLATE utf8_bin DEFAULT NULL;
ALTER TABLE SYNDICATED_DATA modify COLUMN CREATED_BY VARCHAR(100)  COLLATE utf8_bin NOT NULL;
ALTER TABLE SYSTEM_KEY modify COLUMN LAST_UPDATED_BY VARCHAR(100)   COLLATE utf8_bin DEFAULT NULL;
ALTER TABLE SYSTEM_KEY modify COLUMN CREATED_BY VARCHAR(100)  COLLATE utf8_bin NOT NULL;
ALTER TABLE TENANT modify COLUMN LAST_UPDATED_BY VARCHAR(100)   COLLATE utf8_bin DEFAULT NULL;
ALTER TABLE TENANT modify COLUMN CREATED_BY VARCHAR(100)  COLLATE utf8_bin NOT NULL;
ALTER TABLE TENANT_CONFIG modify COLUMN CREATED_BY VARCHAR(100)  COLLATE utf8_bin NOT NULL;
ALTER TABLE TENANT_CONFIG modify COLUMN LAST_UPDATED_BY VARCHAR(100)   COLLATE utf8_bin DEFAULT NULL;

-- -----------------------------------------------------
-- UMG_ADMIN SCHEMA CHANGES
-- -----------------------------------------------------



INSERT INTO SYSTEM_KEY(ID,SYSTEM_KEY,KEY_TYPE,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON)
VALUES
('05a68810-e6f9-11e3-a68a-82687f4fc15c','DRIVER','DATABASE','SYSTEM',1401344421,'SYSTEM',1401344421),
('05be454a-e6f9-11e3-a68a-82687f4fc15c','URL','DATABASE','SYSTEM',1401344421,'SYSTEM',1401344421),
('05be4a2c-e6f9-11e3-a68a-82687f4fc15c','SCHEMA','DATABASE','SYSTEM',1401344421,'SYSTEM',1401344421),
('05be4fe0-e6f9-11e3-a68a-82687f4fc15c','USER','DATABASE','SYSTEM',1401344421,'SYSTEM',1401344421),
('05be5350-e6f9-11e3-a68a-82687f4fc15c','PASSWORD','DATABASE','SYSTEM',1401344421,'SYSTEM',1401344421),
('74246ec4-fac2-11e3-801d-b2227cce2b54','URL','TENANT','SYSTEM',1401344421,'SYSTEM',1401344421),
('742471a8-fac2-11e3-801d-b2227cce2b54','SOME','TENANT','SYSTEM',1401344421,'SYSTEM',1401344421),
('74246ec4-fac3-11e3-801d-b2227cce2c54','COLUMN_IDENTIFIERS','STRING,VARCHAR,DECIMAL,DOUBLE,BIT,INTEGER, INT, SMALLINT, TINYINT, MEDIUMINT, BIGINT,TIMESTAMP,TIME,YEAR,DATETIME,DATE','SYSTEM',1401344421,'SYSTEM',1401344421);
INSERT INTO SYSTEM_KEY(ID,SYSTEM_KEY,KEY_TYPE,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON) VALUES ('15a69810-e665-12e3-b687-98654f4fc15c','BATCH_ENABLED','TENANT','SYSTEM',1401344421,'SYSTEM',1401344421);
INSERT INTO `SYSTEM_KEY`
(`ID`, `SYSTEM_KEY`, `KEY_TYPE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`)
VALUES ('742491a8-fac2-11e3-801d-b2227cce2b54', 'EXCEL', 'PLUGIN', 'SYSTEM', '1401344421', 'SYSTEM', '1401344421');


DROP TABLE IF EXISTS USERS ;
SHOW WARNINGS;
CREATE TABLE USERS (
USERNAME VARCHAR(50) NOT NULL ,
PASSWORD VARCHAR(100) NOT NULL ,
ENABLED TINYINT NOT NULL DEFAULT 1 ,
PRIMARY KEY (USERNAME)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS USER_ROLES ;
SHOW WARNINGS;
CREATE TABLE USER_ROLES (
USER_ROLE_ID INT(11) NOT NULL AUTO_INCREMENT,
USERNAME VARCHAR(50) NOT NULL,
ROLE VARCHAR(25) NOT NULL,
PRIMARY KEY (USER_ROLE_ID),
UNIQUE KEY uni_USERNAME_role (ROLE,USERNAME),
KEY fk_USERNAME_idx (USERNAME),
CONSTRAINT fk_USERNAME FOREIGN KEY (USERNAME) REFERENCES USERS (USERNAME)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;



-- -----------------------------------------------------
-- Table `TRANSPORT_TYPES`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `TRANSPORT_TYPES` (
  ID CHAR(36) NOT NULL,
  NAME VARCHAR(45) NOT NULL,
  CREATED_BY CHAR(36) NOT NULL COMMENT 'User created the record.',
  CREATED_ON BIGINT(20) NOT NULL COMMENT 'Record created time.',
  LAST_UPDATED_BY CHAR(36) NULL COMMENT 'User last updated the record.',
  LAST_UPDATED_ON BIGINT(20) NULL COMMENT 'Record last updated time.',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `TRANSPORT_PARAMETERS`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `TRANSPORT_PARAMETERS` (
  ID CHAR(36) NOT NULL,
  TRANSPORT_TYPE_ID VARCHAR(45) NOT NULL,
  PARAMETER_NAME VARCHAR(45) NOT NULL,
  DEFAULT_VALUE VARCHAR(45) NULL,
  CREATED_BY CHAR(36) NOT NULL COMMENT 'User created the record.',
  CREATED_ON BIGINT(20) NOT NULL COMMENT 'Record created time.',
  LAST_UPDATED_BY CHAR(36) NULL COMMENT 'User last updated the record.',
  LAST_UPDATED_ON BIGINT(20) NULL COMMENT 'Record last updated time.',
  PRIMARY KEY (`ID`),
  CONSTRAINT `FK_TRANSPORT_PARAMETERS_TRANSPORT_TYPE` FOREIGN KEY (`TRANSPORT_TYPE_ID`) REFERENCES `TRANSPORT_TYPES` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
  )
ENGINE = InnoDB;

ALTER TABLE TENANT_CONFIG ADD COLUMN `ROLE` CHAR(20) NULL DEFAULT NULL;

CREATE TABLE SYSTEM_PARAMETER(
                        ID CHAR(36),
                        SYS_KEY VARCHAR(100) ,
                        SYS_VALUE VARCHAR(100),
                        IS_ACTIVE CHAR(1),
                        CREATED_BY VARCHAR(100),
                        CREATED_ON BIGINT(20),
                        LAST_UPDATED_BY VARCHAR(100),
                        LAST_UPDATED_ON BIGINT(20),
                        PRIMARY KEY(ID)
);
CREATE TABLE SYSTEM_PARAMETER_AUDIT(
                        ID CHAR(36),
                        SYS_KEY VARCHAR(100) ,
                        SYS_VALUE VARCHAR(100),
                        IS_ACTIVE CHAR(1),
                        CREATED_BY VARCHAR(100),
                        CREATED_ON BIGINT(20),
                        LAST_UPDATED_BY VARCHAR(100),
                        LAST_UPDATED_ON BIGINT(20),
                        REV INT(11),
                        REVTYPE INT(11),
                        PRIMARY KEY(ID,REV)
);


INSERT INTO umg_admin.SYSTEM_PARAMETER
        (id,sys_key,sys_value,is_active,created_by,created_on,last_updated_by,last_updated_on)
                VALUES('24ead742-b6d0-4775-ab78-3daf42026c1a','sanBase','/sanpath','Y','system',1415354329490,'system',1415354329490);

INSERT INTO umg_admin.SYSTEM_PARAMETER
        (id,sys_key,sys_value,is_active,created_by,created_on,last_updated_by,last_updated_on)
                VALUES('0a4d6264-ef3f-41d4-9f9a-8a01721d3623','umg-runtime-context','/umg-runtime','Y','system',1415354329490,'system',1415354329490);

INSERT INTO umg_admin.SYSTEM_PARAMETER
        (id,sys_key,sys_value,is_active,created_by,created_on,last_updated_by,last_updated_on)
                VALUES('523bd356-f69b-49a1-92d5-171960dd16dd','version-deploy-api','/api/deployment/deploy','Y','system',1415354329490,'system',1415354329490);

INSERT INTO umg_admin.SYSTEM_PARAMETER
        (id,sys_key,sys_value,is_active,created_by,created_on,last_updated_by,last_updated_on)
                VALUES('9bc66734-403f-4adb-b37b-a42892f0eb7c','version-undeploy-api','/api/deployment/undeploy','Y','system',1415354329490,'system',1415354329490);

INSERT INTO umg_admin.SYSTEM_PARAMETER
        (id,sys_key,sys_value,is_active,created_by,created_on,last_updated_by,last_updated_on)
                VALUES('a27df8df-1564-448d-8234-fea06b0b8b50','version-test-api','/api/deployment/test/','Y','system',1415354329490,'system',1415354329490);

INSERT INTO umg_admin.SYSTEM_PARAMETER
        (id,sys_key,sys_value,is_active,created_by,created_on,last_updated_by,last_updated_on)
                VALUES('d3ce07d9-b0b7-4a3c-8f84-722278ecb2f2','umg-runtime-username','admin','Y','system',1415354329490,'system',1415354329490);

INSERT INTO umg_admin.SYSTEM_PARAMETER
        (id,sys_key,sys_value,is_active,created_by,created_on,last_updated_by,last_updated_on)
                VALUES('a44071e2-5c1f-45ac-b64c-143b3547072b','umg-runtime-pwd','admin','Y','system',1415354329490,'system',1415354329490);

INSERT INTO umg_admin.SYSTEM_PARAMETER
        (id,sys_key,sys_value,is_active,created_by,created_on,last_updated_by,last_updated_on)
                VALUES('f80c748f-1a63-443e-a7db-4414abefeb0a','validation-error-code-pattern','RVE','Y','system',1415354329490,'system',1415354329490);

INSERT INTO umg_admin.SYSTEM_PARAMETER
        (id,sys_key,sys_value,is_active,created_by,created_on,last_updated_by,last_updated_on)
                VALUES('2d1ebba5-f8ca-4e58-9769-62deb6274e7f','system-exception-error-code-pattern','RSE','Y','system',1415354329490,'system',1415354329490);

INSERT INTO umg_admin.SYSTEM_PARAMETER
        (id,sys_key,sys_value,is_active,created_by,created_on,last_updated_by,last_updated_on)
                VALUES('52be9e19-5be0-4407-a096-3dabf160a0b7','model-exception-error-code-pattern','RME','Y','system',1415354329490,'system',1415354329490);

INSERT INTO umg_admin.SYSTEM_PARAMETER
    (id,sys_key,sys_value,is_active,created_by,created_on,last_updated_by,last_updated_on)
                VALUES('a44071e2-5c1f-45ac-b64c-143b3546767z','batch-timeout','300000','Y','system',1415354329490,'system',1415354329490);

                INSERT INTO umg_admin.SYSTEM_PARAMETER
        (id,sys_key,sys_value,is_active,created_by,created_on,last_updated_by,last_updated_on)
                VALUES('789B7B7F-192A-4BD4-B156-BEDF9C1A00EA','timeout','60000','Y','system',1417439330020,'system',1417439330020);

INSERT INTO umg_admin.SYSTEM_PARAMETER
        (id,sys_key,sys_value,is_active,created_by,created_on,last_updated_by,last_updated_on)
                VALUES('ED43C133-A673-4F98-9054-149B2C7F0694','retryCount','2','Y','system',1417439330020,'system',1417439330020);

INSERT INTO umg_admin.SYSTEM_PARAMETER
        (id,sys_key,sys_value,is_active,created_by,created_on,last_updated_by,last_updated_on)
                VALUES('882AC8C3-9AC0-4953-9A28-541D784C51C5','bulk-test-exec','1','Y','system',1417439330020,'system',1417439330020);

INSERT INTO umg_admin.SYSTEM_PARAMETER
        (id,sys_key,sys_value,is_active,created_by,created_on,last_updated_by,last_updated_on)
                VALUES('744EBBAC-3916-412E-BB5A-0E559930853B','batch-deploy-api','/api/batch/deploy','Y','system',1417439330020,'system',1417439330020);

INSERT INTO umg_admin.SYSTEM_PARAMETER
        (id,sys_key,sys_value,is_active,created_by,created_on,last_updated_by,last_updated_on)
                VALUES('E69889C1-B81B-4993-93C4-2CA59F7FD78D','batch-undeploy-api','/api/batch/undeploy','Y','system',1417439330020,'system',1417439330020);


INSERT INTO umg_admin.SYSTEM_PARAMETER
        (id,sys_key,sys_value,is_active,created_by,created_on,last_updated_by,last_updated_on)
                VALUES('094B30C4-9778-4A12-8EE6-81723C958CB6','batch.threadPoolSize','10','Y','system',1417439330020,'system',1417439330020);

commit;
