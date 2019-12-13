package com.ca.umg.rt.flows.version;

import org.junit.Test;

import com.ca.framework.core.exception.BusinessException;

import junit.framework.Assert;

public class VersionMapTest {

    @Test
    public void testAddAndGet() throws BusinessException {
        VersionMap versionMap = new VersionMap();
        VersionInfo versionInfo = new VersionInfo("Version");
        versionMap.add(versionInfo);
        Assert.assertEquals(versionMap.get("Version", null, null,false,"Online"), versionInfo);
        Assert.assertEquals(versionMap.get("Version", 0, 0,false,"Online"), versionInfo);

        versionMap = new VersionMap();
        versionInfo = new VersionInfo("Version",1,0);
        versionMap.add(versionInfo);
        Assert.assertEquals(versionMap.get("Version", null, null,false,"Online"), versionInfo);
        Assert.assertEquals(versionMap.get("Version", 1, null,false,"Online"), versionInfo);
        Assert.assertEquals(versionMap.get("Version", 1,0,false,"Online"), versionInfo);
        
        Assert.assertNull(versionMap.get("Version1", null, null,false,"Online"));
        Assert.assertNull(versionMap.get("Version1", 1, 0,false,"Online"));
        Assert.assertNull(versionMap.get("Version", 1, 2,false,"Online"));
        
    }
    
    @Test
    public void testRemoveAndGet() throws BusinessException{
        VersionMap versionMap = new VersionMap();
        VersionInfo versionInfo = new VersionInfo("Version");
        versionMap.add(versionInfo);
        
        Assert.assertEquals(versionMap.get("Version", null, null,false,"Online"), versionInfo);
        Assert.assertEquals(versionMap.get("Version", 0, 0,false,"Online"), versionInfo);
        
        versionMap.remove(versionInfo);
        Assert.assertNull(versionMap.get("Version", null, null,false,"Online"));        
    }
  
}
