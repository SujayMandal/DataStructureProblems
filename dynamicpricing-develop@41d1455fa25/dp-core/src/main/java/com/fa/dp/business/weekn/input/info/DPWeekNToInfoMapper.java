package com.fa.dp.business.weekn.input.info;

import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.command.info.CommandInfo;
import com.fa.dp.business.weekn.entity.DPProcessWeekNParam;
import com.fa.dp.business.weekn.entity.DPWeekNProcessStatus;
import org.joda.time.DateTime;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
@DecoratedWith(DPWeekNToInfoDecorator.class)
public interface DPWeekNToInfoMapper {

    @Mappings({
            @Mapping(target = "createdDate", ignore = true),
            @Mapping(target = "lastModifiedDate", ignore = true)
    })
    DPProcessWeekNParamInfo dpWeekNToInfoMapper(DPProcessWeekNParam dpProcessWeekNParam);

    DPWeekNProcessStatusInfo map(DPWeekNProcessStatus dpWeekNProcessStatus);
    CommandInfo map(Command command);
    DateTime map(Long aLong);
}
