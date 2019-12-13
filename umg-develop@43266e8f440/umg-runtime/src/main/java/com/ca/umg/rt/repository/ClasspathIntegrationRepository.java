/*
 * ClasspathIntegrationRepository.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.rt.core.flow.entity.Tenant;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.flows.generator.FlowMetaData;

/**
 * Example implemenation of {@link IntegrationRepository}. Loads all spring integration context
 * files that follow a pattern.
 **/
public class ClasspathIntegrationRepository
	implements IntegrationRepository, ApplicationContextAware
{
	private ApplicationContext applicationContext;
	private String             flowPattern = "classpath*:**/*-integration-flow.xml";

	/**
	 * DOCUMENT ME!
	 *
	 * @param applicationContext DOCUMENT ME!
	 *
	 * @throws BeansException DOCUMENT ME!
	 **/
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
	  throws BeansException
	{
		this.applicationContext = applicationContext;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws SystemException DOCUMENT ME!
	 **/
	@Override
	public List<IntegrationFlow> loadIntegrationFlow()
	  throws SystemException
	{
		List<IntegrationFlow> flows = new ArrayList<IntegrationFlow>();

		try
		{
			Resource[]      resources = applicationContext.getResources(this.flowPattern);
			IntegrationFlow newFlow   = null;

			for (Resource resource : resources)
			
			{
				try {
				newFlow = new IntegrationFlow();
				newFlow.setFlowName(resource.getFilename()
				                            .substring(0,
				                                       resource.getFilename()
				                                               .indexOf("-integration-flow.xml")));
				newFlow.setDescription(resource.getDescription());
				byte[] byteArray = new byte[resource.getInputStream().available()];
				resource.getInputStream().read(byteArray);
				newFlow.setResource(new ByteArrayResource(byteArray,resource.getDescription()));
				FlowMetaData metadata = new FlowMetaData();
				metadata.setModelName(newFlow.getFlowName());
				metadata.setMajorVersion(1);
				metadata.setMinorVersion(0);
				newFlow.setFlowMetadata(metadata);
				flows.add(newFlow);
				} finally{
					
					if(resource.getInputStream() != null){
						resource.getInputStream().close();
					}
				}
			}
		}
			
		catch (IOException e)
		{
			throw new SystemException(RuntimeExceptionCode.RSE000100,
			                          new Object[] { this.flowPattern },e);
		}

		return flows;
	}

	/**
	 * Pattern used to search integration flow files.
	 *
	 * @return the flowPattern
	 **/
	public String getFlowPattern()
	{
		return flowPattern;
	}

	/**
	 * Set the pattern used to search integration flow files.
	 *
	 * @param flowPattern the flowPattern to set
	 **/
	public void setFlowPattern(String flowPattern)
	{
		this.flowPattern = flowPattern;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	@Override
	public List<Tenant> loadTenants()
	{
		ArrayList<Tenant> tenantList = new ArrayList<Tenant>(3);

		for (int i = 0; i < 3; i++)
		{
			Tenant tenant = new Tenant();
			tenant.setName("Tenant_" + i);
			tenant.setDescription("Tenant_" + i + " Description");
			tenant.setCode("tenant" + i);
			tenantList.add(tenant);
		}

		return tenantList;
	}

    @Override
    public IntegrationFlow loadBatchIntegrationFlow() throws SystemException, BusinessException {
        return new IntegrationFlow();
    }

    @Override
    public List<Tenant> loadBatchEnabledTenants() throws SystemException, BusinessException {
        return loadTenants();
    }

    @Override
    public IntegrationFlow loadWrapperDetail(String wrapperType) throws SystemException, BusinessException {
        return new IntegrationFlow();
    }

    @Override
    public List<String> getAllEnabledWrappers() throws SystemException, BusinessException {
        return new ArrayList<String>();
    }
}
