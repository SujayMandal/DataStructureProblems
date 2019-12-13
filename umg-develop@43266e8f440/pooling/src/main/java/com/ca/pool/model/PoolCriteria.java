package com.ca.pool.model;

import static com.google.common.base.Objects.toStringHelper;

import java.io.Serializable;

public class PoolCriteria implements Serializable{

    private static final long serialVersionUID = -7214319535028676575L;

    private String  id;
	private String criteriaName;
	private int criteriaPriority;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getCriteriaName() {
		return criteriaName;
	}
	
	public void setCriteriaName(String criteriaName) {
		this.criteriaName = criteriaName;
	}
	
	public int getCriteriaPriority() {
		return criteriaPriority;
	}

	public void setCriteriaPriority(int criteriaPriority) {
		this.criteriaPriority = criteriaPriority;
	}

	@Override
	public String toString() {
		return toStringHelper(this.getClass()).add("Criteria Id:", id).
				add("Criteria Name:", criteriaName).toString();	
	}
}
