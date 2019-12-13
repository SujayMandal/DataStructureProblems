/**
 * 
 */
package com.ca.umg.business.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;

import com.ca.framework.core.util.CheckSumUtil;

/**
 * @author elumalas
 *
 */
public class CheckSumUtilTest {

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
    
    @Test
    public void testValidateCheckSumByMD5() {
        final String checkSumStr = "This is testing checksum validation string";
        byte[] checkSumData = checkSumStr.getBytes();
        boolean status = false;
        try {
            status = CheckSumUtil.validateCheckSumByMD5(checkSumData,
                    "9efda66aa3929771ece1f120fe4be7bb");
            assertTrue(status);
        } catch (final Exception exp) {
            fail(exp.getMessage());
        }
    }
    
    @Test
    public void testNegativeValidateCheckSumByMD5() {
        final String checkSumStr = "This is testing checksum validation string";
        byte[] checkSumData = checkSumStr.getBytes();
        boolean status = false;
        try {
            status = CheckSumUtil.validateCheckSumByMD5(checkSumData,
                    "9efda66aa3929771ece1f120fe4be7ba");
            assertFalse(status);
        } catch (final Exception exp) {
            fail(exp.getMessage());
        }
    }
    
    @Test
    public void testValidateCheckSumBySHA512() {
        final String checkSumStr = "This is testing checksum validation string";
        byte[] checkSumData = checkSumStr.getBytes();
        boolean status = false;
        try {
            status = CheckSumUtil.validateCheckSumBySHA512(checkSumData,
                    "0b28d8f73bc72b11c39bca9efb278b0e820838995a969259dcbb694c172af79c7550b35779a44f88c999075c523d4f94eb00d09eacb7ad37196e6705fe94cf0d");
            assertTrue(status);
        } catch (final Exception exp) {
            fail(exp.getMessage());
        }
    }
    
    @Test
    public void testNegativeValidateCheckSumBySHA512() {
        final String checkSumStr = "This is testing checksum validation string";
        byte[] checkSumData = checkSumStr.getBytes();
        boolean status = false;
        try {
            status = CheckSumUtil.validateCheckSumBySHA512(checkSumData,
                    "0b28d8f73bc72b11c39bca9efb278b0e820838995a969259dcbb694c172af79c7550b35779a44f88c999075c523d4f94eb00d09eacb7ad37196e6705fe94cf0a");
            assertFalse(status);
        } catch (final Exception exp) {
            fail(exp.getMessage());
        }
    }
    
    @Test
    public void testValidateCheckSumBySHA384() {
        final String checkSumStr = "This is testing checksum validation string";
        byte[] checkSumData = checkSumStr.getBytes();
        boolean status = false;
        try {
            status = CheckSumUtil.validateCheckSumBySHA384(checkSumData,
                    "76c348dcdc13f74fcbb92fc917b252327cbf491fb34ecbc9879585b038680d959954254ceebf20eba82087416f1d3791");
            assertTrue(status);
        } catch (final Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public void testNegativeValidateCheckSumBySHA384() {
        final String checkSumStr = "This is testing checksum validation string";
        byte[] checkSumData = checkSumStr.getBytes();
        boolean status = false;
        try {
            status = CheckSumUtil.validateCheckSumBySHA384(checkSumData,
                    "76c348dcdc13f74fcbb92fc917b252327cbf491fb34ecbc9879585b038680d959954254ceebf20eba82087416f1d3792");
            assertFalse(status);
        } catch (final Exception exp) {
            fail(exp.getMessage());
        }
    }
    
    @Test
    public void testValidateCheckSumBySHA1() {
        final String checkSumStr = "This is testing checksum validation string";
        byte[] checkSumData = checkSumStr.getBytes();
        boolean status = false;
        try {
            status = CheckSumUtil.validateCheckSumBySHA1(checkSumData,
                    "52134dc86ad76863e15b4659b7da65907d20d689");
            assertTrue(status);
        } catch (final Exception exp) {
            fail(exp.getMessage());
        }
    }
    
    @Test
    public void testNegativeValidateCheckSumBySHA1() {
        final String checkSumStr = "This is testing checksum validation string";
        byte[] checkSumData = checkSumStr.getBytes();
        boolean status = false;
        try {
            status = CheckSumUtil.validateCheckSumBySHA1(checkSumData,
                    "52134dc86ad76863e15b4659b7da65907d20d688");
            assertFalse(status);
        } catch (final Exception exp) {
            fail(exp.getMessage());
        }
    }
    
    @Test
    public void testGetCheckSumValue() {
        final String checkSumStr = "This is testing checksum validation string";
        byte[] checkSumData = checkSumStr.getBytes();
        final String str1 = CheckSumUtil.getCheckSumValue(checkSumData, "SHA256");
        assertEquals("3140f8e174991abd787f9c1ff5069550541f380b8f62bf41db37f520f0b83406", str1);
        final String str2 = CheckSumUtil.getCheckSumValue(checkSumData, "MD5");
        assertEquals("9efda66aa3929771ece1f120fe4be7bb", str2);
        final String str3 = CheckSumUtil.getCheckSumValue(checkSumData, "SHA512");
        assertEquals("0b28d8f73bc72b11c39bca9efb278b0e820838995a969259dcbb694c172af79c7550b35779a44f88c999075c523d4f94eb00d09eacb7ad37196e6705fe94cf0d", str3);
        final String str4 = CheckSumUtil.getCheckSumValue(checkSumData, "SHA384");
        assertEquals("76c348dcdc13f74fcbb92fc917b252327cbf491fb34ecbc9879585b038680d959954254ceebf20eba82087416f1d3791", str4);
        final String str5 = CheckSumUtil.getCheckSumValue(checkSumData, "SHA1");
        assertEquals("52134dc86ad76863e15b4659b7da65907d20d689", str5);
        final String str6 = CheckSumUtil.getCheckSumValue(checkSumData, " ");
        assertEquals("3140f8e174991abd787f9c1ff5069550541f380b8f62bf41db37f520f0b83406", str6);
    }
}
