package com.fa.dp.business.sop.week0.input.mapper;

import org.joda.time.DateTime;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.command.info.CommandInfo;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0ProcessStatus;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ParamInfo;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ProcessStatusInfo;
import com.fa.dp.business.validator.bo.DPDashboardParamInfo;

@Mapper(componentModel = "spring")
@DecoratedWith(DPSOPWeek0DashboardDecorator.class)
public interface DPSOPWeek0DashboardMapper {

	@Mappings({
		@Mapping(target = "sopWeek0ProcessStatus", ignore = true)
	})
	DPSopWeek0ParamInfo dpSopWeek0ParamToInfoMapper(DPSopWeek0Param dpSopWeek0Param);

	@Mappings({
		@Mapping(source = "failedStepCommandName", target = "name"),
		@Mapping(source = "sopWeek0ProcessStatus.inputFileName", target = "inputFileName"),
		@Mapping(source = "sopWeek0ProcessStatus.status", target = "status"),
		@Mapping(source = "sopWeek0ProcessStatus.lastModifiedDate", target = "uploadTimeStampInMillis"),
		@Mapping(source = "sopWeek0ProcessStatus.id", target = "id")}
			)
	DPDashboardParamInfo dpSOPWeek0DashboardInfoMapper(DPSopWeek0ParamInfo dpSopWeek0ParamInfo);

	DPSopWeek0ProcessStatus map(DPSopWeek0ProcessStatusInfo dpSopWeek0ProcessStatusInfo);

	CommandInfo map(Command command);

	DateTime map(Long value);
	}
