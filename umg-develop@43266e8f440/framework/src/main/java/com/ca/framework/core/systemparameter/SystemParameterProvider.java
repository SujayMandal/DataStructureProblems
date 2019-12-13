package com.ca.framework.core.systemparameter;

public interface SystemParameterProvider {
	String REQUIRE_MODEL_SIZE_REDUCTION = "MODEL_SIZE_REDUCTION";
	String SYSTEM_PARAMETER="SYSTEM_PARAMETER_MAP";
	String getParameter(String key);
	
	/**
	 * added this method to refresh the cache from me2
	 */
	public void refreshCache();
}
