package com.fa.dp.business.sop.weekN.mapper;

import com.fa.dp.business.sop.weekN.entity.DPSopWeekNParam;
import com.fa.dp.business.sop.weekN.entity.DPSopWeekNProcessStatus;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNProcessStatusInfo;
import org.joda.time.DateTime;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DPSopWeekNParamMapper {
	@Mappings({
			@Mapping(target = "createdDate", expression = "java(com.fa.dp.core.util.DateConversionUtil.getEstDate(sopWeekNParam.getCreatedDate()))"),
			@Mapping(target = "lastModifiedDate", expression = "java(com.fa.dp.core.util.DateConversionUtil.getEstDate(sopWeekNParam.getLastModifiedDate()))")
	})
	DPSopWeekNParamInfo mapDomainToInfo(DPSopWeekNParam sopWeekNParam);

	@Mappings({
			@Mapping(target = "createdDate", expression = "java(com.fa.dp.core.util.DateConversionUtil.getEstDate(value.getCreatedDate()))"),
			@Mapping(target = "lastModifiedDate", expression = "java(com.fa.dp.core.util.DateConversionUtil.getEstDate(value.getLastModifiedDate()))")
	})
	DPSopWeekNProcessStatusInfo map(DPSopWeekNProcessStatus value);

	@InheritInverseConfiguration
	DPSopWeekNProcessStatus map(DPSopWeekNProcessStatusInfo value);

	DateTime map(Long value);

	@IterableMapping(elementTargetType = DPSopWeekNParamInfo.class)
	List<DPSopWeekNParamInfo> mapDomainToLinfoList(List<DPSopWeekNParam> sopWeekNParams);

	@InheritInverseConfiguration
	DPSopWeekNParam mapInfoToDomain(DPSopWeekNParamInfo sopWeekNParamInfo);

	@IterableMapping(elementTargetType = DPSopWeekNParam.class)
	List<DPSopWeekNParam> mapInfoToDomainList(List<DPSopWeekNParamInfo> sopWeekNParamInfos);

}
