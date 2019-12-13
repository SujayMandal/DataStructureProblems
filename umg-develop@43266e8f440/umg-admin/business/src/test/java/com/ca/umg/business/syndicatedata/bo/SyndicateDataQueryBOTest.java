package com.ca.umg.business.syndicatedata.bo;

import static org.junit.Assert.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.BaseTest;
import com.ca.umg.business.syndicatedata.dao.SyndicateVersionDataDAO;
import com.ca.umg.business.syndicatedata.entity.SyndicateData;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQuery;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryInput;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryObject;

@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
public class SyndicateDataQueryBOTest extends BaseTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyndicateDataQueryBOTest.class);

    @Inject
    private SyndicateDataQueryBO classUnderTest;

    @Inject
    private SyndicateVersionDataDAO syndicateVersionDataDAO;

    @Before
    public void setUp() {
        DateTime pastDateTime = new DateTime().minusDays(5);
        DateTime futureDateTime = new DateTime().plusDays(5);
        SyndicateData data = createSyndicateData("TABLE", "TABLE", 1l, "SYND_DATA_TABLE", "TABLE", "",
                pastDateTime.getMillis() / 1000, futureDateTime.getMillis() / 1000);
        try {
            syndicateVersionDataDAO.insertData(new String[] {
                    "CREATE TABLE SYND_DATA_TABLE(SYNDICATEDVERID INTEGER, COLUMN1 VARCHAR(10), COLUMN2 VARCHAR(10), COLUMN3 DATE);",
                    "INSERT INTO SYND_DATA_TABLE VALUES(" + data.getVersionId()
                            + ", 'A1', 'B1', TO_DATE('JUL-10-2014', 'MON-DD-YYYY'));",
                    "INSERT INTO SYND_DATA_TABLE VALUES(" + data.getVersionId()
                            + ", 'A2', 'B2', TO_DATE('JUL-15-2014', 'MON-DD-YYYY'));" });
        } catch (SystemException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    //TODO: Need to give a re-look in to this test case as this is failing for HSQL DB for bulk insert.
    @Ignore
    @Test
    public void testSyndicateDataQueryTest() {
        String testDate = new SimpleDateFormat("MMM-dd-YYYY").format(new Date());
        Set<SyndicateDataQueryInput> dataQueryInputs = new HashSet<>();
        SyndicateDataQueryInput input = new SyndicateDataQueryInput();
        input.setName("TESTDATE");
        input.setDataType("STRING");
        input.setSampleValue(testDate);
        dataQueryInputs.add(input);
        SyndicateDataQuery query = new SyndicateDataQuery();
        query.setInputParameters(dataQueryInputs);
        SyndicateDataQueryObject queryObject = new SyndicateDataQueryObject();
        query.setQueryObject(queryObject);
        query.setRowType("MULTIPLEROW");
        query.setDataType("ARRAY");
        queryObject.setSelectString("TAB1.COLUMN1 AS \"COL1ALIAS\", TAB2.COLUMN2 AS \"COL2ALIAS\"");
        queryObject.setFromString("SYND_DATA_TABLE TAB1, SYND_DATA_TABLE TAB2");
        queryObject.setWhereClause("TAB1.COLUMN1 = #A1# AND TAB1.COLUMN3 = #COL3#");

        input = new SyndicateDataQueryInput();
        input.setName("A1");
        input.setDataType("STRING");
        input.setSampleValue("A1");
        dataQueryInputs.add(input);

        input = new SyndicateDataQueryInput();
        input.setName("COL3");
        input.setDataType("DATE");
        input.setSampleValue("JUL-10-2014");
        dataQueryInputs.add(input);

        List<Map<String, Object>> testData = null;
        try {
            classUnderTest.runInTestMode(true);
            testData = classUnderTest.fetchTestData(query);
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            Assert.fail();
        }
        assertNotNull(query.getOutputParameters());
        assertNotNull(testData);
        Assert.assertTrue(CollectionUtils.isNotEmpty(testData));
    }
}
