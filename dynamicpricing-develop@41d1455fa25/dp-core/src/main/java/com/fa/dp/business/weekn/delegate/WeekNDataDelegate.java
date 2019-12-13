package com.fa.dp.business.weekn.delegate;

import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.exception.SystemException;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface WeekNDataDelegate {

    public DPProcessWeekNParamEntryInfo fetchInitialWeekNDataFromHubzu(Long selectedDateMillis) throws SystemException, ParseException;

    public void downloadWeekNDataFromHubzu(DPProcessWeekNParamEntryInfo dpProcessParamEntryInfo, Long userSelectedDate, HttpServletResponse response) throws SystemException, IOException, ParseException;

    public List<DPProcessWeekNParamInfo> getDPProcessWeekNParams(MultipartFile file) throws SystemException, IOException;

    public List<DPProcessWeekNParamInfo> uploadWeekNExcel(String originalFilename, List<DPProcessWeekNParamInfo> listOfDPProcessWeekNParamInfos) throws SystemException;

    void downloadWeekNReports(String id, String type, HttpServletResponse response, DPProcessWeekNParamEntryInfo dpProcessWeekNParamEntryInfo) throws SystemException, IOException;
}
