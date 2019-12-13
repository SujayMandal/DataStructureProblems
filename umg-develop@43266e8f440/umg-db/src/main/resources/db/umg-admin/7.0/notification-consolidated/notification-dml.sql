use umg_admin;

set @CREATED_BY = 'SYSTEM';

set @CREATED_ON = UNIX_TIMESTAMP() * 1000;

set SQL_SAFE_UPDATES = 0;


TRUNCATE TABLE NOTIFICATION_TYPE;

TRUNCATE TABLE NOTIFICATION_EVENT;

TRUNCATE TABLE NOTIFICATION_EMAIL_TEMPLATE;

TRUNCATE TABLE NOTIFICATION_EVENT_TEMPLATE_MAPPING;


DELETE FROM SYSTEM_PARAMETER WHERE SYS_KEY in ('SMTP_HOST_NAME', 'SMTP_SERVER', 'NOTIFICATION_TEMPLATE_DIR', 'UMG_ADMIN_URL', 'FROM_ADDRESS', 'fromAddress', 'toAddress');

DELETE FROM SYSTEM_PARAMETER_AUDIT WHERE SYS_KEY in ('SMTP_HOST_NAME', 'SMTP_SERVER', 'NOTIFICATION_TEMPLATE_DIR', 'UMG_ADMIN_URL', 'FROM_ADDRESS', 'fromAddress', 'toAddress');


INSERT INTO NOTIFICATION_TYPE(ID, TYPE, DESCRIPTION, CREATED_BY, CREATED_ON) 
	VALUES ('07E1739D-467E-42BE-8882-DAD8EDB7465B', 'MAIL', 'Mail notification type', @CREATED_BY, @CREATED_ON);

INSERT INTO NOTIFICATION_TYPE(ID, TYPE, DESCRIPTION, CREATED_BY, CREATED_ON) 
	VALUES ('07E1739D-467E-42BE-8882-DAD8EDB7466B', 'SMS', 'SMS notification type', @CREATED_BY, @CREATED_ON);
	

	

INSERT INTO NOTIFICATION_EVENT(ID, NAME, DESCRIPTION, CLASSIFICATION, CREATED_BY, CREATED_ON)
	VALUES ('9FE824C2-5FB4-4C97-AB47-4FA2E33A1C8B', 'On Model Publish', 'Mail will be sent when Model is successfully published', 'Feature', @CREATED_BY, @CREATED_ON);


INSERT INTO NOTIFICATION_EVENT(ID, NAME, DESCRIPTION, CLASSIFICATION, CREATED_BY, CREATED_ON)
	VALUES ('9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B', 'Runtime Transaction Failure', 'Mail will be sent when Runtime throws RSV Errors like 807, 804', 'System', @CREATED_BY, @CREATED_ON);


INSERT INTO NOTIFICATION_EVENT(ID, NAME, DESCRIPTION, CLASSIFICATION, CREATED_BY, CREATED_ON)
	VALUES ('9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B', 'Model Publish Approval', 'Mail will be sent when Model is requested to for approval to publish', 'Feature', @CREATED_BY, @CREATED_ON);

INSERT INTO NOTIFICATION_EVENT(ID, NAME, DESCRIPTION, CLASSIFICATION, CREATED_BY, CREATED_ON)
	VALUES ('9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B', 'New Tenant Added', 'Mail will be sent when new tenant get added', 'System', @CREATED_BY, @CREATED_ON);
	


	

