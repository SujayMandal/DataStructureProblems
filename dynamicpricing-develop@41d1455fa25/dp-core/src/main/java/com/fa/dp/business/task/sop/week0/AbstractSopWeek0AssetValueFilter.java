package com.fa.dp.business.task.sop.week0;

import com.fa.dp.business.command.Command;
import com.fa.dp.business.task.sop.week0.filters.SopWeek0AssetValueFilter;
import com.fa.dp.business.sop.week0.delegate.DPSopProcessFilterDelegate;
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
public abstract class AbstractSopWeek0AssetValueFilter implements SopWeek0AssetValueFilter, Command {

	@Inject
	private DPSopProcessFilterDelegate dpSopProcessFilterDelegate;


	/**
	 * Default implementation for asset value filter.
	 * This method filters the assets or loan number based on the asset value defined in the system.
	 *
	 * @param data
	 * @throws SystemException
	 */
	public void executeAssetValueFilter(Object data, String filterName) throws SystemException {
		log.debug("sopWeek0AssetValueFilter -> processTask started.");
		DPSopParamEntryInfo dpSopParamEntryInfo = null;
		Long startTime = DateTime.now().getMillis();
		try {
				dpSopParamEntryInfo = ((DPSopParamEntryInfo) data);
				dpSopProcessFilterDelegate.filterOnAssetValue(dpSopParamEntryInfo, filterName);
		} catch (SystemException se) {
			log.error(se.getMessage(), se);
			dpSopParamEntryInfo.getDpSopWeek0ProcessStatusInfo().setStatus(DPFileProcessStatus.ERROR.getFileStatus());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			dpSopParamEntryInfo.getDpSopWeek0ProcessStatusInfo().setStatus(DPFileProcessStatus.ERROR.getFileStatus());
		}
		log.info("Time taken for sopWeek0AssetValueFilter : {} ms", (DateTime.now().getMillis() - startTime));
		log.debug("sopWeek0AssetValueFilter -> processTask ended.");
	}
}
