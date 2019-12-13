/**
 *
 */
package com.fa.dp.business.rr.migration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.fa.dp.business.audit.dao.DPWeekNAuditReportsDao;
import com.fa.dp.business.rr.migration.dao.DPMigrationMapDao;
import com.fa.dp.business.rr.migration.entity.DPMigrationMap;
import com.fa.dp.business.util.ThreadPoolExecutorUtil;
import com.fa.dp.business.validator.dao.DPProcessParamsDao;
import com.fa.dp.business.weekn.dao.DPProcessWeekNParamsDao;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.util.KeyValue;

/**
 * @author mandasuj
 */
@Slf4j
@Named
public class RRMigration {

	public static final String OLD_RR_LOAN_NUM = "OLD_RR_LOAN_NUM";
	public static final String LOAN_NUM = "LOAN_NUM";
	public static final String PROP_TEMP = "PROP_TEMP";
	public static final String ID_LIST = "idList";
	public static final String HYPHEN = "-";
	
	public static final String UPDATE_DP_WEEKN_PARAMS_EXISTING_NON_MIGRATED_LOANS = "UPDATE DP_WEEKN_PARAMS K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.ASSET_NUMBER = S.ASSET_NUMBER SET K.ASSET_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER";
	public static final String UPDATE_DP_WEEKN_PARAMS_ORIGINAL_EXISTING_NON_MIGRATED_LOANS = "UPDATE DP_WEEKN_PARAMS_ORIGINAL K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.ASSET_NUMBER = S.ASSET_NUMBER SET K.ASSET_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER";
	public static final String UPDATE_DP_WEEK0_PARAMS_EXISTING_NON_MIGRATED_LOANS = "UPDATE DP_WEEK0_PARAMS K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.ASSET_NUMBER = S.ASSET_NUMBER SET K.ASSET_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER";	
	public static final String UPDATE_DP_WEEK0_PARAMS_ORIGINAL_EXISTING_NON_MIGRATED_LOANS = "UPDATE DP_WEEK0_PARAMS_ORIGINAL K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.ASSET_NUMBER = S.ASSET_NUMBER SET K.ASSET_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER";
	public static final String UPDATE_DP_WEEKN_AUDIT_REPORTS_EXISTING_NON_MIGRATED_LOANS = "UPDATE DP_WEEKN_AUDIT_REPORTS K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.LOAN_NUMBER = S.ASSET_NUMBER SET K.LOAN_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_LOAN_NUMBER = S.OLD_ASSET_NUMBER";
	
	public static final String UPDATE_DP_WEEKN_PARAMS_EXISTING_MIGRATED_LOANS = "UPDATE DP_WEEKN_PARAMS K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.ASSET_NUMBER = S.PROP_TEMP SET K.ASSET_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER";
	public static final String UPDATE_DP_WEEKN_PARAMS_ORIGINAL_EXISTING_MIGRATED_LOANS = "UPDATE DP_WEEKN_PARAMS_ORIGINAL K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.ASSET_NUMBER = S.PROP_TEMP SET K.ASSET_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER";
	public static final String UPDATE_DP_WEEK0_PARAMS_EXISTING_MIGRATED_LOANS = "UPDATE DP_WEEK0_PARAMS K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.ASSET_NUMBER = S.PROP_TEMP SET K.ASSET_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER";	
	public static final String UPDATE_DP_WEEK0_PARAMS_ORIGINAL_EXISTING_MIGRATED_LOANS = "UPDATE DP_WEEK0_PARAMS_ORIGINAL K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.ASSET_NUMBER = S.PROP_TEMP SET K.ASSET_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER";
	public static final String UPDATE_DP_WEEKN_AUDIT_REPORTS_EXISTING_MIGRATED_LOANS = "UPDATE DP_WEEKN_AUDIT_REPORTS K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.LOAN_NUMBER = S.PROP_TEMP SET K.LOAN_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_LOAN_NUMBER = S.OLD_ASSET_NUMBER";
	
