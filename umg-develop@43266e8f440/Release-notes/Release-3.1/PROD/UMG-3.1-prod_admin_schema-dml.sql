use umg_admin;

INSERT INTO `SYSTEM_PARAMETER` (`ID`, `SYS_KEY`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES 
('fbfeedd1-4fad-11e5-bee2-00ffbc73cbd1', 'R_MODELET_STARTUP_SCRIPT', 'export LD_LIBRARY_PATH=/usr/lib64/R/library/rJava/jri:/usr/lib64/R/lib:/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.85.x86_64/jre/lib/amd64/server;export JAVA_HOME=/usr/bin/java;export R_HOME=/usr/lib64/R;nohup java -XX:MaxPermSize=256m -Xmx1024m  -Dlogroot=#port# -Dloglevel=error -Dport=#port# -DserverType=#serverType# -DsanPath=/sanpath -Dworkspace=/opt/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=#JMX_PORT# -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dlog4j.configurationFile=file:/opt/raconf/log4j2.xml -Dhazelcast.config=/opt/raconf/hazelcast-config.xml -DhttpConnectionPooling.properties=file:/opt/raconf/httpConnectionPooling.properties -DrunMatlab=#runMatlab# -DrunR=#runR# -jar /opt/umg/modelet.one-jar.jar > /opt/umg/#port#.out 2>&1 &', 'Y', 'SYSTEM', 20150831123038, NULL, NULL),
('d3bc69ed-4fad-11e5-bee2-00ffbc73cbd1', 'SSH_USER', 'root', 'Y', 'SYSTEM', 20150831122931, NULL, NULL),
('d3c40561-4fad-11e5-bee2-00ffbc73cbd1', 'SSH_PASSWORD', '', 'Y', 'SYSTEM', 20150831122931, NULL, NULL),
('d3cc24ff-4fad-11e5-bee2-00ffbc73cbd1', 'SSH_IDENTITY','/root/.ssh/id_rsa', 'Y', 'SYSTEM', 20150831122931, NULL, NULL),
('d3aca61f-4fad-11e5-bee2-00ffbc73cbd1', 'SSH_PORT', '22', 'Y', 'SYSTEM', 20150831122930, NULL, NULL),
('5a90b098-5fc7-46aa-9907-8098efbace10','MODELET_RESTART_DELAY', '30000', 'Y', 'system', 1435816777, 'SYSTEM', 1436266371220),
('bfd36024-6763-11e5-9c00-00ffbc73cbd1','JMX_MODELET_PORT_MAPPING', '7900-9010|7901-9011|7902-9012|7903-9013|7904-9014', 'Y', 'system', 1435816777, 'SYSTEM', 1436266371220);
COMMIT;