package com.fa.dp.business.sop.week0.input.info;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import lombok.Data;

import com.fa.dp.business.validation.input.base.info.DPProcessParamEntryBaseInfo;

/**
 * @author misprakh
 */

@Data
public class DPSopParamEntryInfo extends DPProcessParamEntryBaseInfo {

	private static final long serialVersionUID = -6445662111919109343L;
	private DPSopWeek0ProcessStatusInfo dpSopWeek0ProcessStatusInfo;

	@Valid
	private List<DPSopWeek0ParamInfo> columnEntries;

	private Map<String, List<DPSopWeek0ParamInfo>> classifiedColumnEntries;

	private boolean dataLevelError = false;
}
