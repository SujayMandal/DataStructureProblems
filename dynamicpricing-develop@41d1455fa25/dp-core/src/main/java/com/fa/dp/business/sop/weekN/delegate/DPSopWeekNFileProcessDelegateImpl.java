package com.fa.dp.business.sop.weekN.delegate;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.sop.week0.bo.DPSopProcessBO;
import com.fa.dp.business.sop.weekN.bo.DPSopWeekNParamBO;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.util.DPFileProcessStatus;
import com.fa.dp.core.base.delegate.AbstractDelegate;
import com.fa.dp.core.exception.SystemException;

@Slf4j
@Named
public class DPSopWeekNFileProcessDelegateImpl extends AbstractDelegate implements DPSopWeekNFileProcessDelegate {

	@Inject
	private DPSopProcessBO dpSopProcessBO;

	@Inject
	private DPSopWeekNParamBO dpSopWeekNParamBO;

	/**
	 * @param sopWeekNParamEntryInfo
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	@Override
	public String setSopWeekNFileStatus(DPSopWeekNParamEntryInfo sopWeekNParamEntryInfo) throws SystemException {
		List<String> failedStepCommands = dpSopWeekNParamBO.findFailedStepCommands(sopWeekNParamEntryInfo.getDpSopWeekNProcessStatus().getId());
		if (failedStepCommands.isEmpty() || failedStepCommands.size() == 0) {
			return DPFileProcessStatus.SUCCESSFUL.getFileStatus();
		} else {
			List<DPSopWeekNParamInfo> listOfSopWeekN = (List<DPSopWeekNParamInfo>) dpSopProcessBO.getSopAssetsByFileId(sopWeekNParamEntryInfo.getDpSopWeekNProcessStatus().getId(), DPAConstants.SOP_WEEKN);
			if (failedStepCommands.size() == listOfSopWeekN.size()) {
				return DPFileProcessStatus.FAILED.getFileStatus();
			} else {
				return DPFileProcessStatus.PARTIAL.getFileStatus();
			}
		}
	}
}

