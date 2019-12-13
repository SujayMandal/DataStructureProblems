package com.fa.dp.business.task.sop.weekn.task.executor.nrz;

import javax.inject.Named;

import org.slf4j.MDC;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.task.sop.weekn.base.AbstractSOPWeekNAssignmentFilter;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.RAClientConstants;

@Named
@CommandDescription(name = DPAConstants.NRZ_SOPWEEKN_ASSIGNMENT_FILTER)
public class NrzSOPWeekNAssignmentFilter extends AbstractSOPWeekNAssignmentFilter {
	
	@Override
	public void execute(Object data) throws SystemException {
		// no action required
	}

}


