#!/bin/sh

scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no setenv.sh root@raprod-umgadmin1.altidev.net:/opt/tomcat/bin
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no setenv.sh root@raprod-umgadmin2.altidev.net:/opt/tomcat/bin
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no setenv.sh root@raprod-umgruntime1.altidev.net:/opt/tomcat/bin
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no setenv.sh root@raprod-umgruntime2.altidev.net:/opt/tomcat/bin
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no setenv.sh root@raprod-me2-server1.altidev.net:/opt/tomcat/bin
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no setenv.sh root@raprod-me2-server2.altidev.net:/opt/tomcat/bin
# UMG-ADMIN
# Server 1
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgadmin1.altidev.net '/opt/tomcat/bin/shutdown.sh'
sleep 10s
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgadmin1.altidev.net 'ps -ef | grep "[t]omcat" | grep java'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgadmin1.altidev.net 'rm -rf /opt/tomcat/webapps/umg-admin'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgadmin1.altidev.net 'rm -rf /opt/tomcat/webapps/umg-admin.war'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgadmin1.altidev.net 'rm -rf /opt/tomcat/work/Catalina'
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no umg-admin.war root@raprod-umgadmin1.altidev.net:/opt/tomcat/webapps
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgadmin1.altidev.net '/opt/tomcat/bin/startup.sh'
# Server 2
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgadmin2.altidev.net '/opt/tomcat/bin/shutdown.sh'
sleep 10s
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgadmin2.altidev.net 'ps -ef | grep "[t]omcat" | grep java'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgadmin2.altidev.net 'rm -rf /opt/tomcat/webapps/umg-admin'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgadmin2.altidev.net 'rm -rf /opt/tomcat/webapps/umg-admin.war'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgadmin2.altidev.net 'rm -rf /opt/tomcat/work/Catalina'
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no umg-admin.war root@raprod-umgadmin2.altidev.net:/opt/tomcat/webapps
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgadmin2.altidev.net '/opt/tomcat/bin/startup.sh'
# UMG-RUNTIME
# Server 1
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgruntime1.altidev.net '/opt/tomcat/bin/shutdown.sh'
sleep 10s
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgruntime1.altidev.net "kill -9 \$(ps aux | grep \"[t]omcat\"  | grep java | awk '{print \$2}')"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgruntime1.altidev.net 'ps -ef | grep "[t]omcat" | grep java'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgruntime1.altidev.net 'rm -rf /opt/tomcat/webapps/umg-runtime'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgruntime1.altidev.net 'rm -rf /opt/tomcat/webapps/umg-runtime.war'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgruntime1.altidev.net 'rm -rf /opt/tomcat/work/Catalina'
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no umg-runtime.war root@raprod-umgruntime1.altidev.net:/opt/tomcat/webapps
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgruntime1.altidev.net '/opt/tomcat/bin/startup.sh'
# Server 2
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgruntime2.altidev.net '/opt/tomcat/bin/shutdown.sh'
sleep 10s
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgruntime2.altidev.net "kill -9 \$(ps aux | grep \"[t]omcat\"  | grep java | awk '{print \$2}')"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgruntime2.altidev.net 'ps -ef | grep "[t]omcat" | grep java'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgruntime2.altidev.net 'rm -rf /opt/tomcat/webapps/umg-runtime'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgruntime2.altidev.net 'rm -rf /opt/tomcat/webapps/umg-runtime.war'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgruntime2.altidev.net 'rm -rf /opt/tomcat/work/Catalina'
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no umg-runtime.war root@raprod-umgruntime2.altidev.net:/opt/tomcat/webapps
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgruntime2.altidev.net '/opt/tomcat/bin/startup.sh'
# UMG-ME2
# Server 1
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-me2-server1.altidev.net '/opt/tomcat/bin/shutdown.sh'
sleep 10s
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-me2-server1.altidev.net 'ps -ef | grep "[t]omcat" | grep java'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-me2-server1.altidev.net 'rm -rf /opt/tomcat/webapps/umg-me2'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-me2-server1.altidev.net 'rm -rf /opt/tomcat/webapps/umg-me2.war'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-me2-server1.altidev.net 'rm -rf /opt/tomcat/work/Catalina'
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no umg-me2.war root@raprod-me2-server1.altidev.net:/opt/tomcat/webapps
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-me2-server1.altidev.net '/opt/tomcat/bin/startup.sh'
# Server 2
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-me2-server2.altidev.net '/opt/tomcat/bin/shutdown.sh'
sleep 10s
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-me2-server2.altidev.net 'ps -ef | grep "[t]omcat" | grep java'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-me2-server2.altidev.net 'rm -rf /opt/tomcat/webapps/umg-me2'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-me2-server2.altidev.net 'rm -rf /opt/tomcat/webapps/umg-me2.war'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-me2-server2.altidev.net 'rm -rf /opt/tomcat/work/Catalina'
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no umg-me2.war root@raprod-me2-server2.altidev.net:/opt/tomcat/webapps
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-me2-server2.altidev.net '/opt/tomcat/bin/startup.sh'

