
package com.ca.umg.business.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ca.umg.business.tenant.report.usage.UsageSearchRequestCancel;

public interface UsageSearchRequestCancelDAO extends JpaRepository<UsageSearchRequestCancel, String>, JpaSpecificationExecutor<UsageSearchRequestCancel> {

	public UsageSearchRequestCancel findById(final String id);
}