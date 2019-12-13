package com.ca.framework.core.rmodel.info;

import java.io.Serializable;

public class VersionExecInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6502416903653947134L;

	private String execLanguage;
	

	private String execLangVer;
	
	private String execEnv;
	
	

	public String getExecLangVer() {
		return execLangVer;
	}

	public void setExecLangVer(String execLangVer) {
		this.execLangVer = execLangVer;
	}

	public String getExecEnv() {
		return execEnv;
	}

	public void setExecEnv(String execEnv) {
		this.execEnv = execEnv;
	}
	
	public String getExecLanguage() {
		return execLanguage;
	}

	public void setExecLanguage(String execLanguage) {
		this.execLanguage = execLanguage;
	}

	

	

}
