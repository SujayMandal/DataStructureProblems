package com.ca.umg.business.mapping.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;

import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.mapping.validation.core.AbstractValidator;
import com.ca.umg.business.mapping.validation.core.MappingValidatorConstants;
import com.ca.umg.business.mapping.validation.core.MappingValidatorContainer;
import com.ca.umg.business.mapping.validation.core.ValidationUtil;
import com.ca.umg.business.mid.extraction.info.MidIOInfo;
import com.ca.umg.business.mid.extraction.info.MidMapping;
import com.ca.umg.business.mid.extraction.info.MidParamInfo;
import com.ca.umg.business.mid.extraction.info.TidExpressionInfo;
import com.ca.umg.business.mid.extraction.info.TidIOInfo;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;
import com.ca.umg.business.mid.extraction.info.TidSqlInfo;
import com.ca.umg.business.validation.ValidationError;

@Named
public class MappingValidatorImpl implements MappingValidator {

	@Inject
	private MappingValidatorUtil mappingValidatorUtil;

	@Inject
	private ValidationUtil validationUtil;

	private static List<String> validatorList;
	static {
		validatorList = new ArrayList<>();
		validatorList.add(MappingValidatorConstants.NULL_VALIDATOR);
		validatorList.add(MappingValidatorConstants.DATA_TYPE_VALIDATOR);
		validatorList.add(MappingValidatorConstants.INPUT_MAPPING_VALIDATOR);
	}

	@Override
	public List<ValidationError> validateMidMapping(MidIOInfo midIOInfo,
			TidIOInfo ioInfo, List<MidMapping> midInputMappings, Map<String,List<String>> mappingToDelete) {
	    //added this for fixing umg-1232 to avoid null pointer if no mappings are there 
	    List<MidMapping>  midInputMappingResult = null;
	    midInputMappingResult = ObjectUtils.defaultIfNull(midInputMappings, new ArrayList<MidMapping>());
	  
		return this.validateMidMapping(midIOInfo, ioInfo, midInputMappingResult,
				validatorList, mappingToDelete);
	}

	public List<ValidationError> validateMidMapping(MidIOInfo midIOInfo,
			TidIOInfo ioInfo, List<MidMapping> midInputMappings,
			List<String> inputValidatorList, Map<String,List<String>> mappingToDelete) {
		List<ValidationError> errors = new ArrayList<>();
		MappingValidatorContainer mappingValidatorContainer = this
				.prepareValidatorContainer(midIOInfo, ioInfo, midInputMappings,
						errors);
		if (CollectionUtils.isEmpty(errors)) {
			for (MidMapping midMapping : mappingValidatorContainer
					.getMidMappingMap().values()) {
				Queue<AbstractValidator> validators = validationUtil
						.getValidators(inputValidatorList);

				AbstractValidator firstValidator = validators.poll();
				mappingValidatorContainer.setMidMapping(midMapping);
				firstValidator.validate(mappingValidatorContainer, errors,
						validators, false, mappingToDelete);
			}
		}
		return errors;
	}

	public MappingValidatorContainer prepareValidatorContainer(
			MidIOInfo midIOInfo, TidIOInfo ioInfo,
			List<MidMapping> midInputMappings,
			List<ValidationError> validationErrors) {
		TidIOInfo tidIOInfo = null;
		tidIOInfo = addTidIOInfo(ioInfo);
		MappingValidatorContainer mappingValidatorContainer = new MappingValidatorContainer();
		mappingValidatorContainer.setMidParamInfoMap(this
				.prepareMidParamInfoMap(midIOInfo));
		mappingValidatorContainer.setTidParamInfoMap(this
				.prepareTidParamInfoMap(tidIOInfo));

		mappingValidatorContainer.setMidMappingMap(this
				.prepareFinalMidMappingMap(midIOInfo,
						mappingValidatorContainer.getTidParamInfoMap(),
						midInputMappings, validationErrors));

		mappingValidatorContainer.setTidSqlInfoMap(this
				.prepareTidSqlInfoMap(tidIOInfo));
		mappingValidatorContainer.setTidExpressionInfoMap(this
				.prepareTidExpressionInfoMap(tidIOInfo));

		if (CollectionUtils.isNotEmpty(mappingValidatorContainer
				.getMidMappingMap().values())
				&& mappingValidatorContainer.getMidParamInfoMap() != null) {
			mappingValidatorContainer.setTidMidParamInfoListMap(this
					.prepareTidMidParamInfoListMap(mappingValidatorContainer
							.getMidMappingMap().values(),
							mappingValidatorContainer.getMidParamInfoMap()));
		} else {
			mappingValidatorContainer
					.setTidMidParamInfoListMap(new HashMap<String, List<MidParamInfo>>());
		}
		return mappingValidatorContainer;
	}

