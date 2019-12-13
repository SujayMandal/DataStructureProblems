package com.ca.umg.business.syndicatedata.daohelper;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.syndicatedata.daohelper.SyndicateDataQueryHelper.DB_TYPE;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQuery;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryInput;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryObject;

@Ignore
// TODO fix ignored test cases
public class SyndicateDataQueryHelperTest {

    @Test
    public void createVersionSubQueryTest() {
        SyndicateDataQueryHelper dataQueryHelper = new SyndicateDataQueryHelper();
        Map<String, String> subQueryMap = dataQueryHelper.createVersionSubQueries(new HashSet<String>(Arrays.asList("T1", "T2")),
                SyndicateDataQueryHelper.DB_TYPE.HSQL);
        System.out.println(subQueryMap);
    }

    @Test
    public void createFilterQueryTest() {
        SyndicateDataQueryHelper dataQueryHelper = new SyndicateDataQueryHelper();
        Map<String, String> aliases = new HashMap<>();
        aliases.put("TABLE1", "T1");
        aliases.put("TABLE2", "T2");
        aliases.put("TABLE3", "T3");
        String query = dataQueryHelper.createFilterQuery(aliases, SyndicateDataQueryHelper.DB_TYPE.HSQL);
        System.out.println("Query: " + query);
    }

    @Test
    public void fetchColumnNameWithTableAliasTest() throws BusinessException {
        SyndicateDataQueryHelper dataQueryHelper = new SyndicateDataQueryHelper();
        Map<String, String> colNameWithAliasMap = dataQueryHelper
                .fetchColumnNameWithTableAlias("t1.col1 = #jjhjh# AND t2.col2 < #jhjh# OR t1.col3 > 'XYZ' AND t2.col4 != t1.col3");
        Assert.assertNotNull(colNameWithAliasMap);
        Assert.assertEquals("{COL4=T2, COL3=T1, COL2=T2, COL1=T1}", colNameWithAliasMap.toString());
    }

    @Test
    public void fetchColumnNameWithTableAlias_withOutAliasesTest() {
        SyndicateDataQueryHelper dataQueryHelper = new SyndicateDataQueryHelper();
        try {
            dataQueryHelper.fetchColumnNameWithTableAlias("col1 = #jjhjh# AND col2 < #jhjh# OR col3 > 'XYZ' AND col4 != t1.col3");
        } catch (BusinessException bse) {
            Assert.assertEquals(BusinessExceptionCodes.BSE000058, bse.getCode());
        }
    }

    @Test
    public void fetchColumnNameWithTableAlias_withINClauseTest() throws BusinessException {
        SyndicateDataQueryHelper dataQueryHelper = new SyndicateDataQueryHelper();
        Map<String, String> colNameWithAliasMap = dataQueryHelper.fetchColumnNameWithTableAlias("t1.col1 in (#abcd#)");
        Assert.assertNotNull(colNameWithAliasMap);
        Assert.assertEquals("{COL1=T1}", colNameWithAliasMap.toString());
    }

    @Test
    public void fetchColumnNameWithValueNameTest() throws BusinessException {
        SyndicateDataQueryHelper dataQueryHelper = new SyndicateDataQueryHelper();
        Map<String, String> colNameWithAliasMap = dataQueryHelper
                .fetchColumnNameWithValueName("t1.col1 = #jjhjh# AND t2.col2 < #jhjh# OR t1.col3 > 'XYZ' AND t2.col4 != t1.col3");
        Assert.assertNotNull(colNameWithAliasMap);
        Assert.assertEquals("{COL4=t1.col3, COL3='XYZ', COL2=jhjh, COL1=jjhjh}", colNameWithAliasMap.toString());
    }

