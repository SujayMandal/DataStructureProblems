use umg_admin;

UPDATE MODEL_EXECUTION_ENVIRONMENTS SET `IS_ACTIVE`='T' WHERE EXECUTION_ENVIRONMENT='Excel';

UPDATE MODEL_EXECUTION_ENVIRONMENTS SET `IS_ACTIVE`='T' WHERE EXECUTION_ENVIRONMENT='Matlab';

UPDATE POOL_CRITERIA_DEF_MAPPING SET POOL_CRITERIA_VALUE = replace(POOL_CRITERIA_VALUE,'& #EXECUTION_LANGUAGE_VERSION# = 3.2.1 ','');

UPDATE POOL_CRITERIA_DEF_MAPPING SET POOL_CRITERIA_VALUE = replace(POOL_CRITERIA_VALUE,'& #EXECUTION_LANGUAGE_VERSION# = 2013 ','');

UPDATE POOL_CRITERIA_DEF_MAPPING SET POOL_CRITERIA_VALUE = replace(POOL_CRITERIA_VALUE,'& #EXECUTION_LANGUAGE_VERSION# = 7.16 ','');

INSERT INTO `MODEL_EXECUTION_ENVIRONMENTS` (`ID`, `EXECUTION_ENVIRONMENT`, `ENVIRONMENT_VERSION`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`, `NAME`, `IS_ACTIVE`) VALUES (UUID(), 'R', '3.3.2', 'nagamani.basa', 1479800282, 'nagamani.basa', 1479800292, 'R-3.3.2', 'T');
	
DELETE FROM `POOL_CRITERIA` WHERE  `CRITERIA_NAME`='EXECUTION_LANGUAGE_VERSION';
UPDATE `POOL_CRITERIA` SET `CRITERIA_PRIORITY`=2 WHERE  `CRITERIA_NAME`='EXECUTION_ENVIRONMENT';
UPDATE `POOL_CRITERIA` SET `CRITERIA_PRIORITY`=3 WHERE  `CRITERIA_NAME`='MODEL';
UPDATE `POOL_CRITERIA` SET `CRITERIA_PRIORITY`=4 WHERE  `CRITERIA_NAME`='MODEL_VERSION';
UPDATE `POOL_CRITERIA` SET `CRITERIA_PRIORITY`=5 WHERE  `CRITERIA_NAME`='TENANT';
UPDATE `POOL_CRITERIA` SET `CRITERIA_PRIORITY`=6 WHERE  `CRITERIA_NAME`='TRANSACTION_MODE';
UPDATE `POOL_CRITERIA` SET `CRITERIA_PRIORITY`=7 WHERE  `CRITERIA_NAME`='TRANSACTION_TYPE';
UPDATE `POOL_CRITERIA` SET `CRITERIA_PRIORITY`=8 WHERE  `CRITERIA_NAME`='CHANNEL';

update NOTIFICATION_EMAIL_TEMPLATE set NOTIFICATION_EMAIL_TEMPLATE.BODY_DEFINITION = '<html><style>table, th, td {border: 1px solid black;border-collapse: collapse;}th, td {padding: 5px;text-align: center;}th {background-color: #F5F5F5;}</style><body style="font-family: Calibri;">Hi,<br><br>Following error has been encountered during execution in $environment<ul><li> Tenant Transaction Id: $transactionId </li><li> Execution Time Stamp: $executionTime </li><li> Error Details: $errorCode $errorMessage </li><li> Model Name: $modelName </li><li> Model Version: $modelVersion </li><li> Tenant Name: $tenantName </li></ul> Modelet Status at  $executionTime , Refer table: #set( $count = 1 )<TABLE><TR><TH>Sl no.</TH><TH>Modelet</TH><TH>Pool Name</TH><TH>Status</TH>#foreach( $modelet in $modeletList)</TR><TR><TD>$count</TD><TD>$modelet.host :$modelet.port /$modelet.rServePort </TD><TD>$modelet.poolName</TD><TD>$modelet.modeletStatus</TD></TR> #set( $count = $count + 1 ) #end</TABLE><br><br><b>Regards,</b><br><b>REALAnalytics Team</b></body></html> | REALAnalytics $environment: Transaction error $errorCode for $tenantName tenant' where NOTIFICATION_EMAIL_TEMPLATE.NAME = 'RUNTIME FAILURE MAIL TEMPLATE' ;

update NOTIFICATION_EMAIL_TEMPLATE set NOTIFICATION_EMAIL_TEMPLATE.BODY_DEFINITION = '<html><body style="font-family: Calibri;">Hi,<br><br>Modelet $modeletHost:$port/$rServePort has been restarted in $environment environment. Please find further details below:<ul><li>Current Pool Name: $poolName</li><li>New Pool Name: $newPoolName</li><li>Restart Time: $modeletRestartTime</li><li>Model Name: $loadedModel & Model Version: $loadedModelVersion prior to restart</li><li>Model Name: $modelToLoad & Model Version: $modelVersionToLoad requested model</li><li>Transactio ID: $transactionId</li><li>Transaction Run Date: $transactionRunDate</li><li>Restart Reason: $reason</li><li># Transaction Processed since last Restart: $execLimit</li><li>Transaction Restart Set-up Count: $restartCount</li></ul><b>Regards,</b><br><b>REALAnalytics Team</b></body></html>' where NOTIFICATION_EMAIL_TEMPLATE.NAME = 'MODELET RESTART TEMPLATE' ;

INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`,`SYS_VALUE`)
VALUES (UUID(), 'R_MODELET_STARTUP_SCRIPT_RJAVA', 'Y', 'system', UNIX_TIMESTAMP(NOW()), 'system', UNIX_TIMESTAMP(NOW()),'export LD_LIBRARY_PATH=/usr/lib64/R/library/rJava/jri:/usr/lib64/R/lib:/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.85.x86_64/jre/lib/amd64/server;export JAVA_HOME=/usr/bin/java;export R_HOME=/usr/lib64/R;
nohup java -XX:MaxPermSize=256m -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseCMSInitiatingOccupancyOnly -Xmx3072m -Dlogroot=#port# -Dloglevel=debug -Dport=#port# -DrServePort=#rServePort# -DrMode=#rMode# -DserverType=#serverType# -DsanPath=/sanpath -Dworkspace=/opt/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=#JMX_PORT# -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dlog4j.configurationFile=file:/opt/raconf/log4j2.xml -Dhazelcast.config=/opt/raconf/hazelcast-config.xml -DhttpConnectionPooling.properties=file:/opt/raconf/httpConnectionPooling.properties -DexecutionLanguage=R -DexecutionEnvironment=Linux -jar /opt/umg/modelet.one-jar.jar > /opt/umg/#port#.out 2>&1 &');

INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`,`SYS_VALUE`)
VALUES (UUID(), 'R_MODELET_STARTUP_SCRIPT_RSERVE', 'Y', 'system', UNIX_TIMESTAMP(NOW()), 'system', UNIX_TIMESTAMP(NOW()),'export LD_LIBRARY_PATH=/usr/lib64/R/library/rJava/jri:/usr/lib64/R/lib:/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.85.x86_64/jre/lib/amd64/server;export JAVA_HOME=/usr/bin/java;export R_HOME=/usr/lib64/R;
R CMD Rserve --RS-port #rServePort# --vanilla;
sleep 5s;
nohup java -XX:MaxPermSize=256m -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseCMSInitiatingOccupancyOnly -Xmx3072m -Dlogroot=#port# -Dloglevel=debug -Dport=#port# -DrServePort=#rServePort# -DrMode=#rMode# -DserverType=#serverType# -DsanPath=/sanpath -Dworkspace=/opt/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=#JMX_PORT# -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dlog4j.configurationFile=file:/opt/raconf/log4j2.xml -Dhazelcast.config=/opt/raconf/hazelcast-config.xml -DhttpConnectionPooling.properties=file:/opt/raconf/httpConnectionPooling.properties -DexecutionLanguage=R -DexecutionEnvironment=Linux -jar /opt/umg/modelet.one-jar.jar > /opt/umg/#port#.out 2>&1 &
');



/* set delimiter */
DELIMITER $$

/* remove procedure insert_authToken... */
DROP PROCEDURE IF EXISTS insert_supportpackages_r332 $$

/* create procedure insert_default_privileges*/ 
CREATE PROCEDURE insert_supportpackages_r332 ()
BEGIN
DECLARE exec_packages_length INT;
DECLARE x INT DEFAULT 0;
DECLARE ID_TEMP varchar(36);

/* drop temp table  TEMP_TABLE_TENANT */ 
DROP TABLE IF EXISTS TEMP_TABLE_SUPPORTPACKAGES_R332;
/* create temp table  TEMP_TABLE_TENANT */
create temporary table if not exists TEMP_TABLE_SUPPORTPACKAGES_R332
select ID AS ID,EXEC_PACKAGES.PACKAGE_NAME  AS PACKAGE_NAME,EXEC_PACKAGES.PACKAGE_FOLDER AS PACKAGE_FOLDER,EXEC_PACKAGES.PACKAGE_VERSION AS PACKAGE_VERSION,EXEC_PACKAGES.PACKAGE_TYPE AS PACKAGE_TYPE,EXEC_PACKAGES.COMPILED_OS AS COMPILED_OS,EXEC_PACKAGES.CREATED_BY AS CREATED_BY,EXEC_PACKAGES.CREATED_ON AS CREATED_ON,EXEC_PACKAGES.LAST_UPDATED_BY AS LAST_UPDATED_BY,EXEC_PACKAGES.LAST_UPDATED_ON AS LAST_UPDATED_ON,EXEC_PACKAGES.EXECUTION_ENVIRONMENT AS EXECUTION_ENVIRONMENT,EXEC_PACKAGES.MODEL_EXEC_ENV_NAME AS MODEL_EXEC_ENV_NAME from MODEL_EXEC_PACKAGES EXEC_PACKAGES WHERE PACKAGE_TYPE='ADDON';

UPDATE TEMP_TABLE_SUPPORTPACKAGES_R332 SET MODEL_EXEC_ENV_NAME='R-3.3.2';

SELECT COUNT(*) FROM TEMP_TABLE_SUPPORTPACKAGES_R332 INTO exec_packages_length;

SELECT exec_packages_length;

WHILE x < exec_packages_length DO

SELECT ID FROM TEMP_TABLE_SUPPORTPACKAGES_R332 LIMIT x,1 INTO ID_TEMP;
UPDATE TEMP_TABLE_SUPPORTPACKAGES_R332 SET ID=uuid() where ID=ID_TEMP;

SET x = x+1;
END WHILE;

INSERT INTO MODEL_EXEC_PACKAGES SELECT * FROM TEMP_TABLE_SUPPORTPACKAGES_R332;


DROP TABLE TEMP_TABLE_SUPPORTPACKAGES_R332;

END $$

DELIMITER ;

CALL insert_supportpackages_r332();

DROP PROCEDURE insert_supportpackages_r332;

commit;