package com.fa.dp.business.audit.info;

import com.fa.dp.business.audit.entity.DPWeekNAuditReports;
import com.fa.dp.business.info.HubzuInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
/*@DecoratedWith(HubzuInfoToAuditDecorator.class)*/
public interface HubzuInfoToAuditMapper {
    @Mappings({
            @Mapping(source = "selrPropIdVcNn", target = "loanNumber"),
            })
    DPWeekNAuditReports hubzuInfoToAuditReports(HubzuInfo hubzuInfo);
}
