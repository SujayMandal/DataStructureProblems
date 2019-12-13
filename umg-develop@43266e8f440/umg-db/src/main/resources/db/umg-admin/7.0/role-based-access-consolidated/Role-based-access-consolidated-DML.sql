use umg_admin;

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

commit;

