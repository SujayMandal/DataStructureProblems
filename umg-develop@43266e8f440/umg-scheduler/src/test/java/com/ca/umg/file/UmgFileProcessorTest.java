/*package com.ca.umg.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.exception.UmgSchedulerExceptionCodes;
import com.ca.umg.file.event.processor.FileEventProcessor;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class UmgFileProcessorTest {

    @Inject
    private UmgFileProcessor umgFileProcessor;

    @Inject
    private UmgFilePoller umgFilePoller;

    @Inject
    private FileEventProcessor fileEventProcessor;

    @Test
    public void testInitialize() {
        try {
            doNothing().when(umgFilePoller).registerDirectoriesForPolling();

            doNothing().when(umgFilePoller).poll();

            doNothing().when(fileEventProcessor).processEvent();

            umgFileProcessor.initialize();
        } catch (SystemException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testRegisterDirectoriesForPollingError() {
        try {
            doThrow(SystemException.newSystemException(UmgSchedulerExceptionCodes.USC0000001, new Object[] {}))
                    .when(umgFilePoller).registerDirectoriesForPolling();
            umgFileProcessor.initialize();
        } catch (SystemException e) {
            assertEquals(UmgSchedulerExceptionCodes.USC0000001, e.getCode());
        }
    }

    @Test
    public void testUmgFilePollError() {
        try {
            doThrow(SystemException.newSystemException(UmgSchedulerExceptionCodes.USC0000003, new Object[] {}))
                    .when(umgFilePoller).poll();
            umgFilePoller.poll();
        } catch (SystemException e) {
            assertEquals(UmgSchedulerExceptionCodes.USC0000003, e.getCode());
        }
    }

    @Test
    public void testProcessEventError() {
        try {
            doThrow(SystemException.newSystemException("", new Object[] {})).when(fileEventProcessor).processEvent();
            umgFilePoller.poll();
        } catch (SystemException e) {
            assertEquals("", e.getCode());
        }
    }
}
*/