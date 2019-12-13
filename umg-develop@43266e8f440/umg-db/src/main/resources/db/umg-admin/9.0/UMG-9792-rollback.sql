USE umg_admin; 

DELETE FROM `SYSTEM_PARAMETER` WHERE `SYS_KEY` = 'PROFILER_DEF_X:MaxPermSize';

DELETE FROM `SYSTEM_PARAMETER` WHERE `SYS_KEY` = 'PROFILER_DEF_X:MaxHeapFreeRatio';

DELETE FROM `SYSTEM_PARAMETER` WHERE `SYS_KEY` = 'PROFILER_DEF_mx';

DELETE FROM `SYSTEM_PARAMETER` WHERE `SYS_KEY` = 'PROFILER_DEF_X:+UseConcMarkSweepGC';

DELETE FROM `SYSTEM_PARAMETER` WHERE `SYS_KEY` = 'PROFILER_DEF_X:+UseParNewGC';

DELETE FROM `SYSTEM_PARAMETER` WHERE `SYS_KEY` = 'PROFILER_DEF_ms';

DELETE FROM `SYSTEM_PARAMETER` WHERE `SYS_KEY` = 'PROFILER_DEF_X:+UseCMSInitiatingOccupancyOnly';

DELETE FROM `SYSTEM_PARAMETER` WHERE `SYS_KEY` = 'PROFILER_DEF_X:CMSInitiatingOccupancyFraction';


UPDATE `SYSTEM_PARAMETER` SET `SYS_VALUE` = 'export LD_LIBRARY_PATH=/usr/lib64/R/library/rJava/jri:/usr/lib64/R/lib:/usr/lib/jvm/java-1.8.0-openjdk.x86_64/jre/lib/amd64/server;export JAVA_HOME=/usr/bin/java;export R_HOME=/usr/lib64/R;\nnohup java -XX:MaxPermSize=256m -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseCMSInitiatingOccupancyOnly -Xms1024m  -Xmx3072m -Dlogroot=#port# -Dloglevel=error -Dport=#port# -DrServePort=#rServePort# -DrMode=#rMode# -DserverType=#serverType# -DsanPath=/sanpath -Dworkspace=/opt/ra-modelet/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=#JMX_PORT# -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dlog4j.configurationFile=/opt/ra-modelet/raconf/log4j2.xml -Dhazelcast.config=/opt/ra-modelet/raconf/hazelcast-config.xml -DhttpConnectionPooling.properties=file:/opt/ra-modelet/raconf/httpConnectionPooling.properties -DexecutionLanguage=R -DexecutionEnvironment=Linux -jar /opt/ra-modelet/umg/modelet.one-jar.jar > /opt/ra-modelet/umg/#port#.out 2>&1 &' WHERE `SYS_KEY` = 'R_MODELET_STARTUP_SCRIPT_RJAVA';

UPDATE `SYSTEM_PARAMETER` SET `SYS_VALUE` = 'export LD_LIBRARY_PATH=/usr/lib64/R/library/rJava/jri:/usr/lib64/R/lib:/usr/lib/jvm/java-1.8.0-openjdk.x86_64/jre/lib/amd64/server;export JAVA_HOME=/usr/bin/java;export R_HOME=/usr/lib64/R;\ntar -zcvf /opt/ra-modelet/umg/log_backup/#port#_Rserve_`date +%Y%m%d_%H%M%S`.tar.gz /opt/ra-modelet/umg/#port#_Rserve.log;\nR CMD Rserve --RS-port #rServePort# --vanilla > /opt/ra-modelet/umg/#port#_Rserve.log ;\nsleep 5s;\nnohup java -XX:MaxPermSize=256m -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseCMSInitiatingOccupancyOnly -Xms1024m -Xmx3072m -Dlogroot=#port# -Dloglevel=error -Dport=#port# -DrServePort=#rServePort# -DrMode=#rMode# -DserverType=#serverType# -DsanPath=/sanpath -Dworkspace=/opt/ra-modelet/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=#JMX_PORT# -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dlog4j.configurationFile=file:/opt/ra-modelet/raconf/log4j2.xml -Dhazelcast.config=/opt/ra-modelet/raconf/hazelcast-config.xml -DhttpConnectionPooling.properties=file:/opt/ra-modelet/raconf/httpConnectionPooling.properties -DexecutionLanguage=R -DexecutionEnvironment=Linux -jar /opt/ra-modelet/umg/modelet.one-jar.jar > /opt/ra-modelet/umg/#port#.out 2>&1 &' WHERE `SYS_KEY` = 'R_MODELET_STARTUP_SCRIPT_RSERVE';

UPDATE `SYSTEM_PARAMETER` SET `SYS_VALUE` = 'nohup java -Xmx3096m -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseCMSInitiatingOccupancyOnly  -XX:MaxPermSize=256m  -Druntime=R -Dlogroot=#port#   -Dloglevel=#port# -Dport=#port# -DserverType=#serverType# -DsanPath=Z:\ -Dworkspace=C:\Workspace\matlab -Dloglevel=debug -DrTempPath=C:\Workspace\matlab -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9026 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dlog4j.configurationFile="file:\\C:\prod\raconf-modelet\log4j2.xml" -Dhazelcast.config="C:\prod\raconf-modelet\hazelcast-config.xml" -DhttpConnectionPooling.properties="file:\C:\prod\raconf-modelet\httpConnectionPooling.properties"  -Djava.library.path="C:\Installed\rJava\jri\x64;C:\Program Files\Java\jdk1.8.0_191\bin;" -DexecutionLanguage=Excel -DexecutionEnvironment=Windows -jar C:\prod\modelet\modelet.one-jar.jar > C:\prod\modelet\7926.out  2>&1 &' WHERE `SYS_KEY` = 'R_MODELET_STARTUP_SCRIPT_WINDOWS';

DROP TABLE `MODELET_PROFILER_PARAM`;

DROP TABLE `MODELET_PROFILER_KEY`;

DROP TABLE `SYSTEM_MODELET_PROFILER_MAP`;

DROP TABLE `MODELET_PROFILER`;

ALTER TABLE `SYSTEM_MODELETS`
	DROP PRIMARY KEY;
	
ALTER TABLE `SYSTEM_MODELETS`
	DROP INDEX `HOST_NAME_PORT`;
	
ALTER TABLE `SYSTEM_MODELETS`
	ADD PRIMARY KEY (`HOST_NAME`,`PORT`)

ALTER TABLE `SYSTEM_MODELETS`
	DROP COLUMN `ID`,
	DROP COLUMN `CREATED_BY`,
	DROP COLUMN `CREATED_ON`,
	DROP COLUMN `LAST_UPDATED_BY`,
	DROP COLUMN `LAST_UPDATED_ON`;

COMMIT;
