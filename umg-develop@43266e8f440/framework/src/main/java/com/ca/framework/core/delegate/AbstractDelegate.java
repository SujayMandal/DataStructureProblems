/*
 * AbstractDelegate.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.framework.core.delegate;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;

import com.ca.framework.core.custom.mapper.UMGConfigurableMapper;

import ma.glasnost.orika.MapperFacade;

/**
 * 
 * **/
public abstract class AbstractDelegate implements Delegate {
    @Inject
    private UMGConfigurableMapper mapper;

    protected <T> T convert(Object source, Class<T> dest) {
        T t = null;
        if (source != null) {
            t = getMapper().map(source, dest);
        }
        return t;
    }

    protected MapperFacade getMapper() {
        return mapper;
    }

    protected <S, T> List<T> convertToList(List<S> sourceList, Class<T> targetType) {
        List<T> listModelInfo = new ArrayList<T>();
        if (CollectionUtils.isNotEmpty(sourceList)) {
            for (S source : sourceList) {
                T modelInfo = getMapper().map(source, targetType);
                listModelInfo.add(modelInfo);
            }
        }
        return listModelInfo;
    }
}
