package com.ca.umg.business.transaction.info;



/**
 * POJO for storing the advance search criterias entered in transaction dashboard page
 * @author raddibas
 *
 */
@SuppressWarnings("PMD")
public class AdvanceTransactionFilter {

	private static final long serialVersionUID = -6693082028313343492L;

	private BasicSearchCriteria clause1;
	
	private BasicSearchCriteria clause2;
	
	private String criteria;

	public BasicSearchCriteria getClause1() {
		return clause1;
	}

	public void setClause1(BasicSearchCriteria clause1) {
		this.clause1 = clause1;
	}

	public BasicSearchCriteria getClause2() {
		return clause2;
	}

	public void setClause2(BasicSearchCriteria clause2) {
		this.clause2 = clause2;
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

}