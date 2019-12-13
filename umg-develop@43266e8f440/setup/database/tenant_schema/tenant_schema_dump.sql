-- MySQL dump 10.13  Distrib 5.6.24, for Win64 (x86_64)
--
-- Host: localhost    Database: localhost
-- ------------------------------------------------------
-- Server version	5.6.24-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED='995eb99f-e348-11e4-9245-00ffbc72cbd1:1-118269,
995eb99f-e348-11e4-9245-00ffbc73cbd1:1-33';

--
-- Table structure for table `BATCH_TRANSACTION`
--

DROP TABLE IF EXISTS `BATCH_TRANSACTION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `BATCH_TRANSACTION` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` varchar(50) COLLATE utf8_bin NOT NULL,
  `BATCH_INPUT_FILE` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `BATCH_OUTPUT_FILE` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `STATUS` varchar(25) COLLATE utf8_bin NOT NULL,
  `IS_TEST` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'Indicates whether the upload is from test bed',
  `START_TIME` bigint(20) DEFAULT NULL,
  `END_TIME` bigint(20) DEFAULT NULL,
  `TOTAL_RECORDS` bigint(20) DEFAULT NULL,
  `SUCCESS_COUNT` bigint(20) DEFAULT NULL,
  `FAIL_COUNT` bigint(20) DEFAULT NULL,
  `NOT_PICKED_COUNT` bigint(20) DEFAULT '0',
  `CREATED_BY` char(36) COLLATE utf8_bin NOT NULL COMMENT 'User created the record.',
  `CREATED_ON` bigint(20) NOT NULL COMMENT 'Record created time.',
  `LAST_UPDATED_BY` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'User last updated the record.',
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL COMMENT 'Record last updated time.',
  `TRANSACTION_MODE` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `TERMINATE_COUNT` int(11) DEFAULT NULL,
  `MODEL_NAME` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `MODEL_VERSION` double DEFAULT NULL,
  `EXECUTION_ENVIRONMENT` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'Execution Environment(Linux/Windows)',
  `USER` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `MODELLING_ENVIRONMENT` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'Modelling Environment(R-3.2.1/R-3.3.2/Matlab-7.16)',
  `STORE_RLOGS` varchar(10) COLLATE utf8_bin NOT NULL DEFAULT 'FALSE',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `BATCH_TRANSACTION`
--

