#!/bin/sh

export LD_LIBRARY_PATH=/usr/local/MATLAB/MATLAB_Compiler_Runtime/v716/runtime/glnxa64

nohup java -XX:MaxPermSize=256m -Xmx1024m -Druntime=MATLAB -Dlogroot=7900 -Dloglevel=error -Dport=7900 -DserverType=SOCKET -DsanPath=/sanpath,/sanpath2 -Dworkspace=/usr/local/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true -jar /usr/local/umg/modelet.one-jar.jar > /usr/local/umg/7900.out 2>&1 &

nohup java -XX:MaxPermSize=256m -Xmx1024m -Druntime=MATLAB -Dlogroot=7901 -Dloglevel=error -Dport=7901 -DserverType=SOCKET -DsanPath=/sanpath,/sanpath2 -Dworkspace=/usr/local/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true -jar /usr/local/umg/modelet.one-jar.jar > /usr/local/umg/7901.out 2>&1 &

nohup java -XX:MaxPermSize=256m -Xmx1024m -Druntime=MATLAB -Dlogroot=7902 -Dloglevel=error -Dport=7902 -DserverType=SOCKET -DsanPath=/sanpath,/sanpath2 -Dworkspace=/usr/local/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true -jar /usr/local/umg/modelet.one-jar.jar > /usr/local/umg/7902.out 2>&1 &

nohup java -XX:MaxPermSize=256m -Xmx1024m -Druntime=MATLAB -Dlogroot=7903 -Dloglevel=error -Dport=7903 -DserverType=SOCKET -DsanPath=/sanpath,/sanpath2 -Dworkspace=/usr/local/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true -jar /usr/local/umg/modelet.one-jar.jar > /usr/local/umg/7903.out 2>&1 &

nohup java -XX:MaxPermSize=256m -Xmx1024m -Druntime=MATLAB -Dlogroot=7904 -Dloglevel=error -Dport=7904 -DserverType=SOCKET -DsanPath=/sanpath,/sanpath2 -Dworkspace=/usr/local/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true -jar /usr/local/umg/modelet.one-jar.jar > /usr/local/umg/7904.out 2>&1 &

nohup java -XX:MaxPermSize=256m -Xmx1024m -Druntime=MATLAB -Dlogroot=7905 -Dloglevel=error -Dport=7905 -DserverType=SOCKET -DsanPath=/sanpath,/sanpath2 -Dworkspace=/usr/local/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true -jar /usr/local/umg/modelet.one-jar.jar > /usr/local/umg/7905.out 2>&1 &

nohup java -XX:MaxPermSize=256m -Xmx1024m -Druntime=MATLAB -Dlogroot=7906 -Dloglevel=error -Dport=7906 -DserverType=SOCKET -DsanPath=/sanpath,/sanpath2 -Dworkspace=/usr/local/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true -jar /usr/local/umg/modelet.one-jar.jar > /usr/local/umg/7906.out 2>&1 &

nohup java -XX:MaxPermSize=256m -Xmx1024m -Druntime=MATLAB -Dlogroot=7907 -Dloglevel=error -Dport=7907 -DserverType=SOCKET -DsanPath=/sanpath,/sanpath2 -Dworkspace=/usr/local/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true -jar /usr/local/umg/modelet.one-jar.jar > /usr/local/umg/7907.out 2>&1 &
