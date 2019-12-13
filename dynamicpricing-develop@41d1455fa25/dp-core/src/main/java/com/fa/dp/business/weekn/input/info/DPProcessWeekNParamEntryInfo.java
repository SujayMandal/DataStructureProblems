package com.fa.dp.business.weekn.input.info;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;

import com.fa.dp.business.validation.input.base.info.DPProcessParamEntryBaseInfo;
import lombok.Data;

@Data
public class DPProcessWeekNParamEntryInfo extends DPProcessParamEntryBaseInfo {

	private static final long serialVersionUID = 1897869869428330419L;

	private DPWeekNProcessStatusInfo dpWeeknProcessStatus;

	@Valid
	private List<DPProcessWeekNParamInfo> columnEntries;

	private boolean fetchProcess;

	private List<DPProcessWeekNParamInfo> skippedTwelveDaysEntries;

}
