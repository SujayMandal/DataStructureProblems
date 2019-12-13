cd ../umg/branches/1.1.0-RELEASE-IAM/umg-db/src/main/resources/db/umg-admin/0.0.1

/c/mysql-replication/mysql-master-1/bin/mysql --host=raprod-mysql1.altidev.net --user=umgprod --password='Prod#2014' < create_tenant_schema_cloud.sql

/c/mysql-replication/mysql-master-1/bin/mysql --host=raprod-mysql1.altidev.net --user=umgprod --password='Prod#2014' < main.sql

/c/mysql-replication/mysql-master-1/bin/mysql --host=raprod-mysql1.altidev.net --user=umgprod --password='Prod#2014' < system_data_cloud_prod.sql

cd ../1.1

/c/mysql-replication/mysql-master-1/bin/mysql --host=raprod-mysql1.altidev.net --user=umgprod --password='Prod#2014' < main.sql

cd ../../umg/0.0.1

/c/mysql-replication/mysql-master-1/bin/mysql --host=raprod-mysql1.altidev.net --user=umgprod --password='Prod#2014' < tenant_schema_cloud.sql

cd ../1.1

/c/mysql-replication/mysql-master-1/bin/mysql --host=raprod-mysql1.altidev.net --user=umgprod --password='Prod#2014' < update_tenant_schema_cloud.sql
