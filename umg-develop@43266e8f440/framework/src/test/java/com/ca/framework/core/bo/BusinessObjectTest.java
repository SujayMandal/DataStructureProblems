package com.ca.framework.core.bo;

import org.junit.Test;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.codes.FrameworkExceptionCodes;

import junit.framework.Assert;

public class BusinessObjectTest {
	@Test
	public void testValidatePositive() {
		MockBusinessObject mock = new MockBusinessObject();
		MockEntity mockEntity = new MockEntity();
		mockEntity.setName("testName");
		try {
			mock.validate(mockEntity);
		} catch (BusinessException e) {
			Assert.assertEquals(e.getCode(), FrameworkExceptionCodes.FSE0000001);
		}
	}

	@Test
	public void testValidateNegative() {
		MockBusinessObject mock = new MockBusinessObject();
		MockEntity mockEntity = new MockEntity();
		try {
			mock.validate(mockEntity);
		} catch (BusinessException e) {
			Assert.assertEquals(e.getCode(), FrameworkExceptionCodes.FSE0000001);
		}
	}
}
