package com.fa.dp.business.sop.weekN.delegate;

import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.validator.bo.DPDashboardParamInfo;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.business.week0.info.DashboardFilterInfo;
import com.fa.dp.business.weekn.input.info.DPAssetDetails;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.exception.SystemException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author misprakh
 */
public interface DPSopWeekNFileProcessDelegate {
    /**
     *
     * @param sopWeekNParamEntryInfo
     * @return
     * @throws SystemException
     */
    String setSopWeekNFileStatus(DPSopWeekNParamEntryInfo sopWeekNParamEntryInfo) throws SystemException;
}
