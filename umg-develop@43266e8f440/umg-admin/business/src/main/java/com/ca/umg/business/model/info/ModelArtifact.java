package com.ca.umg.business.model.info;

import java.io.InputStream;
import java.util.Arrays;

import com.ca.framework.core.info.BaseInfo;

public class ModelArtifact extends BaseInfo {

    private static final long serialVersionUID = 3215718959186552558L;

    private String name;

    private InputStream data;

    private String modelName;

    private String contentType;

    private String umgName;

    private byte[] dataArray;
    
    private String absolutePath;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InputStream getData() {
        return data;
    }

    public void setData(InputStream data) {
        this.data = data;
    }

    public String getUmgName() {
        return umgName;
    }

    public void setUmgName(String umgName) {
        this.umgName = umgName;
    }

    public byte[] getDataArray() {
        return dataArray != null ? Arrays.copyOf(dataArray, dataArray.length) : null;
    }

    public void setDataArray(byte[] dataArray) {
        if (dataArray != null) {
            this.dataArray = Arrays.copyOf(dataArray, dataArray.length);
        }
    }

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

    
}
