package com.fa.dp.business.task.sop.weekn.base;

import com.fa.dp.business.command.Command;
import com.fa.dp.business.task.sop.weekn.filters.SOPWeekNOutputFileCreate;
import com.fa.dp.core.exception.SystemException;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Named;

@Slf4j
@Named
public abstract class AbstractSOPWeekNOutputFileCreate implements Command, SOPWeekNOutputFileCreate {

	@Override
	public void executeOutputFile(Object data) throws SystemException {

	}
}
