package com.ca.umg.business.pooling.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModeletPoolingDetails {

    private List<String> transactionTypes;
    private List<String> transactionModes;
    private List<String> channels;
    // private Set<String> tenants;
    private Map<String, String> tenants;
    private Set<String> environments;
    private Set<String> executionEnvironments;
    private Map<String, Set<String>> modelNamesByTenant;
    private Map<String, Map<String, Map<String, Map<String, Set<String>>>>> modelNamesByTenantAndEnv;
    private String modeletServerCount;
    private String matlabModeletsCount;
    private String rModeletsCount;

    public List<String> getTransactionTypes() {
        return transactionTypes;
    }

    public void setTransactionTypes(List<String> transactionTypes) {
        this.transactionTypes = transactionTypes;
    }

    public List<String> getTransactionModes() {
        return transactionModes;
    }

    public void setTransactionModes(List<String> transactionModes) {
        this.transactionModes = transactionModes;
    }

    /*
     * public Set<String> getTenants() { SortedSet<String> tanentSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
     * tanentSet = (SortedSet<String>) tenants; return tanentSet; }
     * 
     * public void setTenants(Set<String> tenants) { this.tenants = tenants; }
     */

    public Set<String> getEnvironments() {
        return environments;
    }

    public void setEnvironments(Set<String> environments) {
        this.environments = environments;
    }

    public Map<String, Set<String>> getModelNamesByTenant() {
        return modelNamesByTenant;
    }

    public void setModelNamesByTenant(Map<String, Set<String>> modelNamesByTenant) {
        this.modelNamesByTenant = modelNamesByTenant;
    }

    public Map<String, Map<String, Map<String, Map<String, Set<String>>>>> getModelNamesByTenantAndEnv() {
        return modelNamesByTenantAndEnv;
    }

    public void setModelNamesByTenantAndEnv(
            Map<String, Map<String, Map<String, Map<String, Set<String>>>>> modelNamesByTenantAndEnv) {
        this.modelNamesByTenantAndEnv = modelNamesByTenantAndEnv;
    }

    public String getModeletServerCount() {
        return modeletServerCount;
    }

    public void setModeletServerCount(String modeletServerCount) {
        this.modeletServerCount = modeletServerCount;
    }

    public String getMatlabModeletsCount() {
        return matlabModeletsCount;
    }

    public void setMatlabModeletsCount(String matlabModeletsCount) {
        this.matlabModeletsCount = matlabModeletsCount;
    }

    public String getrModeletsCount() {
        return rModeletsCount;
    }

    public void setrModeletsCount(String rModeletsCount) {
        this.rModeletsCount = rModeletsCount;
    }

    public Map<String, String> getTenants() {
        return tenants;
    }

    public void setTenants(Map<String, String> tenants) {
        this.tenants = tenants;
    }

    public List<String> getChannels() {
        return channels;
    }

    public void setChannels(List<String> channels) {
        this.channels = channels;
    }

    public Set<String> getExecutionEnvironments() {
        return executionEnvironments;
    }

    public void setExecutionEnvironments(Set<String> executionEnvironments) {
        this.executionEnvironments = executionEnvironments;
    }

}