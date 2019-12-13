package com.fa.dp.business.sop.weekN.response;

import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SOPWeekNUploadResponse implements Serializable {

	private static final long serialVersionUID = -2748823627908670087L;
	private List<String> errorMessages;
	private boolean dataError = false;
	private DPSopWeekNParamEntryInfo dpSopWeekNParamEntryInfo;

}
