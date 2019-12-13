package com.ca.umg.business.migration.info;

public class ZipContentsInfo {
    
    private byte[] internalZipArray;
    
    private byte[] encryptedKey;

    public byte[] getInternalZipArray() {
        return internalZipArray;
    }

    public void setInternalZipArray(byte[] internalZipArray) {
        this.internalZipArray = internalZipArray;
    }

    public byte[] getEncryptedKey() {
        return encryptedKey;
    }

    public void setEncryptedKey(byte[] encryptedKey) {
        this.encryptedKey = encryptedKey;
    }

}
