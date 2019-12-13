package com.fa.dp.business.week0.report.vacant.mapper;

import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.business.week0.report.info.DPWeek0ReportInfo;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DPWeek0VacantReportMapper {

	@Mappings({
			@Mapping(source = "week0Param.assetNumber", target = "assetNumber"),
			@Mapping(source = "week0Param.propTemp", target = "propTemp"),
			@Mapping(source = "week0Param.oldAssetNumber", target = "oldAssetNumber"),
			@Mapping(source = "week0Param.assetValue", target = "assetValue"),
			@Mapping(source = "week0Param.avSetDate", target = "avSetDate"),
			@Mapping(source = "week0Param.classification", target = "classification"),
			@Mapping(source = "week0Param.clientCode", target = "clientCode"),
			@Mapping(source = "week0Param.listPrice", target = "listPrice"),
			@Mapping(source = "week0Param.status", target = "status"),
			@Mapping(source = "week0Param.assignment", target = "assignment"),
			@Mapping(source = "week0Param.assignmentDate", target = "assignmentDate"),
			@Mapping(source = "week0Param.eligible", target = "eligible"),
			@Mapping(source = "week0Param.notes", target = "notes"),
			@Mapping(source = "week0Param.week0Price", target = "week0Price"),
			@Mapping(source = "week0Param.state", target = "state"),
			@Mapping(source = "week0Param.rtSource", target = "rtSource"),
			@Mapping(source = "week0Param.propertyType", target = "propertyType"),
			@Mapping(source = "week0Param.pctAV", target = "pctAV"),
			@Mapping(source = "week0Param.withinBusinessRules", target = "withinBusinessRules")
	})

	DPWeek0ReportInfo mapDomainToInfo(DPProcessParam week0Param);

	@IterableMapping(elementTargetType = DPWeek0ReportInfo.class)
	List<DPWeek0ReportInfo> mapDomainToLinfoList(List<DPProcessParam> week0Params);

	@InheritInverseConfiguration
	DPProcessParam mapInfoToDomain(DPWeek0ReportInfo week0ParamInfo);

	@IterableMapping(elementTargetType = DPProcessParam.class)
	List<DPProcessParam> mapInfoToDomainList(List<DPWeek0ReportInfo> week0ParamInfos);

}
