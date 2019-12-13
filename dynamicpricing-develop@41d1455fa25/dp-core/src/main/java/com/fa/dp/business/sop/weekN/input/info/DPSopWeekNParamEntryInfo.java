package com.fa.dp.business.sop.weekN.input.info;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import lombok.Data;

import com.fa.dp.business.validation.input.base.info.DPProcessParamEntryBaseInfo;

@Data
public class DPSopWeekNParamEntryInfo extends DPProcessParamEntryBaseInfo {

	private static final long serialVersionUID = 8022926558701155805L;
	private DPSopWeekNProcessStatusInfo dpSopWeekNProcessStatus;

	@Valid
	private List<DPSopWeekNParamInfo> columnEntries;

	private Map<String, List<DPSopWeekNParamInfo>> classifiedColumnEntries;

	private boolean dataLevelError = false;

	private boolean fetchProcess;
}
