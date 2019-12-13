package com.ca.umg.modelet.common;

import java.util.List;

import com.ca.pool.model.TransactionCriteria;

@SuppressWarnings("PMD")
public class HeaderInfo {

	private String modelName;
	private String modelLibraryName;
	private String jarName;
	private String modelClass;
	private String modelMethod;
	private String modelLibraryVersionName;
	private String tenantCode;
	private String version;
	private String engine;
	private int responseSize;
	private List<LibraryInfo> libraries;
	private String modelPackageName;
	private TransactionCriteria transactionCriteria;
	private String commandName;
	private boolean stringsAsFactors;
	private boolean modelSizeReduction;
	private boolean storeRLogs;
	private String modelIdentifier;

	public List<LibraryInfo> getLibraries() {
		return libraries;
	}
	
	public void setLibraries(final List<LibraryInfo> libraries) {
		this.libraries = libraries;
	}

	public String getModelName() {
		return modelName;
	}
	
	public void setModelName(final String modelName) {
		this.modelName = modelName;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(final String version) {
		this.version = version;
	}
	
	public String getEngine() {
		return engine;
	}
	
	public void setEngine(final String engine) {
		this.engine = engine;
	}
	
	public int getResponseSize() {
		return responseSize;
	}
	
	public void setResponseSize(final int responseSize) {
		this.responseSize = responseSize;
	}
	
	public String getJarName() {
		return jarName;
	}
	
	public void setJarName(final String jarName) {
		this.jarName = jarName;
	}
	
	public String getModelClass() {
		return modelClass;
	}
	
	public void setModelClass(final String modelClass) {
		this.modelClass = modelClass;
	}
	
	public String getModelMethod() {
		return modelMethod;
	}
	
	public void setModelMethod(final String modelMethod) {
		this.modelMethod = modelMethod;
	}
	
	public String getTenantCode() {
		return tenantCode;
	}
	
	public void setTenantCode(final String tenantCode) {
		this.tenantCode = tenantCode;
	}
	
	public String getModelLibraryName() {
		return modelLibraryName;
	}
	
	public void setModelLibraryName(final String modelLibraryName) {
		this.modelLibraryName = modelLibraryName;
	}
	
	public String getModelLibraryVersionName() {
		return modelLibraryVersionName;
	}
	
	public void setModelLibraryVersionName(final String modelLibraryVersionName) {
		this.modelLibraryVersionName = modelLibraryVersionName;
	}

	public String getModelPackageName() {
		return modelPackageName;
	}

	public void setModelPackageName(final String modelPackageName) {
		this.modelPackageName = modelPackageName;
	}
	
	public TransactionCriteria getTransactionCriteria() {
		return transactionCriteria;
	}

	public void setTransactionCriteria(TransactionCriteria transactionCriteria) {
		this.transactionCriteria = transactionCriteria;
	}

	public void setCommandName(final String commandName) {
		this.commandName = commandName;
	}
	
	public String getCommandName() {
		return commandName;
	}
	
	public void setStringsAsFactors(final boolean stringsAsFactors) {
		this.stringsAsFactors = stringsAsFactors;
	}
	
	public boolean isStringsAsFactors() {
		return stringsAsFactors;
	}
	
	public void setModelSizeReduction(final boolean modelSizeReduction) {
		this.modelSizeReduction = modelSizeReduction;
	}
	
	public boolean isStoreRLogs() {
		return storeRLogs;
	}

	public void setStoreRLogs(boolean storeRLogs) {
		this.storeRLogs = storeRLogs;
	}

	public boolean isModelSizeReduction() {
		return modelSizeReduction;
	}

	public String getModelIdentifier() {
		return modelIdentifier;
	}

	public void setModelIdentifier(String modelIdentifier) {
		this.modelIdentifier = modelIdentifier;
	}


}
