package com.ca.umg.business;

import org.hamcrest.Description;
import org.junit.internal.matchers.TypeSafeMatcher;

import com.ca.framework.core.exception.BusinessException;

public class BusinessCodeMatcher extends TypeSafeMatcher<BusinessException> {

    public static BusinessCodeMatcher hasCode(String code) {
        return new BusinessCodeMatcher(code);
    }

    private String foundErrorCode;
    private String expectedErrorCode;

    private BusinessCodeMatcher(String expectedErrorCode) {
        this.expectedErrorCode = expectedErrorCode;
    }

    @Override
    public boolean matchesSafely(final BusinessException exception) {
        this.foundErrorCode = exception.getCode();
        return foundErrorCode.equalsIgnoreCase(expectedErrorCode);
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(foundErrorCode).appendText(" was not found instead of ").appendValue(expectedErrorCode);
    }
}
