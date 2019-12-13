package com.ca.umg.rt.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.umg.rt.core.flow.entity.TransactionLog;

public final class IOTransformerUtil {
	
	private IOTransformerUtil(){
		
	}
	
	public static void setAddOnValidations(TransactionLog transactionLog, Map<String, Object> tntRqstHeader) {
		if (tntRqstHeader.get(FrameworkConstant.ADD_ON_VALIDATION)!=null) {
			List<String> addOnValidation =(List<String>) tntRqstHeader.get(FrameworkConstant.ADD_ON_VALIDATION);
			if(CollectionUtils.isNotEmpty(addOnValidation) && addOnValidation.contains(FrameworkConstant.MODEL_OUTPUT)){
				transactionLog.setOpValidation(Boolean.TRUE);
		    }else{
		    	transactionLog.setOpValidation(Boolean.FALSE);
		    }
			if(CollectionUtils.isNotEmpty(addOnValidation) && addOnValidation.contains(FrameworkConstant.ACCEPTABLE_VALUES)){
				transactionLog.setACceptValuesValidation(Boolean.TRUE);
		    }else{
		    	transactionLog.setACceptValuesValidation(Boolean.FALSE);
		    }
		}
	}

	
	

}
