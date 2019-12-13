/**
 * 
 */
package com.fa.dp.core.base.delegate;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.fa.dp.core.mapper.BeanMapper;

import org.apache.commons.collections4.CollectionUtils;

import ma.glasnost.orika.MapperFacade;

/**
 *
 *
 */
public abstract class AbstractDelegate {

    @Inject
    private BeanMapper beanMapper;

    protected <T> T convert(Object source, Class<T> dest) {
        T t = null;
        if (source != null) {
            t = getMapper().map(source, dest);
        }
        return t;
    }

    protected MapperFacade getMapper() {
        return beanMapper;
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
