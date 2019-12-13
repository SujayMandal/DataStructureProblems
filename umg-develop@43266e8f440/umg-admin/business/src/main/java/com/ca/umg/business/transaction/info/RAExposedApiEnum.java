package com.ca.umg.business.transaction.info;

public enum RAExposedApiEnum {

	MODEL_NAME("modelName"), TRAN_ID("transactionId"), UMG_TRAN_ID("umgTransactionId"),
	RUN_AS_OF_DATE_FROM("runAsOfDateFrom"), RUN_AS_OF_DATE_TO("runAsOfDateTo"),MODEL_VERSION("version"),
	TRANSACTION_TYPE("transactionType"), STATUS( "status"), BATCHID("batchId"),
	OFFSET("offset"), LIMIT("limit"),  METADATAONLY("metadataOnly"),
	SORT("sort"), PAYLOADFIELDS( "payloadFields"), EXECUTION_GROUP("executionGroup");

    private String exposedApiParam;

    private RAExposedApiEnum(String exposedApiParam) {
        this.exposedApiParam = exposedApiParam;
    }

	public String getExposedApiParam() {
        return exposedApiParam;
    }
}