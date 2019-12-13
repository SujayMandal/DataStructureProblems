package com.fa.dp.business.weekn.run.status.delegate;

import com.fa.dp.business.command.master.CommandMaster;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.filter.bo.DPProcessSOPWeekNParamsBO;
import com.fa.dp.business.filter.bo.DPProcessWeekNParamsBO;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.validator.bo.DPFileProcessSOPWeekNBO;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.business.weekn.report.info.WeekNDailyQAReportInfo;
import com.fa.dp.business.weekn.run.status.bo.SOPWeekNDailyRunStatusBO;
import com.fa.dp.business.weekn.run.status.info.WeekNDailyRunStatusInfo;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.DateConversionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Named
@Slf4j
public class SOPWeekNDailyRunStatusDelegateImpl implements SOPWeekNDailyRunStatusDelegate{

    @Inject
    private SOPWeekNDailyRunStatusBO sopWeekNDailyRunStatusBO;

    @Inject
    private DPFileProcessSOPWeekNBO dpFileProcessSOPWeekNBO;

    @Inject
    private DPProcessWeekNParamsBO dpProcessWeekNParamsBO;

    @Inject
    private DPProcessSOPWeekNParamsBO dpProcessSOPWeekNParamsBO;


    @Inject
    @Named("dpCommandMaster")
    private CommandMaster commandMaster;


    @Override
    public LocalDate getLastRunDateForSOP() throws SystemException {
        return sopWeekNDailyRunStatusBO.getLastRunDateForSOP();
    }

    @Override
    public LocalDate getLatestWekNRunDate() {
        return dpFileProcessSOPWeekNBO.getLatestWekNRunDate();
    }

    @Override
    public List<WeekNDailyQAReportInfo> checkReduction(List<WeekNDailyQAReportInfo> weekNDailyQAReportInfoList, Boolean sopStatus) {
        log.debug("SOP checkReduction start.");
        List<WeekNDailyQAReportInfo> qaReportInfoList = new ArrayList<>();

        log.info("Reduction : SOP weekN daily qa report info size : {}", weekNDailyQAReportInfoList.size());

        List<WeekNDailyQAReportInfo> nonReductionQAReportInfoList = new ArrayList<>();

        weekNDailyQAReportInfoList.forEach(weeknInfo -> {
            DPProcessWeekNParamInfo data = dpProcessSOPWeekNParamsBO
                    .checkReduction(weeknInfo.getSelrPropIdVcNn(), weeknInfo.getOldLoanNumber(), weeknInfo.getCurrentListEndDate());

            if (Objects.nonNull(data)) {
                //c.setActualListCycle();
                weeknInfo.setWeeknRecommendedListPriceReduction(data.getLpDollarAdjustmentRec() != null ? data.getLpDollarAdjustmentRec().toString() : null);
                if(data.getDeliveryDate() == null) {
                    data.setDeliveryDate(data.getLastModifiedDate().getMillis());
                }
                weeknInfo.setWeeknRecommendedDate(data.getDeliveryDate() != null ? data.getDeliveryDate().toString() : null);
                weeknInfo.setWeeknExclusionReason(data.getExclusionReason());

                String pctPriceChangeFrmLastList;
                if(data.getLpDollarAdjustmentRec() != null && weeknInfo.getPreviousListPrice() != null) {
                    pctPriceChangeFrmLastList = String.valueOf(data.getLpDollarAdjustmentRec().divide(new BigDecimal(weeknInfo.getPreviousListPrice()), 5, RoundingMode.CEILING));
                } else {
                    pctPriceChangeFrmLastList = "0";
                }

                weeknInfo.setPctPriceChangeFrmLastList(pctPriceChangeFrmLastList);
                //c.setRuleViolation();
                //c.setWeeknMissingreport(Boolean.TRUE.toString());
                weeknInfo.setClassification(data.getClassification());
            } else {
                log.error("No record found in SOP weekn db for sele prop id : {}, current list end date :{}", weeknInfo.getSelrPropIdVcNn(),
                        weeknInfo.getCurrentListEndDate());
            }

            boolean reduction = (data != null && data.getLpDollarAdjustmentRec() != null
                    && data.getLpDollarAdjustmentRec().compareTo(new BigDecimal("0")) < 0) ? true : false;
            log.info("Reduction : {}", reduction);
            if (reduction) {
                weeknInfo.setStatus(true);
                weeknInfo.setWeeknMissingreport(Boolean.FALSE.toString());
                weeknInfo.setWeeknExclusionReason(null);
                qaReportInfoList.add(weeknInfo);
            } else {
                //use hubzu command to find exclusion reason
                nonReductionQAReportInfoList.add(weeknInfo);
                //findExclusionReason(c, data, c.getCurrentListEndDate());
                //c.setStatus(false);
            }
        });

        prepareReductionTask(nonReductionQAReportInfoList, sopStatus);

        qaReportInfoList.addAll(nonReductionQAReportInfoList);

        log.debug("SOP weekn checkReduction end.");
        return qaReportInfoList;
    }

