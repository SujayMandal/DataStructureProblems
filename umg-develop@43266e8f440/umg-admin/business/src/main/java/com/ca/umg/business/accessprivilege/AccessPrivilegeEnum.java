package com.ca.umg.business.accessprivilege;

public enum AccessPrivilegeEnum {

    DASHBOARD_TRAN("Dashboard.Transaction", "dashboardTransaction"),
    DASHBOARD_TRAN_DWNLD_TNTIO("Dashboard.Transaction.DownloadTenantIO", "dashboardTransactionDownloadTenantIO"),
    DASHBOARD_TRAN_DWNLD_MODELIO("Dashboard.Transaction.DownloadModelIO", "dashboardTransactionDownloadModelIO"),
    DASHBOARD_TRAN_DWNLD_IOJSON("Dashboard.Transaction.DownloadIOJson", "dashboardTransactionDownloadIOJson"),
    DASHBOARD_TRAN_RERUN("Dashboard.Transaction.Re-run", "dashboardTransactionRerun"),
    DASHBOARD_TRAN_DWLD_IOEXCEL("Dashboard.Transaction.DownloadIOExcel", "dashboardTransactionDownloadIOExcel"),
    DASHBOARD_TRAN_DWLD_EXCEL_USGRPRT("Dashboard.Transaction.DownloadExcelUsageReport", "dashboardTransactionDownloadExcelUsageReport"),
    DASHBOARD_TRAN_DWLD_RPRT("Dashboard.Transaction.DownloadReport", "dashboardTransactionDownloadReport"),
    DASHBOARD_BATCHBULK("Dashboard.BatchBulk", "dashboardBatchBulk"),
    DASHBOARD_BATCHBULK_UPLOAD("Dashboard.BatchBulk.Upload", "dashboardBatchBulkUpload"),
    DASHBOARD_BATCHBULK_DWLDIO("Dashboard.BatchBulk.DownloadIO", "dashboardBatchBulkDownloadIO"),
    DASHBOARD_BATCHBULK_TERMINATE("Dashboard.BatchBulk.TerminateBatch", "dashboardBatchBulkTerminateBatch"),
    DASHBOARD_TRAN_PAYLOAD_FIELD("Dashboard.Transaction.PayloadField","payloadField"),
    DASHBOARD_TRAN_ADV_SEARCH("Dashboard.Transaction.AdvancedSearch","advancedSearch"),    
    MODEL_ADD("Model.Add", "modelAdd"),
    MODEL_MANAGE("Model.Manage", "modelManage"),
    MODEL_MANAGE_VIEW("Model.Manage.View", "modelManageView"),
    MODEL_MANAGE_VIEW_DOWNLOADMANIFEST("Model.Manage.View.DownloadManifest", "modelManageViewDownloadManifest"),
    MODEL_MANAGE_VIEW_DOWNLOADIODEF("Model.Manage.View.DownloadIODef", "modelManageViewDownloadIODef"),
    MODEL_MANAGE_VIEW_DOWNLOADRELEASENOTES("Model.Manage.View.DownloadReleaseNotes", "modelManageViewDownloadReleaseNotes"),
    MODEL_MANAGE_VIEW_DOWNLOADMODELPACKAGE("Model.Manage.View.DownloadModelPackage", "modelManageViewDownloadModelPackage"),
    MODEL_MANAGE_VIEW_DOWNLOADREPORTTEMPLATE("Model.Manage.View.DownloadReportTemplate", "modelManageViewDownloadReportTemplate"),
    MODEL_MANAGE_TEST("Model.Manage.Test", "modelManageTest"),
    MODEL_MANAGE_UPDATEMAPPING("Model.Manage.UpdateMapping", "modelManageUpdateMapping"),
    MODEL_MANAGE_PUBLISH("Model.Manage.Publish", "modelManagePublish"),
    MODEL_MANAGE_DEACTIVATE("Model.Manage.Deactivate", "modelManageDeactivate"),
    MODEL_MANAGE_EXPORTVERSION("Model.Manage.ExportVersion", "modelManageExportVersion"),
    MODEL_MANAGE_EXPORTVERSION_API("Model.Manage.ExportVersionAPI", "modelManageExportVersionAPI"),
    MODEL_MANAGE_EXCEL_DWLD("Model.Manage.ExcelDownload", "modelManageExcelDownload"),
    MODEL_MANAGE_DELETE("Model.Manage.Delete", "modelManageDelete"),
    MODEL_MANAGE_VERSION_METRIC("Model.Manage.VersionMetric", "modelManageVersionMetric"),
    MODEL_MANAGE_ADD_REPORT_TEMPLT("Model.Manage.AddReportTemplate", "modelManageAddReportTemplate"),
    MODEL_MANAGE_EMAILPUBLISHAPPROVAL("Model.Manage.EmailPublishApproval","modelManageEmailPublishApproval"),
    LOOKUP_ADD("Lookup.Add", "lookupAdd"),
    LOOKUP_MANAGE("Lookup.Manage", "lookupManage"),
    LOOKUP_MANAGE_ADD("Lookup.Manage.Add", "lookupManageAdd"),
    LOOKUP_MANAGE_EDIT("Lookup.Manage.Edit", "lookupManageEdit"),
    LOOKUP_MANAGE_DELETE("Lookup.Manage.Delete", "lookupManageDelete"),
    LOOKUP_MANAGE_DATA_DWLD("Lookup.Manage.DataDownload", "lookupManageDataDownload"),
    LOOKUP_MANAGE_DEFN_DWLD("Lookup.Manage.DefinitionDownload", "lookupManageDefinitionDownload"),
    SUPPORT_LIB_ADD("SupportLib.Add", "supportLibAdd"),
    SUPPORT_LIB_MANAGE("SupportLib.Manage", "supportLibManage"),
    SUPPORT_LIB_MANAGE_DWNL_PKGS("SupportLib.Manage.DownloadPackages", "supportLibManageDownloadPackages"),
    NOTIFICATION_ADD("Notifications.Add","notificationAdd"),
    NOTIFICATION_MANAGE("Notifications.Manage","notificationManage");
    
    
    private String privilegeInDb;
    //this is the corresponding declared field in AccessPrivilege object 
    private String privilegeInObject;

