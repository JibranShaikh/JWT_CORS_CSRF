package com.spring.security.jwtbasic.config;

import com.spring.security.jwtbasic.filter.CSRFCookieFilter;
import com.spring.security.jwtbasic.filter.JwtFilter;
import com.spring.security.jwtbasic.jwtutils.JwtAuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

//import com.tutorialspoint.security.formlogin.jwtutils.JwtAuthenticationEntryPoint;
//import com.tutorialspoint.security.formlogin.jwtutils.JwtFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
	private JwtAuthenticationEntryPoint authenticationEntryPoint;
	@Autowired
	private JwtFilter filter;

	@Autowired
	private CSRFCookieFilter csrfCookieFilter ;

	@Bean
	protected PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// this object will validate the received csrf token is same as the generated token.
		CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();
		//the below line will not require to save user details/jsessionid explicitly in SecConHol, spring security will
		//automatically save it.
		http.securityContext(contextConfig->contextConfig.requireExplicitSave(false));
		return http.cors(corsConfig->corsConfig.configurationSource(new CorsConfigurationSource() {
					@Override
					public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
						CorsConfiguration cors = new CorsConfiguration();
						cors.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
						cors.setAllowedMethods(Collections.singletonList("*"));
						cors.setAllowedHeaders(Collections.singletonList("*"));
						cors.setMaxAge(3600L);
						cors.setAllowCredentials(true);
						return cors;
					}
				}))
				//.csrf(AbstractHttpConfigurer::disable)
				.csrf(csrfConfig-> csrfConfig
						.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
						.ignoringRequestMatchers("/login")
				//this will generate and store the csrf token as a cookie. withHttpOnlyFalse will allow your UI app to read the cookie or else by default only the browser
				//can read the cookie.
						.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
				.authorizeHttpRequests(request -> request.requestMatchers("/login").permitAll()
						.anyRequest().authenticated())
				// Send a 401 error response if user is not authentic.
				.exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
				// no session management
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
				// filter the request and add authentication token
				.addFilterBefore(filter,  UsernamePasswordAuthenticationFilter.class)
				.addFilterAfter(csrfCookieFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	@Bean
	AuthenticationManager customAuthenticationManager() {
		return authentication -> new UsernamePasswordAuthenticationToken("randomuser123","password");
	}
}