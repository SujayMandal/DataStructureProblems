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
import com.ca.umg.business.mapping.entity.MappingOutput;

@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class MappingOutputDAOTest extends AbstractMappingTest {

    private String tenantCode = null;

    @Before
    public void setup() {
        RequestContext requestContext = getLocalhostTenantContext();
        tenantCode = requestContext.getTenantCode();
    }

    @Test
    public void testMappingInputDAO() {
        Mapping mapping = createMapping("TIDOPMAPPING-1",
                createModel("tidopModel", "desc", "doc", "sample", "text/xml", "sample"), tenantCode, "tid for model_1",
                "Sample MID Json");
        assertNotNull(mapping);

        MappingOutput mappingOutput = createMappingOutput(mapping, "sampleioMappingData".getBytes(), "sampleioTid".getBytes(),
                tenantCode);
        assertNotNull(mappingOutput);
        assertNotNull(mappingOutput.getId());

        MappingOutput savedMappingOp = getMappingOutputDAO().findByMapping(mapping);
        assertNotNull(savedMappingOp);
        assertEquals(mappingOutput, savedMappingOp);
    }

}
