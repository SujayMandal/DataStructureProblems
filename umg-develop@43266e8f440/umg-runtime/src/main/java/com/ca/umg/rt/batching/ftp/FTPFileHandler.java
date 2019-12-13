/**
 * 
 */
package com.ca.umg.rt.batching.ftp;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.support.MessageBuilder;

import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.util.RuntimeBatchUtil;

/**
 * This service activator feeds the header information required to move the file from input to archive folder.
 * 
 * @author chandrsa
 * 
 */
public class FTPFileHandler {

    private static final String FILE_REMOTE_FILE = "file_remoteFile";
    private static final String FILE_REMOTE_DIRECTORY = "file_remoteDirectory";
    private static final String FILE_RENAME_TO = "file_renameTo";
    private static final String FILE_NAME = "file_umg_name";

    private static final Logger LOGGER = LoggerFactory.getLogger(FTPFileHandler.class);

    @ServiceActivator
    public Object setFTPHeaderInfo(Message<?> message) {
        Message<?> resultMessage = null;
        String fileName = null;
        File payload = null;
        String tenantCode = null;
        if (message != null) {
            payload = (File) message.getPayload();
            if (payload != null) {
                fileName = RuntimeBatchUtil.getRemoteFileName(payload.getName());
                tenantCode = (String) message.getHeaders().get(RuntimeConstants.TENANT_CODE);
                resultMessage = MessageBuilder.withPayload(payload).copyHeaders(message.getHeaders())
                        .setHeaderIfAbsent(FILE_RENAME_TO, RuntimeConstants.BATCH_ARCHIVE)
                        .setHeaderIfAbsent(FILE_REMOTE_DIRECTORY, RuntimeConstants.BATCH_INPUT)
                        .setHeaderIfAbsent(FILE_REMOTE_FILE, fileName).setHeaderIfAbsent(FILE_NAME, payload.getName()).build();
                LOGGER.debug(String.format("Tenant Code :: %s -- Moving %s from %s to %s", tenantCode, fileName,
                        RuntimeConstants.BATCH_INPUT, RuntimeConstants.BATCH_ARCHIVE));
            }
        }
        return resultMessage;
    }


}