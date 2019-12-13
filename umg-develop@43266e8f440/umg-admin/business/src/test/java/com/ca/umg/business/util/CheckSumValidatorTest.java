/**
 * 
 */
package com.ca.umg.business.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;

import com.ca.framework.core.util.CheckSumUtil;

/**
 * @author elumalas
 *
 */
public class CheckSumValidatorTest {

    @Test
    public void testValidateSH256CheckSum() {
        final String checkSumStr = "This is testing checksum validation string";
        byte[] checkSumData = checkSumStr.getBytes();
        boolean status = false;
        try {
            status = CheckSumUtil.validateCheckSumBySHA256(checkSumData,
                    "3140f8e174991abd787f9c1ff5069550541f380b8f62bf41db37f520f0b83406");
            assertTrue(status);
        } catch (final Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public void testNegativeValidateSH256CheckSum() {
        final String checkSumStr = "This is testing checksum validation string";
        byte[] checkSumData = checkSumStr.getBytes();
        boolean status = false;
        try {
            status = CheckSumUtil.validateCheckSumBySHA256(checkSumData,
                    "3140f8e174991abd787f9c1ff5069550541f380b8f62bf41db37f520f0b83423");
            Assert.assertFalse(status);
        } catch (final Exception exp) {
            fail(exp.getMessage());
        }
    }

}
