/*
 * ModelBO.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.tenant.report.usage.bo;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.tenant.report.usage.UsageSearchRequestCancel;

public interface UsageSearchRequestCancelBO {

	public String USAGE_SRCH_REQ_CANCEL_MAP = "USAGE_SEARCH_REQUEST_CANCEL_MAP";

	public UsageSearchRequestCancel createUsageSearchRequestCancel() throws BusinessException;

	public void deleteUsageSearchRequestCancel(final String id);

	public UsageSearchRequestCancel updateUsageSearchRequestCancel(final String id, final boolean cancelStatus);

	public UsageSearchRequestCancel getUsageSearchRequestCancel(final String id);

	public UsageSearchRequestCancel getUsageSearchRequestCancelById(final String id);

	public boolean getUsageSearchRequestCancelStatusFromCache(final String id);
}