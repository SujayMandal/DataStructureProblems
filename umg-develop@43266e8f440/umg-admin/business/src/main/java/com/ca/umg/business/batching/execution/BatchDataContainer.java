package com.ca.umg.business.batching.execution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Named;

import com.ca.framework.core.batch.TransactionStatus;

@Named
public class BatchDataContainer {
	
	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";
    public static final String TERMINATED = TransactionStatus.TERMINATED.getStatus();

	private final Map<String,ResponseObj> batchMap = new ConcurrentHashMap<String, ResponseObj>();
	
	public Map<String,ResponseObj> getResponseMap() {
		return batchMap;
	}
	
	public void initializeForBatch(String batchId, int batchSize){
		Map<String, List<String>> statusMap = new ConcurrentHashMap<String, List<String>>();
		statusMap.put(SUCCESS, Collections.synchronizedList(new ArrayList<String>()));
		statusMap.put(FAILURE, Collections.synchronizedList(new ArrayList<String>()));
        statusMap.put(TERMINATED, Collections.synchronizedList(new ArrayList<String>()));
		ResponseObj responseObj = new ResponseObj();
		responseObj.setRequestCount(batchSize);
		responseObj.setBatchId(batchId);
		responseObj.setStatus(statusMap);
		batchMap.put(batchId, responseObj);
	}
}