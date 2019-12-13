package com.ca.umg.modelet.converter;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.modelet.common.ModelRequestInfo;

public interface Converter {

	Object marshall(ModelRequestInfo modelRequestInfo) throws BusinessException;

	Object unmarshall(Object response) throws BusinessException;
	
	Object unmarshall(final Object response, final boolean reduceModelSize) throws BusinessException;
}