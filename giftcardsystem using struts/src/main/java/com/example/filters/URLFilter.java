package com.example.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class URLFilter implements Filter {
	private static final List<String> ALLOWED_URLS = Arrays.asList("login", "logout", "purchase","PurchaseChart" ,"user/purchaseData", "admin/users",
			"AddUser", "ViewUser", "SearchUser", "CreditUser", "AdminHome", "user/cards", "user/cardTransaction",
			"user/changePassword", "BlockCard", "ChangePassword", "CreateCard", "ViewCard", "ViewTransaction",
			"TopUpCard", "UserHome", "Login", "Purchase", "refreshAccessToken", "css/form-table.css",
			"css/headerStyle.css", "css/tableStyle.css", "js/script.js", "js/userScript.js" );

	@Override
	public void init(FilterConfig filterConfig) {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String requestURI = req.getRequestURI();
		String contextPath = req.getContextPath();

		System.out.println(contextPath);
		if (!isValidURL(requestURI, contextPath)) {
			System.out.println("Blocked unknown URL: " + requestURI);
			res.setContentType("text/plain");
			res.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden
			res.getWriter().write("Unknown URL");
			return;
		}

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

	private boolean isValidURL(String requestURI, String contextPath) {
		String fullPath;

		for (String allowedURL : ALLOWED_URLS) {
			fullPath = contextPath + "/" + allowedURL;
			System.out.println("fullpath: " + fullPath + " request url: " + requestURI);
			if (requestURI.contains(fullPath)) {
				return true;
			}
		}
		return false;
	}

}

/*
 * 
 * private boolean isValidURLPath(String url) { try {
 * 
 * URL ur = new URL(url); System.out.println(ur.getHost());
 * System.out.println(ur.getPort()); new URL(url).toURI(); return true; } catch
 * (Exception e) { return false; } }
 */