package com.fa.dp.business.sop.weekN.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.validator.bo.DPDashboardParamInfo;

@Mapper(componentModel = "spring")
public interface DPSOPWeekNDashboardMapper {

	@Mappings({
		@Mapping(source = "failedStepCommandName", target = "name"),
		@Mapping(source = "sopWeekNProcessStatus.inputFileName", target = "inputFileName"),
		@Mapping(source = "sopWeekNProcessStatus.status", target = "status"),
		@Mapping(target = "uploadTimeStampInMillis", expression = "java(com.fa.dp.core.util.DateConversionUtil.convertUtcToEstTimeZone(weekNParams.getSopWeekNProcessStatus().getLastModifiedDate()))"),
		@Mapping(target = "uploadTimestamp", expression = "java(com.fa.dp.core.util.DateConversionUtil.getEstDateText(weekNParams.getSopWeekNProcessStatus().getLastModifiedDate().getMillis()))"),
		@Mapping(source = "sopWeekNProcessStatus.id", target = "id")
	})
	DPDashboardParamInfo dpSOPWeekNToDashboardInfoMapper(DPSopWeekNParamInfo weekNParams);

}
