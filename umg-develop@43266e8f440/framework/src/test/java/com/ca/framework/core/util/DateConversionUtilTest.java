package com.ca.framework.core.util;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class DateConversionUtilTest {

	@Before
	public void setup(){
		
	}

	@Test
    public void testConvertEstToUtcTimeZone() {
            Assert.assertNotNull( DateConversionUtil.convertEstToUtcTimeZone(DateTime.now()));
	}
	
	@Test
    public void testNegativeConvertEstToUtcTimeZone() {
            Assert.assertNull( DateConversionUtil.convertEstToUtcTimeZone(null));
	}
	@Test
    public void testConvertUtcToEstTimeZone() {
            Assert.assertNotNull( DateConversionUtil.convertUtcToEstTimeZone(DateTime.now()));
	}
	
	@Test
    public void testNegativeConvertUtcToEstTimeZone() {
            Assert.assertNull( DateConversionUtil.convertUtcToEstTimeZone(null));
	}
	
	@Test
    public void testConvertToUtcForAuditable() {
            Assert.assertNotNull( DateConversionUtil.convertToUtcForAuditable(DateTime.now()));
	}
	
	@Test
    public void testNegativeConvertToUtcForAuditable() {
            Assert.assertNotNull( DateConversionUtil.convertToUtcForAuditable(null));
	}
}
