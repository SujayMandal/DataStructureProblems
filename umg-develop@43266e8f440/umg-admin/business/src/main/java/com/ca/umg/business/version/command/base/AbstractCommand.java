/**
 * 
 */
package com.ca.umg.business.version.command.base;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ca.umg.business.version.command.Command;
import com.ca.umg.business.version.command.error.Error;
import com.ca.umg.business.version.command.error.ErrorController;
import com.ca.umg.business.version.command.executor.impl.WSServerExecution;
import com.ca.umg.business.version.info.VersionInfo;

/**
 * @author chandrsa
 *
 */
public abstract class AbstractCommand implements Command {

    private ErrorController errorController;

    private boolean executed;

    /**
     * This method would check the validity of data.
     * 
     * @param errors
     * @param data
     * @return
     */
    protected boolean checkData(List<Error> errors, Object data, String commandName, Class<?> className) {
        boolean dataCorrect = Boolean.TRUE;
        if (data != null) {
            if (!(data.getClass().isAssignableFrom(className))) {
                errors.add(new Error(String
                        .format("SYSTEM ERROR : Data not of expected type (%s), could not proceed!", className), commandName, ""));
                dataCorrect = Boolean.FALSE;
            }
        } else {
            errors.add(new Error("SYSTEM ERROR : No data provided, could not proceed!", commandName, ""));
            dataCorrect = Boolean.FALSE;
        }
        return dataCorrect;
    }

    protected void sendStatusMessage(List<Error> errors, Object data, String message) {
        if (CollectionUtils.isEmpty(errors)) {
            WSServerExecution ws = new WSServerExecution();
            VersionInfo info = (VersionInfo) data;
            ws.sendStatusMessage(message, info.getClientID());
        }
    }

    @Override
    public void setErrorController(ErrorController errorController) {
        this.errorController = errorController;
    }

    public ErrorController getErrorController() {
        return errorController;
    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

}
