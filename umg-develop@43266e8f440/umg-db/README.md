# REALAnalytics database setup
	1. Database setup files are available under folder setup/database.
	2. The database setup contains default configuration values, developer must updated configuration values
	   as per local system setup.
	3. Add the following line at the end of my.ini file of mysql setup:
	    sql_mode=NO_ENGINE_SUBSTITUTION,STRICT_TRANS_TABLES
	4. In case username and password is not set for mongo, we need to do the following changes:
	    1. Update db-context.xml file under umg-admin/sdc/web-ui/src/main/webapp/WEB-INF/spring by removing the following:
	       credentials="${mongo.username}:${mongo.password}@${mongo.dbname}">	     
	       from </mongo:mongo-client>
	    2. Update db-context.xml file under umg-runtime/src/main/webapp/WEB-INF/spring by removing the following:	    
	       credentials="${mongo.username}:${mongo.password}@${mongo.db.name}">	       
	       from </mongo:mongo-client>
		
## umg_admin database setup.
	1. Navigate to mysql installation folder eg : D\mysql\bin
	2. Launch mysql console by typing following command
		..\bin> mysql -u <username> -p <password>
	3. Create new database `umg_admin`
		mysql> create databse `umg_admin`;
	4. Select newly create database 
		mysql> use `umg_admin`;
	5. Execute source command on mysql console
		mysql> source <PROJECT_BASE_DIRECTORY>/setup/database/admin_schema/umg_admin_dump.sql  (eg. mysql> source D:/umg/setup/database/admin_schema/umg_admin_dump.sql)
	6. Run following command to verify if tables have created
		mysql> show tables;		
			+-------------------------------------+
			| Tables_in_umg_admin                 |
			+-------------------------------------+
			| ADDRESS                             |
			| AUTHTOKEN                           |
			| COMMAND                             |
			| MODELET_RESTART_CONFIG              |
			| MODELET_RESTART_CONFIG_AUDIT        |
			| MODEL_EXECUTION_ENVIRONMENTS        |
			| MODEL_EXECUTION_ENVIRONMENTS_AUDIT  |
			| MODEL_EXEC_PACKAGES                 |
			| MODEL_EXEC_PACKAGES_AUDIT           |
			| MODEL_IMPLEMENTATION_TYPE           |
			| NOTIFICATION_EMAIL_TEMPLATE         |
			| NOTIFICATION_EVENT                  |
			| NOTIFICATION_EVENT_TEMPLATE_MAPPING |
			| NOTIFICATION_SMS_TEMPLATE           |
			| NOTIFICATION_TYPE                   |
			| PERMISSIONS                         |
			| PERMISSION_ROLES_MAPPING            |
			| POOL                                |
			| POOL_CRITERIA                       |
			| POOL_CRITERIA_DEF_MAPPING           |
			| POOL_USAGE_ORDER                    |
			| REVINFO                             |
			| ROLES                               |
			| SYNDICATED_DATA                     |
			| SYNDICATED_DATA_AUDIT               |
			| SYSTEM_KEY                          |
			| SYSTEM_MODELETS                     |
			| SYSTEM_PARAMETER                    |
			| SYSTEM_PARAMETER_AUDIT              |
			| TENANT                              |
			| TENANT_CONFIG                       |
			| TENANT_ROLES_MAPPING                |
			| TENANT_USER_MAPPING                 |
			| TENANT_USER_TENANT_ROLE_MAPPING     |
			| TRANSPORT_PARAMETERS                |
			| TRANSPORT_TYPES                     |
			| USERS                               |
			| USERS_LOGIN_AUDIT                   |
			+-------------------------------------+
			38 rows in set (0.03 sec)
			
