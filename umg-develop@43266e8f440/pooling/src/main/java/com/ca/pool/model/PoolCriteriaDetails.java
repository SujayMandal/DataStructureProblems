package com.ca.pool.model;

import static com.ca.framework.core.constants.PoolConstants.CHANNEL;
import static com.ca.framework.core.constants.PoolConstants.EXECUTION_ENVIRONMENT;
import static com.ca.framework.core.constants.PoolConstants.EXECUTION_LANGUAGE;
import static com.ca.framework.core.constants.PoolConstants.MODEL;
import static com.ca.framework.core.constants.PoolConstants.MODEL_VERSION;
import static com.ca.framework.core.constants.PoolConstants.TENANT;
import static com.ca.framework.core.constants.PoolConstants.TRANSACTION_MODE;
import static com.ca.framework.core.constants.PoolConstants.TRANSACTION_TYPE;
import static com.ca.pool.Channel.getChannelByType;
import static com.ca.pool.TransactionMode.getTransactionModeByMode;
import static com.ca.pool.TransactionType.getTransactionTypeByType;
import static com.ca.pool.util.PoolCriteriaUtil.getCriteraValues1;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ca.framework.core.constants.PoolConstants;
import com.ca.pool.util.PoolCriteriaUtil;

public class PoolCriteriaDetails {

    private String tenant;
    private String execLangVersion;  
    private String transactionType;
    private String transactionMode;
    private String channel;
    private String model;
    private String modelName;
    private String modelVersion;
    private String executionLanguage;
    private String executionEnvironment;

    public PoolCriteriaDetails() {

    }

    public PoolCriteriaDetails(final String poolCriteria) {
        final Map<String, String> criteriaValueMap = new HashMap<String, String>();
        getCriteraValues1(poolCriteria, criteriaValueMap);
        this.tenant = criteriaValueMap.get(TENANT.toLowerCase());
        this.modelName = criteriaValueMap.get(MODEL.toLowerCase());
        if (PoolCriteriaUtil.isModelAny(this.modelName)) {
            this.modelName = PoolConstants.ANY;
        }
        this.modelVersion = criteriaValueMap.get(MODEL_VERSION.toLowerCase());
        if (PoolCriteriaUtil.isModelAny(this.modelVersion)) {
            this.modelVersion = PoolConstants.ANY;
        }
        this.executionLanguage = ExecutionLanguage.getEnvironment(criteriaValueMap.get(EXECUTION_LANGUAGE.toLowerCase()))
                .getValue();
//        this.executionLanguageVersion = criteriaValueMap.get(EXECUTION_LANGUAGE_VERSION.toLowerCase());
        this.transactionType = getTransactionTypeByType(criteriaValueMap.get(TRANSACTION_TYPE.toLowerCase())).getType();
        this.transactionMode = getTransactionModeByMode(criteriaValueMap.get(TRANSACTION_MODE.toLowerCase())).getMode();
        this.channel = getChannelByType(criteriaValueMap.get(CHANNEL.toLowerCase())).getChannel();
     //   this.execLangVersion = PoolCriteriaUtil.getEnvironmentWithVersion(executionLanguage, executionLanguageVersion);
        this.model = PoolCriteriaUtil.getModelNameWithVersion(modelName, modelVersion);
        this.executionEnvironment = ExecutionEnvironment.getEnvironment(criteriaValueMap.get(StringUtils.lowerCase(EXECUTION_ENVIRONMENT)))
        		.getEnvironment();
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getExecLangVersion() {
        return execLangVersion;
    }

    public void setExecLangVersion(String execLangVersion) {
        this.execLangVersion = execLangVersion;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionMode() {
        return transactionMode;
    }

    public void setTransactionMode(String transactionMode) {
        this.transactionMode = transactionMode;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getExecutionLanguage() {
        return executionLanguage;
    }

    public void setExecutionLanguage(String executionLanguage) {
        this.executionLanguage = executionLanguage;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getExecutionEnvironment() {
        return executionEnvironment;
    }

    public void setExecutionEnvironment(String executionEnvironment) {
        this.executionEnvironment = executionEnvironment;
    }

}