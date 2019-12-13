/**
 * 
 */
package com.ca.umg.file.event;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.event.EventBus;
import com.ca.umg.event.EventBusPoller;
import com.ca.umg.file.event.info.FileEvent;

/**
 * @author kamathan
 *
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class FileEventBusPollerTest {

    @Inject
    @Named(FileEventBusPoller.BEAN_NAME)
    private EventBusPoller<FileEvent> eventBusPoller;

    @Inject
    @Named(FileEventBus.BEAN_NAME)
    private EventBus<FileEvent> eventBus;

    private static final String TNT_CODE = "TNT_CODE";

    /**
     * Test method for {@link com.ca.umg.file.event.FileEventBusPoller#take()}.
     */
    @Ignore
    @Test
    public void testTake() {
        try {
            /*when(eventBus.take()).thenReturn(new FileEvent(TNT_CODE, StandardWatchEventKinds.ENTRY_CREATE.name(),
                    Paths.get("/sanpath"), Paths.get("dummy.json")));*/
            FileEvent event = eventBusPoller.take();
            assertNotNull(event);
        } catch (SystemException e) {
            fail(e.getLocalizedMessage());
        }
    }

}
