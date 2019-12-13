package com.fa.dp.business.audit.delegate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.business.weekn.permanent.exclusion.report.info.DPPermanentExclusionReportInfo;
import com.fa.dp.business.weekn.permanent.exclusion.report.mapper.DPPermanentExclusionReportMapper;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;

import com.fa.dp.business.audit.bo.DPAuditReportBO;
import com.fa.dp.business.audit.entity.DPWeekNAuditReports;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.rr.migration.RRMigration;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.DateConversionUtil;

@Slf4j
@Named
public class DPAuditReportDelegateImpl implements DPAuditReportDelegate {

    @Inject
    private DPAuditReportBO dpAuditReportBO;

    @Inject
    private RRMigration rRMigration;

    @Inject
    private DPPermanentExclusionReportMapper dpPermanentExclusionReportMapper;

    /**
     * @param setOfAuditOddEntries
     * @param selectedDateMillis
     * @return void
     * @throws SystemException
     */
    @Override
    public void createWeekNAuditEntries(Map<String, String> setOfAuditOddEntries, Long selectedDateMillis, Map<String, String> migrationNewPropToPropMap, Map<String, String> migrationPropToLoanMap) throws SystemException {
//        List<DPWeekNAuditReports> listAuditReports = new ArrayList<>();
//        if(CollectionUtils.isNotEmpty(setOfAuditOddEntries.entrySet())) {
//	        setOfAuditOddEntries.entrySet().stream().forEach(oddEntries -> {
//                    DPWeekNAuditReports dpWeekNAuditReports = new DPWeekNAuditReports();
//                    dpWeekNAuditReports.setRunDate(selectedDateMillis);
//                    dpWeekNAuditReports.setLoanNumber(migrationPropToLoanMap.get(oddEntries.getKey()));
//                    dpWeekNAuditReports.setPropTemp(oddEntries.getKey());
//                    dpWeekNAuditReports.setClassification(oddEntries.getValue());
//	                if(migrationNewPropToPropMap.containsKey(oddEntries.getKey()))
//		                dpWeekNAuditReports.setOldLoanNumber(migrationPropToLoanMap.get(migrationNewPropToPropMap.get(oddEntries.getKey())));
//                    dpWeekNAuditReports.setDeliveryDate(DateConversionUtil.getCurrentEstDate().withTimeAtStartOfDay().getMillis());
//                    dpWeekNAuditReports.setAction("Odd Cycle");
//                    dpWeekNAuditReports.setPermanentExclusion(Boolean.FALSE);
//                    listAuditReports.add(dpWeekNAuditReports);
//                });
//        }
//
//        dpAuditReportBO.saveAuditReport(listAuditReports);

    }

