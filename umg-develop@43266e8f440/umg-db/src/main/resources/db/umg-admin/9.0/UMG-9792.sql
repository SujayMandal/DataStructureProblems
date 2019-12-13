USE umg_admin;

SET @CREATED_BY = 'SYSTEM';
SET @CREATED_ON =  UNIX_TIMESTAMP();

-- Dumping structure for table MODELET_PROFILER
CREATE TABLE IF NOT EXISTS `MODELET_PROFILER` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `EXECUTION_ENV_ID` char(36) COLLATE utf8_bin NOT NULL,
  `NAME` char(36) COLLATE utf8_bin NOT NULL,
  `DESCRIPTION` char(255) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` char(50) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(50) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `EXECU_ENV_ID_FK` (`EXECUTION_ENV_ID`),
  CONSTRAINT `EXECU_ENV_ID_FK` FOREIGN KEY (`EXECUTION_ENV_ID`) REFERENCES `MODEL_EXECUTION_ENVIRONMENTS` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Table to store Model profiler';



-- Dumping structure for table MODELET_PROFILER_KEY
CREATE TABLE IF NOT EXISTS `MODELET_PROFILER_KEY` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `NAME` char(36) COLLATE utf8_bin NOT NULL,
  `CODE` char(36) COLLATE utf8_bin NOT NULL,
  `TYPE` char(36) COLLATE utf8_bin NOT NULL,
  `DELIMITTER` char(5) COLLATE utf8_bin NOT NULL,
  `DESCRIPTION` char(255) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` char(50) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(50) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Table to store Model profiler Key';


-- Dumping structure for table MODELET_PROFILER_PARAM
CREATE TABLE IF NOT EXISTS `MODELET_PROFILER_PARAM` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `PROFILER_ID` char(36) COLLATE utf8_bin NOT NULL,
  `PROFILER_KEY_ID` char(36) COLLATE utf8_bin NOT NULL,
  `PARAM_VALUE` varchar(500) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` char(50) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(50) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_profiler_param_profiler` (`PROFILER_ID`),
  KEY `FK_profiler_param_profiler_key` (`PROFILER_KEY_ID`),
  CONSTRAINT `FK_profiler_param_profiler` FOREIGN KEY (`PROFILER_ID`) REFERENCES `MODELET_PROFILER` (`ID`),
  CONSTRAINT `FK_profiler_param_profiler_key` FOREIGN KEY (`PROFILER_KEY_ID`) REFERENCES `MODELET_PROFILER_KEY` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Table to store Model profiler param';

CREATE TEMPORARY TABLE `SYSTEM_MODELETS_TEMP`
	SELECT * FROM `SYSTEM_MODELETS`;

DROP TABLE `SYSTEM_MODELETS`;

