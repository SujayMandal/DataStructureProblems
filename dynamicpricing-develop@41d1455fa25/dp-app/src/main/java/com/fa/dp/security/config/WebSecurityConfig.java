package com.fa.dp.security.config;

import com.fa.dp.security.config.ldap.CustomAuthenticationProvider;
import com.fa.dp.security.filter.DPSecurityFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.inject.Inject;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Inject
	private CustomAuthenticationProvider customAuthProvider;

	@Inject
	private DPSecurityFilter dpSecurityFilter;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authenticationProvider(customAuthProvider).sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS).and()
				.authorizeRequests().antMatchers("/static/tenant/resources*//**").permitAll().antMatchers("/assets//**").permitAll()
				.antMatchers("/resources/images/*").permitAll().antMatchers("/test").permitAll().antMatchers("/uploadSSInvestorFile").permitAll()
				.antMatchers("/classifiSopFile").permitAll()
				.antMatchers("/uploadPMICompanies").permitAll().antMatchers("/updateSystemParameter").permitAll().antMatchers("*//**")
				.hasAnyRole("USER", "ADMIN").anyRequest().fullyAuthenticated().and().formLogin().loginPage("/loginPage")
				.failureUrl("/loginPage?error=true").usernameParameter("username").passwordParameter("password").permitAll().and().logout()
				.logoutUrl("/logout").logoutSuccessUrl("/loginPage").and().exceptionHandling().accessDeniedPage("/403");
		http.headers().frameOptions().sameOrigin();
		http.csrf().disable().addFilterAfter(dpSecurityFilter, BasicAuthenticationFilter.class);
	}

}

/*
--- old code keeping it for reference
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .and().authorizeRequests()
                .antMatchers("/test")
                .permitAll()
                .antMatchers("*")
                .hasAnyRole("USER","ADMIN")
                .anyRequest().fullyAuthenticated().and().formLogin().loginPage("/loginPage")
                .failureUrl("/loginPage?error=true").usernameParameter("username").passwordParameter("password").permitAll()
                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/loginPage").and().exceptionHandling().accessDeniedPage("/403");

        http.csrf().disable().addFilterBefore(new DpCorsFilter(), BasicAuthenticationFilter.class);;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
            .withUser("user").password("{noop}password").roles("USER");
    }
}*/