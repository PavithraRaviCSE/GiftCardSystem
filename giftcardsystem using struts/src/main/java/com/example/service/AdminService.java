package com.example.service;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import com.example.dao.AdminDAO;
import com.example.dao.DatabaseConnection;
import com.example.helper.Helper;
import com.example.model.User;
import com.example.service.UserException;

public class AdminService {

	public static User addUser(String name, String userType, LocalDate dob, String mobile, double amount)
			throws UserException {

		if (AdminDAO.checkMobileNumberExist(mobile)) {
			throw new UserException("Mobile number already exists");
		}

		User user = null;

		try (Connection conn = DatabaseConnection.getConnection()) {
			conn.setAutoCommit(false);

			try {
				String account_number = AdminDAO.generateAccountNumber(conn, "account_number");
				String password = Helper.generatePassword(dob);

				user = new User(account_number, name, userType, dob, amount, Helper.encryption(password), mobile, true);

				AdminDAO.insertUser(conn, user);
				conn.commit();

				long id = AdminDAO.getUserId(account_number);
				System.out.println(" userid:" + id);
				user.setUserId(id);

				return user;

			} catch (Exception e) {
				conn.rollback();
				throw new UserException("Failed to add user: " + e.getMessage());
			}

		} catch (Exception e) {
			throw new UserException("Database connection error: " + e.getMessage());
		}
	}

	public static List<User> getUsersByName(String name) throws UserException {
		try {
			return AdminDAO.findUsersByName(name);
		} catch (Exception e) {
			throw new UserException("Error retrieving users by name" + e);
		}
	}

	public static User getUserByAccountNumber(String accountNumber) throws UserException {
		try {
			return AdminDAO.findUserByAccountNumber(accountNumber);
		} catch (Exception e) {
			throw new UserException("Error retrieving user by account number" + e);
		}
	}

	public static List<User> getAllUsers() throws UserException {
		try {
			return AdminDAO.getAllUsers();
		} catch (Exception e) {
			throw new UserException("Error retrieving users from the database" + e);
		}
	}

	public static boolean creditAmount(String accountNumber, double amount) throws Exception {
		if (accountNumber == null || accountNumber.isEmpty()) {
		
			throw new IllegalArgumentException("Account number cannot be null ");
		}
		if (amount <= 0) {
			throw new IllegalArgumentException("Amount must be greater than zero.");
		}

		return AdminDAO.creditAmount(accountNumber, amount);
	}

}
