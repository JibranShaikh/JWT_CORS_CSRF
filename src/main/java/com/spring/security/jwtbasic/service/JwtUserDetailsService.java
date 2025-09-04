package com.spring.security.jwtbasic.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService  implements UserDetailsService{

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if("randomuser".equalsIgnoreCase(username)) {
			List<GrantedAuthority> authorities = new ArrayList<>();
			authorities.add(new SimpleGrantedAuthority("HELLO"));
			//Customer customer = customerRepo.findByName(username);
			//List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(customer.getRole()));
			return new User("randomuser", "cGFzc3dvcmQ=", authorities);
		} else {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
	}

}
