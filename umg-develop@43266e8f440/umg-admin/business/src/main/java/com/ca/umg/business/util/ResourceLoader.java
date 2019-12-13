package com.ca.umg.business.util;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;

@Component
public class ResourceLoader implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    public static InputStream getResource(String resoucePath) throws SystemException {
        Resource resouce = applicationContext.getResource(resoucePath);
        if (resouce == null) {
            throw new SystemException(BusinessExceptionCodes.BSE000008, new Object[] { resoucePath });
        }
        try {
            return resouce.getInputStream();
        } catch (IOException e) {
            throw new SystemException(BusinessExceptionCodes.BSE000008, new Object[] { resoucePath, e.getLocalizedMessage() }, e);
        }
    }

}
