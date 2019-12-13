package com.ca.umg.business.tenant.info;

import com.ca.umg.business.mid.extraction.info.DatatypeInfo;

public class TenantReqResHeader {

    private TenantReqResInfo modelName;
    
    private TenantReqResInfo majorVersion;
    
    private TenantReqResInfo minorVersion;
    
    private TenantReqResInfo date;
    
    private TenantReqResInfo transactionId;
    
    public TenantReqResHeader() {
        buildHeaderDetails();
    }

    private void buildHeaderDetails() {
        modelName = new TenantReqResInfo();

        modelName.setDatatype(getDataType("String"));
        modelName.setName("modelName");
        modelName.setMandatory(true);
        
        majorVersion = new TenantReqResInfo();
        majorVersion.setDatatype(getDataType("Integer"));
        majorVersion.setName("majorVersion");
        majorVersion.setMandatory(true);
        
        minorVersion = new TenantReqResInfo();
        minorVersion.setDatatype(getDataType("Integer"));
        minorVersion.setName("minorVersion");
        minorVersion.setMandatory(false);
        
        date = new TenantReqResInfo();
        date.setDatatype(getDataType("String"));
        date.setName("date");
        date.setMandatory(true);
        
        transactionId = new TenantReqResInfo();
        transactionId.setDatatype(getDataType("String"));
        transactionId.setName("transactionId");
        transactionId.setMandatory(true);
        
    }

    private DatatypeInfo getDataType(String type) {
        DatatypeInfo datatype = new DatatypeInfo();
        datatype.setType(type);
        return datatype;
    }

    public TenantReqResInfo getModelName() {
        return modelName;
    }

    public TenantReqResInfo getMajorVersion() {
        return majorVersion;
    }

    public TenantReqResInfo getMinorVersion() {
        return minorVersion;
    }

    public TenantReqResInfo getDate() {
        return date;
    }

    public TenantReqResInfo getTransactionId() {
        return transactionId;
    }
    
}
