package com.ca.umg.modelet.runtime.impl;

public class RPackageStats {

	private String packageName;
	private String jarName;
	private String size;
	private long installTime;
	private long loadTime;
	private long addLibPathTime;
	private long executionTime;
	private boolean model;
	
	public String getPackageName() {
		return packageName;
	}
	
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public String getJarName() {
		return jarName;
	}
	
	public void setJarName(String jarName) {
		this.jarName = jarName;
	}
	
	public String getSize() {
		return size;
	}
	
	public void setSize(String size) {
		this.size = size;
	}
	
	public long getInstallTime() {
		return installTime;
	}
	
	public void setInstallTime(long installTime) {
		this.installTime = installTime;
	}
	
	public long getLoadTime() {
		return loadTime;
	}
	
	public void setLoadTime(long loadTime) {
		this.loadTime = loadTime;
	}
	
	public long getAddLibPathTime() {
		return addLibPathTime;
	}
	
	public void setAddLibPathTime(long addLibPathTime) {
		this.addLibPathTime = addLibPathTime;
	}
	
	public long getExecutionTime() {
		return executionTime;
	}
	
	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}
	
	public boolean isModel() {
		return model;
	}

	public void setModel(boolean model) {
		this.model = model;
	}
	
	@Override
	public String toString() {
		return getPackageName() + "," + getSize() + "," + getInstallTime() + "," + getLoadTime() + "," +  getAddLibPathTime() + "," + (getInstallTime() + getLoadTime() + getAddLibPathTime()) + "," + getExecutionTime();
	}
}