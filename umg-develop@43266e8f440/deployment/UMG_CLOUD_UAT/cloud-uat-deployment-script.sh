#########!/bin/sh
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no setenv.sh root@rauat-umgadmin1.altidev.net:/usr/local/tomcat7/bin
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no setenv.sh root@rauat-umgadmin2.altidev.net:/usr/local/tomcat7/bin
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no setenv.sh root@rauat-umgruntime1.altidev.net:/usr/local/tomcat7/bin
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no setenv.sh root@rauat-umgruntime2.altidev.net:/usr/local/tomcat7/bin
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no setenv.sh root@rauat-me2-server1.altidev.net:/usr/local/tomcat7/bin
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no setenv.sh root@rauat-me2-server2.altidev.net:/usr/local/tomcat7/bin
################################## UMG-ADMIN
################### Server 1
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgadmin1.altidev.net '/usr/local/tomcat7/bin/shutdown.sh'
sleep 10s
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgadmin1.altidev.net 'ps -ef | grep "[t]omcat" | grep java'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgadmin1.altidev.net 'rm -rf /usr/local/tomcat7/webapps/umg-admin'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgadmin1.altidev.net 'rm -rf /usr/local/tomcat7/webapps/umg-admin.war'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgadmin1.altidev.net 'rm -rf /usr/local/tomcat7/work/Catalina'
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no umg-admin.war root@rauat-umgadmin1.altidev.net:/usr/local/tomcat7/webapps
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgadmin1.altidev.net '/usr/local/tomcat7/bin/startup.sh'
###################### Server 2
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgadmin2.altidev.net '/usr/local/tomcat7/bin/shutdown.sh'
sleep 10s
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgadmin2.altidev.net 'ps -ef | grep "[t]omcat" | grep java'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgadmin2.altidev.net 'rm -rf /usr/local/tomcat7/webapps/umg-admin'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgadmin2.altidev.net 'rm -rf /usr/local/tomcat7/webapps/umg-admin.war'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgadmin2.altidev.net 'rm -rf /usr/local/tomcat7/work/Catalina'
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no umg-admin.war root@rauat-umgadmin2.altidev.net:/usr/local/tomcat7/webapps
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgadmin2.altidev.net '/usr/local/tomcat7/bin/startup.sh'
################################# UMG-RUNTIME
################################## Server 1
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgruntime1.altidev.net '/usr/local/tomcat7/bin/shutdown.sh'
sleep 10s
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgruntime1.altidev.net "kill -9 \$(ps aux | grep "[t]omcat"  | grep java | awk '{print \$2}')"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgruntime1.altidev.net 'ps -ef | grep "[t]omcat" | grep java'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgruntime1.altidev.net 'rm -rf /usr/local/tomcat7/webapps/umg-runtime'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgruntime1.altidev.net 'rm -rf /usr/local/tomcat7/webapps/umg-runtime.war'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgruntime1.altidev.net 'rm -rf /usr/local/tomcat7/work/Catalina'
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no umg-runtime.war root@rauat-umgruntime1.altidev.net:/usr/local/tomcat7/webapps
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgruntime1.altidev.net '/usr/local/tomcat7/bin/startup.sh'
################################### Server 2
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgruntime2.altidev.net '/usr/local/tomcat7/bin/shutdown.sh'
sleep 10s
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgruntime2.altidev.net "kill -9 \$(ps aux | grep "[t]omcat"  | grep java | awk '{print \$2}')"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgruntime2.altidev.net 'ps -ef | grep "[t]omcat" | grep java'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgruntime2.altidev.net 'rm -rf /usr/local/tomcat7/webapps/umg-runtime'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgruntime2.altidev.net 'rm -rf /usr/local/tomcat7/webapps/umg-runtime.war'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgruntime2.altidev.net 'rm -rf /usr/local/tomcat7/work/Catalina'
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no umg-runtime.war root@rauat-umgruntime2.altidev.net:/usr/local/tomcat7/webapps
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgruntime2.altidev.net '/usr/local/tomcat7/bin/startup.sh'
####################################UMG-ME2
#####################################Server 1
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-me2-server1.altidev.net '/usr/local/tomcat7/bin/shutdown.sh'
sleep 10s
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-me2-server1.altidev.net 'ps -ef | grep "[t]omcat" | grep java'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-me2-server1.altidev.net 'rm -rf /usr/local/tomcat7/webapps/umg-me2'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-me2-server1.altidev.net 'rm -rf /usr/local/tomcat7/webapps/umg-me2.war'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-me2-server1.altidev.net 'rm -rf /usr/local/tomcat7/work/Catalina'
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no umg-me2.war root@rauat-me2-server1.altidev.net:/usr/local/tomcat7/webapps
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-me2-server1.altidev.net '/usr/local/tomcat7/bin/startup.sh'
#########################################Server 2
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-me2-server2.altidev.net '/usr/local/tomcat7/bin/shutdown.sh'
sleep 10s
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-me2-server2.altidev.net 'ps -ef | grep "[t]omcat" | grep java'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-me2-server2.altidev.net 'rm -rf /usr/local/tomcat7/webapps/umg-me2'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-me2-server2.altidev.net 'rm -rf /usr/local/tomcat7/webapps/umg-me2.war'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-me2-server2.altidev.net 'rm -rf /usr/local/tomcat7/work/Catalina'
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no umg-me2.war root@rauat-me2-server2.altidev.net:/usr/local/tomcat7/webapps
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-me2-server2.altidev.net '/usr/local/tomcat7/bin/startup.sh'

