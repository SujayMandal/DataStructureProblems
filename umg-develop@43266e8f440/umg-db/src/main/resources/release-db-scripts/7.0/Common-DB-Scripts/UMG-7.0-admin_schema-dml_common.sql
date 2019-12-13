use umg_admin;

set SQL_SAFE_UPDATES = 0;

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
	
INSERT INTO NOTIFICATION_EVENT(ID, NAME, DESCRIPTION, CREATED_BY, CREATED_ON)
	VALUES ('9FE824C2-5FB4-4C97-AB47-4FA2E33A1C5B', 'Auth Token Resend Event', 'Mail will be sent when request for resend auth token', @CREATED_BY, @CREATED_ON);
	
INSERT INTO `NOTIFICATION_EVENT` (`ID`, `NAME`, `DESCRIPTION`, `CLASSIFICATION`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
	VALUES ('E3453E48-38DF-4424-A42F-69784A17D11F', 'Excessive Model Exec Time', 'Mail will be sent on excessive model exec time', 'System', 'SYSTEM', 1465810235, 'SYSTEM', 1465810244);
	
INSERT INTO `NOTIFICATION_EVENT` (`ID`, `NAME`, `DESCRIPTION`, `CLASSIFICATION`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
	VALUES ('E76CCC0A-33A5-11E6-AC61-9E71128CAE77', 'Modelet Restart', 'Mail will be sent when modelet restart command is initiated', 'System', 'SYSTEM', 1465810235, 'SYSTEM', 1465810244);


	
INSERT INTO NOTIFICATION_EMAIL_TEMPLATE(ID, NOTIFICATION_EVENT_ID, NAME, DESCRIPTION, BODY_DEFINITION, SUBJECT_DEFINITION, MAIL_CONTENT_TYPE, CREATED_BY, CREATED_ON)
	VALUES ('1D5ED0C3-C206-4AD3-81CB-896FCCFD8D81', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C8B', 'MODEL PUBLISH MAIL TEMPLATE', 'MODEL PUBLISH MAIL TEMPLATE', '<html> <body style="font-family: Calibri;">Hi, <br><br>Following Model has been published in $environment <ul><li>Model Name: $modelName</li><li>Model Published Timestamp: $publishedDate</li><li>Tenant Name: $tenantName</li><li>Approver Name: $publisherName</li> </ul><b>Regards,</b><br><b>REALAnalytics Team</b> </body></html>', 'REALAnalytics $environment: $modelName $modelVersion model published', 'MIME', 'System', 1234567890);

INSERT INTO NOTIFICATION_EMAIL_TEMPLATE(ID, NOTIFICATION_EVENT_ID, NAME, DESCRIPTION, BODY_DEFINITION, SUBJECT_DEFINITION, MAIL_CONTENT_TYPE, CREATED_BY, CREATED_ON)
	VALUES ('1D5ED0C3-C206-4AD3-81CB-896FCCFD8D82', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C9B', 'RUNTIME FAILURE MAIL TEMPLATE', 'RUNTIME FAILURE MAIL TEMPLATE', '<html><style>table, th, td {border: 1px solid black;border-collapse: collapse;}th, td {padding: 5px;text-align: center;}th {background-color: #F5F5F5;}</style><body style="font-family: Calibri;">Hi,<br><br>Following error has been encountered during execution in $environment<ul><li> Tenant Transaction Id: $transactionId </li><li> Execution Time Stamp: $executionTime </li><li> Error Details: $errorCode $errorMessage </li><li> Model Name: $modelName </li><li> Model Version: $modelVersion </li><li> Tenant Name: $tenantName </li></ul> Modelet Status at  $executionTime , Refer table: #set( $count = 1 )<TABLE><TR><TH>Sl no.</TH><TH>Modelet</TH><TH>Pool Name</TH><TH>Status</TH>#foreach( $modelet in $modeletList)</TR><TR><TD>$count</TD><TD>$modelet.host :$modelet.port </TD><TD>$modelet.poolName</TD><TD>$modelet.modeletStatus</TD></TR> #set( $count = $count + 1 ) #end</TABLE><br><br><b>Regards,</b><br><b>REALAnalytics Team</b></body></html>', 'REALAnalytics $environment: Transaction error $errorCode for $tenantName tenant', 'MIME', 'System', 1234567890);
 	
INSERT INTO NOTIFICATION_EMAIL_TEMPLATE(ID, NOTIFICATION_EVENT_ID, NAME, DESCRIPTION, BODY_DEFINITION, SUBJECT_DEFINITION, MAIL_CONTENT_TYPE, CREATED_BY, CREATED_ON)
	VALUES ('1D5ED0C3-C206-4AD3-81CB-896FCCFD8D83', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C7B', 'MODEL APPROVAL EVENT TEMPLATE', 'MODEL APPROVAL EVENT TEMPLATE', '<html> <body style="font-family: Calibri;">Hi,<br><br>Following Model is awaiting your approval for publishing in $environment<ul><li>Model Name: $modelName</li><li>Model Version: $modelVersion</li><li>Tenant Name: $tenantName</li><li>Requestor Name: $publisherName</li></ul> Please refer the attached Release Notes for further details about the model. <br> <br>Click on the following link to APPROVE Model for publishing. <br><br> <a href="$modelApprovalURL">Approve Publish Request</a> <br><br>Please Note: For security reasons, do not share this email.<br><br><b>Regards,</b><br><b>REALAnalytics Team</b> </body></html>', 'REALAnalytics $environment: Approval required for $modelName$modelVersion model publishing', 'MIME', 'System', 1234567890);

INSERT INTO NOTIFICATION_EMAIL_TEMPLATE(ID, NOTIFICATION_EVENT_ID, NAME, DESCRIPTION, BODY_DEFINITION, SUBJECT_DEFINITION, MAIL_CONTENT_TYPE, CREATED_BY, CREATED_ON)
	VALUES ('1D5ED0C3-C206-4AD3-81CB-896FCCFD8D84', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C6B', 'NEW TENANT ADDED TEMPLATE', 'NEW TENANT ADDED EVENT TEMPLATE', '<html> <body style="font-family: Calibri;">Hi, <br><br>Following Tenant has been onboarded in $environment <ul><li> Tenant Name: $tenantName </li><li> Tenant Code : $tenantCode </li><li> Batch Enabled:$batchEnabled </li><li> Bulk Enabled : $bulkEnabled </li><li> Email Notifications Enabled : $emailNotificationsEnabled </li><li> Tenant Onboarded on : $tenantOnboardedOn </li><li> Tenant Onboarded by : $tenantOnboardedBy </li></ul><b>Regards,</b><br><b>REALAnalytics Team</b></body></html>', 'REALAnalytics $environment: Tenant $tenantName added', 'MIME', 'System', 1234567890);
	
INSERT INTO NOTIFICATION_EVENT_TEMPLATE_MAPPING (ID, NOTIFICATION_EVENT_ID, NOTIFICATION_TEMPLATE_ID, NOTIFICATION_TYPE_ID, TENANT_ID, TO_ADDRESS,   FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, CREATED_BY, CREATED_ON)
	VALUES ('9245ecf2-cd13-4e7a-9c25-b5afb45bb404', '9FE824C2-5FB4-4C97-AB47-4FA2E33A1C5B', '1D5ED0C3-C206-4AD3-81CB-896FCCFD8D85', '07E1739D-467E-42BE-8882-DAD8EDB7465B', @TENANT_LOCALHOST, @EMAIL, @EMAIL, @EMAIL, @EMAIL, @CREATED_BY, @CREATED_ON);

INSERT INTO `NOTIFICATION_EMAIL_TEMPLATE` (`ID`, `NOTIFICATION_EVENT_ID`, `NAME`, `DESCRIPTION`, `BODY_DEFINITION`, `SUBJECT_DEFINITION`, `IS_ACTIVE`, `MAJOR_VERSION`, `MAIL_CONTENT_TYPE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
	VALUES ('6259B6AB-C2BC-4720-A997-3C54738BB4A4', 'E3453E48-38DF-4424-A42F-69784A17D11F', 'EXCESSION RUNTIME TEMPLATE', 'EXCESSIVE RUNTIME TEMPLATE', '<html> <body style="font-family: Calibri;">Hi, <br><br>$modelName _ $modelVersion model has been processing a transaction for more than $excessRuntime seconds in  $environment environment.The modelet has been stopped.Please find further details below:<br><ul><li> Tenant Name: $tenantName </li><li>Tenant Transaction Id: $clienttransactionId</li><li>RA Transaction Id: $transactionId</li><li>Processing Start Time: $modelStartTime</li><li>Memory Usage at Processing Start: NA<li>CPU Usage at Processing Start: NA<li>Current Memory Usage: NA</li><li>Current CPU Usage: NA</li><li>Modelet: Server IP:$modeletHost Port :$port</li><li>Pool Name: $poolName</li></ul><b>Regards,</b><br><b>REALAnalytics Team</b></body></html>', 'REALAnalytics $environment: Model $modelName - $modelVersion taking too long to execute', 1, 1, 'MIME', 'System', 1465811587, NULL, NULL);	

INSERT INTO `NOTIFICATION_EMAIL_TEMPLATE` (`ID`, `NOTIFICATION_EVENT_ID`, `NAME`, `DESCRIPTION`, `BODY_DEFINITION`, `SUBJECT_DEFINITION`, `IS_ACTIVE`, `MAJOR_VERSION`, `MAIL_CONTENT_TYPE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
	VALUES ('E76CCF02-33A5-11E6-AC61-9E71128CAE77', 'E76CCC0A-33A5-11E6-AC61-9E71128CAE77', 'MODELET RESTART TEMPLATE', 'MODELET RESTART TEMPLATE', '<html><body style="font-family: Calibri;">Hi,<br><br>Modelet $modeletHost:$port has been restarted in $environment environment. Please find further details below:<ul><li>Current Pool Name: $poolName</li><li>New Pool Name: $newPoolName</li><li>Restart Time: $modeletRestartTime</li><li>Model Name: $loadedModel & Model Version: $loadedModelVersion prior to restart</li><li>Model Name: $modelToLoad & Model Version: $modelVersionToLoad requested model</li><li>Transactio ID: $transactionId</li><li>Transaction Run Date: $transactionRunDate</li><li>Restart Reason: $reason</li></ul><b>Regards,</b><br><b>REALAnalytics Team</b></body></html>', 'REALAnalytics $environment: Modelet restarted', 1, 1, 'MIME', 'System', 1465811587, NULL, NULL);


INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbd27e-0d39-11e6-8666-00ffbc73cbd1', 'Dashboard.BatchBulk', 'page', 'batchDashboard');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbd8a5-0d39-11e6-8666-00ffbc73cbd1', 'Dashboard.BatchBulk.DownloadIO', 'action', 'batchTransactionDashboard_search');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbdc94-0d39-11e6-8666-00ffbc73cbd1', 'Dashboard.BatchBulk.TerminateBatch', 'action', 'terminnateSelectedItems_id');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbe05f-0d39-11e6-8666-00ffbc73cbd1', 'Dashboard.BatchBulk.Upload', 'action', 'bd_upload');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbe110-0d39-11e6-8666-00ffbc73cbd1', 'Dashboard.Transaction', 'page', 'dashboard');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbe167-0d39-11e6-8666-00ffbc73cbd1', 'Dashboard.Transaction.AdvancedSearch', 'action', 'advancedSearch');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbe1be-0d39-11e6-8666-00ffbc73cbd1', 'Dashboard.Transaction.DownloadExcelUsageReport', 'action', 'TransactionDashboard_downldusgrprt');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbe21d-0d39-11e6-8666-00ffbc73cbd1', 'Dashboard.Transaction.DownloadIOExcel', 'action', 'TransactionDashboard_exprtForRerun');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbe278-0d39-11e6-8666-00ffbc73cbd1', 'Dashboard.Transaction.DownloadIOJson', 'action', 'TransactionDashboard_search');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbd448-0d39-11e6-8666-00ffbc73cbd1', 'Dashboard.Transaction.DownloadModelIO', 'action', 'modelIoDownload');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbd4d2-0d39-11e6-8666-00ffbc73cbd1', 'Dashboard.Transaction.DownloadReport', 'action', 'reportGeneration');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbd54c-0d39-11e6-8666-00ffbc73cbd1', 'Dashboard.Transaction.DownloadTenantIO', 'action', 'tenantIoDownload');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbd5b3-0d39-11e6-8666-00ffbc73cbd1', 'Dashboard.Transaction.PayloadField', 'action', 'payloadField');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbd61a-0d39-11e6-8666-00ffbc73cbd1', 'Dashboard.Transaction.Re-run', 'action', 'testBedRedirect');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbd6cb-0d39-11e6-8666-00ffbc73cbd1', 'Lookup.Add', 'page', 'syndicateDataCrud');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbd72e-0d39-11e6-8666-00ffbc73cbd1', 'Lookup.Manage', 'page', 'modelAssumptionList');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbd789-0d39-11e6-8666-00ffbc73cbd1', 'Lookup.Manage.Add', 'action', 'add_vinc');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbd7e0-0d39-11e6-8666-00ffbc73cbd1', 'Lookup.Manage.DataDownload', 'action', 'downloadVersion');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbd836-0d39-11e6-8666-00ffbc73cbd1', 'Lookup.Manage.DefinitionDownload', 'action', 'downloadDefinition');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbd908-0d39-11e6-8666-00ffbc73cbd1', 'Lookup.Manage.Delete', 'action', 'deleteVersion');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbd96a-0d39-11e6-8666-00ffbc73cbd1', 'Lookup.Manage.Edit', 'action', 'editVersion');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbd9c1-0d39-11e6-8666-00ffbc73cbd1', 'Model.Add', 'page', 'modelPublish');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbda1c-0d39-11e6-8666-00ffbc73cbd1', 'Model.Manage', 'page', 'umgVersionView');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbda73-0d39-11e6-8666-00ffbc73cbd1', 'Model.Manage.AddReportTemplate', 'action', 'vl_uploadTemplate');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbdace-0d39-11e6-8666-00ffbc73cbd1', 'Model.Manage.Deactivate', 'action', 'vl_deactivate');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbdb30-0d39-11e6-8666-00ffbc73cbd1', 'Model.Manage.Delete', 'action', 'vl_deleteVersion');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbdb8b-0d39-11e6-8666-00ffbc73cbd1', 'Model.Manage.EmailPublishApproval', 'action', 'vl_sendPublishApproval');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbdbe2-0d39-11e6-8666-00ffbc73cbd1', 'Model.Manage.ExcelDownload', 'action', 'vl_excelDownload');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbdc39-0d39-11e6-8666-00ffbc73cbd1', 'Model.Manage.ExportVersion', 'action', 'vl_exportVersion');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbdcf2-0d39-11e6-8666-00ffbc73cbd1', 'Model.Manage.ExportVersionAPI', 'action', 'vl_exportVersnAPI');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbdd49-0d39-11e6-8666-00ffbc73cbd1', 'Model.Manage.Publish', 'action', 'vl_publish');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbdda0-0d39-11e6-8666-00ffbc73cbd1', 'Model.Manage.Test', 'action', 'vl_test');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbddf7-0d39-11e6-8666-00ffbc73cbd1', 'Model.Manage.UpdateMapping', 'action', 'vl_updateMapping');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbde56-0d39-11e6-8666-00ffbc73cbd1', 'Model.Manage.VersionMetric', 'action', 'vl_versionMetric');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbdeac-0d39-11e6-8666-00ffbc73cbd1', 'Model.Manage.View', 'action', 'vl_view');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('9d0ee3e9-1126-11e6-b9ad-00059a3c7a00', 'Model.Manage.View.DownloadIODef', 'action', 'mv_downloadModelDefinition_div');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('ac8b5c16-1127-11e6-b9ad-00059a3c7a00', 'Model.Manage.View.DownloadManifest', 'action', 'mv_downloadModelManifest_div');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('bb866b13-1127-11e6-b9ad-00059a3c7a00', 'Model.Manage.View.DownloadModelPackage', 'action', 'mv_downloadJar_div');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('1f9461d3-1127-11e6-b9ad-00059a3c7a00', 'Model.Manage.View.DownloadReleaseNotes', 'action', 'mv_downloadModelDoc_div');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('0b3f9faf-1128-11e6-b9ad-00059a3c7a00', 'Model.Manage.View.DownloadReportTemplate', 'action', 'mv_downloadReportTemplate_div');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbdf03-0d39-11e6-8666-00ffbc73cbd1', 'SupportLib.Add', 'page', 'addPackage');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbdf5a-0d39-11e6-8666-00ffbc73cbd1', 'SupportLib.Manage', 'page', 'listPackages');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('f4dbdfb1-0d39-11e6-8666-00ffbc73cbd1', 'SupportLib.Manage.DownloadPackages', 'action', 'pl_dwnVer');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('8fcbc486-2266-11e6-95fd-00ffbc73cbd1', 'Notifications.Add', 'page', 'notificationAdd');
INSERT INTO `PERMISSIONS` (`Id`, `permission`, `permission_type`, `ui_element_id`) VALUES ('dcd84356-2266-11e6-95fd-00ffbc73cbd1', 'Notifications.Manage', 'page', 'notificationManage');

