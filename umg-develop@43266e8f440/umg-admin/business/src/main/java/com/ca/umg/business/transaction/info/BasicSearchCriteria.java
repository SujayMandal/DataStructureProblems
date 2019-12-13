package com.ca.umg.business.transaction.info;

import com.ca.umg.business.transaction.query.Operator;

public class BasicSearchCriteria {

	private String searchKey;
	
	private Object searchValue;
	
	private Operator searchOperator;

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public Object getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(Object searchValue) {
		this.searchValue = searchValue;
	}

	public Operator getSearchOperator() {
		return searchOperator;
	}

	public void setSearchOperator(String searchOperator) {
		this.searchOperator = Operator.getInstanceFromCodeValue(searchOperator);
	}
	
}
