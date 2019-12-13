package com.ca.umg.modelet.common;


public class LibraryInfo implements Comparable<LibraryInfo> {

	private String versionName;
	private String majorVersion;
	private String minorVersion;

	private String modelLibraryName;
	private String jarName;
	private String modelLibraryVersionName;

	private Integer hierarchy;
	private String packageName;
	private String packageFolder;
	private String packageVersion;
	private String packageType;
	private String compiledOs;
	private String execEnv;
	private String envVersion;

	public String getModelLibraryName() {
		return modelLibraryName;
	}

	public void setModelLibraryName(final String modelLibraryName) {
		this.modelLibraryName = modelLibraryName;
	}

	public String getJarName() {
		return jarName;
	}

	public void setJarName(final String jarName) {
		this.jarName = jarName;
	}

	public String getModelLibraryVersionName() {
		return modelLibraryVersionName;
	}

	public void setModelLibraryVersionName(final String modelLibraryVersionName) {
		this.modelLibraryVersionName = modelLibraryVersionName;
	}

	public Integer getHierarchy() {
		return hierarchy;
	}

	public void setHierarchy(final Integer hierarchy) {
		this.hierarchy = hierarchy;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(final String packageName) {
		this.packageName = packageName;
	}

	public String getPackageFolder() {
		return packageFolder;
	}

	public void setPackageFolder(final String packageFolder) {
		this.packageFolder = packageFolder;
	}

	public String getPackageVersion() {
		return packageVersion;
	}

	public void setPackageVersion(final String packageVersion) {
		this.packageVersion = packageVersion;
	}

	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(final String packageType) {
		this.packageType = packageType;
	}

	public String getCompiledOs() {
		return compiledOs;
	}

	public void setCompiledOs(final String compiledOs) {
		this.compiledOs = compiledOs;
	}

	public String getExecEnv() {
		return execEnv;
	}

	public void setExecEnv(final String execEnv) {
		this.execEnv = execEnv;
	}

	public String getEnvVersion() {
		return envVersion;
	}

	public void setEnvVersion(final String envVersion) {
		this.envVersion = envVersion;
	}

	@Override
	public int compareTo(final LibraryInfo o) {
		return getHierarchy().compareTo(o.getHierarchy());
	}

	@Override
	public boolean equals(Object obj) {
		boolean flag = false;
		if (obj instanceof LibraryInfo) {
			flag = this.getHierarchy().equals(((LibraryInfo) obj).getHierarchy());
		}

		return flag;
	}

	@Override
	public int hashCode() {
		return getHierarchy();
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(String majorVersion) {
		this.majorVersion = majorVersion;
	}

	public String getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(String minorVersion) {
		this.minorVersion = minorVersion;
	}
}