-- Dumping structure for table umg_admin.SYSTEM_MODELETS
CREATE TABLE IF NOT EXISTS `SYSTEM_MODELETS` (
  `ID` char(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `HOST_NAME` char(36) NOT NULL,
  `PORT` int(10) NOT NULL,
  `EXEC_LANGUAGE` varchar(20) NOT NULL,
  `MEMBER_HOST` varchar(36) NOT NULL,
  `EXECUTION_ENVIRONMENT` varchar(32) NOT NULL DEFAULT 'Linux' COMMENT 'Execution environment',
  `POOL_NAME` varchar(100) NOT NULL,
  `R_SERVE_PORT` int(10) DEFAULT '0',
  `R_MODE` varchar(10) DEFAULT 'rJava',
  `CREATED_BY` varchar(100) NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` varchar(100) DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `HOST_NAME_PORT` (`HOST_NAME`,`PORT`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Stores all modelet configurations';

INSERT INTO `SYSTEM_MODELETS` (`ID`, `HOST_NAME`, `PORT`, `EXEC_LANGUAGE`, `MEMBER_HOST`, `EXECUTION_ENVIRONMENT`, `POOL_NAME`, `R_SERVE_PORT`, `R_MODE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
	SELECT UUID(), `HOST_NAME`, `PORT`, `EXEC_LANGUAGE`, `MEMBER_HOST`, `EXECUTION_ENVIRONMENT`, `POOL_NAME`, `R_SERVE_PORT`, `R_MODE`, @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON FROM `SYSTEM_MODELETS_TEMP`;

DROP TEMPORARY TABLE `SYSTEM_MODELETS_TEMP`;


-- Dumping structure for table umg_admin.SYSTEM_MODELET_PROFILER_MAP
CREATE TABLE IF NOT EXISTS `SYSTEM_MODELET_PROFILER_MAP` (
  `ID` char(36) COLLATE utf8_bin NOT NULL,
  `SYSTEM_MODELET_ID` char(36) COLLATE utf8_bin NOT NULL,
  `PROFILER_ID` char(36) COLLATE utf8_bin NOT NULL,
  `CREATED_BY` char(50) COLLATE utf8_bin NOT NULL,
  `CREATED_ON` bigint(20) NOT NULL,
  `LAST_UPDATED_BY` char(50) COLLATE utf8_bin DEFAULT NULL,
  `LAST_UPDATED_ON` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_SYSTEM_MODELET_PROFILER_MAP_MODELET_PROFILER` (`PROFILER_ID`),
  KEY `FK_SYSTEM_MODELET_PROFILER_MAP_SYSTEM_MODELETS` (`SYSTEM_MODELET_ID`),
  CONSTRAINT `FK_SYSTEM_MODELET_PROFILER_MAP_MODELET_PROFILER` FOREIGN KEY (`PROFILER_ID`) REFERENCES `MODELET_PROFILER` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_SYSTEM_MODELET_PROFILER_MAP_SYSTEM_MODELETS` FOREIGN KEY (`SYSTEM_MODELET_ID`) REFERENCES `SYSTEM_MODELETS` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


SET @PROFILER_TYPE_EXPORT = 'EXPORT';
SET @PROFILER_TYPE_X_ARG = 'X_ARG';
SET @PROFILER_TYPE_D_ARG = 'D_ARG';
SET @PROFILER_TYPE_D_ARG_ST = 'D_ARG_START';
SET @DELIMITTER1 = '=';
SET @DELIMITTER2 = '';

INSERT INTO `MODELET_PROFILER_KEY` (`ID`, `NAME`, `CODE`, `TYPE`, `DELIMITTER`, `DESCRIPTION`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES
	(UUID(), 'LD_LIBRARY_PATH', 'LD_LIBRARY_PATH', @PROFILER_TYPE_EXPORT, @DELIMITTER1, 'LD library path', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON),
	(UUID(), 'JAVA_HOME', 'JAVA_HOME', @PROFILER_TYPE_EXPORT, @DELIMITTER1, 'JAVA Home', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON),
	(UUID(), 'R_HOME', 'R_HOME', @PROFILER_TYPE_EXPORT, @DELIMITTER1, 'R Home', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON),
	(UUID(), 'MAX_PERM_SIZE', 'X:MaxPermSize', @PROFILER_TYPE_X_ARG, @DELIMITTER1, 'Max perm gen size', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON),
	(UUID(), 'MIN_HEAP_FREE_RATIO', 'X:MinHeapFreeRatio', @PROFILER_TYPE_X_ARG, @DELIMITTER1, 'Minimum heap free ration', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON),
	(UUID(), 'MAX_HEAP_FREE_RATIO', 'X:MaxHeapFreeRatio', @PROFILER_TYPE_X_ARG, @DELIMITTER1, 'Maximum heap free ration', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON),
	(UUID(), 'MAX_HEAP_MEMORY', 'mx', @PROFILER_TYPE_X_ARG, @DELIMITTER2, 'Maximum memory', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON),
	(UUID(), 'INIT_HEAP_MEMORY', 'ms', @PROFILER_TYPE_X_ARG, @DELIMITTER2, 'Initial heap memory', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON),
	(UUID(), 'CMS_INIT_OCCUP', 'X:+UseCMSInitiatingOccupancyOnly', @PROFILER_TYPE_X_ARG, @DELIMITTER2, 'Initiation occupancy', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON),
	(UUID(), 'CMS_INIT_FRAC', 'X:CMSInitiatingOccupancyFraction', @PROFILER_TYPE_X_ARG, @DELIMITTER1, 'Initiation occupancy fraction', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON),
	(UUID(), 'USE_PAR_NEW_GC', 'X:+UseParNewGC', @PROFILER_TYPE_X_ARG, @DELIMITTER2, 'User per new GC', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON),
	(UUID(), 'GC', 'X:+UseConcMarkSweepGC', @PROFILER_TYPE_X_ARG, @DELIMITTER2, 'Mark Sweep GC', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON),
	(UUID(), 'HAZELCAST_CONFIG_FILE', 'hazelcast.config', @PROFILER_TYPE_D_ARG, @DELIMITTER1, 'Hazelcast configuration property file', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON),
	(UUID(), 'HTTP_CONNECTION_POOLING_CONFIG_FILE', 'httpConnectionPooling.properties', @PROFILER_TYPE_D_ARG, @DELIMITTER1, 'http connection poolling property file', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON),
	(UUID(), 'WORKSPACE', 'workspace', @PROFILER_TYPE_D_ARG, '=', 'Workspace path', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON),
	(UUID(), 'LOG_LEVEL', 'loglevel', @PROFILER_TYPE_D_ARG, '=', 'Log Level', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON),
	(UUID(), 'EXECUTION_ENVIRONMENT', 'executionEnvironment', @PROFILER_TYPE_D_ARG, '=', 'Execution Environment', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON),
	(UUID(), 'R_TEMP_PATH', 'rTempPath', @PROFILER_TYPE_D_ARG_ST, '=', 'R Temp Path', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON),
	(UUID(), 'JAVA_LIBRARY_PATH', 'java.library.path', @PROFILER_TYPE_D_ARG_ST, '=', 'JAVA Library Path', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);

UPDATE `SYSTEM_PARAMETER` SET `SYS_VALUE` = '#EXPORT_PARAMS# nohup java #X_ARGS# #D_ARGS# -Dlogroot=#port# -Dport=#port# -DrServePort=#rServePort# -DrMode=#rMode# -DserverType=#serverType# -DsanPath=/sanpath  -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -Dlog4j.configurationFile=file:/opt/raconf/log4j2.xml -DisThreadContextMapInheritable=true -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=#JMX_PORT# -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -DexecutionLanguage=R -DexecutionEnvironment=Linux -jar /opt/ra-modelet/umg/modelet.one-jar.jar > /opt/ra-modelet/umg/#port#.out 2>&1 &' WHERE `SYS_KEY` = 'R_MODELET_STARTUP_SCRIPT_RJAVA';

UPDATE `SYSTEM_PARAMETER` SET `SYS_VALUE` = '#EXPORT_PARAMS#\ntar -zcvf /opt/ra-modelet/umg/log_backup/#port#_Rserve_\`date +%Y%m%d_%H%M%S`.tar.gz /opt/ra-modelet/umg/#port#_Rserve.log;\nR CMD Rserve --RS-port #rServePort# --vanilla > /opt/ra-modelet/umg/#port#_Rserve.log ;\nsleep 5s;\nnohup java #X_ARGS# #D_ARGS# -Dlogroot=#port# -Dport=#port# -DrServePort=#rServePort# -DrMode=#rMode# -DserverType=#serverType# -DsanPath=/sanpath -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -Dlog4j.configurationFile=file:/opt/raconf/log4j2.xml -DisThreadContextMapInheritable=true -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=#JMX_PORT# -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -DexecutionLanguage=R -DexecutionEnvironment=Linux -jar /opt/ra-modelet/umg/modelet.one-jar.jar > /opt/ra-modelet/umg/#port#.out 2>&1 &' WHERE `SYS_KEY` = 'R_MODELET_STARTUP_SCRIPT_RSERVE';

UPDATE `SYSTEM_PARAMETER` SET `SYS_VALUE` = 'nohup java #X_ARGS# #D_ARGS# -Druntime=R -Dlogroot=#port# -Dport=#port# -DserverType=#serverType# -DsanPath=Z:\ -DrTempPath=C:\Workspace\matlab -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -Dlog4j.configurationFile=file:/opt/raconf/log4j2.xml -DisThreadContextMapInheritable=true -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9026 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Djava.library.path="C:\Installed\rJava\jri\x64;C:\Program Files\Java\jdk1.8.0_191\bin;" -DexecutionLanguage=Excel -DexecutionEnvironment=Windows -jar C:\prod\modelet\modelet.one-jar.jar > C:\prod\modelet\7926.out  2>&1 &' WHERE `SYS_KEY` = 'R_MODELET_STARTUP_SCRIPT_WINDOWS';

INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`, `DESCRIPTION`) VALUES (UUID(), 'PROFILER_DEF_X:MaxPermSize', '256m', 'Y', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON, 'Default Profiler MaxPermSize value');
INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`, `DESCRIPTION`) VALUES (UUID(), 'PROFILER_DEF_X:MaxHeapFreeRatio', '70', 'Y', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON, 'Default Profiler MaxHeapFreeRatio value');
INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`, `DESCRIPTION`) VALUES (UUID(), 'PROFILER_DEF_mx', '3072m', 'Y', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON, 'Default Profiler mx value');
INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`, `DESCRIPTION`) VALUES (UUID(), 'PROFILER_DEF_X:+UseConcMarkSweepGC', 'true', 'Y', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON, 'Default Profiler UseConcMarkSweepGC enabled or disabled');
INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`, `DESCRIPTION`) VALUES (UUID(), 'PROFILER_DEF_X:+UseParNewGC', 'true', 'Y', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON, 'Default Profiler UseParNewGC enabled or disabled');
INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`, `DESCRIPTION`) VALUES (UUID(), 'PROFILER_DEF_ms', '1024m', 'Y', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON, 'Default Profiler ms value');
INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`, `DESCRIPTION`) VALUES (UUID(), 'PROFILER_DEF_X:+UseCMSInitiatingOccupancyOnly', 'true', 'Y', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON, 'Default Profiler UseCMSInitiatingOccupancyOnly value');
INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`, `DESCRIPTION`) VALUES (UUID(), 'PROFILER_DEF_X:CMSInitiatingOccupancyFraction', '70', 'Y', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON, 'Default Profiler CMSInitiatingOccupancyFraction value');

INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`, `DESCRIPTION`) VALUES (UUID(), 'MODELET_LOG', 'tail -100 /usr/local/umg-logs/#port#/umg.log', 'Y', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON, 'Modelet log file location');

COMMIT;