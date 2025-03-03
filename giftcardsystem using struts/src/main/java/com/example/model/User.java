package com.example.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Timestamp;
import java.time.LocalDate;

public class User {
	private long userId;
	private String accountNumber;
	private String userName;
	private String userType;

//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate dob;
	private double balance;
	private String password;
	private String mobile;
	private Timestamp createdAt;
	private boolean isFirstLogin;

	public User(long userId, String accountNumber, String userName, String userType, LocalDate dob, double balance,
			String password, String mobile, Timestamp createdAt, boolean isFirstLogin) {
		super();
		this.userId = userId;
		this.accountNumber = accountNumber;
		this.userName = userName;
		this.userType = userType;
		this.dob = dob;
		this.balance = balance;
		this.password = password;
		this.mobile = mobile;
		this.createdAt = createdAt;
		this.isFirstLogin = isFirstLogin;

	}

	public User(String accountNumber, String userName, String userType, LocalDate dob, double balance, String password,
			String mobile, boolean is_first_login) {
		super();
		this.accountNumber = accountNumber;
		this.userName = userName;
		this.userType = userType;
		this.dob = dob;
		this.balance = balance;
		this.password = password;
		this.mobile = mobile;
		this.isFirstLogin = is_first_login;

	}

	public User() {
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return String.format("%-15d %-15s %-15s %-15s %-15.2f %-20s  ", userId, accountNumber, userName, dob, balance,
				mobile);
	}

	public boolean getIsFirstLogin() {
		return isFirstLogin;
	}

	public void setIsFirstLogin(boolean is_first_login) {
		this.isFirstLogin = is_first_login;
	}

	public static String getColumn() {
		return String.format("%-15s %-15s %-15s %-15s %-15s %-15s", "userId", "accountNumber", "username", "dob",
				"balance", "mobile");
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

}
