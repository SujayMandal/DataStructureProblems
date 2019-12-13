/**
 * 
 */
package com.ca.umg.event.listener;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.pool.util.ModeletRegistrationEvent;
import com.ca.umg.file.processor.FileRequestProcessor;

/**
 * @author kamathan
 *
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ModeletRegistrationEventListenerTest {

    @Inject
    private ModeletRegistrationEventListener modeletRegistrationEventListener;

    @Inject
    private FileRequestProcessor fileRequestProcessor;

    @Inject
    private CacheRegistry cacheRegistry;

    private static final String TNT_CODE = "TNT_CODE";

    private static final String MDL_NAME = "MDL_NAME";

    private static final String MDL_VERSION = "1";

    @Before
    public void setup() {
        doNothing().when(fileRequestProcessor).processFileByModel(TNT_CODE, MDL_NAME, MDL_VERSION);
    }

    /**
     * Test method for {@link com.ca.umg.event.listener.ModeletRegistrationEventListener#onMessage(com.hazelcast.core.Message)}.
     */	
    @Ignore
    @Test
    public void testOnMessage() {
        ModeletRegistrationEvent modeletRegistrationEvent = new ModeletRegistrationEvent();
        modeletRegistrationEvent.setModelName(MDL_NAME);
        modeletRegistrationEvent.setMajorVersion(MDL_VERSION);
        modeletRegistrationEvent.setTenantCode(TNT_CODE);
        cacheRegistry.getTopic(FrameworkConstant.MODELET_REG_LISTENER_EVENT).publish(modeletRegistrationEvent);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
        verify(fileRequestProcessor, times(1)).processFileByModel(TNT_CODE, MDL_NAME, MDL_VERSION);
    }

}
