package com.fa.dp.business.sop.weekN.mapper;

import com.fa.dp.business.sop.weekN.entity.DPSopWeekNProcessStatus;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNProcessStatusInfo;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DPSopWeekNProcessStatusMapper {

	@Mappings({
			@Mapping(target = "createdDate", expression = "java(com.fa.dp.core.util.DateConversionUtil.getEstDate(sopWeekNProcessStatus.getCreatedDate()))"),
			@Mapping(target = "lastModifiedDate", expression = "java(com.fa.dp.core.util.DateConversionUtil.getEstDate(sopWeekNProcessStatus.getLastModifiedDate()))")
	})
	DPSopWeekNProcessStatusInfo mapDomainToInfo(DPSopWeekNProcessStatus sopWeekNProcessStatus);

	@IterableMapping(elementTargetType = DPSopWeekNProcessStatusInfo.class)
	List<DPSopWeekNProcessStatusInfo> mapDomainToLinfoList(List<DPSopWeekNProcessStatus> sopWeekNProcessStatusList);

	@InheritInverseConfiguration
	DPSopWeekNProcessStatus mapInfoToDomain(DPSopWeekNProcessStatusInfo sopWeekNProcessStatusInfo);

	@IterableMapping(elementTargetType = DPSopWeekNProcessStatus.class)
	List<DPSopWeekNProcessStatus> mapInfoToDomainList(List<DPSopWeekNProcessStatusInfo> sopWeekNProcessStatusInfos);
}
