package com.fa.dp.rest;

import static java.lang.String.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import com.fa.dp.business.weekn.bo.DPWeekNBOUtil;
import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fa.dp.business.command.master.CommandMaster;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.weekn.delegate.WeekNDataDelegate;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.systemparam.util.AppType;
import com.fa.dp.core.util.RAClientConstants;
import com.fa.dp.rest.response.RestResponse;

@Controller
@Slf4j
public class WeekNController {

    @Inject
    private WeekNDataDelegate weekNDataDelegate;

    @Value("${weekn.start.date}")
    private String weeknStartDate;
    
    @Inject
    @Named("dpCommandMaster")
    private CommandMaster commandMaster;

    @Inject
    private DPWeekNBOUtil dpWeekNBOUtil;

    @RequestMapping(value = "/fetchWeekNData", method = RequestMethod.GET)
    @ResponseBody
     public RestResponse<DPProcessWeekNParamEntryInfo> fetchWeeknDataFromHubzu(Long selectedDateMillis) {
         RestResponse<DPProcessWeekNParamEntryInfo> response = new RestResponse<>();
         try{
             MDC.put(RAClientConstants.APP_CODE, AppType.DPA.getAppCode());
             MDC.put(RAClientConstants.PRODUCT_TYPE, DPAConstants.WEEKN);

             DPProcessWeekNParamEntryInfo dpProcessParamEntryInfo = weekNDataDelegate.fetchInitialWeekNDataFromHubzu(selectedDateMillis);
           //calling the weekn flow for step1
             dpProcessParamEntryInfo.setFetchProcess(Boolean.TRUE);
             commandMaster.prepareWeekN(dpProcessParamEntryInfo);
             response.setSuccess(Boolean.TRUE);
             response.setResponse(dpProcessParamEntryInfo);
         } catch(SystemException se) {
             log.error("System exception Exception occurred while fetching the data from hubzu : ");
             response.setSuccess(Boolean.FALSE);
             response.setMessage(se.getLocalizedMessage());
             response.setErrorCode(se.getCode());
         }catch (Exception e){
             log.error("Exception occurred while retrieving  data from hubzu : ",e);
             response.setSuccess(Boolean.FALSE);
             response.setMessage("Exception occurred while retrieving data from hubzu");
         } finally{
             MDC.remove(RAClientConstants.APP_CODE);
             MDC.remove(RAClientConstants.PRODUCT_TYPE);
         }

         return response;
     }

     @PostMapping(value = "/downloadWeekNData")
     @ResponseBody
     public void downloadWeeknDataFromHubzu(@RequestBody DPProcessWeekNParamEntryInfo dpProcessParamEntryInfo, @RequestParam Long userSelectedDate, HttpServletResponse response) {
        log.info("Download Week n step 1 begin");
        try{
        	MDC.put(RAClientConstants.APP_CODE, AppType.DPA.getAppCode());
        	MDC.put(RAClientConstants.PRODUCT_TYPE, DPAConstants.WEEKN);
        	weekNDataDelegate.downloadWeekNDataFromHubzu(dpProcessParamEntryInfo,userSelectedDate, response);
        } catch(SystemException se) {
             log.error("System exception Exception occurred while downloading excel file. : ",se);
                if (se.getLocalizedMessage() != null) {
                    dpWeekNBOUtil.writeErrorData(response, se.getLocalizedMessage());
                } else {
                    dpWeekNBOUtil.writeErrorData(response, se.getMessage());
                }
         }catch (Exception e) {
            log.error("Exception occurred while downloading excel file. :" , e);
            dpWeekNBOUtil.writeErrorData(response, DPAConstants.UNABLE_TO_DOWNLOAD_FILE);
        } finally {
            try {
                if (response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error(DPAConstants.OUTPUTSTREAM_ERROR  , e);
            }
            MDC.remove(RAClientConstants.APP_CODE);
            MDC.remove(RAClientConstants.PRODUCT_TYPE);
        }
         log.info("Download Week n step 1 end");
     }

    @PostMapping(value = "/uploadWeekNExcel")
    @ResponseBody
    public RestResponse<List<DPProcessWeekNParamInfo>> uploadWeekNExcelFromHubzu(@RequestParam(value = "file") MultipartFile file) {
        log.info("Upload Week n downloaded excel file  begin");
        MDC.put(RAClientConstants.APP_CODE, AppType.DPA.getAppCode());
        MDC.put(RAClientConstants.PRODUCT_TYPE, DPAConstants.WEEKN);
        RestResponse<List<DPProcessWeekNParamInfo>> response = new RestResponse<>();
        List<DPProcessWeekNParamInfo> listOfDPProcessWeekNParamInfos = new ArrayList<>();
        List<DPProcessWeekNParamInfo> savedRecords = null;
        Long startTime;
        String successMessage = null;
        try {
            startTime = DateTime.now().getMillis();
            listOfDPProcessWeekNParamInfos = weekNDataDelegate.getDPProcessWeekNParams(file);
            savedRecords = weekNDataDelegate.uploadWeekNExcel(file.getOriginalFilename(), listOfDPProcessWeekNParamInfos);
            log.info("Time taken for week n file upload : " + (DateTime.now().getMillis() - startTime) + " millis");
            successMessage = "Save Successful, " + savedRecords.size() + " records inserted";
            response.setSuccess(true);
            response.setMessage(successMessage);
            response.setResponse(savedRecords);
        }catch(SystemException se) {
            log.error("Exception caught while reading the file." , se);
            response.setSuccess(false);
            response.setErrorCode(CoreExceptionCodes.DPWKN0110);
            response.setMessage(se.getLocalizedMessage());
        }catch(Exception e) {
            log.error("Exception caught while reading the file." , e);
            response.setSuccess(false);
            response.setErrorCode(CoreExceptionCodes.DPWKN0110);
            response.setMessage(e.getLocalizedMessage());
        }finally{
            MDC.remove(RAClientConstants.APP_CODE);
            MDC.remove(RAClientConstants.PRODUCT_TYPE);
        }
        log.info("Upload Week n downloaded excel file  ends");
        return response;
    }

    /**
     * getWeekNOCNNRZDownload
     *
     * @throws IOException
     * @throws Exception
     */
    @GetMapping(value = "/getWeekNDownload")
    @ResponseBody
    public void getWeekNOCNNRZDownload(String Id, String type, HttpServletResponse response) {
        log.info("Week N download Report controller begins");
        MDC.put(RAClientConstants.APP_CODE, AppType.DPA.getAppCode());
        MDC.put(RAClientConstants.PRODUCT_TYPE, DPAConstants.WEEKN);
        try {
            weekNDataDelegate.downloadWeekNReports(Id, type, response, null);
            log.info("Download report execution success.");
        } catch (SystemException se) {
            log.error("SystemException occurred while downloading File", se);
            dpWeekNBOUtil.writeErrorData(response, se.getLocalizedMessage());
        } catch (Exception e) {
            log.error("Exception occurred while downloading File" , e);
            if (e.getLocalizedMessage() != null) {
                dpWeekNBOUtil.writeErrorData(response, DPAConstants.UNABLE_TO_DOWNLOAD_FILE);
            } else {
                dpWeekNBOUtil.writeErrorData(response,  DPAConstants.UNABLE_TO_DOWNLOAD_FILE);
            }
        } finally {
            try {
                MDC.remove(RAClientConstants.APP_CODE);
                MDC.remove(RAClientConstants.PRODUCT_TYPE);
                if (response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error(DPAConstants.OUTPUTSTREAM_ERROR + Id , e);
            }
        }
        log.info("Week N download Report controller ends");
    }
}