	public static final String UPDATE_DP_WEEKN_PARAMS_NEW_MIGRATED_LOANS = "UPDATE DP_WEEKN_PARAMS K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.ASSET_NUMBER = S.OLD_ASSET_NUMBER SET K.ASSET_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER";
	public static final String UPDATE_DP_WEEKN_PARAMS_ORIGINAL_NEW_MIGRATED_LOANS = "UPDATE DP_WEEKN_PARAMS_ORIGINAL K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.ASSET_NUMBER = S.OLD_ASSET_NUMBER SET K.ASSET_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER";
	public static final String UPDATE_DP_WEEK0_PARAMS_NEW_MIGRATED_LOANS = "UPDATE DP_WEEK0_PARAMS K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.ASSET_NUMBER = S.OLD_ASSET_NUMBER SET K.ASSET_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER";	
	public static final String UPDATE_DP_WEEK0_PARAMS_ORIGINAL_NEW_MIGRATED_LOANS = "UPDATE DP_WEEK0_PARAMS_ORIGINAL K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.ASSET_NUMBER = S.OLD_ASSET_NUMBER SET K.ASSET_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER";
	public static final String UPDATE_DP_WEEKN_AUDIT_REPORTS_NEW_MIGRATED_LOANS = "UPDATE DP_WEEKN_AUDIT_REPORTS K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.LOAN_NUMBER = S.OLD_ASSET_NUMBER SET K.LOAN_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_LOAN_NUMBER = S.OLD_ASSET_NUMBER";
	
	public static final String UPDATE_DP_WEEKN_PARAMS_EXISTING_NON_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER = "UPDATE DP_WEEKN_PARAMS K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.OLD_ASSET_NUMBER IS NOT NULL AND K.OLD_ASSET_NUMBER = S.ASSET_NUMBER SET K.ASSET_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER";
	public static final String UPDATE_DP_WEEKN_PARAMS_ORIGINAL_EXISTING_NON_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER = "UPDATE DP_WEEKN_PARAMS_ORIGINAL K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.OLD_ASSET_NUMBER IS NOT NULL AND K.OLD_ASSET_NUMBER = S.ASSET_NUMBER SET K.ASSET_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER";
	public static final String UPDATE_DP_WEEK0_PARAMS_EXISTING_NON_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER = "UPDATE DP_WEEK0_PARAMS K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.OLD_ASSET_NUMBER IS NOT NULL AND K.OLD_ASSET_NUMBER = S.ASSET_NUMBER SET K.ASSET_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER";	
	public static final String UPDATE_DP_WEEK0_PARAMS_ORIGINAL_EXISTING_NON_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER = "UPDATE DP_WEEK0_PARAMS_ORIGINAL K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.OLD_ASSET_NUMBER IS NOT NULL AND K.OLD_ASSET_NUMBER = S.ASSET_NUMBER SET K.ASSET_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER";
	public static final String UPDATE_DP_WEEKN_AUDIT_REPORTS_EXISTING_NON_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER = "UPDATE DP_WEEKN_AUDIT_REPORTS K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.OLD_LOAN_NUMBER IS NOT NULL AND K.OLD_LOAN_NUMBER = S.ASSET_NUMBER SET K.LOAN_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_LOAN_NUMBER = S.OLD_ASSET_NUMBER";
	
