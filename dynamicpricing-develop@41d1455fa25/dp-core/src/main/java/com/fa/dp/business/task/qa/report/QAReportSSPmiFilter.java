package com.fa.dp.business.task.qa.report;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.filter.delegate.DPProcessWeekNFilterDelegate;
import com.fa.dp.business.pmi.entity.PmiInsuranceCompany;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.RAClientConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Slf4j
@Named
@CommandDescription(name = "qaReportSSPmiFilter")
public class QAReportSSPmiFilter extends AbstractCommand {

	@Inject
	private DPProcessWeekNFilterDelegate dpProcessWeekNFilterDelegate;

	@Override
	public void execute(Object data) throws SystemException {
		log.info("qaReportSSPmiFilter -> processTask ended.");
		Long startTime = DateTime.now().getMillis();

		DPProcessWeekNParamEntryInfo infoObject = ((DPProcessWeekNParamEntryInfo) data);

		dpProcessWeekNFilterDelegate.filterQAReportSSPmi(infoObject);

		log.info("Time Taken for qaReportSSPmiFilter is " + (DateTime.now().getMillis() - startTime) + "ms");
		log.info("qaReportSSPmiFilter -> processTask ended.");
	}
}
