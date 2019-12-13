package com.ca.umg.business.syndicatedata.info;

import java.util.ArrayList;
import java.util.List;
/**
 * This class is used because, ajax call have to return multiple information to the client
 * this is used in query view screen.
 * Adding additional information in this class will not affect the existing functionality 
 */
public class SyndicateQueryWrapper {
	private List<SyndicateDataQueryInfo> allQueries = new ArrayList<>();
	private boolean publishedOrDeactivated;

	public List<SyndicateDataQueryInfo> getAllQueries(){
		return allQueries;
	}
	public void setAllQueries(List<SyndicateDataQueryInfo> allQueries){
		this.allQueries = allQueries;
	}
	public boolean isPublishedOrDeactivated(){
		return publishedOrDeactivated;
	}
	public void setPublishedOrDeactivated(boolean publishedOrDeactivated){
		this.publishedOrDeactivated = publishedOrDeactivated;
	}

	
}
