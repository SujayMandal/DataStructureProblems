package com.ca.umg.plugin.commons.excel.converter;

public interface JsonToExcelConverter {
	public void start(String batchId, String location);
	/**
	 * This method takes an input of batchId, the file location & a flag for writing it to the file or not.
	 * If this flag is true, it writes the generated excel in to file location given. Else it writes back to the batch container, which can then be accessed
	 * via getBatchOutput() method of container.
	 */
	public JsonToExcelBatchContainer start(String batchId, String location,boolean writeOutputToFile,String extension);
	public void stop(String batchId);

    /**
     * @param batchId
     * @param transactionId
     * @param response
     * @param index
     * @param isError
     *            This method used to add transactions if any validation errors,missing column errors and system exceptions
     */

    public void addTransaction(String batchId, String transactionId, Object response, int index, boolean isError);
	public String getLocation(String batchId);
	public void addTerminatedTransaction(String batchId, String transactionId, Object response, int index);
}
