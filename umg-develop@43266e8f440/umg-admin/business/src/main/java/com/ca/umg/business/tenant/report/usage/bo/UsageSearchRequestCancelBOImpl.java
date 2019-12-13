package com.ca.umg.business.tenant.report.usage.bo;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.ca.framework.core.bo.AbstractBusinessObject;
import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.tenant.report.usage.UsageSearchRequestCancel;

@Named
public class UsageSearchRequestCancelBOImpl extends AbstractBusinessObject implements UsageSearchRequestCancelBO {

	private static final Logger LOGGER = LoggerFactory.getLogger(UsageSearchRequestCancelBOImpl.class);

	private static final long serialVersionUID = 1L;

	@Inject
	private com.ca.umg.business.model.dao.UsageSearchRequestCancelDAO usageSearchRequestCancelDAO;

	@Inject
	private CacheRegistry cacheRegistry;

	@Override
	public UsageSearchRequestCancel createUsageSearchRequestCancel() throws BusinessException {
		LOGGER.info("Creating Usage Search Request Cancel");
		UsageSearchRequestCancel usageSearchRequestCancel = new UsageSearchRequestCancel();
		usageSearchRequestCancel.setSearchRequestCancel(false);
		validate(usageSearchRequestCancel);
		usageSearchRequestCancel = usageSearchRequestCancelDAO.save(usageSearchRequestCancel);
		cacheRegistry.getMap(USAGE_SRCH_REQ_CANCEL_MAP).put(usageSearchRequestCancel.getId(), usageSearchRequestCancel.isSearchRequestCancel());
		LOGGER.info("Created Usage Search Request Cancel with Id {} successfully.", usageSearchRequestCancel.getId());
		return usageSearchRequestCancel;
	}

	@Override
	@Transactional
	public void deleteUsageSearchRequestCancel(final String id) {
		LOGGER.info("Deleting Usage Search Request Cancel {}.", id);
		final UsageSearchRequestCancel usageSearchRequestCancel = getUsageSearchRequestCancel(id);
		usageSearchRequestCancelDAO.delete(usageSearchRequestCancel);
		cacheRegistry.getMap(USAGE_SRCH_REQ_CANCEL_MAP).delete(id);
		LOGGER.info("Deleted Usage Search Request Cancel {} successfully.", id);
	}

	@Override
	public UsageSearchRequestCancel getUsageSearchRequestCancel(final String id) {
		final UsageSearchRequestCancel usageSearchRequestCancel = usageSearchRequestCancelDAO.findOne(id);
		cacheRegistry.getMap(USAGE_SRCH_REQ_CANCEL_MAP).put(usageSearchRequestCancel.getId(), usageSearchRequestCancel.isSearchRequestCancel());
		return usageSearchRequestCancel;
	}

	@Override
	public UsageSearchRequestCancel getUsageSearchRequestCancelById(final String id) {
		final UsageSearchRequestCancel usageSearchRequestCancel = usageSearchRequestCancelDAO.findById(id);
		if (usageSearchRequestCancel != null) {
			cacheRegistry.getMap(USAGE_SRCH_REQ_CANCEL_MAP).put(usageSearchRequestCancel.getId(), usageSearchRequestCancel.isSearchRequestCancel());
		}
		return usageSearchRequestCancel;
	}

	@Override
	@Transactional
	public UsageSearchRequestCancel updateUsageSearchRequestCancel(final String id, final boolean cancelStatus) {
		LOGGER.info("Updating Usage Search Request Cancel with Id {}, Cancel Status {}", id, cancelStatus);
		UsageSearchRequestCancel usageSearchRequestCancel = getUsageSearchRequestCancel(id);
		usageSearchRequestCancel.setSearchRequestCancel(cancelStatus);
		usageSearchRequestCancel = usageSearchRequestCancelDAO.save(usageSearchRequestCancel);
		cacheRegistry.getMap(USAGE_SRCH_REQ_CANCEL_MAP).put(usageSearchRequestCancel.getId(), usageSearchRequestCancel.isSearchRequestCancel());
		LOGGER.info("Updated Usage Search Request Cancel with Id {}, Cancel Status {} successfully.", usageSearchRequestCancel.getId(),
				usageSearchRequestCancel.isSearchRequestCancel());
		return usageSearchRequestCancel;
	}

	@Override
	public boolean getUsageSearchRequestCancelStatusFromCache(final String id) {
		boolean cancelStatus = false;
		if (id != null) {
			final Object value = cacheRegistry.getMap(USAGE_SRCH_REQ_CANCEL_MAP).get(id);
			if (value == null) {
				final UsageSearchRequestCancel usageSearchRequestCancel = getUsageSearchRequestCancelById(id);
				if (usageSearchRequestCancel != null) {
					cancelStatus = usageSearchRequestCancel.isSearchRequestCancel();
				}
			} else {
				cancelStatus = (Boolean) value;
			}
		}

		return cancelStatus;
	}
}