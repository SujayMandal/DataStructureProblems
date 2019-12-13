package com.fa.dp.business.classificationExecutor.classificationFactory;

import com.fa.dp.business.classificationExecutor.nrzFiltersExecutor.NrzSopWeek0AssetValueFilter;
import com.fa.dp.business.classificationExecutor.nrzFiltersExecutor.NrzSopWeek0DuplicateFilter;
import com.fa.dp.business.classificationExecutor.ocnFiltersExecutor.OcnSopWeek0AssetValueFilter;
import com.fa.dp.business.classificationExecutor.ocnFiltersExecutor.OcnSopWeek0DuplicateFilter;
import com.fa.dp.business.classificationExecutor.phhFiltersExecutor.PhhSopWeek0AssetValueFilter;
import com.fa.dp.business.classificationExecutor.phhFiltersExecutor.PhhSopWeek0DuplicateFilter;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.task.sop.week0.AbstractSopWeek0AssetValueFilter;
import com.fa.dp.business.task.sop.week0.AbstractSopWeek0DuplicateFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author misprakh
 */

@Named
public class SopWeek0Factory {

	@Inject
	private ApplicationContext applicationContext;

	/**
	 * Use getInstance method to get object of type AbstractSopWeek0AssetValueFilter
	 *
	 * @param commandName
	 * @return
	 */
	public Object getInstance(String commandName) {
		if (commandName == null) {
			return null;
		}
		if (StringUtils.equalsIgnoreCase(commandName, "nrzSopWeek0AssetValueFilter")) {
			return applicationContext.getBean(NrzSopWeek0AssetValueFilter.class);
		}
		if (StringUtils.equalsIgnoreCase(commandName, "ocnSopWeek0AssetValueFilter")) {
			return applicationContext.getBean(OcnSopWeek0AssetValueFilter.class);
		}
		if (StringUtils.equalsIgnoreCase(commandName, "phhSopWeek0AssetValueFilter")) {
			return applicationContext.getBean(PhhSopWeek0AssetValueFilter.class);
		}
		if (StringUtils.equalsIgnoreCase(commandName, "nrzSopWeek0DuplicateFilter")) {
			return applicationContext.getBean(NrzSopWeek0DuplicateFilter.class);
		}
		if (StringUtils.equalsIgnoreCase(commandName, "ocnSopWeek0DuplicateFilter")) {
			return applicationContext.getBean(OcnSopWeek0DuplicateFilter.class);
		}
		if (StringUtils.equalsIgnoreCase(commandName, "phhSopWeek0DuplicateFilter")) {
			return applicationContext.getBean(PhhSopWeek0DuplicateFilter.class);
		}
		return null;
	}
}
