package com.example.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.dao.AdminDAO;
import com.example.model.User;
import com.example.service.AdminService;
import com.example.service.RedisService;
import com.example.service.UserException;
import com.opensymphony.xwork2.ActionSupport;

public class AdminController extends ActionSupport {

	private String id;
	private String type;
	private String userName;
	private String userType;
	private String accountNumber;
	private double amount;
	private String dob;
	private String mobile;
	private User user;
	private List<User> userList = new ArrayList<>();
	private Map<String, Object> response = new HashMap<>();

	public void setId(String id) {
		this.id = id;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public Map<String, Object> getResponse() {
		return response;
	}

	public String create() { // users post

		System.out.println("create user");

		try {
			LocalDate date = LocalDate.parse(dob);
			System.out.println(userName + " " + userType + " " + date + " " + mobile + " " + amount);
			user = AdminService.addUser(userName, userType, date, mobile, amount);
			RedisService.removeUserList();
			response.put("success", "account created successfully");
			response.put("user", user);

		} catch (UserException e) {
			e.printStackTrace();
			response.put("error", e.getMessage());
		}
		return SUCCESS;
	}

	public String update() { // user/id put

		System.out.println("credit user");
		try {

			if (id != null && amount != 0) {
				accountNumber = id;
				System.out.println("account number:" + accountNumber + " amount:" + amount);
				boolean isCredited = AdminService.creditAmount(accountNumber, amount);
				if (isCredited) {
					RedisService.removeUserList();
					response.put("success", "amount credited successfully....");

				} else {
					response.put("error", "enter a valid accountNumber");
				}
			} else {
				response.put("error", "accountNumber or amount is null");
			}
		} catch (Exception e) {
			response.put("error", "cannot credit amount try again later" + e.getMessage());
		}

		return SUCCESS;
	}

	public String index() { // users get

		System.out.println("view  user");

		usersList();

		return SUCCESS;
	}

	private void usersList() {
		List<User> cachedList = RedisService.getUserListFromCache();
		if (cachedList != null && !cachedList.isEmpty()) {
			System.out.println("cached userlist");
			response.put("userList", cachedList);
			return;
		}

		try {
			List<User> userList = AdminDAO.getAllUsers();
			RedisService.saveUserListToCache(userList);
			System.out.println("userList: " + userList);
			response.put("userList", userList);
		} catch (Exception e) {
			response.put("error", "Failed to get gift card: " + e.getMessage());
		}

	}

	public String show() { // user/id get

		System.out.println("show user");

		try {
			if (id != null) {
				if (type != null && type.equals("accountNumber")) {
					accountNumber = id;
					System.out.println("accountNumber: " + accountNumber);
					User user1 = AdminService.getUserByAccountNumber(accountNumber);
					userList.add(user1);
				} else {
					userName = id;
					System.out.println("name: " + userName);
					userList = AdminService.getUsersByName(userName);
				}

				response.put("userList", userList);
			} else {

				response.put("error", "id is null");
			}

		} catch (Exception e) {
			response.put("error", "Excption occured" + e.getMessage());
			System.out.println(e);
		}

		return SUCCESS;
	}

	public void setType(String type) {
		this.type = type;
	}

}
