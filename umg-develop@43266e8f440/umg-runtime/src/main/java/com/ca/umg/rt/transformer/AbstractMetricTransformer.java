package com.ca.umg.rt.transformer;

import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.integration.Message;
import org.springframework.integration.transformer.AbstractTransformer;

import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.umg.rt.util.MessageVariables;
import com.ca.umg.rt.util.StopWatchMetrics;

/**
 * Created by repvenk on 3/10/2016.
 */
public abstract class AbstractMetricTransformer extends AbstractTransformer {

    private SystemParameterProvider systemParameterProvider;

    protected abstract Object doTransform(Message<?> message) throws Exception;//NOPMD

    protected void addMetrics(Message<?> message, StopWatchMetrics watchMetrics) {
        if(allowMetrics() && watchMetrics != null) {
            ((Map<String, Long>)message.getHeaders().get(MessageVariables.METRICS)).putAll(watchMetrics.getMetrics());
        }
    }

    public void setSystemParameterProvider(SystemParameterProvider systemParameterProvider) {
        this.systemParameterProvider = systemParameterProvider;
    }

    protected boolean allowMetrics() {
        return BooleanUtils.toBoolean(getSystemParameterValue("TRACK_METRICS"));
    }

    private String getSystemParameterValue(String key) {
        String paramValue = null;
        if(systemParameterProvider != null) {
            paramValue = systemParameterProvider.getParameter(key);
        }
        return paramValue;
    }

}
