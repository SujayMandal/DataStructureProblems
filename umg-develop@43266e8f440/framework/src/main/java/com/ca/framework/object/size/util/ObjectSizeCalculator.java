package com.ca.framework.object.size.util;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.ehcache.sizeof.SizeOf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.systemparameter.SystemParameterProvider;


public class ObjectSizeCalculator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ObjectSizeCalculator.class);
	
	@Inject
    private static SystemParameterProvider sysParam;
	
	private static SizeOf sizeOf = SizeOf.newInstance();
	
    public static void getObjectDeepSize(final Object object, String id, String message) {
    	if(StringUtils.equalsIgnoreCase(sysParam.getParameter(SystemConstants.OBJECT_SIZE_FLAG), Boolean.TRUE.toString())) {
    		long startTime = System.currentTimeMillis();
    		long deepSize = sizeOf.deepSizeOf(object);
    		LOGGER.error(message + " of heap size: " + deepSize/1024 + " KB, with Id: " + id
    				+ " . Time taken to calculate memory : " + (System.currentTimeMillis() - startTime));
    	}
        return;
    }
    
    public static void getObjectShallowSize(final Object object) {
        long startTime = System.currentTimeMillis();
        long shallowSize = sizeOf.sizeOf(object);
        LOGGER.error("Object " + object.getClass().getSimpleName() + " of shallow size: " + shallowSize/1024 + " KB, "
        		+ "time taken to calculate memory : " + (System.currentTimeMillis() - startTime));
        return;
    }
    
    public SystemParameterProvider getSysParam() {
        return sysParam;
    }

    public void setSysParam(SystemParameterProvider sysParam) {
        this.sysParam = sysParam;
    }
}
