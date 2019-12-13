package com.fa.dp.business.task.sop.weekn.task.executor.nrz;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.task.sop.weekn.base.AbstractSOPWeekNOutputFileCreate;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.RAClientConstants;
import org.slf4j.MDC;

import javax.inject.Named;

@Named
@CommandDescription(name = DPAConstants.NRZ_SOPWEEKN_OUTPUT_FILE_CREATE)
public class NrzSOPWeekNOutputFileCreate extends AbstractSOPWeekNOutputFileCreate {

	@Override
	public void execute(Object data) throws SystemException {
		MDC.put(RAClientConstants.COMMAND_PROCES, this.getClass().getAnnotation(CommandDescription.class).name());
		super.executeOutputFile(data);
	}

}
