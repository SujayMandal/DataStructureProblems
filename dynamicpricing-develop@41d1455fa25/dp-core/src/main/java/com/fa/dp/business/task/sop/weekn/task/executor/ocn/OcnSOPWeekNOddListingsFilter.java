package com.fa.dp.business.task.sop.weekn.task.executor.ocn;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.task.sop.weekn.base.AbstractSOPWeekNOddListingsFilter;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.RAClientConstants;

import org.slf4j.MDC;

import javax.inject.Named;

@Named
@CommandDescription(name = DPAConstants.OCN_SOPWEEKN_ODD_LISTINGS_FILTER)
public class OcnSOPWeekNOddListingsFilter extends AbstractSOPWeekNOddListingsFilter {

	@Override
	public void execute(Object data) throws SystemException {
		MDC.put(RAClientConstants.COMMAND_PROCES, this.getClass().getAnnotation(CommandDescription.class).name());
		super.executeOddListingFilter((DPSopWeekNParamEntryInfo) data);
	}
}
