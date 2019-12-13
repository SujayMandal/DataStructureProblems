package com.fa.dp.business.sop.week0.delegate;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.sop.week0.bo.DPSopProcessBO;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;

/**
 * @author misprakh
 */

@Slf4j
@Named
public class DPSopProcessReportDelegateImpl implements DPSopProcessReportDelegate {
	
	@Inject
	private DPSopProcessBO dpSopProcessBO;
	
	@Override
	public void downloadSOPWeek0Reports(String fileId, String type,
			HttpServletResponse httpResponse) throws SystemException {
		List<DPSopWeek0Param> listOfDPSOPWeek0ProcessParamOCN = new ArrayList<>();
		List<DPSopWeek0Param> listOfDPSOPWeek0ProcessParamPHH = new ArrayList<>();
		List<DPSopWeek0Param> listOfDPSOPWeek0ProcessParamNRZ = new ArrayList<>();
		String zipFileName = "";
		List<DPSopWeek0Param> listOfDPSOPWeek0ProcessParam = fetchSOPWeek0FilesDetailsById(fileId, type);

		if (CollectionUtils.isNotEmpty(listOfDPSOPWeek0ProcessParam) && !ObjectUtils.isEmpty(listOfDPSOPWeek0ProcessParam.get(0).getSopWeek0ProcessStatus())) {
			zipFileName += listOfDPSOPWeek0ProcessParam.get(0).getSopWeek0ProcessStatus().getSysGnrtdInputFileName();
		} else{
			log.error("SOP Week 0 details are not available for requested fileId - {}" , fileId);
			throw new SystemException(CoreExceptionCodes.DPSOPWK0013, fileId);
		}

		listOfDPSOPWeek0ProcessParam.stream().forEach(info -> {
			log.debug("adding SOP Week 0 process param for classification : {}, asset number : {}", info.getClassification(), info.getAssetNumber());
			if (StringUtils.equalsIgnoreCase(info.getClassification(), DPProcessParamAttributes.OCN.getValue())) {
				listOfDPSOPWeek0ProcessParamOCN.add(info);
			}
			if (StringUtils.equalsIgnoreCase(info.getClassification(), DPProcessParamAttributes.PHH.getValue())) {
				listOfDPSOPWeek0ProcessParamPHH.add(info);
			}
			if (StringUtils.equalsIgnoreCase(info.getClassification(), DPProcessParamAttributes.NRZ.getValue())) {
				listOfDPSOPWeek0ProcessParamNRZ.add(info);
			}
		});

		log.debug("SOP Week 0 OCN list count : {}, PHH list count : {}, NRZ list count : {}", listOfDPSOPWeek0ProcessParamOCN.size(), listOfDPSOPWeek0ProcessParamPHH.size(),
				listOfDPSOPWeek0ProcessParamNRZ.size());

		// For OCN, NRZ and PHH File
		if (CollectionUtils.isNotEmpty(listOfDPSOPWeek0ProcessParamOCN) || CollectionUtils.isNotEmpty(listOfDPSOPWeek0ProcessParamNRZ) || CollectionUtils
				.isNotEmpty(listOfDPSOPWeek0ProcessParamPHH)) {
			dpSopProcessBO.generateSOPWeek0OutputFile(listOfDPSOPWeek0ProcessParamOCN, listOfDPSOPWeek0ProcessParamNRZ, listOfDPSOPWeek0ProcessParamPHH, httpResponse, zipFileName);

		}
	}

	@Override
	public List<DPSopWeek0Param> fetchSOPWeek0FilesDetailsById(String fileId,
			String fileType) throws SystemException {
		List<DPSopWeek0Param> listOfDPSOPWeek0ProcessParams = new ArrayList<>();
		if (fileType.equalsIgnoreCase(DPAConstants.SOP_WEEK0)) {
			listOfDPSOPWeek0ProcessParams = dpSopProcessBO.findDPSOPWeek0ProcessParamByProcessID(fileId);
			if (listOfDPSOPWeek0ProcessParams.isEmpty()) {
				throw new SystemException(CoreExceptionCodes.DP020);
			}
		}
		return listOfDPSOPWeek0ProcessParams;
	}
	
}
