package com.fa.dp.business.validation.input.info;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import lombok.Data;

import com.fa.dp.business.validation.input.base.info.DPProcessParamEntryBaseInfo;

@Data
public class DPProcessParamEntryInfo extends DPProcessParamEntryBaseInfo {

	private static final long serialVersionUID = -6445662111919109343L;
	private DPFileProcessStatusInfo dPFileProcessStatusInfo;

	@Valid
	private List<DPProcessParamInfo> columnEntries;

	private Map<String, List<DPProcessParamInfo>> classifiedColumnEntries;

	private boolean dataLevelError = false;
}
