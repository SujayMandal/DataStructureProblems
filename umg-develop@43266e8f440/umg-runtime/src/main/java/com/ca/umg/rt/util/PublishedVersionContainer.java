package com.ca.umg.rt.util;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.rt.core.flow.bo.FlowBO;
import com.ca.umg.rt.core.flow.entity.Tenant;
import com.ca.umg.rt.core.flow.entity.Version;
import com.ca.umg.rt.endpoint.http.Header;
import com.ca.umg.rt.endpoint.http.ModelRequest;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * 
 * @author raddibas
 *
 */
public class PublishedVersionContainer {
	
	@Inject
	private FlowBO flowBO;

	private final Map<String, Object> containerMap = new ConcurrentHashMap<>();
    
	public Map<String, Object> getContainerMap() {
		return containerMap;
	}
    
	@PostConstruct
	private void init() throws SystemException, BusinessException {//NOPMD
		List<Tenant> tenantList = null;
		
		tenantList = flowBO.getAllTenants();
		for (Tenant tenant : tenantList) {
			createPublishedVersions(tenant.getCode());
		}
	}
	
	public Boolean checkRequestedVersionIsPublished (ModelRequest modelRequest) throws SystemException, BusinessException {
        Header header = modelRequest.getHeader();
        if (header == null) {
            throw new SystemException(RuntimeExceptionCode.RVE000202, new Object[] { "Reqeust header not found" }); // NOPMD
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        Boolean publishedVersion = Boolean.FALSE;
        Version version = new Version();
        version.setName(header.getModelName());
        version.setMajorVersion(header.getMajorVersion());
        version.setMinorVersion(header.getMinorVersion());
        String tenantCode = RequestContext.getRequestContext().getTenantCode();
        List<Version> publishedVersionsOfTnt = getList(tenantCode);
        if (publishedVersionsOfTnt.contains(version)) {
            publishedVersion = Boolean.TRUE;
        }
        return publishedVersion;
	}
	
	private List<Version> getList(final String tenantCode) {
		List<Version> publishedVersionsOfTnt = (List<Version>) containerMap.get(tenantCode);
		if (CollectionUtils.isEmpty(publishedVersionsOfTnt)) {
			createPublishedVersions(tenantCode);
			publishedVersionsOfTnt = (List<Version>) containerMap.get(tenantCode);
		}
		return publishedVersionsOfTnt;
	}
	
	private void createPublishedVersions(final String tenantCode) {
		List<Version> publishedVersions = null;
		RequestContext requestContext =   null;
		try {
			Properties properties = new Properties();
	        properties.put(RequestContext.TENANT_CODE, tenantCode);
	        requestContext = new RequestContext(properties);
	        publishedVersions = flowBO.getAllVersionsForTenant();
	        containerMap.put(tenantCode, publishedVersions);
		} finally {
			requestContext.destroy();
		}
	}	
}
