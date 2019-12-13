package com.fa.dp.rest;

import com.fa.dp.DpApplication;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.junit.Assert.*;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

/**
 * @author misprakh
 */

@Sql(scripts = "classpath:create.sql")
@EnableAutoConfiguration//
public class HomeControllerTest  extends AbstractControllerTest {

	@Before
	public void setup() {
		this.mvc = MockMvcBuilders.standaloneSetup(new HomeController()).build();
	}

	@Test
	public void home() {
		String uri = "/test";
		MvcResult result = null;
		try {
			result = mvc.perform(get(uri).accept(MediaType.ALL)).andReturn();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int status = result.getResponse().getStatus();
		Assert.assertEquals(200, status);
	}
}