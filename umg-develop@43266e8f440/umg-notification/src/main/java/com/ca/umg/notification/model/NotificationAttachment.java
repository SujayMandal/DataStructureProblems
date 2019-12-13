package com.ca.umg.notification.model;

import java.io.File;

public class NotificationAttachment {

	private String fileName;
	
	private File file;
	
	private boolean deleteFileAfterSend;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public boolean isDeleteFileAfterSend() {
		return deleteFileAfterSend;
	}

	public void setDeleteFileAfterSend(boolean deleteFileAfterSend) {
		this.deleteFileAfterSend = deleteFileAfterSend;
	}
}