# UMG Modelet Server
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver1.altidev.net "mkdir -p /opt/umg/matlab_workspace"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver2.altidev.net "mkdir -p /opt/umg/matlab_workspace"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver3.altidev.net "mkdir -p /opt/umg/matlab_workspace"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver4.altidev.net "mkdir -p /opt/umg/matlab_workspace"

ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver1.altidev.net "kill -9 \$(ps aux | grep \"[M]ATLAB\"  | grep java | awk '{print \$2}')"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver2.altidev.net "kill -9 \$(ps aux | grep \"[M]ATLAB\"  | grep java | awk '{print \$2}')"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver3.altidev.net "kill -9 \$(ps aux | grep \"[M]ATLAB\"  | grep java | awk '{print \$2}')"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver4.altidev.net "kill -9 \$(ps aux | grep \"[M]ATLAB\"  | grep java | awk '{print \$2}')"

ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver1.altidev.net "rm -rf /opt/umg/*.jar"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver2.altidev.net "rm -rf /opt/umg/*.jar"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver3.altidev.net "rm -rf /opt/umg/*.jar"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver4.altidev.net "rm -rf /opt/umg/*.jar"

scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  modelet.one-jar.jar root@raprod-modeletserver1.altidev.net:/opt/umg
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  modelet.one-jar.jar root@raprod-modeletserver2.altidev.net:/opt/umg
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  modelet.one-jar.jar root@raprod-modeletserver3.altidev.net:/opt/umg
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  modelet.one-jar.jar root@raprod-modeletserver4.altidev.net:/opt/umg

scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  modelet.sh root@raprod-modeletserver1.altidev.net:/opt/umg
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  modelet.sh root@raprod-modeletserver2.altidev.net:/opt/umg
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  modelet.sh root@raprod-modeletserver3.altidev.net:/opt/umg
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  modelet.sh root@raprod-modeletserver4.altidev.net:/opt/umg

scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  modelet_8.sh root@raprod-modeletserver1.altidev.net:/opt/umg
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  modelet_8.sh root@raprod-modeletserver2.altidev.net:/opt/umg
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  modelet_8.sh root@raprod-modeletserver3.altidev.net:/opt/umg
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  modelet_8.sh root@raprod-modeletserver4.altidev.net:/opt/umg

ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver1.altidev.net "chmod 777 /opt/umg/modelet.sh"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver1.altidev.net "chmod 777 /opt/umg/modelet_8.sh"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver2.altidev.net "chmod 777 /opt/umg/modelet.sh"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver2.altidev.net "chmod 777 /opt/umg/modelet_8.sh"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver3.altidev.net "chmod 777 /opt/umg/modelet.sh"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver3.altidev.net "chmod 777 /opt/umg/modelet_8.sh"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver4.altidev.net "chmod 777 /opt/umg/modelet.sh"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver4.altidev.net "chmod 777 /opt/umg/modelet_8.sh"

#ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver1.altidev.net "/opt/umg/modelet.sh"
#ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver2.altidev.net "/opt/umg/modelet.sh"
#ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver3.altidev.net "/opt/umg/modelet.sh"
#ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver4.altidev.net "/opt/umg/modelet.sh"

ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver1.altidev.net "/opt/umg/modelet_8.sh"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver2.altidev.net "/opt/umg/modelet_8.sh"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver3.altidev.net "/opt/umg/modelet_8.sh"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver4.altidev.net "/opt/umg/modelet_8.sh"