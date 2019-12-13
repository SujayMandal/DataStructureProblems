package com.ca.umg.business.transaction.report;

import com.ca.framework.core.systemparameter.SystemParameterProvider;

@SuppressWarnings("PMD")
public class MySystemParameterProvider implements SystemParameterProvider{

	@Override
	public String getParameter(String key) {
		return null;
	}
	
	// added this empty method as there is no usage
	@Override
	public void refreshCache() {
		
	}
}