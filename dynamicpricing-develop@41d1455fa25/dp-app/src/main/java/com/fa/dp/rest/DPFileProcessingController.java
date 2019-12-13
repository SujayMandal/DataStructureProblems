package com.fa.dp.rest;

import static java.lang.String.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.validator.bo.DPDashboardParamInfo;
import com.fa.dp.business.week0.delegate.DPFileProcessDelegate;
import com.fa.dp.business.week0.info.DashboardFilterInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.rest.response.RestResponse;

/**
 * @author misprakh
 */

@Slf4j
@Controller
public class DPFileProcessingController {
    @Inject
    private DPFileProcessDelegate dpFileProcessDelegate;

    /**
     * Mappings to getAssetDetails
     *
     * @throws SystemException
     * @throws Exception
     */
    @GetMapping(value = "/getDashboardDetails")
    @ResponseBody
    public RestResponse getDashboardDetails(String weekType) {
        log.info("Get All uploaded file controller begins");
        long startTime = System.currentTimeMillis();
        RestResponse<List<DPDashboardParamInfo>> response = new RestResponse<>();
        List<DPDashboardParamInfo> listOfDPDashboardParamInfo = new ArrayList<>();
        try {
            listOfDPDashboardParamInfo = dpFileProcessDelegate.getDashboardDetails(weekType);
            log.info("Dashboard API Successful.");
            response.setSuccess(true);
            response.setMessage("Dashboard API Successful");
            response.setErrorCode(null);
            response.setResponse(listOfDPDashboardParamInfo);
        } catch (SystemException se) {
            log.error("System Exception - Dashboard API  failed. " , se);
            response.setSuccess(false);
            response.setMessage("System Exception - Dashboard API  failed.");
            response.setErrorCode(se.getCode());
        } catch (Exception e) {
            log.error("Dashboard API execution failed. " , e);
            response.setSuccess(false);
            response.setMessage("Dashboard API  execution failed. ");
        }
        log.error("Time taken to prepare dashboard data is {} ", (System.currentTimeMillis() - startTime));
        log.info("Get All uploaded file controller ends");
        return response;
    }

    @RequestMapping(value = "/getFilteredDashboardDetails", method = RequestMethod.POST)
    @ResponseBody
    public RestResponse getFilteredDashboardDetails(@RequestBody(required = false) DashboardFilterInfo dashboardFilterInfo) {
        log.info("Get FilteredDashboardDetails controller begins");
        long startTime = System.currentTimeMillis();
        RestResponse<List<DPDashboardParamInfo>> response = new RestResponse<>();
        List<DPDashboardParamInfo> listOfDPDashboardParamInfo = new ArrayList<>();
        try {
            listOfDPDashboardParamInfo = dpFileProcessDelegate.getFilteredDashboardDetails(dashboardFilterInfo);
            log.info("FilteredDashboardDetails API Successful.");
            response.setSuccess(true);
            response.setMessage("FilteredDashboardDetails API Successful");
            response.setErrorCode(null);
            response.setResponse(listOfDPDashboardParamInfo);
        } catch (SystemException se) {
            log.error("System Exception - FilteredDashboardDetails  failed. ",se);
            response.setSuccess(false);
            response.setMessage(se.getLocalizedMessage());
            response.setErrorCode(se.getCode());
        } catch (Exception e) {
            log.error("FilteredDashboardDetails API execution failed. ", e);
            response.setSuccess(false);
            response.setMessage("Dashboard API  execution failed. ");
        }
        log.error("Time taken to prepare filtered dashboard data is {} ", (System.currentTimeMillis() - startTime));
        log.info("FilteredDashboardDetails controller ends");
        return response;
    }

