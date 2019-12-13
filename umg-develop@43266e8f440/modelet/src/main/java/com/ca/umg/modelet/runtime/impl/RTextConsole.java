package com.ca.umg.modelet.runtime.impl;

import static java.util.Locale.getDefault;

import java.util.ArrayList;
import java.util.List;

import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RTextConsole implements RMainLoopCallbacks {

    private static final int ERROR_MSG_LENGTH = 0;
    private static final Logger LOGGER = LoggerFactory.getLogger(RTextConsole.class);
    private final List<String> errorMessageList = new ArrayList<String>();
    private final List<String> completeMessageList = new ArrayList<String>();
    private boolean forModel;

    public List<String> logLevelList() {
        List<String> errorLiteralList = new ArrayList<String>();
        errorLiteralList.add("error");     
        if (!isForModel()) {
            errorLiteralList.add("warning");
        }
        return errorLiteralList;
    }

    public void clearErrorMessage() {
        errorMessageList.clear();
    }
    
    public void clearCompleteMessageList() {
        completeMessageList.clear();
    }

    public String getErrorMessage() {
        final StringBuilder errorMessage = new StringBuilder();
        for (String error : errorMessageList) {
            if (errorMessage != null && errorMessage.length() > ERROR_MSG_LENGTH) {
                errorMessage.append(',');
            }

            errorMessage.append(error);
        }

        return errorMessage.toString();
    }
    
    public String getCompleteMessage() {
        final StringBuilder completeMessage = new StringBuilder();
        for (String msg : completeMessageList) {
            if (completeMessage != null && completeMessage.length() > ERROR_MSG_LENGTH) {
                completeMessage.append(',');
            }

            completeMessage.append(msg);
        }

        return completeMessage.toString();
    }

    @Override
    public void rWriteConsole(final Rengine re, final String text, final int oType) {
        LOGGER.info("R Write Console: " + text);

        if (isErrorMessage(text)) {
            errorMessageList.add(text);
            LOGGER.error("R Write Console: " + text);
        }

        completeMessageList.add(text + "\n");
    }
    
    public void addRCommandMessage(final String command) {
    	completeMessageList.add(command + "\n");
    }

    private boolean isErrorMessage(final String message) {
        boolean errorMessage = false;
        if (message != null) {
            final String lowerCaseMessage = message.toLowerCase(getDefault());
            List<String> logLevelList = logLevelList();
            for (String error : logLevelList) {
                if (lowerCaseMessage.contains(error)) {
                    errorMessage = true;
                    break;
                }
            }
        }

        return errorMessage;
    }

    @Override
    public void rBusy(final Rengine re, final int which) {
        LOGGER.error("rBusy");
    }

    @Override
    public String rReadConsole(final Rengine re, final String prompt, final int addToHistory) {
        return null;
    }

    @Override
    public void rShowMessage(final Rengine re, final String message) {
        LOGGER.info("R Show Message :" + message);
    }

    @Override
    public String rChooseFile(final Rengine re, final int newFile) {
        return null;
    }

    @Override
    public void rFlushConsole(final Rengine re) {
        // LOGGER.info("Useless log statement to avoid PMD error");
    }

    @Override
    public void rLoadHistory(final Rengine re, final String filename) {
        // LOGGER.info("Useless log statement to avoid PMD error");
    }

    @Override
    public void rSaveHistory(final Rengine re, final String filename) {
        // LOGGER.info("Useless log statement to avoid PMD error");
    }

    public boolean isForModel() {
        return forModel;
    }

    public void setForModel(boolean forModel) {
        this.forModel = forModel;
    }

}