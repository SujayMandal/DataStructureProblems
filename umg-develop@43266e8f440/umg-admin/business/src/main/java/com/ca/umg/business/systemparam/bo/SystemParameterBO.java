package com.ca.umg.business.systemparam.bo;

import java.util.List;

import com.ca.umg.business.systemparam.entity.SystemParameter;

public interface SystemParameterBO {
	void saveParameter(SystemParameter systemParameter);
	
	List<SystemParameter> getSysParameters();
}
