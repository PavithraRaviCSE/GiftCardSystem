package com.xyz.giftcard.entity;

import java.time.LocalDate;
import java.sql.Timestamp;

public class User {
	private long userId;
	private String accountNumber;
	private String name;
	private LocalDate dob;
	private double balance;
	private String password;
	private String mobile;
	private Timestamp createdAt;
	private boolean isFirstLogin;

	public User(long userId, String accountNumber, String name, LocalDate dob, double balance, String password,
			String mobile, Timestamp createdAt, boolean isFirstLogin) {
		super();
		this.userId = userId;
		this.accountNumber = accountNumber;
		this.name = name;
		this.dob = dob;
		this.balance = balance;
		this.password = password;
		this.mobile = mobile;
		this.createdAt = createdAt;
		this.isFirstLogin = isFirstLogin;

	}

	public User(String accountNumber, String name, LocalDate dob, double balance, String password, String mobile,
			boolean is_first_login) {
		super();
		this.accountNumber = accountNumber;
		this.name = name;
		this.dob = dob;
		this.balance = balance;
		this.password = password;
		this.mobile = mobile;
		this.isFirstLogin = is_first_login;

	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		return String.format("%-15d %-15s %-15s %-15s %-15.2f %-20s  ", userId, accountNumber, name, dob, balance,
				mobile);
	}

	public boolean is_first_login() {
		return isFirstLogin;
	}

	public void is_first_login(boolean is_first_login) {
		this.isFirstLogin = is_first_login;
	}

	public static String getColumn() {
		return String.format("%-15s %-15s %-15s %-15s %-15s %-15s", "userId" , "accountNumber", "name", "dob",
				"balance", "mobile");
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

}
