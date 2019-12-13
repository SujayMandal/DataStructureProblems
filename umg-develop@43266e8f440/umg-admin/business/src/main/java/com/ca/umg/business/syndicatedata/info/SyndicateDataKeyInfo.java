package com.ca.umg.business.syndicatedata.info;

import java.util.List;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

public class SyndicateDataKeyInfo {

    @NotEmpty(message = "Key name cannot be empty")
    @Pattern(regexp = "^[a-zA-Z0-9_]{1,25}$", message = "Key name cannot contain special charaters and spaces")
    private String keyName;

    @NotEmpty(message = "Columns associated with keys cannot be empty")
    private List<SyndicateDataKeyColumnInfo> sColumnInfos;

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public List<SyndicateDataKeyColumnInfo> getsColumnInfos() {
        return sColumnInfos;
    }

    public void setsColumnInfos(List<SyndicateDataKeyColumnInfo> sColumnInfos) {
        this.sColumnInfos = sColumnInfos;
    }

}
