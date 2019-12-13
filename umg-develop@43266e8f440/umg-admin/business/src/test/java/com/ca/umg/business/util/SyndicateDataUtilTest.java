package com.ca.umg.business.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.validation.ValidationError;

public class SyndicateDataUtilTest {

    @Test
    public void whenErrorsPresent() throws BusinessException {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        ValidationError error1 = new ValidationError("name", "Name cannot be empty");
        errors.add(error1);
        ValidationError error2 = new ValidationError("pin", "Pin code cannot be empty");
        errors.add(error2);
        try {
            SyndicateDataUtil.reportError(errors,"create");
            Assert.fail("BusinessException expected");
        } catch (BusinessException e) {
            assertThat(e.getCode(), is("BSE000025"));
        }
    }

}
