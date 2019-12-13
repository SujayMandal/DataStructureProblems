package com.fa.dp.business.week0.report.sop.mapper;

import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;
import com.fa.dp.business.week0.report.info.DPWeek0ReportInfo;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DPWeek0SopReportMapper {

	@Mappings({
			@Mapping(source = "sopWeek0Param.assetNumber", target = "assetNumber"),
			@Mapping(source = "sopWeek0Param.propTemp", target = "propTemp"),
			@Mapping(source = "sopWeek0Param.oldAssetNumber", target = "oldAssetNumber"),
			@Mapping(source = "sopWeek0Param.assetValue", target = "assetValue"),
			@Mapping(source = "sopWeek0Param.avSetDate", target = "avSetDate"),
			@Mapping(source = "sopWeek0Param.classification", target = "classification"),
			//@Mapping(source = "sopWeek0Param.clientCode", target = "clientCode"),
			@Mapping(source = "sopWeek0Param.listPrice", target = "listPrice"),
			@Mapping(source = "sopWeek0Param.status", target = "status"),
			@Mapping(source = "sopWeek0Param.assignment", target = "assignment"),
			@Mapping(source = "sopWeek0Param.assignmentDate", target = "assignmentDate"),
			@Mapping(source = "sopWeek0Param.eligible", target = "eligible"),
			@Mapping(source = "sopWeek0Param.notes", target = "notes"),
			//@Mapping(source = "sopWeek0Param.week0Price", target = "week0Price"),
			@Mapping(source = "sopWeek0Param.state", target = "state"),
			//@Mapping(source = "sopWeek0Param.rtSource", target = "rtSource"),
			@Mapping(source = "sopWeek0Param.propertyType", target = "propertyType"),
			//@Mapping(source = "sopWeek0Param.pctAV", target = "pctAV"),
			//@Mapping(source = "sopWeek0Param.withinBusinessRules", target = "withinBusinessRules")
	})
	DPWeek0ReportInfo mapDomainToInfo(DPSopWeek0Param sopWeek0Param);

	@IterableMapping(elementTargetType = DPWeek0ReportInfo.class)
	List<DPWeek0ReportInfo> mapDomainToLinfoList(List<DPSopWeek0Param> sopWeek0Params);

	@InheritInverseConfiguration
	DPSopWeek0Param mapInfoToDomain(DPWeek0ReportInfo sopWeek0ParamInfo);

	@IterableMapping(elementTargetType = DPSopWeek0Param.class)
	List<DPSopWeek0Param> mapInfoToDomainList(List<DPWeek0ReportInfo> sopWeek0ParamInfos);

	/*@InheritInverseConfiguration
	DPSopWeek0Param mapInfoToDomain(DPWeek0ReportInfo sopWeek0ParamInfo);

	@IterableMapping(elementTargetType = DPSopWeek0Param.class)
	List<DPSopWeek0Param> mapInfoToDomainList(List<DPWeek0ReportInfo> sopWeek0ParamInfos);*/

}
