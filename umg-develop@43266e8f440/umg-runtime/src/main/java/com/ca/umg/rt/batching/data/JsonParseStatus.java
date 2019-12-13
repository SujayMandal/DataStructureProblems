package com.ca.umg.rt.batching.data;

import java.io.Serializable;

public class JsonParseStatus implements Serializable {

    /**
     * generated serial version ID
     */
    private static final long serialVersionUID = -1238384262828720432L;
    private String errorCode;
    private String errorMessage;
    
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
