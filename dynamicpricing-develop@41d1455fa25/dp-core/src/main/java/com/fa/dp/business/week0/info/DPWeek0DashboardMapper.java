package com.fa.dp.business.week0.info;

import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.command.info.CommandInfo;
import com.fa.dp.business.validation.input.info.DPFileProcessStatusInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.validator.bo.DPDashboardParamInfo;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.business.week0.entity.DynamicPricingFilePrcsStatus;
import org.joda.time.DateTime;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
@DecoratedWith(DPWeek0DashboardDecorator.class)
public interface DPWeek0DashboardMapper {

    @Mappings({
            @Mapping(target = "dynamicPricingFilePrcsStatus", ignore = true)
    })
    DPProcessParamInfo dpProcessParamsToInfoMapper(DPProcessParam dpProcessParam);

    @Mappings({
            @Mapping(source = "command.name", target = "name"),
            @Mapping(source = "dynamicPricingFilePrcsStatus.inputFileName", target = "inputFileName"),
            @Mapping(source = "dynamicPricingFilePrcsStatus.status", target = "status"),
            @Mapping(source = "dynamicPricingFilePrcsStatus.uploadTimestamp", target = "uploadTimeStampInMillis"),
            @Mapping(source = "dynamicPricingFilePrcsStatus.id", target = "id")}
    )
    DPDashboardParamInfo dpWeekZeroToDashboardInfoMapper(DPProcessParamInfo dpProcessWeekZeroParamInfo);
    DynamicPricingFilePrcsStatus map(DPFileProcessStatusInfo fileProcessStatusInfo);
    CommandInfo map(Command command);
    DateTime map(Long value);
}
