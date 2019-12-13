/**
 * 
 */
package com.fa.dp.business.command.base;

import com.fa.dp.business.command.Command;
import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.core.base.delegate.AbstractDelegate;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mandasuj
 *
 */
public abstract class AbstractCommand extends AbstractDelegate implements Command {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCommand.class);

	/**
	 * This method would check the validity of data.
	 * 
	 * @param data
	 * @return
	 */
	protected boolean checkData(Object data, Class<?> className) {
		boolean dataCorrect = Boolean.TRUE;
		if (data != null && data instanceof DPProcessWeekNParamEntryInfo) {
			DPProcessWeekNParamEntryInfo dpProcessParamEntryInfo = ((DPProcessWeekNParamEntryInfo) data);
			if (dpProcessParamEntryInfo != null) {
				LOGGER.debug("Total DPProcessWeekNParamInfo Entries :"
						+ (CollectionUtils.isNotEmpty(dpProcessParamEntryInfo.getColumnEntries())
								? dpProcessParamEntryInfo.getColumnEntries().size()
										: "0"));
			}
			if (!(data.getClass().isAssignableFrom(className))) {
				dataCorrect = Boolean.FALSE;
			}
		} else if (data != null && data instanceof DPProcessParamEntryInfo) {
			DPProcessParamEntryInfo dpProcessParamEntryInfo = ((DPProcessParamEntryInfo) data);
			if (dpProcessParamEntryInfo != null) {
				LOGGER.debug("Total DPProcessParamEntryInfo Entries :"
						+ (CollectionUtils.isNotEmpty(dpProcessParamEntryInfo.getColumnEntries())
								? dpProcessParamEntryInfo.getColumnEntries().size()
										: "0"));
			}
			if (!(data.getClass().isAssignableFrom(className))) {
				dataCorrect = Boolean.FALSE;
			}
		} else {
			dataCorrect = Boolean.FALSE;
		}
		return dataCorrect;
	}

}
