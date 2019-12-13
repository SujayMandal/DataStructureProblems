package com.ca.umg.business.tenant.report.model.bo;

import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.createInputTabularInfo;
import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.createOutputTabularInfo;
import static java.lang.String.valueOf;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.mapping.delegate.MappingDelegate;
import com.ca.umg.business.mapping.info.MappingDescriptor;
import com.ca.umg.business.tenant.report.model.TenantModelReport;
import com.ca.umg.business.tenant.report.model.TenantModelReportEnum;
import com.ca.umg.business.tenant.report.model.dao.TenantModelReportDAO;
import com.ca.umg.business.tenant.report.usage.dao.UsageReportDAO;

@Named
@SuppressWarnings("PMD")
public class TenantModelReportBOImpl implements TenantModelReportBO {

	private static String ENDS_WITH_ZERO = ".0";

	@Inject
	private TenantModelReportDAO tenantModelReportDAO;

	@Inject
	private MappingDelegate mappingDelegate;

	@Inject
	private UsageReportDAO usageReportDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantModelReportBOImpl.class);

	@Override
	public TenantModelReport viewTenantModelReport(final String txnId, final boolean tabularViewData) throws SystemException, BusinessException {
		final TenantModelReport modelReport = tenantModelReportDAO.viewTransactionInputAndOutputs(txnId);
		if (tabularViewData) {
            MappingDescriptor mappingDescription = null;
            try {
                mappingDescription = getMappingDescriptor(modelReport);
            } catch (SystemException | BusinessException ex) {
                LOGGER.error("Error occured while gettting the mapping description data.Exception is :", ex);
            }
            if (mappingDescription != null) {
                modelReport.setInputTabularInfo(createInputTabularInfo(modelReport, mappingDescription));
                if (StringUtils.equals(modelReport.getStatus(), "SUCCESS")) {
                    modelReport.setOutputTabularInfo(createOutputTabularInfo(modelReport, mappingDescription));
                } else {
                    modelReport.setErrorMessage(
                            "Unable to present data in tabular view. Please refer to the JSON format to view the data.");

                }
            }

			// convertDoubleZero(modelReport);
		}
		return modelReport;
	}

	private void convertDoubleZero(final TenantModelReport modelReport) {
		convertDoubleZero(modelReport.getTenantInput());
		convertDoubleZero(modelReport.getTenantOutput());
		convertDoubleZero(modelReport.getModelInput());
		convertDoubleZero(modelReport.getModelOutput());
	}

	private void convertDoubleZero(final Map<String, Object> jsonMap) {
		if (jsonMap != null) {
			for (String key : jsonMap.keySet()) {
				final Object value = jsonMap.get(key);
				if (value instanceof Map) {
					convertDoubleZero((Map<String, Object>) value);
				} else if (value instanceof List) {
					List valuList = (List) value;
					for (Object v : valuList) {
						if (v instanceof Map) {
							convertDoubleZero((Map) v);
						}
					}
				} else if (value instanceof Double) {
					// HTML view shows 1.0 as 1 and 0.0 as 0, showing returning
					// String value for such double values
					if (value.toString().endsWith(ENDS_WITH_ZERO)) {
						jsonMap.put(key, valueOf(value.toString()));
					}
				}
			}
		}
	}

	@Override
	public TenantModelReport exportTenantModelReport(final String txnId, final TenantModelReportEnum report) throws SystemException {
		return tenantModelReportDAO.exportTransactionInputAndOutputs(txnId, report);
	}

	@Override
	public TenantModelReport exportTabularViewReport(final String txnId, final TenantModelReportEnum report) throws SystemException {
		return tenantModelReportDAO.exportTransactionInputAndOutputs(txnId, report);
	}

	private String getDerivedModelName(final TenantModelReport modelReport) throws SystemException {
		return usageReportDao.getDerivedModelName(modelReport.getTransactionId(), modelReport.getMajorVersion(), modelReport.getMinorVersion());
	}

	private MappingDescriptor getMappingDelegate(final String derivedModelName) throws SystemException, BusinessException {
		return mappingDelegate.generateMapping(derivedModelName);
	}

	@Override
	public MappingDescriptor getMappingDescriptor(final TenantModelReport modelReport) throws SystemException, BusinessException {
		final String derivedModelName = getDerivedModelName(modelReport);
		final MappingDescriptor mappingDescription = getMappingDelegate(derivedModelName);
		return mappingDescription;
	}
	
	@Override
	public List<TenantModelReport> viewTenantModelReport(final List<String> txnId) throws SystemException, BusinessException {
		final List<TenantModelReport> modelReport = tenantModelReportDAO.viewTransactionInputAndOutputs(txnId);
		return modelReport;
	}

	@Override
	public MappingDescriptor getMappingDescriptorForReport(TenantModelReport modelReport)
			throws SystemException, BusinessException {
		final String derivedModelName = getDerivedModelName(modelReport);
		final MappingDescriptor mappingDescription = getMappingDelegate(derivedModelName);
		mappingDescription.setTidTree(mappingDelegate.readMapping(derivedModelName.replace("MID", "TID")).getTidTree());
		return mappingDescription;
	}
}