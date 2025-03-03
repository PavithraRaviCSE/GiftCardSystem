package com.example.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.example.dao.BlockingQueueConnectionPool;
import com.example.dao.DatabaseConnection;
import com.example.dao.UserDAO;
import com.example.model.User;
import com.example.service.JwtGenerator;
import com.example.service.QuartzScheduler;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import io.jsonwebtoken.io.IOException;

public class LoginController extends ActionSupport {

	private long userId;
	private String password;

	private User user;
	private String token;

	private String refreshToken;

	private Map<String, Object> response = new HashMap<>();


	public String login() {
		try {

			System.out.println("Execute method....." + userId + " " + password);

			if (userId == 0 || password == null) {
				response.put("error", "User Id or password is null");
				return SUCCESS;
			}

			user = UserDAO.login(userId, password);

			if (user == null) {
				response.put("error", "Invalid user ID or password");
				return SUCCESS;
			}

			System.out.println(user);

			token = JwtGenerator.generateToken(userId, user.getUserType(), user.getUserName(), false);
			refreshToken = JwtGenerator.generateToken(userId, user.getUserType(), user.getUserName(), true);
			addRefreshTokenToCookie(refreshToken);
//			
			response.put("token", token);
			System.out.println("token: " + token);

		} catch (SQLException e) {
			e.printStackTrace();
			response.put("error", e.getMessage());
		}

		System.out.println("login execute method......");
		return SUCCESS;
	}

	private void addRefreshTokenToCookie(String refreshToken) {
		HttpServletResponse httpResponse = ServletActionContext.getResponse();
		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
		refreshTokenCookie.setHttpOnly(true); // js cannot access it
		refreshTokenCookie.setSecure(false); // browser will send this cookie in both http and https request
		refreshTokenCookie.setPath("/");
		refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
		httpResponse.addCookie(refreshTokenCookie);
	}

	public void setUserId(long userId) {
		System.out.println("user id: " + userId);
		this.userId = userId;
	}

	public void setPassword(String password) {
		System.out.println("password: " + password);
		this.password = password;
	}

	public Map<String, Object> getResponse() {
		return response;
	}

}

/*
 * ActionContext ac = ActionContext.getContext();
 * System.out.println("action context: " + ac);
 * System.out.println("action context name " + ac.getName()
 * +"    getActionInvocation: " + ac.getActionInvocation() +
 * "    \ngetApplication: " + ac.getApplication() + " getClass: "+ ac.getClass()
 * + " \ngetContextMap: " + ac.getContextMap() + " getValueStack "+
 * ac.getValueStack());
 * 
 * 
 */