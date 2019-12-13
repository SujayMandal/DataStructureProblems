package com.fa.dp.business.validator.bo;

import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.business.validator.dao.DynamicPricingIntgAuditDao;

@Named
public class DynamicPricingIntgAuditBoImpl implements DynamicPricingIntgAuditBo {
	@Inject
	private DynamicPricingIntgAuditDao dynamicPricingIntgAuditDao;
}
