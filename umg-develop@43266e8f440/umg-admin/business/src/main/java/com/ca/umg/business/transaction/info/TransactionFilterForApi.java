package com.ca.umg.business.transaction.info;

import java.util.List;

@SuppressWarnings("PMD")
public class TransactionFilterForApi {

	private static final long serialVersionUID = -6993042029313343492L;
	
	private Boolean includeTntOutput;
	
	private Boolean includeTntInput;
	
	private List<String> payloadOutputFields;
	
	private List<String> payloadInputFields;

    public Boolean getIncludeTntOutput() {
        return includeTntOutput;
    }

    public void setIncludeTntOutput(Boolean includeTntOutput) {
        this.includeTntOutput = includeTntOutput;
    }

    public Boolean getIncludeTntInput() {
        return includeTntInput;
    }

    public void setIncludeTntInput(Boolean includeTntInput) {
        this.includeTntInput = includeTntInput;
    }

    public List<String> getPayloadOutputFields() {
        return payloadOutputFields;
    }

    public void setPayloadOutputFields(List<String> payloadOutputFields) {
        this.payloadOutputFields = payloadOutputFields;
    }

    public List<String> getPayloadInputFields() {
        return payloadInputFields;
    }

    public void setPayloadInputFields(List<String> payloadInputFields) {
        this.payloadInputFields = payloadInputFields;
    }
}