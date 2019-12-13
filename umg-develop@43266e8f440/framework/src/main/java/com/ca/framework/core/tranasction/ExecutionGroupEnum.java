package com.ca.framework.core.tranasction;

public enum ExecutionGroupEnum {

	BENCHMARK("Benchmark"), MODELED("Modeled"), INELIGIBLE("Ineligible");
	
    private String executionGroup;

    private ExecutionGroupEnum(String executionGroup) {
        this.executionGroup = executionGroup;
    }

	public String getExposedApiParam() {
        return executionGroup;
    }
	
	public static Boolean isValid(final String execGroup) {
        Boolean isValid = Boolean.FALSE;
        if (execGroup != null) {
            for (ExecutionGroupEnum executionGroupEnum : values()) {
                if (executionGroupEnum.getExposedApiParam().equals(execGroup)) {
                    isValid = Boolean.TRUE;
                    break;
                }
            }
        }
        return isValid;
    }
}