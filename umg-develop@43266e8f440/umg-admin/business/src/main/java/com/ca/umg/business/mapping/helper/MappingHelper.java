/**
 * 
 */
package com.ca.umg.business.mapping.helper;

import static com.ca.umg.business.constants.BusinessConstants.UMG_DATE_FORMAT;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.ca.framework.core.db.domain.AbstractPersistable;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.exception.codes.FrameworkExceptionCodes;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mapping.entity.MappingInput;
import com.ca.umg.business.mapping.entity.MappingOutput;
import com.ca.umg.business.mapping.info.TidIoDefinition;
import com.ca.umg.business.mid.extraction.info.DatatypeInfo.Datatype;
import com.ca.umg.business.mid.extraction.info.MappingViewInfo;
import com.ca.umg.business.mid.extraction.info.MappingViews;
import com.ca.umg.business.mid.extraction.info.MidIOInfo;
import com.ca.umg.business.mid.extraction.info.MidParamInfo;
import com.ca.umg.business.mid.extraction.info.PartialMapping;
import com.ca.umg.business.mid.extraction.info.TidIOInfo;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;
import com.ca.umg.business.mid.extraction.info.TidSqlInfo;
import com.ca.umg.business.util.AdminUtil;

/**
 * @author chandrsa
 * 
 */
@SuppressWarnings("PMD")
@Named
public class MappingHelper {

    private static final String DEFAULT_VALUE = "defaultValue";

    public MappingInput prepareInputMapping(Mapping mapping, TidIOInfo tidIOInfo, MappingViews mappingViews)
            throws SystemException {
        MappingInput mappingInput = null;
        PartialMapping<TidParamInfo> tidInput = null;
        PartialMapping<MappingViewInfo> mapInput = null;
        if (mapping != null && tidIOInfo != null && mappingViews != null) {
            mappingInput = new MappingInput();
            tidInput = new PartialMapping<>();
            mapInput = new PartialMapping<>();
            this.sortInputMappingViews(tidIOInfo, mappingViews);
            tidInput.setPartials(tidIOInfo.getTidInput());
            mapInput.setPartials(mappingViews.getInputMappingViews());

            mappingInput.setMapping(mapping);
            mappingInput.setMappingData(ConversionUtil.convertToJsonString(mapInput).getBytes());
            mappingInput.setTenantInterfaceDefn(ConversionUtil.convertToJsonString(tidInput).getBytes());

            if (CollectionUtils.isNotEmpty(tidIOInfo.getTidSystemInput())) {
                PartialMapping<TidParamInfo> tidSytemInput = new PartialMapping<>();
                tidSytemInput.setPartials(tidIOInfo.getTidSystemInput());
                mappingInput.setTenantInterfaceSysDefn(ConversionUtil.convertToJsonString(tidSytemInput).getBytes());
            }
        }
        return mappingInput;
    }

