package com.fa.dp.dataSource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

	@Bean
	@Primary
	@ConfigurationProperties("spring.datasource.hikari")
	public DataSourceProperties mySqlDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@ConfigurationProperties("rtng.datasource.hikari")
	public DataSourceProperties rtngMySqlDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@ConfigurationProperties("rr.datasource.hikari")
	public DataSourceProperties rrMySqlDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@ConfigurationProperties("hubzu.datasource.hikari")
	public DataSourceProperties hubzuMySqlDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@ConfigurationProperties("stage5.datasource.hikari")
	public DataSourceProperties stage5MySqlDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@ConfigurationProperties("pmi.rr.datasource.hikari")
	public DataSourceProperties pmiRrMySqlDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@ConfigurationProperties("pmi.arlt.datasource.hikari")
	public DataSourceProperties pmiArltMySqlDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean(name = "dataSource")
	@Primary
	@ConfigurationProperties("spring.datasource.hikari")
	public DataSource mySqlDataSource() {
		return mySqlDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}

	/**
	 * Configures datasource to query RTNG data.
	 * <p>
	 * Usage : Initialize JdbcTemplate with this datasource query the records
	 *
	 * @return
	 */
	@Bean(name = "rtngDataSource")
	@ConfigurationProperties(prefix = "rtng.datasource.hikari")
	public DataSource RtngDataSource() {
		return rtngMySqlDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}

	/**
	 * Configures oracle datasource to query RR data.
	 * <p>
	 * Usage : Initialize JdbcTemplate with this datasource query the records
	 *
	 * @return
	 */
	@Bean(name = "rrDataSource")
	@ConfigurationProperties(prefix = "rr.datasource.hikari")
	public DataSource oracleRRDataSource() {
		return rrMySqlDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}

	/**
	 * Configures oracle datasource to query Hubzu data.
	 * <p>
	 * Usage : Initialize JdbcTemplate with this datasource query the records
	 *
	 * @return
	 */
	@Bean(name = "hubzuDataSource")
	@ConfigurationProperties(prefix = "hubzu.datasource.hikari")
	public DataSource oracleHubzuDataSource() {
		return hubzuMySqlDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}

	/**
	 * Configures oracle datasource to query stage5 data.
	 * <p>
	 * Usage : Initialize JdbcTemplate with this datasource query the records
	 *
	 * @return
	 */
	@Bean(name = "stage5DataSource")
	@ConfigurationProperties(prefix = "stage5.datasource.hikari")
	public DataSource oraclestageFiveDataSource() {
		return stage5MySqlDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}

	/**
	 * Configures Oracle datasource to query PMI flag (ENT_REPOS) data.
	 *
	 * @return
	 */
	@Bean(name = "pmiRRDataSource")
	@ConfigurationProperties(prefix = "pmi.rr.datasource.hikari")
	public DataSource oraclePmiFlagDataSource() {
		return pmiRrMySqlDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}

	/**
	 * Configures Oracle datasource to fetch Insurance comp ID to query ARLT data.
	 *
	 * @return
	 */
	@Bean(name = "pmiArltDataSource")
	@ConfigurationProperties(prefix = "pmi.arlt.datasource.hikari")
	public DataSource oraclePmiInscCompDataSource() {
		return pmiArltMySqlDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}

}
