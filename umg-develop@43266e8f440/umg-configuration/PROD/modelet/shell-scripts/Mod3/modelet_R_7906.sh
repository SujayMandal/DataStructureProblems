#!/bin/sh

export LD_LIBRARY_PATH=/usr/lib64/R/library/rJava/jri:/usr/lib64/R/lib:/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.85.x86_64/jre/lib/amd64/server
export JAVA_HOME=/usr/bin/java
export R_HOME=/usr/lib64/R

nohup java -XX:MaxPermSize=256m -Xmx1024m  -Dlogroot=7906 -Dloglevel=debug -Dport=7906 -DserverType=SOCKET -DsanPath=/sanpath -Dworkspace=/opt/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9016 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dlog4j.configurationFile=file:/opt/raconf/log4j2.xml -Dhazelcast.config=/opt/raconf/hazelcast-config.xml -DhttpConnectionPooling.properties=file:/opt/raconf/httpConnectionPooling.properties -DrunMatlab=false -DrunR=true -jar /opt/umg/modelet.one-jar.jar > /opt/umg/7906.out 2>&1 &
