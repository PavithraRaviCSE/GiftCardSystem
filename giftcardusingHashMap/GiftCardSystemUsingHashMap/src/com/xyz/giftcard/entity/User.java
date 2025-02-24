package com.xyz.giftcard.entity;

import java.time.LocalDate;
import java.util.Objects;

public class User {

	public static int customerCount = 0;

	private int id;
	private String accountNumber;
	private String name;
	private LocalDate dob;
	private double balance;
	private String password;
	private String mobile;

	public User(int id, String accountNumber, String name, LocalDate dob, double balance, String password,
			String mobile) {
		super();
		this.id = id;
		this.accountNumber = accountNumber;
		this.name = name;
		this.dob = dob;
		this.balance = balance;
		this.password = password;
		this.mobile = mobile;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public void setMobile(String role) {
		this.mobile = role;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", accountNumber=" + accountNumber + ", name=" + name + ", dob=" + dob + ", balance="
				+ balance + ", password=" + password + ", mobile=" + mobile + "]";
	}

	public String getUserData() {
		return "User [id=" + id + ", accountNumber=" + accountNumber + " passowrd=" + password + ", name=" + name + ", dob=" + dob + ", balance="
				+ balance + ", mobile=" + mobile + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(dob, mobile, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(dob, other.dob) && Objects.equals(mobile, other.mobile)
				&& Objects.equals(name, other.name);
	}

}
