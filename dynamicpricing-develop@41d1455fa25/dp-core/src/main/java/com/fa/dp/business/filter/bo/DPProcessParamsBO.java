package com.fa.dp.business.filter.bo;

import java.util.List;
import java.util.Set;

import com.fa.dp.business.ssinvestor.entity.SpclServicingInvestor;
import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.business.week0.report.info.DPWeek0ReportInfo;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.KeyValue;

public interface DPProcessParamsBO {

    DPProcessParam saveDPProcessParam(DPProcessParam dpProcessParam);

    DPProcessParamInfo saveDPProcessParamInfo(DPProcessParamInfo dpProcessParamInfo);

    List<DPProcessParam> saveDPProcessParams(List<DPProcessParam> dpProcessParam);

    List<DPProcessParamInfo> saveDPProcessParamInfos(List<DPProcessParamInfo> dpProcessParamInfoList);

    KeyValue<List<DPProcessParamInfo>, List<DPProcessParamInfo>> filterOnInvestorCode(
            List<SpclServicingInvestor> aspsClientInfos, DPProcessParamEntryInfo inputParamEntry);

    DPProcessParamInfo filterOnDuplicates(DPProcessParamInfo columnEntry, List<DPProcessParamInfo> dpProcessParams);

    List<DPProcessParam> searchByAssetNumber(String assetNumber);

    List<DPProcessParam> findByAssetNumberAndClassification(String assetNumber, String classification);

    KeyValue<List<DPProcessParamInfo>, List<DPProcessParamInfo>> filterOnPropertyType(
            DPProcessParamEntryInfo inputParamEntry);

    KeyValue<List<DPProcessParamInfo>, List<DPProcessParamInfo>> filterOnAssetValue(
            DPProcessParamEntryInfo inputParamEntry);

    DPProcessParam findInWeek0ForAssetNumber(String selrPropIdVcNn) throws SystemException;

    /**
     * @param assetNumber
     * @return DPProcessParam
     * @throws SystemException
     */
    DPProcessParam findOcwenLoanBYAssetNumber(String assetNumber) throws SystemException;

    /**
     * Fetch latest non duplicate number from week0 for asset numbers
     *
     * @param assetFromHbz
     * @return
     */
    List<DPProcessParam> findLatestNonDuplicateInWeek0ForGivenAsset(Set<String> assetFromHbz);

    /**
     * Fetch vacant week0 data for given client code and assignment date between start date and endd date.
     * @param startDate
     * @param endDate
     * @param clientCode
     * @return
     */
    List<DPWeek0ReportInfo> fetchWeek0Report(Long startDate, Long endDate, List<String> clientCode);

    DPProcessParam findOutOfScopeLoanByAssetNumber(String assetNumber) throws SystemException;

}
