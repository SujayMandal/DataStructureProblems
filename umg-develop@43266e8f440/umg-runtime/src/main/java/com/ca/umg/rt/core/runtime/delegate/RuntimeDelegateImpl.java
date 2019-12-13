/*
 * RuntimeDelegateImpl.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.core.runtime.delegate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.ca.umg.rt.core.runtime.bo.RuntimeBO;
import com.ca.umg.rt.core.runtime.info.RuntimeFlowInfo;
import com.ca.umg.rt.repository.IntegrationFlow;

/**
 * 
 **/
@Component
public class RuntimeDelegateImpl
	implements RuntimeDelegate
{
	@Inject
	private RuntimeBO runtimeBO;

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	@Override
	public Map<String, List<RuntimeFlowInfo>> getFlowList()
	{
		Map<String, Collection<IntegrationFlow>> flowList       = runtimeBO.getFlowList();
		Map<String, List<RuntimeFlowInfo>>       runtimeFlowMap = new HashMap<String, List<RuntimeFlowInfo>>();
		ArrayList<RuntimeFlowInfo> tenantFlows = null;
		RuntimeFlowInfo runtimeFlowInfo = null;
		for(String tenant:flowList.keySet()){
		    tenantFlows = new ArrayList<RuntimeFlowInfo>();
		    for(IntegrationFlow integrationFlow: flowList.get(tenant)) {
		        runtimeFlowInfo = new RuntimeFlowInfo();
	            runtimeFlowInfo.setName(integrationFlow.getFlowName());
	            runtimeFlowInfo.setMajorVersion(integrationFlow.getFlowMetadata().getMajorVersion());
	            runtimeFlowInfo.setMinorVersion(integrationFlow.getFlowMetadata().getMinorVersion());
	            //runtimeFlowInfo.setDeactivatedDate(deactivatedDate);
	            //runtimeFlowInfo.setPublishedDate(publishedDate);
	            tenantFlows.add(runtimeFlowInfo);
		    }
		    runtimeFlowMap.put(tenant, tenantFlows);		    
		}
		return runtimeFlowMap;
	}
}
