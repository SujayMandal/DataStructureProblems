package com.ca.umg.business.common.info;

import java.io.Serializable;

public class SearchOptions extends PagingInfo implements Serializable {
    private static final long serialVersionUID = 6175289075436305770L;
    private String  fromDate;
    private String toDate;
    private String searchText;
	public String getFromDate(){
		return fromDate;
	}
	public void setFromDate(String fromDate){
		this.fromDate = fromDate;
	}
	public String getToDate(){
		return toDate;
	}
	public void setToDate(String toDate){
		this.toDate = toDate;
	}
	public String getSearchText(){
		return searchText;
	}
	public void setSearchText(String searchText){
		this.searchText = searchText;
	} 

}
