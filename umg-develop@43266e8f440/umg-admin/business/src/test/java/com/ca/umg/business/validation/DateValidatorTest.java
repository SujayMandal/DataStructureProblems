package com.ca.umg.business.validation;

import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.parse;
import static org.joda.time.format.DateTimeFormat.forPattern;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.syndicatedata.entity.SyndicateData;
import com.ca.umg.business.syndicatedata.info.SyndicateDataContainerInfo;

public class DateValidatorTest {

    private static final String DATE_FORMAT = "dd-MMM-yyyy hh:mm";

    private List<ValidationError> errors;

    private DateValidator classUnderTest;

    private SyndicateDataContainerInfo bean;

    private List<SyndicateData> previousVersions;

    @Before
    public void setUp() {
        errors = new ArrayList<ValidationError>();
        classUnderTest = new DateValidator();
        bean = new SyndicateDataContainerInfo();
        previousVersions = new ArrayList<>();
    }

    @Test
    public void testValidateDatesWhenActiveFromPriorToCurrentDateTime() throws BusinessException {
        bean.setValidFrom(parse("22-Mar-2013 11:21", forPattern(DATE_FORMAT)).getMillis());
        bean.setValidTo(parse("22-Mar-2016 11:25", forPattern(DATE_FORMAT)).getMillis());

        classUnderTest.validateDates(bean, previousVersions, errors);

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0).getField(), is("validFrom"));
        assertThat(errors.get(0).getMessage(), is("'Active From' should not be prior to current date time."));
    }

    @Test
    public void testValidateDatesWhenActiveUntillPriorToActiveFrom() throws BusinessException {
        bean.setValidFrom(System.currentTimeMillis() + 100000);
        bean.setValidTo(parse("22-Mar-2014 11:25", forPattern(DATE_FORMAT)).getMillis());

        classUnderTest.validateDates(bean, previousVersions, errors);

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0).getField(), is("validFrom"));
        assertThat(errors.get(0).getMessage(), is("'Active Untill' should not be prior to 'Active From'."));
    }

    @Test
    public void testValidateDatesWhenActiveFromPriorToActiveFromPreviousVersion() throws BusinessException {
        List<SyndicateData> previousVersions = new ArrayList<>();
        SyndicateData version1 = new SyndicateData();
        SyndicateData version2 = new SyndicateData();
        SyndicateData version3 = new SyndicateData();

        previousVersions.add(version3);
        previousVersions.add(version2);
        previousVersions.add(version1);

        version3.setValidFrom(System.currentTimeMillis());

        bean.setValidFrom(System.currentTimeMillis() - 1000);
        bean.setValidTo(parse("22-Mar-2016 11:25", forPattern(DATE_FORMAT)).getMillis());

        classUnderTest.validateDates(bean, previousVersions, errors);

        assertThat(errors.size(), is(3));
        assertThat(errors.get(0).getField(), is("validFrom"));

    }

    @Test
    public void testValidateDatesWhenThereIsATimeGap() throws BusinessException {
        SyndicateData version1 = new SyndicateData();
        SyndicateData version2 = new SyndicateData();
        SyndicateData version3 = new SyndicateData();

        previousVersions.add(version3);
        previousVersions.add(version2);
        previousVersions.add(version1);

        bean.setValidFrom(parse("22-Mar-2015 11:21", forPattern(DATE_FORMAT)).getMillis());
        bean.setValidTo(parse("22-Mar-2015 11:25", forPattern(DATE_FORMAT)).getMillis());

        version3.setValidFrom(parse("22-Mar-2014 11:21", forPattern(DATE_FORMAT)).getMillis());
        version3.setValidTo(parse("22-Mar-2014 11:22", forPattern(DATE_FORMAT)).getMillis());

        try {
            classUnderTest.validateDates(bean, previousVersions, errors);
            fail("BusinessException expected");
        } catch (BusinessException e) {
            assertThat(e.getCode(), is("BSE000034"));
        }
    }

    @Test
    public void testValidateDatesWhenActiveFromOverlapsWithPreviousVersionActiveTo() throws BusinessException {
        SyndicateData version1 = new SyndicateData();
        SyndicateData version2 = new SyndicateData();
        SyndicateData version3 = new SyndicateData();

        previousVersions.add(version3);
        previousVersions.add(version2);
        previousVersions.add(version1);

        version3.setValidFrom(parse("22-Mar-2014 11:21", forPattern(DATE_FORMAT)).getMillis());
        version3.setValidTo(parse("22-Mar-2015 11:22", forPattern(DATE_FORMAT)).getMillis());

        bean.setValidFrom(parse("22-Mar-2015 11:21", forPattern(DATE_FORMAT)).getMillis());
        bean.setValidTo(parse("22-Mar-2016 11:25", forPattern(DATE_FORMAT)).getMillis());

        try {
            classUnderTest.validateDates(bean, previousVersions, errors);
            fail("BusinessException expected");
        } catch (BusinessException e) {
            assertThat(e.getCode(), is("BSE000033"));
        }
    }

}
