package com.ca.umg.business.mapping.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mapping.entity.MappingInput;

@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class MappingInputDAOTest extends AbstractMappingTest {

    private String tenantCode = null;

    @Before
    public void setup() {
        RequestContext requestContext = getLocalhostTenantContext();
        tenantCode = requestContext.getTenantCode();
    }

    @Test
    public void testMappingInputDAO() {
        Mapping mapping = createMapping("TIDIOMAPPING-1",
                createModel("tidioModel", "desc", "doc", "sample", "text/xml", "sample"), tenantCode, "tid for model_1",
                "Sample MID Json");
        assertNotNull(mapping);

        MappingInput mappingInput = createMappingInput(mapping, "sampleMappingData".getBytes(), "sampleTid".getBytes(),
                tenantCode);
        assertNotNull(mappingInput);
        assertNotNull(mappingInput.getId());

        MappingInput savedMappingIp = getMappingInputDAO().findByMapping(mapping);
        assertNotNull(savedMappingIp);
        assertEquals(mappingInput, savedMappingIp);
    }

}
