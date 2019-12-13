package com.ca.umg.plugin.commons.excel.converter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Named;

import com.ca.umg.plugin.commons.excel.reader.constants.ExcelConstants;

@Named
public class JsonToExcelConverterImpl implements JsonToExcelConverter {
	private static final int POOL_SIZE = 2;//TODO Pick from umgproperties
	private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(POOL_SIZE);

	private Map<String, JsonToExcelBatchContainer> batchTransactionQueueMap = new HashMap<>();

	public void start(String batchId, String location) {
		JsonToExcelBatchContainer jsonToExcelBatchContainer = batchTransactionQueueMap
				.get(batchId);
		if (jsonToExcelBatchContainer == null) {
			jsonToExcelBatchContainer = new JsonToExcelBatchContainer();
			jsonToExcelBatchContainer.setBatchId(batchId);
			jsonToExcelBatchContainer.setLocation(location);
			jsonToExcelBatchContainer.setFinished(false);
			batchTransactionQueueMap.put(batchId, jsonToExcelBatchContainer);			
			JsonToExcelPreparator jsonToExcelPreparator=new JsonToExcelPreparator(jsonToExcelBatchContainer);
			EXECUTOR_SERVICE.execute(jsonToExcelPreparator);
		}
	}
	
	/**
	 * This method takes an input of batchId, the file location & a flag for writing it to the file or not.
	 * If this flag is true, it writes the generated excel in to file location given. Else it writes back to the batch container, which can then be accessed
	 * via getBatchOutput() method of container.
	 */
	public JsonToExcelBatchContainer start(String batchId, String location, boolean writeOutputToFile,String extension) {
		JsonToExcelBatchContainer jsonToExcelBatchContainer = batchTransactionQueueMap.get(batchId);
		if (jsonToExcelBatchContainer == null) {
			jsonToExcelBatchContainer = new JsonToExcelBatchContainer();
			jsonToExcelBatchContainer.setBatchId(batchId);
			jsonToExcelBatchContainer.setLocation(location);
			jsonToExcelBatchContainer.setFinished(false);
			jsonToExcelBatchContainer.setWriteOutputToFile(writeOutputToFile);
			jsonToExcelBatchContainer.setExtension(extension);
			batchTransactionQueueMap.put(batchId, jsonToExcelBatchContainer);			
			JsonToExcelPreparator jsonToExcelPreparator=new JsonToExcelPreparator(jsonToExcelBatchContainer);
			EXECUTOR_SERVICE.execute(jsonToExcelPreparator);
		}
		return jsonToExcelBatchContainer;
	}
	
	public String getLocation(String batchId){
		String location = null;
		JsonToExcelBatchContainer jsonToExcelBatchContainer = batchTransactionQueueMap
				.get(batchId);
		if(jsonToExcelBatchContainer !=null){
			location=jsonToExcelBatchContainer.getLocation();
		}
		return location;
	}
	public void stop(String batchId) {
		JsonToExcelBatchContainer jsonToExcelBatchContainer = batchTransactionQueueMap
				.get(batchId);
		if (jsonToExcelBatchContainer != null) {
			jsonToExcelBatchContainer.setFinished(true);
			batchTransactionQueueMap.remove(batchId);
		}
	}

	

    @Override
    public void addTransaction(String batchId, String transactionId, Object response, int index, boolean isError) {
        JsonToExcelBatchContainer jsonToExcelBatchContainer = batchTransactionQueueMap.get(batchId);
        if (jsonToExcelBatchContainer != null) {
            TransactionElement transactionElement = new TransactionElement();
            transactionElement.setTransactionId(transactionId);
            transactionElement.setResponse(response instanceof Map ? new HashMap((Map)response) : response);
            transactionElement.setIndex(index);
            transactionElement.setError(isError);
            Map<String,Object> map = (Map<String, Object>) ((Map<String,Object>)response).get("data");		
			transactionElement.setIndex((Integer)map.get(ExcelConstants.ROW_NO));
			map.remove(ExcelConstants.ROW_NO);
            jsonToExcelBatchContainer.getTransactionElementQueue().add(transactionElement);
        }
    }
    
    @Override
    public void addTerminatedTransaction(String batchId, String transactionId, Object response, int index) {
        JsonToExcelBatchContainer jsonToExcelBatchContainer = batchTransactionQueueMap.get(batchId);
        if (jsonToExcelBatchContainer != null) {
            TransactionElement transactionElement = new TransactionElement();
            transactionElement.setTransactionId(transactionId);
            transactionElement.setResponse(response instanceof Map ? new HashMap((Map)response) : response);
            transactionElement.setIndex(index);
            transactionElement.setTerminated(true);
            jsonToExcelBatchContainer.getTransactionElementQueue().add(transactionElement);
        }
    }

}
