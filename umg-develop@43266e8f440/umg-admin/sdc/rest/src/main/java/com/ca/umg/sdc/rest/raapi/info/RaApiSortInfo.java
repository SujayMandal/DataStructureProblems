/**
 * 
 */
package com.ca.umg.sdc.rest.raapi.info;


/**
 * @author raddibas
 *
 */
public class RaApiSortInfo {
    
    private static final long serialVersionUID = 8115855512829894851L;

    private String sortColumn;

    private Boolean descending;

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public Boolean getDescending() {
        return descending;
    }

    public void setDescending(Boolean descending) {
        this.descending = descending;
    }
}
