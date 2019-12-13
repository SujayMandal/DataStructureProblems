package com.ca.umg.rt.batching.data;

import java.io.Serializable;

/**
 * @author basanaga
 * 
 */
public class BatchFileStatusInput implements Serializable {

    /**
     * generated serial version ID
     */
    private static final long serialVersionUID = -1238384262823720432L;

    // Tenant input file name
    private String fileName;

    // Tenant searching date
    private String date;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
