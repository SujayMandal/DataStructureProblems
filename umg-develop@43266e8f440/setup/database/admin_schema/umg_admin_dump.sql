-- MySQL dump 10.13  Distrib 5.6.24, for Win64 (x86_64)
--
-- Host: localhost    Database: umg_admin
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

SET @@GLOBAL.GTID_PURGED='995eb99f-e348-11e4-9245-00ffbc72cbd1:1-118270,
995eb99f-e348-11e4-9245-00ffbc73cbd1:1-33';

--
-- Table structure for table `ADDRESS`
--

DROP TABLE IF EXISTS `ADDRESS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ADDRESS` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Tenant ID for the address',
  `ADDRESS_1` varchar(200) COLLATE utf8_bin NOT NULL,
  `ADDRESS_2` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `CITY` varchar(200) COLLATE utf8_bin NOT NULL,
  `STATE` varchar(200) COLLATE utf8_bin NOT NULL,
  `ZIP` varchar(6) COLLATE utf8_bin NOT NULL,
  `COUNTRY` varchar(200) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ADDRESS_TENANT` (`TENANT_ID`),
  CONSTRAINT `FK_ADDRESS_TENANT` FOREIGN KEY (`TENANT_ID`) REFERENCES `TENANT` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ADDRESS`
--

LOCK TABLES `ADDRESS` WRITE;
/*!40000 ALTER TABLE `ADDRESS` DISABLE KEYS */;
INSERT INTO `ADDRESS` VALUES ('8541c5c5-d22a-41f5-b31c-584091695e71','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','dasasdas','dasdasdd','dasdasda','adasdad','412342','sdasdasda','anil.kamath',1495029829649,'anil.kamath',1495029829649);
/*!40000 ALTER TABLE `ADDRESS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AUTHTOKEN`
--

DROP TABLE IF EXISTS `AUTHTOKEN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AUTHTOKEN` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Tenant ID',
  `AUTH_CODE` varchar(64) COLLATE utf8_bin NOT NULL,
  `ACTIVE_FROM` bigint(20) DEFAULT NULL,
  `ACTIVE_UNTIL` bigint(20) DEFAULT NULL,
  `STATUS` varchar(100) COLLATE utf8_bin NOT NULL,
  `COMMENT` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `CREATED_BY` varchar(100) COLLATE utf8_bin NOT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_AUTHTOKEN_TENANT` (`TENANT_ID`),
  CONSTRAINT `FK_AUTHTOKEN_TENANT` FOREIGN KEY (`TENANT_ID`) REFERENCES `TENANT` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AUTHTOKEN`
--

LOCK TABLES `AUTHTOKEN` WRITE;
/*!40000 ALTER TABLE `AUTHTOKEN` DISABLE KEYS */;
INSERT INTO `AUTHTOKEN` VALUES ('53a77ddf-2702-11e6-8a6e-00ffbc73cbd1','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','cBt/PQa4YTajb4dO+LNurq/oWcQn+bu8ew5QHwUcdJKr0yFBCy3rqrYiaNG/KBLg',1464331168000,1495867168000,'Active','Tenant Onboarded',1464680162,'SYSTEM','SYSTEM',1464680162),('bf381d6b-c2ac-4b02-9e38-cd89e7413fc1','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','q539KFGCs1oI9qQ5jWw67jLgq6eB4OlZQP2y46i6dWvNPLr6W6SxtZdwLzCTzbqC',1495030645977,1526480245977,'Active','rwerwer',1495029829650,'anil.kamath','anil.kamath',1495030646065);
/*!40000 ALTER TABLE `AUTHTOKEN` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `COMMAND`
--

DROP TABLE IF EXISTS `COMMAND`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `COMMAND` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(100) COLLATE utf8_bin NOT NULL COMMENT 'Name of the command',
  `DESCRIPTION` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT 'Description of the command',
  `EXECUTION_SEQUENCE` int(36) NOT NULL COMMENT 'Command sequence id',
  `PROCESS` varchar(100) COLLATE utf8_bin NOT NULL COMMENT 'Name of the command',
  `CREATED_BY` char(36) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(36) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UN_COMMAND_SEQUENCE` (`NAME`,`PROCESS`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `COMMAND`
--

LOCK TABLES `COMMAND` WRITE;
/*!40000 ALTER TABLE `COMMAND` DISABLE KEYS */;
INSERT INTO `COMMAND` VALUES ('15c27bfe-4491-41a8-98d5-276735f36bc9','createModel','Create model definition.',7,'CREATE','SYSTEM',12354856456,NULL,NULL),('22d294bf-85b9-4d60-8e0f-ed0d8a36da2a','symanticCheckModelIOXml','Symantic validation of uploaded model definiition file.',5,'CREATE','SYSTEM',12354856456,NULL,NULL),('28483ea2-740d-4b3c-b858-27afd0326944','createModelLibrary','Create model library command.',3,'CREATE','SYSTEM',12354856456,NULL,NULL),('3528F744-40A1-4779-89C6-9B5595E9D019','convertExcelToXml','Conversion of Excel to Xml',4,'CREATE','SYSTEM',12354856456,NULL,NULL),('3528F744-40A1-4779-89C6-9B5595E9D089','validateRManifestFile','Validating of Manifest file',1,'CREATE','SYSTEM',12354856456,NULL,NULL),('46d54377-5e5e-4cdf-a0cd-4b11ef5c096b','testVersion','Test version.',13,'CREATE1','SYSTEM',12354856456,NULL,NULL),('5c6c2228-5e25-4d6b-b989-0e41b00c4eec','generateTestInput','Generate test input json.',12,'CREATE','SYSTEM',12354856456,NULL,NULL),('5eb9aede-1c06-40ea-bd33-bd436b314816','createMapping','Create default Mapping.',8,'CREATE','SYSTEM',12354856456,NULL,NULL),('640f5323-1f7d-4d33-98c2-b753291fc375','createVersion','Create default version.',9,'CREATE','SYSTEM',12354856456,NULL,NULL),('9f7a95a9-dfad-489b-a1bd-420bf589c743','validateLibraryChecksum','Validate checksum of the uploaded model library jar file.',2,'CREATE','SYSTEM',12354856456,NULL,NULL),('A37E5EFF-9CBF-4FC0-8F25-B71C5318FB21','deleteRModelArtifacts','delete R Model from temp path and DB',14,'CREATE','SYSTEM',0,NULL,NULL),('A37E5EFF-9CBF-4FC0-8F25-B71C5318FB22','validateReportTemplate','Validate Report Template',10,'CREATE','SYSTEM',12354856456,NULL,NULL),('A37E5EFF-9CBF-4FC0-8F25-B71C5318FB23','saveReportTemplate','Save Report Template',11,'CREATE','SYSTEM',12354856456,NULL,NULL),('A37E5EFF-9CBF-4FC0-8F25-B71C5318FB24','generateModelReport','Generate Report Template (this should be last command in the sequence)',15,'CREATE','SYSTEM',12354856456,NULL,NULL),('ebfed01f-30a8-460c-95fc-3f7215df2f65','validateModelIOXml','Valiadate uploaded model definition file..',6,'CREATE','SYSTEM',12354856456,NULL,NULL);
/*!40000 ALTER TABLE `COMMAND` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MODELET_RESTART_CONFIG`
--

DROP TABLE IF EXISTS `MODELET_RESTART_CONFIG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MODELET_RESTART_CONFIG` (
  `ID` varchar(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `MODELNAME_VERSION` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `RESTART_COUNT` int(5) DEFAULT NULL,
  `CREATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_ON` bigint(20) DEFAULT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_modelet_restart_tenant` (`TENANT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MODELET_RESTART_CONFIG`
--

LOCK TABLES `MODELET_RESTART_CONFIG` WRITE;
/*!40000 ALTER TABLE `MODELET_RESTART_CONFIG` DISABLE KEYS */;
/*!40000 ALTER TABLE `MODELET_RESTART_CONFIG` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MODELET_RESTART_CONFIG_AUDIT`
--

DROP TABLE IF EXISTS `MODELET_RESTART_CONFIG_AUDIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MODELET_RESTART_CONFIG_AUDIT` (
  `ID` varchar(36) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `MODELNAME_VERSION` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `RESTART_COUNT` int(5) DEFAULT NULL,
  `CREATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_ON` bigint(20) DEFAULT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MODELET_RESTART_CONFIG_AUDIT`
--

LOCK TABLES `MODELET_RESTART_CONFIG_AUDIT` WRITE;
/*!40000 ALTER TABLE `MODELET_RESTART_CONFIG_AUDIT` DISABLE KEYS */;
/*!40000 ALTER TABLE `MODELET_RESTART_CONFIG_AUDIT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MODEL_EXECUTION_ENVIRONMENTS`
--

DROP TABLE IF EXISTS `MODEL_EXECUTION_ENVIRONMENTS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MODEL_EXECUTION_ENVIRONMENTS` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `EXECUTION_ENVIRONMENT` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'Model execution environment.',
  `ENVIRONMENT_VERSION` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'Version of the execution environment.',
  `CREATED_BY` char(50) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(50) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `NAME` varchar(50) COLLATE utf8_bin NOT NULL,
  `IS_ACTIVE` char(1) COLLATE utf8_bin NOT NULL DEFAULT 'F',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT COMMENT='Table to store Model execution environment and version';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MODEL_EXECUTION_ENVIRONMENTS`
--

LOCK TABLES `MODEL_EXECUTION_ENVIRONMENTS` WRITE;
/*!40000 ALTER TABLE `MODEL_EXECUTION_ENVIRONMENTS` DISABLE KEYS */;
INSERT INTO `MODEL_EXECUTION_ENVIRONMENTS` VALUES ('1ad96ce2-3b9c-11e7-b3a5-00ffbc73cbd1','R','3.3.2','nagamani.basa',1479800282,'nagamani.basa',1479800292,'R-3.3.2','T'),('C7642E72-DE1B-4E5B-B1B1-0C70E1B3BCA6','Matlab','7.16','nagamani.basa',1433246416,'nagamani.basa',1433246416,'Matlab-7.16','F'),('F6ACD2A3-D78D-4E36-A2D8-7420F7CF7537','R','3.2.1','nagamani.basa',1433246416,'nagamani.basa',1433246416,'R-3.2.1','F'),('d39d8c31-bb83-11e6-91b6-00ffbc73cbd1','Excel','2013','sujay.mandal',1479990810,'sujay.mandal',1479990810,'Excel-2013','F');
/*!40000 ALTER TABLE `MODEL_EXECUTION_ENVIRONMENTS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MODEL_EXECUTION_ENVIRONMENTS_AUDIT`
--

DROP TABLE IF EXISTS `MODEL_EXECUTION_ENVIRONMENTS_AUDIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MODEL_EXECUTION_ENVIRONMENTS_AUDIT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `EXECUTION_ENVIRONMENT` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'Model execution environment.',
  `ENVIRONMENT_VERSION` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'Version of the execution environment.',
  `CREATED_BY` char(50) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_ON` bigint(20) DEFAULT NULL,
  `LAST_UPDATED_BY` char(50) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT COMMENT='Table to store Model execution language and descriptions';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MODEL_EXECUTION_ENVIRONMENTS_AUDIT`
--

LOCK TABLES `MODEL_EXECUTION_ENVIRONMENTS_AUDIT` WRITE;
/*!40000 ALTER TABLE `MODEL_EXECUTION_ENVIRONMENTS_AUDIT` DISABLE KEYS */;
/*!40000 ALTER TABLE `MODEL_EXECUTION_ENVIRONMENTS_AUDIT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MODEL_EXEC_PACKAGES`
--

DROP TABLE IF EXISTS `MODEL_EXEC_PACKAGES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MODEL_EXEC_PACKAGES` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `PACKAGE_NAME` varchar(200) COLLATE utf8_bin NOT NULL,
  `PACKAGE_FOLDER` varchar(200) COLLATE utf8_bin NOT NULL,
  `PACKAGE_VERSION` varchar(50) COLLATE utf8_bin NOT NULL,
  `PACKAGE_TYPE` varchar(50) COLLATE utf8_bin NOT NULL,
  `COMPILED_OS` varchar(50) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` char(50) COLLATE utf8_bin NOT NULL,
  `MODEL_EXEC_ENV_NAME` varchar(50) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(50) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `EXECUTION_ENVIRONMENT` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT 'Linux',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UNIQUE_PACKAGE_NAME` (`PACKAGE_NAME`,`MODEL_EXEC_ENV_NAME`),
  UNIQUE KEY `PACKAGE_FOLDER_VERSION_ENV_CONSTRAINT` (`PACKAGE_FOLDER`,`PACKAGE_VERSION`,`EXECUTION_ENVIRONMENT`,`MODEL_EXEC_ENV_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT COMMENT='Table to store model execution environment base packages';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MODEL_EXEC_PACKAGES`
--

LOCK TABLES `MODEL_EXEC_PACKAGES` WRITE;
/*!40000 ALTER TABLE `MODEL_EXEC_PACKAGES` DISABLE KEYS */;
INSERT INTO `MODEL_EXEC_PACKAGES` VALUES ('1','dasdas','dsadas','dasd','dasd','dasd','dasd','dasdas',5345345,NULL,NULL,'Linux');
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
  `EXECUTION_ENVIRONMENT` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT 'Linux'
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
-- Table structure for table `MODEL_IMPLEMENTATION_TYPE`
--

DROP TABLE IF EXISTS `MODEL_IMPLEMENTATION_TYPE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MODEL_IMPLEMENTATION_TYPE` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `IMPLEMENTATION` varchar(45) COLLATE utf8_bin NOT NULL,
  `TYPE_XSD` blob,
  `CREATED_BY` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MODEL_IMPLEMENTATION_TYPE`
--

LOCK TABLES `MODEL_IMPLEMENTATION_TYPE` WRITE;
/*!40000 ALTER TABLE `MODEL_IMPLEMENTATION_TYPE` DISABLE KEYS */;
/*!40000 ALTER TABLE `MODEL_IMPLEMENTATION_TYPE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NOTIFICATION_EMAIL_TEMPLATE`
--

DROP TABLE IF EXISTS `NOTIFICATION_EMAIL_TEMPLATE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NOTIFICATION_EMAIL_TEMPLATE` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(64) COLLATE utf8_bin NOT NULL,
  `DESCRIPTION` varchar(256) COLLATE utf8_bin NOT NULL DEFAULT '',
  `BODY_DEFINITION` mediumblob NOT NULL COMMENT 'Mail Template Definition',
  `SUBJECT_DEFINITION` varchar(256) COLLATE utf8_bin NOT NULL,
  `IS_ACTIVE` int(10) NOT NULL DEFAULT '1',
  `MAJOR_VERSION` int(11) NOT NULL DEFAULT '1',
  `MAIL_CONTENT_TYPE` varchar(64) COLLATE utf8_bin NOT NULL COMMENT 'MIME Message Types',
  `CREATED_BY` varchar(32) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `NOTIFICATION_EVENT_ID` char(36) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NOTIFICATION_EMAIL_TEMPLATE`
--

