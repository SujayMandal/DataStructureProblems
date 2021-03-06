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
