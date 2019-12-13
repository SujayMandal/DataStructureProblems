package com.ca.umg.business.pooling.model;

import java.util.Comparator;

import com.ca.umg.business.constants.BusinessConstants;

public class CompletePoolDetailsComparator implements Comparator<CompletePoolDetails>{

	@Override
    public int compare(CompletePoolDetails firstCompletePoolDetails, CompletePoolDetails secondCompletePoolDetails) {
		int returnValue;
        returnValue = compareString(firstCompletePoolDetails.getPool().getExecutionLanguage(), secondCompletePoolDetails
                .getPool().getExecutionLanguage());
        if(returnValue == BusinessConstants.NUMBER_ZERO) {
            returnValue = compareString(firstCompletePoolDetails.getPool().getExecutionEnvironment(), secondCompletePoolDetails
                    .getPool().getExecutionEnvironment());
            if(returnValue == BusinessConstants.NUMBER_ZERO) {
                returnValue = compareString(firstCompletePoolDetails.getPool().getPoolName(), secondCompletePoolDetails
                        .getPool().getPoolName());
            }
        }
		return returnValue;
	}

    public int compareString(String firstPoolCriteria, String secondPoolCriteria) {
    	int result = -1; 
        if (firstPoolCriteria != null && secondPoolCriteria != null) {
        	result = firstPoolCriteria.compareToIgnoreCase(secondPoolCriteria);
        }
        return result;
    }
}