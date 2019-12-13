package com.fa.dp.business.sop.week0.input.mapper;

import java.util.List;

import org.joda.time.DateTime;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0ProcessStatus;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ParamInfo;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ProcessStatusInfo;
import com.fa.dp.business.weekn.input.info.DPAssetDetails;

@Mapper(componentModel = "spring")
public interface DPSopWeek0ParamMapper {

	@Mappings({
			@Mapping(target = "createdDate", expression = "java(com.fa.dp.core.util.DateConversionUtil.getEstDate(sopWeek0Param.getCreatedDate()))"),
			@Mapping(target = "lastModifiedDate", expression = "java(com.fa.dp.core.util.DateConversionUtil.getEstDate(sopWeek0Param.getLastModifiedDate()))")
	})

	DPSopWeek0ParamInfo mapDomainToInfo(DPSopWeek0Param sopWeek0Param);

	DateTime map(Long value);

	@IterableMapping(elementTargetType = DPSopWeek0ParamInfo.class)
	List<DPSopWeek0ParamInfo> mapDomainToInfoList(List<DPSopWeek0Param> sopWeek0Params);

	@Mappings({
		@Mapping(target = "createdDate", expression = "java(com.fa.dp.core.util.DateConversionUtil.getEstDate(value.getCreatedDate()))"),
		@Mapping(target = "lastModifiedDate", expression = "java(com.fa.dp.core.util.DateConversionUtil.getEstDate(value.getLastModifiedDate()))")
	})
	DPSopWeek0ProcessStatusInfo map(DPSopWeek0ProcessStatus value);

	@InheritInverseConfiguration
	DPSopWeek0ProcessStatus map(DPSopWeek0ProcessStatusInfo value);

	@InheritInverseConfiguration
	DPSopWeek0Param mapInfoToDomain(DPSopWeek0ParamInfo sopWeek0ParamInfo);

	@IterableMapping(elementTargetType = DPSopWeek0Param.class)
	List<DPSopWeek0Param> mapInfoToDomainList(List<DPSopWeek0ParamInfo> sopWeek0ParamInfos);

	DPAssetDetails dpProcessParamToAssetDetailsMapper(DPSopWeek0Param dpProcessParam);

}
