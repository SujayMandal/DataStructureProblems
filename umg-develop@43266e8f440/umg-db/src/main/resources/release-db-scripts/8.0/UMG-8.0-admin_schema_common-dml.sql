use `umg_admin`;

UPDATE POOL_CRITERIA set CRITERIA_NAME = 'EXECUTION_LANGUAGE' WHERE CRITERIA_NAME = 'ENVIRONMENT';

UPDATE POOL_CRITERIA set CRITERIA_NAME = 'EXECUTION_LANGUAGE_VERSION' WHERE CRITERIA_NAME = 'ENVIRONMENT_VERSION';

UPDATE POOL_CRITERIA set CRITERIA_PRIORITY = 4 WHERE CRITERIA_NAME = 'MODEL';
UPDATE POOL_CRITERIA set CRITERIA_PRIORITY = 5 WHERE CRITERIA_NAME = 'MODEL_VERSION';
UPDATE POOL_CRITERIA set CRITERIA_PRIORITY = 6 WHERE CRITERIA_NAME = 'TENANT';
UPDATE POOL_CRITERIA set CRITERIA_PRIORITY = 7 WHERE CRITERIA_NAME = 'TRANSACTION_MODE';
UPDATE POOL_CRITERIA set CRITERIA_PRIORITY = 8 WHERE CRITERIA_NAME = 'TRANSACTION_TYPE';
UPDATE POOL_CRITERIA set CRITERIA_PRIORITY = 9 WHERE CRITERIA_NAME = 'CHANNEL';

INSERT INTO `POOL_CRITERIA` (`ID`, `CRITERIA_NAME`, `CRITERIA_PRIORITY`) VALUES ('9', 'EXECUTION_ENVIRONMENT', 3);

UPDATE POOL_CRITERIA_DEF_MAPPING SET POOL_CRITERIA_VALUE = replace(POOL_CRITERIA_VALUE,'ENVIRONMENT','EXECUTION_LANGUAGE');

UPDATE POOL_CRITERIA_DEF_MAPPING SET POOL_CRITERIA_DEF_MAPPING.POOL_CRITERIA_VALUE = CONCAT(POOL_CRITERIA_DEF_MAPPING.POOL_CRITERIA_VALUE, ' & #EXECUTION_ENVIRONMENT# = Linux');

UPDATE POOL SET `POOL_DESCRIPTION`='POOL OF R-Linux-Default' WHERE  `POOL_NAME`='R-Default';

UPDATE POOL SET `POOL_NAME`='R-Linux-Default' WHERE  `POOL_NAME`='R-Default';

UPDATE POOL SET `POOL_DESCRIPTION`='POOL OF Matlab-Linux-Default' WHERE  `POOL_NAME`='Matlab-Default';

UPDATE POOL SET `POOL_NAME`='Matlab-Linux-Default' WHERE  `POOL_NAME`='Matlab-Default';

UPDATE SYSTEM_PARAMETER SET SYS_VALUE='RMV' WHERE  sys_key='model-exception-error-code-pattern';

INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `DESCRIPTION`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
VALUES ('EE9E19F3-AB0A-4FBC-8935-ACD677A7348F', 'R_MODELET_STARTUP_SCRIPT_WINDOWS', 'modelet script for windows os', 'export LD_LIBRARY_PATH=/usr/lib64/R/library/rJava/jri:/usr/lib64/R/lib:/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.85.x86_64/jre/lib/amd64/server; export JAVA_HOME=/usr/bin/java; export R_HOME=/usr/lib64/R/; nohup java -XX:MaxPermSize=256m -Xmx1024m  -Druntime=MATLAB -Dlogroot=#port# -Dloglevel=#port# -Dport=#port# -DserverType=#serverType# -DsanPath=/sanpath -Dworkspace=/opt/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true   -Dlog4j.configurationFile=file:/opt/raconf/log4j2.xml -Dhazelcast.config=/opt/raconf/hazelcast-config.xml -DhttpConnectionPooling.properties=file:/opt/raconf/httpConnectionPooling.properties -DrunMatlab=#runMatlab# -DrunR=#runR# -DexecutionEnvironment=Windows -jar /opt/umg/modelet.one-jar.jar > /opt/umg/#port#.out 2>&1 & ', 'Y', NULL, NULL, NULL, NULL);

