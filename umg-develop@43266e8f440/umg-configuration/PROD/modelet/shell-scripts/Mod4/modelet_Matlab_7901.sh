#!/bin/sh

export LD_LIBRARY_PATH=/usr/local/MATLAB/MATLAB_Compiler_Runtime/v716/runtime/glnxa64
export JAVA_HOME=/usr/bin/java

nohup java -XX:MaxPermSize=256m -Xmx1024m  -Dlogroot=7901 -Dloglevel=error -Dport=7901 -DserverType=SOCKET -DsanPath=/sanpath -Dworkspace=/opt/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9011 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dlog4j.configurationFile=file:/opt/raconf/log4j2.xml -Dhazelcast.config=/opt/raconf/hazelcast-config.xml -DhttpConnectionPooling.properties=file:/opt/raconf/httpConnectionPooling.properties -DrunMatlab=true -DrunR=false -jar /opt/umg/modelet.one-jar.jar > /opt/umg/7901.out 2>&1 &