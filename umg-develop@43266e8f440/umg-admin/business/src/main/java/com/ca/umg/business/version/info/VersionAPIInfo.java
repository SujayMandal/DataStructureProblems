/**
 * 
 */
package com.ca.umg.business.version.info;

import java.util.List;
import java.util.Map;

/**
 * @author kamathan
 *
 */
public class VersionAPIInfo {

    private Map<String, Object> header;

    private List<TenantParamInfo> tidInputParams;

    private List<TenantParamInfo> tidOutputParams;

    public List<TenantParamInfo> getTidInputParams() {
        return tidInputParams;
    }

    public void setTidInputParams(List<TenantParamInfo> tidInputParams) {
        this.tidInputParams = tidInputParams;
    }

    public List<TenantParamInfo> getTidOutputParams() {
        return tidOutputParams;
    }

    public void setTidOutputParams(List<TenantParamInfo> tidOutputParams) {
        this.tidOutputParams = tidOutputParams;
    }

    public Map<String, Object> getHeader() {
        return header;
    }

    public void setHeader(Map<String, Object> header) {
        this.header = header;
    }

}
