/*
 * DeploymentController.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 *
 *
 * Author : KR Kumar
 */
package com.ca.umg.rt.web.rest.controller;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.util.MessageContainer;
import com.ca.umg.rt.core.deployment.delegate.DeploymentDelegate;
import com.ca.umg.rt.core.deployment.info.DeploymentStatusInfo;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;

/**
 * 
 * @author chandrsa
 * 
 */
@Controller
@RequestMapping("/api/batch")
public class BatchDeploymentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchDeploymentController.class);
    
    @Inject
    private DeploymentDelegate deploymentDelegate;

    /**
     * DOCUMENT ME!
     * 
     * @param deploymentDescriptor
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     **/
    @RequestMapping(value = "/deploy", method = RequestMethod.GET)
    public @ResponseBody
    DeploymentStatusInfo deploy() {
        LOGGER.info("Deploying Batch Processing for tenant {}", RequestContext.getRequestContext().getTenantCode());
        long start = System.currentTimeMillis();
        DeploymentStatusInfo deploymentStatusInfo = new DeploymentStatusInfo();
        try {
            deploymentStatusInfo = deploymentDelegate.deployBatch();
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            deploymentStatusInfo.setErrorCode(e.getCode());
            deploymentStatusInfo.setError(true);
            deploymentStatusInfo.setErrorMessage(e.getLocalizedMessage());
        } catch (Exception ex) {// NOPMD
            LOGGER.error(ex.getLocalizedMessage(), ex);
            deploymentStatusInfo.setErrorCode(RuntimeExceptionCode.RSE000501);
            deploymentStatusInfo.setError(true);
            deploymentStatusInfo.setErrorMessage(ex.getLocalizedMessage());
        }
        long end = System.currentTimeMillis();
        deploymentStatusInfo.setTimeTaken(end - start);
        return deploymentStatusInfo;
    }
    
    
    @RequestMapping(value = "/ftp/deploy", method = RequestMethod.GET)
    public @ResponseBody DeploymentStatusInfo deployFTP(){
    	LOGGER.info("Deploying FTP Processing for tenant {}", RequestContext.getRequestContext().getTenantCode());
    	DeploymentStatusInfo deploymentStatusInfo = new DeploymentStatusInfo();
    	long start = System.currentTimeMillis();
        try {
            deploymentStatusInfo = deploymentDelegate.deployBatchWrapper("ftp");
        } catch (SystemException | BusinessException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
            deploymentStatusInfo.setErrorCode(RuntimeExceptionCode.RSE000501);
            deploymentStatusInfo.setError(true);
            deploymentStatusInfo.setErrorMessage(ex.getLocalizedMessage());
        }
    	long end = System.currentTimeMillis();
        deploymentStatusInfo.setTimeTaken(end - start);
    	return deploymentStatusInfo;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param deploymentDescriptor
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     **/
    @RequestMapping(value = "/undeploy", method = RequestMethod.GET)
    public @ResponseBody
    DeploymentStatusInfo undeploy() {
        LOGGER.info("UnDeploying Batch Processing for tenant {}", RequestContext.getRequestContext().getTenantCode());
        long start = System.currentTimeMillis();
        DeploymentStatusInfo deploymentStatusInfo = new DeploymentStatusInfo();
        try {
            deploymentStatusInfo = deploymentDelegate.undeployBatch();
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            deploymentStatusInfo.setErrorCode(e.getCode());
            deploymentStatusInfo.setError(true);
            deploymentStatusInfo.setErrorMessage(e.getLocalizedMessage());
        } catch (Exception ex) {// NOPMD
            LOGGER.error(ex.getLocalizedMessage(), ex);
            deploymentStatusInfo.setErrorCode(RuntimeExceptionCode.RSE000502);
            deploymentStatusInfo.setError(true);
            deploymentStatusInfo.setErrorMessage(MessageContainer.getMessage(RuntimeExceptionCode.RSE000502, new Object[] {}));
        }
        long end = System.currentTimeMillis();
        deploymentStatusInfo.setTimeTaken(end - start);
        return deploymentStatusInfo;
    }
    
    
    @RequestMapping(value = "/ftp/undeploy", method = RequestMethod.GET)
    public @ResponseBody DeploymentStatusInfo undeployFTP(){
    	LOGGER.info("UnDeploying FTP Processing for tenant {}", RequestContext.getRequestContext().getTenantCode());
    	DeploymentStatusInfo deploymentStatusInfo = new DeploymentStatusInfo();
        long start = System.currentTimeMillis();
        try {
            deploymentStatusInfo = deploymentDelegate.undeployBatchWrapper("ftp");
        } catch (SystemException | BusinessException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
            deploymentStatusInfo.setErrorCode(RuntimeExceptionCode.RSE000501);
            deploymentStatusInfo.setError(true);
            deploymentStatusInfo.setErrorMessage(ex.getLocalizedMessage());
        }
        long end = System.currentTimeMillis();
        deploymentStatusInfo.setTimeTaken(end - start);
        return deploymentStatusInfo;
    }
}