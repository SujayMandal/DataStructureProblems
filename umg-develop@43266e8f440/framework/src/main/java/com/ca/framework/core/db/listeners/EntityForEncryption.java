package com.ca.framework.core.db.listeners;

import com.ca.framework.core.db.annotation.Encrypt;

public class EntityForEncryption {
    
    @Encrypt
    private String ssn;

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

}
