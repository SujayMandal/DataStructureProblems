#!/bin/sh

# UMG-ADMIN
# Server 1
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgadmin1.altidev.net "ps aux | grep "[t]omcat"  | grep java | awk '{print \$2}'"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgadmin2.altidev.net "ps aux | grep "[t]omcat"  | grep java | awk '{print \$2}'"
# UMG-RUNTIME
# Server 1
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgruntime1.altidev.net "ps aux | grep "[t]omcat"  | grep java | awk '{print \$2}'"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-umgruntime2.altidev.net "ps aux | grep "[t]omcat"  | grep java | awk '{print \$2}'"
# UMG-ME2
# Server 1
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-me2-server1.altidev.net "ps aux | grep "[t]omcat"  | grep java | awk '{print \$2}'"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-me2-server2.altidev.net "ps aux | grep "[t]omcat"  | grep java | awk '{print \$2}'"
# UMG Modelet Server 
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver1.altidev.net "ps aux | grep "[M]ATLAB" | grep java |awk '{print \$2}'"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver2.altidev.net "ps aux | grep "[M]ATLAB" | grep java |awk '{print \$2}'"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver3.altidev.net "ps aux | grep "[M]ATLAB" | grep java |awk '{print \$2}'"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@raprod-modeletserver4.altidev.net "ps aux | grep "[M]ATLAB" | grep java |awk '{print \$2}'"