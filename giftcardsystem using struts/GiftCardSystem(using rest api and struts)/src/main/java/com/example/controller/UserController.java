package com.example.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.dao.UserDAO;
import com.example.helper.Helper;
import com.example.model.GiftCard;
import com.example.model.GiftCardTransactions;
import com.example.model.User;
import com.example.service.ThreadLocalClass;
import com.example.service.UserException;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class UserController extends ActionSupport {

	private String id;
	private boolean isFirstLoggin;
	private String newPassword;
//	private String currentPassword;
	private Map<String, Object> response = new HashMap<>();
	Map<String, Object> userInfo = ThreadLocalClass.getUserInfo();

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public void setFirstLoggin(boolean isFirstLoggin) {
		this.isFirstLoggin = isFirstLoggin;
	}

//
//	public void setCurrentPassword(String currentPassword) {
//		this.currentPassword = currentPassword;
//	}
//	
	public Map<String, Object> getResponse() {
		return response;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String update() {
		try {
			System.out.println("Retrieved userId: " + userInfo.get("userId"));

			System.out.println(id + " " + newPassword);
			boolean message = UserDAO.changePassword((long)userInfo.get("userId"), Helper.encryption(id),
					Helper.encryption(newPassword), isFirstLoggin);
			if (message)
				response.put("success", "password changed successfully");

			else {
				response.put("error", "cannot change the password try again");

			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("error", "cannot change the password try again");

		}

		return SUCCESS;
	}

}
