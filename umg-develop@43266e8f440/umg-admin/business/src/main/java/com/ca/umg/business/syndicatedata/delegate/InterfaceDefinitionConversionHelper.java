package com.ca.umg.business.syndicatedata.delegate;

import static com.ca.umg.business.mid.extraction.info.DatatypeInfo.DIMENSIONS;
import static com.ca.umg.business.mid.extraction.info.DatatypeInfo.Datatype.OBJECT;
import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;
import org.springframework.stereotype.Service;

import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.mid.extraction.info.DatatypeInfo;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;
import com.ca.umg.business.mid.extraction.info.TidSqlInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryParameterInfo;
import com.ca.umg.business.syndicatedata.util.QueryResultTypes;

@Service
public class InterfaceDefinitionConversionHelper {

    public List<TidSqlInfo> convertToInterfaceDefinitionInfos(List<SyndicateDataQueryInfo> allQueries) {
        List<TidSqlInfo> interfaceParams = new ArrayList<>();
        for (SyndicateDataQueryInfo query : allQueries) {
            interfaceParams.add(createInterfaceDefinitionInfo(query));
        }
        return interfaceParams;
    }

    private TidSqlInfo createInterfaceDefinitionInfo(SyndicateDataQueryInfo query) {
        TidSqlInfo tidInfo = new TidSqlInfo();
        tidInfo.setSqlName(query.getName());
        tidInfo.setSqlId(query.getName());
        tidInfo.setDatatypeInfo(getType(query));
        tidInfo.setInputParams(generateInputParams(query));
        tidInfo.setOutputParams(generateOutputParams(query));
        return tidInfo;
    }

    /**
     * <code>SingleRow|SingleCol= [1,1]</code></br> <code>SingleRow|MultipleCol = [1,n]</code></br>
     * <code>MultipleRow|MultipleCol = [-1,n]</code></br>
     * <code>MultipleRow|SingleCol = [-1] as the column is ignored. Represents single dimension array.</code>
     * <code></br>If marked as {@link QueryResultTypes#SINGLE_DIM_ARRAY} the dimension is -1</br>
     * This option is allowed only in cases as MultipleRow|SingleCol & SingleRow|MultipleCol
     * </code>
     * 
     * @param query
     * @return
     */
    private DatatypeInfo getType(SyndicateDataQueryInfo query) {
        DatatypeInfo type = new DatatypeInfo();
        int numOfCol = 0;
        if (isSingleDimensionArray(query)) {
            type.setArray(true);
            type.setProperties(getProperties(-1, 1));
            type.setType(getDatatypeIfArray(query));
        } else if (isArray(query)) {
            numOfCol = getNoOfOutputParameters(query);
            type.setArray(true);
            if (isMultipleRows(query)) {
                type.setProperties(getProperties(-1, numOfCol));
            } else if (isSingleRow(query)) {
                type.setProperties(getProperties(1, numOfCol));
            }
            type.setType(getDatatypeIfArray(query));
        } else if (isObject(query)) {
            if (isMultipleRows(query)) {
                type.setArray(true);
                type.setProperties(getProperties(-1, 1));
            }
            type.setType(query.getDataType());
        } else if (isPrimitive(query)) {
            type.setType(getDatatypeIfArray(query));
        }
        return type;
    }

    private String getDatatypeIfArray(SyndicateDataQueryInfo query) {
        // This is a temporary solution. Should be removed once we store data type of array in DB(during query save).
        return query.getOutputParameters().get(0).getDataType();
    }

    private boolean isObject(SyndicateDataQueryInfo query) {
        return equalsIgnoreCase(query.getDataType(), OBJECT.getDatatype());
    }

    private Map<String, Object> getProperties(int rows, int columns) {
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        properties.put(DIMENSIONS, getDimensions(rows, columns));
        return properties;
    }

    private boolean isArray(SyndicateDataQueryInfo query) {
        return equalsIgnoreCase(query.getDataType(), QueryResultTypes.ARRAY.getDatatype());
    }

