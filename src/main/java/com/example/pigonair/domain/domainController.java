package com.example.pigonair.domain;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class domainController {

	@GetMapping("/home")
	public String home(){
		return "home";
	}
}