    @Override
    public void notifyDailyRunStatus(WeekNDailyRunStatusInfo weekNDailyRunStatus, String exceptionTrace, List<String> failedLoanNumbers)
            throws SystemException {
        sopWeekNDailyRunStatusBO.notifyDailyRunStatus(weekNDailyRunStatus, exceptionTrace, failedLoanNumbers);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void saveWeekNSOPQaReport(List<WeekNDailyQAReportInfo> weeknQAReportList, WeekNDailyRunStatusInfo runStatusInfo) throws SystemException {
        WeekNDailyRunStatusInfo weeknRunStatusInfo = sopWeekNDailyRunStatusBO.saveWeekNSOPRunStatus(runStatusInfo);
        sopWeekNDailyRunStatusBO.saveWeekNSOPQaReport(weeknQAReportList, weeknRunStatusInfo);
    }

    private void prepareReductionTask(List<WeekNDailyQAReportInfo> nonReductionQAReportInfoList, Boolean sopStatus) {
        DPSopWeekNParamEntryInfo paramEntryInfo = new DPSopWeekNParamEntryInfo();
        List<DPSopWeekNParamInfo> columnEntries = new ArrayList<>();

        nonReductionQAReportInfoList.stream().forEach(info -> {
            DPSopWeekNParamInfo paramInfo = new DPSopWeekNParamInfo();
            paramInfo.setAssetNumber(info.getSelrPropIdVcNn());
            paramInfo.setClassification(info.getClassification());
            paramInfo.setPropTemp(info.getSelrPropIdVcNn());
            paramInfo.setOldAssetNumber(info.getOldLoanNumber());
            paramInfo.setMostRecentListEndDate(info.getCurrentListEndDate().format(DateTimeFormatter.ofPattern(DateConversionUtil.DATE_DD_MMM_YY)));
            columnEntries.add(paramInfo);
        });

        paramEntryInfo.setColumnEntries(columnEntries);
        commandMaster.filterQaReportForSOP(paramEntryInfo, sopStatus);

        Map<String, String> exclusionMap = new HashMap<>();
        Map<String, Boolean> exclusionBooleanMap = new HashMap<>();
        paramEntryInfo.getColumnEntries().stream().forEach(a -> {
            exclusionMap.put(a.getPropTemp(), a.getExclusionReason());
            if (StringUtils.equalsAny(a.getClassification(), DPProcessParamAttributes.OCN.getValue(), DPProcessParamAttributes.NRZ.getValue(),
                    DPProcessParamAttributes.PHH.getValue()) && StringUtils
                    .equalsAny(a.getExclusionReason(), DPProcessFilterParams.PMI.getValue(), DPProcessFilterParams.SPECIAL_SERVICE.getValue(),
                            DPProcessFilterParams.STATE_LAW.getValue().replace("#", "State :PR"))) {
                exclusionBooleanMap.put(a.getPropTemp(), Boolean.TRUE);
            } else if (StringUtils.equalsAny(a.getClassification(), DPProcessParamAttributes.OCN.getValue(), DPProcessParamAttributes.PHH.getValue())
                    && StringUtils.equalsAny(a.getExclusionReason(), DPProcessFilterParams.WEEK_ZERO_NOT_RUN.getValue(),
                    DPProcessFilterParams.ASSIGNMENT_BENCHMARK.getValue(), DPProcessFilterParams.PAST_12_CYCLES_EXCLUSION.getValue())) {
                exclusionBooleanMap.put(a.getPropTemp(), Boolean.TRUE);
            } else {
                exclusionBooleanMap.put(a.getPropTemp(), Boolean.FALSE);
            }
        });

        log.info("SOP weekn exclusionBooleanMap value : {}", exclusionBooleanMap);

        nonReductionQAReportInfoList.stream().forEach(nrInfo -> {
            //if(StringUtils.isBlank(exclusionMap.get(c.getSelrPropIdVcNn()))) {
            if (BooleanUtils.isTrue(exclusionBooleanMap.get(nrInfo.getSelrPropIdVcNn()))) {
                nrInfo.setStatus(true);
                nrInfo.setWeeknExclusionReason(exclusionMap.get(nrInfo.getSelrPropIdVcNn()));
                nrInfo.setWeeknMissingreport(Boolean.FALSE.toString());
            } else {
                nrInfo.setStatus(false);
                nrInfo.setWeeknMissingreport(Boolean.TRUE.toString());
				/*if (StringUtils.isEmpty(c.getWeeknExclusionReason())) {
					c.setWeeknExclusionReason(exclusionMap.get(c.getSelrPropIdVcNn()));
				}*/
            }
        });
    }

}