SET @ID :=UUID();
INSERT INTO `MODEL_EXECUTION_ENVIRONMENTS` (`ID`, `EXECUTION_ENVIRONMENT`, `ENVIRONMENT_VERSION`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`, `NAME`) 
VALUES (@ID, 'Excel', '2013', 'sujay.mandal', 1479990810, 'sujay.mandal', 1479990810, 'Excel-2013');

SET @POOL_ID :=UUID();
INSERT INTO `POOL` (`ID`, `POOL_NAME`, `POOL_DESCRIPTION`, `IS_DEFAULT_POOL`, `EXECUTION_LANGUAGE`, `EXECUTION_ENVIRONMENT`, `POOL_STATUS`, `MODELET_COUNT`, `MODELET_CAPACITY`, `PRIORITY`, `WAIT_TIMEOUT`) 
VALUES (@POOL_ID, 'R-Windows-Default', 'POOL OF R-Windows-Default', 1, 'R', 'Windows', NULL, 1, '4GB - Windows 64 bit', 1, 60000);

INSERT INTO `POOL_CRITERIA_DEF_MAPPING` (`ID`, `POOL_ID`, `POOL_CRITERIA_VALUE`) 
VALUES (@POOL_ID, @POOL_ID, '#TENANT# = Any & #EXECUTION_LANGUAGE# = R & #EXECUTION_LANGUAGE_VERSION# = 3.2.1 & #TRANSACTION_TYPE# = Any & #MODEL# = Any & #MODEL_VERSION# = Any & #TRANSACTION_MODE# = Any & #CHANNEL# = Any & #EXECUTION_ENVIRONMENT# = Windows');

INSERT INTO `POOL_USAGE_ORDER` (`ID`, `POOL_ID`, `POOL_USAGE_ID`, `POOL_TRY_ORDER`) 
VALUES (@POOL_ID, @POOL_ID, @POOL_ID, 1);

SET @POOL_ID :=UUID();
INSERT INTO `POOL` (`ID`, `POOL_NAME`, `POOL_DESCRIPTION`, `IS_DEFAULT_POOL`, `EXECUTION_LANGUAGE`, `EXECUTION_ENVIRONMENT`, `POOL_STATUS`, `MODELET_COUNT`, `MODELET_CAPACITY`, `PRIORITY`, `WAIT_TIMEOUT`) 
VALUES (@POOL_ID, 'Excel-Windows-Default', 'POOL OF Excel-Windows-Default', 1, 'Excel', 'Windows', NULL, 1, '1GB - Windows 64 bit', 1, 60000);

INSERT INTO `POOL_CRITERIA_DEF_MAPPING` (`ID`, `POOL_ID`, `POOL_CRITERIA_VALUE`) 
VALUES (@POOL_ID, @POOL_ID, '#TENANT# = Any & #EXECUTION_LANGUAGE# = Excel & #EXECUTION_LANGUAGE_VERSION# = 2013 & #TRANSACTION_TYPE# = Any & #MODEL# = Any & #MODEL_VERSION# = Any & #TRANSACTION_MODE# = Any & #CHANNEL# = Any & #EXECUTION_ENVIRONMENT# = Windows');

INSERT INTO `POOL_USAGE_ORDER` (`ID`, `POOL_ID`, `POOL_USAGE_ID`, `POOL_TRY_ORDER`) 
VALUES (@POOL_ID, @POOL_ID, @POOL_ID, 1);

INSERT INTO `SYSTEM_KEY` (`ID`, `SYSTEM_KEY`, `KEY_TYPE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
VALUES ('D2D2FD08-004B-48D5-9217-0C4F2BF60248', 'AcceptableValues', 'TENANT', 'SYSTEM', 1473659602, 'SYSTEM', 1473659602);

INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `DESCRIPTION`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES 
('E2B1135E-2D2B-4AE3-810F-742FF5A70515', 'MAX_DISPLAY_RECORDS_SIZE', 'MAX DISPLAY PAGE SIZE IN TXN DASHBOARD', '50000', 'Y', 'SYSTEM', 1486624549, 'SYSTEM', 1486624576);


INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `DESCRIPTION`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES 
('93A1EDC8-30F2-4376-8AC0-25A88D9E3BD6', 'DEFAULT_SEARCH_PAGE_SIZE', 'DEFAULT DASHBOARD PAGE SEARCH', '500', 'Y', 'SYSTEM', 1486624459, 'SYSTEM', 1486624473);

DELETE FROM `SYSTEM_MODELETS`;

INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`, `DESCRIPTION`) VALUES 
(UUID(), 'R_MODELET_RESTART_ERROR_CODES', 'RSE008041,RSE008042,RSE008043,RSE008044,RSE008045,RSE008046,RSE008047,RSE008048,RSE000930', 'Y', 'SYSTEM', 5155451518, 'SYSTEM', 1487673061335, 'Comma separated error codes to trigger R modelet restarts incase of execution errors. Supported error codes are RSE008041,RSE008042,RSE008043,RSE008044,RSE008045,RSE008046,RSE008047,RSE008048,RSE000930');

commit;