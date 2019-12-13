package com.fa.dp.business.filter.delegate;

import com.fa.dp.business.filter.bo.DPProcessParamsBO;
import com.fa.dp.business.ssinvestor.bo.SpclServicingInvestorBO;
import com.fa.dp.business.ssinvestor.entity.SpclServicingInvestor;
import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.core.base.delegate.AbstractDelegate;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.KeyValue;
import com.fa.dp.core.util.RAClientConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named
public class DPProcessFilterDelegateImpl extends AbstractDelegate implements DPProcessFilterDelegate {

	private static final Logger LOGGER = LoggerFactory.getLogger(DPProcessFilterDelegateImpl.class);
	@Inject
	private DPProcessParamsBO dpProcessParamsBo;
	@Inject
	private SpclServicingInvestorBO spclServicingInvestorBo;

	@Override
	public void filterOnDuplicates(DPProcessParamEntryInfo inputParamEntry) throws SystemException {
		if(CollectionUtils.isNotEmpty(inputParamEntry.getColumnEntries())) {
			LOGGER.info("Filtering on duplicate check started..");
			List<DPProcessParamInfo> successEntries = new ArrayList<>();

			for (DPProcessParamInfo columnEntry : inputParamEntry.getColumnEntries()) {
				MDC.put(RAClientConstants.LOAN_NUMBER, columnEntry.getAssetNumber());
				List<DPProcessParam> dpProcessParams = dpProcessParamsBo.searchByAssetNumber(columnEntry.getAssetNumber());

				DPProcessParamInfo filteredEntry = null;
				if(CollectionUtils.isNotEmpty(dpProcessParams)) {
					//TODO use mapstruct
					List<DPProcessParamInfo> dpProcessParamInfos = convertToList(dpProcessParams, DPProcessParamInfo.class);
					filteredEntry = dpProcessParamsBo.filterOnDuplicates(columnEntry, dpProcessParamInfos);
				}
				if(null != filteredEntry) {
					// saving record as ineligible to db
					LOGGER.info("Saving ineligible duplicate record to db ..");
					saveParam(filteredEntry);
				} else {
					// saving non filtered out column entry as it is to db
					DPProcessParamInfo infoWithId = new DPProcessParamInfo();
					//Check for reprocess status
					if(inputParamEntry.isReprocess()) {
						columnEntry.setCommand(null);
						columnEntry.setEligible(null);
						columnEntry.setErrorDetail(null);
						columnEntry.setAssignment(null);
						columnEntry.setAssignmentDate(null);
						columnEntry.setNotes(null);
						columnEntry.setErrorDetail(null);
						columnEntry.setRtSource(null);
						columnEntry.setState(null);
						columnEntry.setWeek0Price(null);
						columnEntry.setPctAV(null);
						columnEntry.setWithinBusinessRules(null);
						columnEntry.setEnsemble(null);
						columnEntry.setNotesRa(null);
						columnEntry.setPrMode(null);
						columnEntry.setRrResponse(null);
						columnEntry.setRtngResponse(null);
						columnEntry.setTimestamp(null);
						columnEntry.setMessage(null);
						columnEntry.setEstimated(null);
						columnEntry.setFsd(null);
						columnEntry.setGeneratedZip(null);
						columnEntry.setTimestampREO(null);
						columnEntry.setMessageREO(null);
						columnEntry.setEstimatedREO(null);
						columnEntry.setFsdREO(null);
						columnEntry.setGeneratedZipREO(null);
						columnEntry.setStartTime(null);
						columnEntry.setEndTime(null);
						DateTime date = new DateTime();
						columnEntry.setUpdateTimestamp(date.getMillis());
					}

					infoWithId = saveParam(columnEntry);
					successEntries.add(infoWithId);
				}
				MDC.remove(RAClientConstants.LOAN_NUMBER);
			}
			inputParamEntry.setColumnEntries(successEntries);
			LOGGER.info("Filtering on duplicate check ended.");
		}
	}

