package com.fa.dp.business.task.weekn;

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
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.RAClientConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Scope;
import org.springframework.util.ObjectUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Named
@Scope("prototype")
@CommandDescription(name = "weekNPast12CyclesFilter")
public class WeekNPast12CyclesFilter extends AbstractCommand {

	@Inject
	private CommandDAO commandDAO;

	@Inject
	private DPProcessWeekNParamsBO dpProcessWeekNParamsBO;

	@Override
	public void execute(Object data) throws SystemException {
		DPProcessWeekNParamEntryInfo dpProcessParamEntryInfo = ((DPProcessWeekNParamEntryInfo) data);
		log.info("weekNPast12CyclesFilter -> processTask started.");
		Long startTime = DateTime.now().getMillis();
		List<DPProcessWeekNParamInfo> successEntries = new ArrayList<>();
		if (dpProcessParamEntryInfo.getColumnEntries() != null) {
			for (DPProcessWeekNParamInfo columnEntry : dpProcessParamEntryInfo.getColumnEntries()) {
				if (columnEntry.getCommand() == null) {
					if (!ObjectUtils.isEmpty(columnEntry.getHubzuDBResponse().getHubzuInfos())
							&& columnEntry.getHubzuDBResponse().getHubzuInfos().size() > 12) {
						CommandInfo commandInfo;
						if (StringUtils.equalsIgnoreCase(columnEntry.getClassification(), DPProcessParamAttributes.OCN.getValue())) {
							List<Command> command = commandDAO
									.findByProcess(CommandProcess.WEEKN_OCN.getCommmandProcess(), DPAConstants.PAST12_CYCLES_FILTER);
							commandInfo = convert(command.get(0), CommandInfo.class);
						} else if (StringUtils.equalsIgnoreCase(columnEntry.getClassification(), DPProcessParamAttributes.NRZ.getValue())) {
							List<Command> command = commandDAO
									.findByProcess(CommandProcess.WEEKN_NRZ.getCommmandProcess(), DPAConstants.PAST12_CYCLES_FILTER);
							commandInfo = convert(command.get(0), CommandInfo.class);
						} else {
							List<Command> command = commandDAO
									.findByProcess(CommandProcess.WEEKN_PHH.getCommmandProcess(), DPAConstants.PAST12_CYCLES_FILTER);
							commandInfo = convert(command.get(0), CommandInfo.class);
						}
						columnEntry.setCommand(commandInfo);
						columnEntry.setListEndDateDtNn(columnEntry.getHubzuDBResponse().getHubzuInfos().get(11).getListEndDateDtNn() == null ?
								RAClientConstants.CHAR_EMPTY :
								columnEntry.getHubzuDBResponse().getHubzuInfos().get(11).getListEndDateDtNn().toString());
						columnEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
						columnEntry.setExclusionReason(DPProcessFilterParams.PAST_12_CYCLES_EXCLUSION.getValue());
						if (!dpProcessParamEntryInfo.isFetchProcess())
							dpProcessWeekNParamsBO.saveDPProcessWeekNParamInfo(columnEntry);
					} else {
						successEntries.add(columnEntry);
					}
				}
			}
			if (!dpProcessParamEntryInfo.isFetchProcess())
				dpProcessParamEntryInfo.setColumnEntries(successEntries);
		}
		log.info("Time taken for weekNPast12CyclesFilter : " + (DateTime.now().getMillis() - startTime) + "ms");
		log.info("weekNPast12CyclesFilter -> processTask ended.");
	}

}
