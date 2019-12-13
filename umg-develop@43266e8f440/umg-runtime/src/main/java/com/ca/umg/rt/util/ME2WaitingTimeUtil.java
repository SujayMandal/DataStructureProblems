package com.ca.umg.rt.util;

import static com.ca.umg.rt.util.MessageVariables.ME2_WAITING_TIME;
import static com.ca.umg.rt.util.MessageVariables.MODELET_EXECUTION_TIME;
import static com.ca.umg.rt.util.MessageVariables.MODEL_EXECUTION_TIME;

import java.util.Map;

import com.ca.umg.me2.util.ModelExecResponse;

@SuppressWarnings("PMD")
public class ME2WaitingTimeUtil {

    private static final Long ZERO = Long.valueOf(0l);

    public static Long getModelExecutionTime(final Map<String, Object> payload, final Map<String, Object> headers) {
        if (isNotNullAndMoreThanZero(headers.get(MODEL_EXECUTION_TIME))) {
            return (Long) headers.get(MODEL_EXECUTION_TIME);
        } else {
            return getModelExecutionTime(payload);
        }
    }

    public static Long getModelExecutionTime(final Map<String, Object> payload) {
        Long lModelExecutionTime = ZERO;
        ModelExecResponse<Map<String, Object>> modelExecResponse = getMe2Response(payload);
        if (modelExecResponse != null) {
            final Map<String, Object> modelResponse = modelExecResponse.getResponse();
            if (modelResponse != null) {
                final Object modelExecutionTime = modelResponse.get(MODEL_EXECUTION_TIME);
                if (modelExecutionTime != null) {
                    lModelExecutionTime = getLongValue(modelExecutionTime);
                }
            }
        }

        return lModelExecutionTime;
    }

    public static Long getModeletExecution(final Map<String, Object> payload, final Map<String, Object> headers) {
        if (isNotNullAndMoreThanZero(headers.get(MODELET_EXECUTION_TIME))) {
            return (Long) headers.get(MODELET_EXECUTION_TIME);
        } else {
            return getModeletExecution(payload);
        }
    }

    public static Long getModeletExecution(final Map<String, Object> payload) {
        Long lModeletExecutionTime = ZERO;
        final ModelExecResponse<Map<String, Object>> me2Response = getMe2Response(payload);
        if (me2Response != null) {
            final Map<String, Object> modelResponse = me2Response.getResponse();
            if (modelResponse != null) {
                final Object modeletExecutionTime = modelResponse.get(MODELET_EXECUTION_TIME);
                if (modeletExecutionTime != null) {
                    lModeletExecutionTime = getLongValue(modeletExecutionTime);
                }
            }
        }
        return lModeletExecutionTime;
    }

    private static ModelExecResponse<Map<String, Object>> getMe2Response(final Map<String, Object> payload) {
        ModelExecResponse<Map<String, Object>> me2Response = null;
        if (payload != null) {
            me2Response = (ModelExecResponse<Map<String, Object>>) payload.get(MessageVariables.ME2_RESPONSE);
        }
        return me2Response;
    }

    private static Long getMe2Execution(final Map<String, Object> payload) {
        Long lM2eExecutionTime = ZERO;

        final ModelExecResponse<Map<String, Object>> me2Response = getMe2Response(payload);
        if (me2Response != null) {
            lM2eExecutionTime = me2Response.getMe2ExecutionTime();
        }
        return lM2eExecutionTime;
    }

    public static Long getMe2WaitingTime(final Map<String, Object> payload, final Map<String, Object> headers) {
        if (isNotNullAndMoreThanZero(headers.get(ME2_WAITING_TIME))) {
            return (Long) headers.get(ME2_WAITING_TIME);
        } else if(isNotNullAndMoreThanZero(payload.get(ME2_WAITING_TIME))) {
            return (Long) payload.get(ME2_WAITING_TIME);
        } else {
            return getMe2WaitingTime(payload);
        }
    }

    public static Long getMe2WaitingTime(final Map<String, Object> payload) {
        final Long modeletExecutionTime = getModeletExecution(payload);
        final Long me2ExecutionTime = getMe2Execution(payload);
        return Long.valueOf(me2ExecutionTime - modeletExecutionTime);
    }

    private static Long getLongValue(final Object executionValue) {
        Long convertedValue = null;
        if (executionValue instanceof Integer) {
            Integer intValue = (Integer) executionValue;
            convertedValue = intValue == null ? 0 : intValue.longValue();
        } else if (executionValue instanceof Long) {
            convertedValue = (Long) executionValue;
        }
        return convertedValue;
    }

    private static boolean isNotNullAndMoreThanZero(final Object value) {
        if (value != null) {
            if (value instanceof Long) {
                return ((Long) value).longValue() > 0;
            }

            if (value instanceof Integer) {
                return ((Integer) value).intValue() > 0;
            }
        }

        return false;
    }
}