## Tenant specific database setup
	1. Navigate to mysql installation folder eg : D\mysql\bin
	2. Launch mysql console by typing following command
		..\bin> mysql -u <username> -p <password>
	3. Create new database `localhost`
		mysql> create database `localhost`;
	4. Select newly create database 
		mysql> use `localhost`;
	5. Execute source command on mysql console
		mysql> source <PROJECT_BASE_DIRECTORY>/setup/database/tenant_schema/tenant_schema_dump.sql  (eg. mysql> source D:/umg/setup/database/tenant_schema/tenant_schema_dump.sql)
	6. Run following command to verify if tables have created
		mysql> show tables;	
			+-----------------------------------------+ 
			| Tables_in_localhost_test                | 		
			+-----------------------------------------+ 	
			| BATCH_TRANSACTION                       | 
			| BATCH_TXN_RUNTIME_TXN_MAPPING           | 
			| MAPPING                                 | 
			| MAPPING_AUDIT                           | 
			| MAPPING_INPUT                           | 
			| MAPPING_INPUT_AUDIT                     | 
			| MAPPING_OUTPUT                          | 
			| MAPPING_OUTPUT_AUDIT                    | 
			| MEDIATE_MODEL_LIBRARY                   | 
			| MEDIATE_MODEL_LIBRARY_AUDIT             | 
			| MIGRATION_LOG                           | 
			| MODEL                                   | 
			| MODEL_AUDIT                             | 
			| MODEL_DEFINITION                        | 
			| MODEL_DEFINITION_AUDIT                  | 
			| MODEL_EXEC_PACKAGES                     | 
			| MODEL_EXEC_PACKAGES_AUDIT               | 
			| MODEL_LIBRARY                           | 
			| MODEL_LIBRARY_AUDIT                     | 
			| MODEL_LIB_EXEC_PKG_MAPPING              | 
			| MODEL_LIB_EXEC_PKG_MAPPING_AUDIT        | 
			| MODEL_REPORT_STATUS                     | 
			| MODEL_REPORT_TEMPLATE                   | 
			| MODEL_REPORT_TEMPLATE_AUDIT             | 
			| REVINFO                                 | 
			| SYNDICATE_DATA_QUERY                    | 
			| SYNDICATE_DATA_QUERY_AUDIT              | 
			| SYNDICATE_DATA_QUERY_INPUTS             | 
			| SYNDICATE_DATA_QUERY_INPUTS_AUDIT       | 
			| SYNDICATE_DATA_QUERY_OUTPUTS            | 
			| SYNDICATE_DATA_QUERY_OUTPUTS_AUDIT      | 
			| SYNDICATE_DATA_QUERY_RESULT_TYPES       | 
			| SYNDICATE_DATA_QUERY_RESULT_TYPES_AUDIT | 
			| TRANSPORT_PARAMETER_VALUES              | 
			| UMG_RUNTIME_TRANSACTION                 | 
			| UMG_RUNTIME_TRANSACTION_AUDIT           | 
			| UMG_RUNTIME_TRANSACTION_AUDIT_BK        | 
			| UMG_RUNTIME_TRANSACTION_BK              | 
			| UMG_VERSION                             | 
			| UMG_VERSION_AUDIT                       | 
			| USAGE_SEARCH_REQUEST_CANCEL             | 
			+-----------------------------------------+ 
			41 rows in set (0.03 sec)   

## Updating configuration values as per local system setup for tenant.
	1.Execute following query to fetch tenant configuration details in database.
		select tc.id,t.CODE, s.KEY_TYPE, s.SYSTEM_KEY, tc.CONFIG_VALUE from SYSTEM_KEY s, TENANT_CONFIG tc, TENANT t 
		where tc.SYSTEM_KEY_ID = s.ID and tc.TENANT_ID = t.id;
	2. update config values for all the tenants in TENANT_CONFIG table wih following query
		UPDATE tenant_config tc set tc.config_value = '<new_config_value>'  where  id  = '<tenant_config_id>';
		* DATABSE URL
		* DATABSE USER
		* DATABSE PASSWORD
		* TENANT RUNTIME_BASE_URL

## Updating systen parameter values as per local system setup.
	1. Execute following query to fetch system parameter configurations defined in the system.
		select s.id, s.sys_key, s.sys_value  from system_parameter s;
	2. Update values of system parameters using the following query.
		update system_parameter s set s.sys_value='<new_value>' where s.sys_key = '<key_value>';
		* sanBase
		* UMG_ADMIN_URL
		* umgAdminUrl
		
			