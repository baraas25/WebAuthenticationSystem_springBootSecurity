package de.bs.webauthenticationsystem_be.security.configuration;

import de.bs.webauthenticationsystem_be.security.filter.JwtAccessDenied;
import de.bs.webauthenticationsystem_be.security.filter.JwtAuthenticationFilter;
import de.bs.webauthenticationsystem_be.security.filter.JwtAuthenticationHttp403;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private static final String[] PUBLIC_MATCHERS = {
    "/api/v1/user/home",
    "/api/v1/user/login",
    "/api/v1/user/register",
    "/api/v1/user/resetpassword/**",
    "/api/v1/user/activeUserAccount/*",
    "/h2-console/**",
    "/api/v1/user/all",
    "/logout",
    "/api/v1/pass/*"
  };
  private static final String[] USER_READ_MATCHERS = {
    "/api/v1/user/role", "/api/v1/user/changepassword/*",
  };

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtAccessDenied jwtAccessDenied;
  private final JwtAuthenticationHttp403 jwtAuthenticationHttp403;

  @Autowired
  public SecurityConfiguration(
      JwtAuthenticationFilter jwtAuthenticationFilter,
      JwtAccessDenied jwtAccessDenied,
      JwtAuthenticationHttp403 jwtAuthenticationHttp403) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.jwtAccessDenied = jwtAccessDenied;
    this.jwtAuthenticationHttp403 = jwtAuthenticationHttp403;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http = http.csrf().disable();
    http = http.sessionManagement().sessionCreationPolicy(STATELESS).and();
    http =
        http.exceptionHandling()
            .accessDeniedHandler(jwtAccessDenied)
            .authenticationEntryPoint(jwtAuthenticationHttp403)
            .and();

    http.authorizeRequests()
        .antMatchers(PUBLIC_MATCHERS)
        .permitAll()
        .antMatchers(USER_READ_MATCHERS)
        .hasAnyAuthority("user:read")
        .antMatchers("/api/v1/user/admin")
        .hasAnyAuthority("user:delete")
        .anyRequest()
        .authenticated();

    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    // TODO
    /*
    http.logout()
    .logoutUrl("/api/v1/user/logout")
    .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
    .clearAuthentication(true)
    .invalidateHttpSession(true)
    .deleteCookies("JSESSIONID");*/
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }
}
