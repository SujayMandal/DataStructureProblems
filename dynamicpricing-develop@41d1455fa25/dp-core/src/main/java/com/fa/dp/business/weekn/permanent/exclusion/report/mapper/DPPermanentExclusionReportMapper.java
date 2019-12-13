package com.fa.dp.business.weekn.permanent.exclusion.report.mapper;

import com.fa.dp.business.audit.entity.DPWeekNAuditReports;
import com.fa.dp.business.weekn.permanent.exclusion.report.info.DPPermanentExclusionReportInfo;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DPPermanentExclusionReportMapper {
	@Mappings({
			@Mapping(source = "auditReport.loanNumber", target = "eqLoanNumber"),
			@Mapping(source = "auditReport.oldLoanNumber", target = "rrLoanNumber"),
			@Mapping(source = "auditReport.propTemp", target = "propertyId"),
			@Mapping(source = "auditReport.classification", target = "classiication"),
			@Mapping(source = "auditReport.action", target = "exclusionReason"),
			@Mapping(target = "updateDate", expression = "java(com.fa.dp.core.util.DateConversionUtil.getEstDateText(auditReport.getDeliveryDate()))"),
	})
	DPPermanentExclusionReportInfo mapDomainToInfo(DPWeekNAuditReports auditReport);

	@IterableMapping(elementTargetType = DPPermanentExclusionReportInfo.class)
	List<DPPermanentExclusionReportInfo> mapDomainToLinfoList(List<DPWeekNAuditReports> auditReportList);

	@InheritInverseConfiguration
	DPWeekNAuditReports mapInfoToDomain(DPPermanentExclusionReportInfo exclusionReportInfo);

	@IterableMapping(elementTargetType = DPWeekNAuditReports.class)
	List<DPWeekNAuditReports> mapInfoToDomainList(List<DPPermanentExclusionReportInfo> exclusionReportInfoList);
}
