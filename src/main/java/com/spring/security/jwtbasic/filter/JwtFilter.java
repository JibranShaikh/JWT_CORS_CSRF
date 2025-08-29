package com.spring.security.jwtbasic.filter;

import java.io.IOException;

//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.spring.security.jwtbasic.jwtutils.TokenManager;
import com.spring.security.jwtbasic.service.JwtUserDetailsService;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtFilter extends OncePerRequestFilter {
	
	@Autowired
	private JwtUserDetailsService userDetailsService;
	
	@Autowired
	private TokenManager tokenManager;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		System.out.println("Entering jwtfilter");
		String tokenHeader = request.getHeader("Authorization");
		String userName = null;
	    String token = null;
		String uri = request.getRequestURI();
		if(uri.contains("/login")) {
			filterChain.doFilter(request,response);
			return;
		}
	    if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
	    	token = tokenHeader.substring(7);
	    	try {
	    		userName = tokenManager.getUserNameFromToken(token);
	    	} catch (IllegalArgumentException e) {
	            System.out.println("Unable to get JWT Token");
	         } catch (ExpiredJwtException e) {
	            System.out.println("JWT Token has expired");
	         }
	    } else {
	    	System.out.println("Bearer String not found in token");
	    }
	    if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	    	UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
	    	if(tokenManager.validateJwtToken(token, userDetails)) {
	    		
	    		UsernamePasswordAuthenticationToken
	            authenticationToken = new UsernamePasswordAuthenticationToken(
	            userDetails, null,
	            userDetails.getAuthorities());
	    		
	            authenticationToken.setDetails(new
	            WebAuthenticationDetailsSource().buildDetails(request));
	            
	            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	    	}
	    }
	    filterChain.doFilter(request, response);
		
	}

//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//	}
}
