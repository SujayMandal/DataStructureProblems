/**
 * 
 */
package com.ca.umg.file.scheduler;

import static org.mockito.Mockito.doNothing;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.umg.file.processor.FileRequestProcessor;

/**
 * @author kamathan
 *
 */
@Ignore
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class FileRequestProcessingSchedulerTest {

    @Inject
    private FileRequestProcessor fileRequestProcessor;

    @Inject
    private FileRequestProcessingScheduler fileRequestProcessingScheduler;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        doNothing().when(fileRequestProcessor).processAllFiles();
    }

    /**
     * Test method for {@link com.ca.umg.file.scheduler.FileRequestProcessingScheduler#schedule()}.
     */
    @Test
    public void testSchedule() {
        fileRequestProcessingScheduler.schedule();
    }

}
