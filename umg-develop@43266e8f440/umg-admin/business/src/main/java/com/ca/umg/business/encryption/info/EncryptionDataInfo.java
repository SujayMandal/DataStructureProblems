package com.ca.umg.business.encryption.info;


public class EncryptionDataInfo {
    
    private byte[] encryptedData;
    
    private byte[] decryptedData;
    
    private byte[] encryptedKey;
    
    public byte[] getDecryptedData() {
        return decryptedData;
    }

    public void setDecryptedData(byte[] decryptedData) {
        this.decryptedData = decryptedData;
    }

    public byte[] getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(byte[] encryptedData) {
        this.encryptedData = encryptedData;
    }

    public byte[] getEncryptedKey() {
        return encryptedKey;
    }

    public void setEncryptedKey(byte[] encryptedKey) {
        this.encryptedKey = encryptedKey;
    }
}
