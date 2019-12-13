/*
 * MessageVariables.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.util;

import org.springframework.integration.Message;

/**
 * Variables used in {@link Message}. While message gets processed through different components it get enriched with these
 * variables.
 **/
public final class MessageVariables {
    /* Spring integration message specific variables */
    public static final String MESSAGE_ID = "id";
    /* Variables used for enrichment */
    public static final String TENANT_REQUEST = "tenantRequest";
    public static final String TENANT_RESPONSE = "tenantResponse";
    public static final String TENANT_REQUEST_HEADER = "tenantRequestHeader";
    public static final String MODEL_REQUEST_STRING = "modelRequest";
    public static final String MODEL_REQUEST = "modelRequestString";
    public static final String ME2_RESPONSE = "me2Response";
    public static final String MODEL_RESPONSE = "modelResponse";
    public static final String MODEL_RESPONSE_TRANSLATED = "modelResponseTranslated";
    public static final String REQUEST = "request";
    public static final String RESULT = "result";
    public static final String VALIDATIONS = "validations";
    /* Variables present in request/response data */
    public static final String HEADER = "header";
    public static final String DATA = "data";
    public static final String MODEL_NAME = "modelName";
    public static final String MAJOR_VERSION = "majorVersion";
    public static final String MINOR_VERSION = "minorVersion";
    public static final String DATE = "date";
    public static final String CORRELATION_ID = "ExcelCorrelationID";
    public static final String TRANSACTION_ID = "transactionId";
    public static final String TRANSACTION_CRITERIA = "transactionCriteria";
    public static final String AUTH_TOKEN = "authToken";
    public static final String UMG_TRANSACTION_ID = "umgTransactionId";
    public static final String TESTDATE_MILLIS = "TESTDATE_MILLIS";
    public static final String TESTDATE = "TESTDATE";
    public static final String DATE_USED = "DATE_USED";
    public static final String BATCH_ID = "batchId";
    public static final String ADD_ON_VALIDATION = "addOnValidation";
    /* Variables for reading from TID,Mapping,MID */
    public static final String METADATA = "metadata";
    public static final String MID_INPUT = "midInput";
    public static final String MID_OUTPUT = "midOutput";
    public static final String PARTIALS = "partials";
    public static final String MAPPING_IN = "mappingParam";
    public static final String MAPPING_OUT = "mappedTo";
    public static final String TRAN_ONLINE = "Online";
    public static final String TRAN_BULK = "Bulk";
    public static final String TRAN_BATCH = "batch";
    public static final String TRAN_MODE = "transactionMode";
    public static final String TENANT_INPUT = "tenantInput";
    public static final String TENANT_OUTPUT = "tenantOutput";
    public static final String MODEL_INPUT = "modelInput";
    public static final String MODEL_OUTPUT = "modelOutput";
    public static final String ALTERNATE_STORAGE = "alternateStorage";

    // added for exposed to tenant
    public static final String EXPOSED_TO_TNT = "exposedToTenant";

    /* Variables for ModelRequest */
    public static final String HEADER_INFO = "headerInfo";
    public static final String PAYLOAD = "payload";
    /* Variables for ModelResponse */
    public static final String RESPONSE_HEADER_INFO = "responseHeaderInfo";
    /* UMG-5015 Variables for R command execution log */
    public static final String EXECUTION_COMMAND = "executionCommand";
    public static final String EXECUTION_RESPONSE = "executionResponse";
    public static final String EXECUTION_LOGS = "executionLogs";
    /* UMG-5016 */
    public static final String FLATENED_NAME = "IO-FieldName";
    public static final String MODELOUTPUT_FIELDNAME = "ModelOutput-FieldName";
    public static final String MODELOUTPUT_SEQUENCEPATH = "ModelOutput-SequencePath";
    public static final String EXPECTED_DATATYPE = "IO-ExpectedDataType";
    public static final String MODELOUTPUT_DATATYPE = "ModelOutput-DataType";
    public static final String MODELOUTPUT_NATIVEDATATYPE = "ModelOutput-NativeDataType";
    public static final String MODELOUTPUT_VALUE = "ModelOutput-Value";
    public static final String DIMENSIONS = "IO-Dimensions";
    /* Common error variables */
    public static final String ERROR = "error";
    public static final String ERROR_CODE = "errorCode";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String SUCCESS = "success";
    public static final String DETAILED_ERROR_MESSAGE = "detailedErrorMessage";

