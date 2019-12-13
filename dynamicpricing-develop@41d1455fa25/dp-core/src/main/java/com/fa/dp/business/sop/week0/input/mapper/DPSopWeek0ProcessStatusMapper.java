package com.fa.dp.business.sop.week0.input.mapper;

import com.fa.dp.business.sop.week0.entity.DPSopWeek0ProcessStatus;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ProcessStatusInfo;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DPSopWeek0ProcessStatusMapper {

	@Mappings({
		@Mapping(target = "createdDate", expression = "java(com.fa.dp.core.util.DateConversionUtil.getEstDate(sopWeek0ProcessStatus.getCreatedDate()))"),
		@Mapping(target = "lastModifiedDate", expression = "java(com.fa.dp.core.util.DateConversionUtil.getEstDate(sopWeek0ProcessStatus.getLastModifiedDate()))")
	})
	DPSopWeek0ProcessStatusInfo mapDomainToInfo(DPSopWeek0ProcessStatus sopWeek0ProcessStatus);

	@IterableMapping(elementTargetType = DPSopWeek0ProcessStatusInfo.class)
	List<DPSopWeek0ProcessStatusInfo> mapDomainToLinfoList(List<DPSopWeek0ProcessStatus> sopWeek0ProcessStatusList);

	@InheritInverseConfiguration
	DPSopWeek0ProcessStatus mapInfoToDomain(DPSopWeek0ProcessStatusInfo sopWeek0ProcessStatusInfo);

	@IterableMapping(elementTargetType = DPSopWeek0ProcessStatus.class)
	List<DPSopWeek0ProcessStatus> mapInfoToDomainList(List<DPSopWeek0ProcessStatusInfo> sopWeek0ProcessStatusInfos);
}