################################# UMG Modelet Server
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver1.altidev.net "mkdir -p /opt/umg/matlab_workspace"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver2.altidev.net "mkdir -p /opt/umg/matlab_workspace"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver3.altidev.net "mkdir -p /opt/umg/matlab_workspace"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver4.altidev.net "mkdir -p /opt/umg/matlab_workspace"

ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver1.altidev.net "kill -9 \$(ps aux | grep "[M]ATLAB"  | grep java | awk '{print \$2}')"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver2.altidev.net "kill -9 \$(ps aux | grep "[M]ATLAB"  | grep java | awk '{print \$2}')"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver3.altidev.net "kill -9 \$(ps aux | grep "[M]ATLAB"  | grep java | awk '{print \$2}')"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver4.altidev.net "kill -9 \$(ps aux | grep "[M]ATLAB"  | grep java | awk '{print \$2}')"

ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver1.altidev.net "rm -rf /usr/local/umg/*.jar"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver2.altidev.net "rm -rf /usr/local/umg/*.jar"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver3.altidev.net "rm -rf /usr/local/umg/*.jar"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver4.altidev.net "rm -rf /usr/local/umg/*.jar"

scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  modelet.one-jar.jar root@rauat-modeletserver1.altidev.net:/usr/local/umg
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  modelet.one-jar.jar root@rauat-modeletserver2.altidev.net:/usr/local/umg
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  modelet.one-jar.jar root@rauat-modeletserver3.altidev.net:/usr/local/umg
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  modelet.one-jar.jar root@rauat-modeletserver4.altidev.net:/usr/local/umg

scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  modelet.sh root@rauat-modeletserver1.altidev.net:/usr/local/umg
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  modelet.sh root@rauat-modeletserver2.altidev.net:/usr/local/umg
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  modelet.sh root@rauat-modeletserver3.altidev.net:/usr/local/umg
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  modelet.sh root@rauat-modeletserver4.altidev.net:/usr/local/umg

scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  modelet_8.sh root@rauat-modeletserver1.altidev.net:/usr/local/umg
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  modelet_8.sh root@rauat-modeletserver2.altidev.net:/usr/local/umg
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  modelet_8.sh root@rauat-modeletserver3.altidev.net:/usr/local/umg
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  modelet_8.sh root@rauat-modeletserver4.altidev.net:/usr/local/umg

##ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver1.altidev.net "chmod 777 /usr/local/umg/modelet.sh"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver1.altidev.net "chmod 777 /usr/local/umg/modelet_8.sh"
#####ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver2.altidev.net "chmod 777 /usr/local/umg/modelet.sh"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver2.altidev.net "chmod 777 /usr/local/umg/modelet_8.sh"
########ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver3.altidev.net "chmod 777 /usr/local/umg/modelet.sh"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver3.altidev.net "chmod 777 /usr/local/umg/modelet_8.sh"
#########ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver4.altidev.net "chmod 777 /usr/local/umg/modelet.sh"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver4.altidev.net "chmod 777 /usr/local/umg/modelet_8.sh"

ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver1.altidev.net "/usr/local/umg/modelet_8.sh" 
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver2.altidev.net "/usr/local/umg/modelet_8.sh"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver3.altidev.net "/usr/local/umg/modelet_8.sh"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver4.altidev.net "/usr/local/umg/modelet_8.sh"