    private AccessPrivilegeEnum(String privilegeInDb, String privilegeInObject) {
        this.privilegeInDb = privilegeInDb;
        this.privilegeInObject = privilegeInObject;
    }
	
	public String getPrivilegeInDb() {
        return privilegeInDb;
    }

    public String getPrivilegeInObject() {
        return privilegeInObject;
    }

    /**
     * gets the field name in {@link AccessPrivilege} for the passed value from db
     * @param valueFromDb
     * @return
     */
    public static String getPrivilegeFieldForValueInDb(String valueFromDb) {
        String fieldInObject = null;
        for (AccessPrivilegeEnum privilegeCriteria : AccessPrivilegeEnum.values()) {
            if (valueFromDb.equalsIgnoreCase(privilegeCriteria.getPrivilegeInDb())) {
            	fieldInObject = privilegeCriteria.getPrivilegeInObject();
            	break;
            }
        }
        return fieldInObject;
    }
    
    /**
     * gets the field name in {@link AccessPrivilege} for the passed value from db
     * @param valueFromDb
     * @return
     */
    public static String getPrivilegeField(String valueFromDb) {
        String fieldInObject = null;
        for (AccessPrivilegeEnum privilegeCriteria : AccessPrivilegeEnum.values()) {
            if (valueFromDb.equalsIgnoreCase(privilegeCriteria.getPrivilegeInDb())) {
            	fieldInObject =	privilegeCriteria.getPrivilegeInDb();
            	break;
            }
        }
        return fieldInObject;
    }

}