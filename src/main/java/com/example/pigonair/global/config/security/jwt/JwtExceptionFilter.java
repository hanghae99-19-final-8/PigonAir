package com.example.pigonair.global.config.security.jwt;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "JWT 인증 에러 처리")
public class JwtExceptionFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException e) {
			handleException(response, "Token expired. Please login again.", "/login-page");
		} catch (MalformedJwtException | SecurityException e) {
			handleException(response, "Invalid token format.", "/login-page");
		} catch (UnsupportedJwtException | IllegalArgumentException e) {
			handleException(response, "Token not supported.", "/error-page");
		} catch (Exception e) {
			handleException(response, "An internal error occurred.", "/error-page");
		}
	}

	private void handleException(HttpServletResponse response, String message, String redirectPath) throws IOException {
		log.error("JWT token validation error: " + message);
		clearCookies(response);
		redirect(response, redirectPath);
	}

	private void clearCookies(HttpServletResponse response) {
		response.addHeader("Set-Cookie", "Authorization=; Path=/; HttpOnly; Expires=Thu, 01 Jan 1970 00:00:00 GMT");
	}

	private void redirect(HttpServletResponse response, String path) throws IOException {
		if (!response.isCommitted()) {
			response.sendRedirect(path);
		}
	}

}