LOCK TABLES `NOTIFICATION_EMAIL_TEMPLATE` WRITE;
/*!40000 ALTER TABLE `NOTIFICATION_EMAIL_TEMPLATE` DISABLE KEYS */;
INSERT INTO `NOTIFICATION_EMAIL_TEMPLATE` VALUES ('1D5ED0C3-C206-4AD3-81CB-896FCCFD8D81','MODEL PUBLISH MAIL TEMPLATE','MODEL PUBLISH MAIL TEMPLATE','<html> <body style=\"font-family: Calibri;\">Hi, <br><br>Following Model has been published in $environment <ul><li>Model Name: $modelName</li><li>Model Published Timestamp: $publishedDate</li><li>Tenant Name: $tenantName</li><li>Approver Name: $publisherName</li> </ul><b>Regards,</b><br><b>REALAnalytics Team</b> </body></html>','REALAnalytics $environment: $modelName $modelVersion model published',1,1,'MIME','System',1234567890,NULL,NULL,'9FE824C2-5FB4-4C97-AB47-4FA2E33A1C8B'),('1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','RUNTIME FAILURE MAIL TEMPLATE','RUNTIME FAILURE MAIL TEMPLATE','<html><style>table, th, td {border: 1px solid black;border-collapse: collapse;}th, td {padding: 5px;text-align: center;}th {background-color: #F5F5F5;}</style><body style=\"font-family: Calibri;\">Hi,<br><br>Following error has been encountered during execution in $environment<ul><li> Tenant Transaction Id: $transactionId </li><li> Execution Time Stamp: $executionTime </li><li> Error Details: $errorCode $errorMessage </li><li> Model Name: $modelName </li><li> Model Version: $modelVersion </li><li> Tenant Name: $tenantName </li></ul> Modelet Status at  $executionTime , Refer table: #set( $count = 1 )<TABLE><TR><TH>Sl no.</TH><TH>Modelet</TH><TH>Pool Name</TH><TH>Status</TH>#foreach( $modelet in $modeletList)</TR><TR><TD>$count</TD><TD>$modelet.host :$modelet.port </TD><TD>$modelet.poolName</TD><TD>$modelet.modeletStatus</TD></TR> #set( $count = $count + 1 ) #end</TABLE><br><br><b>Regards,</b><br><b>REALAnalytics Team</b></body></html>','REALAnalytics $environment: Transaction error $errorCode for $tenantName tenant',1,1,'MIME','System',1234567890,NULL,NULL,'9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B'),('1D5ED0C3-C206-4AD3-81CB-896FCCFD8D83','MODEL APPROVAL EVENT TEMPLATE','MODEL APPROVAL EVENT TEMPLATE','<html> <body style=\"font-family: Calibri;\">Hi,<br><br>Following Model is awaiting your approval for publishing in $environment<ul><li>Model Name: $modelName</li><li>Model Version: $modelVersion</li><li>Tenant Name: $tenantName</li><li>Requestor Name: $publisherName</li></ul> Please refer the attached Release Notes for further details about the model. <br> <br>Click on the following link to APPROVE Model for publishing. <br><br> <a href=\"$modelApprovalURL\">Approve Publish Request</a> <br><br>Please Note: For security reasons, do not share this email.<br><br><b>Regards,</b><br><b>REALAnalytics Team</b> </body></html>','REALAnalytics $environment: Approval required for $modelName$modelVersion model publishing',1,1,'MIME','System',1234567890,NULL,NULL,'9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B'),('1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','NEW TENANT ADDED TEMPLATE','NEW TENANT ADDED EVENT TEMPLATE','<html> <body style=\"font-family: Calibri;\">Hi, <br><br>Following Tenant has been onboarded in $environment <ul><li> Tenant Name: $tenantName </li><li> Tenant Code : $tenantCode </li><li> Batch Enabled:$batchEnabled </li><li> Bulk Enabled : $bulkEnabled </li><li> Email Notifications Enabled : $emailNotificationsEnabled </li><li> Tenant Onboarded on : $tenantOnboardedOn </li><li> Tenant Onboarded by : $tenantOnboardedBy </li></ul><b>Regards,</b><br><b>REALAnalytics Team</b></body></html>','REALAnalytics $environment: Tenant $tenantName added',1,1,'MIME','System',1234567890,NULL,NULL,'9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B'),('6259B6AB-C2BC-4720-A997-3C54738BB4A4','EXCESSION RUNTIME TEMPLATE','EXCESSIVE RUNTIME TEMPLATE','<html> <body style=\"font-family: Calibri;\">Hi, <br><br>$modelName _ $modelVersion model has been processing a transaction for more than $excessRuntime seconds in  $environment environment.The modelet has been stopped.Please find further details below:<br><ul><li> Tenant Name: $tenantName </li><li>Tenant Transaction Id: $clienttransactionId</li><li>RA Transaction Id: $transactionId</li><li>Processing Start Time: $modelStartTime</li><li>Memory Usage at Processing Start: NA<li>CPU Usage at Processing Start: NA<li>Current Memory Usage: NA</li><li>Current CPU Usage: NA</li><li>Modelet: Server IP:$modeletHost Port :$port</li><li>Pool Name: $poolName</li></ul><b>Regards,</b><br><b>REALAnalytics Team</b></body></html>','REALAnalytics $environment: Model $modelName - $modelVersion taking too long to execute',1,1,'MIME','System',1465811587,NULL,NULL,'E3453E48-38DF-4424-A42F-69784A17D11F'),('E76CCF02-33A5-11E6-AC61-9E71128CAE77','MODELET RESTART TEMPLATE','MODELET RESTART TEMPLATE','<html><body style=\"font-family: Calibri;\">Hi,<br><br>Modelet $modeletHost:$port has been restarted in $environment environment. Please find further details below:<ul><li>Current Pool Name: $poolName</li><li>New Pool Name: $newPoolName</li><li>Restart Time: $modeletRestartTime</li><li>Model Name: $loadedModel & Model Version: $loadedModelVersion prior to restart</li><li>Model Name: $modelToLoad & Model Version: $modelVersionToLoad requested model</li><li>Transactio ID: $transactionId</li><li>Transaction Run Date: $transactionRunDate</li><li>Restart Reason: $reason</li><li># Transaction Processed since last Restart: $execLimit</li><li>Transaction Restart Set-up Count: $restartCount</li></ul><b>Regards,</b><br><b>REALAnalytics Team</b></body></html>','REALAnalytics $environment: Modelet restarted',1,1,'MIME','System',1465811587,NULL,NULL,'E76CCC0A-33A5-11E6-AC61-9E71128CAE77');
/*!40000 ALTER TABLE `NOTIFICATION_EMAIL_TEMPLATE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NOTIFICATION_EVENT`
--

DROP TABLE IF EXISTS `NOTIFICATION_EVENT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NOTIFICATION_EVENT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(45) COLLATE utf8_bin NOT NULL COMMENT 'Model Publishing Success event',
  `DESCRIPTION` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `CLASSIFICATION` varchar(64) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` varchar(32) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NAME_UNIQUE` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NOTIFICATION_EVENT`
--

LOCK TABLES `NOTIFICATION_EVENT` WRITE;
/*!40000 ALTER TABLE `NOTIFICATION_EVENT` DISABLE KEYS */;
INSERT INTO `NOTIFICATION_EVENT` VALUES ('9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','New Tenant Added','Mail will be sent when new tenant get added','System','SYSTEM',1468301732000,NULL,NULL),('9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B','Model Publish Approval','Mail will be sent when Model is requested to for approval to publish','Feature','SYSTEM',1468301732000,NULL,NULL),('9FE824C2-5FB4-4C97-AB47-4FA2E33A1C8B','On Model Publish','Mail will be sent when Model is successfully published','Feature','SYSTEM',1468301732000,NULL,NULL),('9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','Runtime Transaction Failure','Mail will be sent when Runtime throws RSV Errors like 807, 804','System','SYSTEM',1468301732000,NULL,NULL),('E3453E48-38DF-4424-A42F-69784A17D11F','Excessive Model Exec Time','Mail will be sent on excessive model exec time','System','SYSTEM',1465810235,'SYSTEM',1465810244),('E76CCC0A-33A5-11E6-AC61-9E71128CAE77','Modelet Restart','Mail will be sent when modelet restart command is initiated','System','SYSTEM',1465810235,'SYSTEM',1465810244);
/*!40000 ALTER TABLE `NOTIFICATION_EVENT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NOTIFICATION_EVENT_TEMPLATE_MAPPING`
--

DROP TABLE IF EXISTS `NOTIFICATION_EVENT_TEMPLATE_MAPPING`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NOTIFICATION_EVENT_TEMPLATE_MAPPING` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(45) COLLATE utf8_bin NOT NULL,
  `NOTIFICATION_EVENT_ID` char(36) COLLATE utf8_bin NOT NULL,
  `NOTIFICATION_TEMPLATE_ID` char(36) COLLATE utf8_bin NOT NULL,
  `NOTIFICATION_TYPE_ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` varchar(45) COLLATE utf8_bin NOT NULL,
  `TO_ADDRESS` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `FROM_ADDRESS` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `CC_ADDRESS` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `BCC_ADDRESS` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `MOBILE` bigint(20) DEFAULT NULL,
  `CREATED_BY` varchar(32) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NOTIFICATION_EVENT_TEMPLATE_MAPPING`
--

LOCK TABLES `NOTIFICATION_EVENT_TEMPLATE_MAPPING` WRITE;
/*!40000 ALTER TABLE `NOTIFICATION_EVENT_TEMPLATE_MAPPING` DISABLE KEYS */;
INSERT INTO `NOTIFICATION_EVENT_TEMPLATE_MAPPING` VALUES ('00dc13cc-de02-46dd-b686-a000f5d0e238','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','umg8274','','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'anil.kamath',1495029829720,'anil.kamath',1495029829720),('011a7b98-639b-4f81-89e1-572e6f590d25','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','nagteant',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1474282772875,'PUJA1',1474282772875),('02BC91DD-7D4E-4D03-845D-F37ED8FE77-1','Excessive Model Exec Time','E3453E48-38DF-4424-A42F-69784A17D11F','6259B6AB-C2BC-4720-A997-3C54738BB4A4','07E1739D-467E-42BE-8882-DAD8EDB7465B','rentrange',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1465889602000,NULL,NULL),('02BC91DD-7D4E-4D03-845D-F37ED8FE77-2','Excessive Model Exec Time','E3453E48-38DF-4424-A42F-69784A17D11F','6259B6AB-C2BC-4720-A997-3C54738BB4A4','07E1739D-467E-42BE-8882-DAD8EDB7465B','ocwen',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1465889602000,NULL,NULL),('02BC91DD-7D4E-4D03-845D-F37ED8FE77-3','Excessive Model Exec Time','E3453E48-38DF-4424-A42F-69784A17D11F','6259B6AB-C2BC-4720-A997-3C54738BB4A4','07E1739D-467E-42BE-8882-DAD8EDB7465B','equator',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1465889602000,NULL,NULL),('02BC91DD-7D4E-4D03-845D-F37ED8FE77-4','Excessive Model Exec Time','E3453E48-38DF-4424-A42F-69784A17D11F','6259B6AB-C2BC-4720-A997-3C54738BB4A4','07E1739D-467E-42BE-8882-DAD8EDB7465B','hubzu',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1465889602000,NULL,NULL),('02BC91DD-7D4E-4D03-845D-F37ED8FE77-5','Excessive Model Exec Time','E3453E48-38DF-4424-A42F-69784A17D11F','6259B6AB-C2BC-4720-A997-3C54738BB4A4','07E1739D-467E-42BE-8882-DAD8EDB7465B','realtrans',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1465889602000,NULL,NULL),('02BC91DD-7D4E-4D03-845D-F37ED8FE77-6','Excessive Model Exec Time','E3453E48-38DF-4424-A42F-69784A17D11F','6259B6AB-C2BC-4720-A997-3C54738BB4A4','07E1739D-467E-42BE-8882-DAD8EDB7465B','owners',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'superman',1465889602000,NULL,NULL),('02bc4673-6caa-4caa-8416-24b0a84aa181','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','odo1','gaurav.das2@altisource.com;gaurav.das2@altisource.com','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1478862374721,'PUJA1',1478862374721),('037fe8a5-6abb-4078-aa58-46f45450c831','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','gauravuser',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1472197016166,'PUJA1',1472197016166),('0570ddc3-ac39-4523-8662-0c2643c80f58','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','testr',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1475651255274,'PUJA1',1475651255274),('0b80d31f-3701-4fdf-ae7a-8ceb957ce9b9','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','nagteant',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1474282821776,'PUJA1',1474282821776),('13ca7d3c-9240-4b11-bb70-34835dff43ad','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','gauravusr1',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1472197087821,'PUJA1',1472197087821),('17405323-8b16-499a-9b5d-c4af3b77cd62','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','nagteant',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1474282864077,'PUJA1',1474282864077),('1e893cd1-ed08-4268-b9af-4d8655b5fa9e','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','demo','gaurav.das2@altisource.com;gaurav.das2@altisource.com','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1483094987865,'PUJA1',1483094987865),('1ee24a2c-cb56-47e6-b46e-9da201a99731','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','owners1',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1470661375530,'PUJA1',1470661375530),('1f75138e-e47d-4b84-b3e6-73fbd9352da9','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','gauravusr1',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1472197087825,'PUJA1',1472197087825),('26f332cb-72b0-433e-85d4-8a02876c84bb','Model Publish Approval','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D83','07E1739D-467E-42BE-8882-DAD8EDB7465B','owners',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1471427508877,'PUJA1',1471427508877),('2ea4ca5c-7b5b-45f2-805f-b4a3ef39e724','On Model Publish','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C8B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D81','07E1739D-467E-42BE-8882-DAD8EDB7465B','owners',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1471427530005,'PUJA1',1471427530005),('39f39e84-c959-407c-9f28-3cd688bd27a9','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','umg8274','','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'anil.kamath',1495029829725,'anil.kamath',1495029829725),('3a99b010-51b9-422d-9b1c-2c60631b9ecb','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','satishva',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1474471017318,'PUJA1',1474471017318),('3f5fdbed-148e-48e1-bf25-e6c2ff11adb0','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','nagteant',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1474282772869,'PUJA1',1474282772869),('440de1d9-e665-4648-8800-d8df1a0796f9','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','newtenant3','','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1477483695811,'PUJA1',1477483695811),('46044854-22e2-4d8a-8af7-e99ac9833c90','Model Publish Approval','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D83','07E1739D-467E-42BE-8882-DAD8EDB7465B','hubzu','pradeep.nagar@altisource.com','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1470810059160,'PUJA1',1477467157606),('4d32650e-d071-4b1e-bc5e-0824094f1a09','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','altisource','gaurav.das2@altisource.com;gaurav.das2@altisource.com','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1478769029612,'PUJA1',1478769029612),('5007dec6-2f3b-40ae-a2b8-8a1c30f650b6','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','nagteant',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1474282913120,'PUJA1',1474282913120),('543d6c58-8ff9-4971-a46f-280194002a66','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','newtenant2','','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1477483663387,'PUJA1',1477483663387),('574633c9-e5de-49a3-915f-71c8fc154c05','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','owners1',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1470661375528,'PUJA1',1470661375528),('5ea9c4a4-a314-42de-854a-a9a121883485','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','testr',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1475650910286,'PUJA1',1475650910286),('64b50b03-0512-4782-bbc2-628c67370dd2','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','nagteant',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1474282913124,'PUJA1',1474282913124),('656dee5b-6e36-462a-b5ee-5a55c2da548b','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','mandatory','gaurav.das2@altisource.com;gaurav.das2@altisource.com','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1482217407348,'PUJA1',1482217407348),('672bc109-50f5-4795-93ed-f46cf41e82fc','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','owners',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1470660511602,'PUJA1',1470660511602),('6bc65c16-f4a5-4582-860c-679f05f443ad','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','t2','','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1477390959170,'PUJA1',1477390959170),('757b1a1f-0e3c-497c-8023-53bcc9ff398b','Model Publish Approval','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D83','07E1739D-467E-42BE-8882-DAD8EDB7465B','t2','puja.chand@altisource.com','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1477484549627,'PUJA1',1477484549627),('781b2c15-58fe-4648-8ae6-b7b4674618cf','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','testr',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1475651322035,'PUJA1',1475651322035),('799c3680-155a-406f-9d75-8ec6600de3d4','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','bizmodels','gaurav.das2@altisource.com;gaurav.das2@altisource.com','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1481796630816,'PUJA1',1481796630816),('7BDB4BB8-33A7-11E6-AC61-9E71128CAE-2','Modelet Restart','E76CCC0A-33A5-11E6-AC61-9E71128CAE77','E76CCF02-33A5-11E6-AC61-9E71128CAE77','07E1739D-467E-42BE-8882-DAD8EDB7465B','ocwen',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1465889602000,NULL,NULL),('7a3eb848-d351-4fb7-8f58-57b8cb6e589b','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','gauravuser',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1472197016160,'PUJA1',1472197016160),('7ff3e26f-f66b-4a2d-a34a-4c8a75057ce0','On Model Publish','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C8B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D81','07E1739D-467E-42BE-8882-DAD8EDB7465B','equator','puja.chand@altisource.com','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1477480826308,'PUJA1',1477481419813),('7ff48127-f628-4ac8-befd-8022afd4be93','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','newtenant1','','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1477483593760,'PUJA1',1477483593760),('8854fc28-7d62-4842-a316-27379311d913','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','newtenant3','','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1477483695808,'PUJA1',1477483695808),('8A275022-33A7-11E6-AC61-9E71128CAE-3','Modelet Restart','E76CCC0A-33A5-11E6-AC61-9E71128CAE77','E76CCF02-33A5-11E6-AC61-9E71128CAE77','07E1739D-467E-42BE-8882-DAD8EDB7465B','equator','pradeep.nagar@altisource.com','REALAnalytics-QE@altisource.com','SatishD.KumarD@altisource.com',NULL,NULL,'SYSTEM',1465889602000,NULL,NULL),('8A275022-33A7-11E6-AC61-9E71128CAE-4','Modelet Restart','E76CCC0A-33A5-11E6-AC61-9E71128CAE77','E76CCF02-33A5-11E6-AC61-9E71128CAE77','07E1739D-467E-42BE-8882-DAD8EDB7465B','hubzu','SatishD.KumarD@altisource.com','REALAnalytics-QE@altisource.com','SatishD.KumarD@altisource.com',NULL,NULL,'SYSTEM',1465889602000,NULL,NULL),('8A275022-33A7-11E6-AC61-9E71128CAE-5','Modelet Restart','E76CCC0A-33A5-11E6-AC61-9E71128CAE77','E76CCF02-33A5-11E6-AC61-9E71128CAE77','07E1739D-467E-42BE-8882-DAD8EDB7465B','realtrans',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1465889602000,NULL,NULL),('8A275022-33A7-11E6-AC61-9E71128CAE-6','Modelet Restart','E76CCC0A-33A5-11E6-AC61-9E71128CAE77','E76CCF02-33A5-11E6-AC61-9E71128CAE77','07E1739D-467E-42BE-8882-DAD8EDB7465B','owners',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'superman',1465889602000,NULL,NULL),('8a5111b5-0b39-4b2b-bece-d1baaa464a5e','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','owners',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1470660539141,'PUJA1',1470660539141),('8cbdb3fe-350b-46d1-a041-edbda5f0d33c','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','testr',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1475651255278,'PUJA1',1475651255278),('8d2d3578-54b4-4b03-8294-cdf66854bb4a','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','altisource','gaurav.das2@altisource.com;gaurav.das2@altisource.com','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1478769029615,'PUJA1',1478769029615),('8d4ba680-7385-49c1-b3e3-30ab728cd542','On Model Publish','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C8B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D81','07E1739D-467E-42BE-8882-DAD8EDB7465B','hubzu',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1470810088426,'PUJA1',1470810088426),('8e056153-43cc-4072-a2ec-9771c633c7a2','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','mandatory','gaurav.das2@altisource.com;gaurav.das2@altisource.com','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1482217407365,'PUJA1',1482217407365),('912deeb8-dfd6-466f-9f99-6295a67c0e31','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','odo1','gaurav.das2@altisource.com;gaurav.das2@altisource.com','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1478862374723,'PUJA1',1478862374723),('91860a0a-ad70-413c-a3ef-d1c4a3d49029','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','testr',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1475650910280,'PUJA1',1475650910280),('9245ecf2-cd13-4e7a-9c25-b5afb45bb401','On Model Publish','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C8B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D81','07E1739D-467E-42BE-8882-DAD8EDB7465B','rentrange',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1469334864000,NULL,NULL),('9245ecf2-cd13-4e7a-9c25-b5afb45bb402','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','rentrange',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1469334864000,NULL,NULL),('9245ecf2-cd13-4e7a-9c25-b5afb45bb403','Model Publish Approval','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D83','07E1739D-467E-42BE-8882-DAD8EDB7465B','rentrange',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1469334864000,NULL,NULL),('9245ecf2-cd13-4e7a-9c25-b5afb45bb404','On Model Publish','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C8B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D81','07E1739D-467E-42BE-8882-DAD8EDB7465B','ocwen',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1469334864000,NULL,NULL),('9245ecf2-cd13-4e7a-9c25-b5afb45bb405','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','ocwen',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1469334864000,NULL,NULL),('9245ecf2-cd13-4e7a-9c25-b5afb45bb406','Model Publish Approval','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D83','07E1739D-467E-42BE-8882-DAD8EDB7465B','ocwen',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1469334864000,NULL,NULL),('9245ecf2-cd13-4e7a-9c25-b5afb45bb408','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','equator',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1469334864000,NULL,NULL),('9245ecf2-cd13-4e7a-9c25-b5afb45bb409','Model Publish Approval','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D83','07E1739D-467E-42BE-8882-DAD8EDB7465B','equator','puja.chand@altisource.com','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1469334864000,'PUJA1',1477481400565),('9245ecf2-cd13-4e7a-9c25-b5afb45bb410','On Model Publish','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C8B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D81','07E1739D-467E-42BE-8882-DAD8EDB7465B','hubzu',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1469334864000,NULL,NULL),('9245ecf2-cd13-4e7a-9c25-b5afb45bb411','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','hubzu',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1469334864000,NULL,NULL),('9245ecf2-cd13-4e7a-9c25-b5afb45bb412','Model Publish Approval','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D83','07E1739D-467E-42BE-8882-DAD8EDB7465B','hubzu',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1469334864000,NULL,NULL),('9245ecf2-cd13-4e7a-9c25-b5afb45bb413','On Model Publish','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C8B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D81','07E1739D-467E-42BE-8882-DAD8EDB7465B','realtrans',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1469334864000,NULL,NULL),('9245ecf2-cd13-4e7a-9c25-b5afb45bb414','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','realtrans',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1469334864000,NULL,NULL),('9245ecf2-cd13-4e7a-9c25-b5afb45bb415','Model Publish Approval','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D83','07E1739D-467E-42BE-8882-DAD8EDB7465B','realtrans',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1469334864000,NULL,NULL),('929e8521-18ea-403a-ac03-98ae01925229','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','owners',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1470660721696,'PUJA1',1470660721696),('92c529ec-98fd-42ce-b9fb-a5c585e7a0ad','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','testr',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1475651322032,'PUJA1',1475651322032),('957cdde8-44eb-11e6-beb8-9e71128cae77','On Model Publish','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C8B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D81','07E1739D-467E-42BE-8882-DAD8EDB7465B','owners',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1469334864000,NULL,NULL),('957ce284-44eb-11e6-beb8-9e71128cae77','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','owners',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1469334864000,NULL,NULL),('957ce72a-44eb-11e6-beb8-9e71128cae77','Model Publish Approval','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D83','07E1739D-467E-42BE-8882-DAD8EDB7465B','owners',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1469334864000,NULL,NULL),('9bcbe028-1609-4014-b091-0533b1ce95bb','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','owners',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1470661131332,'PUJA1',1470661131332),('D74BEB48-33A6-11E6-AC61-9E71128CAE-1','Modelet Restart','E76CCC0A-33A5-11E6-AC61-9E71128CAE77','E76CCF02-33A5-11E6-AC61-9E71128CAE77','07E1739D-467E-42BE-8882-DAD8EDB7465B','rentrange',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'SYSTEM',1465889602000,NULL,NULL),('a2e4502c-6857-49c9-ae1e-2cc9c3ca5ec5','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','demo','gaurav.das2@altisource.com;gaurav.das2@altisource.com','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1483094987871,'PUJA1',1483094987871),('aa119d5e-8abe-423e-b579-0263340c25ce','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','newtenant4','gaurav.das2@altisource.com;gaurav.das2@altisource.com','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1477484635986,'PUJA1',1477484635986),('aa72cc43-3786-47e5-85f7-492d393808b5','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','newtenant4','gaurav.das2@altisource.com;gaurav.das2@altisource.com','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1477484635989,'PUJA1',1477484635989),('af67fb86-aefe-495d-8e70-a5acd80a4245','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','t2','','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1477390959167,'PUJA1',1477390959167),('b7445f2c-f43b-4f53-b428-dcae880013d7','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','owners',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1470661131334,'PUJA1',1470661131334),('b84ff86e-9179-4d8a-9e0f-f51fcc4c4265','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','owners',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1470660511599,'PUJA1',1470660511599),('b9e3c57e-45bd-4d52-8b1e-1ef1ede6780a','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','bizmodels','gaurav.das2@altisource.com;gaurav.das2@altisource.com','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1481796630800,'PUJA1',1481796630800),('bb6aca5e-ac53-4f80-a357-4cfab56131c3','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','satishva',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1474471017307,'PUJA1',1474471017307),('c36af122-9814-4474-afaa-452a297ef6e9','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','owners',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1470660721698,'PUJA1',1470660721698),('c5643cd8-9294-4cf5-a87a-9c820818c5f3','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','newtenant2','','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1477483663383,'PUJA1',1477483663383),('d4e8f94b-d012-427d-8f87-9edf940be6af','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','newtenant1','','REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1477483593763,'PUJA1',1477483593763),('dc8747e2-ca8e-4d9a-af24-fe1914d745e6','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','testr',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1475651870900,'PUJA1',1475651870900),('ee0fc0b9-fa22-46fa-bb7b-aa91a55cf20f','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','testr',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1475651513596,'PUJA1',1475651513596),('f4545383-604e-47f6-9007-f7c05677a0d9','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','owners',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1470660539142,'PUJA1',1470660539142),('f6a8f210-4974-4391-8f5b-141d5bd181b3','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','testr',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1475651870903,'PUJA1',1475651870903),('fa333649-82df-4547-9513-9bbac7457fd0','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','nagteant',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1474282821773,'PUJA1',1474282821773),('fdd830ae-1c01-45c2-b8c1-aec2a35e3909','Runtime Transaction Failure','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82','07E1739D-467E-42BE-8882-DAD8EDB7465B','nagteant',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1474282864080,'PUJA1',1474282864080),('ff0a117a-46e6-48b2-a672-1b062ca4405b','New Tenant Added','9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B','1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84','07E1739D-467E-42BE-8882-DAD8EDB7465B','testr',NULL,'REALAnalytics-QE@altisource.com',NULL,NULL,NULL,'PUJA1',1475651513593,'PUJA1',1475651513593);
/*!40000 ALTER TABLE `NOTIFICATION_EVENT_TEMPLATE_MAPPING` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NOTIFICATION_SMS_TEMPLATE`
--

DROP TABLE IF EXISTS `NOTIFICATION_SMS_TEMPLATE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NOTIFICATION_SMS_TEMPLATE` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(64) COLLATE utf8_bin NOT NULL,
  `DESCRIPTION` varchar(256) COLLATE utf8_bin NOT NULL DEFAULT '',
  `SMS_DEFINITION` blob NOT NULL COMMENT 'Model Publishing Success Template',
  `IS_ACTIVE` int(10) NOT NULL DEFAULT '1',
  `MAJOR_VERSION` int(11) NOT NULL DEFAULT '1',
  `CREATED_BY` varchar(32) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NOTIFICATION_SMS_TEMPLATE`
--

LOCK TABLES `NOTIFICATION_SMS_TEMPLATE` WRITE;
/*!40000 ALTER TABLE `NOTIFICATION_SMS_TEMPLATE` DISABLE KEYS */;
/*!40000 ALTER TABLE `NOTIFICATION_SMS_TEMPLATE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NOTIFICATION_TYPE`
--

DROP TABLE IF EXISTS `NOTIFICATION_TYPE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NOTIFICATION_TYPE` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TYPE` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'Mail or SMS',
  `DESCRIPTION` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_BY` varchar(32) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `TYPE_UNIQUE` (`TYPE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NOTIFICATION_TYPE`
--

LOCK TABLES `NOTIFICATION_TYPE` WRITE;
/*!40000 ALTER TABLE `NOTIFICATION_TYPE` DISABLE KEYS */;
INSERT INTO `NOTIFICATION_TYPE` VALUES ('07E1739D-467E-42BE-8882-DAD8EDB7465B','MAIL','Mail notification type','SYSTEM',1468301732000,NULL,NULL),('07E1739D-467E-42BE-8882-DAD8EDB7466B','SMS','SMS notification type','SYSTEM',1468301732000,NULL,NULL);
/*!40000 ALTER TABLE `NOTIFICATION_TYPE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PERMISSIONS`
--

DROP TABLE IF EXISTS `PERMISSIONS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PERMISSIONS` (
  `Id` varchar(50) COLLATE utf8_bin NOT NULL,
  `permission` varchar(100) COLLATE utf8_bin NOT NULL,
  `permission_type` varchar(50) COLLATE utf8_bin NOT NULL,
  `ui_element_id` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PERMISSIONS`
--

LOCK TABLES `PERMISSIONS` WRITE;
/*!40000 ALTER TABLE `PERMISSIONS` DISABLE KEYS */;
INSERT INTO `PERMISSIONS` VALUES ('f4dbd27e-0d39-11e6-8666-00ffbc73cbd1','Dashboard.BatchBulk','page','batchDashboard'),('f4dbd448-0d39-11e6-8666-00ffbc73cbd1','Dashboard.Transaction.DownloadModelIO','action','modelIoDownload'),('f4dbd4d2-0d39-11e6-8666-00ffbc73cbd1','Dashboard.Transaction.DownloadReport','action','reportGeneration'),('f4dbd54c-0d39-11e6-8666-00ffbc73cbd1','Dashboard.Transaction.DownloadTenantIO','action','tenantIoDownload'),('f4dbd5b3-0d39-11e6-8666-00ffbc73cbd1','Dashboard.Transaction.PayloadField','action','payloadField'),('f4dbd61a-0d39-11e6-8666-00ffbc73cbd1','Dashboard.Transaction.Re-run','action','testBedRedirect'),('f4dbd6cb-0d39-11e6-8666-00ffbc73cbd1','Lookup.Add','page','syndicateDataCrud'),('f4dbd72e-0d39-11e6-8666-00ffbc73cbd1','Lookup.Manage','page','modelAssumptionList'),('f4dbd789-0d39-11e6-8666-00ffbc73cbd1','Lookup.Manage.Add','action','add_vinc'),('f4dbd7e0-0d39-11e6-8666-00ffbc73cbd1','Lookup.Manage.DataDownload','action','downloadVersion'),('f4dbd836-0d39-11e6-8666-00ffbc73cbd1','Lookup.Manage.DefinitionDownload','action','downloadDefinition'),('f4dbd8a5-0d39-11e6-8666-00ffbc73cbd1','Dashboard.BatchBulk.DownloadIO','action','batchTransactionDashboard_search'),('f4dbd908-0d39-11e6-8666-00ffbc73cbd1','Lookup.Manage.Delete','action','deleteVersion'),('f4dbd96a-0d39-11e6-8666-00ffbc73cbd1','Lookup.Manage.Edit','action','editVersion'),('f4dbd9c1-0d39-11e6-8666-00ffbc73cbd1','Model.Add','page','modelPublish'),('f4dbda1c-0d39-11e6-8666-00ffbc73cbd1','Model.Manage','page','umgVersionView'),('f4dbda73-0d39-11e6-8666-00ffbc73cbd1','Model.Manage.AddReportTemplate','action','vl_uploadTemplate'),('f4dbdace-0d39-11e6-8666-00ffbc73cbd1','Model.Manage.Deactivate','action','vl_deactivate'),('f4dbdb30-0d39-11e6-8666-00ffbc73cbd1','Model.Manage.Delete','action','vl_deleteVersion'),('f4dbdb8b-0d39-11e6-8666-00ffbc73cbd1','Model.Manage.EmailPublishApproval','action','vl_sendPublishApproval'),('f4dbdbe2-0d39-11e6-8666-00ffbc73cbd1','Model.Manage.ExcelDownload','action','vl_excelDownload'),('f4dbdc39-0d39-11e6-8666-00ffbc73cbd1','Model.Manage.ExportVersion','action','vl_exportVersion'),('f4dbdc94-0d39-11e6-8666-00ffbc73cbd1','Dashboard.BatchBulk.TerminateBatch','action','terminnateSelectedItems_id'),('f4dbdcf2-0d39-11e6-8666-00ffbc73cbd1','Model.Manage.ExportVersionAPI','action','vl_exportVersionAPI'),('f4dbdd49-0d39-11e6-8666-00ffbc73cbd1','Model.Manage.Publish','action','vl_publish'),('f4dbdda0-0d39-11e6-8666-00ffbc73cbd1','Model.Manage.Test','action','vl_test'),('f4dbddf7-0d39-11e6-8666-00ffbc73cbd1','Model.Manage.UpdateMapping','action','vl_updateMapping'),('f4dbde56-0d39-11e6-8666-00ffbc73cbd1','Model.Manage.VersionMetric','action','vl_versionMetric'),('f4dbdeac-0d39-11e6-8666-00ffbc73cbd1','Model.Manage.View','action','vl_view'),('f4dbdf03-0d39-11e6-8666-00ffbc73cbd1','SupportLib.Add','page','addPackage'),('f4dbdf5a-0d39-11e6-8666-00ffbc73cbd1','SupportLib.Manage','page','listPackages'),('f4dbdfb1-0d39-11e6-8666-00ffbc73cbd1','SupportLib.Manage.DownloadPackages','action','pl_dwnVer'),('f4dbe008-0d39-11e6-8666-00ffbc73cbd1','Tenant.Add','page','addTenant'),('f4dbe05f-0d39-11e6-8666-00ffbc73cbd1','Dashboard.BatchBulk.Upload','action','bd_upload'),('f4dbe0b9-0d39-11e6-8666-00ffbc73cbd1','Tenant.Manage','page','manageTenant'),('f4dbe110-0d39-11e6-8666-00ffbc73cbd1','Dashboard.Transaction','page','dashboard'),('f4dbe167-0d39-11e6-8666-00ffbc73cbd1','Dashboard.Transaction.AdvancedSearch','action','advancedSearch'),('f4dbe1be-0d39-11e6-8666-00ffbc73cbd1','Dashboard.Transaction.DownloadExcelUsageReport','action','TransactionDashboard_downldusgrprt'),('f4dbe21d-0d39-11e6-8666-00ffbc73cbd1','Dashboard.Transaction.DownloadIOExcel','action','TransactionDashboard_exprtForRerun'),('f4dbe278-0d39-11e6-8666-00ffbc73cbd1','Dashboard.Transaction.DownloadIOJson','action','TransactionDashboard_search');
/*!40000 ALTER TABLE `PERMISSIONS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PERMISSION_ROLES_MAPPING`
--

DROP TABLE IF EXISTS `PERMISSION_ROLES_MAPPING`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PERMISSION_ROLES_MAPPING` (
  `id` varchar(50) COLLATE utf8_bin NOT NULL,
  `tenant_roles_map_id` varchar(50) COLLATE utf8_bin NOT NULL,
  `permission_id` varchar(50) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_permission_roles_mapping_user_permissions` (`permission_id`),
  KEY `FK_permission_roles_mapping_tenant_roles_mapping` (`tenant_roles_map_id`),
  CONSTRAINT `FK_permission_roles_mapping_tenant_roles_mapping` FOREIGN KEY (`tenant_roles_map_id`) REFERENCES `tenant_roles_mapping` (`Id`),
  CONSTRAINT `FK_permission_roles_mapping_user_permissions` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PERMISSION_ROLES_MAPPING`
--

LOCK TABLES `PERMISSION_ROLES_MAPPING` WRITE;
/*!40000 ALTER TABLE `PERMISSION_ROLES_MAPPING` DISABLE KEYS */;
INSERT INTO `PERMISSION_ROLES_MAPPING` VALUES ('01f95b6e-1172-4417-ad26-07b7edd71673','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbda1c-0d39-11e6-8666-00ffbc73cbd1'),('03f30018-ed9e-42d8-8942-3aceb086ab36','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbdd49-0d39-11e6-8666-00ffbc73cbd1'),('0b86e6f8-6c0b-4b65-af43-db383e9ed0b1','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbe278-0d39-11e6-8666-00ffbc73cbd1'),('0f1e1694-3bc1-44a8-ac00-3440af5a81f6','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbd908-0d39-11e6-8666-00ffbc73cbd1'),('146c2dc8-d997-45c1-b835-9fca2c9f23eb','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbd4d2-0d39-11e6-8666-00ffbc73cbd1'),('1762e7cb-2cf3-4043-b6d0-5de4796dce19','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbdbe2-0d39-11e6-8666-00ffbc73cbd1'),('26d03044-7c52-488c-a03f-970de74dd579','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbd61a-0d39-11e6-8666-00ffbc73cbd1'),('2b13e48d-4d80-43a7-9ec4-d721eca2a5d7','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbdb8b-0d39-11e6-8666-00ffbc73cbd1'),('3358dd07-5132-4a6e-9929-00806e0a871b','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbda73-0d39-11e6-8666-00ffbc73cbd1'),('384c1c9c-a85c-40ea-aac3-128bc34dacab','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbe21d-0d39-11e6-8666-00ffbc73cbd1'),('3d70ee63-ca0d-4f96-bbf2-b068607b4ff7','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbdeac-0d39-11e6-8666-00ffbc73cbd1'),('40be8c1f-5f6c-46e4-af61-fd466300a18b','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbd8a5-0d39-11e6-8666-00ffbc73cbd1'),('4c4e9720-3aa1-4db3-9a70-f9e867f14138','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbdb30-0d39-11e6-8666-00ffbc73cbd1'),('5ac58ddf-395f-4cd7-959a-eb2151861835','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbd96a-0d39-11e6-8666-00ffbc73cbd1'),('5ce6db8e-474d-40be-9b14-67e5552e1b99','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbd7e0-0d39-11e6-8666-00ffbc73cbd1'),('643113fa-7f66-4775-82f7-d3a236510977','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbdcf2-0d39-11e6-8666-00ffbc73cbd1'),('67c2860e-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd27e-0d39-11e6-8666-00ffbc73cbd1'),('67cbc3a8-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd448-0d39-11e6-8666-00ffbc73cbd1'),('67d36c1f-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd4d2-0d39-11e6-8666-00ffbc73cbd1'),('67dccd29-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd54c-0d39-11e6-8666-00ffbc73cbd1'),('67e29a55-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd5b3-0d39-11e6-8666-00ffbc73cbd1'),('67e7b173-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd61a-0d39-11e6-8666-00ffbc73cbd1'),('67f330cf-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd6cb-0d39-11e6-8666-00ffbc73cbd1'),('67f83c63-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd72e-0d39-11e6-8666-00ffbc73cbd1'),('68044310-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd789-0d39-11e6-8666-00ffbc73cbd1'),('680a04bf-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd7e0-0d39-11e6-8666-00ffbc73cbd1'),('680f1f2d-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd836-0d39-11e6-8666-00ffbc73cbd1'),('6814c20b-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd8a5-0d39-11e6-8666-00ffbc73cbd1'),('6824e536-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd908-0d39-11e6-8666-00ffbc73cbd1'),('6830724a-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd96a-0d39-11e6-8666-00ffbc73cbd1'),('683d0a8c-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd9c1-0d39-11e6-8666-00ffbc73cbd1'),('68434caf-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbda1c-0d39-11e6-8666-00ffbc73cbd1'),('684a3048-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbda73-0d39-11e6-8666-00ffbc73cbd1'),('68514078-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdace-0d39-11e6-8666-00ffbc73cbd1'),('685799cd-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdb30-0d39-11e6-8666-00ffbc73cbd1'),('685e0472-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdb8b-0d39-11e6-8666-00ffbc73cbd1'),('68698719-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdbe2-0d39-11e6-8666-00ffbc73cbd1'),('68739dc5-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdc39-0d39-11e6-8666-00ffbc73cbd1'),('687b3384-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdc94-0d39-11e6-8666-00ffbc73cbd1'),('6882d876-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdcf2-0d39-11e6-8666-00ffbc73cbd1'),('688a77a7-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdd49-0d39-11e6-8666-00ffbc73cbd1'),('68921bb1-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdda0-0d39-11e6-8666-00ffbc73cbd1'),('6899ec18-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbddf7-0d39-11e6-8666-00ffbc73cbd1'),('68a15d68-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbde56-0d39-11e6-8666-00ffbc73cbd1'),('68a8fc99-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdeac-0d39-11e6-8666-00ffbc73cbd1'),('68b09fe8-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdf03-0d39-11e6-8666-00ffbc73cbd1'),('68b84305-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdf5a-0d39-11e6-8666-00ffbc73cbd1'),('68c1279a-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdfb1-0d39-11e6-8666-00ffbc73cbd1'),('68c8c85d-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbe008-0d39-11e6-8666-00ffbc73cbd1'),('68d808b3-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbe05f-0d39-11e6-8666-00ffbc73cbd1'),('68e0f464-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbe0b9-0d39-11e6-8666-00ffbc73cbd1'),('68ec6cb1-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbe110-0d39-11e6-8666-00ffbc73cbd1'),('68f7d654-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbe167-0d39-11e6-8666-00ffbc73cbd1'),('68ff7a00-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbe1be-0d39-11e6-8666-00ffbc73cbd1'),('690725a8-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbe21d-0d39-11e6-8666-00ffbc73cbd1'),('690e1a52-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbe278-0d39-11e6-8666-00ffbc73cbd1'),('6bfe31ec-fafb-4037-b10a-93520081f156','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbdc39-0d39-11e6-8666-00ffbc73cbd1'),('6e6edbf4-f7e3-4234-9658-f5c055b7a57f','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbdf03-0d39-11e6-8666-00ffbc73cbd1'),('6e843fd8-b34a-4199-990e-5e461d34eec2','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbd72e-0d39-11e6-8666-00ffbc73cbd1'),('7035b1fb-78c6-4646-912f-0e15a03b123e','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbd9c1-0d39-11e6-8666-00ffbc73cbd1'),('80666583-81ca-4146-994d-bb7f879a204d','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbd448-0d39-11e6-8666-00ffbc73cbd1'),('81b09846-1a76-4e55-8492-670cc16cdb75','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbe110-0d39-11e6-8666-00ffbc73cbd1'),('8509285d-acbb-4159-a3d6-26209d44396a','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbe1be-0d39-11e6-8666-00ffbc73cbd1'),('8a34f844-d798-4068-8e33-7e092f552e2f','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbe008-0d39-11e6-8666-00ffbc73cbd1'),('8d1c11f0-2aef-4ad4-9021-4b68fa77412f','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbdda0-0d39-11e6-8666-00ffbc73cbd1'),('8dcc7575-1ebd-4103-9452-70341225bb99','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbddf7-0d39-11e6-8666-00ffbc73cbd1'),('8dceec71-302e-4f57-af21-72ef4c343f51','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbd789-0d39-11e6-8666-00ffbc73cbd1'),('8f32ae84-51b7-4060-beca-ca29a03b84f0','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbd836-0d39-11e6-8666-00ffbc73cbd1'),('9b2b5938-29ba-47d8-9922-51404ee76f87','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbd5b3-0d39-11e6-8666-00ffbc73cbd1'),('9e1e9beb-0604-40fe-8050-524bd181250d','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbe167-0d39-11e6-8666-00ffbc73cbd1'),('a624c189-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd27e-0d39-11e6-8666-00ffbc73cbd1'),('a6323021-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd448-0d39-11e6-8666-00ffbc73cbd1'),('a63b1430-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd4d2-0d39-11e6-8666-00ffbc73cbd1'),('a645431f-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd54c-0d39-11e6-8666-00ffbc73cbd1'),('a64d4ced-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd5b3-0d39-11e6-8666-00ffbc73cbd1'),('a65af54e-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd61a-0d39-11e6-8666-00ffbc73cbd1'),('a6651295-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd6cb-0d39-11e6-8666-00ffbc73cbd1'),('a66f3721-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd72e-0d39-11e6-8666-00ffbc73cbd1'),('a6759747-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd789-0d39-11e6-8666-00ffbc73cbd1'),('a67e7c50-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd7e0-0d39-11e6-8666-00ffbc73cbd1'),('a6861de9-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd836-0d39-11e6-8666-00ffbc73cbd1'),('a68dbc68-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd8a5-0d39-11e6-8666-00ffbc73cbd1'),('a69d9202-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd908-0d39-11e6-8666-00ffbc73cbd1'),('a6a4addf-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd96a-0d39-11e6-8666-00ffbc73cbd1'),('a6ad8937-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbd9c1-0d39-11e6-8666-00ffbc73cbd1'),('a6b3e173-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbda1c-0d39-11e6-8666-00ffbc73cbd1'),('a6bb862b-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbda73-0d39-11e6-8666-00ffbc73cbd1'),('a6c32566-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdace-0d39-11e6-8666-00ffbc73cbd1'),('a6c97f88-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdb30-0d39-11e6-8666-00ffbc73cbd1'),('a6d638f3-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdb8b-0d39-11e6-8666-00ffbc73cbd1'),('a6dc9275-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdbe2-0d39-11e6-8666-00ffbc73cbd1'),('a6e43968-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdc39-0d39-11e6-8666-00ffbc73cbd1'),('a6ebd8ea-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdc94-0d39-11e6-8666-00ffbc73cbd1'),('a6f234dd-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdcf2-0d39-11e6-8666-00ffbc73cbd1'),('a6f9d401-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdd49-0d39-11e6-8666-00ffbc73cbd1'),('a70199d7-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdda0-0d39-11e6-8666-00ffbc73cbd1'),('a7091a2e-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbddf7-0d39-11e6-8666-00ffbc73cbd1'),('a711ff06-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbde56-0d39-11e6-8666-00ffbc73cbd1'),('a71eb7f3-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdeac-0d39-11e6-8666-00ffbc73cbd1'),('a72510a8-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdf03-0d39-11e6-8666-00ffbc73cbd1'),('a72cb4bb-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdf5a-0d39-11e6-8666-00ffbc73cbd1'),('a73452ee-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbdfb1-0d39-11e6-8666-00ffbc73cbd1'),('a73be33c-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbe008-0d39-11e6-8666-00ffbc73cbd1'),('a7425398-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbe05f-0d39-11e6-8666-00ffbc73cbd1'),('a748b050-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbe0b9-0d39-11e6-8666-00ffbc73cbd1'),('a7507061-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbe110-0d39-11e6-8666-00ffbc73cbd1'),('a757f9a0-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbe167-0d39-11e6-8666-00ffbc73cbd1'),('a76a5a44-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbe1be-0d39-11e6-8666-00ffbc73cbd1'),('a77904cc-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbe21d-0d39-11e6-8666-00ffbc73cbd1'),('a787c70a-1370-11e6-a998-00ffbc73cbd1','d36e5136-0d29-11e6-b78a-00ffde411c75','f4dbe278-0d39-11e6-8666-00ffbc73cbd1'),('aec901d6-6e3b-4aef-9d35-1e0821d58b88','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbdfb1-0d39-11e6-8666-00ffbc73cbd1'),('b4627d24-3f9d-497d-9964-583b177a6341','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbd54c-0d39-11e6-8666-00ffbc73cbd1'),('b636fc53-5453-4868-aef9-3761de8933b8','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbe0b9-0d39-11e6-8666-00ffbc73cbd1'),('be389992-5af9-4e25-916d-e0fcdcd37dc5','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbdace-0d39-11e6-8666-00ffbc73cbd1'),('bfc59745-f85d-4eab-aa16-47aee305f5c4','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbd27e-0d39-11e6-8666-00ffbc73cbd1'),('cb4c1503-61d3-4835-b94e-bb6b207de438','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbde56-0d39-11e6-8666-00ffbc73cbd1'),('e9f6a8c6-e0e9-407d-b37f-1d2ade6a3b76','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbdf5a-0d39-11e6-8666-00ffbc73cbd1'),('f0b1bd5b-3924-45a0-be2a-fa170923e1f1','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbd6cb-0d39-11e6-8666-00ffbc73cbd1'),('f9bbd305-8910-4aef-baae-4d31d21f9399','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbdc94-0d39-11e6-8666-00ffbc73cbd1'),('fa337495-6ceb-4933-b791-66f649ba30cf','39b37b7b-57d0-4a66-8b19-f8143df7cbd4','f4dbe05f-0d39-11e6-8666-00ffbc73cbd1');
/*!40000 ALTER TABLE `PERMISSION_ROLES_MAPPING` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `POOL`
--

DROP TABLE IF EXISTS `POOL`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `POOL` (
  `ID` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'Pool id',
  `POOL_NAME` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'Priority Pool Name',
  `POOL_DESCRIPTION` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT 'Priority Pool Description',
  `IS_DEFAULT_POOL` tinyint(1) NOT NULL COMMENT 'Flag fog default pool',
  `EXECUTION_LANGUAGE` varchar(32) COLLATE utf8_bin NOT NULL COMMENT 'No of Modelelts allocated to this pool',
  `EXECUTION_ENVIRONMENT` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT 'Linux' COMMENT 'Execution environment',
  `POOL_STATUS` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT 'Status of pool (IN_PROGRESS, etc, will be used for batch)',
  `MODELET_COUNT` int(11) NOT NULL COMMENT 'No of Modelelts allocated to this pool',
  `MODELET_CAPACITY` varchar(32) COLLATE utf8_bin NOT NULL COMMENT 'Max Heap size of Modelet',
  `PRIORITY` int(11) NOT NULL COMMENT 'Priority of pool',
  `WAIT_TIMEOUT` int(50) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UNIQUE_POOL_NAME` (`POOL_NAME`),
  UNIQUE KEY `UNIQUE_POOL_PRIORITY` (`PRIORITY`,`EXECUTION_LANGUAGE`,`EXECUTION_ENVIRONMENT`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `POOL`
--

LOCK TABLES `POOL` WRITE;
/*!40000 ALTER TABLE `POOL` DISABLE KEYS */;
INSERT INTO `POOL` VALUES ('2','R-Linux-Default','POOL OF R-Linux-Default',1,'R','Linux',NULL,2,'4GB - Linux 64 bit',12,60000),('8528c929-9a0d-408a-86ac-dbe29e1349db','EAGER_LOAD','EAGER_LOAD',0,'R','Linux',NULL,1,'4GB - Linux 64 bit',10,60000),('95806a46-a4ce-432f-aeeb-357497fe6360','P1','P1',0,'R','Linux',NULL,0,'4GB - Linux 64 bit',11,60000);
/*!40000 ALTER TABLE `POOL` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `POOL_CRITERIA`
--

DROP TABLE IF EXISTS `POOL_CRITERIA`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `POOL_CRITERIA` (
  `ID` varchar(50) COLLATE utf8_bin NOT NULL COMMENT 'pool criteria id',
  `CRITERIA_NAME` varchar(100) COLLATE utf8_bin NOT NULL COMMENT 'pool criteria name',
  `CRITERIA_PRIORITY` int(11) NOT NULL COMMENT 'citeria priority used in sorting the pool definition for selection',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UNIQUE_POOL_CRITERIA` (`CRITERIA_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Priority pool criterias';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `POOL_CRITERIA`
--

LOCK TABLES `POOL_CRITERIA` WRITE;
/*!40000 ALTER TABLE `POOL_CRITERIA` DISABLE KEYS */;
INSERT INTO `POOL_CRITERIA` VALUES ('1','TENANT',5),('2','EXECUTION_LANGUAGE',1),('4','TRANSACTION_TYPE',7),('5','TRANSACTION_MODE',6),('6','MODEL',3),('7','MODEL_VERSION',4),('8','CHANNEL',8),('9','EXECUTION_ENVIRONMENT',2);
/*!40000 ALTER TABLE `POOL_CRITERIA` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `POOL_CRITERIA_DEF_MAPPING`
--

DROP TABLE IF EXISTS `POOL_CRITERIA_DEF_MAPPING`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `POOL_CRITERIA_DEF_MAPPING` (
  `ID` varchar(50) COLLATE utf8_bin NOT NULL,
  `POOL_ID` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `POOL_CRITERIA_VALUE` varchar(512) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `FK_UNIQUE_CRITERIA` (`POOL_ID`),
  UNIQUE KEY `UNIQUE_POOL_CRITERIA_DEF_MAPPING` (`POOL_ID`),
  CONSTRAINT `FK_POOL_CRITERIA_DEF_MAPPING_pool` FOREIGN KEY (`POOL_ID`) REFERENCES `pool` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `POOL_CRITERIA_DEF_MAPPING`
--

LOCK TABLES `POOL_CRITERIA_DEF_MAPPING` WRITE;
/*!40000 ALTER TABLE `POOL_CRITERIA_DEF_MAPPING` DISABLE KEYS */;
INSERT INTO `POOL_CRITERIA_DEF_MAPPING` VALUES ('2','2','#TENANT# = Any & #EXECUTION_LANGUAGE# = R & #TRANSACTION_TYPE# = Any & #MODEL# = Any & #MODEL_VERSION# = Any & #TRANSACTION_MODE# = Any & #CHANNEL# = Any & #EXECUTION_ENVIRONMENT# = Linux'),('5b658034-c777-4751-9a0d-f122b769d757','8528c929-9a0d-408a-86ac-dbe29e1349db','#TENANT# = localhost & #EXECUTION_LANGUAGE# = R & #TRANSACTION_TYPE# = Any & #MODEL# = Any & #MODEL_VERSION# = Any & #TRANSACTION_MODE# = Any & #CHANNEL# = Any & #EXECUTION_ENVIRONMENT# = Linux'),('96a737a0-a0f0-4c71-aca4-afc2dbeba8a7','95806a46-a4ce-432f-aeeb-357497fe6360','#TENANT# = Any & #EXECUTION_LANGUAGE# = R & #TRANSACTION_TYPE# = Any & #MODEL# = Any & #MODEL_VERSION# = Any & #TRANSACTION_MODE# = Any & #CHANNEL# = HTTP & #EXECUTION_ENVIRONMENT# = Linux');
/*!40000 ALTER TABLE `POOL_CRITERIA_DEF_MAPPING` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `POOL_USAGE_ORDER`
--

DROP TABLE IF EXISTS `POOL_USAGE_ORDER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `POOL_USAGE_ORDER` (
  `ID` varchar(50) COLLATE utf8_bin NOT NULL,
  `POOL_ID` varchar(50) COLLATE utf8_bin NOT NULL,
  `POOL_USAGE_ID` varchar(50) COLLATE utf8_bin NOT NULL,
  `POOL_TRY_ORDER` int(10) NOT NULL,
  `CAN_USE_INPROGRES` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'flag for whether usage pool can be used during progress',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UNIQUE_POOL_USAGE_ORDER` (`POOL_ID`,`POOL_USAGE_ID`),
  KEY `FKPOOL_ID` (`POOL_ID`),
  CONSTRAINT `FK_POOL_USAGE_ORDER_pool` FOREIGN KEY (`POOL_ID`) REFERENCES `pool` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `POOL_USAGE_ORDER`
--

LOCK TABLES `POOL_USAGE_ORDER` WRITE;
/*!40000 ALTER TABLE `POOL_USAGE_ORDER` DISABLE KEYS */;
INSERT INTO `POOL_USAGE_ORDER` VALUES ('2','2','2',1,0),('4398de79-7348-40b2-aee9-1e1086769708','95806a46-a4ce-432f-aeeb-357497fe6360','95806a46-a4ce-432f-aeeb-357497fe6360',1,0),('5b53895c-4b66-4c0c-8a83-0b1a71d8ca7d','8528c929-9a0d-408a-86ac-dbe29e1349db','8528c929-9a0d-408a-86ac-dbe29e1349db',1,0);
/*!40000 ALTER TABLE `POOL_USAGE_ORDER` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `REVINFO`
--

LOCK TABLES `REVINFO` WRITE;
/*!40000 ALTER TABLE `REVINFO` DISABLE KEYS */;
INSERT INTO `REVINFO` VALUES (1,1434707898050,'anil.kamath'),(2,1434711465705,'anil.kamath'),(3,1435140468691,'anil.kamath'),(4,1436266371378,'anil.kamath'),(5,1438244131305,'anil.kamath'),(6,1438244131305,'anil.kamath'),(7,1438244131305,'anil.kamath'),(8,1438244131305,'anil.kamath'),(9,1438328870313,'anil.kamath'),(10,1438328993968,'anil.kamath'),(11,1438329096479,'anil.kamath'),(12,1438329096510,'anil.kamath'),(13,1438329376820,'anil.kamath'),(14,1438329376962,'anil.kamath'),(15,1438329377074,'anil.kamath'),(16,1438329377321,'anil.kamath'),(17,1438329912943,'anil.kamath'),(18,1438329913005,'anil.kamath'),(19,1438329913005,'anil.kamath'),(20,1438330166729,'anil.kamath'),(21,1438330764543,'anil.kamath'),(22,1438333382109,'anil.kamath'),(23,1438333382226,'anil.kamath'),(24,1438333856289,'anil.kamath'),(25,1438334215677,'anil.kamath'),(26,1438334216076,'anil.kamath'),(27,1438334656206,'anil.kamath'),(28,1438334656211,'anil.kamath'),(29,1438334657224,'anil.kamath'),(30,1438852048443,'anil.kamath'),(31,1440153444062,'anil.kamath'),(32,1447409302289,'anil.kamath'),(33,1447409302846,'anil.kamath'),(34,1447679843362,'anil.kamath'),(35,1448009655194,'anil.kamath'),(36,1448381019414,'anil.kamath'),(37,1448382183700,'anil.kamath'),(38,1448427373692,'anil.kamath'),(39,1448430271127,'anil.kamath'),(40,1448430271252,'anil.kamath'),(41,1448430326977,'anil.kamath'),(42,1448430358811,'anil.kamath'),(43,1448430359049,'anil.kamath'),(44,1448430503987,'anil.kamath'),(45,1448431058986,'anil.kamath'),(46,1448431074155,'anil.kamath'),(51,1460557830804,'anil.kamath'),(52,1460558863086,'anil.kamath'),(53,1460560212995,'anil.kamath'),(54,1462520016498,'anil.kamath'),(55,1465280013845,'anil.kamath'),(56,1473936179135,'anil.kamath'),(57,1477294308948,'anil.kamath'),(58,1477294417040,'anil.kamath'),(59,1477294473755,'anil.kamath'),(60,1477294582187,'anil.kamath'),(61,1477294668173,'anil.kamath'),(62,1477294935321,'anil.kamath'),(63,1477295104098,'anil.kamath'),(64,1477295269512,'anil.kamath'),(65,1477295619833,'anil.kamath'),(66,1477295677724,'anil.kamath'),(67,1487669240630,'anil.kamath'),(68,1487671451948,'anil.kamath'),(69,1487673061470,'anil.kamath'),(70,1492594712842,'anil.kamath'),(71,1493733108466,'anil.kamath'),(73,1493795800187,'anil.kamath'),(74,1503042763509,'anil.kamath'),(75,1503042773197,'anil.kamath'),(76,1503042786924,'anil.kamath'),(77,1503924181737,'anil.kamath'),(78,1505461594841,'anil.kamath'),(79,1507805244473,'anil.kamath'),(80,1507805349000,'anil.kamath'),(81,1510752769571,'anil.kamath'),(82,1512656862031,'anil.kamath');
/*!40000 ALTER TABLE `REVINFO` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ROLES`
--

DROP TABLE IF EXISTS `ROLES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ROLES` (
  `ROLE` varchar(100) COLLATE utf8_bin NOT NULL,
  `Id` char(36) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `Id` (`Id`),
  UNIQUE KEY `ROLE` (`ROLE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ROLES`
--

LOCK TABLES `ROLES` WRITE;
/*!40000 ALTER TABLE `ROLES` DISABLE KEYS */;
INSERT INTO `ROLES` VALUES ('ROLE_ADMIN','028f1293-cbc5-40b9-beba-c53929e6ac33'),('ROLE_USER','87f01f20-e912-4549-80ba-93fec1b4d756');
/*!40000 ALTER TABLE `ROLES` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SYNDICATED_DATA`
--

DROP TABLE IF EXISTS `SYNDICATED_DATA`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SYNDICATED_DATA` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `CONTAINER_NAME` varchar(50) COLLATE utf8_bin NOT NULL,
  `DESCRIPTION` varchar(200) COLLATE utf8_bin NOT NULL,
  `VERSION_ID` bigint(4) DEFAULT NULL,
  `VERSION_NAME` varchar(50) COLLATE utf8_bin NOT NULL,
  `VERSION_DESCRIPTION` varchar(200) COLLATE utf8_bin NOT NULL,
  `TABLE_NAME` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `VALID_FROM` bigint(20) DEFAULT NULL,
  `VALID_TO` bigint(20) DEFAULT NULL,
  `CREATED_BY` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `uniqueVersion` (`CONTAINER_NAME`,`VERSION_ID`),
  KEY `INDEX_CINTAINER_NAME` (`CONTAINER_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SYNDICATED_DATA`
--

LOCK TABLES `SYNDICATED_DATA` WRITE;
/*!40000 ALTER TABLE `SYNDICATED_DATA` DISABLE KEYS */;
INSERT INTO `SYNDICATED_DATA` VALUES ('30c195f4-4090-4d00-b0ea-02fa02e7b69d','REQAPPR','REQAPPR',1,'1.0','REQAPPR','SYND_DATA_REQAPPR',1477416060000,NULL,'anil.kamath',1477295619488,'anil.kamath',1477295619488),('3abc471c-69ee-4518-99eb-851cb2f7eab3','ZIP2FIPS','ZIP2FIPS',1,'1.0','ZIP2FIPS','SYND_DATA_ZIP2FIPS',1477415100000,NULL,'anil.kamath',1477294574175,'anil.kamath',1477294574175),('80ae52a0-07b9-4bd9-86da-0688d4fc5304','SPP_TABLE_FINAL','SPP_TABLE_FINAL',1,'1.0','SPP_TABLE_FINAL','SYND_DATA_SPP_TABLE_FINAL',1477415700000,NULL,'anil.kamath',1477295266732,'anil.kamath',1477295266732),('9b53c530-a1d1-4ca5-9010-ef69876daaca','EVICTIONPROB','EVICTIONPROB',1,'1.0','EVICTIONPROB','SYND_DATA_EVICTIONPROB',1477415040000,NULL,'anil.kamath',1477294473266,'anil.kamath',1477294473266),('cf146784-86f0-4b2e-8a73-1b4591e41be5','MISCSYND','MISCSYND',1,'1.0','MISCSYND','SYND_DATA_MISCSYND',1477415460000,NULL,'anil.kamath',1477294934855,'anil.kamath',1477294934855),('d71b7e07-16fa-4c57-8f20-0020e272e14a','HPI_CONTAINER','HPI_CONTAINER',1,'1.0','HPI_CONTAINER','SYND_DATA_HPI_CONTAINER',1477412700000,NULL,'anil.kamath',1477294289676,'anil.kamath',1477294289676),('d875ed14-67c4-4b2a-a79e-4f710795c320','GVL_DATA','GVL_DATA',1,'1.0','GVL_DATA','SYND_DATA_GVL_DATA',1477414980000,NULL,'anil.kamath',1477294416653,'anil.kamath',1477294416653),('e05e43c6-5d8d-46d9-87d3-3bc7f797f6c8','REOSTIGMA','REOSTIGMA',1,'1.0','REOSTIGMA','SYND_DATA_REOSTIGMA',1477415640000,NULL,'anil.kamath',1477295103774,'anil.kamath',1477295103774),('ed1d1637-a079-45ea-95c7-c8f65a73addd','SSDIL_INDIVIDUAL_SYND2','SSDIL_INDIVIDUAL_SYND2',1,'1.0','SSDIL_INDIVIDUAL_SYND2','SYND_DATA_SSDIL_INDIVIDUAL_SYND2',1477415220000,NULL,'anil.kamath',1477294667831,'anil.kamath',1477294667831),('f3a1cdff-8015-4a6c-80e6-3f735ad55fa4','SSSYNDFINAL','SSSYNDFINAL',1,'1.0','SSSYNDFINAL','SYND_DATA_SSSYNDFINAL',1477416240000,NULL,'anil.kamath',1477295677126,'anil.kamath',1477295677126);
/*!40000 ALTER TABLE `SYNDICATED_DATA` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SYNDICATED_DATA_AUDIT`
--

DROP TABLE IF EXISTS `SYNDICATED_DATA_AUDIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SYNDICATED_DATA_AUDIT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL DEFAULT '',
  `CONTAINER_NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `DESCRIPTION` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `VERSION_NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `VERSION_DESCRIPTION` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `VERSION_ID` bigint(20) DEFAULT NULL,
  `TABLE_NAME` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `VALID_FROM` bigint(20) DEFAULT NULL,
  `VALID_TO` bigint(20) DEFAULT NULL,
  `CREATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_ON` bigint(20) DEFAULT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`,`REV`),
  KEY `FK_SYNDICATED_DATA_AUDIT_REVINFO` (`REV`),
  CONSTRAINT `FK_SYNDICATED_DATA_AUDIT_REVINFO` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SYNDICATED_DATA_AUDIT`
--

LOCK TABLES `SYNDICATED_DATA_AUDIT` WRITE;
/*!40000 ALTER TABLE `SYNDICATED_DATA_AUDIT` DISABLE KEYS */;
INSERT INTO `SYNDICATED_DATA_AUDIT` VALUES ('30c195f4-4090-4d00-b0ea-02fa02e7b69d','REQAPPR','REQAPPR','1.0','REQAPPR',1,'SYND_DATA_REQAPPR',1477416060000,NULL,'anil.kamath',1477295619488,'anil.kamath',1477295619488,65,0),('3abc471c-69ee-4518-99eb-851cb2f7eab3','ZIP2FIPS','ZIP2FIPS','1.0','ZIP2FIPS',1,'SYND_DATA_ZIP2FIPS',1477415100000,NULL,'anil.kamath',1477294574175,'anil.kamath',1477294574175,60,0),('80ae52a0-07b9-4bd9-86da-0688d4fc5304','SPP_TABLE_FINAL','SPP_TABLE_FINAL','1.0','SPP_TABLE_FINAL',1,'SYND_DATA_SPP_TABLE_FINAL',1477415700000,NULL,'anil.kamath',1477295266732,'anil.kamath',1477295266732,64,0),('9b53c530-a1d1-4ca5-9010-ef69876daaca','EVICTIONPROB','EVICTIONPROB','1.0','EVICTIONPROB',1,'SYND_DATA_EVICTIONPROB',1477415040000,NULL,'anil.kamath',1477294473266,'anil.kamath',1477294473266,59,0),('cf146784-86f0-4b2e-8a73-1b4591e41be5','MISCSYND','MISCSYND','1.0','MISCSYND',1,'SYND_DATA_MISCSYND',1477415460000,NULL,'anil.kamath',1477294934855,'anil.kamath',1477294934855,62,0),('d71b7e07-16fa-4c57-8f20-0020e272e14a','HPI_CONTAINER','HPI_CONTAINER','1.0','HPI_CONTAINER',1,'SYND_DATA_HPI_CONTAINER',1477412700000,NULL,'anil.kamath',1477294289676,'anil.kamath',1477294289676,57,0),('d875ed14-67c4-4b2a-a79e-4f710795c320','GVL_DATA','GVL_DATA','1.0','GVL_DATA',1,'SYND_DATA_GVL_DATA',1477414980000,NULL,'anil.kamath',1477294416653,'anil.kamath',1477294416653,58,0),('e05e43c6-5d8d-46d9-87d3-3bc7f797f6c8','REOSTIGMA','REOSTIGMA','1.0','REOSTIGMA',1,'SYND_DATA_REOSTIGMA',1477415640000,NULL,'anil.kamath',1477295103774,'anil.kamath',1477295103774,63,0),('ed1d1637-a079-45ea-95c7-c8f65a73addd','SSDIL_INDIVIDUAL_SYND2','SSDIL_INDIVIDUAL_SYND2','1.0','SSDIL_INDIVIDUAL_SYND2',1,'SYND_DATA_SSDIL_INDIVIDUAL_SYND2',1477415220000,NULL,'anil.kamath',1477294667831,'anil.kamath',1477294667831,61,0),('f3a1cdff-8015-4a6c-80e6-3f735ad55fa4','SSSYNDFINAL','SSSYNDFINAL','1.0','SSSYNDFINAL',1,'SYND_DATA_SSSYNDFINAL',1477416240000,NULL,'anil.kamath',1477295677126,'anil.kamath',1477295677126,66,0);
/*!40000 ALTER TABLE `SYNDICATED_DATA_AUDIT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SYSTEM_KEY`
--

DROP TABLE IF EXISTS `SYSTEM_KEY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SYSTEM_KEY` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `SYSTEM_KEY` varchar(45) COLLATE utf8_bin NOT NULL,
  `KEY_TYPE` varchar(200) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SYSTEM_KEY`
--

LOCK TABLES `SYSTEM_KEY` WRITE;
/*!40000 ALTER TABLE `SYSTEM_KEY` DISABLE KEYS */;
INSERT INTO `SYSTEM_KEY` VALUES ('05a68810-e6f9-11e3-a68a-82687f4fc15c','DRIVER','DATABASE','SYSTEM',1401344421,'SYSTEM',1401344421),('05be454a-e6f9-11e3-a68a-82687f4fc15c','URL','DATABASE','SYSTEM',1401344421,'SYSTEM',1401344421),('05be454a-e6f9-11e3-a68a-82687f4fcx15','maxIdleTime','DATABASE','SYSTEM',1401344421,'SYSTEM',1401344421),('05be454a-e6f9-11e3-a68a-82687f4fcx16','minPoolSize','DATABASE','SYSTEM',1401344421,'SYSTEM',1401344421),('05be454a-e6f9-11e3-a68a-82687f4fcx17','maxPoolSize','DATABASE','SYSTEM',1401344421,'SYSTEM',1401344421),('05be4a2c-e6f9-11e3-a68a-82687f4fc15c','SCHEMA','DATABASE','SYSTEM',1401344421,'SYSTEM',1401344421),('05be4fe0-e6f9-11e3-a68a-82687f4fc15c','USER','DATABASE','SYSTEM',1401344421,'SYSTEM',1401344421),('05be5350-e6f9-11e3-a68a-82687f4fc15c','PASSWORD','DATABASE','SYSTEM',1401344421,'SYSTEM',1401344421),('138c0981-1293-11e6-b2dd-00ffbc73cbd1','BULK_ENABLED','TENANT','SYSTEM',1462433357,'SYSTEM',1462433357),('15a69810-e665-12e3-b687-98654f4fc15c','BATCH_ENABLED','TENANT','SYSTEM',1401344421,'SYSTEM',1401344421),('1A2F140D-B8C1-4CE7-9857-8092C5964653','FTP_HOST','FTP_BATCH_WRAPPER','SYSTEM',1401344421,'SYSTEM',1401344421),('1A2F140D-B8C1-4CE7-9857-8092C8512653','FTP_OUTPUT_FOLDER','FTP_BATCH_WRAPPER','SYSTEM',1401344421,'SYSTEM',1401344421),('232d9687-8b00-11e6-989d-00059a3c7a00','ModelOutput_Validation','TENANT','SYSTEM',1475674340,'SYSTEM',1475674340),('3d952b55-20cb-11e6-83a0-00ffbc73cbd1','EMAIL_NOTIFICATIONS_ENABLED','TENANT','SYSTEM',1463996797,'SYSTEM',1463996797),('6EDF46E9-288D-4787-B993-3B5A3B2C2019','FTP_INPUT_FOLDER','FTP_BATCH_WRAPPER','SYSTEM',1401344421,'SYSTEM',1401344421),('74246ec4-fac2-11e3-801d-b2227cce2b54','RUNTIME_BASE_URL','TENANT','SYSTEM',1401344421,'SYSTEM',1401344421),('74246ec4-fac3-11e3-801d-b2227cce2c54','COLUMN_IDENTIFIERS','STRING,VARCHAR,DECIMAL,DOUBLE,BIT,INTEGER, INT, SMALLINT, TINYINT, MEDIUMINT, BIGINT,TIMESTAMP,TIME,YEAR,DATETIME,DATE','SYSTEM',1401344421,'SYSTEM',1401344421),('742491a8-fac2-11e3-801d-b2227cce2b54','EXCEL','PLUGIN','SYSTEM',1401344421,'SYSTEM',1401344421),('76ea853d-7479-4d25-87fc-cb899043a9e5','maxConnectionAge','DATABASE','SYSTEM',1401344421,'SYSTEM',1401344421),('92F88437-8F5F-4FC7-81CE-B0F88137A4B4','FTP','TENANT_WRAPPER','SYSTEM',1401344421,'SYSTEM',1401344421),('9f4a1bad-8dcd-4197-846f-cdd20dee020f','defaultAutoCommit','DATABASE','SYSTEM',1401344421,'SYSTEM',1401344421),('C96144C4-21FA-462E-B9A6-4E1A23E95365','FTP_ARCHIVE_FOLDER','FTP_BATCH_WRAPPER','SYSTEM',1401344421,'SYSTEM',1401344421),('C96144C4-21FA-462E-B9A6-4E1A23E98659','FTP_PASSWORD','FTP_BATCH_WRAPPER','SYSTEM',1401344421,'SYSTEM',1401344421),('F6F8B3E8-9F5B-42EA-A679-852DFC0C86FB','FTP_USER_NAME','FTP_BATCH_WRAPPER','SYSTEM',1401344421,'SYSTEM',1401344421),('F6F8B3E8-9F5B-42EA-A679-925DFC0C86FB','FTP_ERROR_FOLDER','FTP_BATCH_WRAPPER','SYSTEM',1401344421,'SYSTEM',1401344421),('F6F8B3E8-9F5B-42EA-A680-925DFC0C86FB','FTP_PORT','FTP_BATCH_WRAPPER','SYSTEM',1401344421,'SYSTEM',1401344421),('a2dd8479-6a69-4ba5-b439-8c003bf1a7fe','connectionTimeout','DATABASE','SYSTEM',1401344421,'SYSTEM',1401344421);
/*!40000 ALTER TABLE `SYSTEM_KEY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SYSTEM_MODELETS`
--

DROP TABLE IF EXISTS `SYSTEM_MODELETS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SYSTEM_MODELETS` (
  `HOST_NAME` char(36) NOT NULL,
  `PORT` int(10) NOT NULL,
  `EXEC_LANGUAGE` varchar(20) NOT NULL,
  `MEMBER_HOST` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'hazelcast member host name',
  `EXECUTION_ENVIRONMENT` varchar(100) NOT NULL,
  `POOL_NAME` varchar(100) NOT NULL,
  `R_SERVE_PORT` int(11) DEFAULT NULL,
  `R_MODE` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`HOST_NAME`,`PORT`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Stores all modelet configurations';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SYSTEM_MODELETS`
--

LOCK TABLES `SYSTEM_MODELETS` WRITE;
/*!40000 ALTER TABLE `SYSTEM_MODELETS` DISABLE KEYS */;
INSERT INTO `SYSTEM_MODELETS` VALUES ('10.0.75.1',7902,'R','127.0.0.1','Linux','EAGER_LOAD',0,'rjava'),('10.0.75.1',7903,'R','127.0.0.1','Linux','R-Linux-Default',0,'rjava'),('10.0.75.1',7904,'R','127.0.0.1','Linux','R-Linux-Default',0,'rjava');
/*!40000 ALTER TABLE `SYSTEM_MODELETS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SYSTEM_PARAMETER`
--

DROP TABLE IF EXISTS `SYSTEM_PARAMETER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SYSTEM_PARAMETER` (
  `ID` char(36) COLLATE utf8_bin NOT NULL DEFAULT '',
  `SYS_KEY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `SYS_VALUE` varchar(1500) COLLATE utf8_bin DEFAULT NULL,
  `IS_ACTIVE` char(1) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_ON` bigint(20) DEFAULT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `DESCRIPTION` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SYSTEM_PARAMETER`
--

LOCK TABLES `SYSTEM_PARAMETER` WRITE;
/*!40000 ALTER TABLE `SYSTEM_PARAMETER` DISABLE KEYS */;
INSERT INTO `SYSTEM_PARAMETER` VALUES ('094B30C4-9778-4A12-8EE6-81723C958CB6','batch.threadPoolSize','10','Y','system',1417439330020,'system',1417439330020,NULL),('094B30C4-9778-4A12-8EE6-81723C958CB7','MATLAB_MAX_MODELET_COUNT','1','Y','system',1417439330020,'system',1417439330020,NULL),('094B30C4-9778-4A12-8EE6-81723C958CB8','R_MAX_MODELET_COUNT','1','Y','system',1417439330020,'system',1417439330020,NULL),('094B30C4-9778-4A12-8EE6-81723C958CB9','BOTH_MAX_MODELET_COUNT','0','Y','system',1417439330020,'system',1417439330020,NULL),('094B30C4-9778-4A12-8EE6-81723C958CC1','MATLAB_MIN_MODELET_COUNT','0','Y','system',1417439330020,'system',1417439330020,NULL),('094B30C4-9778-4A12-8EE6-81723C958CC2','R_MIN_MODELET_COUNT','0','Y','system',1417439330020,'system',1417439330020,NULL),('094B30C4-9778-4A12-8EE6-81723C958CC3','BOTH_MIN_MODELET_COUNT','0','Y','system',1417439330020,'system',1417439330020,NULL),('0a4d6264-ef3f-41d4-9f9a-8a01721d3623','umg-runtime-context','/umg-runtime','Y','system',1415354329490,'system',1415354329490,NULL),('24ead742-b6d0-4775-ab78-3daf42026c1a','sanBase','D:\\\\sanpath\\\\san','Y','system',1415354329490,'anil.kamath',1434711465697,NULL),('2d1ebba5-f8ca-4e58-9769-62deb6274e7f','system-exception-error-code-pattern','RSE','Y','system',1415354329490,'system',1415354329490,NULL),('523bd356-f69b-49a1-92d5-171945dd16dd','ftp-deploy-api','/api/batch/ftp/deploy','Y','system',1415354329490,'system',1415354329490,NULL),('523bd356-f69b-49a1-92d5-171960dd16dd','version-deploy-api','/api/deployment/deploy','Y','system',1415354329490,'system',1415354329490,NULL),('52be9e19-5be0-4407-a096-3dabf160a0b7','model-exception-error-code-pattern','RME','Y','system',1415354329490,'system',1415354329490,NULL),('5365967d-f804-11e6-a5d5-00ffbc73cbd1','MODELET_RESTART_ERROR_CODES','MSE0000001,MSE0000002','Y','SYSTEM',5155451518,'SYSTEM',1487673061335,'Comma separated error codes to trigger R modelet restarts incase of execution errors'),('67577854-0203-4404-843B-0E7F90E67F2','max-wait-time-advance-search','30000','Y','system',1417439330020,'system',1427197136815,NULL),('744EBBAC-3916-412E-BB5A-0E559930853B','batch-deploy-api','/api/batch/deploy','Y','system',1417439330020,'system',1417439330020,NULL),('789B7B7F-192A-4BD4-B156-BEDF9C1A00EA','timeout','100','Y','system',1417439330020,'anil.kamath',1493795800181,'sds'),('882AC8C3-9AC0-4953-9A28-541D784C51C5','bulk-test-exec','1','Y','system',1417439330020,'system',1417439330020,NULL),('911DD83E-C514-466A-8768-DED5DAADCC25','me2URL','http://localhost:5050/umg-me2/modelExecEngine/execute','Y','system',1417439330020,'system',1417439330020,NULL),('911DD83E-C514-466A-8768-DED5DAADCC29','baseReportURL','https://ra-rel26-test.altidev.net/umg-api','Y','system',1417439330020,'system',1417439330020,'Base Report URL'),('93A1EDC8-30F2-4376-8AC0-25A88D9E3BD6','DEFAULT_SEARCH_PAGE_SIZE','500','Y','SYSTEM',1486624459,'SYSTEM',1486624473,'DEFAULT DASHBOARD PAGE SEARCH'),('951BFFFE-89D6-4701-9241-29C64B90EA5E','uploadFileTempPath','temp','Y','system',1435816777,'anil.kamath',1436266371220,NULL),('9bc66734-403f-4adb-b37b-a42756f0eb7c','ftp-undeploy-api','/api/batch/ftp/undeploy','Y','system',1415354329490,'system',1415354329490,NULL),('9bc66734-403f-4adb-b37b-a42892f0eb7c','version-undeploy-api','/api/deployment/undeploy','Y','system',1415354329490,'system',1415354329490,NULL),('E2B1135E-2D2B-4AE3-810F-742FF5A70515','MAX_DISPLAY_RECORDS_SIZE','50000','Y','SYSTEM',1486624549,'SYSTEM',1486624576,'MAX DISPLAY PAGE SIZE IN TXN DASHBOARD'),('E69889C1-B81B-4993-93C4-2CA59F7FD78D','batch-undeploy-api','/api/batch/undeploy','Y','system',1417439330020,'system',1417439330020,NULL),('ED43C133-A673-4F98-9054-149B2C7F0694','retryCount','1','Y','system',1417439330020,'anil.kamath',1435140468159,NULL),('FFB80A23-2AF8-4E2F-B3A3-57D3B324B882','max-wait-time-primary-search','10000','Y','system',1417439330020,'system',1427197136815,NULL),('a27df8df-1564-448d-8234-fea06b0b8b50','version-test-api','/api/deployment/test/','Y','system',1415354329490,'system',1415354329490,NULL),('a44071e2-5c1f-45ac-b64c-143b3546767z','batch-timeout','2000','Y','system',1415354329490,'anil.kamath',1507805244313,'jgj'),('a44071e2-5c1f-45ac-b64c-143b3547072b','umg-runtime-pwd','admin','Y','system',1415354329490,'system',1415354329490,NULL),('af5d85e1-47f5-4adc-a77b-1e6bdda18d6f','RA_API_RECORD_LIMIT_METADATA_ONLY','2000','Y','system',1417439330020,'system',1417439330020,'count to set the number of records returned for ra api metadata only'),('af5e85e1-47k5-4ahc-106bdda18d27x','UMG_ADMIN_URL','http://localhost:9090/umg-admin','Y','SYSTEM',1464242110,'SYSTEM',1464242110,'UMG ADMIN URL'),('af5e85e1-47k5-4ahc-106bdda18d69x','FROM_ADDRESS','REALAnalytics-QE@altisource.com','Y',NULL,1464242110,NULL,1464242110,'From address for tenant added'),('af5e85e1-47k5-4ahc-106bdda18d77x','umgAdminUrl','http://localhost:9090/umg-admin','Y','system',1417439330020,'system',1417439330020,'UMG ADMIN URL'),('af5e85e1-47k5-4ahc-9e6bdda18d77x','NOTIFICATION_TEMPLATE_DIR','/usr/tmp/','Y','SYSTEM',1464242110,'SYSTEM',1464242110,'NOTIFICATION_TEMPLATE_DIR'),('af5e85e1-47k5-4ahc-9e6bdda18d88x','SMTP_HOST_NAME','NAV8EHCNMP01.ASCORP.COM','Y','SYSTEM',1464242110,'SYSTEM',1464242110,'SMTP Server Host Name'),('af5e85e1-47k5-4ahc-9e6bdda18d99x','SMTP_SERVER','mail02.svc.den.vz.altidev.net','Y','SYSTEM',1464242110,'SYSTEM',1464242110,'SMTP Server'),('af5e85e1-47k5-4ahc-a74b-9e6bdda18d6v','RA_API_RECORD_LIMIT_TENANT_OUT_ONLY','200','Y','system',1417439330020,'system',1417439330020,'count to set the number of records returned for ra api when tenant output is to be fetched'),('bfd36024-6763-11e5-9c00-00ffbc73cbd1','JMX_MODELET_PORT_MAPPING','7900-9010|7901-9011|7902-9012|7903-9013|7904-9014','Y','system',1435816777,'anil.kamath',1436266371220,NULL),('d3aca61f-4fad-11e5-bee2-00ffbc73cbd1','SSH_PORT','22','Y','SYSTEM',20150831122930,NULL,NULL,NULL),('d3bc69ed-4fad-11e5-bee2-00ffbc73cbd1','SSH_USER','root','Y','SYSTEM',20150831122931,NULL,NULL,NULL),('d3c40561-4fad-11e5-bee2-00ffbc73cbd1','SSH_PASSWORD','','Y','SYSTEM',20150831122931,NULL,NULL,NULL),('d3cc24ff-4fad-11e5-bee2-00ffbc73cbd1','SSH_IDENTITY','C:\\\\Users\\\\kamathan\\\\.ssh\\\\id_rsa','Y','SYSTEM',20150831122931,NULL,NULL,NULL),('d3ce07d9-b0b7-4a3c-8f84-722278ecb2f2','umg-runtime-username','admin','Y','system',1415354329490,'system',1415354329490,NULL),('dsada','MODELET_EXEC_TIME_LIMIT','3600','Y','system',1435816777,'anil.kamath',1512656861829,'DSDD'),('e967c9eb-52fc-11e5-9fee-00ffbc73cbd1','MODELET_RESTART_DELAY','1','Y','system',1435816777,'anil.kamath',1503042786904,'dasdasd'),('f80c748f-1a63-443e-a7db-4414abefeb07','umg-admin-schema','umg_admin','Y','system',145155552522,'anil.kamath',1438852048348,NULL),('f80c748f-1a63-443e-a7db-4414abefeb0a','validation-error-code-pattern','RVE','Y','system',1415354329490,'system',1415354329490,NULL),('faaa2695-96c3-11e7-8edd-ecf4bb04e2cc','MODEL_PUBLISH_STATUS_UPDATE_URL','ws://localhost:8181/umg-admin','Y','system',1505068200,'anil.kamath',1505461594755,'dasdasd'),('fbfeedd1-4fad-11e5-bee2-00ffbc73cbd1','R_MODELET_STARTUP_SCRIPT','export LD_LIBRARY_PATH=/usr/lib64/R/library/rJava/jri:/usr/lib64/R/lib:/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.85.x86_64/jre/lib/amd64/server; export JAVA_HOME=/usr/bin/java; export R_HOME=/usr/lib64/R/; nohup java -XX:MaxPermSize=256m -Xmx1024m  -XX:MinHeapFreeRatio=10 -XX:MaxHeapFreeRatio=30 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=#JMX_PORT# -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Druntime=MATLAB -Dlogroot=#port# -Dloglevel=#port# -Dport=#port# -DserverType=#serverType# -DsanPath=/sanpath -Dworkspace=/opt/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true   -Dlog4j.configurationFile=file:/opt/raconf/log4j2.xml -Dhazelcast.config=/opt/raconf/hazelcast-config.xml -DhttpConnectionPooling.properties=file:/opt/raconf/httpConnectionPooling.properties -DrServePort=#rServePort#-jar /opt/umg/modelet.one-jar.jar > /opt/umg/#port#.out 2>&1 &','Y','SYSTEM',20150831123038,NULL,NULL,NULL),('fgsdsdfshsdhfsghdfgh','MODEL_SIZE_REDUCTION','false','Y','system',1435816777,'anil.kamath',1462520015500,'asdasda'),('qeqeeqw','STRINGS_AS_FACTORS','false','Y','fsdfsd',4532423423,'anil.kamath',1473936178945,'fsdfsdfsdfsdf');
/*!40000 ALTER TABLE `SYSTEM_PARAMETER` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SYSTEM_PARAMETER_AUDIT`
--

DROP TABLE IF EXISTS `SYSTEM_PARAMETER_AUDIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SYSTEM_PARAMETER_AUDIT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL DEFAULT '',
  `SYS_KEY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `SYS_VALUE` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `IS_ACTIVE` char(1) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_ON` bigint(20) DEFAULT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `REV` int(11) NOT NULL DEFAULT '0',
  `DESCRIPTION` varchar(500) COLLATE utf8_bin NOT NULL DEFAULT '0',
  `REVTYPE` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`,`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SYSTEM_PARAMETER_AUDIT`
--

LOCK TABLES `SYSTEM_PARAMETER_AUDIT` WRITE;
/*!40000 ALTER TABLE `SYSTEM_PARAMETER_AUDIT` DISABLE KEYS */;
INSERT INTO `SYSTEM_PARAMETER_AUDIT` VALUES ('24ead742-b6d0-4775-ab78-3daf42026c1a','sanBase','D:\\sanpath\\sanPath1,D:\\sanpath\\sanPath2','Y','system',1415354329490,'anil.kamath',1434707898020,1,'0',1),('24ead742-b6d0-4775-ab78-3daf42026c1a','sanBase','D:\\sanpath\\sanPath1','Y','system',1415354329490,'anil.kamath',1434711465697,2,'0',1),('5365967d-f804-11e6-a5d5-00ffbc73cbd1','MODELET_RESTART_ERROR_CODES','','Y','SYSTEM',5155451518,'anil.kamath',1487669240527,67,'',1),('5365967d-f804-11e6-a5d5-00ffbc73cbd1','MODELET_RESTART_ERROR_CODES','MSE0000001','Y','SYSTEM',5155451518,'anil.kamath',1487671451843,68,'',1),('5365967d-f804-11e6-a5d5-00ffbc73cbd1','MODELET_RESTART_ERROR_CODES','MSE0000001,MSE0000002','Y','SYSTEM',5155451518,'anil.kamath',1487673061335,69,'',1),('789B7B7F-192A-4BD4-B156-BEDF9C1A00EA','timeout','100','Y','system',1417439330020,'anil.kamath',1493795800181,73,'sds',1),('951BFFFE-89D6-4701-9241-29C64B90EA5E','uploadFileTempPath','temp','Y','system',1435816777,'anil.kamath',1436266371220,4,'0',1),('ED43C133-A673-4F98-9054-149B2C7F0694','retryCount','1','Y','system',1417439330020,'anil.kamath',1435140468159,3,'0',1),('a44071e2-5c1f-45ac-b64c-143b3546767z','batch-timeout','2000','Y','system',1415354329490,'anil.kamath',1507805244313,79,'jgj',1),('af5e85e1-47k5-4ahc-106bdda18d77x','umgAdminUrl','http://localhost:9090/umg-admin','Y','system',1417439330020,'system',1417439330020,0,'UMG ADMIN URL',NULL),('af5e85e1-47k5-4ahc-106bdda18d78x','UMG_ADMIN_URL','http://localhost:9090/umg-admin','Y','SYSTEM',1464242110,'SYSTEM',1464242110,0,'UMG ADMIN URL',NULL),('af5e85e1-47k5-4ahc-106bdda18d79x','FROM_ADDRESS',NULL,'Y',NULL,1464242110,NULL,1464242110,0,'TO Address for tenant added',NULL),('af5e85e1-47k5-4ahc-9e6bdda1888x','SMTP_HOST_NAME','NAV8EHCNMP01.ASCORP.COM','Y','SYSTEM',1464242110,'SYSTEM',1464242110,0,'SMTP Server Host Name',NULL),('af5e85e1-47k5-4ahc-9e6bdda1899x','SMTP_SERVER','mail02.svc.den.vz.altidev.net','Y','SYSTEM',1464242110,'SYSTEM',1464242110,0,'SMTP Server',NULL),('af5e85e1-47k5-4ahc-9e6bdda18d77x','NOTIFICATION_TEMPLATE_DIR','/usr/tmp/','Y','SYSTEM',1464242110,'SYSTEM',1464242110,0,'NOTIFICATION_TEMPLATE_DIR',NULL),('dsada','MODELET_EXEC_TIME_LIMIT','30','Y','system',1435816777,'anil.kamath',1492594712838,70,'DSDD',1),('dsada','MODELET_EXEC_TIME_LIMIT','120','Y','system',1435816777,'anil.kamath',1493733108281,71,'DSDD',1),('dsada','MODELET_EXEC_TIME_LIMIT','10','Y','system',1435816777,'anil.kamath',1503042763494,74,'DSDD',1),('dsada','MODELET_EXEC_TIME_LIMIT','1000','Y','system',1435816777,'anil.kamath',1503924181686,77,'DSDD',1),('dsada','MODELET_EXEC_TIME_LIMIT','10','Y','system',1435816777,'anil.kamath',1507805348993,80,'DSDD',1),('dsada','MODELET_EXEC_TIME_LIMIT','300','Y','system',1435816777,'anil.kamath',1510752769490,81,'DSDD',1),('dsada','MODELET_EXEC_TIME_LIMIT','3600','Y','system',1435816777,'anil.kamath',1512656861829,82,'DSDD',1),('e967c9eb-52fc-11e5-9fee-00ffbc73cbd1','MODELET_RESTART_DELAY','0','Y','system',1435816777,'anil.kamath',1503042773195,75,'dasdasd',1),('e967c9eb-52fc-11e5-9fee-00ffbc73cbd1','MODELET_RESTART_DELAY','1','Y','system',1435816777,'anil.kamath',1503042786904,76,'dasdasd',1),('f80c748f-1a63-443e-a7db-4414abefeb07','umg-admin-schema','umg_admin','Y','system',145155552522,'anil.kamath',1438852048348,30,'0',1),('faaa2695-96c3-11e7-8edd-ecf4bb04e2cc','MODEL_PUBLISH_STATUS_UPDATE_URL','ws://localhost:8181/umg-admin','Y','system',1505068200,'anil.kamath',1505461594755,78,'dasdasd',1),('fgsdsdfshsdhfsghdfgh','MODEL_SIZE_REDUCTION','true','Y','system',1435816777,'anil.kamath',1460557830772,51,'asdasda',1),('fgsdsdfshsdhfsghdfgh','MODEL_SIZE_REDUCTION','false','Y','system',1435816777,'anil.kamath',1460558863076,52,'asdasda',1),('fgsdsdfshsdhfsghdfgh','MODEL_SIZE_REDUCTION','true','Y','system',1435816777,'anil.kamath',1460560212845,53,'asdasda',1),('fgsdsdfshsdhfsghdfgh','MODEL_SIZE_REDUCTION','false','Y','system',1435816777,'anil.kamath',1462520015500,54,'asdasda',1),('qeqeeqw','STRINGS_AS_FACTORS','false','Y','fsdfsd',4532423423,'anil.kamath',1473936178945,56,'fsdfsdfsdfsdf',1);
/*!40000 ALTER TABLE `SYSTEM_PARAMETER_AUDIT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TENANT`
--

DROP TABLE IF EXISTS `TENANT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TENANT` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(45) COLLATE utf8_bin NOT NULL COMMENT 'Tenant name',
  `DESCRIPTION` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CODE` varchar(10) COLLATE utf8_bin NOT NULL COMMENT 'Tenant code used for unique identificaiton',
  `TENANT_TYPE` varchar(45) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UN_TENANT_NAME` (`NAME`),
  UNIQUE KEY `UN_TENANT_CODE` (`CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TENANT`
--

LOCK TABLES `TENANT` WRITE;
/*!40000 ALTER TABLE `TENANT` DISABLE KEYS */;
INSERT INTO `TENANT` VALUES ('1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','localhost','Ocwen Financial Corporation','localhost','both','SYSTEM',1401344421,'SYSTEM',1401344421),('2c3f9da5-e3a5-4064-a5c8-861d79f890bc','sdds','dasdasd','umg8274','both','anil.kamath',1495029829639,'anil.kamath',1495029829639);
/*!40000 ALTER TABLE `TENANT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TENANT_CONFIG`
--

DROP TABLE IF EXISTS `TENANT_CONFIG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TENANT_CONFIG` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TENANT_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Tenant where this config parameter belongs to.',
  `SYSTEM_KEY_ID` char(36) COLLATE utf8_bin NOT NULL COMMENT 'Config parameter key.',
  `CONFIG_VALUE` varchar(500) COLLATE utf8_bin DEFAULT NULL COMMENT 'Config parameter value.',
  `CREATED_BY` varchar(100) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  `ROLE` char(20) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UN_TENANT_CONFIG_KEY` (`TENANT_ID`,`SYSTEM_KEY_ID`) COMMENT 'Key for a tenant is unique',
  KEY `FK_TENANT_CONFIG_SYSTEM_KEY_idx` (`SYSTEM_KEY_ID`),
  CONSTRAINT `FK_TENANT_CONFIG_SYSTEM_KEY` FOREIGN KEY (`SYSTEM_KEY_ID`) REFERENCES `SYSTEM_KEY` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_TENANT_CONFIG_TENANT` FOREIGN KEY (`TENANT_ID`) REFERENCES `TENANT` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TENANT_CONFIG`
--

LOCK TABLES `TENANT_CONFIG` WRITE;
/*!40000 ALTER TABLE `TENANT_CONFIG` DISABLE KEYS */;
INSERT INTO `TENANT_CONFIG` VALUES ('01c0e99f-3d31-42e2-a70d-217641f04b0a','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','15a69810-e665-12e3-b687-98654f4fc15c','true','anil.kamath',1436260613606,'anil.kamath',1507620979660,NULL),('0329ef2f-a1b3-4998-97b6-c2501888f728','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','92F88437-8F5F-4FC7-81CE-B0F88137A4B4','false','SYSTEM',1401344421,'anil.kamath',1445249054437,NULL),('05261213-3a8c-4127-b7de-5e59dfaeeaee','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','05be454a-e6f9-11e3-a68a-82687f4fcx15','28200','anil.kamath',1495029829675,'anil.kamath',1495029829675,''),('07ea9dce-ab71-4bc4-9e70-55c26ee40d1d','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','a2dd8479-6a69-4ba5-b439-8c003bf1a7fe','60000','SYSTEM',1401344421,'SYSTEM',1401344421,NULL),('09122e42-09e4-4698-af60-7be1cf381494','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','F6F8B3E8-9F5B-42EA-A679-925DFC0C86FB','','anil.kamath',1495029829657,'anil.kamath',1495029829657,''),('0f596fa8-6159-42af-b38f-bda304aefa5e','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','3d952b55-20cb-11e6-83a0-00ffbc73cbd1',NULL,'anil.kamath',1464680446076,'anil.kamath',1464680446076,NULL),('122cd655-79b1-4d75-9439-93b7764d500f','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','742491a8-fac2-11e3-801d-b2227cce2b54','false','anil.kamath',1436260613601,'anil.kamath',1445249002426,NULL),('1452e0a6-4e90-453e-87db-54e07003031e','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','F6F8B3E8-9F5B-42EA-A680-925DFC0C86FB','','anil.kamath',1495029829666,'anil.kamath',1495029829666,''),('20d1623e-bdf9-408c-a795-fe0b4b6afebb','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','05be5350-e6f9-11e3-a68a-82687f4fc15c','','anil.kamath',1495029829660,'anil.kamath',1495029829660,''),('216151f0-0ffb-4679-9b69-ec0b1417b6bf','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','F6F8B3E8-9F5B-42EA-A679-925DFC0C86FB','error','SYSTEM',1401344421,'SYSTEM',1401344421,NULL),('2535d45c-d91b-4075-894f-82d83861615a','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','05be4a2c-e6f9-11e3-a68a-82687f4fc15c','','anil.kamath',1495029829651,'anil.kamath',1495029829651,''),('38c80c19-0a87-46b9-a024-ac3d687308a6','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','05a68810-e6f9-11e3-a68a-82687f4fc15c','com.mysql.jdbc.Driver','anil.kamath',1495029829654,'anil.kamath',1495029829654,''),('38dc0b75-73a9-458e-a467-f594b06adbd4','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','F6F8B3E8-9F5B-42EA-A679-852DFC0C86FB','','anil.kamath',1495029829665,'anil.kamath',1495029829665,''),('484e36c2-a941-422e-9122-76cf4313cebc','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','138c0981-1293-11e6-b2dd-00ffbc73cbd1','false','anil.kamath',1495029829668,'anil.kamath',1495030393117,''),('4a4b27c7-4b14-44da-9c3b-4f77687e13a4','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','F6F8B3E8-9F5B-42EA-A680-925DFC0C86FB','221','SYSTEM',1401344421,'SYSTEM',1401344421,NULL),('5b66168e-cce7-42d1-9002-c6097f7c78be','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','C96144C4-21FA-462E-B9A6-4E1A23E98659','','SYSTEM',1401344421,'anil.kamath',1445002344514,NULL),('5f23cb52-1ad5-4a08-b818-12da46d80dd9','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','9f4a1bad-8dcd-4197-846f-cdd20dee020f','true','SYSTEM',1401344421,'SYSTEM',1401344421,NULL),('6a82cea7-c079-49d4-bbff-ec062276db3d','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','05be454a-e6f9-11e3-a68a-82687f4fcx16',NULL,'anil.kamath',1495029829662,'anil.kamath',1495030393113,''),('6bc48112-d8c7-4355-918d-6e78ca5dd2ab','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','C96144C4-21FA-462E-B9A6-4E1A23E95365','','anil.kamath',1495029829656,'anil.kamath',1495029829656,''),('6e54e42b-76d1-490a-8450-bb2a4837a9c8','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','05be454a-e6f9-11e3-a68a-82687f4fcx17',NULL,'anil.kamath',1495029829655,'anil.kamath',1495030393115,''),('71dac541-48e8-423c-9724-40162565825c','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','138c0981-1293-11e6-b2dd-00ffbc73cbd1','true','anil.kamath',1464680446067,'anil.kamath',1512989833681,NULL),('71f76f5c-57c3-4ec8-9ee0-b72f925f6c5f','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','C96144C4-21FA-462E-B9A6-4E1A23E98659','','anil.kamath',1495029829672,'anil.kamath',1495029829672,''),('744d383c-43ce-451f-bbc0-2ff5b1675fba','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','74246ec4-fac3-11e3-801d-b2227cce2c54','','anil.kamath',1495029829669,'anil.kamath',1495029829669,''),('755fbf30-c749-42ba-bb6b-7d1f353fd443','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','1A2F140D-B8C1-4CE7-9857-8092C5964653','','SYSTEM',1401344421,'SYSTEM',1401344421,NULL),('7e0bb3f7-92ca-42d0-980f-558143813b8f','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','1A2F140D-B8C1-4CE7-9857-8092C8512653','output','SYSTEM',1401344421,'SYSTEM',1401344421,NULL),('7ef47bd4-9f91-4a11-96cf-3c97952ec039','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','742491a8-fac2-11e3-801d-b2227cce2b54','false','anil.kamath',1495029829658,'anil.kamath',1495029829658,''),('805a2a6a-1e34-4770-9978-9f91c7a16f63','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','15a69810-e665-12e3-b687-98654f4fc15c','false','anil.kamath',1495029829664,'anil.kamath',1495030393118,''),('92c59d89-d285-4d8b-9bae-49e986079aac','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','F6F8B3E8-9F5B-42EA-A679-852DFC0C86FB','','SYSTEM',1401344421,'anil.kamath',1445002344550,NULL),('9f28f270-fac2-11e3-801d-b2227cce2b54','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','74246ec4-fac2-11e3-801d-b2227cce2b54','http://localhost:7171','SYSTEM',1401344421,'SYSTEM',1401344421,NULL),('a66e9423-96bd-4178-b640-74ccf13011d5','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','6EDF46E9-288D-4787-B993-3B5A3B2C2019','input','SYSTEM',1401344421,'SYSTEM',1401344421,NULL),('acd8ca4b-73e0-4ef0-b609-be8cf45ba3ca','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','74246ec4-fac3-11e3-801d-b2227cce2c54',NULL,'anil.kamath',1436260613533,'anil.kamath',1436260613533,NULL),('b321608d-ca67-4599-a9fc-39309ac99943','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','a2dd8479-6a69-4ba5-b439-8c003bf1a7fe','60000','anil.kamath',1495029829663,'anil.kamath',1495029829663,''),('b4bdb35e-110b-4638-9b1a-32ef11bbeb13','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','05be454a-e6f9-11e3-a68a-82687f4fc15c','jdbc:mysql://localhost:3306/localhost','anil.kamath',1495029829661,'anil.kamath',1495029829661,''),('b9579452-11df-440a-99f4-929fd40990e5','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','05be4fe0-e6f9-11e3-a68a-82687f4fc15c','root','anil.kamath',1495029829671,'anil.kamath',1495029829671,''),('bd09c218-9adc-484c-8bc5-8d2d2145dc2c','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','232d9687-8b00-11e6-989d-00059a3c7a00','true','anil.kamath',1475753666509,'anil.kamath',1516356618583,NULL),('bf5a726a-da23-4d9d-9044-d3784de6b611','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','74246ec4-fac2-11e3-801d-b2227cce2b54','http://localhost:7171','anil.kamath',1495029829679,'anil.kamath',1495029829679,''),('c152aa6b-ce58-4610-9f75-def2c8fd64b0','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','9f4a1bad-8dcd-4197-846f-cdd20dee020f','true','anil.kamath',1495029829677,'anil.kamath',1495029829677,''),('c637b633-16b5-461b-95f0-556b8d9bfdad','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','1A2F140D-B8C1-4CE7-9857-8092C8512653','','anil.kamath',1495029829666,'anil.kamath',1495029829666,''),('c7fb30ba-1bd3-422c-ad1a-5b0e2a097d21','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','1A2F140D-B8C1-4CE7-9857-8092C5964653','','anil.kamath',1495029829653,'anil.kamath',1495029829653,''),('ca94b07d-b0f1-44dc-aea2-7d35fd7220e3','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','6EDF46E9-288D-4787-B993-3B5A3B2C2019','','anil.kamath',1495029829661,'anil.kamath',1495029829661,''),('d5a4e0d0-6940-4a73-9c39-1daeb6c3abd0','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','92F88437-8F5F-4FC7-81CE-B0F88137A4B4','false','anil.kamath',1495029829673,'anil.kamath',1495029829673,''),('d5b91561-2b80-4e75-8846-afe3c11b179a','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','3d952b55-20cb-11e6-83a0-00ffbc73cbd1','false','anil.kamath',1495029829652,'anil.kamath',1495029829652,''),('d60de758-e6fb-11e3-aab5-82687f4fc15c','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','05a68810-e6f9-11e3-a68a-82687f4fc15c','com.mysql.jdbc.Driver','SYSTEM',1401344421,'SYSTEM',1401344421,NULL),('d60dea46-e6fb-11e3-aab5-82687f4fc15c','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','05be454a-e6f9-11e3-a68a-82687f4fc15c','jdbc:mysql://localhost:3306/localhost','SYSTEM',1401344421,'SYSTEM',1401344421,NULL),('d60dec58-e6fb-11e3-aab5-82687f4fc15c','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','05be4a2c-e6f9-11e3-a68a-82687f4fc15c','localhost','SYSTEM',1401344421,'SYSTEM',1401344421,NULL),('d60dee42-e6fb-11e3-aab5-82687f4fc15c','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','05be4fe0-e6f9-11e3-a68a-82687f4fc15c','root','SYSTEM',1401344421,'SYSTEM',1401344421,NULL),('d60df036-e6fb-11e3-aab5-82687f4fc15c','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','05be5350-e6f9-11e3-a68a-82687f4fc15c','','SYSTEM',1401344421,'SYSTEM',1401344421,NULL),('e72c9065-511f-4bd1-b2da-ee867988bc47','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','232d9687-8b00-11e6-989d-00059a3c7a00','','anil.kamath',1495029829659,'anil.kamath',1495029829659,''),('f1a0bf2b-99c1-4cce-a911-e0772a9d3838','2c3f9da5-e3a5-4064-a5c8-861d79f890bc','76ea853d-7479-4d25-87fc-cb899043a9e5','10000','anil.kamath',1495029829655,'anil.kamath',1495029829655,''),('f9fda5e5-4956-4cff-93e1-b31579ae3c5d','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','C96144C4-21FA-462E-B9A6-4E1A23E95365','archive','SYSTEM',1401344421,'SYSTEM',1401344421,NULL),('fac86e54-3dce-4996-ac90-61c9b04b1d7X','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','05be454a-e6f9-11e3-a68a-82687f4fcx15','28200','SYSTEM',1401344421,'SYSTEM',1401344421,NULL),('fac86e54-3dce-4996-ac90-61c9b04b1d7c','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','76ea853d-7479-4d25-87fc-cb899043a9e5','10000','SYSTEM',1401344421,'anil.kamath',1445000323085,NULL),('fac86e54-3dce-4996-ac90-61c9b04b1d8X','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','05be454a-e6f9-11e3-a68a-82687f4fcx16',NULL,'SYSTEM',1401344421,'anil.kamath',1495030362006,NULL),('fac86e54-3dce-4996-ac90-61c9b04b1d9X','1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','05be454a-e6f9-11e3-a68a-82687f4fcx17','10','SYSTEM',1401344421,'anil.kamath',1464680446203,NULL);
/*!40000 ALTER TABLE `TENANT_CONFIG` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TENANT_ROLES_MAPPING`
--

DROP TABLE IF EXISTS `TENANT_ROLES_MAPPING`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TENANT_ROLES_MAPPING` (
  `Id` varchar(50) COLLATE utf8_bin NOT NULL,
  `roles_id` varchar(50) COLLATE utf8_bin NOT NULL,
  `tenant_code` varchar(10) COLLATE utf8_bin NOT NULL COMMENT 'Tenant code used for unique identificaiton',
  PRIMARY KEY (`Id`),
  KEY `FK_user_roles_mapping_user_roles` (`roles_id`),
  KEY `FK_tenant_roles_mapping_tenant` (`tenant_code`),
  CONSTRAINT `FK_tenant_roles_mapping_tenant` FOREIGN KEY (`tenant_code`) REFERENCES `tenant` (`CODE`),
  CONSTRAINT `FK_user_roles_mapping_user_roles` FOREIGN KEY (`roles_id`) REFERENCES `roles` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TENANT_ROLES_MAPPING`
--

LOCK TABLES `TENANT_ROLES_MAPPING` WRITE;
/*!40000 ALTER TABLE `TENANT_ROLES_MAPPING` DISABLE KEYS */;
INSERT INTO `TENANT_ROLES_MAPPING` VALUES ('39b37b7b-57d0-4a66-8b19-f8143df7cbd4','028f1293-cbc5-40b9-beba-c53929e6ac33','umg8274'),('d36e5136-0d29-11e6-b78a-00ffde411c75','028f1293-cbc5-40b9-beba-c53929e6ac33','localhost');
/*!40000 ALTER TABLE `TENANT_ROLES_MAPPING` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TENANT_USER_MAPPING`
--

DROP TABLE IF EXISTS `TENANT_USER_MAPPING`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TENANT_USER_MAPPING` (
  `Id` varchar(50) COLLATE utf8_bin NOT NULL,
  `user_id` varchar(50) COLLATE utf8_bin NOT NULL,
  `tenant_code` varchar(10) COLLATE utf8_bin NOT NULL COMMENT 'Tenant code used for unique identificaiton',
  PRIMARY KEY (`Id`),
  UNIQUE KEY `user_id_tenant_id` (`user_id`,`tenant_code`),
  KEY `FK_tenant_user_mapping_tenant_code` (`tenant_code`),
  CONSTRAINT `FK_tenant_user_mapping_tenant_code` FOREIGN KEY (`tenant_code`) REFERENCES `tenant` (`CODE`),
  CONSTRAINT `FK_tenant_user_mapping_users_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TENANT_USER_MAPPING`
--

LOCK TABLES `TENANT_USER_MAPPING` WRITE;
/*!40000 ALTER TABLE `TENANT_USER_MAPPING` DISABLE KEYS */;
INSERT INTO `TENANT_USER_MAPPING` VALUES ('33038ad4-0ddc-11e6-b9ad-00059a3c7a00','0d5ea8ae-1294-11e6-b2dd-00ffbc73cbd1','localhost');
/*!40000 ALTER TABLE `TENANT_USER_MAPPING` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TENANT_USER_TENANT_ROLE_MAPPING`
--

DROP TABLE IF EXISTS `TENANT_USER_TENANT_ROLE_MAPPING`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TENANT_USER_TENANT_ROLE_MAPPING` (
  `Id` varchar(50) COLLATE utf8_bin NOT NULL,
  `tenant_user_map_id` varchar(50) COLLATE utf8_bin NOT NULL,
  `tenant_role_map_id` varchar(50) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `FK_tenant_user_role_mapping_tenant_user_mapping` (`tenant_user_map_id`),
  KEY `FK_tenant_user_role_mapping_tenant_roles_mapping` (`tenant_role_map_id`),
  CONSTRAINT `FK_tenant_user_role_mapping_tenant_roles_mapping` FOREIGN KEY (`tenant_role_map_id`) REFERENCES `tenant_roles_mapping` (`Id`),
  CONSTRAINT `FK_tenant_user_role_mapping_tenant_user_mapping` FOREIGN KEY (`tenant_user_map_id`) REFERENCES `tenant_user_mapping` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TENANT_USER_TENANT_ROLE_MAPPING`
--

LOCK TABLES `TENANT_USER_TENANT_ROLE_MAPPING` WRITE;
/*!40000 ALTER TABLE `TENANT_USER_TENANT_ROLE_MAPPING` DISABLE KEYS */;
INSERT INTO `TENANT_USER_TENANT_ROLE_MAPPING` VALUES ('9aafc604-1293-11e6-89ca-00ffde411c75','33038ad4-0ddc-11e6-b9ad-00059a3c7a00','d36e5136-0d29-11e6-b78a-00ffde411c75');
/*!40000 ALTER TABLE `TENANT_USER_TENANT_ROLE_MAPPING` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TRANSPORT_PARAMETERS`
--

DROP TABLE IF EXISTS `TRANSPORT_PARAMETERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TRANSPORT_PARAMETERS` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `TRANSPORT_TYPE_ID` varchar(45) COLLATE utf8_bin NOT NULL,
  `PARAMETER_NAME` varchar(45) COLLATE utf8_bin NOT NULL,
  `DEFAULT_VALUE` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_BY` char(36) COLLATE utf8_bin NOT NULL COMMENT 'User created the record.',
  `CREATED_ON` bigint(20) NOT NULL COMMENT 'Record created time.',
  `LAST_UPDATED_BY` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'User last updated the record.',
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL COMMENT 'Record last updated time.',
  PRIMARY KEY (`ID`),
  KEY `FK_TRANSPORT_PARAMETERS_TRANSPORT_TYPE` (`TRANSPORT_TYPE_ID`),
  CONSTRAINT `FK_TRANSPORT_PARAMETERS_TRANSPORT_TYPE` FOREIGN KEY (`TRANSPORT_TYPE_ID`) REFERENCES `TRANSPORT_TYPES` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TRANSPORT_PARAMETERS`
--

LOCK TABLES `TRANSPORT_PARAMETERS` WRITE;
/*!40000 ALTER TABLE `TRANSPORT_PARAMETERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `TRANSPORT_PARAMETERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TRANSPORT_TYPES`
--

DROP TABLE IF EXISTS `TRANSPORT_TYPES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TRANSPORT_TYPES` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(45) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` char(36) COLLATE utf8_bin NOT NULL COMMENT 'User created the record.',
  `CREATED_ON` bigint(20) NOT NULL COMMENT 'Record created time.',
  `LAST_UPDATED_BY` char(36) COLLATE utf8_bin DEFAULT NULL COMMENT 'User last updated the record.',
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL COMMENT 'Record last updated time.',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TRANSPORT_TYPES`
--

LOCK TABLES `TRANSPORT_TYPES` WRITE;
/*!40000 ALTER TABLE `TRANSPORT_TYPES` DISABLE KEYS */;
/*!40000 ALTER TABLE `TRANSPORT_TYPES` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `USERS`
--

DROP TABLE IF EXISTS `USERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `USERS` (
  `Id` char(36) COLLATE utf8_bin NOT NULL,
  `USERNAME` varchar(50) COLLATE utf8_bin NOT NULL,
  `PASSWORD` varchar(100) COLLATE utf8_bin NOT NULL,
  `ENABLED` tinyint(4) NOT NULL DEFAULT '1',
  `NAME` varchar(126) COLLATE utf8_bin DEFAULT NULL COMMENT 'Name of User',
  `sys_admin` enum('true','false') COLLATE utf8_bin NOT NULL DEFAULT 'false' COMMENT 'to set user as sys-admin',
  `rf_user` enum('true','false') COLLATE utf8_bin NOT NULL DEFAULT 'false' COMMENT 'to set user as rf-user',
  `OFFICIAL_EMAIL` varchar(252) COLLATE utf8_bin DEFAULT NULL COMMENT 'Official E-mail ID of User',
  `ORGANIZATION` varchar(126) COLLATE utf8_bin DEFAULT NULL COMMENT 'Organization of User',
  `COMMENTS` varchar(252) COLLATE utf8_bin DEFAULT NULL COMMENT 'Comment of User',
  `CREATED_ON` bigint(20) DEFAULT NULL COMMENT 'Created Date in milliseconds in GMT',
  `LAST_ACTIVATED_ON` bigint(20) DEFAULT NULL COMMENT 'Last Activated Date of this User in milliseconds in GMT',
  `LAST_DEACTIVATED_ON` bigint(20) DEFAULT NULL COMMENT 'Last Deactivated Date of this User in milliseconds in GMT',
  PRIMARY KEY (`Id`),
  UNIQUE KEY `username` (`USERNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `USERS`
--

LOCK TABLES `USERS` WRITE;
/*!40000 ALTER TABLE `USERS` DISABLE KEYS */;
INSERT INTO `USERS` VALUES ('0d5ea8ae-1294-11e6-b2dd-00ffbc73cbd1','anil.kamath','$2a$10$4ntcyw7gZVG6QTH0iKPf8euXvwA2Y4xvaX4cE6Cl3BT.LZFKzzzmS',1,NULL,'true','false',NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `USERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `USERS_LOGIN_AUDIT`
--

DROP TABLE IF EXISTS `USERS_LOGIN_AUDIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `USERS_LOGIN_AUDIT` (
  `username` varchar(50) COLLATE utf8_bin NOT NULL,
  `tenant_code` varchar(10) COLLATE utf8_bin NOT NULL COMMENT 'Tenant code used for unique identificaiton',
  `sys_ip_address` varchar(15) COLLATE utf8_bin NOT NULL,
  `access_on` bigint(20) NOT NULL,
  `activity` varchar(126) COLLATE utf8_bin NOT NULL,
  `reason_code` varchar(32) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`access_on`),
  KEY `FK_USERS_LOGIN_AUDIT_KEY` (`username`,`tenant_code`),
  CONSTRAINT `FK_users_login_audit_users` FOREIGN KEY (`username`) REFERENCES `USERS` (`USERNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `USERS_LOGIN_AUDIT`
--

LOCK TABLES `USERS_LOGIN_AUDIT` WRITE;
/*!40000 ALTER TABLE `USERS_LOGIN_AUDIT` DISABLE KEYS */;
INSERT INTO `USERS_LOGIN_AUDIT` VALUES ('anil.kamath','localhost','0:0:0:0:0:0:0:1',1462446570295,'Logout Success','BSE0000515'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1462446572175,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1462448126819,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1462448889579,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1462450912304,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1462451688410,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1462452497610,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1462455663625,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1462456219126,'Logout Success','BSE0000515'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1462456220694,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1462471615862,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1462517278072,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1462524631052,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1462525924060,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1462528561044,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1462528608127,'Logout Success','BSE0000515'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1463387514904,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1463392401900,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1463396646248,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1463411844549,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1463459174548,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1463460561845,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1463464214744,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1463562642651,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1463569062036,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1463571085299,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1463990567999,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1463990691369,'Logout Success','BSE0000515'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1463990878380,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1463995359453,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1463996030575,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1463996982984,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464007928411,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464010125571,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464013018072,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464016005596,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464017662153,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464017841003,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464018032627,'Logout Success','BSE0000515'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464018065977,'Login Success','BSE0000513'),('anil.kamath','fsdfsf','0:0:0:0:0:0:0:1',1464075066457,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464076067793,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464076100863,'Logout Success','BSE0000515'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464079534648,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464082267714,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464084081988,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464161672915,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464161813090,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464167204472,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464182493248,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464335183818,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464339885064,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464339896843,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464340989805,'Logout Success','BSE0000515'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464355368426,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464540034253,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464680235289,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464680747018,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464684449498,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464686294888,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464689970996,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464691761843,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464692250327,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1464692487067,'Logout Success','BSE0000515'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1465205417541,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1465206009771,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1465207992852,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1465278243833,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1465278977005,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1465281300530,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1465291249943,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1466092913082,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1466096667952,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1466144426864,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1466146522945,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1466148889201,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1466155124250,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1466160623879,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1466163218599,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1466167839370,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1466406913519,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1466409482297,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1466415597834,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1468243900079,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1468244715780,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1468309739876,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1468934975979,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1469093862903,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1469097808833,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1469099420806,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1469099737030,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1469100442438,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1469102507720,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1469102820565,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1469103430603,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1469110598782,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1471527942644,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1471528442064,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1471592574415,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1471864419569,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1471866914211,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1471866931378,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1471869693972,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1471870218800,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1472021050405,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1472021089730,'Logout Success','BSE0000515'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1472211807671,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1472450802300,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1472454182933,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1472454666169,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1472610872508,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1472611417013,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1472627828195,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1472628993615,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1472638457400,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1472639057845,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1472654480153,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1473756343675,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1473763343096,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1473765609729,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1473769823251,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1473835737610,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1473850307500,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1473857304714,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1473859237927,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1473936158725,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1473950623078,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1473993703463,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1474283115611,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1474283269544,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1474286160936,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1474286187115,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1474463449077,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1474465994872,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1474467209151,'Logout Success','BSE0000515'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1474467686019,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1474529250204,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1474632821847,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1474635928313,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1474873308139,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1474875143659,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1474880885779,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1474887533982,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1474889850604,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1474893025795,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1474893578074,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1474894200529,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1474897206410,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1474897229813,'Logout Success','BSE0000515'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1474898665183,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1474899067632,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1474975024545,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475064693770,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475069424064,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475072765917,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475074526497,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475145481051,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475147897029,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475150217872,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475154058710,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475154926600,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475587781642,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475647681516,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475649675549,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475653678652,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475657301636,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475669579339,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475673023294,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475674659694,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475732458381,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475738143525,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475751374316,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475753238719,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475763038545,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475764565491,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475765093701,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475767367857,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475768791791,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475770048670,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475770246348,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475771624627,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475772091525,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475772100369,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475818644815,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475825305712,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475839253563,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475840213355,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475841725777,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1475842452613,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476279468405,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476431139122,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476440384396,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476443714616,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476449369654,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476451810136,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476452271907,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476452812192,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476685003822,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476689232794,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476704864293,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476706566953,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476712612599,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476771006544,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476773906834,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476781952041,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476782224958,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476785255310,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476874591006,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476944103702,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476944452342,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476944776173,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476945363810,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476949106680,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476950394217,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476954073865,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476956565314,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476957108985,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476964722824,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476965559913,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476968084756,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1476971329063,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477033374740,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477033856463,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477036702560,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477041224250,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477044114795,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477048926473,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477293537802,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477293976067,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477311781375,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477312321004,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477314459722,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477314762574,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477375679736,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477396071257,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477397735552,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477398498585,'Logout Success','BSE0000515'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477398500235,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477402056482,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477403412919,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477403721963,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477404328614,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477404515983,'Logout Success','BSE0000515'),('anil.kamath','abc','0:0:0:0:0:0:0:1',1477404517413,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477405395851,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477405623875,'Logout Success','BSE0000515'),('anil.kamath','abc','0:0:0:0:0:0:0:1',1477405625255,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477463222809,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477467908377,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477479679814,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477480140071,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477481937891,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477483037341,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477553900512,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477554045454,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477559794271,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477562753200,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477563162188,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477565665286,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477569642135,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477570580040,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477570832213,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477571643288,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477571824710,'Logout Success','BSE0000515'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477663662093,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1477664098090,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1478693110627,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1478695559593,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1478696149868,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1478697273836,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1478777411000,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1478786282369,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1478787835123,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1478845825855,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1478851014002,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1478857819369,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1478861784807,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1478866267913,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1478869307517,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1478870068862,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1479108144619,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1479116452498,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1479122942621,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1479124339747,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1479131465681,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1479132010396,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1479132421728,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1479133118063,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1479212592938,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1479213208276,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1479277575126,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1480492471304,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1480496754450,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1480501121084,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1480501265575,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1480507632066,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1480513307432,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1480514854655,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1480517483927,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1480583112574,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1480588341235,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1480588781033,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1480590315165,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1480591219890,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1480600348106,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1480669499190,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1480673091598,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1480939180972,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481016846378,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481017628056,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481019704212,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481029743056,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481106541678,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481521338852,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481524295768,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481525773483,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481614478130,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481615561567,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481620249014,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481620462235,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481623766352,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481624142041,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481625962107,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481628205007,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481631756319,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481634347921,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481706205966,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481711185974,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481711477292,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481712329867,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481712490863,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481713155135,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1481715544826,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1483354909658,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1483355082393,'Logout Success','BSE0000515'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1483355083724,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1483426689180,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1483428034848,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1483428201660,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1483429814112,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1483430508323,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1483434442655,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1483434947044,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1483441419314,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1483700553874,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1483953293597,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1483956037904,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486451355577,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486547164926,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486547925777,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486631408185,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486634147987,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486634780331,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486635445258,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486636600417,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486906538963,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486907790058,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486907957186,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486908050787,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486919936520,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486922041843,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486922461096,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486965396988,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486965749780,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486966056268,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486967258433,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486967536976,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486967717194,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486968553401,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486969092492,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486969286046,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486972247457,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486974402372,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486974729688,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1486980927533,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1487008071701,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1487052442612,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1487055837827,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1487058516108,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1487063535326,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1487065397375,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1487155586396,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1487158401316,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1487228242332,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1487231466763,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1487662237714,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1487663061732,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1487668054755,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1487669136449,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1487671309970,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1487672938055,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1487673022413,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1488375376010,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1488377855965,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1488378537249,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1488378581445,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1488463611847,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1488463892746,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1488465111240,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1488521279999,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1488521800701,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1488524511439,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1488526370840,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1488527013449,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1488783938194,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1488784513513,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1488793473532,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1488794665386,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1488796378424,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1488798480504,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1488798590454,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1488798825912,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1488800573005,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1488804441732,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1488958451753,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1488964210422,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489053770366,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489057207327,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489059522856,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489060144862,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489061561761,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489062115966,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489062308266,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489062855072,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489065430800,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489065821997,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489069348895,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489069797029,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489070852843,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489070978812,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489071774039,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489072419057,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489123048234,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489123555094,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489126075148,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489126277711,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489126778607,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489127252896,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489127503742,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489138156661,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489138517380,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489138889754,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489139438287,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489140540991,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489142378888,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489143715055,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489143906984,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489144008163,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489568703755,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489568941485,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489577482049,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489577615365,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489578301821,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489578356161,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489579383756,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489580754432,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489585239042,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489651121141,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489651519275,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489660463603,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489660712337,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1489672582735,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1491982280979,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1491982501956,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1491983016397,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1491983207685,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492000249572,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492000355829,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492068674764,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492074143726,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492074341519,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492074738068,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492074770606,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492077266198,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492077270488,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492077578800,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492077580587,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492079701680,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492079703128,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492081233441,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492081237305,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492085962038,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492086344932,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492086369502,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492438528418,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492438742677,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492438857090,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492440645610,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492440741793,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492593379598,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492594603989,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1492595456601,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1493719448497,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1493719648358,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1493720347628,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1493723585666,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1493727053041,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1493728353928,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1493732817011,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1493732967260,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1493734868409,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1493734871339,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1493735216535,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1493735218219,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1493793665628,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494246694817,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494320799550,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494321208299,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494396312884,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494399335798,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494399562194,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494402900929,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494407720981,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494412346951,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494416010046,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494416702804,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494424840863,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494425185478,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494425797011,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494426127212,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494426373660,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494426454779,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494426839256,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494850348193,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494852328896,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494852334696,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494852956951,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494853249678,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494856943101,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494859461047,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494859823581,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494920874496,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1494925929941,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1495006736394,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1495028959861,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1495029878379,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1495030028657,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1495461166469,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1495461720108,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1495461722293,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1495533831402,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1495535619591,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1495540800571,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1495631065829,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1495631210005,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1496058749826,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1496059074588,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1496319605556,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1496324716404,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1496821169624,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1496821790360,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1496822661432,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1496826673413,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1496828297998,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1496829080141,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1496829742155,'Login Success','BSE0000513'),('anil.kamath','localhost','10.207.112.25',1500455567886,'Login Success','BSE0000513'),('anil.kamath','localhost','10.207.108.84',1500458487539,'Login Success','BSE0000513'),('anil.kamath','localhost','10.207.112.92',1500462614549,'Login Success','BSE0000513'),('anil.kamath','localhost','10.207.112.92',1500462621553,'Login Success','BSE0000513'),('anil.kamath','localhost','10.207.108.84',1500463559031,'Login Success','BSE0000513'),('anil.kamath','localhost','10.207.112.6',1500467423673,'Login Success','BSE0000513'),('anil.kamath','localhost','10.207.112.3',1500472206198,'Login Success','BSE0000513'),('anil.kamath','localhost','10.207.112.6',1500472924364,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1500551993787,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1500552670114,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1500554398615,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1500554584878,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1500557200354,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1500557392231,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1500620943608,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1500902006940,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1500906493355,'Login Success','BSE0000513'),('anil.kamath','localhost','10.207.108.126',1500975783333,'Login Success','BSE0000513'),('anil.kamath','localhost','10.207.108.126',1500975792037,'Logout Success','BSE0000515'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1500979157516,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1500981974699,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1500981976140,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1500990107689,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1500992000449,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1500993303214,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1500994845502,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1500995060011,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1500996604822,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1500999221701,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1501053479399,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1501165844959,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1501505108194,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1501584843984,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1501587632911,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1501590735224,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1501598397011,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1501600732520,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1501601995855,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1501604736962,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1501658598133,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1501667361441,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1501685809064,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1501762428110,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1501767307457,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1501769839598,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1501771168600,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1502190729884,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1502192251048,'Logout Success','BSE0000515'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1502262320974,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1502264232821,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1502269895536,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1502359836011,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1502361869731,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1502366008898,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1502369565975,'Logout Success','BSE0000515'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1502972581811,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1502973273866,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1502975939797,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1502979124043,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1502980882578,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1503039514965,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1503041063154,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1503042480622,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1503042736927,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1503047565032,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1503049734338,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1503049794305,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1503050135895,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1503050342184,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1503050652490,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1503922417722,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1503931348069,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1503993182735,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504011260754,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504095626045,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504103505853,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504105137180,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504107081857,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504165958415,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504170209635,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504174974788,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504188757391,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504192009329,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504194266176,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504267041712,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504272995192,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504274452917,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504509334511,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504531722515,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504596654915,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504597574330,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504602864007,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504603793427,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504604387300,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504604388909,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504606099087,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504610565612,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504610672773,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504613327179,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504613333473,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504620069463,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504695174575,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504700867810,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504704203369,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504768084389,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504769433692,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504780532916,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504785626896,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504792130128,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504793638705,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504851570261,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504854875453,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504856377015,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504868925444,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504873944620,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1504882213040,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505124738489,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505127832908,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505130535534,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505131119919,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505131574492,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505131661357,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505215786608,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505218276642,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505220646595,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505226528964,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505227103848,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505297868674,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505384151056,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505388344040,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505390352049,'Logout Success','BSE0000515'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505390354232,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505461315502,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505462564972,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505467844061,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505470439395,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505721731758,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505739491606,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505743407907,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505806633911,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505813223531,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505815622325,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505817354156,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505820992589,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1505825628234,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1507121248176,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1507199290692,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1507202630942,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1507620874370,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1507633212250,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1507636597178,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1507704587302,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1507704778020,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1507709531151,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1507717569130,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1507719397974,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1507803616655,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1507804339251,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1507804672352,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1507804931998,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1507805193658,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1510752592366,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1510752737456,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1510753454609,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1510753933702,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1510754364071,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1512024881361,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1512026757746,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1512026772587,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1512026775545,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1512026776889,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1512026777797,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1512026784304,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1512122106587,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1512124598671,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1512124825839,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1512125634884,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1512655016540,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1512656297780,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1512657698040,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1512988750666,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1512989487600,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1512993402936,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1512994295755,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1512997618823,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1513004862159,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1513069663484,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1513079892137,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1513085310363,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1513087659890,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1513089518104,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1513090698748,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1513091993156,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1513150436774,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1513151868623,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1513152253543,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1513156201553,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1513159044762,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1513160994413,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1513163989895,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1513165011219,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1513168623936,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1513168785273,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1513169613464,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1513169615187,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1514979646336,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1514982035620,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1514987324778,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1516000603722,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1516012002585,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1516088426239,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1516095210884,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1516356166299,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1516358268926,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1516623076578,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1516706992157,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1516707154359,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1516708312853,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1516708426441,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1516708510744,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1517233815249,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1517319216650,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1517319259406,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1522074787055,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1522076985873,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1522077265593,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1522134257920,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1522135638797,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1522136375538,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1522141837690,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1522143784596,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1522315054173,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1522315335294,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1527154796506,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1549535778152,'Login Success','BSE0000513'),('anil.kamath','localhost','0:0:0:0:0:0:0:1',1549536136436,'Logout Success','BSE0000515');
/*!40000 ALTER TABLE `USERS_LOGIN_AUDIT` ENABLE KEYS */;
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

-- Dump completed on 2019-02-07 19:33:22
