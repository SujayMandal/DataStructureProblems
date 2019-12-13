USE umg_admin;

ALTER TABLE `SYSTEM_PARAMETER`
CHANGE COLUMN `SYS_VALUE` `SYS_VALUE` VARCHAR(1500) NULL DEFAULT NULL COLLATE 'utf8_bin' AFTER `SYS_KEY`;
commit;

ALTER TABLE `SYSTEM_PARAMETER_AUDIT`
	CHANGE COLUMN `SYS_VALUE` `SYS_VALUE` VARCHAR(1500) NULL DEFAULT NULL COLLATE 'utf8_bin' AFTER `SYS_KEY`;
commit;

INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
VALUES ('fbfeedd1-4fad-11e5-bee2-00ffbc73cbd1', 'R_MODELET_STARTUP_SCRIPT', 'export LD_LIBRARY_PATH=/usr/lib64/R/library/rJava/jri:/usr/lib64/R/lib:/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.85.x86_64/jre/lib/amd64/server; export JAVA_HOME=/usr/bin/java; export R_HOME=/usr/lib64/R/; nohup java -XX:MaxPermSize=256m -Xmx1024m  -Druntime=MATLAB -Dlogroot=#port# -Dloglevel=#port# -Dport=#port# -DserverType=#serverType# -DsanPath=/sanpath -Dworkspace=/opt/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true   -Dlog4j.configurationFile=file:/opt/raconf/log4j2.xml -Dhazelcast.config=/opt/raconf/hazelcast-config.xml -DhttpConnectionPooling.properties=file:/opt/raconf/httpConnectionPooling.properties -DrunMatlab=#runMatlab# -DrunR=#runR# -jar /opt/umg/modelet.one-jar.jar > /opt/umg/#port#.out 2>&1 &', 'Y', 'SYSTEM', 20150831123038, NULL, NULL);
INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('d3bc69ed-4fad-11e5-bee2-00ffbc73cbd1', 'SSH_USER', 'root', 'Y', 'SYSTEM', 20150831122931, NULL, NULL);
INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('d3c40561-4fad-11e5-bee2-00ffbc73cbd1', 'SSH_PASSWORD', '', 'Y', 'SYSTEM', 20150831122931, NULL, NULL);
--change the value for SYS_VALUE from "C:\\\\Users\\\\kamathan\\\\.ssh\\\\id_rsa" to your local file location
-- for aws dev/qe it is set as /opt/tomcat/real-impact-arch.pem
INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
VALUES ('d3cc24ff-4fad-11e5-bee2-00ffbc73cbd1', 'SSH_IDENTITY', 
'/opt/tomcat/real-impact-arch.pem', 'Y', 'SYSTEM', 20150831122931, NULL, NULL);
INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('d3aca61f-4fad-11e5-bee2-00ffbc73cbd1', 'SSH_PORT', '22', 'Y', 'SYSTEM', 20150831122930, NULL, NULL);
 
INSERT INTO `umg_admin`.`SYSTEM_PARAMETER` (`ID`,`SYS_KEY`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES 
('5a90b098-5fc7-46aa-9907-8098efbace10','MODELET_RESTART_DELAY', '30000', 'Y', 'system', 1435816777, 'anil.kamath', 1436266371220);

commit;