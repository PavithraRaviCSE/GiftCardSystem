package com.example.controller;

import com.example.service.JwtGenerator;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class RefreshTokenController extends ActionSupport implements ServletRequestAware, ServletResponseAware {

	private HttpServletRequest request;
	private HttpServletResponse httpresponse;

	private Map<String, Object> response = new HashMap<>();

	public String refreshAccessToken() {

		System.out.println("refredsh function called");
		String refreshToken = getRefreshTokenFromCookies();
		if (refreshToken == null || refreshToken.isEmpty()) {
			System.out.println("refresh token expired");
			httpresponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.put("error", "Refresh token is missing");
			return SUCCESS;
		}

		Map<String, Object> refreshData = JwtGenerator.validateToken(refreshToken);
		if (refreshData.isEmpty()) {
			System.out.println("redersh token  is expired");
			httpresponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.put("error", "Invalid or expired refresh token");
			return SUCCESS;
		}

		String newAccessToken = JwtGenerator.generateToken((long) refreshData.get("userId"),
				(String) refreshData.get("userType"), (String) refreshData.get("userName"), false);
		System.out.println("Refresh token called....");
		response.put("accessToken", newAccessToken);
		return SUCCESS;
	}

	private String getRefreshTokenFromCookies() {
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("refreshToken".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	public String deleteCookie() {
		try {
			String refreshToken = getRefreshTokenFromCookies();
			
			Cookie refreshTokenCookie = new Cookie("refreshToken", "");
			refreshTokenCookie.setHttpOnly(true);
			refreshTokenCookie.setSecure(false);
			refreshTokenCookie.setPath("/");
			refreshTokenCookie.setMaxAge(0);
			httpresponse.addCookie(refreshTokenCookie);

			response.put("success", "Logout successfully");

		} catch (Exception e) {
			System.out.println("failed to delete cookie");
			response.put("error", "Failed to delete cookie: " + e.getMessage());

		}

		return SUCCESS;
	}

	public Map<String, Object> getResponse() {
		return response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setServletResponse(HttpServletResponse httpresponse) {
		this.httpresponse = httpresponse;
	}
}
