package com.fa.dp.business.task.sop.weekn.task.executor;

import javax.inject.Named;

import org.slf4j.MDC;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.task.sop.weekn.base.AbstractSopWeekNFetchDataProcess;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.RAClientConstants;

@Named
@CommandDescription(name = DPAConstants.SOPWEEKN_DATA_FETCH_FAILURE)
public class DPSopWeekNFetchDataProcess extends AbstractSopWeekNFetchDataProcess {
	
	@Override
	public void execute(Object data) throws SystemException {
		MDC.put(RAClientConstants.COMMAND_PROCES, this.getClass().getAnnotation(CommandDescription.class).name());
		executeWeekNFetchProcess((DPSopWeekNParamEntryInfo) data);
	}
}