	private Map<String, MidMapping> prepareFinalMidMappingMap(
			MidIOInfo midIOInfo, Map<String, TidParamInfo> tidParamInfoMap,
			List<MidMapping> midInputMappings,
			List<ValidationError> validationErrors) {
		Map<String, MidMapping> midMappingMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(midIOInfo.getMidInput())) {
			for (MidParamInfo midParamInfo : midIOInfo.getMidInput()) {
				this.prepareMidMappingMap(midMappingMap, midInputMappings,
						midParamInfo, tidParamInfoMap, validationErrors);
			}
		}
		return midMappingMap;
	}

	private void prepareMidMappingMap(Map<String, MidMapping> midMappingMap,
			List<MidMapping> midInputMappings, MidParamInfo midParamInfo,
			Map<String, TidParamInfo> tidParamInfoMap,
			List<ValidationError> validationErrors) {
		MidMapping midMapping = this.getMappingElement(midInputMappings,
				midParamInfo);
		if (midMapping != null
				&& CollectionUtils.isNotEmpty(midMapping.getInputs())) {
			for (String tidEle : midMapping.getInputs()) {
				mappingValidatorUtil.prepareChildMidMappingMap(midMappingMap,
						validationErrors, tidParamInfoMap.get(tidEle),
						midParamInfo);
			}
		} else if (!CollectionUtils.isNotEmpty(midParamInfo.getChildren())) {
			this.prepareEmptyMidMappingMap(midMappingMap, midParamInfo);

		}
		if (CollectionUtils.isNotEmpty(midParamInfo.getChildren())) {
			for (MidParamInfo childMid : midParamInfo.getChildren()) {
				this.prepareMidMappingMap(midMappingMap, midInputMappings,
						childMid, tidParamInfoMap, validationErrors);
			}
		}
	}

	private void prepareEmptyMidMappingMap(
			Map<String, MidMapping> midMappingMap, MidParamInfo midParamInfo) {
		if (midMappingMap.get(midParamInfo.getFlatenedName()) == null) {
			MidMapping mapping = new MidMapping();
			mapping.setMappedTo(midParamInfo.getFlatenedName());
			mapping.setInputs(new ArrayList<String>());
			midMappingMap.put(mapping.getMappedTo(), mapping);
		}
	}

	private MidMapping getMappingElement(List<MidMapping> midInputMappings,
			MidParamInfo midParamInfo) {
		MidMapping result = null;
		for (MidMapping midMapping : midInputMappings) {
			if (midParamInfo.getFlatenedName().equals(midMapping.getMappedTo())) {
				result = midMapping;
				break;
			}
		}
		return result;
	}

	private Map<String, List<MidParamInfo>> prepareTidMidParamInfoListMap(
			Collection<MidMapping> midInputMappings,
			Map<String, MidParamInfo> midParamInfoMap) {
		Map<String, List<MidParamInfo>> tidMidParamInfoListMap = new HashMap<>();
		for (MidMapping midMapping : midInputMappings) {
			if (CollectionUtils.isNotEmpty(midMapping.getInputs())) {
				for (String tidName : midMapping.getInputs()) {
					if (tidMidParamInfoListMap.get(tidName) == null) {
						tidMidParamInfoListMap.put(tidName,
								new ArrayList<MidParamInfo>());
					}
					tidMidParamInfoListMap.get(tidName).add(
							midParamInfoMap.get(midMapping.getMappedTo()));
				}

			}
		}

		return tidMidParamInfoListMap;
	}

	private Map<String, MidParamInfo> prepareMidParamInfoMap(MidIOInfo midIOInfo) {
		Map<String, MidParamInfo> midParamInfoMap = new HashMap<>();
		if (midIOInfo.getMidInput() != null) {
			for (MidParamInfo midParamInfo : midIOInfo.getMidInput()) {
				if (midParamInfo != null) {
					getMidParamMap(midParamInfoMap, midParamInfo);
				}
			}
		}

		return midParamInfoMap;
	}

	private void getMidParamMap(Map<String, MidParamInfo> midParamInfoMap,
			MidParamInfo midParamInfo) {
		midParamInfoMap.put(midParamInfo.getFlatenedName(), midParamInfo);
		if (CollectionUtils.isNotEmpty(midParamInfo.getChildren())) {
			for (MidParamInfo paramInfo : midParamInfo.getChildren()) {
				getMidParamMap(midParamInfoMap, paramInfo);
			}
		}
	}

	private Map<String, TidParamInfo> prepareTidParamInfoMap(TidIOInfo tidIOInfo) {
		Map<String, TidParamInfo> tidParamInfoMap = new HashMap<>();
		if (tidIOInfo.getTidInput() != null) {
			for (TidParamInfo tidParamInfo : tidIOInfo.getTidInput()) {
				if (tidParamInfo != null) {
					getTidParamMap(tidParamInfoMap, tidParamInfo, tidParamInfo.isExposedToTenant());
				}
			}
		}
		return tidParamInfoMap;
	}

	private void getTidParamMap(Map<String, TidParamInfo> midParamInfoMap,
			TidParamInfo tidParamInfo, boolean prntExposedToTenant) {
		midParamInfoMap.put(tidParamInfo.getFlatenedName(), tidParamInfo);
		if (CollectionUtils.isNotEmpty(tidParamInfo.getChildren())) {
			for (TidParamInfo paramInfo : tidParamInfo.getChildren()) {
				if (prntExposedToTenant) {
					paramInfo.setExposedToTenant(Boolean.TRUE);
					paramInfo.setUserExposedToTenant(BusinessConstants.NUMBER_ZERO);
				} else if (paramInfo.getUserExposedToTenant() == BusinessConstants.NUMBER_ZERO) {
					paramInfo.setExposedToTenant(prntExposedToTenant);
				} else if (paramInfo.getUserExposedToTenant() == BusinessConstants.NUMBER_ONE) {
					paramInfo.setExposedToTenant(Boolean.TRUE);
				} else if (paramInfo.getUserExposedToTenant() == BusinessConstants.NUMBER_TWO) {
					paramInfo.setExposedToTenant(Boolean.FALSE);
				}
				getTidParamMap(midParamInfoMap, paramInfo,
						paramInfo.isExposedToTenant());
			}
		}
	}

	private Map<String, TidSqlInfo> prepareTidSqlInfoMap(TidIOInfo tidIOInfo) {
		Map<String, TidSqlInfo> tidSqlInfoMap = new HashMap<>();
		if (tidIOInfo.getSqlInfos() != null) {
			for (TidSqlInfo tidSqlInfo : tidIOInfo.getSqlInfos()) {
				tidSqlInfoMap.put(tidSqlInfo.getSqlId(), tidSqlInfo);
			}
		}
		return tidSqlInfoMap;
	}

	private Map<String, TidExpressionInfo> prepareTidExpressionInfoMap(
			TidIOInfo tidIOInfo) {
		Map<String, TidExpressionInfo> tidExpressionInfoMap = new HashMap<>();
		if (tidIOInfo.getExpressionInfos() != null) {
			for (TidExpressionInfo tidExpressionInfo : tidIOInfo
					.getExpressionInfos()) {
				tidExpressionInfoMap.put(tidExpressionInfo.getExpressionName(),
						tidExpressionInfo);
			}
		}

		return tidExpressionInfoMap;
	}

	private TidIOInfo addTidIOInfo(TidIOInfo tidIOInfo) {
		TidIOInfo ioInfo = new TidIOInfo();
		List<TidParamInfo> paramInfos = null;
		if (tidIOInfo != null
				&& CollectionUtils.isNotEmpty(tidIOInfo.getTidInput())) {
			ioInfo.setExpressionInfos(tidIOInfo.getExpressionInfos());
			ioInfo.setSqlInfos(tidIOInfo.getSqlInfos());
			ioInfo.setTidOutput(tidIOInfo.getTidOutput());
			ioInfo.setTidInput(tidIOInfo.getTidInput());
			if (CollectionUtils.isNotEmpty(tidIOInfo.getTidSystemInput())) {
				paramInfos = tidIOInfo.getTidSystemInput();
				ioInfo.getTidInput().addAll(paramInfos);
			}
		} else {
			ioInfo = tidIOInfo;
		}
		return ioInfo;
	}
}
