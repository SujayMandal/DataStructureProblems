package com.ca.umg.rt.batching.ftp;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.integration.Message;
import org.springframework.integration.message.GenericMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import junit.framework.Assert;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class FTPFileHandlerTest {

    private FTPFileHandler ftpfileHandler = new FTPFileHandler();


    String filePayload_path = ".src/test/resources/com/ca/umg/rt/batching/ftp/FTP_16MB_Regression_FTP_1422340896401.json";

    @Test
    public void testSetFTPHeaderInfo() {
        File payLoad = new File(filePayload_path);
        Message updatedMessage = (Message) ftpfileHandler.setFTPHeaderInfo(new GenericMessage(payLoad));
        Assert.assertEquals(6, updatedMessage.getHeaders().size());
        // Assigning all references to null to free memory
        updatedMessage = null;
        payLoad = null;
        ftpfileHandler = null;

    }
}
