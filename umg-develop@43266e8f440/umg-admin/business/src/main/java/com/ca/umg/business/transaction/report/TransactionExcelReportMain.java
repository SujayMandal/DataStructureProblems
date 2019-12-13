package com.ca.umg.business.transaction.report;

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
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.umg.business.transaction.dao.TransactionExcelReportDAO;
import com.ca.umg.business.transaction.dao.TransactionExcelReportQuery;
import com.ca.umg.business.transaction.info.TransactionFilter;

@SuppressWarnings("PMD")
public class TransactionExcelReportMain {

	public static final String TENANT_ID = "davxotdrnq02";

	private final StaticApplicationContext context;			
	private final TransactionExcelReportDAO dao;
	private final TransactionFilter filter;
	private final SqlRowSet sqlRowSet;
	private final TransactionExcelReport report;
	private final String reportFileName;
	private final OutputStream outputStream;

	public TransactionExcelReportMain() throws BusinessException, SystemException, FileNotFoundException,
		IOException {
		context = createAppContext();			
		initTransactionExcelReportQuery(context);
		dao = initTransactionExcelReportDAO(context);
		filter = createFilter();
		sqlRowSet = dao.loadTransactionsRowSet(filter, TENANT_ID);
		report = createTransactionExcelReport(sqlRowSet);
		reportFileName = getFileName(dao, report, filter);
		outputStream = createOutputStream(reportFileName);		
	}
	
	public static void main(String[] args) throws BusinessException, SystemException, FileNotFoundException,
		IOException {
		TransactionExcelReportMain reportMain = new TransactionExcelReportMain();
		final TransactionExcelReport report = reportMain.createTransactionExcelReport(reportMain.sqlRowSet);
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
	
	private String getFileName(final TransactionExcelReportDAO dao, final TransactionExcelReport report, final TransactionFilter filter) {
		final Map<String, Object> map = dao.getMinAndMaxRunAsOfDate(filter, TENANT_ID);
		final Long startTime = (Long) map.get("mindate");
		final Long endTime = (Long) map.get("maxdate");
		return report.getReportFileName(TENANT_ID, startTime, endTime);
	}
	
	private TransactionExcelReport createTransactionExcelReport(final SqlRowSet sqlRowSet) {
		return new TransactionExcelReport(sqlRowSet);
	}
	
	private StaticApplicationContext createAppContext() {
		final StaticApplicationContext context = new StaticApplicationContext();
		context.registerBeanDefinition("dataSource", createDataSourceBean());
		context.registerBeanDefinition("systemParameterProvider", createSystemParameterProviderBean());
		context.registerBeanDefinition("dao", createReportDAOBean());
		context.registerBeanDefinition("transactionExcelReportQuery", createReportQueryBean());
		context.refresh();
		context.start();
		return context;
	}
	
	private TransactionExcelReportDAO initTransactionExcelReportDAO(final StaticApplicationContext context) {
		final TransactionExcelReportDAO dao = (TransactionExcelReportDAO) context.getBean("dao");
		final DataSource dataSource = (DataSource) context.getBean("dataSource");
		dao.setDataSource(dataSource);
		
		final TransactionExcelReportQuery transactionExcelReportQuery = (TransactionExcelReportQuery) context.getBean("transactionExcelReportQuery");
		dao.setTransactionExcelReportQuery(transactionExcelReportQuery);
		dao.initializeTemplate();
		return dao;
	}
	
	private TransactionExcelReportQuery initTransactionExcelReportQuery(final StaticApplicationContext context) {
		final SystemParameterProvider systemParameterProvider = (SystemParameterProvider) context.getBean("systemParameterProvider");
		final TransactionExcelReportQuery query = (TransactionExcelReportQuery) context.getBean("transactionExcelReportQuery");
		query.setSystemParameterProvider(systemParameterProvider);
		return query;
	}
	
	private BeanDefinition createDataSourceBean() {
		final BeanDefinition dataSourceBean = new RootBeanDefinition();
		dataSourceBean.setBeanClassName("org.springframework.jdbc.datasource.DriverManagerDataSource");
		dataSourceBean.getPropertyValues().addPropertyValue("driverClassName", "com.mysql.jdbc.Driver");
		dataSourceBean.getPropertyValues().addPropertyValue("url", "jdbc:mysql://172.26.146.169:3306/" + TENANT_ID);
		dataSourceBean.getPropertyValues().addPropertyValue("username", "causer");
		dataSourceBean.getPropertyValues().addPropertyValue("password", "@arye6Go");
		return dataSourceBean;
	}
	
	private BeanDefinition createSystemParameterProviderBean() {
		final BeanDefinition systemParameterProviderBean = new RootBeanDefinition();
		systemParameterProviderBean.setBeanClassName("com.ca.umg.business.transaction.report.MySystemParameterProvider");
		return systemParameterProviderBean;
	}
	
	private BeanDefinition createReportDAOBean() {
		final BeanDefinition reportDAOBean = new RootBeanDefinition();
		reportDAOBean.setBeanClassName("com.ca.umg.business.transaction.dao.TransactionExcelReportDAO");
		reportDAOBean.setAutowireCandidate(true);
		return reportDAOBean;
	}

	private BeanDefinition createReportQueryBean() {
		final BeanDefinition reportQueryBean = new RootBeanDefinition();
		reportQueryBean.setBeanClassName("com.ca.umg.business.transaction.dao.TransactionExcelReportQuery");
		reportQueryBean.setAutowireCandidate(true);
		return reportQueryBean;
	}
	
	private TransactionFilter createFilter() {
		final TransactionFilter filter = new TransactionFilter();
		// filter.setBatchId("e7498bf6-8e93-42ae-b09e-6673e00ef84f");
		//TODO commented this as method is not used anywhere for umg-4200 
		//need to change according to new filter object if this method is used
		//filter.setShowTestTxn(false);
		return filter;
	}
}
