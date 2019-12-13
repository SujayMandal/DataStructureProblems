#Admin Server
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@radev-umgadm-webserver.altidev.net '/usr/local/tomcat7/bin/shutdown.sh'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@radev-umgadm-webserver.altidev.net 'rm -rf /usr/local/tomcat7/webapps/umg-admin'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@radev-umgadm-webserver.altidev.net 'rm -rf /usr/local/tomcat7/webapps/umg-admin.war'
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no umg-admin.war root@radev-umgadm-webserver.altidev.net:/usr/local/tomcat7/webapps
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@radev-umgadm-webserver.altidev.net '/usr/local/tomcat7/bin/startup.sh'
#Runtime Server
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@radev-umgruntime.altidev.net '/usr/local/tomcat7/bin/shutdown.sh'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@radev-umgruntime.altidev.net 'rm -rf /usr/local/tomcat7/webapps/umg-runtime'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@radev-umgruntime.altidev.net 'rm -rf /usr/local/tomcat7/webapps/umg-runtime.war'
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no umg-runtime.war root@radev-umgruntime.altidev.net:/usr/local/tomcat7/webapps
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@radev-umgruntime.altidev.net '/usr/local/tomcat7/bin/startup.sh'
#ME2 Server
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@radev-me2.altidev.net '/usr/local/tomcat7/bin/shutdown.sh'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@radev-me2.altidev.net 'rm -rf /usr/local/tomcat7/webapps/umg-me2'
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@radev-me2.altidev.net 'rm -rf /usr/local/tomcat7/webapps/umg-me2.war'
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no umg-me2.war root@radev-me2.altidev.net:/usr/local/tomcat7/webapps
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@radev-me2.altidev.net '/usr/local/tomcat7/bin/startup.sh'
# UMG Modelet Server
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@radev-modeletserver1.altidev.net "kill $(ps aux | grep MATLAB  | grep java | awk '{print $2}')"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@radev-modeletserver2.altidev.net "kill $(ps aux | grep MATLAB  | grep java | awk '{print $2}')"

scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no modelet-0.0.1-SNAPSHOT.one-jar.jar root@radev-modeletserver1.altidev.net:/usr/local/umg
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no modelet.sh root@radev-modeletserver1.altidev.net:/usr/local/umg
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@radev-modeletserver1.altidev.net "chmod 777 /usr/local/umg/modelet.sh"

scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no modelet-0.0.1-SNAPSHOT.one-jar.jar root@radev-modeletserver2.altidev.net:/usr/local/umg
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no modelet.sh root@radev-modeletserver2.altidev.net:/usr/local/umg
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@radev-modeletserver2.altidev.net "chmod 777 /usr/local/umg/modelet.sh"

ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@radev-modeletserver1.altidev.net "/usr/local/umg/modelet.sh"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@radev-modeletserver2.altidev.net "/usr/local/umg/modelet.sh"