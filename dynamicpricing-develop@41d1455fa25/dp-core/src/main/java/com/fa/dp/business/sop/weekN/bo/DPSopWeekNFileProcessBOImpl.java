package com.fa.dp.business.sop.weekN.bo;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

import com.fa.dp.business.sop.week0.dao.DPSopWeek0ParamsDao;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;
import com.fa.dp.business.sop.weekN.dao.DPSopWeekNProcessStatusDao;
import com.fa.dp.business.sop.weekN.entity.DPSopWeekNProcessStatus;
import com.fa.dp.core.exception.SystemException;

@Named
@Slf4j
public class DPSopWeekNFileProcessBOImpl implements DPSopWeekNFileProcessBO {

	@Inject
	private DPSopWeekNProcessStatusDao dpSopWeekNProcessStatusDao;
	
	@Inject
	private DPSopWeek0ParamsDao dPSopWeek0ParamsDao;

	/**
	 * @param id
	 *
	 * @return
	 */
	@Override
	public DPSopWeekNProcessStatus findSopWeekNProcessById(String id)  throws SystemException {
		Optional<DPSopWeekNProcessStatus> obj = dpSopWeekNProcessStatusDao.findById(id);
		return obj.get();
	}
	
	@Override
	public Map<String, DPSopWeek0Param> findLatestNonDuplicateInSopWeek0ForAsset(List<String> assetNumbers) {
		List<DPSopWeek0Param> processParamList = dPSopWeek0ParamsDao.findLatestNonDuplicateInSopWeek0ForAsset(new HashSet<String> (assetNumbers));
		return processParamList.stream().collect(Collectors.toMap(DPSopWeek0Param::getAssetNumber, c -> c));
	}

}
