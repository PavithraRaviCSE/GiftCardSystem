package com.xyz.giftcard.repo;

import com.xyz.giftcard.entity.Admin;

public interface AdminFunctionalities {

	Admin adminLogin(String name, String password);

	void addUser(String name, String dob, double amount, String mobile);

	void searchUserByAccountNumber(String accountNumber);

	void searchUserByName(String name);

}
