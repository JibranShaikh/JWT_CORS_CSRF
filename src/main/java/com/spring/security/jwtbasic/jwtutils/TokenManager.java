package com.spring.security.jwtbasic.jwtutils;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecretKeyBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;


@Component
public class TokenManager implements Serializable{
	
	   private static final long serialVersionUID = 7008375124389347049L;
	   public static final long TOKEN_VALIDITY = 10 * 60 * 60;

	   public static SecretKey secret = Jwts.SIG.HS256.key().build();

	   public String generateJWTToken(UserDetails userDetails) {
		   String jwtToken = "no token";
		   try {
			    jwtToken = Jwts.builder()
						.issuer("Myapp")
						.subject(userDetails.getUsername())
						.claim("username", userDetails.getUsername())
						.issuedAt(new Date(System.currentTimeMillis()))
						.expiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY * 1000))
						.signWith(secret)
						.compact();

		   } catch (Exception e ){
			   System.out.println(e);
		   }
		   return jwtToken;
	   }
	   
	   
	   public boolean validateJwtToken(String token, UserDetails userDetails) {
		   try {
			   String userName = getUserNameFromToken(token);
			   //SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
			   Claims claims = Jwts.parser().verifyWith(secret).build().parseSignedClaims(token).getPayload();
			   //Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
			   boolean isTokenExpired = claims.getExpiration().before(new Date());
			   return (userName.equalsIgnoreCase(userDetails.getUsername()) && !isTokenExpired);
		   } catch(Exception e ){
			   throw new BadCredentialsException("Invalid Token Received");
		   }
		   
	   }
	   
	   
	   public String getUserNameFromToken(String token) {
		   //SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
		   Claims claims = Jwts.parser().verifyWith(secret).build().parseSignedClaims(token).getPayload();
		   return claims.getSubject();
	   }
}
