package com.ca.umg.modelet.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.modelet.constants.ErrorCodes;
import com.ca.umg.modelet.util.Modeletutil;

public class MatlabModel {

    private Method method;
    private Object execModelInstance;

    public MatlabModel(final Object execModelInstance, final String modelName) throws SystemException {
        this.execModelInstance = execModelInstance;
        try {
            method = execModelInstance.getClass().getDeclaredMethod(modelName, new Class[] { List.class, List.class });
        } catch (NoSuchMethodException | SecurityException e) {
            throw new SystemException(ErrorCodes.ME0008, new String[] { e.getMessage(), modelName }, e);
        }
    }

    @SuppressWarnings("rawtypes")
    public void executeModel(final List output, final List input) throws SystemException {
        try {
            method.invoke(execModelInstance, output, input);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new SystemException(ErrorCodes.ME0007, new String[] { Modeletutil.extractModelException(e) }, e);
        } catch (InvocationTargetException e) {
            throw new SystemException(ErrorCodes.ME0007, new String[] { Modeletutil.extractModelException(e) }, e);
        } catch (Exception e) { // NOPMD
            throw new SystemException(ErrorCodes.ME0007, new String[] { Modeletutil.extractModelException(e) }, e);
        }
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(final Method method) {
        this.method = method;
    }

    public Object getExecModelInstance() {
        return execModelInstance;
    }

    public void setExecModelInstance(final Object execModelInstance) {
        this.execModelInstance = execModelInstance;
    }

}
