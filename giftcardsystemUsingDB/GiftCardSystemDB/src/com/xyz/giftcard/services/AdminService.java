package com.xyz.giftcard.services;

import java.sql.Timestamp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import com.xyz.giftcard.dao.AdminDAO;
import com.xyz.giftcard.entity.Admin;
import com.xyz.giftcard.entity.User;
import com.xyz.giftcard.helper.Helper;

public class AdminService implements AdminServiceInterface {

	private static Scanner input = new Scanner(System.in);
	private AdminDAO adminDao = new AdminDAO();

	public void adminFunctionalities() {
		System.out.println("Enter username: ");
		String userName = input.next();
		System.out.println("Enter password: ");
		String password = input.next();

		Admin admin1 = adminDao.adminLogin(userName, password);

		if (admin1 == null) {
			System.out.println("Please provide a valid username and password");
			return;
		}

		System.out.println("Logged in successfully......");
		adminMenu();

	}

	public void adminMenu() {

		boolean isLoggedin = true;

		while (isLoggedin) {

			displayMenu();
			int choice = Helper.getValidChoice(1, 5, input);
			switch (choice) {
			case 1:
				isLoggedin = false;
				System.out.println("Logged out successfully......");
				break;
			case 2:
				addUser();
				break;
			case 3:
				searchUser();
				break;
			case 4:
				creditAmount();
				break;
			case 5:
				viewUsers();
				break;

			}

		}
	}

	private void displayMenu() {
		System.out.println("Menu");
		System.out.println("1. Exit/Logout");
		System.out.println("2. Add new user");
		System.out.println("3. search user");
		System.out.println("4. credit amount");
		System.out.println("5. Show User");
		System.out.println("Enter your option:");

	}

	public void addUser() {

		System.out.println("Enter Name: ");
		String userName = input.next();
		System.out.println("Enter Dob (dd/mm/yyy): ");
		String dob = input.next();
		LocalDate date = null;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			date = LocalDate.parse(dob, formatter);
//			System.out.println("Parsed date: " + date);
		} catch (DateTimeParseException e) {
			System.err.println("Invalid date format. Please use the format dd/MM/yyyy.");
			return;
		}
		System.out.println("Enter your mobile: ");
		String mobile = input.next();
		System.out.println("Enter the amount: ");
		double amount = input.nextDouble();

		String account_number = adminDao.generateAccountNumber("account_number");

		String password = Helper.generatePassword(date);
		User user = new User(account_number, userName, date, amount, Helper.encryption(password), mobile, true);

		try {
			if (adminDao.insertUser(user)) {
				System.out.println("User created successfully");
				System.out.println("account_number: " + user.getAccountNumber());
			}
		} catch (Exception e) {
			System.out.println("exception.............: " + e);
		}

	}

	public void creditAmount() {

		System.out.println("Enter the account number: ");
		String accountNumber = input.next();

		if (adminDao.printUserByAccountNumber(accountNumber)) {
			System.out.println("Enter the amount: ");
			double amount = input.nextDouble();
			if (adminDao.UpdateBalance(accountNumber, amount)) {

				System.out.println("Amount credited successfully......");
			} else {
				System.out.println("Unable to credit");
			}
		}

	}

	public void searchUser() {

		System.out.println("1.Search user by name");
		System.out.println("2.Search user by AccountNumber: ");
		System.out.println("Enter your option: ");
		int option = Helper.getValidChoice(1, 2, input);

		if (option == 1) {
			System.out.println("Enter the name: ");
			String name = input.next();
			List<User> users = adminDao.getUserByName(name);
			Helper.displayList(users, User.getColumn(), false);

		} else if (option == 2) {
			System.out.println("Enter the account number: ");
			String accountNumber = input.next();
			List<User> users = adminDao.getUsersByAccountNumber(accountNumber);
			Helper.printList(users, User.getColumn());

		}
	}


	public void viewUsers() {
		Helper.printList(adminDao.getAllUser(), User.getColumn());
	}

}
