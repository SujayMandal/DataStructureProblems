package com.fa.dp.business.week0.info;

import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.command.info.CommandInfo;
import com.fa.dp.business.validation.input.info.DPFileProcessStatusInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.business.week0.entity.DynamicPricingFilePrcsStatus;
import com.fa.dp.business.weekn.input.info.DPAssetDetails;
import org.joda.time.DateTime;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
@DecoratedWith(DPWeek0ToInfoDecorator.class)
public interface DPWeek0ToInfoMapper {

	@Mappings({
		@Mapping(target = "createdDate", ignore = true),
		@Mapping(target = "lastModifiedDate", ignore = true)
	})
	DPProcessParamInfo dpWeek0ToInfoMapper(DPProcessParam dpProcessParam);

	@Mappings({
		@Mapping(target = "uploadTimestampStr", expression = "java( com.fa.dp.core.util.DateConversionUtil.getEstDate(dynamicPricingFilePrcsStatus.getUploadTimestamp()).toString(com.fa.dp.core.util.DateConversionUtil.DATE_TIME_FORMATTER) )") })
	DPFileProcessStatusInfo dpFileProcessStatusToInfo(DynamicPricingFilePrcsStatus dynamicPricingFilePrcsStatus);

	DynamicPricingFilePrcsStatus dpFileProcessStsInfoToStatus(DPFileProcessStatusInfo dpFileProcessStatusInfo);

	CommandInfo map(Command command);

	DateTime map(Long aLong);


	DPAssetDetails dpProcessParamToAssetDetailsMapper(DPProcessParam dpProcessParam);

}