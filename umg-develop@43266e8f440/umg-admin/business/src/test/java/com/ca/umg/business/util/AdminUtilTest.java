package com.ca.umg.business.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class AdminUtilTest {
	
	@Before
	public void setup(){
		
	}

	@Test
	public void testCountOccurence() {
		assertEquals(2,AdminUtil.countOccurence("Comma,separated,sentence", ","));
		assertEquals(2,AdminUtil.countOccurence("Comma,separated,sentence,", ","));
	}

	@Test
	public void testGetMillisFromString() {
		try {
		    Assert.assertNotNull(AdminUtil.getMillisFromString("2014-12-01_10:30:59", "yyyy-mm-dd_hh:mm:ss").longValue());
		} catch (BusinessException e) {
			fail("Exception while converting date string to milliseconds");
		}
	}

	@Test
	public void testGetDateFormatEpoch() {
	    Assert.assertNotNull( AdminUtil.getDateFormatEpoch(1409057960l, "yyyy-mm-dd_hh:mm:ss"));
	}

	@Test
	public void testGetMillisFromStringJoda() {
		try {
		    Assert.assertNotNull( AdminUtil.getMillisFromStringJoda("2014-12-01_10:30:59", "yyyy-mm-dd_hh:mm:ss").longValue());
		} catch (BusinessException e) {
			fail("Exception while converting date string to milliseconds");
		}
	}

	@Test
	public void testConvertDateFormat() {
		try {
			assertEquals("1983-04-02",AdminUtil.convertDateFormat("DD-MMM-YYYY", "YYYY-MM-DD", "02-APR-1983"));
		} catch (ParseException e) {
			fail("Exception while converting date format");
		}
	}
	
	@Test
	public void testGetLikePattern(){
	    assertEquals("%likepatternstring%",(AdminUtil.getLikePattern("likePatternString")));
	}
	
	@Test
	public void testGenerateUmgName(){
	    Assert.assertNotNull(AdminUtil.generateUmgName("DemoUmg"));
	}
	
	@Test
	public void testGenerateSyndDataTableName(){
	    assertEquals("SYND_DATA_DEMOTABLE",AdminUtil.generateSyndDataTableName("demoTable"));
	}
	
	@Test
	public void testConvertStreamToByteArray() throws SystemException{
	    Assert.assertNotNull(AdminUtil.convertStreamToByteArray(new InputStream() {
            
            @Override
            public int read() throws IOException {
                return 0;
            }
        } ));
	}
	
	@Test
    public void testGetMillisFromStringJodaOffset() {
        try {
            Assert.assertNotNull( AdminUtil.getMillisFromStringJodaOffset("2014-12-01_10:30:59", "yyyy-mm-dd_hh:mm:ss").longValue());
        } catch (BusinessException e) {
            fail("Exception while converting date string to milliseconds");
        }
    }
	
	@Test
    public void testGetDateFormatMillisForEst() {
            Assert.assertNotNull( AdminUtil.getDateFormatMillisForEst(1411495621600L, null));
	}
	
	@Test
    public void testGetDateFormatForEst() {
            Assert.assertNotNull( AdminUtil.getDateFormatForEst(DateTime.now(), null));
	}
	
	@Test
    public void testGetMillisFromEstToUtc() {
        try {
            Assert.assertNotNull( AdminUtil.getMillisFromEstToUtc("2014-SEP-30 10:30",null));
        } catch (BusinessException e) {
            fail("Exception while converting date string to UTC milliseconds");
        }
    }
	
	@Test
    public void testGetMillisFromUtcToEst() {
            try {
				Assert.assertNotNull( AdminUtil.getMillisFromUtcToEst(1411495621600L));
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				 fail("Exception while converting long value to EST milliseconds");
			}
	}
}
