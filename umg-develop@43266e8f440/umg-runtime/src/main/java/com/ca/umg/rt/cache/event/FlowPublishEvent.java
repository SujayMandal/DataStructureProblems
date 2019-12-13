package com.ca.umg.rt.cache.event;

import java.io.Serializable;

import com.ca.umg.rt.core.deployment.info.DeploymentDescriptor;

public class FlowPublishEvent implements Serializable{
    private static final long serialVersionUID = 5454686393029209587L;
    public static final String DEPLOY="deploy";
    public static final String UNDEPLOY="undeploy";
    private String tenantCode;
    private DeploymentDescriptor deploymentDescriptor;
    private String event;
    /**
     * @return the tenantCode
     */
    public String getTenantCode() {
        return tenantCode;
    }
    /**
     * @param tenantCode the tenantCode to set
     */
    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }
    
    public String getEvent() {
        return event;
    }
    public void setEvent(String event) {
        this.event = event;
    }
    public DeploymentDescriptor getDeploymentDescriptor() {
        return deploymentDescriptor;
    }
    public void setDeploymentDescriptor(DeploymentDescriptor deploymentDescriptor) {
        this.deploymentDescriptor = deploymentDescriptor;
    }
}
