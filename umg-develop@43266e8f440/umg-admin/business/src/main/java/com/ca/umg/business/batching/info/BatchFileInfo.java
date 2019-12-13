/**
 * 
 */
package com.ca.umg.business.batching.info;

import java.io.InputStream;

/**
 * @author nigampra
 * 
 */
public class BatchFileInfo {

	private InputStream fileInputStream;
	private String fileName;

	public InputStream getFileInputStream() {
		return fileInputStream;
	}

	public void setFileInputStream(InputStream fileInputStream) {
		this.fileInputStream = fileInputStream;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
