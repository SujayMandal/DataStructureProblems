package com.fa.dp.business.db.client;

import com.fa.dp.business.info.SSPMIInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@Named
public class PMIRRDBClient {

	private NamedParameterJdbcTemplate namedJdbcTemplate;

	@Inject
	@Named(value = "pmiRRDataSource")
	private DataSource dataSource;

	@PostConstruct
	public void initializeTemplate() {
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public Callable<List<SSPMIInfo>> fetchPMIFlagsForPropIds(String selectQuery, String integrationType,
			List<DPProcessWeekNParamInfo> subColEntriesList) {
		return new Callable<List<SSPMIInfo>>() {

			@Override
			public List<SSPMIInfo> call() throws Exception {
				List<String> assetnumbers = new ArrayList<>();
				subColEntriesList.stream().forEach(colEntry -> {
					String assetNo = colEntry.getAssetNumber();
					assetnumbers.add(assetNo);
				});
				MapSqlParameterSource parameters = new MapSqlParameterSource();
				parameters.addValue("idList", assetnumbers);
				log.info("Query to fetch PMI flag :" + selectQuery + " for PMI-RR Integration Type :" + integrationType);
				List<SSPMIInfo> ssPmiInfos = namedJdbcTemplate.execute(selectQuery, parameters, new PreparedStatementCallback<List<SSPMIInfo>>() {

					@Override
					public List<SSPMIInfo> doInPreparedStatement(final PreparedStatement ps) throws SQLException, DataAccessException {
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
					ssPmiInfo.setAssetNumber(rs.getString("PROP_TEMP"));
					ssPmiInfo.setPmiFlag(rs.getBoolean("PMI_FLAG"));
					ssPmiInfos.add(ssPmiInfo);
				}
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
		return ssPmiInfos;
	}

	public Callable<List<SSPMIInfo>> fetchClientIdsForPropIds(String selectQuery, String integrationType, List<SSPMIInfo> subAssetList) {
		return new Callable<List<SSPMIInfo>>() {

			@Override
			public List<SSPMIInfo> call() throws Exception {
				List<String> assetnumbers = new ArrayList<>();
				subAssetList.stream().forEach(pmiInfo -> {
					String assetNo = pmiInfo.getAssetNumber();
					assetnumbers.add(assetNo);
				});
				MapSqlParameterSource parameters = new MapSqlParameterSource();
				parameters.addValue("idList", assetnumbers);
				log.info("Query to fetch Client Code :" + selectQuery + " for SS_ENT_REPOS Integration Type :" + integrationType);
				List<SSPMIInfo> ssPmiInfos = namedJdbcTemplate.execute(selectQuery, parameters, new PreparedStatementCallback<List<SSPMIInfo>>() {

					@Override
					public List<SSPMIInfo> doInPreparedStatement(final PreparedStatement ps) throws SQLException, DataAccessException {
						log.info("Inside doInPreparedStatement.");
						return createSSPmiInfosForClientId(ps.executeQuery());
					}
				});
				return ssPmiInfos;
			}
		};
	}

	private List<SSPMIInfo> createSSPmiInfosForClientId(final ResultSet rs) {
		log.info("Enter RRDBClient :: method createSSPmiInfosForClientId");
		List<SSPMIInfo> ssPmiInfos = new ArrayList<>();
		if (rs != null) {
			try {
				while (rs.next()) {
					SSPMIInfo ssPmiInfo = new SSPMIInfo();
					ssPmiInfo.setAssetNumber(rs.getString("LOAN_ID"));
					ssPmiInfo.setClientCode(rs.getString("CLIENT_CODE"));
					ssPmiInfos.add(ssPmiInfo);
				}
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
		return ssPmiInfos;
	}

}