	public static final String UPDATE_DP_WEEKN_PARAMS_EXISTING_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER = "UPDATE DP_WEEKN_PARAMS K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.OLD_ASSET_NUMBER IS NOT NULL AND K.OLD_ASSET_NUMBER = S.PROP_TEMP SET K.ASSET_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER";
	public static final String UPDATE_DP_WEEKN_PARAMS_ORIGINAL_EXISTING_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER = "UPDATE DP_WEEKN_PARAMS_ORIGINAL K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.OLD_ASSET_NUMBER IS NOT NULL AND K.OLD_ASSET_NUMBER = S.PROP_TEMP SET K.ASSET_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER";
	public static final String UPDATE_DP_WEEK0_PARAMS_EXISTING_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER = "UPDATE DP_WEEK0_PARAMS K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.OLD_ASSET_NUMBER IS NOT NULL AND K.OLD_ASSET_NUMBER = S.PROP_TEMP SET K.ASSET_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER";	
	public static final String UPDATE_DP_WEEK0_PARAMS_ORIGINAL_EXISTING_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER= "UPDATE DP_WEEK0_PARAMS_ORIGINAL K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.OLD_ASSET_NUMBER IS NOT NULL AND K.OLD_ASSET_NUMBER = S.PROP_TEMP SET K.ASSET_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER";
	public static final String UPDATE_DP_WEEKN_AUDIT_REPORTS_EXISTING_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER = "UPDATE DP_WEEKN_AUDIT_REPORTS K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.OLD_LOAN_NUMBER IS NOT NULL AND K.OLD_LOAN_NUMBER = S.PROP_TEMP SET K.LOAN_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_LOAN_NUMBER = S.OLD_ASSET_NUMBER";
	
	public static final String UPDATE_DP_WEEKN_PARAMS_NEW_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER = "UPDATE DP_WEEKN_PARAMS K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.OLD_ASSET_NUMBER IS NOT NULL AND K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER SET K.ASSET_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER";
	public static final String UPDATE_DP_WEEKN_PARAMS_ORIGINAL_NEW_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER = "UPDATE DP_WEEKN_PARAMS_ORIGINAL K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.OLD_ASSET_NUMBER IS NOT NULL AND K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER SET K.ASSET_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER";
	public static final String UPDATE_DP_WEEK0_PARAMS_NEW_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER = "UPDATE DP_WEEK0_PARAMS K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.OLD_ASSET_NUMBER IS NOT NULL AND K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER SET K.ASSET_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER";	
	public static final String UPDATE_DP_WEEK0_PARAMS_ORIGINAL_NEW_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER = "UPDATE DP_WEEK0_PARAMS_ORIGINAL K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.OLD_ASSET_NUMBER IS NOT NULL AND K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER SET K.ASSET_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_ASSET_NUMBER = S.OLD_ASSET_NUMBER";
	public static final String UPDATE_DP_WEEKN_AUDIT_REPORTS_NEW_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER = "UPDATE DP_WEEKN_AUDIT_REPORTS K JOIN (SELECT * FROM DP_MIGRATION_MAP LIMIT 100000) S ON K.OLD_LOAN_NUMBER IS NOT NULL AND K.OLD_LOAN_NUMBER = S.OLD_ASSET_NUMBER SET K.LOAN_NUMBER = S.ASSET_NUMBER, K.PROP_TEMP = S.PROP_TEMP, K.OLD_LOAN_NUMBER = S.OLD_ASSET_NUMBER";

	@Value("${WEEKN_CONCURRENT_DBCALL_INITIAL_QUERY_POOL_SIZE}")
	private int concurrentWeekNDbCallInitialQueryPoolSize;

	@Value("${WEEKN_INITIAL_QUERY_IN_CLAUSE_COUNT}")
	private int initialQueryInClauseCount;

	@Inject
	@Named(value = "rrDataSource")
	private DataSource dataSource;
	
	@Inject
	@Named(value = "dataSource")
	private DataSource localMysqlDdataSource;

	@Inject
	private CacheManager cacheManager;
	
	@Inject
	private DPProcessParamsDao dpProcessParamsDao;
	
	@Inject
	private DPMigrationMapDao dpMigrationMapDao;
	
	@Inject
	private DPProcessWeekNParamsDao dpProcessWeekNParamsDao;
	
