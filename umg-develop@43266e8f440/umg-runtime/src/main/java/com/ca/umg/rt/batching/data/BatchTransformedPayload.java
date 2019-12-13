package com.ca.umg.rt.batching.data;

import java.util.List;
import java.util.Map;

import org.springframework.integration.Message;

import com.ca.framework.core.util.KeyValuePair;
import com.ca.pool.model.TransactionCriteria;

/**
 * @author basanaga
 * 
 */
public class BatchTransformedPayload {
    
    private Message<?> message;
    private String fileName;
    private String tenantCode;
    private String batchId;
    private BatchRequest batchRequest;
    private String absolutePath;
    private KeyValuePair<Object, List<Map<String, Object>>> msgPayload;
    private KeyValuePair<Object, byte[]> errorMsgPayLoad;
    private boolean error;
    private String sanPath;
    private TransactionCriteria transactionCriteria;


    /**
     * @return the message
     */
    public Message<?> getMessage() {
        return message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(Message<?> message) {
        this.message = message;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName
     *            the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the tenantCode
     */
    public String getTenantCode() {
        return tenantCode;
    }

    /**
     * @param tenantCode
     *            the tenantCode to set
     */
    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    /**
     * @return the batchId
     */
    public String getBatchId() {
        return batchId;
    }

    /**
     * @param batchId
     *            the batchId to set
     */
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    /**
     * @return the batchRequest
     */
    public BatchRequest getBatchRequest() {
        return batchRequest;
    }

    /**
     * @param batchRequest
     *            the batchRequest to set
     */
    public void setBatchRequest(BatchRequest batchRequest) {
        this.batchRequest = batchRequest;
    }

    /**
     * @return the absolutePath
     */
    public String getAbsolutePath() {
        return absolutePath;
    }

    /**
     * @param absolutePath
     *            the absolutePath to set
     */
    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    /**
     * @return the msgPayload
     */
    public KeyValuePair<Object, List<Map<String, Object>>> getMsgPayload() {
        return msgPayload;
    }

    /**
     * @param msgPayload
     *            the msgPayload to set
     */
    public void setMsgPayload(KeyValuePair<Object, List<Map<String, Object>>> msgPayload) {
        this.msgPayload = msgPayload;
    }

    /**
     * @return the error
     */
    public boolean isError() {
        return error;
    }

    /**
     * @param error
     *            the error to set
     */
    public void setError(boolean error) {
        this.error = error;
    }

	/**
	 * get the error msg payload
	 * @return
	 */
	public KeyValuePair<Object, byte[]> getErrorMsgPayLoad() {
		return errorMsgPayLoad;
	}

	/**
	 * @param errorMsgPayLoad
	 */
	public void setErrorMsgPayLoad(KeyValuePair<Object, byte[]> errorMsgPayLoad) {
		this.errorMsgPayLoad = errorMsgPayLoad;
	}

    public String getSanPath() {
        return sanPath;
    }

    public void setSanPath(String sanPath) {
        this.sanPath = sanPath;
    }

	public TransactionCriteria getTransactionCriteria() {
		return transactionCriteria;
	}

	public void setTransactionCriteria(TransactionCriteria transactionCriteria) {
		this.transactionCriteria = transactionCriteria;
	}



}
