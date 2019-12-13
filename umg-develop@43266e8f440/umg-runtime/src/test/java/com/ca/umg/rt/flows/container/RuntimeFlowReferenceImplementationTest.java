package com.ca.umg.rt.flows.container;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.rt.endpoint.http.HttpTransportHandler;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
public class RuntimeFlowReferenceImplementationTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Inject
    private HttpTransportHandler handler;

    @Inject
    ApplicationContext context;

    @Before
    public void setup() {
        Properties properties = new Properties();
        properties.put(RequestContext.TENANT_CODE, "tenant1");
        new RequestContext(properties);
    }

    @Ignore
    @Test
    public final void test() throws SystemException, BusinessException, ServletException, IOException {
        Resource resource = context.getResource("classpath:/com/ca/umg/rt/flows/container/tenant-input-data-1.json");
        try{
        byte[] data = new byte[resource.getInputStream().available()];
        resource.getInputStream().read(data);
        this.request = new MockHttpServletRequest();
        this.response = new MockHttpServletResponse();
        this.request.setContentType("application/json");
        this.request.setMethod("POST");
        this.request.setRequestURI("/umg-runtime/modelname");
        this.request.setContent(data);
        this.request.setContextPath("umg-runtime");
        this.request.setServletPath("/umg-runtime");

        HttpServletRequest request = (HttpServletRequest) this.request;
        ServletRequestAttributes attributes = new ServletRequestAttributes(this.request);

        LocaleContextHolder.setLocale(request.getLocale());
        RequestContextHolder.setRequestAttributes(attributes);

        handler.handleRequest(request, response);
        } finally{
        	if(resource.getInputStream() != null){
        		resource.getInputStream().close();
        	}
        }
    }

    @SuppressWarnings("unused")
    private static class MockRestTemplate2 extends RestTemplate {

        @Override
        public <T> ResponseEntity<T> exchange(URI uri, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType)
                throws RestClientException {
            return new ResponseEntity<T>(HttpStatus.OK);
        }
    }
}