    /**
     * Mappings to getAssetDetails
     *
     * @throws SystemException
     * @throws Exception
     */
    @GetMapping(value = "/getAssetDetails")
    @ResponseBody
    public RestResponse getAssetDetails(String fileId, String weekType) {
        log.info("Asset details  begins");
        RestResponse<List<DPProcessParamInfo>> response = new RestResponse<>();
        List<DPProcessParamInfo> listOfDPProcessParamInfo = new ArrayList<>();
        try {
            listOfDPProcessParamInfo = dpFileProcessDelegate.getAssetDetails(fileId, weekType);
            response.setSuccess(true);
            response.setMessage("Asset details  successful.");
            response.setErrorCode(null);
            response.setResponse(listOfDPProcessParamInfo);
            log.info("Asset details execution success.");
        } catch (SystemException se) {
            log.error("Week 0  Asset details  failed with exception : " , se);
            response.setSuccess(false);
            response.setMessage("System Exception - Week 0 Asset details  failed.");
            response.setErrorCode(se.getCode());
        } catch (Exception e) {
            log.error("Week 0 Asset details  failed with exception : " , e);
            response.setSuccess(false);
            response.setMessage("Asset details  failed with exception : " + e.getMessage());
        }
        log.info("Asset details controller ends");
        return response;
    }

    /**
     * Mappings to getReport
     *
     * @throws IOException
     * @throws Exception
     */
    @GetMapping(value = "/downloadReport")
    @ResponseBody
    public void getReport(String fileId, String type, HttpServletResponse response) {
        log.info("download Report controller begins");
        try {
            dpFileProcessDelegate.downloadReports(fileId, type, response);
            log.info("Download report execution success.");
        } catch (SystemException se) {
            log.error("Exception occurred while downloading File " ,se);
            if (se.getLocalizedMessage() != null) {
                writeErrorData(response, fileId, se.getLocalizedMessage());
            } else {
                writeErrorData(response, fileId, se.getMessage());
            }
        } catch (Exception e) {
            log.error("Exception occurred while downloading File " , e);
            if (e.getLocalizedMessage() != null) {
                writeErrorData(response, fileId, DPAConstants.UNABLE_TO_DOWNLOAD_FILE);
            } else {
                writeErrorData(response, fileId, DPAConstants.UNABLE_TO_DOWNLOAD_FILE);
            }
        } finally {
            try {
                if (response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error(DPAConstants.OUTPUTSTREAM_ERROR + fileId , e);
            }
        }
        log.info("download Report controller ends");
    }

    /**
     * Mappings to getAssetDetails
     *
     * @throws SystemException
     * @throws Exception
     */
    @GetMapping(value = "/getWeekNAssetDetails")
    @ResponseBody
    public RestResponse getWeekNAssetDetails(String weekNId, String WeekType) {
        log.info("Asset details  begins");
        RestResponse<List<DPProcessWeekNParamInfo>> response = new RestResponse<>();
        try {
            List<DPProcessWeekNParamInfo> listOfDPProcessParamInfo = dpFileProcessDelegate.getWeekNAssetDetails(weekNId, WeekType);
            response.setSuccess(true);
            response.setMessage("Asset details  successful.");
            response.setErrorCode(null);
            response.setResponse(listOfDPProcessParamInfo);
            log.info("Asset details execution success.");
        } catch (SystemException se) {
            log.error("System Exception - Asset details  failed. " , se);
            response.setSuccess(false);
            response.setMessage("System Exception - Asset details  failed.");
            response.setErrorCode(se.getCode());
        } catch (Exception e) {
            log.error("Asset details  failed with exception :" , e);
            response.setSuccess(false);
            response.setMessage("Asset details  failed with exception : " + e.getMessage());
        }
        log.info("Asset details controller ends");
        return response;
    }

    private void writeErrorData(final HttpServletResponse response, final String fileId, final String msg) {
        try {
            final String headerValue = format("attachment; filename=\"%s\"", "error_" + fileId + ".txt");
            response.setHeader("Content-Disposition", headerValue);
            response.setHeader("Access-Control-Expose-Headers","Content-Disposition");
            String errorMsg = null;
            if (msg == null) {
                errorMsg = "No Data found for the fileId :" + fileId;
                response.getOutputStream().write(errorMsg.getBytes());
            } else {
                errorMsg = msg;
                response.getOutputStream().write(errorMsg.getBytes());
            }
        } catch (IOException excep) {
            log.error("Error while Writing error data  ", excep);
        }

    }
}
