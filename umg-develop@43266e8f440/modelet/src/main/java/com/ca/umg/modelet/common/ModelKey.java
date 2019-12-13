package com.ca.umg.modelet.common;

import java.util.List;
import java.util.Map;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.Property;

import com.ca.umg.modelet.runtime.impl.RCommandsStatus;

public class ModelKey {

	@Property
	private String modelName;

	@Property
	private String modelLibrary;

	@Property
	private String modelClass;

	@Property
	private String modelMethod;

	@Property
	private String umgName;

	@Property
	private String tenantCode;

	private String filePath;
	
	private String localFilePath;
	
	private String jarName;
	
	private String modelPackageName;

	private List<String> libraryNames;
	
	private Map<String, String> libInstallPathByPackage;
	
	private RCommandsStatus commandsStatus = new RCommandsStatus();

	public List<String> getLibraryNames() {
		return libraryNames;
	}

	public void setLibraryNames(final List<String> libraryNames) {
		this.libraryNames = libraryNames;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(final String modelName) {
		this.modelName = modelName;
	}

	public String getModelLibrary() {
		return modelLibrary;
	}

	public void setModelLibrary(final String modelLibrary) {
		this.modelLibrary = modelLibrary;
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

	public String getUmgName() {
		return umgName;
	}

	public void setUmgName(final String umgName) {
		this.umgName = umgName;
	}

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(final String tenantCode) {
		this.tenantCode = tenantCode;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(final String filePath) {
		this.filePath = filePath;
	}

	@Override
	public final boolean equals(final Object obj) {
		return Pojomatic.equals(this, obj);
	}

	@Override
	public final int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	public void setModelPackageName(final String modelPackageName) {
		this.modelPackageName = modelPackageName;
	}

	public String getModelPackageName() {
		return modelPackageName;
	}
	public Map<String, String> getLibInstallPathByPackage() {
		return libInstallPathByPackage;
	}
	
	public void setLibInstallPathByPackage(final Map<String, String> libInstallPathByPackage) {
		this.libInstallPathByPackage = libInstallPathByPackage;
	}
	
	public void setCommandsStatus(final RCommandsStatus commandsStatus) {
		this.commandsStatus = commandsStatus;
	}
	
	public RCommandsStatus getCommandsStatus() {
		return commandsStatus;
	}

	public String getLocalFilePath() {
		return localFilePath;
	}

	public void setLocalFilePath(String localFilePath) {
		this.localFilePath = localFilePath;
	}
}