	@Inject
	private DPWeekNAuditReportsDao dpWeekNAuditReportsDao;

	private ExecutorService executorService;

	private NamedParameterJdbcTemplate namedJdbcTemplate;
	
	private JdbcTemplate jdbcTemplate;
	
	private JdbcTemplate mysqlJdbcTemplate;
	
	@PostConstruct
	public void initializeTemplate() {
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		jdbcTemplate = new JdbcTemplate(dataSource);
		mysqlJdbcTemplate = new JdbcTemplate(localMysqlDdataSource);
		executorService = ThreadPoolExecutorUtil.getFixedSizeThreadPool(concurrentWeekNDbCallInitialQueryPoolSize);
	}
	
	@PreDestroy
    public void destroy() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

	public void getMigrationMaps(final Map<String, String> migrationNewPropToPropMap, final Map<String, String> migrationPropToLoanMap, List<String> props) {
		List<List<String>> splitListProps = ListUtils.partition(props, initialQueryInClauseCount);
		Boolean exceptionOccured;
		do {
			exceptionOccured = false;
			List<String> failedPropsList = new ArrayList<>();
			List<KeyValue<List<String>,Future<KeyValue<Map<String, String>, Map<String, String>>>>> futureList = new ArrayList<>();
			String rrMigrationPropTempQuery = (String) cacheManager.getAppParamValue(AppParameterConstant.RR_MIGRATION_PROP_TEMP_QUERY);
			String rrMigrationLoanNumQuery = (String) cacheManager.getAppParamValue(AppParameterConstant.RR_MIGRATION_LOAN_NUM_WHERE_OLD_RR_LOAN_NULL_QUERY);
			String rrMigrationOldLoanNumQuery = (String) cacheManager.getAppParamValue(AppParameterConstant.RR_MIGRATION_OLD_RR_LOAN_NUM_QUERY);
			for (List<String> subListProps : splitListProps) {
				Future<KeyValue<Map<String, String>, Map<String, String>>> rrRespFuture = executorService
						.submit(fetchMigrationInfoForPropTemps(rrMigrationPropTempQuery, rrMigrationLoanNumQuery, rrMigrationOldLoanNumQuery, subListProps));
				futureList.add(new KeyValue<>(subListProps, rrRespFuture));
			}
			for (KeyValue<List<String>,Future<KeyValue<Map<String, String>, Map<String, String>>>> future : futureList) {
				KeyValue<Map<String, String>, Map<String, String>> rrResp;
				try {
					rrResp = future.getValue().get();
					Map<String, String> newPropToPropMap = rrResp.getKey();
					Map<String, String> propToLoanMap = rrResp.getValue();
					migrationNewPropToPropMap.putAll(newPropToPropMap);
					migrationPropToLoanMap.putAll(propToLoanMap);
				} catch (InterruptedException | ExecutionException e) {
					log.error("Exception while getMigrationMaps : {}", e);
					if(future.getKey().size() != 1){
						exceptionOccured = true;
						log.error("Retrying building MigrationMaps for affected batch with properties : {}", future.getKey());
						failedPropsList.addAll(future.getKey());
					}
				}
			}
			if(CollectionUtils.isNotEmpty(failedPropsList)){
				splitListProps = ListUtils.partition(failedPropsList, 1);
			}
		} while(exceptionOccured);
	}

