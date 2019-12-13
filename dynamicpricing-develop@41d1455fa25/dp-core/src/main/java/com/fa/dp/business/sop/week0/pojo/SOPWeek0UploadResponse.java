package com.fa.dp.business.sop.week0.pojo;

import java.util.List;

import com.fa.dp.business.sop.week0.input.info.DPSopParamEntryInfo;

public class SOPWeek0UploadResponse {
	private List<String> errorMessages;
	private boolean dataError = false;
	private DPSopParamEntryInfo dpSopParamEntryInfo;

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

	public DPSopParamEntryInfo getDpSopParamEntryInfo() {
		return dpSopParamEntryInfo;
	}

	public void setDpSopParamEntryInfo(DPSopParamEntryInfo dpSopParamEntryInfo) {
		this.dpSopParamEntryInfo = dpSopParamEntryInfo;
	}

}
