package com.ca.systemmodelet;

import com.ca.modelet.ModeletClientInfo;

/**
 * @author basanaga
 */
public interface SystemModeletConfig {

    /**
     * added this method to refresh the cache
     */
    void refreshCache();

    /**
     * create system modelet
     * @param modeletClientInfo
     *
     * @return
     */
    String createModeletConfig(final ModeletClientInfo modeletClientInfo);

    /**
     * update modelet profiler link
     * @param modeletClientInfo
     * @param modeletId
     */
    void createModeletProfilerLink(final ModeletClientInfo modeletClientInfo, final String modeletId);

    /**
     * @param modeletClientInfo
     * 
     */
    void updateModeletConfig(final ModeletClientInfo modeletClientInfo, String poolName);

}
