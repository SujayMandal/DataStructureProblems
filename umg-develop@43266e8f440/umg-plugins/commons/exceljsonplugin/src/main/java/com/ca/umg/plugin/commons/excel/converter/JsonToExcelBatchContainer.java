package com.ca.umg.plugin.commons.excel.converter;

import java.io.ByteArrayOutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JsonToExcelBatchContainer {
	private boolean isFinished;
	private String batchId;
	private String location;
	private Queue<TransactionElement> transactionElementQueue = new ConcurrentLinkedQueue<>();
	private ByteArrayOutputStream batchOutput;
	private boolean writeOutputToFile = true;
	private String extension;


	public boolean isFinished() {
		return isFinished;
	}

	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Queue<TransactionElement> getTransactionElementQueue() {
		return transactionElementQueue;
	}
	public boolean isEmptyQueue(){
		return transactionElementQueue.isEmpty();
	}
	
	/**
	 * This method sets the batch output & notifies the thread waiting for output.
	 */
	public synchronized void setBatchOutput(ByteArrayOutputStream batchOutputStream) {
		batchOutput = batchOutputStream;
		notify();
	}

	/**
	 * This method returns a valid stream by waiting for the output to be set by setBatchOutput(ByteArrayOutputStream batchOutputStream)
	 */
	public synchronized ByteArrayOutputStream getBatchOutput() {
		if (!writeOutputToFile) {
			while (batchOutput == null) {
				try {
					wait();
				} catch (InterruptedException ex) {
					// DO NOTHING
				}
			}
		}
		return batchOutput;
	}

	public boolean isWriteOutputToFile() {
		return writeOutputToFile;
	}

	public void setWriteOutputToFile(boolean writeOutputToFile) {
		this.writeOutputToFile = writeOutputToFile;
	}
	
	/**
	 * @return the extension
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * @param extension the extension to set
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}

}