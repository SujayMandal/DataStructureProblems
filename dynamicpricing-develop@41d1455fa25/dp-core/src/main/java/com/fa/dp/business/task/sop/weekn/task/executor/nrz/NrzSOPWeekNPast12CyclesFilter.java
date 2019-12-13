package com.fa.dp.business.task.sop.weekn.task.executor.nrz;

import javax.inject.Named;

import org.slf4j.MDC;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.task.sop.weekn.base.AbstractSOPWeekNPast12CyclesFilter;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.RAClientConstants;

@Named
@CommandDescription(name = DPAConstants.NRZ_SOPWEEKN_PAST_12_CYCLES_FILTER)
public class NrzSOPWeekNPast12CyclesFilter extends AbstractSOPWeekNPast12CyclesFilter {

	@Override
	public void execute(Object data) throws SystemException {
		MDC.put(RAClientConstants.COMMAND_PROCES, this.getClass().getAnnotation(CommandDescription.class).name());
		//super.executePast12CycleFilter((DPSopWeekNParamEntryInfo) data);
	}

}