	private Callable<KeyValue<Map<String, String>, Map<String, String>>> fetchMigrationInfoForPropTemps(String rrMigrationPropTempQuery,
			String rrMigrationLoanNumQuery, String rrMigrationOldLoanNumQuery, List<String> subListProps) {
		return () -> {
			Map<String, String> migrationNewPropToPropMap = new HashMap<>();
			Map<String, String> migrationPropToLoanMap = new HashMap<>();
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			List<String> listProps = subListProps.stream().filter(prop -> {
				if(StringUtils.contains(HYPHEN, prop)){
					log.info("Property Id with '-' removed : {}", prop);
					return false;
				}
				else 
					return true;
			}).collect(Collectors.toList());
			log.debug("ListProps : {}", listProps.toString());
			if(listProps.size() != 0){
				parameters.addValue(ID_LIST, listProps);
			KeyValue<Map<String, String>, Map<String, String>> fetchedPropTempMap = namedJdbcTemplate
					.execute(rrMigrationPropTempQuery, parameters, ps -> {
						Map<String, String> propTempToOldLoanNumMap = new HashMap<>();
						Map<String, String> propTempToLoanNumMap = new HashMap<>();
						ResultSet rs = ps.executeQuery();
						while (rs.next()) {
							if (StringUtils.isNotEmpty(rs.getString(OLD_RR_LOAN_NUM))) {
								propTempToOldLoanNumMap.put(rs.getString(PROP_TEMP), rs.getString(OLD_RR_LOAN_NUM));
							}
							propTempToLoanNumMap.put(rs.getString(PROP_TEMP), rs.getString(LOAN_NUM));
						}
						return new KeyValue<>(propTempToOldLoanNumMap, propTempToLoanNumMap);
					});

			Map<String, String> propTempToOldLoanNumMap = fetchedPropTempMap.getKey();
			Map<String, String> propTempToLoanNumMap = fetchedPropTempMap.getValue();
			migrationPropToLoanMap.putAll(propTempToLoanNumMap);
			List<String> oldLoans = propTempToOldLoanNumMap.values().stream().collect(Collectors.toList());
			log.debug("fetchedPropTempMap : {}", fetchedPropTempMap.toString());
			log.debug("fetchedPropTempMap : {}", fetchedPropTempMap.getKey().toString());
			log.debug("fetchedPropTempMap : {}", fetchedPropTempMap.getValue().toString());
			log.debug("oldLoans : {}", oldLoans.toString());
			if(oldLoans.size() != 0){
				parameters.addValue(ID_LIST, oldLoans);
				Map<String, String> fetchedLoanNumMap = namedJdbcTemplate
						.execute(rrMigrationLoanNumQuery, parameters, ps -> {
							ResultSet rs = ps.executeQuery();
							Map<String, String> propToLoanNumMap = new HashMap<>();
							while (rs.next()) {
								propToLoanNumMap.put(rs.getString(PROP_TEMP), rs.getString(LOAN_NUM));
							}
							return propToLoanNumMap;
						});
					Set<String> discardedLoanNums = fetchedLoanNumMap.entrySet().stream().filter(prop -> StringUtils.contains(prop.getKey(), HYPHEN))
							.map(prop -> prop.getValue()).collect(Collectors.toSet());
					log.info("Discarding Loan Numbers : {}", discardedLoanNums.toString());
					Map<String, String> refinedLoanNumMap = fetchedLoanNumMap.entrySet().stream().filter(entry -> !discardedLoanNums.contains(entry.getValue()))
							.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
					migrationPropToLoanMap.putAll(refinedLoanNumMap);
					final Map<String, String> reverseFetchedMap = refinedLoanNumMap.entrySet().stream()
						.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
				propTempToOldLoanNumMap.forEach((k, v) -> {
					if (StringUtils.isNotEmpty(reverseFetchedMap.get(v))) {
						migrationNewPropToPropMap.put(k, reverseFetchedMap.get(v));
					}
				});
			}
			List<String> loans = propTempToLoanNumMap.values().stream().collect(Collectors.toList());
			log.debug("loans : {}", loans.toString());
				if(loans.size() != 0){
			parameters.addValue(ID_LIST, loans);
			KeyValue<Map<String, String>, Map<String, String>> fetchedOldRrLoanMap = namedJdbcTemplate
					.execute(rrMigrationOldLoanNumQuery, parameters, ps -> {
						ResultSet rs = ps.executeQuery();
						Map<String, String> oldLoanNumToPropTempMap = new HashMap<>();
						Map<String, String> propToLoanNumMap = new HashMap<>();
						while (rs.next()) {
							oldLoanNumToPropTempMap.put(rs.getString(OLD_RR_LOAN_NUM), rs.getString(PROP_TEMP));
							propToLoanNumMap.put(rs.getString(PROP_TEMP), rs.getString(LOAN_NUM));
						}
						return new KeyValue<>(oldLoanNumToPropTempMap, propToLoanNumMap);
					});
			Map<String, String> oldLoanNumToPropTempMap = fetchedOldRrLoanMap.getKey();
			Map<String, String> propToLoanNumMap = fetchedOldRrLoanMap.getValue();
			migrationPropToLoanMap.putAll(propToLoanNumMap);
			propTempToLoanNumMap.forEach((k, v) -> {
				if (StringUtils.isNotEmpty(oldLoanNumToPropTempMap.get(v)) && !migrationNewPropToPropMap.containsKey(oldLoanNumToPropTempMap.get(v))) {
					migrationNewPropToPropMap.put(oldLoanNumToPropTempMap.get(v), k);
				}
			});
				}
			}
			return new KeyValue<>(migrationNewPropToPropMap, migrationPropToLoanMap);
		};
	}
	