    @Test
    public void fetchColumnNameWithValueName_WithINClauseTest() throws BusinessException {
        SyndicateDataQueryHelper dataQueryHelper = new SyndicateDataQueryHelper();
        Map<String, String> colNameWithAliasMap = dataQueryHelper
                .fetchColumnNameWithValueName("t1.col1 = #jjhjh# AND t2.col2 < #jhjh# OR t1.col3 in (#XYZ#) AND t2.col4 != t1.col3");
        Assert.assertNotNull(colNameWithAliasMap);
        Assert.assertEquals("{COL4=t1.col3, COL3=(XYZ), COL2=jhjh, COL1=jjhjh}", colNameWithAliasMap.toString());
    }

    @Test
    public void fetchColumnNameWithTableAlias_WithANDSpacesMandatoryTest() throws BusinessException {
        SyndicateDataQueryHelper dataQueryHelper = new SyndicateDataQueryHelper();
        Map<String, String> colNameWithAliasMap = dataQueryHelper
                .fetchColumnNameWithTableAlias("t1.ANDES = #jjhjh# AND t2.ORIS < #jhjh# OR t1.col3 in (#XYZ#) AND AND.col4 != t1.col3");
        Assert.assertNotNull(colNameWithAliasMap);
        Assert.assertEquals("{COL4=AND, COL3=T1, ORIS=T2, ANDES=T1}", colNameWithAliasMap.toString());
    }
    
    @Test
    public void getRuntimeExecutableQuery_ForDate() {
    	SyndicateDataQueryHelper dataQueryHelper = new SyndicateDataQueryHelper();
    	SyndicateDataQuery synDataQuery = new SyndicateDataQuery();
    	SyndicateDataQueryObject queryObject  = new SyndicateDataQueryObject();
    	String whereClause = "GG.DOB=#DateOfBirth# AND GG.JOIN_DATE=#JoinDate# AND GG.END_DATE=#EndDate# OR GG.START_DATE = #StartDate# AND "
    			+ "GG.MAXDATE=#MaxDate# OR KK.MINDATE=#MinDate#";
    	queryObject.setWhereClause(whereClause);
    	synDataQuery.setQueryObject(queryObject);
    	Set<SyndicateDataQueryInput> synDataQueryInput = new HashSet<SyndicateDataQueryInput>();
    	synDataQueryInput.add(createSyndicateDataQueryInput("DateOfBirth", "DATE", "DD-MM-YYYY"));
    	synDataQueryInput.add(createSyndicateDataQueryInput("JoinDate", "DATE", "MM-DD-YYYY"));
    	synDataQueryInput.add(createSyndicateDataQueryInput("EndDate", "DATE", "DD-MMM-YYYY"));
    	synDataQueryInput.add(createSyndicateDataQueryInput("StartDate", "DATE", "MMM-DD-YYYY"));
    	synDataQueryInput.add(createSyndicateDataQueryInput("MaxDate", "DATE", "YYYY-MM-DD"));
    	synDataQueryInput.add(createSyndicateDataQueryInput("MinDate", "DATE", "YYYY-MMM-DD"));
    	synDataQuery.setInputParameters(synDataQueryInput);
    	String generatedWhereClause = dataQueryHelper.generateRuntimeExecutableQuery(synDataQuery, DB_TYPE.MYSQL);
    	assertTrue(generatedWhereClause.contains("%d-%m-%Y"));
    	assertTrue(generatedWhereClause.contains("%d-%M-%Y"));
    	assertTrue(generatedWhereClause.contains("%M-%d-%Y"));
    	assertTrue(generatedWhereClause.contains("%m-%d-%Y"));
    	assertTrue(generatedWhereClause.contains("%Y-%m-%d"));
    	assertTrue(generatedWhereClause.contains("%Y-%M-%d"));
    }
    
    private SyndicateDataQueryInput createSyndicateDataQueryInput(String name, String dataType, String dataTypeFormat) {
    	SyndicateDataQueryInput synDataQryIp = new SyndicateDataQueryInput();
    	synDataQryIp.setName(name);
    	synDataQryIp.setDataType(dataType);
    	synDataQryIp.setDataTypeFormat(dataTypeFormat);
    	return synDataQryIp;
    }
}
