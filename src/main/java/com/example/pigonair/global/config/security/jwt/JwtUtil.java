package com.example.pigonair.global.config.security.jwt;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.pigonair.global.config.common.exception.ErrorCode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Getter
@Slf4j(topic = "JWT Util")
public class JwtUtil {

	// Header KEY 값
	public static final String AUTHORIZATION_HEADER = "Authorization";
	// 사용자 권한 값의 KEY
	public static final String AUTHORIZATION_KEY = "auth";
	// Token 식별자
	public static final String BEARER_PREFIX = "Bearer ";
	// 토큰 만료시간

	@Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
	private String secretKey;

	@Value("${spring.jwt.token.access-expiration-time}")
	private long accessExpirationTime;

	@Value("${spring.jwt.token.refresh-expiration-time}")
	private long refreshExpirationTime;
	private Key key;
	private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

	// 로그 설정
	public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

	@PostConstruct
	public void init() {
		byte[] bytes = Base64.getDecoder().decode(secretKey);
		key = Keys.hmacShaKeyFor(bytes);
	}

	// JWT 생성
	public String createToken(String email) {
		Date date = new Date();
		Date expireDate = new Date(date.getTime() + accessExpirationTime); // 만료 시간 설정 부분

		return BEARER_PREFIX +
			Jwts.builder()
				.setSubject(email) // 사용자 식별자값(ID)
				.claim("email", email)
				.setExpiration(expireDate) // 만료 시간
				.setIssuedAt(date) // 발급일
				.signWith(key, signatureAlgorithm) // 암호화 알고리즘 ***
				.compact();
	}

	public String createRefreshToken() {
		Date now = new Date();
		Date expireDate = new Date(now.getTime() + refreshExpirationTime);
		Claims claims = Jwts
			.claims();

		// refresh Token 반환
		return BEARER_PREFIX + Jwts.builder()
			.setIssuedAt(now)
			.setExpiration(expireDate)
			.setClaims(claims)
			.signWith(key, signatureAlgorithm) // 암호화 알고리즘 ***
			.compact();
	}

	// JWT Cookie 에 저장
	public void addJwtToCookie(String token, HttpServletResponse res) {
		try {
			// 특수문자 혹은 공백을 없애기 위해 URL 인코더로 인코딩
			token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20");

			Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token);
			cookie.setPath("/");

			res.addCookie(cookie);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}
	}

	// JWT 토큰 substring
	public String substringToken(String tokenValue) {
		if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
			return tokenValue.split(" ")[1].trim();
		}
		logger.error("Not Found Token");
		throw new NullPointerException("Not Found Token");
	}

	// 토큰 검증
	public void validateToken(String token, String email) throws
		/*
		액세스 토큰을 검증
		만약 액세스 토큰이 유효하지 않다면 JwtExceptionfilter에서 리프레쉬 토큰을 이용하여 액세스 토큰을 재발급
		 */
		ExpiredTokenException,
		UnsupportedJwtException,
		SecurityException,
		MalformedJwtException,
		IllegalArgumentException {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
		} catch (ExpiredJwtException e) {
			throw new ExpiredTokenException(e.getHeader(), e.getClaims(),
				ErrorCode.ACCESS_TOKEN_ERROR.getMessage(), token, email);
		}
	}

	public void validateRefreshToken(String token){
		if (token == null || token.isEmpty()) {
			throw new ExpiredJwtException(null, null, ErrorCode.REFRESH_TOKEN_ERROR.getMessage());
		}
	}

	public Claims getUserInfoFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}

	// HttpServletRequest 에서 Cookie Value : JWT 가져오기
	public String getTokenFromRequest(HttpServletRequest req) {
		// 쿠키 전부 배열에 담기
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(AUTHORIZATION_HEADER)) {
					try {
						return URLDecoder.decode(cookie.getValue(), "UTF-8"); // Encode 되어 넘어간 Value 다시 Decode
					} catch (UnsupportedEncodingException e) {
						return null;
					}
				}
			}
		}
		return null;
	}

}
