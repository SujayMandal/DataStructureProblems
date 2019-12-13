package com.fa.dp.core.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

@Configuration
@EnableScheduling
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = { "com.fa.dp.business.*.dao", "com.fa.dp.business.*.*.dao", "com.fa.dp.core.*.dao" })
@EntityScan(basePackages = { "com.fa.dp.core.*.domain", "com.fa.dp.core.*.*.domain", "com.fa.dp.business.*.domain", "com.fa.dp.business.*.entity",
		"com.fa.dp.business.*.*.entity", "com.fa.dp.business.*.*.*.entity" })
//@ComponentScan(basePackages = {"com.fa.dp.business.week0.report.delegate"})
public class DPTestConfig {
	@Bean
	public DataSource initializeDatasource() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
				// .addScript("data_qe.sql")
				.setName("testDB;MODE=MySQL").build();
	}
}
