package com.ca.umg.business.tenant.report.usage;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.MultiTenantEntity;

@Entity
@Table(name = "USAGE_SEARCH_REQUEST_CANCEL")
public class UsageSearchRequestCancel extends MultiTenantEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "IS_USAGE_SEARCH_CANCEL")
	@Property
	private boolean searchRequestCancel;

	public boolean isSearchRequestCancel() {
		return searchRequestCancel;
	}

	public void setSearchRequestCancel(final boolean searchRequestCancel) {
		this.searchRequestCancel = searchRequestCancel;
	}
}