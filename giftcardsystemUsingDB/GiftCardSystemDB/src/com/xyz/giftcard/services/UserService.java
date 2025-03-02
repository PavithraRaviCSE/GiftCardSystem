package com.xyz.giftcard.services;

import java.util.List;
import java.util.Scanner;

import com.xyz.giftcard.dao.UserDAO;
import com.xyz.giftcard.entity.GiftCard;
import com.xyz.giftcard.entity.GiftCardTransaction;
import com.xyz.giftcard.entity.User;
import com.xyz.giftcard.helper.Helper;

public class UserService implements UserServiceInterface {

	private static Scanner input = new Scanner(System.in);
	private static UserDAO userDao = new UserDAO();

	public void userFunctionalities() {

		while (true) {

			System.out.println("Enter account Number: ");
			String accountNumber = input.next();
			System.out.println("Enter password: ");
			String password = input.next();

			User user = userDao.userLogin(accountNumber, Helper.encryption(password));

			if (user == null) {

				System.out.println("login failed........\nenter correct username and password \n");
				break;

			}

			System.out.println("Logged in successfully......");
			userMenu(user);
			break;

		}

	}

	public void userMenu(User user) {

		boolean isLoggedIn = true;

		if (user.is_first_login()) {
			System.out.println("change password...");
			changePassword(user, true);
		}
		while (isLoggedIn) {

			displayMenu();

			int choice = getValidChoice(1, 8);

			switch (choice) {
			case 1:
				isLoggedIn = false;
				System.out.println("Logged out successfully......");
				break;
			case 2:
				generateGiftCard(user);
				break;
			case 3:
				Helper.displayList(getCardListOf(user, null), GiftCard.getColumn(), false);
				break;
			case 4:
				topUp(user);
				break;
			case 5:
				viewCardTransaction(user);
				break;
			case 6:
				blockGiftCard(user);
				break;
			case 7:
				changePassword(user, false);
				break;
			case 8:
				getAccountBalance(user);
				break;
			default:
				System.out.println("Please enter a valid option.");
				break;
			}
		}

	}

	private void getAccountBalance(User user) {
 
		System.out.println(userDao.checkBalance(user));
		
	}

	int getValidChoice(int min, int max) {
		int choice;
		while (true) {
			try {
				choice = input.nextInt();
				if (choice >= min && choice <= max) {
					return choice;
				}
				System.out.println("Please enter a number between " + min + " and " + max + ".");
			} catch (Exception e) {
				System.out.println("Invalid input. Please enter a valid number.");
				input.nextLine();
			}
		}
	}

	private void displayMenu() {
		System.out.println("User Menu:");
		System.out.println("1. Exit/Logout");
		System.out.println("2. Generate gift card");
		System.out.println("3. View card");
		System.out.println("4. Top up card amount");
		System.out.println("5. View Gift Card Transaction");
		System.out.println("6. Block gift card");
		System.out.println("7. Change password");
		System.out.println("8. Check Balance");
		System.out.println("Enter your choice: ");
	}

	public void generateGiftCard(User user) {

		System.out.println("Enter the amount: ");
		double amount = input.nextDouble();

		if (user.getBalance() >= amount) {

			GiftCard card = userDao.createGiftCard(user, amount);
			if (card == null) {
				System.out.println("Error in card creation");
				return;
			}
			System.out.println("card number: " + card.getCardNumber() + "\nPin: " + card.getPin());

		} else {
			System.out.println("your Account balance is inSuffient..");
		}

	}

	public void viewCards(List<GiftCard> cardList) {

		if (cardList.isEmpty()) {
			System.out.println("No cards found");
			return;
		}

		int count = 01;
		System.out.println("Sn: \t" + GiftCard.getColumn());
		for (GiftCard c : cardList) {
			System.out.println(count++ + "\t" + c);
		}

	}

	public void changePassword(User user, boolean b) {
		System.out.println("Enter your new Password: ");
		String password = input.next();
		userDao.changePassword(user, Helper.encryption(password), b);
	}

	public void viewCardTransaction(User user) {

		List<GiftCard> cardList = getCardListOf(user, null);
		if (cardList.isEmpty()) {
			System.out.println("No cards available....");
			return;
		}

		Helper.displayList(cardList, GiftCard.getColumn(), true);

		System.out.println("Enter the option: ");
		int option = getValidChoice(1, cardList.size());
		Helper.displayList(userDao.getTransactionHistory(cardList.get(option - 1)), GiftCardTransaction.getColumn(),
				false);

	}

	public void topUp(User user) {
		List<GiftCard> cardList = userDao.getGiftCardList(user, 1);

		if (cardList.isEmpty()) {
			System.out.println("No active card found...");
			return;
		}

		Helper.displayList(cardList, GiftCard.getColumn(), true);

		System.out.println("Enter your option: ");
		int option = getValidChoice(1, cardList.size());
		GiftCard selectedCard = cardList.get(option - 1);

		System.out.println("Enter the amount: ");
		double amount = input.nextDouble();

		if (user.getBalance() >= amount) {

			if (userDao.topUpGiftCard(user, selectedCard, amount))
				System.out.println("Amount added successfully......");

		} else {
			System.out.println("Your balance is insufficient...");
		}
	}

	public void blockGiftCard(User user) {

		List<GiftCard> list = getCardListOf(user, 1);
		Helper.displayList(list, GiftCard.getColumn(), true);
		if (list.isEmpty())
			return;

		System.out.println("Enter the option: ");
		int option = getValidChoice(1, list.size());

		userDao.blockGiftCard(list.get(option - 1));

	}

	public List<GiftCard> getCardListOf(User user, Integer status) {

		return userDao.getGiftCardList(user, status);

	}

}