	public void retrospectUpdateMigrationInformation() {
		log.info("retrospectUpdateMigrationInformation called");
		// existing non-migrated loans
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEKN_PARAMS_EXISTING_NON_MIGRATED_LOANS);
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEKN_PARAMS_ORIGINAL_EXISTING_NON_MIGRATED_LOANS);
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEK0_PARAMS_EXISTING_NON_MIGRATED_LOANS);
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEK0_PARAMS_ORIGINAL_EXISTING_NON_MIGRATED_LOANS);
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEKN_AUDIT_REPORTS_EXISTING_NON_MIGRATED_LOANS);
		// existing migrated loans
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEKN_PARAMS_EXISTING_MIGRATED_LOANS);
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEKN_PARAMS_ORIGINAL_EXISTING_MIGRATED_LOANS);
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEK0_PARAMS_EXISTING_MIGRATED_LOANS);
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEK0_PARAMS_ORIGINAL_EXISTING_MIGRATED_LOANS);
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEKN_AUDIT_REPORTS_EXISTING_MIGRATED_LOANS);
		// new migrations
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEKN_PARAMS_NEW_MIGRATED_LOANS);
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEKN_PARAMS_ORIGINAL_NEW_MIGRATED_LOANS);
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEK0_PARAMS_NEW_MIGRATED_LOANS);
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEK0_PARAMS_ORIGINAL_NEW_MIGRATED_LOANS);
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEKN_AUDIT_REPORTS_NEW_MIGRATED_LOANS);
		// existing non-migrated loans by old asset number
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEKN_PARAMS_EXISTING_NON_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER);
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEKN_PARAMS_ORIGINAL_EXISTING_NON_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER);
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEK0_PARAMS_EXISTING_NON_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER);
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEK0_PARAMS_ORIGINAL_EXISTING_NON_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER);
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEKN_AUDIT_REPORTS_EXISTING_NON_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER);
		// existing migrated loans by old asset number
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEKN_PARAMS_EXISTING_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER);
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEKN_PARAMS_ORIGINAL_EXISTING_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER);
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEK0_PARAMS_EXISTING_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER);
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEK0_PARAMS_ORIGINAL_EXISTING_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER);
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEKN_AUDIT_REPORTS_EXISTING_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER);
		// new migrations by old asset number
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEKN_PARAMS_NEW_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER);
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEKN_PARAMS_ORIGINAL_NEW_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER);
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEK0_PARAMS_NEW_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER);
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEK0_PARAMS_ORIGINAL_NEW_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER);
		mysqlJdbcTemplate.execute(UPDATE_DP_WEEKN_AUDIT_REPORTS_NEW_MIGRATED_LOANS_BY_OLD_ASSET_NUMBER);
	}
	
	public void checkForMigration(Map<String, String> assetMap){
		String rrMigrationQuery =  (String) cacheManager.getAppParamValue(AppParameterConstant.RR_MIGRATION_QUERY);
		String assetNumber = jdbcTemplate.execute(rrMigrationQuery, (PreparedStatementCallback<String>) ps -> {
			String asset = null;		
			ps.setString(1, assetMap.get(LOAN_NUM));
			ps.setString(2, assetMap.get(LOAN_NUM));
			try(ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					asset = rs.getString(LOAN_NUM);
					assetMap.put(OLD_RR_LOAN_NUM, rs.getString(OLD_RR_LOAN_NUM));
					assetMap.put(PROP_TEMP, rs.getString(PROP_TEMP));
				}
			} catch (SQLException se) {
				log.error("SQLException on checkForMigration. {}", se);
			}
			return asset;
		});
		if(assetNumber != null){
			assetMap.put(LOAN_NUM, assetNumber);
			DPMigrationMap dpMigrationMap = new DPMigrationMap();
			dpMigrationMap.setAssetNumber(assetMap.get(LOAN_NUM));
			dpMigrationMap.setOldAssetNumber(assetMap.get(OLD_RR_LOAN_NUM));
			dpMigrationMap.setPropTemp(assetMap.get(PROP_TEMP));
			dpMigrationMapDao.deleteAll();
			dpMigrationMapDao.save(dpMigrationMap);
			retrospectUpdateMigrationInformation();
			dpMigrationMapDao.deleteAll();
		}
	}
	
	public List<String> getPropTemps(String rrMigrationLoanNumQuery, List<String> assetNums){
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(ID_LIST, assetNums);
		List<String> propTemps = namedJdbcTemplate.execute(rrMigrationLoanNumQuery, parameters, (PreparedStatementCallback<List<String>>) ps -> {
			List<String> props = new ArrayList<String>();
			try(ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					props.add(rs.getString(PROP_TEMP));
				}
			} catch (SQLException se) {
				log.error("SQLException on getPropTemps. {}", se);
			}
			return props;
		});
		return propTemps;
	}
	
	public void checkAndUpdateNonUpdatedAssets() {
		Set<String> distinctNonUpdatedAssets = new HashSet<String>();
		
		// deleting loans with asset number null
		dpProcessParamsDao.deleteByAssetNumberIsNull();
		dpProcessWeekNParamsDao.deleteByAssetNumberIsNull();
		dpWeekNAuditReportsDao.deleteByAssetNumberIsNull();
		
		// finding loans with prop temp null
		distinctNonUpdatedAssets.addAll(dpProcessParamsDao.findNonUpdatedAssets());
		distinctNonUpdatedAssets.addAll(dpProcessWeekNParamsDao.findNonUpdatedAssets());
		distinctNonUpdatedAssets.addAll(dpWeekNAuditReportsDao.findNonUpdatedAssets());
		
		// finding old loans with prop temp null for migrated loans
		distinctNonUpdatedAssets.addAll(dpProcessParamsDao.findNonUpdatedMigratedOldAssets());
		distinctNonUpdatedAssets.addAll(dpProcessWeekNParamsDao.findNonUpdatedMigratedOldAssets());
		distinctNonUpdatedAssets.addAll(dpWeekNAuditReportsDao.findNonUpdatedMigratedOldAssets());
		
		if(CollectionUtils.isNotEmpty(distinctNonUpdatedAssets)){
			List<String> nonUpdatedAssets = distinctNonUpdatedAssets.stream().collect(Collectors.toList());	
			
			// get prop temps for assset numbers from RR db
			List<List<String>> splitListProps = ListUtils.partition(nonUpdatedAssets, initialQueryInClauseCount);
			List<Future<List<String>>> futureList = new ArrayList<>();
			List<String>  nonUpdatedProps = new ArrayList<String>();
			String rrMigrationLoanNumQuery = (String) cacheManager.getAppParamValue(AppParameterConstant.RR_MIGRATION_LOAN_NUM_QUERY);
			for (List<String> subListProps : splitListProps) {
				Future<List<String>> rrRespFuture = executorService
						.submit(getPropTempsFromRR(rrMigrationLoanNumQuery, subListProps));
				futureList.add(rrRespFuture);
			}
			for (Future<List<String>> rrRespFuture : futureList) {
				List<String> rrResp;
				try {
					rrResp = rrRespFuture.get();
					nonUpdatedProps.addAll(rrResp);
				} catch (InterruptedException | ExecutionException e) {
					log.error("Exception while checkAndUpdateNonUpdatedAssets : ", e);
				}
			}
			
			// adding asset numbers to prop temps list as previously prop temps were used as asset number  
			nonUpdatedProps.addAll(nonUpdatedAssets);
			Set<String> distinctNonUpdatedProps = nonUpdatedProps.stream().collect(Collectors.toSet());
			nonUpdatedProps = distinctNonUpdatedProps.stream().collect(Collectors.toList());	
			
			// getting migration maps and preparing migration update information
			final Map<String, String> migrationNewPropToPropMap = new HashMap<String, String>();
			final Map<String, String> migrationPropToLoanMap = new HashMap<String, String>();
			getMigrationMaps(migrationNewPropToPropMap, migrationPropToLoanMap, nonUpdatedProps);
			final Set<String> updatedProps = new HashSet<String>();
			List<DPMigrationMap> dpMigrationMaps = new ArrayList<DPMigrationMap>();
			migrationNewPropToPropMap.forEach((k, v) -> {
				updatedProps.add(k);
				updatedProps.add(v);
				DPMigrationMap dpMigrationMap = new DPMigrationMap();
				dpMigrationMap.setAssetNumber(migrationPropToLoanMap.get(k));
				dpMigrationMap.setOldAssetNumber(migrationPropToLoanMap.get(v));
				dpMigrationMap.setPropTemp(k);
				dpMigrationMaps.add(dpMigrationMap);
			});
			migrationPropToLoanMap.forEach((k, v) -> {
				if(!updatedProps.contains(k)){
					DPMigrationMap dpMigrationMap = new DPMigrationMap();
					dpMigrationMap.setAssetNumber(migrationPropToLoanMap.get(k));
					dpMigrationMap.setOldAssetNumber(null);
					dpMigrationMap.setPropTemp(k);
					dpMigrationMaps.add(dpMigrationMap);
					updatedProps.add(k);
				}
			});
			
			// updating migration information
			dpMigrationMapDao.deleteAll();
			dpMigrationMapDao.saveAll(dpMigrationMaps);
			retrospectUpdateMigrationInformation();
			dpMigrationMapDao.deleteAll();
			
			// updating loans with still prop temp is null as they are not present in RR db
			dpProcessParamsDao.updatePropTempNullAsAssetNumber();
			dpProcessWeekNParamsDao.updatePropTempNullAsAssetNumber();
			dpWeekNAuditReportsDao.updatePropTempNullAsAssetNumber();
		}
	}
	
	private Callable<List<String>> getPropTempsFromRR(String rrMigrationLoanNumQuery, List<String> subListProps) {
		return () -> {
			return getPropTemps(rrMigrationLoanNumQuery, subListProps);
		};
	}
	
	public Set<String> getNonMigratedProps(){
		List<String> nonMigratedProps = new ArrayList<String>();
		nonMigratedProps.addAll(dpProcessParamsDao.findNonMigratedAssets());
		nonMigratedProps.addAll(dpProcessWeekNParamsDao.findNonMigratedAssets());
		nonMigratedProps.addAll(dpWeekNAuditReportsDao.findNonMigratedAssets());
		return nonMigratedProps.stream().collect(Collectors.toSet());
	}

}