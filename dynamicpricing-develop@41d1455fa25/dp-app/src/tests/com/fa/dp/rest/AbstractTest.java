package com.fa.dp.rest;

import com.fa.dp.DpApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

/*
@AutoConfigureMockMvc*/

@RunWith(SpringRunner.class)
@ContextConfiguration
@SpringBootTest(classes = DpApplication.class)
@TestPropertySource(locations ="classpath:application-test-integration.properties")
@AutoConfigureMockMvc
public abstract class AbstractTest {

}

