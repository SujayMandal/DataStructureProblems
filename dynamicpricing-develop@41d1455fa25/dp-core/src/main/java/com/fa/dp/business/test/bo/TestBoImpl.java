package com.fa.dp.business.test.bo;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import com.fa.dp.business.test.dao.TestDao;
import com.fa.dp.business.test.domain.TestEntity;

import org.springframework.jdbc.core.JdbcTemplate;

@Named
public class TestBoImpl implements TestBo {

    @Inject
    private TestDao testDao;

    @Inject
    @Named(value = "rtngDataSource")
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initializeTemplate() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }



    @Override public List<TestEntity> getAll() {

        List<RtngInfo> rtngInfos = jdbcTemplate.query("select * from rpt_dynmc_prcng", new RtngTestRowMapper());
        return testDao.findAll();
    }
}
