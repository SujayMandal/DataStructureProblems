package com.fa.dp.business.task.sop.weekn.base;

import com.fa.dp.business.command.Command;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.pmi.entity.PmiInsuranceCompany;
import com.fa.dp.business.sop.weekN.delegate.DPSopWeekNParamDelegate;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.ssinvestor.bo.SpclServicingInvestorBO;
import com.fa.dp.business.task.sop.weekn.filters.SOPWeekNSSPmiFilter;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.RAClientConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Named
public abstract class AbstractSOPWeekNSSPmiFilter implements Command, SOPWeekNSSPmiFilter {

	@Inject
	private SpclServicingInvestorBO spclServicingInvestorBo;

	@Inject
	private DPSopWeekNParamDelegate sopWeekNParamDelegate;

	@Override
	public void executeWeekNSSPmiFilter(DPSopWeekNParamEntryInfo paramEntryInfo) throws SystemException {
		List<DPSopWeekNParamInfo> successEntries = new ArrayList<>();
		List<DPSopWeekNParamInfo> failEntries = new ArrayList<>();

		List<PmiInsuranceCompany> pmiInscCompanies = spclServicingInvestorBo.findPmiInsCompsByActiveTrue();
		List<String> pmiInscCompIds = pmiInscCompanies.stream().map(a -> new String[] {a.getInsuranceCompany(), a.getCompanyCode()})
				.flatMap(a -> Arrays.stream(a)).distinct().collect(Collectors.toList());

		if(CollectionUtils.isNotEmpty(paramEntryInfo.getColumnEntries())) {
			paramEntryInfo.getColumnEntries().stream().filter(d -> StringUtils.isEmpty(d.getFailedStepCommandName())).forEach(paramEntry -> {
				if(StringUtils.equals(RAClientConstants.YES, paramEntry.getPrivateMortgageInsurance())) {
					paramEntry.setFailedStepCommandName(MDC.get(RAClientConstants.COMMAND_PROCES));
					paramEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
					paramEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
					paramEntry.setExclusionReason(DPProcessFilterParams.PMI.getValue());
					failEntries.add(paramEntry);
				} else if(StringUtils.equals(paramEntry.getPrivateMortgageInsurance(), RAClientConstants.NO) && pmiInscCompIds
						.contains(paramEntry.getPmiCompanyInsuranceId())) {
					paramEntry.setFailedStepCommandName(MDC.get(RAClientConstants.COMMAND_PROCES));
					paramEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
					paramEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
					paramEntry.setExclusionReason(DPProcessFilterParams.PMI.getValue());
					failEntries.add(paramEntry);
				} else if(paramEntry.getSpecialServicingFlag().equalsIgnoreCase(RAClientConstants.YES)) {
					paramEntry.setFailedStepCommandName(MDC.get(RAClientConstants.COMMAND_PROCES));
					paramEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
					paramEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
					paramEntry.setExclusionReason(DPProcessFilterParams.SPECIAL_SERVICE.getValue());
					failEntries.add(paramEntry);
				} else {
					successEntries.add(paramEntry);
				}
			});
		}
		if(!paramEntryInfo.isFetchProcess()) {
			paramEntryInfo.setColumnEntries(successEntries);
			if(CollectionUtils.isNotEmpty(failEntries)) {
				sopWeekNParamDelegate.saveSopWeekNParamInfoList(failEntries);
			}
		}
	}
}
