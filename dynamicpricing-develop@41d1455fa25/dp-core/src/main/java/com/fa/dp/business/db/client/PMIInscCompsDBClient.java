package com.fa.dp.business.db.client;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.fa.dp.business.info.SSPMIInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Named
public class PMIInscCompsDBClient {
	
private NamedParameterJdbcTemplate namedJdbcTemplate;
	
	@Inject
	@Named(value = "pmiArltDataSource")
	private DataSource dataSource;
	
	@PostConstruct
	public void initializeTemplate() {
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	public Callable<List<SSPMIInfo>> fetchInscIdsForPropIds(String selectQuery, String integrationType, List<SSPMIInfo> subPmiInfos) {
		return new Callable<List<SSPMIInfo>>() {

			@Override
			public List<SSPMIInfo> call() throws Exception {
				List<String> assetnumbers = new ArrayList<>();
				subPmiInfos.stream().forEach(pmiInfo -> {
					String assetNo = pmiInfo.getAssetNumber();
					assetnumbers.add(assetNo);
				});
				MapSqlParameterSource parameters = new MapSqlParameterSource();
				parameters.addValue("idList", assetnumbers);
				log.info("Query to fetch Insurance company :" + selectQuery + " for PMI-ARLT Integration Type :" + integrationType);
				List<SSPMIInfo> ssPmiInfos = namedJdbcTemplate.execute(selectQuery, parameters, new PreparedStatementCallback<List<SSPMIInfo>>() {

					@Override
					public List<SSPMIInfo> doInPreparedStatement(final PreparedStatement ps)
							throws SQLException, DataAccessException {
							log.info("Inside doInPreparedStatement.");
							return createSSPmiInfos(ps.executeQuery());
					}
				});
				return ssPmiInfos;
			}
		};
	}

	public Callable<List<SSPMIInfo>> fetchInscIdsForAssetIds(String selectQuery, String integrationType, List<String> subPmiInfos) {
		return new Callable<List<SSPMIInfo>>() {

			@Override
			public List<SSPMIInfo> call() throws Exception {
				/*List<String> assetnumbers = new ArrayList<>();
				subPmiInfos.stream().forEach(pmiInfo -> {
					String assetNo = pmiInfo.getAssetNumber();
					assetnumbers.add(assetNo);
				});*/
				MapSqlParameterSource parameters = new MapSqlParameterSource();
				parameters.addValue("idList", subPmiInfos);
				log.info("Query to fetch Insurance company :" + selectQuery + " for PMI-ARLT Integration Type :" + integrationType);
				List<SSPMIInfo> ssPmiInfos = namedJdbcTemplate.execute(selectQuery, parameters, new PreparedStatementCallback<List<SSPMIInfo>>() {

					@Override
					public List<SSPMIInfo> doInPreparedStatement(final PreparedStatement ps)
							  throws SQLException, DataAccessException {
						log.info("Inside doInPreparedStatement.");
						return createSSPmiInfos(ps.executeQuery());
					}
				});
				return ssPmiInfos;
			}
		};
	}
	
	private List<SSPMIInfo> createSSPmiInfos(final ResultSet rs) {
		log.info("Enter RRDBClient :: method createSSPMIInfo");
		List<SSPMIInfo> ssPmiInfos = new ArrayList<>();
				if (rs != null) {
					try {
						while (rs.next()) {
							SSPMIInfo ssPmiInfo = new SSPMIInfo();
							ssPmiInfo.setAssetNumber(rs.getString("LOANNUMBER"));
							ssPmiInfo.setInsuranceId(rs.getString("INSCID"));
							ssPmiInfos.add(ssPmiInfo);
						}
					} catch (SQLException e) {
						log.error(e.getMessage(),e);
					}
				}
		return ssPmiInfos;
	}

}
