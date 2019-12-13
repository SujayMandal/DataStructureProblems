package com.ca.umg.business.tenant.report.usage.util;

import static java.lang.Double.valueOf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum;
import com.ca.umg.business.tenant.report.usage.UsageReportFilter;
import com.ca.umg.business.tenant.report.usage.bo.UsageExcelReport;
import com.ca.umg.business.tenant.report.usage.dao.UsageReportDAO;
import com.ca.umg.business.tenant.report.usage.dao.UsageReportQuery;

@SuppressWarnings("PMD")
public class UsageReportMain {

	// public static final String TENANT_ID = "davxotdrnq02";
	public static final String TENANT_ID = "localhost";

	public static final int PAGE_SIZE = 100;

	private final UsageReportDAO dao;
	private final SqlRowSet sqlRowSet;
	private final OutputStream outputStream;

	public UsageReportMain() throws BusinessException, SystemException, FileNotFoundException,
	IOException {
		final StaticApplicationContext context = createAppContext();
		initUsageReportQuery(context);
		dao = initUsageReportDAO(context);
		final UsageReportFilter filter = createFilter();
		sqlRowSet = dao.loadTransactionsRowSet(filter);
		final UsageExcelReport report = createUsageReport(sqlRowSet);
		final String reportFileName = getFileName(report, filter);
		outputStream = createOutputStream(reportFileName);
	}

	public static void main(final String[] args) throws BusinessException, SystemException, FileNotFoundException,
	IOException {
		UsageReportMain reportMain = new UsageReportMain();
		final UsageExcelReport report = reportMain.createUsageReport(reportMain.sqlRowSet);
		report.createReport(reportMain.outputStream);
		reportMain.closeOutputStream(reportMain.outputStream);
	}

	private void closeOutputStream(final OutputStream outputStream) throws IOException {
		if (outputStream != null) {
			outputStream.flush();
			outputStream.close();
		}
	}

	private OutputStream createOutputStream(final String fileName) throws FileNotFoundException {
		final File reportFile = new File("D:\\Work\\" + fileName);
		return new FileOutputStream(reportFile);
	}

	private String getFileName(final UsageExcelReport report, final UsageReportFilter filter) {
		final Long startTime = filter.getRunAsOfDateFrom();
		final Long endTime = filter.getRunAsOfDateTo();
		return report.getReportFileName(TENANT_ID, startTime, endTime);
	}

	private UsageExcelReport createUsageReport(final SqlRowSet sqlRowSet) {
		return new UsageExcelReport(sqlRowSet);
	}

	private StaticApplicationContext createAppContext() {
		final StaticApplicationContext context = new StaticApplicationContext();
		context.registerBeanDefinition("dataSource", createDataSourceBean());
		context.registerBeanDefinition("systemParameterProvider", createSystemParameterProviderBean());
		context.registerBeanDefinition("dao", createReportDAOBean());
		context.registerBeanDefinition("UsageReportQuery", createReportQueryBean());
		context.refresh();
		context.start();
		return context;
	}

	private UsageReportDAO initUsageReportDAO(final StaticApplicationContext context) {
		final UsageReportDAO dao = (UsageReportDAO) context.getBean("dao");
		final DataSource dataSource = (DataSource) context.getBean("dataSource");
		dao.setDataSource(dataSource);

		dao.initializeTemplate();
		return dao;
	}

	private UsageReportQuery initUsageReportQuery(final StaticApplicationContext context) {
		return (UsageReportQuery) context.getBean("UsageReportQuery");
	}

	private BeanDefinition createDataSourceBean() {
		final BeanDefinition dataSourceBean = new RootBeanDefinition();
		dataSourceBean.setBeanClassName("org.springframework.jdbc.datasource.DriverManagerDataSource");
		dataSourceBean.getPropertyValues().addPropertyValue("driverClassName", "com.mysql.jdbc.Driver");
		/*dataSourceBean.getPropertyValues().addPropertyValue("url", "jdbc:mysql://172.26.146.169:3306/" + TENANT_ID);
		dataSourceBean.getPropertyValues().addPropertyValue("username", "causer");
		dataSourceBean.getPropertyValues().addPropertyValue("password", "Db@123");*/
		dataSourceBean.getPropertyValues().addPropertyValue("url", "jdbc:mysql://localhost:3306/" + TENANT_ID);
		dataSourceBean.getPropertyValues().addPropertyValue("username", "root");
		dataSourceBean.getPropertyValues().addPropertyValue("password", "");
		return dataSourceBean;
	}

	private BeanDefinition createSystemParameterProviderBean() {
		final BeanDefinition systemParameterProviderBean = new RootBeanDefinition();
		systemParameterProviderBean.setBeanClassName("com.ca.umg.business.transaction.report.MySystemParameterProvider");
		return systemParameterProviderBean;
	}

	private BeanDefinition createReportDAOBean() {
		final BeanDefinition reportDAOBean = new RootBeanDefinition();
		reportDAOBean.setBeanClassName("com.ca.umg.business.tenant.report.usage.dao.UsageReportDAO");
		reportDAOBean.setAutowireCandidate(true);
		return reportDAOBean;
	}

	private BeanDefinition createReportQueryBean() {
		final BeanDefinition reportQueryBean = new RootBeanDefinition();
		reportQueryBean.setBeanClassName("com.ca.umg.business.tenant.report.usage.dao.UsageReportQuery");
		reportQueryBean.setAutowireCandidate(true);
		return reportQueryBean;
	}

	private UsageReportFilter createFilter() {
		final UsageReportFilter filter = new UsageReportFilter();
		filter.setTenantModelName("All");
		filter.setRunAsOfDateFrom(1422616680000l);
		filter.setRunAsOfDateTo(1424182500000l);
		filter.setTransactionStatus("");
		filter.setPage(-1);
		filter.setPageSize(PAGE_SIZE);
		filter.setSortColumn(UsageReportColumnEnum.PROCESSING_TIME.getExcelHeaderName());
		filter.setDescending(true);
		filter.setTenantCode(TENANT_ID);
		filter.setCancelRequestId("");
		final Map<String, Object> countMap = dao.getTransactionCount(filter);
		final long totalCount = ((Long) countMap.get("totalcount")).longValue();
		filter.setMatchedTransactionCount(totalCount);
		filter.setTotalPages(((Double) Math.ceil(valueOf(totalCount) / PAGE_SIZE)).intValue());
		return filter;
	}
}