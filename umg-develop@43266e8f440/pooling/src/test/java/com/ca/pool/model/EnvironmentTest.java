package com.ca.pool.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class EnvironmentTest {

	@Test
	public void getEnvironmentTest() {
		ExecutionLanguage e = ExecutionLanguage.getEnvironment(ExecutionLanguage.MATLAB.getValue());		
		assertTrue(ExecutionLanguage.MATLAB == e);
		
		e = ExecutionLanguage.getEnvironment(ExecutionLanguage.R.getValue());
		
		assertTrue(ExecutionLanguage.R == e);
		
		e = ExecutionLanguage.getEnvironment("");
		
		assertTrue(null == e);
	}
}
