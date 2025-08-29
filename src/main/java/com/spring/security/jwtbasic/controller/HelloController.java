package com.spring.security.jwtbasic.controller;

import com.spring.security.jwtbasic.model.PostHelloModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	
	@GetMapping("/hello")
	public String Hello() {
		return "hello";
	}


	@PostMapping("/posthello")
	public String postHello(@RequestBody PostHelloModel postHelloModel){
		System.out.println(postHelloModel);
		return "saved the model somwhere";
	}
}
