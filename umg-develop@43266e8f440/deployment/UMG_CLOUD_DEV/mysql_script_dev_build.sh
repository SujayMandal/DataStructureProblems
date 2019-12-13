cd ../umg/branches/UMG_1052_ADAUTH_11SEP/umg-db/src/main/resources/db/umg-admin/0.0.1

/c/mysql-replication/mysql-master-1/bin/mysql --host=radev-mysql.altidev.net --user=umgdev --password='Dev#2014' < main.sql

/c/mysql-replication/mysql-master-1/bin/mysql --host=radev-mysql.altidev.net --user=umgdev --password='Dev#2014' < system_data_cloud_dev.sql

cd ../1.1

/c/mysql-replication/mysql-master-1/bin/mysql --host=radev-mysql.altidev.net --user=umgdev --password='Dev#2014' < main.sql

cd ../../umg/0.0.1

/c/mysql-replication/mysql-master-1/bin/mysql --host=radev-mysql.altidev.net --user=umgdev --password='Dev#2014' < tenant_schema.sql

cd ../1.1

/c/mysql-replication/mysql-master-1/bin/mysql --host=radev-mysql.altidev.net --user=umgdev --password='Dev#2014' < update_tenant_schema.sql


