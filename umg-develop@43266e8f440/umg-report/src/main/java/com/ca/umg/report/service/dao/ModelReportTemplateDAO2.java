package com.ca.umg.report.service.dao;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.ca.umg.report.model.ModelReportStatusInfo;
import com.ca.umg.report.model.ModelReportTemplateInfo;
import com.ca.umg.report.model.ReportTemplateStatus;

@Repository
public class ModelReportTemplateDAO2 {
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelReportTemplateDAO2.class);

	@Inject
	@Named(value = "dataSource")
	private DataSource dataSource;

	private JdbcTemplate jdbcTemplate;

	@PostConstruct
	public void initializeTemplate() {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public ModelReportTemplateInfo getActiveReportTemplate(final ModelReportStatusInfo info) {
		ModelReportTemplateInfo result = null;
		
		final StringBuilder sql = new StringBuilder();
		
		sql.append("select * from MODEL_REPORT_TEMPLATE ");
		sql.append("where ID = '" + info.getReportTemplateId()).append("' and ");
		sql.append("IS_ACTIVE = " + ReportTemplateStatus.ACTIVE.getStatus()).append(" and ");
		sql.append("TENANT_ID = '" + info.getTenantId() + "'");
		
		LOGGER.info("getActiveReportTemplate Query is :" + sql.toString());
		
		final List<ModelReportTemplateInfo> list = jdbcTemplate.query(sql.toString(), new TransactionRowMapper());	
		if (list != null) {
			LOGGER.info("Number of active report templates is :" + list.size());
			result = list.get(0);
		} else {
			LOGGER.error("No Active templates found for template id :" + info.getReportTemplateId());
		}
		
		return result;
	}
	
	private class TransactionRowMapper implements RowMapper<ModelReportTemplateInfo> {
		@Override
		public ModelReportTemplateInfo mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final ModelReportTemplateInfo info = new ModelReportTemplateInfo();
			info.setId(rs.getString("ID"));
			info.setReportVersion(rs.getInt("MAJOR_VERSION"));
			info.setTemplateDefinition(rs.getBytes("TEMPLATE_DEFINATION"));
			info.setVersionId(rs.getString("UMG_VERSION_ID"));
			info.setTenantId(rs.getString("TENANT_ID"));
			info.setName(rs.getString("NAME"));
			info.setIsActive(rs.getInt("IS_ACTIVE"));
			info.setTemplateFileName(rs.getString("TEMPLATE_FILE_NAME"));
			info.setReportEngine(rs.getString("REPORT_ENGINE"));
			info.setReportType(rs.getString("REPORT_TYPE"));
			
			LOGGER.info("Report Template got from database is " + info.toString());
			return info;
		}
	}
}
