package com.ca.umg.modelet.runtime.impl;

public class RCommandsStatus {

	private boolean loadLibraries;
	private boolean loadedLibraries;
	private boolean unloadLibraries;

	private boolean loadModel;
	private boolean loadedModel;
	private boolean unloadModel;
	
	public boolean isLoadLibraries() {
		return loadLibraries;
	}
	
	public void setLoadLibraries(boolean loadLibraries) {
		this.loadLibraries = loadLibraries;
	}

	public boolean isLoadedLibraries() {
		return loadedLibraries;
	}
	
	public void setLoadedLibraries(boolean loadedLibraries) {
		this.loadedLibraries = loadedLibraries;
	}

	public boolean isUnloadLibraries() {
		return unloadLibraries;
	}
	
	public void setUnloadLibraries(boolean unloadLibraries) {
		this.unloadLibraries = unloadLibraries;
	}
	
	public boolean isLoadModel() {
		return loadModel;
	}
	
	public void setLoadModel(boolean loadModel) {
		this.loadModel = loadModel;
	}

	public boolean isLoadedModel() {
		return loadedModel;
	}
	
	public void setLoadedModel(boolean loadedModel) {
		this.loadedModel = loadedModel;
	}

	public boolean isUnloadModel() {
		return unloadModel;
	}

	public void setUnloadModel(boolean unloadModel) {
		this.unloadModel = unloadModel;
	}
}
