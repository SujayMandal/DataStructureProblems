package com.fa.dp.business.sop.week0.input.mapper;

import javax.inject.Inject;

import org.joda.time.DateTime;

import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.command.info.CommandInfo;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0ProcessStatus;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ParamInfo;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ProcessStatusInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.validator.bo.DPDashboardParamInfo;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.core.util.DateConversionUtil;

public class DPSOPWeek0DashboardDecorator implements DPSOPWeek0DashboardMapper {

	@Inject
    private DPSOPWeek0DashboardMapper sopWeek0DashboardMapper;
	
	@Override
    public DPSopWeek0ParamInfo dpSopWeek0ParamToInfoMapper(DPSopWeek0Param dpSopWeek0Param) {
        return null;
    }

    @Override
    public DPDashboardParamInfo dpSOPWeek0DashboardInfoMapper(DPSopWeek0ParamInfo dpSopWeek0ParamInfo) {
        DPDashboardParamInfo dpDashboardParamInfo = sopWeek0DashboardMapper.dpSOPWeek0DashboardInfoMapper(dpSopWeek0ParamInfo);
        dpDashboardParamInfo.setUploadTimestamp(DateConversionUtil.getEstDateText(dpSopWeek0ParamInfo.getSopWeek0ProcessStatus().getLastModifiedDate().getMillis()));
        dpDashboardParamInfo.setUploadTimeStampInMillis(DateConversionUtil.convertUtcToEstTimeZone(dpSopWeek0ParamInfo.getSopWeek0ProcessStatus().getLastModifiedDate()));
        return dpDashboardParamInfo;
    }

    @Override
    public DPSopWeek0ProcessStatus map(DPSopWeek0ProcessStatusInfo dpSopWeek0ProcessStatusInfo) {
    	DPSopWeek0ProcessStatus dpSopWeek0ProcessStatus = sopWeek0DashboardMapper.map(dpSopWeek0ProcessStatusInfo);
        return dpSopWeek0ProcessStatus;
    }

    @Override
    public CommandInfo map(Command command) {
        CommandInfo commandInfo = sopWeek0DashboardMapper.map(command);
        return commandInfo;
    }

    @Override
    public DateTime map(Long value) {
        return sopWeek0DashboardMapper.map(value);
    }

}
