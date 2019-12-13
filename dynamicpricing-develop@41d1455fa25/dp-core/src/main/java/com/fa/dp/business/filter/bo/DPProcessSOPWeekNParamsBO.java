package com.fa.dp.business.filter.bo;

import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface DPProcessSOPWeekNParamsBO {

    /**
     * Fetch reduction data from weekn param
     * @param rbidPropIdVcNn
     * @param oldLoanNumber
     * @param currentListEndDate
     * @return
     */
    DPProcessWeekNParamInfo checkReduction(String rbidPropIdVcNn, String oldLoanNumber, LocalDate currentListEndDate);

    /**
     * Fetch latest non duplicate number from week0 for asset numbers
     *
     * @param assetFromHbz
     * @return
     */
    List<DPSopWeek0Param> findLatestNonDuplicateInSopWeek0ForAsset(Set<String> assetFromHbz);
}