INSERT INTO NOTIFICATION_EMAIL_TEMPLATE(ID, NOTIFICATION_EVENT_ID, NAME, DESCRIPTION, BODY_DEFINITION, SUBJECT_DEFINITION, MAIL_CONTENT_TYPE, CREATED_BY, CREATED_ON)
	VALUES ('1D5ED0C3-C206-4AD3-81CB-896FCCFD8D81', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C8B', 'MODEL PUBLISH MAIL TEMPLATE', 'MODEL PUBLISH MAIL TEMPLATE', '<html> <body style="font-family: Calibri;">Hi, <br><br>Following Model has been published in $environment <ul><li>Model Name: $modelName</li><li>Model Published Timestamp: $publishedDate</li><li>Tenant Name: $tenantName</li><li>Approver Name: $publisherName</li> </ul><b>Regards,</b><br><b>REALAnalytics Team</b> </body></html>', 'REALAnalytics $environment: $modelName $modelVersion model published', 'MIME', 'System', 1234567890);


INSERT INTO NOTIFICATION_EMAIL_TEMPLATE(ID, NOTIFICATION_EVENT_ID, NAME, DESCRIPTION, BODY_DEFINITION, SUBJECT_DEFINITION, MAIL_CONTENT_TYPE, CREATED_BY, CREATED_ON)
	VALUES ('1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B', 'RUNTIME FAILURE MAIL TEMPLATE', 'RUNTIME FAILURE MAIL TEMPLATE', '<html> <body style="font-family: Calibri;">Hi, <br><br>Following error has been encountered during execution in $environment <ul><li> Tenant Transaction Id: $transactionId </li><li> Execution Time Stamp: $executionTime </li><li> Error Details: $errorCode $errorMessage </li><li> Model Name: $modelName </li><li> Model Version: $modelVersion </li><li> Tenant Name: $tenantName </li></ul><b>Regards,</b><br><b>REALAnalytics Team</b></body></html>', 'REALAnalytics $environment: Transaction error $errorCode for $tenantName tenant', 'MIME', 'System', 1234567890);
 	

INSERT INTO NOTIFICATION_EMAIL_TEMPLATE(ID, NOTIFICATION_EVENT_ID, NAME, DESCRIPTION, BODY_DEFINITION, SUBJECT_DEFINITION, MAIL_CONTENT_TYPE, CREATED_BY, CREATED_ON)
	VALUES ('1D5ED0C3-C206-4AD3-81CB-896FCCFD8D83', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B', 'MODEL APPROVAL EVENT TEMPLATE', 'MODEL APPROVAL EVENT TEMPLATE', '<html> <body style="font-family: Calibri;">Hi,<br><br>Following Model is awaiting your approval for publishing in $environment<ul><li>Model Name: $modelName</li><li>Model Version: $modelVersion</li><li>Tenant Name: $tenantName</li><li>Requestor Name: $publisherName</li></ul> Please refer the attached Release Notes for further details about the model. <br> <br>Click on the following link to APPROVE Model for publishing. <br><br> <a href="$modelApprovalURL">Approve Publish Request</a> <br><br>Please Note: For security reasons, do not share this email.<br><br><b>Regards,</b><br><b>REALAnalytics Team</b> </body></html>', 'REALAnalytics $environment: Approval required for $modelName$modelVersion model publishing', 'MIME', 'System', 1234567890);


INSERT INTO NOTIFICATION_EMAIL_TEMPLATE(ID, NOTIFICATION_EVENT_ID, NAME, DESCRIPTION, BODY_DEFINITION, SUBJECT_DEFINITION, MAIL_CONTENT_TYPE, CREATED_BY, CREATED_ON)
	VALUES ('1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B', 'NEW TENANT ADDED TEMPLATE', 'NEW TENANT ADDED EVENT TEMPLATE', '<html> <body style="font-family: Calibri;">Hi, <br><br>Following Tenant has been onboarded in $environment <ul><li> Tenant Name: $tenantName </li><li> Tenant Code : $tenantCode </li><li> Batch Enabled:$batchEnabled </li><li> Bulk Enabled : $bulkEnabled </li><li> Email Notifications Enabled : $emailNotificationsEnabled </li><li> Tenant Onboarded on : $tenantOnboardedOn </li><li> Tenant Onboarded by : $tenantOnboardedBy </li></ul><b>Regards,</b><br><b>REALAnalytics Team</b></body></html>', 'REALAnalytics $environment: Tenant $tenantName added', 'MIME', 'System', 1234567890);


	


set @TENANT_CODE = 'rentrange';	
set @TO_ADDRESS = 'umgqe@altisource.com';
set @FROM_ADDRESS = 'REALAnalytics-QE@altisource.com';
set @CC_ADDRESS = '';
set @BCC_ADDRESS = '';

INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NAME, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb401', 'On Model Publish', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C8B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D81', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);


INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NAME, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb403', 'Model Publish Approval', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D83', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);

	
set @TO_ADDRESS = '';
	
INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NAME, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb402', 'Runtime Transaction Failure', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);


	
	
	

set @TENANT_CODE = 'ocwen';	
set @TO_ADDRESS = 'umgqe@altisource.com';
set @FROM_ADDRESS = 'REALAnalytics-QE@altisource.com';
set @CC_ADDRESS = '';
set @BCC_ADDRESS = '';	

INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NAME, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb404', 'On Model Publish', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C8B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D81', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);

	
INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NAME, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb406', 'Model Publish Approval', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D83', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);

	
set @TO_ADDRESS = '';
	
INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NAME, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb405', 'Runtime Transaction Failure', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);

	



set @TENANT_CODE = 'equator';	
set @TO_ADDRESS = 'umgqe@altisource.com';
set @FROM_ADDRESS = 'REALAnalytics-QE@altisource.com';
set @CC_ADDRESS = '';
set @BCC_ADDRESS = '';	

INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NAME, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb407', 'On Model Publish', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C8B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D81', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);


INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NAME, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb409', 'Model Publish Approval', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D83', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);

	
set @TO_ADDRESS = '';
	
INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NAME, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb408', 'Runtime Transaction Failure', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);

	
	

	

set @TENANT_CODE = 'hubzu';	
set @TO_ADDRESS = 'umgqe@altisource.com';
set @FROM_ADDRESS = 'REALAnalytics-QE@altisource.com';
set @CC_ADDRESS = '';
set @BCC_ADDRESS = '';		

INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NAME, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb410', 'On Model Publish', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C8B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D81', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);


INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NAME, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb412', 'Model Publish Approval', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D83', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);

	
set @TO_ADDRESS = '';
	
INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NAME, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb411', 'Runtime Transaction Failure', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);

	
	



set @TENANT_CODE = 'realtrans';	
set @TO_ADDRESS = 'umgqe@altisource.com';
set @FROM_ADDRESS = 'REALAnalytics-QE@altisource.com';
set @CC_ADDRESS = '';
set @BCC_ADDRESS = '';		

INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NAME, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb413', 'On Model Publish', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C8B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D81', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);


INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NAME, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb415', 'Model Publish Approval', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D83', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);

	
set @TO_ADDRESS = '';
	
INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NAME, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb414', 'Runtime Transaction Failure', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);

	
	

	

set @TENANT_CODE = 'localhost';	
set @TO_ADDRESS = 'umgqe@altisource.com';
set @FROM_ADDRESS = 'REALAnalytics-QE@altisource.com';
set @CC_ADDRESS = '';
set @BCC_ADDRESS = '';		

INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NAME, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb416', 'On Model Publish', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C8B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D81', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);


INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NAME, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb418', 'Model Publish Approval', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D83', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);
	

set @TO_ADDRESS = '';
	
INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NAME, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb417', 'Runtime Transaction Failure', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);

	


	
	
	
	

	
	
	
INSERT INTO SYSTEM_PARAMETER (`ID`, `SYS_KEY`, `DESCRIPTION`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
	VALUES ('af5e85e1-47k5-4ahc-9e6bdda18d88x', 'SMTP_HOST_NAME', 'SMTP Server Host Name', 'NAV8EHCNMP01.ASCORP.COM', 'Y', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
	
	
INSERT INTO SYSTEM_PARAMETER (`ID`, `SYS_KEY`, `DESCRIPTION`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
	VALUES ('af5e85e1-47k5-4ahc-9e6bdda18d99x', 'SMTP_SERVER', 'SMTP Server', 'mail02.svc.den.vz.altidev.net', 'Y', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
	
	

INSERT INTO SYSTEM_PARAMETER_AUDIT (`ID`, `SYS_KEY`, `DESCRIPTION`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
	VALUES ('af5e85e1-47k5-4ahc-9e6bdda1888x', 'SMTP_HOST_NAME', 'SMTP Server Host Name', 'NAV8EHCNMP01.ASCORP.COM', 'Y', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);

INSERT INTO SYSTEM_PARAMETER_AUDIT (`ID`, `SYS_KEY`, `DESCRIPTION`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
	VALUES ('af5e85e1-47k5-4ahc-9e6bdda1899x', 'SMTP_SERVER', 'SMTP Server', 'mail02.svc.den.vz.altidev.net', 'Y', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);	


	
INSERT INTO SYSTEM_PARAMETER (`ID`, `SYS_KEY`, `DESCRIPTION`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
	VALUES ('af5e85e1-47k5-4ahc-9e6bdda18d77x', 'NOTIFICATION_TEMPLATE_DIR', 'NOTIFICATION_TEMPLATE_DIR', '/usr/tmp/', 'Y', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);

	
INSERT INTO SYSTEM_PARAMETER_AUDIT (`ID`, `SYS_KEY`, `DESCRIPTION`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
	VALUES ('af5e85e1-47k5-4ahc-9e6bdda18d77x', 'NOTIFICATION_TEMPLATE_DIR', 'NOTIFICATION_TEMPLATE_DIR', '/usr/tmp/', 'Y', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);

	
	
INSERT INTO SYSTEM_PARAMETER (`ID`, `SYS_KEY`, `DESCRIPTION`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
	VALUES ('af5e85e1-47k5-4ahc-106bdda18d27x', 'UMG_ADMIN_URL', 'UMG ADMIN URL', 'http://localhost:9090/umg-admin', 'Y', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);


INSERT INTO SYSTEM_PARAMETER_AUDIT (`ID`, `SYS_KEY`, `DESCRIPTION`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
	VALUES ('af5e85e1-47k5-4ahc-106bdda18d78x', 'UMG_ADMIN_URL', 'UMG ADMIN URL', 'http://localhost:9090/umg-admin', 'Y', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);


set @FROM_ADDRESS = 'REALAnalytics-QE@altisource.com';

	
INSERT INTO SYSTEM_PARAMETER (`ID`, `SYS_KEY`, `DESCRIPTION`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
	VALUES ('af5e85e1-47k5-4ahc-106bdda18d69x', 'FROM_ADDRESS', 'From address for tenant added', @FROM_ADDRESS, 'Y', CREATED_BY, @CREATED_ON, CREATED_BY, @CREATED_ON);

	
INSERT INTO SYSTEM_PARAMETER_AUDIT (`ID`, `SYS_KEY`, `DESCRIPTION`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
	VALUES ('af5e85e1-47k5-4ahc-106bdda18d79x', 'FROM_ADDRESS', 'TO Address for tenant added', @SUPER_ADMIN_EMAIL, 'Y', CREATED_BY, @CREATED_ON, CREATED_BY, @CREATED_ON);
	
COMMIT;