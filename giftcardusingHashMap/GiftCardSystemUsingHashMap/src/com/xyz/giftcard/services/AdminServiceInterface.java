package com.xyz.giftcard.services;

import com.xyz.giftcard.entity.Admin;

public interface AdminServiceInterface {
	Admin adminLogin(String name, String password);

	void adminFunctionalities();

	void adminMenu();

	void addUser();

	void creditAmount();

	void searchUser();

	void viewUsers();
}
