package com.ca.framework.core.publishing.status.constants;

public enum PublishingStatus {

	VALIDATING_MANIFEST("1"),
    VALIDATING_CHECKSUM("2"),
    UPLOADING_MODEL_PACKAGE("3"),
    VALIDATING_IO_DEFINITION("4"),
    VALIDATE_REPORT_TEMPLATE("5"),
    OBTAINING_MODELET("6"),
    LOADING_LIBRARIES("7"),
    LOADING_MODEL_PACKAGE("8"),
    EXECUTING_TEST_TRANSACTION("9"),
    TESTING_REPORT("10");
    
    private String status;
    
    public static final String MODELET_FOUND = "MODELET_FOUND";
    public static final String LOAD_LIB = "LOAD_LIB";
    public static final String LOAD_MODEL = "LOAD_MODEL";

    private PublishingStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
