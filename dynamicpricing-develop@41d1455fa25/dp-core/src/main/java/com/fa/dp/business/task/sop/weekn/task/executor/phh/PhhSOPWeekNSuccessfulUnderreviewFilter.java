package com.fa.dp.business.task.sop.weekn.task.executor.phh;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.task.sop.weekn.base.AbstractSOPWeekNSuccessfulUnderreviewFilter;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.RAClientConstants;
import org.slf4j.MDC;

import javax.inject.Named;

@Named
@CommandDescription(name = DPAConstants.PHH_SOPWEEKN_SUCCESSFUL_UNDERREVIEW_FILTER)
public class PhhSOPWeekNSuccessfulUnderreviewFilter extends AbstractSOPWeekNSuccessfulUnderreviewFilter {

	@Override
	public void execute(Object data) throws SystemException {
		MDC.put(RAClientConstants.COMMAND_PROCES, this.getClass().getAnnotation(CommandDescription.class).name());
		super.executeSuccessfulUnderReviewFilter((DPSopWeekNParamEntryInfo) data);
	}
}
