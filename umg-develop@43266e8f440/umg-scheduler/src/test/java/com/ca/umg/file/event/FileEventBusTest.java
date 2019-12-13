package com.ca.umg.file.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
import com.ca.umg.file.event.info.FileEvent;

@Ignore
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class FileEventBusTest {

    private static final String TNT_CODE = "TNT_CODE";

    @Inject
    @Named(FileEventBus.BEAN_NAME)
    private EventBus<FileEvent> eventBus;

    @Test
    public void testAdd() {
        /*FileEvent fileEvent = new FileEvent(TNT_CODE, StandardWatchEventKinds.ENTRY_CREATE.name(),
                Paths.get("/sanpath/ocwen/bulk/input"), Paths.get("file1"));*/
        /*eventBus.add(fileEvent);*/
    }

    @Test
    public void testTake() {
        /*FileEvent fileEvent = new FileEvent(TNT_CODE, StandardWatchEventKinds.ENTRY_CREATE.name(),
                Paths.get("/sanpath/ocwen/bulk/input"), Paths.get("file1"));
        eventBus.add(fileEvent);*/

        try {
            FileEvent fiveEventFrombus = eventBus.take();
            //assertEquals(fileEvent, fiveEventFrombus);
        } catch (SystemException e) {
            fail(e.getMessage());
        }

    }

}
