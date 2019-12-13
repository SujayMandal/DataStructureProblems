package com.fa.dp.business.task.sop.week0;

import com.fa.dp.business.command.Command;
import com.fa.dp.business.sop.week0.delegate.DPSopProcessFilterDelegate;
import com.fa.dp.business.task.sop.week0.filters.SopWeek0DuplicateFilter;
import com.fa.dp.business.sop.week0.input.info.DPSopParamEntryInfo;
import com.fa.dp.business.util.DPFileProcessStatus;
import com.fa.dp.core.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author misprakh
 */

@Slf4j
@Named
public abstract class AbstractSopWeek0DuplicateFilter implements SopWeek0DuplicateFilter, Command {

	@Inject
	private DPSopProcessFilterDelegate dpSopProcessFilterDelegate;

	@Override
	public void executeSopWeek0DuplicateFilter(Object data, String filterName) throws SystemException {
		log.info("sopWeek0DuplicateFilter -> processTask started.");
		Long startTime = DateTime.now().getMillis();
		DPSopParamEntryInfo dpSopParamEntryInfo = null;
		try {
				dpSopParamEntryInfo = ((DPSopParamEntryInfo) data);
				dpSopProcessFilterDelegate.filterOnDuplicates(dpSopParamEntryInfo, filterName);
		} catch (SystemException e) {
			log.error(e.getMessage(), e);
			dpSopParamEntryInfo.getDpSopWeek0ProcessStatusInfo().setStatus(DPFileProcessStatus.ERROR.getFileStatus());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			dpSopParamEntryInfo.getDpSopWeek0ProcessStatusInfo().setStatus(DPFileProcessStatus.ERROR.getFileStatus());
		}
		log.info("Time taken for sopWeek0DuplicateFilter : {}ms" , (DateTime.now().getMillis() - startTime));
		log.info("sopWeek0DuplicateFilter -> processTask ended.");
	}
}
