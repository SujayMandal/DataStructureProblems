package com.fa.dp.business.weekn.input.info;

import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.command.info.CommandInfo;
import com.fa.dp.business.validator.bo.DPDashboardParamInfo;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
@DecoratedWith(DPWeekNDashboardDecorator.class)
public interface DPWeekNDashboardMapper {
    @Mappings({
            @Mapping(source = "command.name", target = "name"),
            @Mapping(source = "dpWeekNProcessStatus.status", target = "status"),
            @Mapping(source = "dpWeekNProcessStatus.fetchedDateStr", target = "fetchedDate"),
            @Mapping(source = "dpWeekNProcessStatus.lastModifiedDate", target = "uploadTimestamp"),
            @Mapping(source = "dpWeekNProcessStatus.lastModifiedDate", target = "uploadTimeStampInMillis"),
            @Mapping(source = "dpWeekNProcessStatus.sysGnrtdInputFileName", target = "inputFileName"),
            @Mapping(source = "dpWeekNProcessStatus.id", target = "id")
    })
    DPDashboardParamInfo dpWeekNToDashboardInfoMapper(DPProcessWeekNParamInfo weekNParams);
    CommandInfo map(Command command);
}
