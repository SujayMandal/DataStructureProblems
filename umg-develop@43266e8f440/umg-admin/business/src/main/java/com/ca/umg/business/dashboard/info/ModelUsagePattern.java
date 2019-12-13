package com.ca.umg.business.dashboard.info;

import java.util.List;

public class ModelUsagePattern {

	private String interval;
	
	private List<ModelUsageInfo> modelUsageInfos;

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	public List<ModelUsageInfo> getModelUsageInfos() {
		return modelUsageInfos;
	}

	public void setModelUsageInfos(List<ModelUsageInfo> modelUsageInfos) {
		this.modelUsageInfos = modelUsageInfos;
	}
	
	
}
