package com.ca.umg.rt.flows.container;

import java.io.IOException;
import java.util.Properties;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.HttpRequestHandler;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.rt.repository.IntegrationFlow;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
public class RuntimeFlowContainerDatabaseTest {    
    
    @Inject
    @Qualifier("flowContainerManager")
    private ContainerManager containerManager;
    
    @Inject
    private ApplicationContext applicationContext;
    
    @Before
    public void init() {
        Properties properties = new Properties();
        properties.put(RequestContext.TENANT_CODE, "localhost");
        new RequestContext(properties);
    }
    
    @Ignore
    @Test
    public final void test() throws SystemException, BusinessException {
        containerManager.start();
        Properties properties = new Properties();
        properties.setProperty(RequestContext.TENANT_CODE, "localhost");
        RequestContext context= new RequestContext(properties);
        context.setTenantCode("localhost");
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getServletPath()).thenReturn("/umg-runtime");
        Mockito.when(request.getContextPath()).thenReturn("umg-runtime");
        Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/umg-runtime/echo1"));
        Mockito.when(request.getRequestURI()).thenReturn("/umg-runtime/modelname1");
        Mockito.when(request.getMethod()).thenReturn("POST");
        HttpRequestHandler adapter = containerManager.getHandler(request, null);
        Assert.assertNotNull(adapter);
        context.destroy();
        containerManager.stop();
    }
    //@Test
    public final void testRefresh() throws SystemException, BusinessException, ServletException, IOException {
        containerManager.start();
        //containerManager.refresh();        
        containerManager.stop();
    }
    
    //@Test
    public final void testRefreshFlow() throws SystemException, BusinessException, ServletException, IOException {
        containerManager.start();
        IntegrationFlow  flow = new IntegrationFlow();
        flow.setFlowName("flow-1");
        //containerManager.refreshFlow(flow);      
        containerManager.stop();
    }
    
    //@Test
    public final void testUnDeployFlow() throws SystemException, BusinessException, ServletException, IOException {
        containerManager.start();
        IntegrationFlow  flow = new IntegrationFlow();
        flow.setFlowName("flow-1");
        containerManager.unDeployflow(flow ,true);      
        containerManager.stop();
    }
    
    //@Test
    public final void testDeployFlow() throws SystemException, BusinessException, ServletException, IOException {
        containerManager.start();
        IntegrationFlow  flow = new IntegrationFlow();
        flow.setFlowName("deployflow");
        
        Resource resource = applicationContext.getResource("classpath:flows/flow-3/runtime-integration-flow.xml");
        if(resource == null) {
            throw new IOException("File does not exist");
        }
        try{
        byte[] data = new byte[resource.getInputStream().available()];      
        resource.getInputStream().read(data);
        String xml = new String(data);
        String newXml = xml.replaceAll("/modelname", "/modelname-test-deploy");
        flow.setResource(new ByteArrayResource(newXml.getBytes()));
        containerManager.deployflow(flow , true);
        Properties properties = new Properties();
        properties.setProperty(RequestContext.TENANT_CODE, "localhost");
        RequestContext context= new RequestContext(properties);
        
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getServletPath()).thenReturn("/umg-runtime");
        Mockito.when(request.getContextPath()).thenReturn("umg-runtime");
        Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/umg-runtime/modelname-test-deploy"));
        Mockito.when(request.getRequestURI()).thenReturn("/umg-runtime/modelname-test-deploy");
        Mockito.when(request.getMethod()).thenReturn("POST");
        HttpRequestHandler adapter = containerManager.getHandler(request, null);
        Assert.assertNotNull(adapter);
        context.destroy();
        containerManager.stop();
        } finally{
        	if(resource.getInputStream() != null){
        		resource.getInputStream().close();
        		
        	}
        }
    }
    
}