    /**
     * @param failedEntries
     * @return void
     * @throws SystemException
     */
    @Override
    public void createFailedEntriesInAudit(List<DPProcessWeekNParamInfo> failedEntries, Long selectedDateMillis) throws SystemException {
        List<DPWeekNAuditReports> listAuditReports = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(failedEntries)) {
            for (DPProcessWeekNParamInfo exclusionList : failedEntries) {
                DPWeekNAuditReports dpWeekNAuditReports = new DPWeekNAuditReports();
                dpWeekNAuditReports.setRunDate(selectedDateMillis != null ? selectedDateMillis : getLastMillisecondOfDay());
                dpWeekNAuditReports.setLoanNumber(exclusionList.getAssetNumber());
                dpWeekNAuditReports.setOldLoanNumber(exclusionList.getOldAssetNumber());
                dpWeekNAuditReports.setPropTemp(exclusionList.getPropTemp());
                dpWeekNAuditReports.setDeliveryDate(DateConversionUtil.getCurrentEstDate().withTimeAtStartOfDay().getMillis());
                if (exclusionList.getExclusionReason().equalsIgnoreCase(DPProcessFilterParams.PMI.getValue()) || exclusionList.getExclusionReason().equalsIgnoreCase(DPProcessFilterParams.SPECIAL_SERVICE.getValue()) ||
                        exclusionList.getExclusionReason().equalsIgnoreCase(DPProcessFilterParams.ASSIGNMENT_BENCHMARK.getValue()) ||
                        exclusionList.getExclusionReason().equalsIgnoreCase(DPProcessFilterParams.PAST_12_CYCLES_EXCLUSION.getValue()) ||
                        exclusionList.getExclusionReason().contains(DPProcessFilterParams.STATE_LAW.getValue()) ||
                        exclusionList.getExclusionReason().contains(DPProcessFilterParams.WEEK_ZERO_NOT_RUN.getValue()) ||
                        exclusionList.getExclusionReason().contains(DPProcessFilterParams.ACTIVE_LISTINGS_EXCLUSION.getValue())) {
                    dpWeekNAuditReports.setAction(exclusionList.getExclusionReason());
                    dpWeekNAuditReports.setPermanentExclusion(Boolean.TRUE);
                } else {
                    dpWeekNAuditReports.setAction(exclusionList.getExclusionReason());
                    dpWeekNAuditReports.setPermanentExclusion(Boolean.FALSE);
                }
                dpWeekNAuditReports.setClassification(exclusionList.getClassification());
                if (dpWeekNAuditReports.getPermanentExclusion() && (selectedDateMillis != null || !dpAuditReportBO.updateExclusionEntriesAuditReport(dpWeekNAuditReports))) {
                    listAuditReports.add(dpWeekNAuditReports);
                }
            }
            ;
        }
        dpAuditReportBO.saveAuditReport(listAuditReports);
    }

    /**
     * @param recommendedEntries
     * @return void
     * @throws SystemException
     */
    @Override
    public void createRecommendedEntriesInAudit(List<DPProcessWeekNParamInfo> recommendedEntries, Long selectedDateMillis) throws SystemException {
//        List<DPWeekNAuditReports> listAuditReports = new ArrayList<>();
//        if(CollectionUtils.isNotEmpty(recommendedEntries)){
//        	recommendedEntries.stream().forEach(recommendation -> {
//                DPWeekNAuditReports dpWeekNAuditReports = new DPWeekNAuditReports();
//                dpWeekNAuditReports.setRunDate(selectedDateMillis != null ? selectedDateMillis : getLastMillisecondOfDay());
//                dpWeekNAuditReports.setLoanNumber(recommendation.getAssetNumber());
//                dpWeekNAuditReports.setOldLoanNumber(recommendation.getOldAssetNumber());
//                dpWeekNAuditReports.setPropTemp(recommendation.getPropTemp());
//                dpWeekNAuditReports.setDeliveryDate(selectedDateMillis != null ? null : recommendation.getDeliveryDate());
//                dpWeekNAuditReports.setAction("Recommendation");
//                dpWeekNAuditReports.setPermanentExclusion(Boolean.FALSE);
//                dpWeekNAuditReports.setClassification(recommendation.getClassification());
//                listAuditReports.add(dpWeekNAuditReports);
//            });
//        }
//        dpAuditReportBO.saveAuditReport(listAuditReports);
    }

    /**
     * @param priorRecommendedEntries
     * @return void
     * @throws SystemException
     */
    @Override
    public void createPriorRecommendedEntriesInAudit(List<DPProcessWeekNParamInfo> priorRecommendedEntries, Long selectedDateMillis) throws SystemException {
//        List<DPWeekNAuditReports> listAuditReports = new ArrayList<>();
//        if (CollectionUtils.isNotEmpty(priorRecommendedEntries)) {
//            for (DPProcessWeekNParamInfo priorRecommendation : priorRecommendedEntries) {
//                if (!dpAuditReportBO.updatePriorRecommendationAuditReport(priorRecommendation, selectedDateMillis)) {
//                    DPWeekNAuditReports dpWeekNAuditReports = new DPWeekNAuditReports();
//                    dpWeekNAuditReports.setRunDate(selectedDateMillis);
//                    dpWeekNAuditReports.setLoanNumber(priorRecommendation.getAssetNumber());
//                    dpWeekNAuditReports.setOldLoanNumber(priorRecommendation.getOldAssetNumber());
//                    dpWeekNAuditReports.setPropTemp(priorRecommendation.getPropTemp());
//                    dpWeekNAuditReports.setDeliveryDate(priorRecommendation.getDeliveryDate());
//                    dpWeekNAuditReports.setAction("Old Recommendation");
//                    dpWeekNAuditReports.setPermanentExclusion(Boolean.FALSE);
//                    dpWeekNAuditReports.setClassification(priorRecommendation.getClassification());
//                    listAuditReports.add(dpWeekNAuditReports);
//                }
//            }
//            ;
//        }
//        if (CollectionUtils.isNotEmpty(listAuditReports)) {
//            dpAuditReportBO.saveAuditReport(listAuditReports);
//        }
    }

    @Override
    public List<DPPermanentExclusionReportInfo> findPermanentExclusionList(List<String> clasifications) throws SystemException {
        List<DPWeekNAuditReports> reports = null;
        try {
            reports = dpAuditReportBO.findPermanentExclusionList(clasifications);
        } catch (Exception e) {
            log.error("Permanent exclusion report failure. {}", e);
            SystemException.newSystemException(CoreExceptionCodes.DPRPRT00001);
        }
        return dpPermanentExclusionReportMapper.mapDomainToLinfoList(reports);
    }

    /**
     * @param successUnderReviewEntries
     * @return void
     * @throws SystemException
     */
    @Override
    public void createSucessfulUnderreviewEntriesInAudit(List<DPProcessWeekNParamInfo> successUnderReviewEntries) throws SystemException {
//        List<DPWeekNAuditReports> listAuditReports = new ArrayList<>();
//        if (CollectionUtils.isNotEmpty(successUnderReviewEntries)) {
//            successUnderReviewEntries.stream().forEach(successUnderReview -> {
//                DPWeekNAuditReports dpWeekNAuditReports = new DPWeekNAuditReports();
//                dpWeekNAuditReports.setRunDate(getLastMillisecondOfDay());
//                dpWeekNAuditReports.setLoanNumber(successUnderReview.getAssetNumber());
//                dpWeekNAuditReports.setOldLoanNumber(successUnderReview.getOldAssetNumber());
//                dpWeekNAuditReports.setDeliveryDate(successUnderReview.getDeliveryDate());
//                dpWeekNAuditReports.setAction("Successful / Underreview");
//                dpWeekNAuditReports.setPermanentExclusion(Boolean.FALSE);
//                dpWeekNAuditReports.setClassification(successUnderReview.getClassification());
//                listAuditReports.add(dpWeekNAuditReports);
//            });
//        }
//        dpAuditReportBO.saveAuditReport(listAuditReports);
    }

    private Long getLastMillisecondOfDay() {
        return DateConversionUtil.getCurrentUTCTime().withTimeAtStartOfDay().getMillis() + (86400 * 1000 - 1);
    }

}
