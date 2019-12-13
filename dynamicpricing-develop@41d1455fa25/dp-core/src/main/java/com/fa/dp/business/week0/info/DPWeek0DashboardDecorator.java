package com.fa.dp.business.week0.info;

import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.command.info.CommandInfo;
import com.fa.dp.business.validation.input.info.DPFileProcessStatusInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.validator.bo.DPDashboardParamInfo;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.business.week0.entity.DynamicPricingFilePrcsStatus;
import com.fa.dp.core.util.DateConversionUtil;

import org.joda.time.DateTime;

import javax.inject.Inject;

public class DPWeek0DashboardDecorator implements DPWeek0DashboardMapper {

    @Inject
    private DPWeek0DashboardMapper dashboardMapper;


    @Override
    public DPProcessParamInfo dpProcessParamsToInfoMapper(DPProcessParam dpProcessParam) {
        return null;
    }

    @Override
    public DPDashboardParamInfo dpWeekZeroToDashboardInfoMapper(DPProcessParamInfo dpProcessWeekZeroParamInfo) {
        DPDashboardParamInfo dpDashboardParamInfo = dashboardMapper.dpWeekZeroToDashboardInfoMapper(dpProcessWeekZeroParamInfo);
        dpDashboardParamInfo.setUploadTimestamp(dpProcessWeekZeroParamInfo.getDynamicPricingFilePrcsStatus().getUploadTimestampStr());
        dpDashboardParamInfo.setUploadTimeStampInMillis(DateConversionUtil.EST_DATE_TIME_FORMATTER.parseDateTime(dpProcessWeekZeroParamInfo.getDynamicPricingFilePrcsStatus().getUploadTimestampStr()));
        return dpDashboardParamInfo;
    }

    @Override
    public DynamicPricingFilePrcsStatus map(DPFileProcessStatusInfo dpFileProcessStatusInfo) {
        DynamicPricingFilePrcsStatus dynamicPricingFilePrcsStatus = dashboardMapper.map(dpFileProcessStatusInfo);
        return dynamicPricingFilePrcsStatus;
    }

    @Override
    public CommandInfo map(Command command) {
        CommandInfo commandInfo = dashboardMapper.map(command);
        return commandInfo;
    }

    @Override
    public DateTime map(Long value) {
        return dashboardMapper.map(value);
    }
}