    private boolean isSingleDimensionArray(SyndicateDataQueryInfo query) {
        return equalsIgnoreCase(query.getDataType(), QueryResultTypes.SINGLE_DIM_ARRAY.getDatatype());
    }

    private boolean isPrimitive(SyndicateDataQueryInfo query) {
        return equalsIgnoreCase(query.getDataType(), QueryResultTypes.PRIMITIVE.getDatatype());
    }

    private List<Integer> getDimensions(int rows, int columns) {
        List<Integer> dimensions = null;
        if (rows == -1 && columns == 1) {
            dimensions = asList(new Integer[] { rows });
        } else {
            dimensions = asList(new Integer[] { rows, columns });
        }
        return dimensions;
    }

    private boolean isSingleRow(SyndicateDataQueryInfo query) {
        return equalsIgnoreCase(query.getRowType(), QueryResultTypes.SINGLEROW.getDatatype());
    }

    private boolean isMultipleRows(SyndicateDataQueryInfo query) {
        return equalsIgnoreCase(query.getRowType(), QueryResultTypes.MULTIPLEROW.getDatatype());
    }

    private Integer getNoOfOutputParameters(SyndicateDataQueryInfo query) {
        return query.getOutputParameters().size();
    }

    private List<TidParamInfo> generateOutputParams(SyndicateDataQueryInfo query) {
        List<TidParamInfo> outputParams = new ArrayList<>();
        if (isNotEmpty(query.getOutputParameters())) {
            for (SyndicateDataQueryParameterInfo param : query.getOutputParameters()) {
                TidParamInfo outputParam = new TidParamInfo();
                DatatypeInfo datatype = new DatatypeInfo();
                datatype.setType(param.getDataType());
                outputParam.setDatatype(datatype);
                outputParam.setFlatenedName(query.getName() + BusinessConstants.SLASH + param.getName());
                outputParam.setName(param.getName());
                outputParam.setSqlId(query.getName());
                // outputParam.setSyndicate(BusinessConstants.TRUE);
                // outputParam.setSqlOutput(BusinessConstants.TRUE);
                outputParam.setSequence(param.getSequence());
                outputParams.add(outputParam);
            }
            sort(outputParams, new SequenceComparator());
        }
        return outputParams;
    }

    private List<TidParamInfo> generateInputParams(SyndicateDataQueryInfo query) {
        List<TidParamInfo> inputParams = new ArrayList<>();
        if (isNotEmpty(query.getInputParameters())) {
            for (SyndicateDataQueryParameterInfo param : query.getInputParameters()) {
                if (isNotTestDate(param)) {
                    TidParamInfo inputParam = new TidParamInfo();
                    DatatypeInfo datatype = new DatatypeInfo();
                    datatype.setType(param.getDataType());
                    inputParam.setDatatype(datatype);
                    inputParam.setFlatenedName(param.getName());
                    inputParam.setName(param.getName());
                    inputParam.setSqlId(query.getName());
                    inputParam.setSyndicate(true);
                    // TODO : set after discussing
                    inputParams.add(inputParam);
                }
            }
        }
        sort(inputParams, new BeanComparator<TidParamInfo>("name"));
        return inputParams;
    }

    private class SequenceComparator implements Comparator<TidParamInfo> {

        @Override
        public int compare(TidParamInfo o1, TidParamInfo o2) {
        	int result = 0;
            if (o1.getSequence() > o2.getSequence()) {
            	result =  BusinessConstants.NUMBER_ONE;
            } else if (o1.getSequence() < o2.getSequence()) {
            	result =  BusinessConstants.NEGETIVE_NUMBER_ONE;
            }
            return result;
        }

    }

    private boolean isNotTestDate(SyndicateDataQueryParameterInfo param) {
        return !equalsIgnoreCase(param.getName(), "TESTDATE");
    }

}