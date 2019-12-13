package com.ca.umg.business.validation;

public enum UserConfirmation {
    AGREED_TO_ADJUST_TIME_OVERLAP("AGREED_TO_ADJUST_TIME_OVERLAP"), AGREED_TO_ADJUST_TIME_GAP("AGREED_TO_ADJUST_TIME_GAP");

    private String action;

    /**
     * Creates a new UserConfirmation object.
     * 
     * @param name
     *            DOCUMENT ME!
     **/
    private UserConfirmation(String action) {
        this.action = action;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     **/
    public String getAction() {
        return action;
    }
}
