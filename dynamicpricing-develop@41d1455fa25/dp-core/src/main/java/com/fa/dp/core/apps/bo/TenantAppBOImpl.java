package com.fa.dp.core.apps.bo;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import com.fa.dp.core.apps.dao.AdGroupAppMappingDao;
import com.fa.dp.core.apps.dao.TenantAppDao;
import com.fa.dp.core.apps.dao.TenantAppParamDao;
import com.fa.dp.core.apps.domain.AdGroupAppMapping;
import com.fa.dp.core.apps.domain.TenantApp;
import com.fa.dp.core.apps.domain.TenantAppParam;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.DateConversionUtil;

import org.joda.time.DateTime;
import org.springframework.jdbc.core.JdbcTemplate;

@Named
public class TenantAppBOImpl implements TenantAppBO {

	@Inject
	private TenantAppDao tenantAppDao;

	@Inject
	private TenantAppParamDao tenantAppParamDao;

	@Inject
	private AdGroupAppMappingDao adGroupAppMappingDao;
	

	@Inject
	@Named(value = "dataSource")
	private DataSource dataSource;

	private JdbcTemplate jdbcTemplate;

	@PostConstruct
	public void initializeTemplate() {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fa.ra.client.core.apps.bo.TenantAppBO#
	 * getTenantAppsByADGroupsSortedByPriorty(java.util.List)
	 */
	@Override
	public List<TenantApp> getTenantAppsByADGroupsSortedByPriorty(List<String> adGroups) throws SystemException {
		return null;// tenantAppDao.getTenantAppsByADGroupsSortedByPriorty(adGroups);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fa.ra.client.core.apps.bo.TenantAppBO#getAllAppParams()
	 */
	@Override
	public List<TenantAppParam> getAllAppParams() {
		return tenantAppParamDao.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fa.ra.client.core.apps.bo.TenantAppBO#getAllTenantApps()
	 */
	@Override
	public List<TenantApp> getAllTenantApps() throws SystemException {
		return tenantAppDao.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fa.ra.client.core.apps.bo.TenantAppBO#getAllAdGroupAppMapping()
	 */
	@Override
	public List<AdGroupAppMapping> getAllAdGroupAppMapping() throws SystemException {
		return adGroupAppMappingDao.findAll();
	}

	@Override
	public void updateAppParams(List<TenantAppParam> list, String updatedBy) throws SystemException {
		for (TenantAppParam tenantAppParam : list) {
			tenantAppParamDao.setAppParameterValueByKey(tenantAppParam.getAttrKey(), tenantAppParam.getAttrValue(),
					updatedBy,
					DateConversionUtil.convertUtcToEstTimeZone(new DateTime()).getMillis());
		}
	}

	@Override
	public List<TenantAppParam> getTenantAppParams(String code,String type) {
		TenantApp tnt = tenantAppDao.findByCode(code);
		return tenantAppParamDao.findByTenantAppAndClassificationOrderByLastModifiedDateDesc(tnt, type);
	}

	@Override
	public TenantApp getTenantApp(String code) {
		return tenantAppDao.findByCode(code);
	}

}
