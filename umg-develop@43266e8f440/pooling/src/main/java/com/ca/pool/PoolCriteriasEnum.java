package com.ca.pool;

public enum PoolCriteriasEnum {

    MODEL("MODEL", "modelName"),

    MODEL_VERSION("MODEL_VERSION", "modelVersion"),

    TENANT("TENANT", "tenantCode"),

    EXECUTION_LANGUAGE("EXECUTION_LANGUAGE", "executionLanguage"),

    EXECUTION_LANGUAGE_VERSION("EXECUTION_LANGUAGE_VERSION", "executionLanguageVersion"),

    TRANSACTION_TYPE("TRANSACTION_TYPE", "transactionRequestType"),

    TRANSACTION_MODE("TRANSACTION_MODE", "transactionRequestMode"),

    CHANNEL("CHANNEL", "transactionRequestChannel"),

    EXECUTION_ENVIRONMENT("EXECUTION_ENVIRONMENT", "executionEnvironment");

    private String criteriaNameInDb;
    // this is the corresponding declared field in transactionCriteria object
    private String criteriaFeildInObject;

    private PoolCriteriasEnum(String criteriaNameInDb, String criteriaPropInObject) {
        this.criteriaNameInDb = criteriaNameInDb;
        this.criteriaFeildInObject = criteriaPropInObject;
    }

    public String getCriteriaNameInDb() {
        return criteriaNameInDb;
    }

    public String getCriteriaPropInObject() {
        return criteriaFeildInObject;
    }

    public static String getCriteriaFieldForNameInDb(String nameInDb) {
        String fieldInObject = null;
        for (PoolCriteriasEnum poolCriteria : PoolCriteriasEnum.values()) {
            if (nameInDb.equalsIgnoreCase(poolCriteria.getCriteriaNameInDb())) {
                fieldInObject = poolCriteria.getCriteriaPropInObject();
                break;
            }
        }
        return fieldInObject;
    }

}