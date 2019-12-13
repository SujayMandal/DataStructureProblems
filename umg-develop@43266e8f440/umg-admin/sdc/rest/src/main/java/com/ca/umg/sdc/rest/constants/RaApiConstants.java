package com.ca.umg.sdc.rest.constants;

@SuppressWarnings("PMD")
public final class RaApiConstants {

	public static final String CONTROLLER_SUCCESS_MESSAGE = "Success";
	
	public static final String MODEL_NAME = "modelName";
	
	public static final String TRANSACTION_ID = "transactionId";
	
	public static final String UMG_TRANSACTION_ID = "umgTransactionId";
	
	public static final String RUN_AS_OF_DATE = "runAsOfDate";
	
	public static final String RUN_AS_OF_DATE_FROM = "runAsOfDateFrom";
    
    public static final String RUN_AS_OF_DATE_TO = "runAsOfDateTo";
    
    public static final String VERSION = "version";
    
    public static final String TRANSACTION_TYPE = "transactionType";
    
  //added createdBy and execution group for umg-4698
    public static final String USER_NAME = "userName";
    
    public static final String EXECUTION_GROUP = "executionGroup";
    
    public static final String STATUS = "status";
    
    public static final String BATCH_ID = "batchId";
    
  //added for umg-4849 the below 4 variables
    public static final String INCLUDE_TNT_INPUT = "includeTenantInput";
    
    public static final String INCLUDE_TNT_OUTPUT = "includeTenantOutput";
    
    public static final String INCLUDE_TNT_OUTPUT_FIELDS = "tenantOutput.data.";
    
    public static final String INCLUDE_TNT_INPUT_FIELDS = "tenantInput.data.";
    
    public static final String LIMIT = "limit";
    
    public static final String OFFSET = "offset";
    
	public static final String SORT = "sort";
	
	public static final String PAYLOAD_OUTPUT_FIELDS = "payloadOutputFields";
	
	public static final String PAYLOAD_INPUT_FIELDS = "payloadInputFields";
	
	public static final String TRAN_STATUS_PROD = "prod";
	
	public static final String TRAN_STATUS_TEST = "test";
	
	public static final String RA_API_RECORD_LIMIT_METADATA_ONLY = "RA_API_RECORD_LIMIT_METADATA_ONLY";
	
	// added for umg-4849
	public static final String RA_API_RECORD_LIMIT_TENANT_OUT_ONLY = "RA_API_RECORD_LIMIT_TENANT_OUT_ONLY";
	
	public static final String NO_TRANSACTION_RECORDS_FOUND_API = "No records found for the search criteria. Please change your search criteria.";
	
	public static final String REPORT_NAME = "reportName";

	private RaApiConstants() {
	}

}