    private void sortInputMappingViews(TidIOInfo tidIOInfo, MappingViews mappingViews) {
        Map<String, TidParamInfo> tidParamInfoMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(tidIOInfo.getTidInput())) {
            for (TidParamInfo tidParamInfo : tidIOInfo.getTidInput()) {
                tidParamInfoMap.put(tidParamInfo.getFlatenedName(), tidParamInfo);
                prepareChildTidParamInfoMap(tidParamInfoMap, tidParamInfo);
            }
        }
        if (CollectionUtils.isNotEmpty(tidIOInfo.getTidSystemInput())) {
            for (TidParamInfo tidParamInfo : tidIOInfo.getTidSystemInput()) {
                tidParamInfoMap.put(tidParamInfo.getFlatenedName(), tidParamInfo);
                prepareChildTidParamInfoMap(tidParamInfoMap, tidParamInfo);
            }
        }
        if (CollectionUtils.isNotEmpty(mappingViews.getInputMappingViews())) {
            /*
             * Collections.sort(mappingViews.getInputMappingViews(), new MappingComparator(tidParamInfoMap));
             */
            Collections.sort(mappingViews.getInputMappingViews(),
                    new MappingComparatorChain(new MappingComparator(tidParamInfoMap), new TidComparator(tidParamInfoMap)));
        }
    }

    // added for sorting the mapping in order of
    // tid params at top and syndicate at bottom
    private class MappingComparator implements Comparator<MappingViewInfo> {
        private final Map<String, TidParamInfo> tidParamInfoMap;

        public MappingComparator(Map<String, TidParamInfo> tidParamInfoMap) {
            this.tidParamInfoMap = tidParamInfoMap;
        }

        @Override
        public int compare(MappingViewInfo o1, MappingViewInfo o2) {
            TidParamInfo firstTidParamInfo = tidParamInfoMap.get(o1.getMappingParam());
            TidParamInfo secondTidParamInfo = tidParamInfoMap.get(o2.getMappingParam());
            int result = 0;
            if (firstTidParamInfo != null && secondTidParamInfo != null) {
                result = Boolean.compare(firstTidParamInfo.getSqlId() != null, secondTidParamInfo.getSqlId() != null);
            }
            return result;
        }
    }

    // added for sorting the mapping in order of
    // tid optional - tid mandatory - syndicate optional - syndicate mandatory
    private class TidComparator implements Comparator<MappingViewInfo> {
        private final Map<String, TidParamInfo> tidParamInfoMap;

        public TidComparator(Map<String, TidParamInfo> tidParamInfoMap) {
            this.tidParamInfoMap = tidParamInfoMap;
        }

        @Override
        public int compare(MappingViewInfo o1, MappingViewInfo o2) {
            TidParamInfo firstTidParamInfo = tidParamInfoMap.get(o1.getMappingParam());
            TidParamInfo secondTidParamInfo = tidParamInfoMap.get(o2.getMappingParam());
            int result = 0;
            if (firstTidParamInfo != null && secondTidParamInfo != null) {
                if (firstTidParamInfo.getSqlId() != null && secondTidParamInfo.getSqlId() != null) {
                    result = Boolean.compare(firstTidParamInfo.isMandatory(), secondTidParamInfo.isMandatory());
                } else if (firstTidParamInfo.getSqlId() == null && secondTidParamInfo.getSqlId() == null) {
                    result = Boolean.compare(firstTidParamInfo.isMandatory(), secondTidParamInfo.isMandatory());
                } else {
                    result = 0;
                }
            }
            return result;
        }
    }

    // added for sorting the mapping in order of
    // tid optional - tid mandatory - syndicate optional - syndicate mandatory
    private class MappingComparatorChain implements Comparator<MappingViewInfo> {
        private final List<Comparator<MappingViewInfo>> listComparators;

        @SafeVarargs
        public MappingComparatorChain(Comparator<MappingViewInfo>... comparators) {
            this.listComparators = Arrays.asList(comparators);
        }

        @Override
        public int compare(MappingViewInfo c1, MappingViewInfo c2) {
            int result = BusinessConstants.NUMBER_ZERO;
            for (Comparator<MappingViewInfo> comparator : listComparators) {
                result = comparator.compare(c1, c2);
                if (result != BusinessConstants.NUMBER_ZERO) {
                    break;
                }
            }
            return result;
        }
    }

    private void prepareChildTidParamInfoMap(Map<String, TidParamInfo> tidParamInfoMap, TidParamInfo tidParamInfo) {
        if (CollectionUtils.isNotEmpty(tidParamInfo.getChildren())) {
            for (TidParamInfo childTidParamInfo : tidParamInfo.getChildren()) {
                tidParamInfoMap.put(childTidParamInfo.getFlatenedName(), childTidParamInfo);
                this.prepareChildTidParamInfoMap(tidParamInfoMap, childTidParamInfo);
            }
        }
    }

    public MappingOutput prepareOutputMapping(Mapping mapping, TidIOInfo tidIOInfo, MappingViews mappingViews)
            throws SystemException {
        MappingOutput mappingOutput = null;
        PartialMapping<MappingViewInfo> mapOutput = null;
        PartialMapping<TidParamInfo> tidOutput = null;
        if (mapping != null && tidIOInfo != null && mappingViews != null) {
            mappingOutput = new MappingOutput();
            tidOutput = new PartialMapping<>();
            mapOutput = new PartialMapping<>();

            tidOutput.setPartials(tidIOInfo.getTidOutput());
            mapOutput.setPartials(mappingViews.getOutputMappingViews());

            mappingOutput.setMapping(mapping);
            mappingOutput.setMappingData(ConversionUtil.convertToJsonString(mapOutput).getBytes());
            mappingOutput.setTenantInterfaceDefn(ConversionUtil.convertToJsonString(tidOutput).getBytes());
        }
        return mappingOutput;
    }

    public PartialMapping<TidParamInfo> getTidInfoInitial(MidIOInfo midIOInfo, String type) {
        List<MidParamInfo> midParamInfos = null;
        MidIOInfo tempInfo = null;
        TidIOInfo tempTidInfo = null;
        final PartialMapping<TidParamInfo> partialTid = new PartialMapping<TidParamInfo>();
        if (midIOInfo != null && StringUtils.isNotBlank(type)) {
            tempInfo = new MidIOInfo();
            tempTidInfo = new TidIOInfo();
            if (StringUtils.equalsIgnoreCase(type, BusinessConstants.TYPE_INPUT_MAPPING)) {
                midParamInfos = midIOInfo.getMidInput();
                tempInfo.setMidInput(midParamInfos);
                tempTidInfo.copy(tempInfo);
                partialTid.setPartials(tempTidInfo.getTidInput());
            } else {
                midParamInfos = midIOInfo.getMidOutput();
                tempInfo.setMidOutput(midParamInfos);
                tempTidInfo.copy(tempInfo);
                partialTid.setPartials(tempTidInfo.getTidOutput());
            }
        }
        return partialTid;
    }

    public TidIOInfo getSQLOutputsSave(List<TidSqlInfo> infos, TidIOInfo tidIOInfo) {
        TidIOInfo info = new TidIOInfo();
        info.copy(tidIOInfo);
        return info;
    }

    public <T> String getExistingId(T mappingData) {
        String id = null;
        if (mappingData != null) {
            id = ((AbstractPersistable) mappingData).getId();
        }
        return id;
    }

    public Map<String, TidParamInfo> getFlattenedInputMapping(TidIOInfo tidIOInfo) throws BusinessException, SystemException {
        Map<String, TidParamInfo> tidFlatInputMap = null;
        if (tidIOInfo != null) {
            joinTidParams(tidIOInfo);
            tidFlatInputMap = new HashMap<String, TidParamInfo>();
            List<TidParamInfo> tidInput = tidIOInfo.getTidInput();

            for (TidParamInfo tidParamParent : tidInput) {
                if (CollectionUtils.isNotEmpty(tidParamParent.getChildren())) {
                    tidFlatInputMap.putAll(getTidChildMap(tidParamParent));
                } else {
                    tidFlatInputMap.put(tidParamParent.getFlatenedName(), tidParamParent);
                }
            }

        }
        return tidFlatInputMap;
    }

    public Map<String, TidParamInfo> getTidChildMap(TidParamInfo tidParamParent) {
        Map<String, TidParamInfo> childFlatInputMap = new HashMap<String, TidParamInfo>();
        List<TidParamInfo> tidInputParentChildList = tidParamParent.getChildren();

        if (CollectionUtils.isNotEmpty(tidInputParentChildList)) {
            for (TidParamInfo tidChild : tidInputParentChildList) {
                if (CollectionUtils.isEmpty(tidChild.getChildren())) {
                    childFlatInputMap.put(tidChild.getFlatenedName(), tidChild);
                } else {
                    childFlatInputMap.putAll(getTidChildMap(tidChild));
                }
            }
        }

        return childFlatInputMap;
    }

    public Map<String, MidParamInfo> getFlattenedOutputMapping(MidIOInfo midIOInfo) throws BusinessException, SystemException {
        Map<String, MidParamInfo> midFlatOutpuMap = null;

        if (midIOInfo != null) {
            midFlatOutpuMap = new HashMap<String, MidParamInfo>();
            List<MidParamInfo> midOutput = midIOInfo.getMidOutput();

            for (MidParamInfo midParamParent : midOutput) {
                if (midParamParent.getChildren() != null) {
                    midFlatOutpuMap.putAll(getMidChildMap(midParamParent));
                } else {
                    midFlatOutpuMap.put(midParamParent.getFlatenedName(), midParamParent);
                }
            }

        }
        return midFlatOutpuMap;
    }

    public Map<String, MidParamInfo> getMidChildMap(MidParamInfo midParamParent) {
        Map<String, MidParamInfo> childFlatOutputMap = new HashMap<String, MidParamInfo>();
        List<MidParamInfo> midOutputParentChildList = midParamParent.getChildren();

        for (MidParamInfo midChild : midOutputParentChildList) {
            if (midChild.getChildren() == null) {
                childFlatOutputMap.put(midChild.getFlatenedName(), midChild);
            } else {
                childFlatOutputMap.putAll(getMidChildMap(midChild));
            }
        }

        return childFlatOutputMap;
    }

    private void joinTidParams(TidIOInfo tidIOInfo) {
        if (tidIOInfo.getTidInput() != null && tidIOInfo.getTidSystemInput() != null) {
            tidIOInfo.getTidInput().addAll(tidIOInfo.getTidSystemInput());
        }
    }

    /**
     * This method would create a {@link Map} as [mappingParam, mappedTo]
     * 
     * @param mappingViewInfos
     * @return
     */
    public Map<String, String> createMappingDetailMap(List<MappingViewInfo> mappingViewInfos) {
        Map<String, String> mappingDetailMap = null;
        if (CollectionUtils.isNotEmpty(mappingViewInfos)) {
            mappingDetailMap = new HashMap<String, String>();
            for (MappingViewInfo mappingViewInfo : mappingViewInfos) {
                mappingDetailMap.put(mappingViewInfo.getMappingParam().toUpperCase(Locale.getDefault()),
                        mappingViewInfo.getMappedTo());
            }
        }
        return mappingDetailMap;
    }

    public void clearModifiedByAndDate(Mapping mapping) {
        mapping.setLastModifiedBy(null);
        mapping.setLastModifiedDate(null);
    }

    public Map<String, String> buildSqlMappedInputs(List<TidSqlInfo> sqlInfos) {
        List<TidParamInfo> inputParams = null;
        Map<String, String> queryInputs = new HashMap<String, String>();
        if (CollectionUtils.isNotEmpty(sqlInfos)) {
            for (TidSqlInfo sqlInfo : sqlInfos) {
                inputParams = sqlInfo.getInputParams();
                if (CollectionUtils.isNotEmpty(inputParams)) {
                    for (TidParamInfo input : inputParams) {
                        queryInputs.put(input.getFlatenedName(), sqlInfo.getSqlName());
                    }
                }
            }
        }
        return queryInputs;
    }

    public void getIoDefinitions(List<TidParamInfo> tidParamInfos, List<TidIoDefinition> definitions, boolean isTestBed) {
        TidIoDefinition definition = null;
        String type = null;
        if (CollectionUtils.isNotEmpty(tidParamInfos)) {
            for (TidParamInfo tidParamInfo : tidParamInfos) {
                type = tidParamInfo.getDatatype().getType();
                if (StringUtils.equalsIgnoreCase(tidParamInfo.getDatatype().getType(), Datatype.OBJECT.getDatatype())) {
                    if (!isTestBed) {
                        definition = setTidIoDefiinition(tidParamInfo);
                        setTypeElement(type, definition.isArrayType(), definition);
                        definitions.add(definition);
                    }
                    getIoDefinitions(tidParamInfo.getChildren(), definitions, isTestBed);
                } else {
                    definition = setTidIoDefiinition(tidParamInfo);
                    if (tidParamInfo.getDatatype().getProperties().containsKey(DEFAULT_VALUE)) {
                        if (tidParamInfo.getDatatype().isArray()) {
                            setArrayValue(isTestBed, definition, tidParamInfo);
                        } else {
                            definition.setValue(tidParamInfo.getDatatype().getProperties().get(DEFAULT_VALUE));
                        }
                    }
                    setTypeElement(type, definition.isArrayType(), definition);
                    definitions.add(definition);
                }
            }
        }
    }

    private void setArrayValue(boolean isTestBed, TidIoDefinition definition, TidParamInfo tidParamInfo) {
        // UMG-3128:adding double quotes to string of arrays: START#
        // added the or check ofr date to fix the bug UMG-4553 (default value for date array)
        if ((StringUtils.equals("string", tidParamInfo.getDatatype().getType())
                || StringUtils.equals("date", tidParamInfo.getDatatype().getType())) && isTestBed) {
            List<Object> stringListwithDoubleQuotes = new ArrayList<>();
            AdminUtil.getStringArrayWithDoubleQuotes(tidParamInfo.getDatatype().getProperties().get(DEFAULT_VALUE),
                    stringListwithDoubleQuotes);
            if (!CollectionUtils.isEmpty(stringListwithDoubleQuotes)) {
                definition.setArrayValue(stringListwithDoubleQuotes.toString());
                definition.setValue(stringListwithDoubleQuotes.toString());
            }

        } else {
            if (isTestBed && tidParamInfo.getDatatype().getProperties().get(DEFAULT_VALUE) != null) {
                definition.setValue(tidParamInfo.getDatatype().getProperties().get(DEFAULT_VALUE).toString());
                definition.setArrayValue(tidParamInfo.getDatatype().getProperties().get(DEFAULT_VALUE).toString());
            } else {
                definition.setValue(tidParamInfo.getDatatype().getProperties().get(DEFAULT_VALUE));
                definition.setArrayValue(tidParamInfo.getDatatype().getProperties().get(DEFAULT_VALUE));

            }
        }
        // UMG-3128:adding double quotes to string of arrays: END#
    }

    private void setTypeElement(String type, boolean isArray, TidIoDefinition definition) {
        // TODO move all description to constants file.
        String htmlElement = null;
        String description = null;
        if (StringUtils.isNotBlank(type)) {
            definition.getDatatype().put("type", type.toUpperCase(Locale.getDefault()));
            if (isArray) {
                definition.setValidationMethod("validate_array");
                definition.setHtmlElement(BusinessConstants.TEXTAREA);
                definition.setDescription(
                        "Any data of the format [[1,3],[3,5],[6,7]] would represent array of 3/2 dimension. A single dimension array could be [1, 2, 3, 4].");
            } else {
                definition.setValidationMethod("validate_" + type.toLowerCase(Locale.getDefault()));
                switch (Datatype.valueOf(type.toUpperCase(Locale.getDefault()))) {
                case DOUBLE:
                    description = "Any numeric value assignable to a double field. Eg. 1, -1.1, 2.0";
                    htmlElement = BusinessConstants.TEXTBOX;
                    break;
                case INTEGER:
                    description = "Any numeric value assignable to a integer field.No decimal field will be allowed. Eg. 1, -1, 2";
                    htmlElement = BusinessConstants.TEXTBOX;
                    break;
                case STRING:
                    description = "Any numeric value assignable to a String field. Eg. 1, abc, 2, sampleStr etc.";
                    htmlElement = BusinessConstants.TEXTBOX;
                    break;
                case DATE:
                    description = "Any date data. Please use the date picker to choose a date.";
                    htmlElement = BusinessConstants.DATE;
                    break;
                case DATETIME:
                    description = "Any date time data. Please use the date & time picker to choose a date and time.";
                    htmlElement = BusinessConstants.DATE_TIME;
                    break;
                case BOOLEAN:
                    description = "Any boolean data. Checked would mean TRUE";
                    htmlElement = BusinessConstants.CHECKBOX;
                    break;
                case OBJECT:
                    // do nothing
                    break;
                default:
                    description = "Any data of the format [[1,3],[3,5],[6,7]] would represent array of 3/2 dimension. A single dimension array could be [1, 2, 3, 4].";
                    htmlElement = BusinessConstants.TEXTBOX;
                    break;
                }
                definition.setHtmlElement(htmlElement);
                definition.setDescription(description);
            }
        }
    }

    public void createObjectStructure(Map<String, Object> complexMap, Map<String, Object> inputMap) {
        int maxWeight = 0;
        int keyWeight = 0;
        Map<String, Object> comarptmentComplexMap = null;
        Map<Integer, Map<String, Object>> weightedComplexMap = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer arg0, Integer arg1) {
                if (arg0 > arg1) {
                    return -1;
                } else if (arg0 < arg1) {
                    return 1;
                }
                return 0;
            }
        });
        for (Entry<String, Object> complexEntry : complexMap.entrySet()) {
            keyWeight = AdminUtil.countOccurence(complexEntry.getKey(), "/");
            maxWeight = keyWeight > maxWeight ? keyWeight : maxWeight;
            if (weightedComplexMap.containsKey(keyWeight)) {
                weightedComplexMap.get(keyWeight).put(complexEntry.getKey(), complexEntry.getValue());
            } else {
                comarptmentComplexMap = new HashMap<>();
                comarptmentComplexMap.put(complexEntry.getKey(), complexEntry.getValue());
                weightedComplexMap.put(keyWeight, comarptmentComplexMap);
            }
        }
        createWghtedObjectStructure(weightedComplexMap, maxWeight, inputMap);
    }

    private KeyValuePair<String, String> getParentChildNames(String complexNames) {
        KeyValuePair<String, String> parentChild = null;
        int lastindex = 0;
        if (StringUtils.isNotBlank(complexNames)) {
            lastindex = StringUtils.lastIndexOf(complexNames, '/');
            parentChild = new KeyValuePair<String, String>(StringUtils.substring(complexNames, 0, lastindex),
                    StringUtils.substring(complexNames, lastindex + 1, complexNames.length()));
        }
        return parentChild;
    }

    @SuppressWarnings("unchecked")
    public void createWghtedObjectStructure(Map<Integer, Map<String, Object>> weightedComplexMap, int maxWeight,
            Map<String, Object> inputMap) {
        KeyValuePair<String, String> parentChild = null;
        Map<String, Object> complexLevelMap = null;
        Map<String, Object> childMap = null;
        Map<String, Object> complexMap = null;
        int currentWeight = 0;
        if (MapUtils.isNotEmpty(weightedComplexMap)) {
            complexLevelMap = new HashMap<>();
            complexMap = weightedComplexMap.get(maxWeight);

            for (Entry<String, Object> objectEntry : complexMap.entrySet()) {
                if (StringUtils.contains(objectEntry.getKey(), "/")) {
                    parentChild = getParentChildNames(objectEntry.getKey());
                    if (complexLevelMap.containsKey(parentChild.getKey())) {
                        childMap = (Map<String, Object>) complexLevelMap.get(parentChild.getKey());
                        childMap.put(parentChild.getValue(), objectEntry.getValue());
                    } else {
                        childMap = new HashMap<String, Object>();
                        childMap.put(parentChild.getValue(), objectEntry.getValue());
                        complexLevelMap.put(parentChild.getKey(), childMap);
                    }
                } else {
                    inputMap.put(objectEntry.getKey(), objectEntry.getValue());
                }
            }
            if (complexLevelMap.size() > BusinessConstants.NUMBER_ZERO) {
                currentWeight = maxWeight - 1;
                if (currentWeight == BusinessConstants.NUMBER_ZERO) {
                    inputMap.putAll(complexLevelMap);
                    weightedComplexMap.clear();
                } else if (weightedComplexMap.containsKey(currentWeight)) {
                    weightedComplexMap.get(currentWeight).putAll(complexLevelMap);
                } else {
                    weightedComplexMap.put(currentWeight, complexLevelMap);
                }
                createWghtedObjectStructure(weightedComplexMap, currentWeight, inputMap);
            }
        }
    }

    public void setElementValue(TidIoDefinition definition) throws SystemException {
        String type = (String) definition.getDatatype().get("type");
        Object value = null;
        try {
            if (definition.isArrayType()) {
                if (definition.getValue() == null) {
                    value = null;
                } else if (definition.getArrayValue() instanceof List) {
                    value = getArrayVal((String) definition.getDatatype().get("type"), (List<?>) definition.getArrayValue());
                    if (value == null) {
                        value = getElementValue(definition, type);
                        if (value != null) {
                            List valueList = new ArrayList();
                            valueList.add(value);
                            value = valueList;
                        }
                    }
                } else {
                    value = definition.getArrayValue();
                }
            } else if (StringUtils.isNotBlank(type)) {
                value = getElementValue(definition, type);
            }
            definition.setValue(value);
        } catch (ParseException exp) {
            SystemException.newSystemException(FrameworkExceptionCodes.BSE000009, new Object[] { exp.getMessage() }, exp);
        }
    }

    private Object getArrayVal(String type, List<?> arrayVal) {
        List<Object> doubles = null;
        Object result = arrayVal;
        if (StringUtils.equalsIgnoreCase(type, Datatype.DOUBLE.getDatatype()) && arrayVal != null) {
            doubles = new ArrayList<>();
            for (Object object : arrayVal) {
                if (object instanceof List) {
                    object = getArrayVal(type, (List<?>) object);
                    doubles.add(object);
                } else {
                    if (object != null) {
                        doubles.add(Double.parseDouble(String.valueOf(object)));
                    } else {
                        doubles.add(object);
                    }
                }
            }
            result = doubles;
        }
        return result;
    }

    private Object getElementValue(TidIoDefinition definition, String type) throws ParseException {
        Object value = null;
        if (definition.getValue() != null && StringUtils.isNotBlank(String.valueOf(definition.getValue()))) {
            switch (Datatype.valueOf(type.toUpperCase(Locale.getDefault()))) {
            case DOUBLE:
                value = Double.parseDouble(String.valueOf(definition.getValue()));
                break;
            case INTEGER:
                value = Integer.parseInt(String.valueOf(definition.getValue()));
                break;
            case LONG:
                value = Long.parseLong(String.valueOf(definition.getValue()));
                break;
            case BIGINTEGER:
                value = new BigInteger(String.valueOf(definition.getValue()));
                break;
            case BIGDECIMAL:
                value = new BigDecimal(String.valueOf(definition.getValue()));
                break;
            case DATE:
                value = AdminUtil.convertDateFormat(UMG_DATE_FORMAT, getDatePattern(definition),
                        String.valueOf(definition.getValue()));
                break;
            case BOOLEAN:
                value = Boolean.parseBoolean(String.valueOf(definition.getValue()));
                break;
            default:
                value = String.valueOf(definition.getValue());
                break;
            }
        }
        return value;
    }

    private String getDatePattern(TidIoDefinition definition) {
        String dateFormat = BusinessConstants.TENANT_INPUT_DATE_FORMAT;
        String incomingPattern = null;
        if (MapUtils.isNotEmpty(definition.getDatatype())) {
            incomingPattern = String.valueOf(definition.getDatatype().get("pattern"));
            if (StringUtils.isNotBlank(incomingPattern) && !StringUtils.equalsIgnoreCase(incomingPattern, "null")) {
                dateFormat = incomingPattern.toUpperCase(Locale.getDefault());

            }
        }
        return dateFormat;
    }

    /**
     * This method used to set TID IO definitions for test bed which comes from Transaction Dashboard
     * 
     * @param tidParamInfos
     * @param definitions
     * @param defaultValuesList
     * @param paramNames
     * @throws BusinessException
     */
    public void getIoDefinitionsFromTxnDashboard(List<TidParamInfo> tidParamInfos, List<TidIoDefinition> definitions,
            List<String> defaultValuesList, List<String> paramNames) throws BusinessException {
        TidIoDefinition definition = null;
        String type = null;
        if (CollectionUtils.isNotEmpty(tidParamInfos)) {
            for (TidParamInfo tidParamInfo : tidParamInfos) {
                if (StringUtils.equalsIgnoreCase(tidParamInfo.getDatatype().getType(), Datatype.OBJECT.getDatatype())) {
                    getIoDefinitionsFromTxnDashboard(tidParamInfo.getChildren(), definitions, defaultValuesList, paramNames);
                } else {
                    type = tidParamInfo.getDatatype().getType();
                    definition = setTidIoDefiinition(tidParamInfo);
                    paramNames.add(definition.getName());
                    if (tidParamInfo.getValue() == null) {
                        defaultValuesList.add(tidParamInfo.getFlatenedName());
                    } else {
                        definition.setValue(tidParamInfo.getValue());
                    }
                    setTypeElement(type, definition.isArrayType(), definition);
                    if (StringUtils.equalsIgnoreCase(type, BusinessConstants.DATE) && definition.getValue() != null) {
                        try {
                            if (definition.isArrayType()) {
                                String completeDateString = String.valueOf(definition.getValue());
                                // commented the code for umg-4555
                                // String invidualDates[] = completeDateString.split("[\"]");
                                /*
                                 * for (final String invidualDate : invidualDates) { if (invidualDate == null ||
                                 * invidualDate.isEmpty() || invidualDate.equals("[") || invidualDate.equals("]") ||
                                 * invidualDate.equals(",")) { continue; } else { final String formattedDate =
                                 * AdminUtil.convertDateFormatStrict( getDatePattern(definition), UMG_DATE_FORMAT, invidualDate);
                                 * completeDateString = completeDateString.replace(invidualDate, formattedDate); } }
                                 */

                                definition.setValue(completeDateString);
                            } else {
                                definition.setValue(AdminUtil.convertDateFormatStrict(getDatePattern(definition), UMG_DATE_FORMAT,
                                        String.valueOf(definition.getValue())));
                            }
                        } catch (ParseException e) {
                            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000024, new String[] {
                                    String.valueOf(definition.getValue()), BusinessConstants.TENANT_INPUT_DATE_FORMAT });
                        }

                    }

                    definitions.add(definition);
                }
            }
        }
    }

    /**
     * This method used to set basic info to TID IO Definition
     * 
     * @param tidParamInfo
     * @return
     */
    private TidIoDefinition setTidIoDefiinition(TidParamInfo tidParamInfo) {
        TidIoDefinition definition = new TidIoDefinition();
        definition.setName(tidParamInfo.getFlatenedName());
        definition.setArrayType(tidParamInfo.getDatatype().isArray());
        definition.setDatatype(tidParamInfo.getDatatype().getProperties());
        definition.setMandatory(tidParamInfo.isMandatory());
        return definition;
    }

    /**
     * removes the mapping who have the parameter exposed to tenant set as true
     * 
     * @param mappingViews
     * @param mappingToDelete
     */
    public void removeTenantExposedMappings(MappingViews mappingViews, Map<String, List<String>> mappingToDelete) {

        if (mappingToDelete != null && !mappingToDelete.isEmpty()) {
            Iterator itr = mappingViews.getInputMappingViews().iterator();
            while (itr.hasNext()) {
                MappingViewInfo mapViewInfo = (MappingViewInfo) itr.next();
                if (mappingToDelete.containsKey(mapViewInfo.getMappedTo())) {
                    List<String> listMapToDelete = mappingToDelete.get(mapViewInfo.getMappedTo());
                    for (String str : listMapToDelete) {
                        if (str.equals(mapViewInfo.getMappingParam())) {
                            itr.remove();
                        }
                    }
                }
            }
        }
    }

    /**
     * removes the tid params which are marked skip in tenant api
     * 
     * @param tidParamInfos
     */
    public void removeTidParamsSkippedToTenant(List<TidParamInfo> tidParamInfos) {
        Iterator itr = tidParamInfos.iterator();
        while (itr.hasNext()) {
            TidParamInfo tidParam = (TidParamInfo) itr.next();
            if (tidParam.isExposedToTenant()) {
                itr.remove();
            } else if (tidParam.getChildren() != null) {
                removeTidParamsSkippedToTenant(tidParam.getChildren());
            }
        }
    }
}
