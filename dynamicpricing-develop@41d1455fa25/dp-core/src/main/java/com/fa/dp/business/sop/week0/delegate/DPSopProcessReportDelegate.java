package com.fa.dp.business.sop.week0.delegate;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;
import com.fa.dp.core.exception.SystemException;

/**
 * @author misprakh
 */

public interface DPSopProcessReportDelegate {

	/**
     * @param   fileId
     * @param   type
     * @param   response
     * @return void
     * @throws SystemException
     */
    void downloadSOPWeek0Reports(String fileId, String type,  HttpServletResponse response) throws SystemException;
    
    /**
     * @param fileId
     * @param fileType
     * @return List<DPProcessParam>
     * @throws SystemException
     */
    List<DPSopWeek0Param> fetchSOPWeek0FilesDetailsById(String fileId, String fileType) throws SystemException;
}
