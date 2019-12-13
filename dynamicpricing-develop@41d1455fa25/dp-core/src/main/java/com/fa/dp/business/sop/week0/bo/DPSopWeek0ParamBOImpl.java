package com.fa.dp.business.sop.week0.bo;

import com.fa.dp.business.sop.week0.dao.DPSopWeek0ParamsDao;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ParamInfo;
import com.fa.dp.business.sop.week0.input.mapper.DPSopWeek0ParamMapper;
import com.fa.dp.business.sop.weekN.entity.DPSopWeekNParam;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.week0.report.info.DPWeek0ReportInfo;
import com.fa.dp.business.week0.report.sop.mapper.DPWeek0SopReportMapper;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
@Slf4j
public class DPSopWeek0ParamBOImpl implements DPSopWeek0ParamBO {

	@Inject
	private DPSopWeek0ParamsDao dpSopWeek0ParamsDao;

	@Inject
	private DPWeek0SopReportMapper dpWeek0SopReportMapper;

	@Inject
	private DPSopWeek0ParamMapper sopWeek0ParamMapper;

	@Override
	public List<DPWeek0ReportInfo> fetchWeek0Report(Long startDate, Long endDate, List<String> clientCode) {
		List<DPSopWeek0Param> reports = dpSopWeek0ParamsDao.findWeek0Report(startDate, endDate, clientCode);
		return dpWeek0SopReportMapper.mapDomainToLinfoList(reports);
	}

	@Override
	public List<DPSopWeek0ParamInfo> fetchSopWeek0ParamsRA(String assetNumber, String eligible) throws SystemException {
		List<DPSopWeek0ParamInfo> sopWeek0ParamInfoList = null;
		try {
			List<DPSopWeek0Param> sopWeek0ParamList = dpSopWeek0ParamsDao.findByAssetNumberAndEligibleOrderByCreatedDateDesc(assetNumber, eligible);
			sopWeek0ParamInfoList = sopWeek0ParamMapper.mapDomainToInfoList(sopWeek0ParamList);
		} catch (Exception e) {
			log.error("fetchSopWeek0ParamsRA failure.", e);
			SystemException.newSystemException(CoreExceptionCodes.DPWKN1003);
		}
		return sopWeek0ParamInfoList;
	}
}
