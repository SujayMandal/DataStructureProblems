package com.fa.dp.business.weekn.input.info;

import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.command.info.CommandInfo;
import com.fa.dp.business.validator.bo.DPDashboardParamInfo;
import com.fa.dp.core.util.DateConversionUtil;

import javax.inject.Inject;

public class DPWeekNDashboardDecorator implements DPWeekNDashboardMapper {


    @Inject
    private DPWeekNDashboardMapper dpWeekNDashboardMapper;

    @Override
    public DPDashboardParamInfo dpWeekNToDashboardInfoMapper(DPProcessWeekNParamInfo dpProcessWeekNParamInfo) {
        DPDashboardParamInfo dpDashboardParamInfo = dpWeekNDashboardMapper.dpWeekNToDashboardInfoMapper(dpProcessWeekNParamInfo);
        dpDashboardParamInfo.setUploadTimestamp(DateConversionUtil.getEstDate(dpProcessWeekNParamInfo.getDpWeekNProcessStatus().getLastModifiedDate().getMillis()).toString(DateConversionUtil.DATE_TIME_FORMATTER));
        return dpDashboardParamInfo;
    }

    @Override
    public CommandInfo map(Command command) {
        CommandInfo commandInfo = dpWeekNDashboardMapper.map(command);
        return commandInfo;
    }
}
