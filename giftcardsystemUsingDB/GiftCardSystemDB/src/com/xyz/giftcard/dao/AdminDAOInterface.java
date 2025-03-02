package com.xyz.giftcard.dao;

import java.util.List;

import com.xyz.giftcard.entity.User;

public interface AdminDAOInterface {
	
	boolean insertUser(User user);

	boolean UpdateBalance(String accountnumber, double amount);

	List<User> getAllUser();

	List<User> getUserByName(String name);

	List<User> getUsersByAccountNumber(String accountNumber);

	boolean printUserByAccountNumber(String accountNumber);
}
