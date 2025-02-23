package com.xyz.giftcard.services;

import java.util.Scanner;

import com.xyz.giftcard.entity.Admin;
import com.xyz.giftcard.repo.UserRepository;

public class AdminService implements AdminServiceInterface {

	static Scanner input = new Scanner(System.in);
	private UserRepository userRepo = new UserRepository();

	public void adminFunctionalities() {
		System.out.println("Enter username: ");
		String userName = input.next();
		System.out.println("Enter password: ");
		String password = input.next();

		Admin admin = adminLogin(userName, password);

		if (admin == null) {
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
			int choice = input.nextInt();

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
			default:
				System.out.println("Please enter a valid option....");
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
		System.out.println("Enter your mobile: ");
		String mobile = input.next();
		System.out.println("Enter the amount: ");
		double amount = input.nextDouble();

		if (!userRepo.isMobileNumberExist(mobile)) {
			userRepo.addUser(userName, dob, amount, mobile);

		} else {
			System.out.println("mobile already exist.. \n Please provide unique values");
		}

	}

	public Admin adminLogin(String userName, String password) {

		return userRepo.adminLogin(userName, password);

	}

	public void creditAmount() {

		System.out.println("Enter the accountNumber: ");
		String accountNumber = input.next();
		if (userRepo.isAccountNumberExist(accountNumber)) {
			System.out.println("Enter the amount: ");
			double amount = input.nextDouble();
			userRepo.credit(amount, accountNumber);

		} else {
			System.out.println("Incorrect account Number...\nPlease provide valid account number");
		}

	}

	public void searchUser() {

		System.out.println("1.Search user by name");
		System.out.println("2.Search user by AccountNumber: ");
		System.out.println("Enter your option: ");
		int option = input.nextInt();

		if (option == 1) {
			String name = input.next();
			userRepo.searchUserByName(name);

		} else if (option == 2) {
			String accountNumber = input.next();
			userRepo.searchUserByAccountNumber(accountNumber);

		}
	}

	public void viewUsers() {
		userRepo.viewUsers();
	}

	

}