    public static final String ME2_ERROR_MESSAGE = "message";
    public static final String RESPONSE = "response";

    /* Common variables */
    public static final String VALUE = "value";

    // added for bulk transaction umg-5173
    public static final String FILE_NAME = "fileName";
    public static final String BULK_HTTP = "bulk-http";
    public static final String BULK = "bulk";
    public static final String BULK_FILE = "bulk-file";
    public static final String INPUT_FOLDER = "input";
    public static final String DOCUMENTS_FOLDER = "failed-documents";
    public static final String ARCHIVE_FOLDER = "archive";
    public static final String INPROGRESS_FOLDER = "inprogress";
    public static final String OUTPUT_FOLDER = "output";
    public static final String FILE_NAME_HEADER = "FILE_NAME";
    public static final String SAN_PATH = "SAN_PATH";
    public static final String FAILURE = "FAILURE";
    public static final String ERROR_FILE_STR = "Error";
    public static final String TENANT_TRAN_COUNT = "tenantTranCount";
    // public static final String OUTPUT_FILE_SUB = "Output";

    public static final String MODEL_EXECUTION_TIME = "modelExecutionTime";
    public static final String MODELET_EXECUTION_TIME = "modeletExecutionTime";
    public static final String ME2_EXECUTION_TIME = "me2ExecutionTime";
    public static final String ME2_WAITING_TIME = "me2WaitingTime";

    public static final String RNTM_CALL_START = "runtimeCallStart";
    public static final String RNTM_CALL_END = "runtimeCallEnd";
    public static final String MODEL_CALL_START = "modelCallStart";
    public static final String MODEL_CALL_END = "modelCallEnd";

    public static final String MODELET_SERVER_HOST = "host";
    public static final String MODELET_SERVER_PORT = "port";
    public static final String MODELET_SERVER_MEMBER_HOST = "memberHost";
    public static final String MODELET_SERVER_MEMBER_PORT = "memberPort";
    public static final String MODELET_POOL_NAME = "poolName";
    public static final String MODELET_POOL_CRITERIA = "poolCriteria";
    public static final String MODELET_SERVER_TYPE = "serverType";
    public static final String MODELET_SERVER_CONTEX_TPATH = "contextPath";

    // added this to fix umg-4251 to set versionCreationTest flag to true
    // if it is test transaction during version creation else the flag will be false
    public static final String VERSION_CREATION_TEST = "versionCreationTest";

    // added to fix UMG-4500 Additional variables in Transaction header
    public static final String USER = "user";
    public static final String TRANSACTION_TYPE = "transactionType";

    // added to fix UMG-4697 Additional variables in Transaction header
    public static final String EXECUTION_GROUP = "executionGroup";
    public static final String DEFAULT_EXECUTION_GROUP = "Modeled";

    public static final String MODEL_LIBRARY_VERSION_NAME = "modelLibraryVersionName";
    public static final String TEST = "test";

    public static final String METRICS = "METRICS";

    // Notification RSE flag to send notification in case of runtime system exception
    public static final String NOTIFICATION_RSE_FLAG = "NOTIFICATION_RSE_FLAG";

    public static final String FREE_MEMORY = "freeMemorey";

    public static final String CPU_USAGE = "cpuUsage";

    public static final String FREE_MEMORY_AT_START = "freeMemoreyAtStart";

    public static final String CPU_USAGE_AT_START = "cpuUsageAtStart";

    public static final String NO_OF_ATTEMPTS = "NO_OF_ATTEMPTS"; 
  

    public static final String DATA_TYPE = "dataType";
    public static final String NATIVE_DATA_TYPE = "nativeDataType";
    public static final String COLLECTION = "collection";
    public static final String SEQUENCE = "sequence";
    public static final String COLUMN_NAMES = "colnames";
    public static final String FIELD_NAME = "modelParameterName";
    public static final String PAYLOAD_STORAGE = "payloadStorage";  
    public static final String CHANNEL = "channel";
    public static final String STORE_RLOGS = "storeRLogs";
    
    public static final String CLIENT_ID = "clientID";
    private MessageVariables() {
    }

    public enum ChannelType {

        HTTP("HTTP"),

        FILE("FILE"),

        ANY("ANY");

        private String channel;

        ChannelType(String channel) {
            this.channel = channel;
        }

        public String getChannel() {
            return channel;
        }

    }

}