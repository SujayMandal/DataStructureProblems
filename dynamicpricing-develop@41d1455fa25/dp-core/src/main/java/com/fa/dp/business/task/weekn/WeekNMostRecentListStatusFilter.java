package com.fa.dp.business.task.weekn;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Scope;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.command.dao.CommandDAO;
import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.command.info.CommandInfo;
import com.fa.dp.business.command.info.CommandProcess;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.filter.bo.DPProcessWeekNParamsBO;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.info.HubzuInfo;
import com.fa.dp.business.weekn.entity.DPProcessWeekNParam;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.DateConversionUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Named
@Scope("prototype")
@CommandDescription(name = "weekNSuccessfulUnderreviewFilter")
public class WeekNMostRecentListStatusFilter extends AbstractCommand {

	@Inject
	private CommandDAO commandDAO;

	@Inject
	private DPProcessWeekNParamsBO dpProcessWeekNParamsBO;

	@Override
	public void execute(Object data) throws SystemException {
		DPProcessWeekNParamEntryInfo dpProcessParamEntryInfo = null;
		if (checkData(data, DPProcessWeekNParamEntryInfo.class)) {
			dpProcessParamEntryInfo = ((DPProcessWeekNParamEntryInfo) data);
			Long startTime = DateTime.now().getMillis();

			log.info("weekNSuccessfulUnderreviewFilter -> processTask started.");
			List<DPProcessWeekNParamInfo> successEntries = new ArrayList<>();
			if (!dpProcessParamEntryInfo.getColumnEntries().isEmpty()) {

				for (DPProcessWeekNParamInfo columnEntry : dpProcessParamEntryInfo.getColumnEntries()) {
					List<HubzuInfo> hubzuResponse = columnEntry.getHubzuDBResponse().getHubzuInfos();
					HubzuInfo lastRecord = hubzuResponse.get(hubzuResponse.size() - 1);

					if (lastRecord.getListSttsDtlsVc() == null) {
						successEntries.add(columnEntry);
						continue;
					}
					if (lastRecord.getListSttsDtlsVc().equals("SUCCESSFUL") || lastRecord.getListSttsDtlsVc().equals("UNDERREVIEW")) {
						//Fetch entries from WeekN db whose delivery date is null
						List<DPProcessWeekNParam> dpProcessParams = dpProcessWeekNParamsBO.searchByAssetDeliveryNull(
								columnEntry.getAssetNumber());
						if (!dpProcessParams.isEmpty()) {
							if (!dpProcessParamEntryInfo.isFetchProcess()) {
								CommandInfo commandInfo;
								if (StringUtils.equalsIgnoreCase(columnEntry.getClassification(), DPProcessParamAttributes.OCN.getValue())) {
									List<Command> command = commandDAO.findByProcess(
											CommandProcess.WEEKN_OCN.getCommmandProcess(), DPAConstants.SUCCESSFUL_UNDERREVIEW_FILTER);
									commandInfo = convert(command.get(0), CommandInfo.class);
								} else if(StringUtils.equalsIgnoreCase(columnEntry.getClassification(), DPProcessParamAttributes.NRZ.getValue())){
									List<Command> command = commandDAO.findByProcess(
											CommandProcess.WEEKN_NRZ.getCommmandProcess(), DPAConstants.SUCCESSFUL_UNDERREVIEW_FILTER);
									commandInfo = convert(command.get(0), CommandInfo.class);
								}else{
									List<Command> command = commandDAO.findByProcess(
											  CommandProcess.WEEKN_PHH.getCommmandProcess(), DPAConstants.SUCCESSFUL_UNDERREVIEW_FILTER);
									commandInfo = convert(command.get(0), CommandInfo.class);
								}
								columnEntry.setCommand(commandInfo);
								columnEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
								columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
								columnEntry.setExclusionReason(DPProcessFilterParams.SUCCESFUL_UNDERREVIEW_EXCLUSION.getValue());
								dpProcessWeekNParamsBO.saveDPProcessWeekNParamInfo(columnEntry);
							}
						} else {
							successEntries.add(columnEntry);
						}
					} else {
						successEntries.add(columnEntry);
					}
				}
				dpProcessParamEntryInfo.setColumnEntries(successEntries);
			}
			log.info("weekNSuccessfulUnderreviewFilter -> processTask ended.");
			log.info("Time Taken for weekNSuccessfulUnderreviewFilter is " + (DateTime.now().getMillis() - startTime) + "ms");
		}
	}
}
