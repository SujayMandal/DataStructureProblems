package com.ca.umg.business.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.exception.SystemException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class XmlValidatorTest {

	@Test
	public final void validatePositive() throws SystemException, IOException {
		InputStream xsd = null;
		InputStream xml = null;
		try {
			xsd = this.getClass().getClassLoader().getResourceAsStream("schema/matlab/UMG-MATLAB-IO.XSD");
			xml = this.getClass().getClassLoader().getResourceAsStream("com/ca/umg/business/util/UMG-MATLAB-IO.XML");
			assertNotNull(xsd);
			assertNotNull(xml);
			assertTrue(XmlValidator.validate(xsd, xml));
		} finally {
			try {
				if (xsd != null) {
					xsd.close();
				}
				if (xml != null) {
					xml.close();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	@Test
	public final void validateNegative() throws SystemException, IOException {
		InputStream xsd = null;
		InputStream xml = null;
		try {
			xsd = this.getClass().getClassLoader().getResourceAsStream("schema/matlab/UMG-MATLAB-IO.XSD");
			xml = this.getClass().getClassLoader()
					.getResourceAsStream("com/ca/umg/business/util/INVALID_UMG-MATLAB-IO.XML");
			assertNotNull(xsd);
			assertNotNull(xml);
			assertFalse(XmlValidator.validate(xsd, xml));
		} finally {
			try {
				if (xsd != null) {
					xsd.close();
				}
				if (xml != null) {
					xml.close();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@Test
	public final void validate() throws SystemException, IOException {
		InputStream xsd = null;
		InputStream xml = null;
		try {
			xsd = this.getClass().getClassLoader().getResourceAsStream("schema/matlab/UMG-MATLAB-IO.XSD");
			xml = this.getClass().getClassLoader().getResourceAsStream("com/ca/umg/business/util/AQMK.xml");
			assertNotNull(xsd);
			assertNotNull(xml);
			assertTrue(XmlValidator.validate(xsd, xml));
		} finally {
			try {
				if (xsd != null) {
					xsd.close();
				}
				if (xml != null) {
					xml.close();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

}
