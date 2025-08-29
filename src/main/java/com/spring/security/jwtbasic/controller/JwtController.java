package com.spring.security.jwtbasic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.spring.security.jwtbasic.jwtutils.TokenManager;
import com.spring.security.jwtbasic.model.JwtRequestModel;
import com.spring.security.jwtbasic.model.JwtResponseModel;
import com.spring.security.jwtbasic.service.JwtUserDetailsService;


@RestController
public class JwtController {
	
	
	@Autowired
	private JwtUserDetailsService userDetailsService;
	 
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private TokenManager tokenManager;

	
	@GetMapping("/login")
	   public ResponseEntity<?> createToken(@RequestHeader("Username") String username,
											@RequestHeader("Password") String password) throws Exception{

		try {
			authenticationManager.authenticate(new
		            UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
	         throw new Exception("USER_DISABLED", e);
	    } catch (BadCredentialsException e) {
	         throw new Exception("INVALID_CREDENTIALS", e);
	    }
		try {
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			String jwtToken = tokenManager.generateJWTToken(userDetails);
			return ResponseEntity.ok(new JwtResponseModel(jwtToken));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
}