INSERT INTO `ROLES` (`ROLE`, `Id`) VALUES ('ROLE_SUPER_ADMIN', '028f1293-cbc5-40b9-beba-c53929e6ac33');
INSERT INTO `ROLES` (`ROLE`, `Id`) VALUES ('ROLE_ADMIN', '87f01f20-e912-4549-80ba-93fec1b4d756');
INSERT INTO `ROLES` (`ROLE`, `Id`) VALUES ('ROLE_MODELER', 'f298ca03-23f5-11e6-a547-00ffde411c75');
INSERT INTO `ROLES` (`ROLE`, `Id`) VALUES ('ROLE_TENANT_USER', '03245535-23f6-11e6-a547-00ffde411c75');

CALL insert_default_privileges();

DROP PROCEDURE insert_default_privileges;

SET autocommit=0;


INSERT INTO `SYSTEM_KEY` (`ID`, `SYSTEM_KEY`, `KEY_TYPE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES (UUID(),'BULK_ENABLED','TENANT','SYSTEM',UNIX_TIMESTAMP(),'SYSTEM',UNIX_TIMESTAMP());

INSERT INTO `SYSTEM_KEY` (`ID`, `SYSTEM_KEY`, `KEY_TYPE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES (UUID(),'EMAIL_NOTIFICATIONS_ENABLED','TENANT','SYSTEM',UNIX_TIMESTAMP(),'SYSTEM',UNIX_TIMESTAMP());

DELETE FROM TENANT_CONFIG WHERE SYSTEM_KEY_ID = ( SELECT ID FROM SYSTEM_KEY WHERE SYSTEM_KEY = "SOME");

DELETE FROM SYSTEM_KEY WHERE SYSTEM_KEY = "SOME" ;



/* set delimiter */

DELIMITER $$

/* remove procedure insert_authToken... */
DROP PROCEDURE IF EXISTS insert_authToken $$

/* create procedure insert_default_privileges*/ 
CREATE PROCEDURE insert_authToken ()
BEGIN
DECLARE authToken_length INT;
DECLARE x INT DEFAULT 0;
DECLARE tenantId VARCHAR(100);

/* drop temp table  TEMP_TABLE_TENANT */ 
DROP TABLE IF EXISTS TEMP_TABLE_AUTHTOKEN;
/* create temp table  TEMP_TABLE_TENANT */
create temporary table if not exists TEMP_TABLE_AUTHTOKEN
select 
	TENANT_TEMP.AUTH_TOKEN as 'AUTH_TOKEN',TENANT_TEMP.ID AS 'TENANT_ID'
from TENANT as TENANT_TEMP;

SELECT * FROM TEMP_TABLE_AUTHTOKEN;


SELECT COUNT(*) FROM TEMP_TABLE_AUTHTOKEN INTO authToken_length;

SELECT authToken_length;

WHILE x < authToken_length DO
SELECT TENANT_ID FROM TEMP_TABLE_AUTHTOKEN LIMIT x,1 INTO tenantId ;

INSERT INTO AUTHTOKEN(`ID`, `TENANT_ID`, `AUTH_CODE`,`ACTIVE_FROM`,`ACTIVE_UNTIL`,`STATUS`,`COMMENT`,`CREATED_ON`,`CREATED_BY`,`LAST_UPDATED_BY`,
`LAST_UPDATED_ON`) VALUES(uuid(),(select TTU.ID  from TENANT AS TTU WHERE tenantId=TTU.ID),(select TTU.AUTH_TOKEN  from TENANT AS TTU WHERE tenantId=TTU.ID),1464331168000,1495780768000,'Active','Tenant Onboarded',(select UNIX_TIMESTAMP()),'SYSTEM','SYSTEM',(select UNIX_TIMESTAMP()));
SET x = x+1;
END WHILE;

DROP TABLE TEMP_TABLE_AUTHTOKEN;

/*Successfully dropped temp tables*/

SELECT AUTH_CODE,TENANT_ID FROM AUTHTOKEN;

END $$

DELIMITER ;

CALL insert_authToken();

DROP PROCEDURE insert_authToken;

ALTER TABLE `TENANT`	DROP COLUMN `AUTH_TOKEN`;

INSERT INTO SYSTEM_PARAMETER (`ID`, `SYS_KEY`, `DESCRIPTION`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
	VALUES ('af5e85e1-47k5-4ahc-9e6bdda18d88x', 'SMTP_HOST_NAME', 'SMTP Server Host Name', 'mail02.svc.mia.vz.altidev.net', 'Y', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);
	
INSERT INTO SYSTEM_PARAMETER (`ID`, `SYS_KEY`, `DESCRIPTION`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
	VALUES ('af5e85e1-47k5-4ahc-9e6bdda18d99x', 'SMTP_SERVER', 'SMTP Server', 'mail02.svc.mia.vz.altidev.net', 'Y', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);

INSERT INTO SYSTEM_PARAMETER_AUDIT (`ID`, `SYS_KEY`, `DESCRIPTION`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
	VALUES ('af5e85e1-47k5-4ahc-9e6bdda1888x', 'SMTP_HOST_NAME', 'SMTP Server Host Name', 'mail02.svc.mia.vz.altidev.net', 'Y', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);

INSERT INTO SYSTEM_PARAMETER_AUDIT (`ID`, `SYS_KEY`, `DESCRIPTION`, `SYS_VALUE`, `IS_ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
	VALUES ('af5e85e1-47k5-4ahc-9e6bdda1899x', 'SMTP_SERVER', 'SMTP Server', 'mail02.svc.mia.vz.altidev.net', 'Y', @CREATED_BY, @CREATED_ON, @CREATED_BY, @CREATED_ON);	

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