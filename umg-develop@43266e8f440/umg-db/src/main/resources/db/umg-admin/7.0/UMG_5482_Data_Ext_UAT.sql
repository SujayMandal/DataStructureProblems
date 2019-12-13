use umg_admin;

set @CREATED_BY = 'SYSTEM';

set @CREATED_ON = '1401344421';

set @TENANT_LOCALHOST = 'localhost';

set @EMAIL = 'Nageswara.Reddy@altisource.com';

set SQL_SAFE_UPDATES = 0;


TRUNCATE TABLE NOTIFICATION_TYPE;

TRUNCATE TABLE NOTIFICATION_EVENT;

TRUNCATE TABLE NOTIFICATION_EMAIL_TEMPLATE;

TRUNCATE TABLE NOTIFICATION_EVENT_TEMPLATE_MAPPING;


	

	
	
set @TENANT_CODE = 'rentrange';	
set @TO_ADDRESS = 'umgqe@altisource.com;realanalyticssupport@altisource.com';
set @FROM_ADDRESS = 'REALAnalytics-ExtUAT@altisource.com';
set @CC_ADDRESS = 'ketan.gandhi@altisource.com;ram.malapaka@altisource.com;udaya.kiranss@altisource.com';
set @BCC_ADDRESS = '';

INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb401', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C8B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D81', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);


INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb402', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);


set @TO_ADDRESS = 'bin.yu@altisource.com';
set @CC_ADDRESS = '';
INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb403', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D83', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);
	
	
	
	

	
set @TENANT_CODE = 'ocwen';	
set @TO_ADDRESS = 'umgqe@altisource.com;realanalyticssupport@altisource.com';
set @FROM_ADDRESS = 'REALAnalytics-ExtUAT@altisource.com';
set @CC_ADDRESS = 'ketan.gandhi@altisource.com;ram.malapaka@altisource.com;udaya.kiranss@altisource.com';
set @BCC_ADDRESS = '';	

INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb404', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C8B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D81', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);


INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb405', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);

set @TO_ADDRESS = 'ketan.gandhi@altisource.com';
set @CC_ADDRESS = '';	
INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb406', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D83', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);
	

	
	
	


set @TENANT_CODE = 'equator';	
set @TO_ADDRESS = 'umgqe@altisource.com;realanalyticssupport@altisource.com';
set @FROM_ADDRESS = 'REALAnalytics-ExtUAT@altisource.com';
set @CC_ADDRESS = 'ketan.gandhi@altisource.com;ram.malapaka@altisource.com;udaya.kiranss@altisource.com';
set @BCC_ADDRESS = '';	

INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb407', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C8B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D81', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);


INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb408', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);

set @TO_ADDRESS = 'ketan.gandhi@altisource.com';
set @CC_ADDRESS = '';		
INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb409', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D83', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);
	

	
	
	
	
	
set @TENANT_CODE = 'hubzu';	
set @TO_ADDRESS = 'umgqe@altisource.com;realanalyticssupport@altisource.com';
set @FROM_ADDRESS = 'REALAnalytics-ExtUAT@altisource.com';
set @CC_ADDRESS = 'ketan.gandhi@altisource.com;ram.malapaka@altisource.com;udaya.kiranss@altisource.com';
set @BCC_ADDRESS = '';		

INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb410', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C8B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D81', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);


INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb411', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);

set @TO_ADDRESS = 'prem.swaroop@altisource.com';
set @CC_ADDRESS = '';	
	
INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb412', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D83', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);
	








set @TENANT_CODE = 'realtrans';	
set @TO_ADDRESS = 'umgqe@altisource.com;realanalyticssupport@altisource.com';
set @FROM_ADDRESS = 'REALAnalytics-ExtUAT@altisource.com';
set @CC_ADDRESS = 'ketan.gandhi@altisource.com;ram.malapaka@altisource.com;udaya.kiranss@altisource.com';
set @BCC_ADDRESS = '';		

INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb413', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C8B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D81', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);


INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb414', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);

set @TO_ADDRESS = 'prem.swaroop@altisource.com';
set @CC_ADDRESS = '';	
	
INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb415', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D83', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_CODE, @TO_ADDRESS, @FROM_ADDRESS, @CC_ADDRESS, @BCC_ADDRESS, @CREATED_BY, @CREATED_ON);
	
		
	


commit;