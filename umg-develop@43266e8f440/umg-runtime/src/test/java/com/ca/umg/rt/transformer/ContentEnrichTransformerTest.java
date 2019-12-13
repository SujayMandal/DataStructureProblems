package com.ca.umg.rt.transformer;

import javax.inject.Inject;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ContentEnrichTransformerTest {

    @Inject
    MappingInputTransformer mappingTransformer;
    
    @Ignore
    @Test
    public void testDoTransform() throws Exception {
        
    }
}
