#!/bin/sh

export LD_LIBRARY_PATH=/usr/local/MATLAB/MATLAB_Compiler_Runtime/v716/runtime/glnxa64:/usr/lib64/R:/usr/lib64/R/library/rJava/jri
export JAVA_HOME=/usr/local/tomcat-UMG/umg_runtime/jdk1.7.0_67
export R_HOME=/usr/lib64/R

nohup java -XX:MaxPermSize=256m -Xmx1024m -Druntime=MATLAB -DrTempPath=/usr/local/tomcat-UMG/umg_runtime/test_modelet/r_temp -Dlogroot=7900 -Dloglevel=error -Dport=7900 -DserverType=SOCKET -DsanPath=/sanpath,/sanpath2 -Dworkspace=/usr/local/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true -Dcom.sun.management.jmxremote.port=7978 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.remote.ssl=false -jar /usr/local/umg/modelet.one-jar.jar > /usr/local/umg/7900.out 2>&1 &

nohup java -XX:MaxPermSize=256m -Xmx1024m -Druntime=MATLAB -DrTempPath=/usr/local/tomcat-UMG/umg_runtime/test_modelet/r_temp -Dlogroot=7901 -Dloglevel=error -Dport=7901 -DserverType=SOCKET -DsanPath=/sanpath,/sanpath2 -Dworkspace=/usr/local/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true -Dcom.sun.management.jmxremote.port=7978 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.remote.ssl=false -jar /usr/local/umg/modelet.one-jar.jar > /usr/local/umg/7901.out 2>&1 &

nohup java -XX:MaxPermSize=256m -Xmx1024m -Druntime=MATLAB -DrTempPath=/usr/local/tomcat-UMG/umg_runtime/test_modelet/r_temp -Dlogroot=7902 -Dloglevel=error -Dport=7902 -DserverType=SOCKET -DsanPath=/sanpath,/sanpath2 -Dworkspace=/usr/local/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true -Dcom.sun.management.jmxremote.port=7978 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.remote.ssl=false -jar /usr/local/umg/modelet.one-jar.jar > /usr/local/umg/7902.out 2>&1 &

nohup java -XX:MaxPermSize=256m -Xmx1024m -Druntime=MATLAB -DrTempPath=/usr/local/tomcat-UMG/umg_runtime/test_modelet/r_temp -Dlogroot=7903 -Dloglevel=error -Dport=7903 -DserverType=SOCKET -DsanPath=/sanpath,/sanpath2 -Dworkspace=/usr/local/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true -Dcom.sun.management.jmxremote.port=7978 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.remote.ssl=false -jar /usr/local/umg/modelet.one-jar.jar > /usr/local/umg/7903.out 2>&1 &
