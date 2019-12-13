#!/bin/sh

# UMG-ADMIN
# Server 1
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgadmin1.altidev.net "ps aux | grep "[t]omcat"  | grep java | awk '{print \$2}'"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgadmin2.altidev.net "ps aux | grep "[t]omcat"  | grep java | awk '{print \$2}'"
# UMG-RUNTIME
# Server 1
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgruntime1.altidev.net "ps aux | grep "[t]omcat"  | grep java | awk '{print \$2}'"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-umgruntime2.altidev.net "ps aux | grep "[t]omcat"  | grep java | awk '{print \$2}'"
# UMG-ME2
# Server 1
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-me2-server1.altidev.net "ps aux | grep "[t]omcat"  | grep java | awk '{print \$2}'"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-me2-server2.altidev.net "ps aux | grep "[t]omcat"  | grep java | awk '{print \$2}'"
# UMG Modelet Server 
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver1.altidev.net "ps aux | grep "[M]ATLAB" | grep java |awk '{print \$2}'"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver2.altidev.net "ps aux | grep "[M]ATLAB" | grep java |awk '{print \$2}'"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver3.altidev.net "ps aux | grep "[M]ATLAB" | grep java |awk '{print \$2}'"
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@rauat-modeletserver4.altidev.net "ps aux | grep "[M]ATLAB" | grep java |awk '{print \$2}'"