LOCK TABLES `BATCH_TRANSACTION` WRITE;
/*!40000 ALTER TABLE `BATCH_TRANSACTION` DISABLE KEYS */;
/*!40000 ALTER TABLE `BATCH_TRANSACTION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `BATCH_TXN_RUNTIME_TXN_MAPPING`
--

DROP TABLE IF EXISTS `BATCH_TXN_RUNTIME_TXN_MAPPING`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `BATCH_TXN_RUNTIME_TXN_MAPPING` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `BATCH_ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` varchar(50) COLLATE utf8_bin NOT NULL,
  `TRANSACTION_ID` char(36) COLLATE utf8_bin NOT NULL,
  `STATUS` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `ERROR` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_BY` char(36) COLLATE utf8_bin NOT NULL COMMENT 'User created the record.',
  `CREATED_ON` bigint(20) NOT NULL COMMENT 'Record created time.',
  `LAST_UPDATED_BY` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'User last updated the record.',
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL COMMENT 'Record last updated time.',
  KEY `FK_BATCH_RUNTIME_TXN_MAPPING_BATCH` (`BATCH_ID`),
  KEY `batch_txn_runtime_txn_mapping_index_2` (`TRANSACTION_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `BATCH_TXN_RUNTIME_TXN_MAPPING`
--

LOCK TABLES `BATCH_TXN_RUNTIME_TXN_MAPPING` WRITE;
/*!40000 ALTER TABLE `BATCH_TXN_RUNTIME_TXN_MAPPING` DISABLE KEYS */;
/*!40000 ALTER TABLE `BATCH_TXN_RUNTIME_TXN_MAPPING` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MAPPING`
--

DROP TABLE IF EXISTS `MAPPING`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MAPPING` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(100) COLLATE utf8_bin NOT NULL COMMENT 'Derived name of the mapping.Includes version date',
  `DESCRIPTION` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT 'Description of the mapping.',
  `MODEL_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Model used for this mapping',
  `MODEL_IO_DATA` mediumblob,
  `CREATED_BY` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin NOT NULL DEFAULT 'davxotdrnq02' COMMENT 'Tenant code for the record',
  `STATUS` varchar(20) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UN_MAPPING_NAME` (`NAME`),
  KEY `FK_MAPPING_MODEL` (`MODEL_ID`),
  CONSTRAINT `FK_MAPPING_MODEL` FOREIGN KEY (`MODEL_ID`) REFERENCES `MODEL` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MAPPING`
--

LOCK TABLES `MAPPING` WRITE;
/*!40000 ALTER TABLE `MAPPING` DISABLE KEYS */;
/*!40000 ALTER TABLE `MAPPING` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MAPPING_AUDIT`
--

DROP TABLE IF EXISTS `MAPPING_AUDIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MAPPING_AUDIT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `NAME` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT 'Derived name of the mapping.Includes version date',
  `DESCRIPTION` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT 'Description of the mapping.',
  `MODEL_ID` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'Model used for this mapping',
  `MODEL_IO_DATA` mediumblob,
  `TENANT_ID` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'Tenant code for the record',
  `STATUS` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_ON` bigint(20) DEFAULT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`,`REV`),
  KEY `IDX_MAPPING_AUDIT_REVINFO` (`REV`),
  CONSTRAINT `FK_MAPPING_AUDIT_REVINFO` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MAPPING_AUDIT`
--

LOCK TABLES `MAPPING_AUDIT` WRITE;
/*!40000 ALTER TABLE `MAPPING_AUDIT` DISABLE KEYS */;
/*!40000 ALTER TABLE `MAPPING_AUDIT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MAPPING_INPUT`
--

DROP TABLE IF EXISTS `MAPPING_INPUT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MAPPING_INPUT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `MAPPING_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Model used for this mapping',
  `MAPPING_DATA` mediumblob,
  `TENANT_INTERFACE_DEFINITION` mediumblob,
  `CREATED_BY` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin NOT NULL DEFAULT 'davxotdrnq02' COMMENT 'Tenant code for the record',
  `TENANT_INTF_SYS_DEFINITION` mediumblob,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UN_MAPPING_INPUT` (`MAPPING_ID`),
  CONSTRAINT `FK_MAPPING_INPUT_MAPPING` FOREIGN KEY (`MAPPING_ID`) REFERENCES `MAPPING` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MAPPING_INPUT`
--

LOCK TABLES `MAPPING_INPUT` WRITE;
/*!40000 ALTER TABLE `MAPPING_INPUT` DISABLE KEYS */;
/*!40000 ALTER TABLE `MAPPING_INPUT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MAPPING_INPUT_AUDIT`
--

DROP TABLE IF EXISTS `MAPPING_INPUT_AUDIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MAPPING_INPUT_AUDIT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `MAPPING_ID` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'Model used for this mapping',
  `MAPPING_DATA` mediumblob,
  `TENANT_INTERFACE_DEFINITION` mediumblob,
  `TENANT_ID` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'Tenant code for the record',
  `TENANT_INTF_SYS_DEFINITION` mediumblob,
  `CREATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_ON` bigint(20) DEFAULT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`,`REV`),
  KEY `IDX_MAPPING_INPUT_AUDIT_REVINFO` (`REV`),
  CONSTRAINT `FK_MAPPING_INPUT_AUDIT_REVINFO` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MAPPING_INPUT_AUDIT`
--

LOCK TABLES `MAPPING_INPUT_AUDIT` WRITE;
/*!40000 ALTER TABLE `MAPPING_INPUT_AUDIT` DISABLE KEYS */;
/*!40000 ALTER TABLE `MAPPING_INPUT_AUDIT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MAPPING_OUTPUT`
--

DROP TABLE IF EXISTS `MAPPING_OUTPUT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MAPPING_OUTPUT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `MAPPING_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Model used for this mapping',
  `MAPPING_DATA` mediumblob,
  `TENANT_INTERFACE_DEFINITION` mediumblob,
  `CREATED_BY` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin NOT NULL DEFAULT 'davxotdrnq02' COMMENT 'Tenant code for the record',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UN_MAPPING_OUTPUT` (`MAPPING_ID`),
  CONSTRAINT `FK_MAPPING_OUTPUT_MAPPING` FOREIGN KEY (`MAPPING_ID`) REFERENCES `MAPPING` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MAPPING_OUTPUT`
--

LOCK TABLES `MAPPING_OUTPUT` WRITE;
/*!40000 ALTER TABLE `MAPPING_OUTPUT` DISABLE KEYS */;
/*!40000 ALTER TABLE `MAPPING_OUTPUT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MAPPING_OUTPUT_AUDIT`
--

DROP TABLE IF EXISTS `MAPPING_OUTPUT_AUDIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MAPPING_OUTPUT_AUDIT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `MAPPING_ID` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'Model used for this mapping',
  `MAPPING_DATA` mediumblob,
  `TENANT_INTERFACE_DEFINITION` mediumblob,
  `TENANT_ID` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'Tenant code for the record',
  `CREATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_ON` bigint(20) DEFAULT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `TEMP_MODEL_IO` mediumblob,
  PRIMARY KEY (`ID`,`REV`),
  KEY `IDX_MAPPING_OUTPUT_AUDIT_REVINFO` (`REV`),
  CONSTRAINT `FK_MAPPING_OUTPUT_AUDIT_REVINFO` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MAPPING_OUTPUT_AUDIT`
--

LOCK TABLES `MAPPING_OUTPUT_AUDIT` WRITE;
/*!40000 ALTER TABLE `MAPPING_OUTPUT_AUDIT` DISABLE KEYS */;
/*!40000 ALTER TABLE `MAPPING_OUTPUT_AUDIT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MEDIATE_MODEL_LIBRARY`
--

DROP TABLE IF EXISTS `MEDIATE_MODEL_LIBRARY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MEDIATE_MODEL_LIBRARY` (
  `ID` char(36) NOT NULL,
  `TAR_NAME` varchar(100) NOT NULL,
  `CHECKSUM_VALUE` varchar(100) NOT NULL,
  `CHECKSUM_TYPE` varchar(45) NOT NULL,
  `TENANT_ID` char(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'Tenant code for the record',
  `MODEL_EXEC_ENV_NAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATED_BY` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `LAST_UPDATED_ON` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_MODEL_EXEC_ID` (`MODEL_EXEC_ENV_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MEDIATE_MODEL_LIBRARY`
--

LOCK TABLES `MEDIATE_MODEL_LIBRARY` WRITE;
/*!40000 ALTER TABLE `MEDIATE_MODEL_LIBRARY` DISABLE KEYS */;
/*!40000 ALTER TABLE `MEDIATE_MODEL_LIBRARY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MEDIATE_MODEL_LIBRARY_AUDIT`
--

DROP TABLE IF EXISTS `MEDIATE_MODEL_LIBRARY_AUDIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MEDIATE_MODEL_LIBRARY_AUDIT` (
  `ID` char(36) NOT NULL,
  `TAR_NAME` varchar(100) NOT NULL,
  `CHECKSUM_VALUE` varchar(100) NOT NULL,
  `CHECKSUM_TYPE` varchar(100) NOT NULL,
  `MODEL_EXEC_ENV_NAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MEDIATE_MODEL_LIBRARY_AUDIT`
--

LOCK TABLES `MEDIATE_MODEL_LIBRARY_AUDIT` WRITE;
/*!40000 ALTER TABLE `MEDIATE_MODEL_LIBRARY_AUDIT` DISABLE KEYS */;
/*!40000 ALTER TABLE `MEDIATE_MODEL_LIBRARY_AUDIT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MIGRATION_LOG`
--

DROP TABLE IF EXISTS `MIGRATION_LOG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MIGRATION_LOG` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Tenant code for the record',
  `VERSION_ID` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'Version ID which is imported / exported',
  `MIGRATION_TYPE` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'Specifies whether it was an import / export entry',
  `VERSION_DATA` blob COMMENT 'Version Details',
  `STATUS` varchar(20) COLLATE utf8_bin NOT NULL COMMENT 'Migration Status',
  `CREATED_BY` char(36) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(36) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `IMPORT_FILE_NAME` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_VERSION` (`VERSION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MIGRATION_LOG`
--

LOCK TABLES `MIGRATION_LOG` WRITE;
/*!40000 ALTER TABLE `MIGRATION_LOG` DISABLE KEYS */;
/*!40000 ALTER TABLE `MIGRATION_LOG` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MODEL`
--

DROP TABLE IF EXISTS `MODEL`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MODEL` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Tenant code for the record',
  `NAME` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'Name of the model, given by user as TAG.',
  `UMG_NAME` varchar(100) COLLATE utf8_bin NOT NULL COMMENT 'UMG provided name of the model. TAG-MM-DD-YYYY-HH-MM',
  `DESCRIPTION` varchar(200) COLLATE utf8_bin NOT NULL COMMENT 'Description of the model.',
  `IO_DEFINITION_NAME` varchar(100) COLLATE utf8_bin NOT NULL COMMENT 'Name of the XML file uploaded. Useed to show on UI.',
  `DOC_NAME` varchar(100) COLLATE utf8_bin NOT NULL COMMENT 'Name of the DOCUMENTATION file uploaded. Useed to show on UI.',
  `ALLOW_NULL` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'Flag to know whether to allow nulls or not',
  `CREATED_BY` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `IO_DEF_EXCEL_NAME` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT 'Name of the Excel file uploaded. Useed to show on UI.',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UN_MODEL_UMG_NAME` (`UMG_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='The table which contains the individual model details';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MODEL`
--

LOCK TABLES `MODEL` WRITE;
/*!40000 ALTER TABLE `MODEL` DISABLE KEYS */;
/*!40000 ALTER TABLE `MODEL` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MODEL_AUDIT`
--

DROP TABLE IF EXISTS `MODEL_AUDIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MODEL_AUDIT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'Tenant code for the record',
  `NAME` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'Name of the model, given by user as TAG.',
  `UMG_NAME` varchar(100) COLLATE utf8_bin NOT NULL COMMENT 'UMG provided name of the model. TAG-MM-DD-YYYY-HH-MM',
  `DESCRIPTION` varchar(200) COLLATE utf8_bin NOT NULL COMMENT 'Description of the model.',
  `IO_DEFINITION_NAME` varchar(100) COLLATE utf8_bin NOT NULL COMMENT 'Name of the XML file uploaded. Useed to show on UI.',
  `DOC_NAME` varchar(100) COLLATE utf8_bin NOT NULL COMMENT 'Name of the DOCUMENTATION file uploaded. Useed to show on UI.',
  `ALLOW_NULL` tinyint(4) NOT NULL,
  `CREATED_BY` char(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `IO_DEF_EXCEL_NAME` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT 'Name of the Excel file uploaded. Useed to show on UI.',
  PRIMARY KEY (`ID`,`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='The table which contains the individual model audit details';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MODEL_AUDIT`
--

LOCK TABLES `MODEL_AUDIT` WRITE;
/*!40000 ALTER TABLE `MODEL_AUDIT` DISABLE KEYS */;
/*!40000 ALTER TABLE `MODEL_AUDIT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MODEL_DEFINITION`
--

DROP TABLE IF EXISTS `MODEL_DEFINITION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MODEL_DEFINITION` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'Tenant code for the record',
  `IO_TYPE` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'Type of model definition text/xml or application/json',
  `IO_DEFINITION` mediumblob NOT NULL COMMENT 'The actual input output defintion for the model.',
  `MODEL_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'The model to which this definition belongs.',
  `CREATED_BY` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_MODEL_ID` (`MODEL_ID`),
  CONSTRAINT `FK_MODEL` FOREIGN KEY (`MODEL_ID`) REFERENCES `MODEL` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='The table which contains the individual model definitions';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MODEL_DEFINITION`
--

LOCK TABLES `MODEL_DEFINITION` WRITE;
/*!40000 ALTER TABLE `MODEL_DEFINITION` DISABLE KEYS */;
/*!40000 ALTER TABLE `MODEL_DEFINITION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MODEL_DEFINITION_AUDIT`
--

DROP TABLE IF EXISTS `MODEL_DEFINITION_AUDIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MODEL_DEFINITION_AUDIT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'Tenant code for the record',
  `IO_TYPE` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'Type of model definition text/xml or application/json',
  `IO_DEFINITION` mediumblob NOT NULL COMMENT 'The actual input output defintion for the model.',
  `MODEL_ID` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'The model to which this definition belongs.',
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `CREATED_BY` char(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`,`REV`),
  KEY `IDX_MODEL_DEFINITION_AUDIT_REVINFO` (`REV`),
  CONSTRAINT `FK_MODEL_DEFINITION_AUDIT_REVINFO` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='The table which contains the individual model definitions audit';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MODEL_DEFINITION_AUDIT`
--

LOCK TABLES `MODEL_DEFINITION_AUDIT` WRITE;
/*!40000 ALTER TABLE `MODEL_DEFINITION_AUDIT` DISABLE KEYS */;
/*!40000 ALTER TABLE `MODEL_DEFINITION_AUDIT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MODEL_EXEC_PACKAGES`
--

DROP TABLE IF EXISTS `MODEL_EXEC_PACKAGES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MODEL_EXEC_PACKAGES` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Tenant code for the record',
  `MODEL_EXEC_ENV_ID` char(36) COLLATE utf8_bin NOT NULL,
  `PACKAGE_NAME` varchar(200) COLLATE utf8_bin NOT NULL,
  `PACKAGE_FOLDER` varchar(200) COLLATE utf8_bin NOT NULL,
  `PACKAGE_VERSION` varchar(50) COLLATE utf8_bin NOT NULL,
  `PACKAGE_TYPE` varchar(50) COLLATE utf8_bin NOT NULL,
  `COMPILED_OS` varchar(50) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` char(50) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(50) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `TENANT_ID_MODEL_EXEC_ENV_ID_PKG_VERSION` (`TENANT_ID`,`MODEL_EXEC_ENV_ID`,`PACKAGE_FOLDER`,`PACKAGE_VERSION`),
  KEY `FK_MODEL_EXECUTION_ENV_ID` (`MODEL_EXEC_ENV_ID`),
  CONSTRAINT `FK_MODEL_EXEC_ENV_ID` FOREIGN KEY (`MODEL_EXEC_ENV_ID`) REFERENCES `MODEL_EXECUTION_ENVIRONMENTS` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Table to store model execution environment base packages';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MODEL_EXEC_PACKAGES`
--

LOCK TABLES `MODEL_EXEC_PACKAGES` WRITE;
/*!40000 ALTER TABLE `MODEL_EXEC_PACKAGES` DISABLE KEYS */;
/*!40000 ALTER TABLE `MODEL_EXEC_PACKAGES` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MODEL_EXEC_PACKAGES_AUDIT`
--

DROP TABLE IF EXISTS `MODEL_EXEC_PACKAGES_AUDIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MODEL_EXEC_PACKAGES_AUDIT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `MODEL_EXEC_ENV_ID` char(36) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'Tenant code for the record',
  `PACKAGE_NAME` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `PACKAGE_FOLDER` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `PACKAGE_VERSION` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `PACKAGE_TYPE` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `COMPILED_OS` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `CREATED_BY` char(50) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_ON` bigint(20) DEFAULT NULL,
  `LAST_UPDATED_BY` char(50) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKMODEL_EXEC_ENV_REV` (`REV`),
  CONSTRAINT `FKMODEL_EXEC_ENV_REV` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT COMMENT='Table to store model execution environment base packages';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MODEL_EXEC_PACKAGES_AUDIT`
--

LOCK TABLES `MODEL_EXEC_PACKAGES_AUDIT` WRITE;
/*!40000 ALTER TABLE `MODEL_EXEC_PACKAGES_AUDIT` DISABLE KEYS */;
/*!40000 ALTER TABLE `MODEL_EXEC_PACKAGES_AUDIT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MODEL_LIBRARY`
--

DROP TABLE IF EXISTS `MODEL_LIBRARY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MODEL_LIBRARY` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Tenant code for the record',
  `NAME` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'Name of the model library, given by user as TAG.',
  `DESCRIPTION` varchar(200) COLLATE utf8_bin NOT NULL COMMENT 'Description of the model library.',
  `UMG_NAME` varchar(100) COLLATE utf8_bin NOT NULL COMMENT 'UMG provided name of the model library. TAG-MM-DD-YYYY-HH-MM',
  `EXECUTION_LANGUAGE` varchar(25) COLLATE utf8_bin NOT NULL COMMENT 'The execution language for the version',
  `EXECUTION_TYPE` varchar(20) COLLATE utf8_bin NOT NULL COMMENT 'The execution mechanism for the version',
  `JAR_NAME` varchar(100) COLLATE utf8_bin NOT NULL COMMENT 'The name of the JAR uploaded against the library',
  `CHECKSUM_TYPE` varchar(45) COLLATE utf8_bin NOT NULL,
  `CHECKSUM_VALUE` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `MODEL_EXEC_ENV_NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `R_MANIFEST_FILE_NAME` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT 'R Manifest file name',
  `PACKAGE_NAME` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `EXECUTION_ENVIRONMENT` varchar(50) COLLATE utf8_bin DEFAULT 'Linux' COMMENT 'Execution environment of the model library',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UN_MODEL_LIBRARY_UMG_NAME` (`UMG_NAME`),
  KEY `FKMODEL_EXEC_ENV_ID` (`MODEL_EXEC_ENV_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='The table which contains the individual model library definitions';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MODEL_LIBRARY`
--

LOCK TABLES `MODEL_LIBRARY` WRITE;
/*!40000 ALTER TABLE `MODEL_LIBRARY` DISABLE KEYS */;
/*!40000 ALTER TABLE `MODEL_LIBRARY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MODEL_LIBRARY_AUDIT`
--

DROP TABLE IF EXISTS `MODEL_LIBRARY_AUDIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MODEL_LIBRARY_AUDIT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'Tenant code for the record',
  `NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'Name of the model library, given by user as TAG.',
  `DESCRIPTION` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT 'Description of the model library.',
  `UMG_NAME` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT 'UMG provided name of the model library. TAG-MM-DD-YYYY-HH-MM',
  `EXECUTION_LANGUAGE` varchar(25) COLLATE utf8_bin DEFAULT NULL COMMENT 'The execution language for the version',
  `EXECUTION_TYPE` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT 'The execution mechanism for the version',
  `JAR_NAME` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT 'The name of the JAR uploaded against the library',
  `CHECKSUM_TYPE` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `CHECKSUM_VALUE` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_BY` char(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `MODEL_EXEC_ENV_NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `R_MANIFEST_FILE_NAME` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT 'R Manifest file name',
  `PACKAGE_NAME` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `EXECUTION_ENVIRONMENT` varchar(50) COLLATE utf8_bin DEFAULT 'Linux' COMMENT 'Execution environment of the model library',
  PRIMARY KEY (`ID`,`REV`),
  KEY `IDX_MODEL_LIBRARY_AUDIT_REVINFO` (`REV`),
  CONSTRAINT `FK_MODEL_LIBRARY_AUDIT_REVINFO` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='The table which contains the individual model library definitions audit table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MODEL_LIBRARY_AUDIT`
--

LOCK TABLES `MODEL_LIBRARY_AUDIT` WRITE;
/*!40000 ALTER TABLE `MODEL_LIBRARY_AUDIT` DISABLE KEYS */;
/*!40000 ALTER TABLE `MODEL_LIBRARY_AUDIT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MODEL_LIB_EXEC_PKG_MAPPING`
--

DROP TABLE IF EXISTS `MODEL_LIB_EXEC_PKG_MAPPING`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MODEL_LIB_EXEC_PKG_MAPPING` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Tenant code for the record',
  `MODEL_LIBRARY_ID` char(36) COLLATE utf8_bin NOT NULL,
  `MODEL_EXEC_PKG_ID` char(36) COLLATE utf8_bin NOT NULL,
  `EXEC_SEQUENCE` int(11) NOT NULL,
  `CREATED_BY` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_MODEL_EXEC_PKG_ID` (`MODEL_EXEC_PKG_ID`),
  KEY `FK_MODEL_LIBRARY_ID` (`MODEL_LIBRARY_ID`),
  CONSTRAINT `FK_MODEL_LIBRARY_ID` FOREIGN KEY (`MODEL_LIBRARY_ID`) REFERENCES `MODEL_LIBRARY` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Mapping table to hold model library and execution package';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MODEL_LIB_EXEC_PKG_MAPPING`
--

LOCK TABLES `MODEL_LIB_EXEC_PKG_MAPPING` WRITE;
/*!40000 ALTER TABLE `MODEL_LIB_EXEC_PKG_MAPPING` DISABLE KEYS */;
/*!40000 ALTER TABLE `MODEL_LIB_EXEC_PKG_MAPPING` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MODEL_LIB_EXEC_PKG_MAPPING_AUDIT`
--

DROP TABLE IF EXISTS `MODEL_LIB_EXEC_PKG_MAPPING_AUDIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MODEL_LIB_EXEC_PKG_MAPPING_AUDIT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'Tenant code for the record',
  `MODEL_LIBRARY_ID` char(36) COLLATE utf8_bin DEFAULT NULL,
  `MODEL_EXEC_PKG_ID` char(36) COLLATE utf8_bin DEFAULT NULL,
  `EXEC_SEQUENCE` int(11) DEFAULT NULL,
  `CREATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_ON` bigint(20) DEFAULT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_MODEL_LIB_EXEC_PKG_REV` (`REV`),
  CONSTRAINT `FK_MODEL_LIB_EXEC_PKG_REV` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT COMMENT='Mapping table to hold model library and execution package';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MODEL_LIB_EXEC_PKG_MAPPING_AUDIT`
--

LOCK TABLES `MODEL_LIB_EXEC_PKG_MAPPING_AUDIT` WRITE;
/*!40000 ALTER TABLE `MODEL_LIB_EXEC_PKG_MAPPING_AUDIT` DISABLE KEYS */;
/*!40000 ALTER TABLE `MODEL_LIB_EXEC_PKG_MAPPING_AUDIT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MODEL_REPORT_STATUS`
--

DROP TABLE IF EXISTS `MODEL_REPORT_STATUS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MODEL_REPORT_STATUS` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `REPORT_TEMPLATE_ID` char(36) COLLATE utf8_bin NOT NULL,
  `UMG_TRANSACTION_ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin NOT NULL,
  `REPORT_URL` varchar(256) COLLATE utf8_bin NOT NULL,
  `REPORT_LOCATION` varchar(256) COLLATE utf8_bin NOT NULL,
  `REPORT_FILE_NAME` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `EXECUTION_STATUS` varchar(45) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` varchar(126) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(126) COLLATE utf8_bin NOT NULL,
  `LAST_UPDATED_ON` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `TRANSACTION_INDEX` (`TENANT_ID`,`UMG_TRANSACTION_ID`),
  KEY `REPORT_TEMPLATE_INDXE` (`REPORT_TEMPLATE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Table to store details of generated report';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MODEL_REPORT_STATUS`
--

LOCK TABLES `MODEL_REPORT_STATUS` WRITE;
/*!40000 ALTER TABLE `MODEL_REPORT_STATUS` DISABLE KEYS */;
/*!40000 ALTER TABLE `MODEL_REPORT_STATUS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MODEL_REPORT_TEMPLATE`
--

DROP TABLE IF EXISTS `MODEL_REPORT_TEMPLATE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MODEL_REPORT_TEMPLATE` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `UMG_VERSION_ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `DESCRIPTION` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `IS_ACTIVE` int(11) NOT NULL,
  `TEMPLATE_FILE_NAME` varchar(256) COLLATE utf8_bin NOT NULL,
  `TEMPLATE_DEFINATION` mediumblob NOT NULL,
  `COMPILED_DEFINATION` mediumblob,
  `MAJOR_VERSION` int(11) DEFAULT NULL,
  `REPORT_TYPE` varchar(45) COLLATE utf8_bin NOT NULL,
  `REPORT_ENGINE` varchar(45) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` varchar(126) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(126) COLLATE utf8_bin NOT NULL,
  `LAST_UPDATED_ON` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `VERSION_INDEX` (`TENANT_ID`,`UMG_VERSION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Stores report template for each model';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MODEL_REPORT_TEMPLATE`
--

LOCK TABLES `MODEL_REPORT_TEMPLATE` WRITE;
/*!40000 ALTER TABLE `MODEL_REPORT_TEMPLATE` DISABLE KEYS */;
/*!40000 ALTER TABLE `MODEL_REPORT_TEMPLATE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MODEL_REPORT_TEMPLATE_AUDIT`
--

DROP TABLE IF EXISTS `MODEL_REPORT_TEMPLATE_AUDIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MODEL_REPORT_TEMPLATE_AUDIT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `UMG_VERSION_ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `DESCRIPTION` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `IS_ACTIVE` int(11) NOT NULL,
  `TEMPLATE_FILE_NAME` varchar(256) COLLATE utf8_bin NOT NULL,
  `TEMPLATE_DEFINATION` mediumblob NOT NULL,
  `COMPILED_DEFINATION` mediumblob,
  `MAJOR_VERSION` int(11) DEFAULT NULL,
  `REPORT_TYPE` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `REPORT_ENGINE` varchar(45) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` varchar(126) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(126) COLLATE utf8_bin NOT NULL,
  `LAST_UPDATED_ON` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID`,`REV`),
  KEY `VERSION_INDEX` (`TENANT_ID`,`UMG_VERSION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Audit table to stores report template for each model';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MODEL_REPORT_TEMPLATE_AUDIT`
--

LOCK TABLES `MODEL_REPORT_TEMPLATE_AUDIT` WRITE;
/*!40000 ALTER TABLE `MODEL_REPORT_TEMPLATE_AUDIT` DISABLE KEYS */;
/*!40000 ALTER TABLE `MODEL_REPORT_TEMPLATE_AUDIT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `REVINFO`
--

DROP TABLE IF EXISTS `REVINFO`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `REVINFO` (
  `REV` int(11) NOT NULL AUTO_INCREMENT,
  `REVTSTMP` bigint(20) DEFAULT NULL,
  `REVBY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`REV`)
) ENGINE=InnoDB AUTO_INCREMENT=6978 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `REVINFO`
--

LOCK TABLES `REVINFO` WRITE;
/*!40000 ALTER TABLE `REVINFO` DISABLE KEYS */;
/*!40000 ALTER TABLE `REVINFO` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SYNDICATE_DATA_QUERY`
--

DROP TABLE IF EXISTS `SYNDICATE_DATA_QUERY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SYNDICATE_DATA_QUERY` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin DEFAULT NULL,
  `MAPPING_ID` char(36) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(50) COLLATE utf8_bin NOT NULL,
  `DESCRIPTION` varchar(200) COLLATE utf8_bin NOT NULL,
  `EXEC_SEQUENCE` int(11) NOT NULL,
  `SELECT_COMPONENT` varchar(500) COLLATE utf8_bin NOT NULL,
  `FROM_COMPONENT` varchar(200) COLLATE utf8_bin NOT NULL,
  `WHERE_COMPONENT` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `ORDER_BY_COMPONENT` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `EXEC_QUERY` varchar(3000) COLLATE utf8_bin DEFAULT NULL,
  `ROW_TYPE` varchar(12) COLLATE utf8_bin NOT NULL,
  `DATA_TYPE` varchar(20) COLLATE utf8_bin NOT NULL,
  `MAPPING_TYPE` varchar(15) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` char(36) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(36) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SYNDICATE_DATA_QUERY`
--

LOCK TABLES `SYNDICATE_DATA_QUERY` WRITE;
/*!40000 ALTER TABLE `SYNDICATE_DATA_QUERY` DISABLE KEYS */;
/*!40000 ALTER TABLE `SYNDICATE_DATA_QUERY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SYNDICATE_DATA_QUERY_AUDIT`
--

DROP TABLE IF EXISTS `SYNDICATE_DATA_QUERY_AUDIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SYNDICATE_DATA_QUERY_AUDIT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin DEFAULT NULL,
  `MAPPING_ID` char(36) COLLATE utf8_bin DEFAULT NULL,
  `NAME` varchar(50) COLLATE utf8_bin NOT NULL,
  `DESCRIPTION` varchar(200) COLLATE utf8_bin NOT NULL,
  `EXEC_SEQUENCE` int(11) NOT NULL,
  `SELECT_COMPONENT` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `FROM_COMPONENT` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `WHERE_COMPONENT` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `ORDER_BY_COMPONENT` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `EXEC_QUERY` varchar(3000) COLLATE utf8_bin DEFAULT NULL,
  `ROW_TYPE` varchar(12) COLLATE utf8_bin DEFAULT NULL,
  `DATA_TYPE` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `MAPPING_TYPE` varchar(15) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_BY` char(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID`,`REV`),
  KEY `IDX_SYNDICATE_DATA_QUERY_AUDIT_REVINFO` (`REV`),
  CONSTRAINT `FK_SYNDICATE_DATA_QUERY_AUDIT_REVINFO` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SYNDICATE_DATA_QUERY_AUDIT`
--

LOCK TABLES `SYNDICATE_DATA_QUERY_AUDIT` WRITE;
/*!40000 ALTER TABLE `SYNDICATE_DATA_QUERY_AUDIT` DISABLE KEYS */;
/*!40000 ALTER TABLE `SYNDICATE_DATA_QUERY_AUDIT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SYNDICATE_DATA_QUERY_INPUTS`
--

DROP TABLE IF EXISTS `SYNDICATE_DATA_QUERY_INPUTS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SYNDICATE_DATA_QUERY_INPUTS` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin DEFAULT NULL,
  `SYNDICATE_DATA_QUERY_ID` char(36) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `DATA_TYPE` varchar(25) COLLATE utf8_bin NOT NULL,
  `SAMPLE_VALUE` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `DATATYPE_FORMAT` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'The format of the type if applicable',
  `CREATED_BY` char(36) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(36) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_INPUTS_QUERYS` (`SYNDICATE_DATA_QUERY_ID`),
  CONSTRAINT `FK_INPUTS_QUERYS` FOREIGN KEY (`SYNDICATE_DATA_QUERY_ID`) REFERENCES `SYNDICATE_DATA_QUERY` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SYNDICATE_DATA_QUERY_INPUTS`
--

LOCK TABLES `SYNDICATE_DATA_QUERY_INPUTS` WRITE;
/*!40000 ALTER TABLE `SYNDICATE_DATA_QUERY_INPUTS` DISABLE KEYS */;
/*!40000 ALTER TABLE `SYNDICATE_DATA_QUERY_INPUTS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SYNDICATE_DATA_QUERY_INPUTS_AUDIT`
--

DROP TABLE IF EXISTS `SYNDICATE_DATA_QUERY_INPUTS_AUDIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SYNDICATE_DATA_QUERY_INPUTS_AUDIT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin DEFAULT NULL,
  `SYNDICATE_DATA_QUERY_ID` char(36) COLLATE utf8_bin DEFAULT NULL,
  `NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `DATA_TYPE` varchar(25) COLLATE utf8_bin DEFAULT NULL,
  `SAMPLE_VALUE` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `DATATYPE_FORMAT` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'The format of the type if applicable',
  `CREATED_BY` char(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID`,`REV`),
  KEY `IDX_SYNDICATE_DATA_QUERY_INPUTS_AUDIT_REVINFO` (`REV`),
  CONSTRAINT `FK_SYNDICATE_DATA_QUERY_INPUTS_AUDIT_REVINFO` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SYNDICATE_DATA_QUERY_INPUTS_AUDIT`
--

LOCK TABLES `SYNDICATE_DATA_QUERY_INPUTS_AUDIT` WRITE;
/*!40000 ALTER TABLE `SYNDICATE_DATA_QUERY_INPUTS_AUDIT` DISABLE KEYS */;
/*!40000 ALTER TABLE `SYNDICATE_DATA_QUERY_INPUTS_AUDIT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SYNDICATE_DATA_QUERY_OUTPUTS`
--

DROP TABLE IF EXISTS `SYNDICATE_DATA_QUERY_OUTPUTS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SYNDICATE_DATA_QUERY_OUTPUTS` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin DEFAULT NULL,
  `SYNDICATE_DATA_QUERY_ID` char(36) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `DATA_TYPE` varchar(25) COLLATE utf8_bin NOT NULL,
  `DATATYPE_FORMAT` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'The format of the type if applicable',
  `SEQUENCE` int(11) NOT NULL,
  `CREATED_BY` char(36) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(36) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_OUTPUTS_QUERYS` (`SYNDICATE_DATA_QUERY_ID`),
  CONSTRAINT `FK_OUTPUTS_QUERYS` FOREIGN KEY (`SYNDICATE_DATA_QUERY_ID`) REFERENCES `SYNDICATE_DATA_QUERY` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SYNDICATE_DATA_QUERY_OUTPUTS`
--

LOCK TABLES `SYNDICATE_DATA_QUERY_OUTPUTS` WRITE;
/*!40000 ALTER TABLE `SYNDICATE_DATA_QUERY_OUTPUTS` DISABLE KEYS */;
/*!40000 ALTER TABLE `SYNDICATE_DATA_QUERY_OUTPUTS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SYNDICATE_DATA_QUERY_OUTPUTS_AUDIT`
--

DROP TABLE IF EXISTS `SYNDICATE_DATA_QUERY_OUTPUTS_AUDIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SYNDICATE_DATA_QUERY_OUTPUTS_AUDIT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin DEFAULT NULL,
  `SYNDICATE_DATA_QUERY_ID` char(36) COLLATE utf8_bin DEFAULT NULL,
  `NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `DATA_TYPE` varchar(25) COLLATE utf8_bin DEFAULT NULL,
  `DATATYPE_FORMAT` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'The format of the type if applicable',
  `SEQUENCE` int(11) DEFAULT NULL,
  `CREATED_BY` char(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID`,`REV`),
  KEY `IDX_SYNDICATE_DATA_QUERY_OUTPUTS_AUDIT_REVINFO` (`REV`),
  CONSTRAINT `FK_SYNDICATE_DATA_QUERY_OUTPUTS_AUDIT_REVINFO` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SYNDICATE_DATA_QUERY_OUTPUTS_AUDIT`
--

LOCK TABLES `SYNDICATE_DATA_QUERY_OUTPUTS_AUDIT` WRITE;
/*!40000 ALTER TABLE `SYNDICATE_DATA_QUERY_OUTPUTS_AUDIT` DISABLE KEYS */;
/*!40000 ALTER TABLE `SYNDICATE_DATA_QUERY_OUTPUTS_AUDIT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SYNDICATE_DATA_QUERY_RESULT_TYPES`
--

DROP TABLE IF EXISTS `SYNDICATE_DATA_QUERY_RESULT_TYPES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SYNDICATE_DATA_QUERY_RESULT_TYPES` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin DEFAULT NULL,
  `TYPE_NAME` varchar(50) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` char(36) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(36) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `TYPE_NAME_IDX` (`TYPE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SYNDICATE_DATA_QUERY_RESULT_TYPES`
--

LOCK TABLES `SYNDICATE_DATA_QUERY_RESULT_TYPES` WRITE;
/*!40000 ALTER TABLE `SYNDICATE_DATA_QUERY_RESULT_TYPES` DISABLE KEYS */;
/*!40000 ALTER TABLE `SYNDICATE_DATA_QUERY_RESULT_TYPES` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SYNDICATE_DATA_QUERY_RESULT_TYPES_AUDIT`
--

DROP TABLE IF EXISTS `SYNDICATE_DATA_QUERY_RESULT_TYPES_AUDIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SYNDICATE_DATA_QUERY_RESULT_TYPES_AUDIT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL DEFAULT '',
  `TENANT_ID` char(36) COLLATE utf8_bin DEFAULT NULL,
  `TYPE_NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_BY` char(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID`,`REV`),
  KEY `IDX_SYNDICATE_DATA_QUERY_RESULT_TYPES_AUDIT_REVINFO` (`REV`),
  CONSTRAINT `FK_SYNDICATE_DATA_QUERY_RESULT_TYPES_AUDIT_REVINFO` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SYNDICATE_DATA_QUERY_RESULT_TYPES_AUDIT`
--

LOCK TABLES `SYNDICATE_DATA_QUERY_RESULT_TYPES_AUDIT` WRITE;
/*!40000 ALTER TABLE `SYNDICATE_DATA_QUERY_RESULT_TYPES_AUDIT` DISABLE KEYS */;
/*!40000 ALTER TABLE `SYNDICATE_DATA_QUERY_RESULT_TYPES_AUDIT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TRANSPORT_PARAMETER_VALUES`
--

DROP TABLE IF EXISTS `TRANSPORT_PARAMETER_VALUES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TRANSPORT_PARAMETER_VALUES` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` varchar(45) COLLATE utf8_bin NOT NULL,
  `TRANSPORT_NAME` varchar(45) COLLATE utf8_bin NOT NULL,
  `PARAMETER_NAME` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `PARAMETER_VALUE` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_BY` char(36) COLLATE utf8_bin NOT NULL COMMENT 'User created the record.',
  `CREATED_ON` bigint(20) NOT NULL COMMENT 'Record created time.',
  `LAST_UPDATED_BY` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'User last updated the record.',
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL COMMENT 'Record last updated time.',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TRANSPORT_PARAMETER_VALUES`
--

LOCK TABLES `TRANSPORT_PARAMETER_VALUES` WRITE;
/*!40000 ALTER TABLE `TRANSPORT_PARAMETER_VALUES` DISABLE KEYS */;
/*!40000 ALTER TABLE `TRANSPORT_PARAMETER_VALUES` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UMG_RUNTIME_TRANSACTION`
--

DROP TABLE IF EXISTS `UMG_RUNTIME_TRANSACTION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UMG_RUNTIME_TRANSACTION` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Tenant code for the record',
  `CLIENT_TRANSACTION_ID` varchar(250) COLLATE utf8_bin NOT NULL COMMENT 'Transaction Number',
  `LIBRARY_NAME` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'Name of the model library, given by user as TAG.',
  `VERSION_NAME` varchar(100) COLLATE utf8_bin NOT NULL COMMENT 'umg version used for the transaction - Tenant Model Name in UI',
  `MAJOR_VERSION` int(10) NOT NULL COMMENT 'Major version',
  `MINOR_VERSION` int(10) NOT NULL COMMENT 'Minor version',
  `STATUS` varchar(20) COLLATE utf8_bin NOT NULL COMMENT 'Transaction Status',
  `TENANT_INPUT` mediumblob NOT NULL,
  `TENANT_OUTPUT` mediumblob NOT NULL COMMENT 'The actual tenant output.',
  `MODEL_INPUT` mediumblob NOT NULL,
  `MODEL_OUTPUT` mediumblob NOT NULL COMMENT 'The actual output from the model.',
  `RUN_AS_OF_DATE` bigint(20) NOT NULL COMMENT 'Run Date From',
  `RUNTIME_CALL_START` bigint(20) DEFAULT NULL COMMENT 'Runtime Call Start Time',
  `RUNTIME_CALL_END` bigint(20) DEFAULT NULL COMMENT 'Runtime Call End Time',
  `MODEL_CALL_START` bigint(20) DEFAULT NULL COMMENT 'Model Call Start Time',
  `MODEL_CALL_END` bigint(20) DEFAULT NULL COMMENT 'Model Call End Time',
  `IS_TEST` tinyint(4) NOT NULL COMMENT 'Transaction run during test bed',
  `ACCEPTABLEVALUES_VALIDATION` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Flag to allow acceptable validation',
  `OP_VALIDATION` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Flag to allow model output validation',
  `CREATED_BY` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL COMMENT 'Record created time.',
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL COMMENT 'Record last updated time.',
  `ERROR_CODE` char(10) COLLATE utf8_bin DEFAULT NULL COMMENT 'Error code received while execution Model.',
  `ERROR_DESCRIPTION` blob,
  `MODEL_EXECUTION_TIME` bigint(20) DEFAULT NULL,
  `MODELET_EXECUTION_TIME` bigint(20) DEFAULT NULL,
  `ME2_WAITING_TIME` bigint(20) DEFAULT NULL,
  `TRANSACTION_MODE` varchar(50) COLLATE utf8_bin DEFAULT 'Online',
  `CPU_USAGE` double DEFAULT '1',
  `FREE_MEMORY` varchar(50) COLLATE utf8_bin DEFAULT '0',
  `FREE_MEMORY_AT_START` varchar(50) COLLATE utf8_bin DEFAULT '0',
  `CPU_USAGE_AT_START` double DEFAULT '0',
  `IP_AND_PORT` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `POOL_NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `NO_OF_ATTEMPTS` int(2) DEFAULT NULL,
  `MODEL_EXEC_ENV_NAME` varchar(50) COLLATE utf8_bin DEFAULT 'R-3.2.1',
  `R_SERVE_PORT` int(10) DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `UMG_RUNTIME_TRANSACTION_INDEX_1` (`CREATED_ON`,`VERSION_NAME`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UMG_RUNTIME_TRANSACTION`
--

LOCK TABLES `UMG_RUNTIME_TRANSACTION` WRITE;
/*!40000 ALTER TABLE `UMG_RUNTIME_TRANSACTION` DISABLE KEYS */;
/*!40000 ALTER TABLE `UMG_RUNTIME_TRANSACTION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UMG_RUNTIME_TRANSACTION_AUDIT`
--

DROP TABLE IF EXISTS `UMG_RUNTIME_TRANSACTION_AUDIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UMG_RUNTIME_TRANSACTION_AUDIT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'Tenant code for the record',
  `CLIENT_TRANSACTION_ID` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'Transaction Number',
  `LIBRARY_NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'Name of the model library, given by user as TAG.',
  `VERSION_NAME` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT 'umg version used for the transaction - Tenant Model Name in UI',
  `MAJOR_VERSION` int(10) DEFAULT NULL COMMENT 'Major version',
  `MINOR_VERSION` int(10) DEFAULT NULL COMMENT 'Minor version',
  `STATUS` varchar(10) COLLATE utf8_bin DEFAULT NULL COMMENT 'Transaction Status',
  `TENANT_INPUT` mediumblob NOT NULL,
  `TENANT_OUTPUT` mediumblob COMMENT 'The actual tenant output.',
  `MODEL_INPUT` mediumblob NOT NULL,
  `MODEL_OUTPUT` mediumblob COMMENT 'The actual output from the model.',
  `RUN_AS_OF_DATE` bigint(20) DEFAULT NULL COMMENT 'Run Date From',
  `RUNTIME_CALL_START` bigint(20) DEFAULT NULL COMMENT 'Runtime Call Start Time',
  `RUNTIME_CALL_END` bigint(20) DEFAULT NULL COMMENT 'Runtime Call End Time',
  `MODEL_CALL_START` bigint(20) DEFAULT NULL COMMENT 'Model Call Start Time',
  `MODEL_CALL_END` bigint(20) DEFAULT NULL COMMENT 'Model Call End Time',
  `IS_TEST` tinyint(4) DEFAULT NULL COMMENT 'Transaction run during test bed',
  `CREATED_BY` char(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `ERROR_CODE` char(10) COLLATE utf8_bin DEFAULT NULL COMMENT 'Error code received while execution Model.',
  `ERROR_DESCRIPTION` varchar(1500) COLLATE utf8_bin DEFAULT NULL COMMENT 'Error description while Model execution.',
  `MODEL_EXECUTION_TIME` bigint(20) DEFAULT NULL,
  `MODELET_EXECUTION_TIME` bigint(20) DEFAULT NULL,
  `ME2_WAITING_TIME` bigint(20) DEFAULT NULL,
  `TRANSACTION_MODE` varchar(50) COLLATE utf8_bin DEFAULT 'Online',
  PRIMARY KEY (`ID`),
  KEY `IDX_UMG_RUNTIME_TRANSACTION_AUDIT_REVINFOds` (`REV`),
  CONSTRAINT `FK_UMG_RUNTIME_TRANSACTION_AUDIT_REVINFOds` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UMG_RUNTIME_TRANSACTION_AUDIT`
--

LOCK TABLES `UMG_RUNTIME_TRANSACTION_AUDIT` WRITE;
/*!40000 ALTER TABLE `UMG_RUNTIME_TRANSACTION_AUDIT` DISABLE KEYS */;
/*!40000 ALTER TABLE `UMG_RUNTIME_TRANSACTION_AUDIT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UMG_RUNTIME_TRANSACTION_AUDIT_BK`
--

DROP TABLE IF EXISTS `UMG_RUNTIME_TRANSACTION_AUDIT_BK`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UMG_RUNTIME_TRANSACTION_AUDIT_BK` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'Tenant code for the record',
  `CLIENT_TRANSACTION_ID` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'Transaction Number',
  `LIBRARY_NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'Name of the model library, given by user as TAG.',
  `VERSION_NAME` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT 'umg version used for the transaction - Tenant Model Name in UI',
  `MAJOR_VERSION` int(10) DEFAULT NULL COMMENT 'Major version',
  `MINOR_VERSION` int(10) DEFAULT NULL COMMENT 'Minor version',
  `STATUS` varchar(10) COLLATE utf8_bin DEFAULT NULL COMMENT 'Transaction Status',
  `TENANT_INPUT` mediumblob NOT NULL,
  `TENANT_OUTPUT` mediumblob COMMENT 'The actual tenant output.',
  `MODEL_INPUT` mediumblob NOT NULL,
  `MODEL_OUTPUT` mediumblob COMMENT 'The actual output from the model.',
  `RUN_AS_OF_DATE` bigint(20) DEFAULT NULL COMMENT 'Run Date From',
  `RUNTIME_CALL_START` bigint(20) DEFAULT NULL COMMENT 'Runtime Call Start Time',
  `RUNTIME_CALL_END` bigint(20) DEFAULT NULL COMMENT 'Runtime Call End Time',
  `MODEL_CALL_START` bigint(20) DEFAULT NULL COMMENT 'Model Call Start Time',
  `MODEL_CALL_END` bigint(20) DEFAULT NULL COMMENT 'Model Call End Time',
  `IS_TEST` tinyint(4) DEFAULT NULL COMMENT 'Transaction run during test bed',
  `CREATED_BY` char(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `ERROR_CODE` char(10) COLLATE utf8_bin DEFAULT NULL COMMENT 'Error code received while execution Model.',
  `ERROR_DESCRIPTION` varchar(1500) COLLATE utf8_bin DEFAULT NULL COMMENT 'Error description while Model execution.',
  `MODEL_EXECUTION_TIME` bigint(20) DEFAULT NULL,
  `MODELET_EXECUTION_TIME` bigint(20) DEFAULT NULL,
  `TRANSACTION_MODE` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `ME2_WAITING_TIME` bigint(20) DEFAULT NULL,
  `MODEL_EXEC_ENV_NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_UMG_RUNTIME_TRANSACTION_AUDIT_REVINFO` (`REV`),
  CONSTRAINT `FK_UMG_RUNTIME_TRANSACTION_AUDIT_REVINFO` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UMG_RUNTIME_TRANSACTION_AUDIT_BK`
--

LOCK TABLES `UMG_RUNTIME_TRANSACTION_AUDIT_BK` WRITE;
/*!40000 ALTER TABLE `UMG_RUNTIME_TRANSACTION_AUDIT_BK` DISABLE KEYS */;
/*!40000 ALTER TABLE `UMG_RUNTIME_TRANSACTION_AUDIT_BK` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UMG_RUNTIME_TRANSACTION_BK`
--

DROP TABLE IF EXISTS `UMG_RUNTIME_TRANSACTION_BK`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UMG_RUNTIME_TRANSACTION_BK` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Tenant code for the record',
  `CLIENT_TRANSACTION_ID` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'Transaction Number',
  `LIBRARY_NAME` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'Name of the model library, given by user as TAG.',
  `VERSION_NAME` varchar(100) COLLATE utf8_bin NOT NULL COMMENT 'umg version used for the transaction - Tenant Model Name in UI',
  `MAJOR_VERSION` int(10) NOT NULL COMMENT 'Major version',
  `MINOR_VERSION` int(10) NOT NULL COMMENT 'Minor version',
  `STATUS` varchar(20) COLLATE utf8_bin NOT NULL COMMENT 'Transaction Status',
  `TENANT_INPUT` mediumblob,
  `TENANT_OUTPUT` mediumblob COMMENT 'The actual tenant output.',
  `MODEL_INPUT` mediumblob,
  `MODEL_OUTPUT` mediumblob COMMENT 'The actual output from the model.',
  `RUN_AS_OF_DATE` bigint(20) NOT NULL COMMENT 'Run Date From',
  `RUNTIME_CALL_START` bigint(20) DEFAULT NULL COMMENT 'Runtime Call Start Time',
  `RUNTIME_CALL_END` bigint(20) DEFAULT NULL COMMENT 'Runtime Call End Time',
  `MODEL_CALL_START` bigint(20) DEFAULT NULL COMMENT 'Model Call Start Time',
  `MODEL_CALL_END` bigint(20) DEFAULT NULL COMMENT 'Model Call End Time',
  `IS_TEST` tinyint(4) NOT NULL COMMENT 'Transaction run during test bed',
  `OP_VALIDATION` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Flag to allow model output validation',
  `CREATED_BY` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL COMMENT 'Record created time.',
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL COMMENT 'Record last updated time.',
  `ERROR_CODE` char(10) COLLATE utf8_bin DEFAULT NULL COMMENT 'Error code received while execution Model.',
  `ERROR_DESCRIPTION` blob,
  `MODEL_EXECUTION_TIME` bigint(20) DEFAULT NULL,
  `MODELET_EXECUTION_TIME` bigint(20) DEFAULT NULL,
  `ME2_WAITING_TIME` bigint(20) DEFAULT NULL,
  `TRANSACTION_MODE` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `CPU_USAGE` double DEFAULT '1',
  `FREE_MEMORY` varchar(50) COLLATE utf8_bin DEFAULT '0',
  `FREE_MEMORY_AT_START` varchar(50) COLLATE utf8_bin DEFAULT '0',
  `CPU_USAGE_AT_START` double DEFAULT '0',
  `IP_AND_PORT` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `POOL_NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `NO_OF_ATTEMPTS` int(2) DEFAULT NULL,
  `ACCEPTABLEVALUES_VALIDATION` varchar(5000) COLLATE utf8_bin DEFAULT NULL,
  `MODEL_EXEC_ENV_NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UMG_RUNTIME_TRANSACTION_BK`
--

LOCK TABLES `UMG_RUNTIME_TRANSACTION_BK` WRITE;
/*!40000 ALTER TABLE `UMG_RUNTIME_TRANSACTION_BK` DISABLE KEYS */;
/*!40000 ALTER TABLE `UMG_RUNTIME_TRANSACTION_BK` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UMG_VERSION`
--

DROP TABLE IF EXISTS `UMG_VERSION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UMG_VERSION` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Tenant code for the record',
  `NAME` varchar(100) COLLATE utf8_bin NOT NULL COMMENT 'Name of the UMG verison',
  `DESCRIPTION` varchar(250) COLLATE utf8_bin NOT NULL COMMENT 'Description of UMG Version',
  `MAJOR_VERSION` int(10) NOT NULL COMMENT 'Major version',
  `MINOR_VERSION` int(10) NOT NULL COMMENT 'Minor version',
  `MAPPING_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Mapping used by this version',
  `MODEL_LIBRARY_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'MODEL library used by this version',
  `STATUS` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `VERSION_DESCRIPTION` varchar(200) COLLATE utf8_bin NOT NULL COMMENT 'Description of major or minor version',
  `PUBLISHED_ON` bigint(20) DEFAULT NULL COMMENT 'UMG version published time.',
  `PUBLISHED_BY` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'User who published the umg version',
  `DEACTIVATED_ON` bigint(20) DEFAULT NULL COMMENT 'UMG version deactivated time.',
  `DEACTIVATED_BY` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'User who deactivated the umg version',
  `CREATED_BY` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL COMMENT 'Record created time.',
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL COMMENT 'Record last updated time.',
  `MODEL_TYPE` varchar(10) COLLATE utf8_bin NOT NULL DEFAULT 'Online',
  `REQUESTED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `REQUESTED_ON` bigint(20) DEFAULT NULL,
  `EMAIL_APPRROVAL` smallint(6) DEFAULT '0',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UN_UMG_VERSION` (`TENANT_ID`,`NAME`,`MAJOR_VERSION`,`MINOR_VERSION`),
  KEY `FK_IDX_UMG_VERSION_MAPPING` (`MAPPING_ID`),
  KEY `FK_IDX_UMG_VERSION_MODEL_LIBRARY` (`MODEL_LIBRARY_ID`),
  KEY `umg_version_index_1` (`MODEL_LIBRARY_ID`) USING BTREE,
  CONSTRAINT `FK_UMG_VERSION_MAPPING` FOREIGN KEY (`MAPPING_ID`) REFERENCES `MAPPING` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_UMG_VERSION_MODEL_LIBRARY` FOREIGN KEY (`MODEL_LIBRARY_ID`) REFERENCES `MODEL_LIBRARY` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UMG_VERSION`
--

LOCK TABLES `UMG_VERSION` WRITE;
/*!40000 ALTER TABLE `UMG_VERSION` DISABLE KEYS */;
/*!40000 ALTER TABLE `UMG_VERSION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UMG_VERSION_AUDIT`
--

DROP TABLE IF EXISTS `UMG_VERSION_AUDIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UMG_VERSION_AUDIT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'Tenant code for the record',
  `NAME` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT 'Name of the UMG verison',
  `DESCRIPTION` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT 'Description of UMG Version',
  `MAJOR_VERSION` int(10) DEFAULT NULL COMMENT 'Major version',
  `MINOR_VERSION` int(10) DEFAULT NULL COMMENT 'Minor version',
  `MAPPING_ID` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'Mapping used by this version',
  `MODEL_LIBRARY_ID` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'MODEL library used by this version',
  `STATUS` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `VERSION_DESCRIPTION` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT 'Description of major or minor version',
  `PUBLISHED_ON` bigint(20) DEFAULT NULL COMMENT 'UMG version published time.',
  `PUBLISHED_BY` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'User who published the umg version',
  `DEACTIVATED_ON` bigint(20) DEFAULT NULL COMMENT 'UMG version deactivated time.',
  `DEACTIVATED_BY` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'User who deactivated the umg version',
  `CREATED_BY` char(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `MODEL_TYPE` varchar(10) COLLATE utf8_bin NOT NULL DEFAULT 'Bulk',
  `REQUESTED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `REQUESTED_ON` bigint(20) DEFAULT NULL,
  `EMAIL_APPRROVAL` smallint(6) DEFAULT '0',
  PRIMARY KEY (`ID`,`REV`),
  KEY `IDX_UMG_VERSION_AUDIT_REVINFO` (`REV`),
  CONSTRAINT `FK_UMG_VERSION_AUDIT_REVINFO` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UMG_VERSION_AUDIT`
--

LOCK TABLES `UMG_VERSION_AUDIT` WRITE;
/*!40000 ALTER TABLE `UMG_VERSION_AUDIT` DISABLE KEYS */;
/*!40000 ALTER TABLE `UMG_VERSION_AUDIT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `USAGE_SEARCH_REQUEST_CANCEL`
--

DROP TABLE IF EXISTS `USAGE_SEARCH_REQUEST_CANCEL`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `USAGE_SEARCH_REQUEST_CANCEL` (
  `ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Unique Identifier',
  `TENANT_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Tenant code for the record',
  `IS_USAGE_SEARCH_CANCEL` tinyint(4) NOT NULL COMMENT 'Flag to indicate whether request has been cancelled or not',
  `CREATED_BY` char(36) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(36) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='contains cancellation status for all usage search requests';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `USAGE_SEARCH_REQUEST_CANCEL`
--

LOCK TABLES `USAGE_SEARCH_REQUEST_CANCEL` WRITE;
/*!40000 ALTER TABLE `USAGE_SEARCH_REQUEST_CANCEL` DISABLE KEYS */;
/*!40000 ALTER TABLE `USAGE_SEARCH_REQUEST_CANCEL` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-02-07 18:20:04
