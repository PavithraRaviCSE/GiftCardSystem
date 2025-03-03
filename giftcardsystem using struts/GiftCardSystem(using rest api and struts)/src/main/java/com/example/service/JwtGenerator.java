package com.example.service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JwtGenerator {

	private static JwtGenerator jwtClass = new JwtGenerator();

	private static long ACCESS_TOKEN_EXPIRY_MINUTES = 15;
	private static long REFRESH_TOKEN_EXPIRY_DAYS = 1;

	private Key getSecretKey() {
		Properties prop = new Properties();
		try {
			prop.load(this.getClass().getClassLoader().getResourceAsStream("struts.properties"));
			return Keys.hmacShaKeyFor(prop.getProperty("secret.key.string").getBytes());
		} catch (java.io.IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	
	public static String generateToken(long userId, String userType, String userName, boolean isRefreshToken) {
		try {
			Instant now = Instant.now();
			long expiryMinutes = isRefreshToken ? REFRESH_TOKEN_EXPIRY_DAYS * 24 * 60 : ACCESS_TOKEN_EXPIRY_MINUTES;

			return Jwts.builder().claim("userId", userId).claim("userType", userType).claim("userName", userName)
					.setIssuedAt(Date.from(now)).setExpiration(Date.from(now.plus(expiryMinutes, ChronoUnit.MINUTES)))
					.signWith(jwtClass.getSecretKey(), SignatureAlgorithm.HS256).compact();
		} catch (Exception e) {
			System.out.println("Error generating token: " + e.getMessage());
			return null;
		}
	}



	public static Map<String, Object> validateToken(String token) {
		Map<String, Object> userDetails = new HashMap<>();

		try {
			Claims claims = Jwts.parserBuilder().setSigningKey(jwtClass.getSecretKey()).build().parseClaimsJws(token)
					.getBody();

			Date expiration = claims.getExpiration();
			if (expiration.after(new Date())) {
				userDetails.put("userId", claims.get("userId", Long.class));
				userDetails.put("userType", claims.get("userType", String.class));
				userDetails.put("userName", claims.get("userName", String.class));
			}
		} catch (Exception e) {
			System.out.println("Invalid token: " + e.getMessage());
		}
		return userDetails;
	}
}



/*
ServletContext context = ServletActionContext.getServletContext();
InputStream input = context.getResourceAsStream("/WEB-INF/classes/struts.properties");
Properties prop = new Properties();
prop.load(input);


ResourceBundle bundle = ResourceBundle.getBundle("struts");
String value = bundle.getString("somePropertyKey");



*/