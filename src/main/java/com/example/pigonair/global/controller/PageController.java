package com.example.pigonair.global.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Transaction;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PageController {
	@GetMapping("/signup")
	public String signupPage(Model model) {
		model.addAttribute("ErrorMessage", null);
		return "signup";
	}

	@GetMapping("/login-page")
	public String loginPage() {
		return "login";
	}

	@GetMapping("/")
	public String homePage(HttpServletRequest request) {
		setTransactionNameBasedOnJMeterTag(request);
		return "index";
	}

	@GetMapping("/mypage")
	public String myPage(@AuthenticationPrincipal UserDetails userDetails) {
		return "mypage";
	}

	@GetMapping("/favicon.ico")
	@ResponseBody
	public void returnNoFavicon() {
	}
	private void setTransactionNameBasedOnJMeterTag(HttpServletRequest request) {
		Transaction transaction = ElasticApm.currentTransaction();
		String threadGroupName = request.getHeader("X-ThreadGroup-Name");
		if (threadGroupName != null && !threadGroupName.isEmpty()) {
			transaction.setName("Transaction-" + threadGroupName);
		}
	}
}
