package com.ca.umg.business.model.info;

import com.ca.framework.core.info.BaseInfo;

/**
 * @author basanaga
 *
 */
public class MediateModelLibraryInfo extends BaseInfo {

    /**
     * generated serial version id
     */
    private static final long serialVersionUID = 7731475698319162359L;
    private String tarName;
    private String checksum;
    private String encodingType;
    private String version;
    private String modelExecEnvName;
    private String execEnv;

    public String getTarName() {
        return tarName;
    }

    public void setTarName(String tarName) {
        this.tarName = tarName;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getModelExecEnvName() {
        return modelExecEnvName;
    }

    public void setModelExecEnvName(String modelExecEnvName) {
        this.modelExecEnvName = modelExecEnvName;
    }

    public String getExecEnv() {
        return execEnv;
    }

    public void setExecEnv(String execEnv) {
        this.execEnv = execEnv;
    }

}
