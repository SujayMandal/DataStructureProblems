package com.fa.dp.business.week0.delegate;

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
public interface DPFileProcessDelegate {

    /**
     * @param   fileType
     * @return List<DPDashboardParamInfo>
     * @throws SystemException
     */
    List<DPDashboardParamInfo> getDashboardDetails(String fileType) throws SystemException;

    /**
     * @param   fileId
     * @param   type
     * @param   response
     * @return void
     * @throws SystemException
     */
    void downloadReports(String fileId, String type,  HttpServletResponse response) throws SystemException;

    /**
     * @param   fileId
     * @param   type
     * @return List<DPProcessParamInfo>
     * @throws SystemException
     */
    List<DPProcessParamInfo> getAssetDetails(String fileId, String type) throws SystemException;

    /**
     * @param infoObject
     * @return String
     * @throws SystemException
     */
    String setStatus(DPProcessParamEntryInfo infoObject) throws SystemException;

    /**
     * @param fileId
     * @param fileType
     * @return List<DPProcessParam>
     * @throws SystemException
     */
    List<DPProcessParam> fetchFilesDetailsById(String fileId, String fileType) throws SystemException;

    /**
     * @param   dashboardFilterInfo
     * @return List<DPDashboardParamInfo>
     * @throws SystemException
     */
    List<DPDashboardParamInfo> getFilteredDashboardDetails(DashboardFilterInfo dashboardFilterInfo) throws SystemException;

    /**
     * @param   weekNId
     * @param   weekType
     * @return List<DPProcessWeekNParamInfo>
     * @throws SystemException
     */
    List<DPProcessWeekNParamInfo> getWeekNAssetDetails(String weekNId, String weekType) throws SystemException;

    /**
     * @param   dpProcessWeekNParamEntryInfo
     * @return String
     * @throws SystemException
     */
    String setWeeknStatus(DPProcessWeekNParamEntryInfo dpProcessWeekNParamEntryInfo) throws SystemException;

    /**
     * @param   assetNumber
     * @param   occupancy
     * @return List<DPAssetDetails>
     * @throws SystemException
     */
    List<DPAssetDetails> searchAssetDetails(String assetNumber, String occupancy) throws SystemException;

    /**
     * @param   assetNumber
     * @param   occupancy
     * @param   reason
     * @return DPAssetDetails
     * @throws SystemException
     */
    DPAssetDetails removeLoanFromDP(String assetNumber, String occupancy, String reason) throws SystemException;
}
