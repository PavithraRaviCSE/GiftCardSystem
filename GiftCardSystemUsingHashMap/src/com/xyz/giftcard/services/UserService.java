package com.xyz.giftcard.services;

import java.util.List;
import java.util.Scanner;

import com.xyz.giftcard.entity.GiftCard;
import com.xyz.giftcard.entity.User;
import com.xyz.giftcard.repo.UserRepository;

public class UserService implements UserServiceInterface {

	private static Scanner input = new Scanner(System.in);
	private UserRepository userRepo = new UserRepository();

	public void userFunctionalities() {

		boolean isUser = true;

		while (isUser) {

			System.out.println("Enter account Number: ");
			String accountNumber = input.next();
			System.out.println("Enter password: ");
			String password = input.next();

			User user = userLogin(accountNumber, password);

			if (user == null) {

				System.out.println("login failed........\n enter correct username and password \n");
				break;

			}

			System.out.println("Logged in successfully......");
			userMenu(user);
			break;

		}

	}

	public void userMenu(User user) {

		boolean isLoggedIn = true;

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
				viewCards(getCardListOf(user));
				break;
			case 4:
				topUp(user);
				break;
			case 5:
				viewCardTransaction(user);
				break;
			case 6:
				blockGiftCard(getAvailableCardListOf(user, 1));
				break;
			case 7:
				changePassword(user);
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

	private int getValidChoice(int min, int max) {
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

	public User userLogin(String accountNumber, String password) {
		return userRepo.userLogin(accountNumber, password);
	}

	public void generateGiftCard(User user) {

		System.out.println("Enter the amount: ");
		double amount = input.nextDouble();

		if (user.getBalance() >= amount) {

			GiftCard card = userRepo.generateGiftCard(user, amount);
			viewCards(List.of(card));
			debitAccountFromUser(user, amount);
		} else {
			System.out.println("your Account balance is inSuffient..");
		}

	}

	public void viewCards(List<GiftCard> cardList) {

		if (cardList.isEmpty()) {
			System.out.println("No cards found");
			return;
		}

		int count = 0;
		System.out.println("cardNumber \t\t pin \t\t balance\taccountNumber \t\t status");
		for (GiftCard c : cardList) {
			System.out.println(++count + " " + c.getCardNumber() + " \t\t" + c.getPin() + " \t\t" + c.getBalance()
					+ " \t\t" + c.getAccountNumber() + "\t\t" + c.getStatus());
		}

	}

	public void changePassword(User user) {
		System.out.println("Enter your new Password: ");
		String password = input.next();
		userRepo.changePassword(user, password);
	}

	public void viewCardTransaction(User user) {

		List<GiftCard> cardList = getCardListOf(user);
		if (cardList.isEmpty()) {
			System.out.println("No cards available....");
			return;
		}

		viewCards(cardList);

		System.out.println("Enter the option: ");
		int option = getValidChoice(1, cardList.size());
		userRepo.giftCardTransaction(cardList.get(option - 1));

	}

	public void topUp(User user) {

		List<GiftCard> cardList = getAvailableCardListOf(user, 1);

		if (cardList.isEmpty()) {
			System.out.println("No card found...");
			return;
		}

		viewCards(cardList);

		System.out.println("Enter your option: ");
		int option = getValidChoice(1, cardList.size());
		GiftCard card = cardList.get(option - 1);

		System.out.println("Enter the amount: ");
		double amount = input.nextDouble();

		if (user.getBalance() >= amount) {

			userRepo.debitFromUser(user, amount);
			userRepo.topUpGiftCard(card, amount);
			viewCards(List.of(card));

		} else {

			System.out.println("Your balance is insufficient...");

		}

	}

	private List<GiftCard> getAvailableCardListOf(User user, int status) {

		return userRepo.getAvailableCardListOf(user, status);
	}

	public void blockGiftCard(List<GiftCard> list) {

		if (list.isEmpty()) {
			System.out.println("No card available...");
			return;
		}
		viewCards(list);

		System.out.println("Enter your option: ");
		int option = getValidChoice(1, list.size());
		userRepo.blockGiftCard(list.get(option - 1));
		viewCards(List.of(list.get(option - 1)));

	}

	public void getAccountBalance(User user) {
		System.out.println("Account Balance: " + userRepo.getAccountBalance(user));
	}

	public void debitAccountFromUser(User user, double amount) {
		userRepo.debitFromUser(user, amount);
	}

	public List<GiftCard> getCardListOf(User user) {

		return userRepo.getGiftCardList(user);

	}

}