	@Override
	public void filterOnInvestorCode(DPProcessParamEntryInfo inputParamEntry) throws SystemException {
		if(CollectionUtils.isNotEmpty(inputParamEntry.getColumnEntries())) {
			LOGGER.info("Filtering on Investor code started..");
			// get all the active ASPS clients from db
			List<SpclServicingInvestor> aspsClients = spclServicingInvestorBo.findByActiveTrue();

			/*List<SpclServicingInvestorInfo> aspsClientInfos = convertToList(aspsClients,
					SpclServicingInvestorInfo.class);*/

			KeyValue<List<DPProcessParamInfo>, List<DPProcessParamInfo>> filteredParamsMap = dpProcessParamsBo
					.filterOnInvestorCode(aspsClients, inputParamEntry);

			// set successful records after filtering, back to input object
			inputParamEntry.setColumnEntries(filteredParamsMap.getKey());

			// save ineligible records to DP_PROCESS_PARAMS_WEEK0 table
			if(CollectionUtils.isNotEmpty(filteredParamsMap.getValue())) {
				LOGGER.info("Saving all ineligible 'investor code' entries to db..");
				saveParams(filteredParamsMap.getValue());
			}
			LOGGER.info("Filtering on Investor code ended..");
		}
	}

	private List<DPProcessParamInfo> saveParams(List<DPProcessParamInfo> recordsToSave) {
		List<DPProcessParamInfo> result = new ArrayList<>();
		for (DPProcessParamInfo info : recordsToSave) {
			DPProcessParam in = new DPProcessParam();
			in = convert(info, DPProcessParam.class);
			in = dpProcessParamsBo.saveDPProcessParam(in);
			info.setId(in.getId());
			result.add(info);
		}
		return result;
	}

	@Override
	public void filterOnPropertyType(DPProcessParamEntryInfo inputParamEntry) throws SystemException {
		if(null != inputParamEntry.getColumnEntries() && !inputParamEntry.getColumnEntries().isEmpty()) {
			LOGGER.info("Filtering on Property type started..");
			KeyValue<List<DPProcessParamInfo>, List<DPProcessParamInfo>> resultMap = dpProcessParamsBo.filterOnPropertyType(inputParamEntry);
			// setting the successful records after filtering, back to input object
			inputParamEntry.setColumnEntries(resultMap.getKey());

			// save ineligible records to DP_PROCESS_PARAMS table
			if(CollectionUtils.isNotEmpty(resultMap.getValue())) {
				LOGGER.info("Saving all ineligible PropertyType entries to db..");
				saveParams(resultMap.getValue());
			}
			LOGGER.info("Filtering on Property type ended..");
		}
	}

	@Override
	public void filterOnAssetValue(DPProcessParamEntryInfo inputParamEntry) throws SystemException {
		if(null != inputParamEntry.getColumnEntries() && !inputParamEntry.getColumnEntries().isEmpty()) {
			LOGGER.info("Filtering on Asset value started..");
			KeyValue<List<DPProcessParamInfo>, List<DPProcessParamInfo>> resultMap = dpProcessParamsBo.filterOnAssetValue(inputParamEntry);
			// setting the successful records after filtering, back to input object
			inputParamEntry.setColumnEntries(resultMap.getKey());

			// save ineligible records to DP_PROCESS_PARAMS table
			if(CollectionUtils.isNotEmpty(resultMap.getValue())) {
				LOGGER.info("Saving all ineligible AssetValue entries to db..");
				saveParams(resultMap.getValue());
			}
			LOGGER.info("Filtering on Asset value ended..");
		}
	}

	private DPProcessParamInfo saveParam(DPProcessParamInfo recordToSave) {
		DPProcessParam in = new DPProcessParam();
		in = convert(recordToSave, DPProcessParam.class);
		in = dpProcessParamsBo.saveDPProcessParam(in);
		recordToSave.setId(in.getId());
		return recordToSave;
	}

}