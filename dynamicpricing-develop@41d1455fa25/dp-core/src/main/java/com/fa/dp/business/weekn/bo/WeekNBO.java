package com.fa.dp.business.weekn.bo;

import com.fa.dp.business.weekn.entity.DPProcessWeekNParam;
import com.fa.dp.business.weekn.entity.DPWeekNProcessStatus;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.exception.SystemException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @author misprakh
 * <p>
 * DP process status BO interface
 */
public interface WeekNBO {

	/**
	 * @param dpProcessParamEntryInfo
	 * @param response
     * @return
     * create Week N Excel for Step 1
	 */
	public void createWeekNExcel(DPProcessWeekNParamEntryInfo dpProcessParamEntryInfo, Long userSelectedDate, HttpServletResponse response) throws SystemException, ParseException;

	/**
	 * @param fileStatus
     * @return
     * find DPFileProcessStatus By Status
	 */
	public DPWeekNProcessStatus findDPFileProcessStatusByStatus(String fileStatus);

	/**
	 * @param file
     * @return
     * get Sheets From WeekN Uploaded Excel
	 */
	public List<DPProcessWeekNParamInfo> getSheetsFromWeekNUploadedExcel(MultipartFile file) throws IOException, SystemException;

	List<DPProcessWeekNParam> fetchWeekNFilesDetailsById(String id) throws IOException, SystemException;

	void generateWeekNOutputFile(Map<String, List<DPProcessWeekNParam>> allLists, HttpServletResponse response, String zipFileName, String sysGnrtdInputFileName, String type) throws SystemException;

	List<DPProcessWeekNParam> findAllWeekNParams() throws SystemException;

	boolean findWeekNReports(String sysGnrtdInputFileName, String zipFileName, HttpServletResponse response);

	/**
	 * @param
	 * * @return Most Recent List End Date
	 */
	public String findFirstListEndDate() throws SystemException, ParseException;
}
