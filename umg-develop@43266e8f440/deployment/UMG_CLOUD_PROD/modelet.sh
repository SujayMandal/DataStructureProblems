#!/bin/sh

export LD_LIBRARY_PATH=/usr/local/MATLAB/MATLAB_Compiler_Runtime/v716/runtime/glnxa64

nohup java -XX:MaxPermSize=256m -Xmx1024m -Druntime=MATLAB -Dlogroot=7900 -Dloglevel=error -Dport=7900 -DserverType=SOCKET -DsanPath=/sanpath,/sanpath2 -Dworkspace=/opt/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true -jar /opt/umg/modelet.one-jar.jar > /opt/umg/7900.out 2>&1 &

nohup java -XX:MaxPermSize=256m -Xmx1024m -Druntime=MATLAB -Dlogroot=7901 -Dloglevel=error -Dport=7901 -DserverType=SOCKET -DsanPath=/sanpath,/sanpath2 -Dworkspace=/opt/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true -jar /opt/umg/modelet.one-jar.jar > /opt/umg/7901.out 2>&1 &

nohup java -XX:MaxPermSize=256m -Xmx1024m -Druntime=MATLAB -Dlogroot=7902 -Dloglevel=error -Dport=7902 -DserverType=SOCKET -DsanPath=/sanpath,/sanpath2 -Dworkspace=/opt/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true -jar /opt/umg/modelet.one-jar.jar > /opt/umg/7902.out 2>&1 &

nohup java -XX:MaxPermSize=256m -Xmx1024m -Druntime=MATLAB -Dlogroot=7903 -Dloglevel=error -Dport=7903 -DserverType=SOCKET -DsanPath=/sanpath,/sanpath2 -Dworkspace=/opt/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true -jar /opt/umg/modelet.one-jar.jar > /opt/umg/7903.out 2>&1 &
