package com.ca.umg.rt.repository;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
public class DatabaseIntegrationRepositoryTest {

    @Autowired
    IntegrationRepository integrationRepository;

    @Before
    public void setup() {
        Properties properties = new Properties();
        properties.put(RequestContext.TENANT_CODE, "localhost");
        new RequestContext(properties);
    }

    @Ignore
    @Test
    public void testLoadIntegrationFlow() throws SystemException, BusinessException {

        assertTrue(integrationRepository.loadIntegrationFlow().size() > 0);

    }

}
