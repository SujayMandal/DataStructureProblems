package com.ca.umg.business.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DataTypeUtilsTest {

    @Test
    public void isNotBooleanTestForTrue() {
        assertFalse(DataTypeUtils.isNotBoolean("true"));
    }

    @Test
    public void isNotBooleanTestForFalse() {
        assertFalse(DataTypeUtils.isNotBoolean("false"));
    }

    @Test
    public void isNotBooleanTestForJunk() {
        assertTrue(DataTypeUtils.isNotBoolean("junk"));
    }

    @Test
    public void isNotDateTestForJunk() {
        assertTrue(DataTypeUtils.isNotDate("junk"));
    }

    @Test
    public void isNotDateTest() {
        assertFalse(DataTypeUtils.isNotDate("23-JAN-2142"));
    }

    @Test
    public void isNotCharTestFail() {
        assertTrue(DataTypeUtils.isNotChar("JAN-23-2142"));
    }

    @Test
    public void isNotCharTest() {
        assertFalse(DataTypeUtils.isNotChar("J"));
    }

    @Test
    public void isNotIntegerTestFail() {
        assertTrue(DataTypeUtils.isNotInteger("J"));
    }

    @Test
    public void isNotIntegerTest() {
        assertFalse(DataTypeUtils.isNotInteger("1"));
    }

}
