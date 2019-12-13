-- --------------------------------------------------------
-- Host:                         localhost
-- Server version:               5.6.17 - MySQL Community Server (GPL)
-- Server OS:                    Win32
-- HeidiSQL Version:             8.3.0.4694
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Dumping structure for table localhost.mediate_model_library
CREATE TABLE IF NOT EXISTS `MEDIATE_MODEL_LIBRARY` (
  `ID` char(36) NOT NULL,
  `TAR_NAME` varchar(100) NOT NULL,
  `CHECKSUM_VALUE` varchar(100) NOT NULL,
  `CHECKSUM_TYPE` varchar(45) NOT NULL,
  `TENANT_ID` char(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'Tenant code for the record',
  `MODEL_EXEC_ENV_ID` char(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATED_BY` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `LAST_UPDATED_ON` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_MODEL_EXEC_ID` (`MODEL_EXEC_ENV_ID`),
  CONSTRAINT `FK_MODEL_EXEC_ID` FOREIGN KEY (`MODEL_EXEC_ENV_ID`) REFERENCES `MODEL_EXECUTION_ENVIRONMENTS` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table localhost.mediate_model_library_audit
CREATE TABLE IF NOT EXISTS `MEDIATE_MODEL_LIBRARY_AUDIT` (
  `ID` char(36) NOT NULL,
  `TAR_NAME` varchar(100) NOT NULL,
  `CHECKSUM_VALUE` varchar(100) NOT NULL,
  `CHECKSUM_TYPE` varchar(100) NOT NULL,
  `MODEL_EXEC_ENV_ID` char(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'Tenant code for the record',
  `REV` int(11) NOT NULL,
  `CREATED_BY` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `LAST_UPDATED_ON` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`,`REV`),
  KEY `FK_REV` (`REV`),
  CONSTRAINT `FK_REV` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

