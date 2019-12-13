package com.fa.dp.client.pojo;

import java.util.List;

import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;

public class DPProcessResponse {
	private List<String> errorMessages;
	private boolean dataError = false;
	private DPProcessParamEntryInfo dpProcessParamEntry;

	public List<String> getErrorMessages() {
		return errorMessages;
	}

	public void setErrorMessages(List<String> errorMessages) {
		this.errorMessages = errorMessages;
	}

	public boolean isDataError() {
		return dataError;
	}

	public void setDataError(boolean dataError) {
		this.dataError = dataError;
	}

	public DPProcessParamEntryInfo getDpProcessParamEntry() {
		return dpProcessParamEntry;
	}

	public void setDpProcessParamEntry(DPProcessParamEntryInfo dpProcessParamEntry) {
		this.dpProcessParamEntry = dpProcessParamEntry;
	}

}
