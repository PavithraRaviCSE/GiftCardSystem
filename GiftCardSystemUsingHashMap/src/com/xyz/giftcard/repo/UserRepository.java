package com.xyz.giftcard.repo;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;
//import java.util.Map;

import com.xyz.giftcard.entity.*;
import com.xyz.giftcard.helper.*;

public class UserRepository implements UserFunctionalities, AdminFunctionalities {

	private static List<Admin> adminList = new ArrayList<>();
	private static List<User> userList = new ArrayList<>();
	private static List<GiftCard> giftCardList = new ArrayList<>();
	private static List<GiftCardTransaction> giftCardTransactionList = new ArrayList<>();

	static {
		Admin admin1 = new Admin("admin", "admin");
		adminList.add(admin1);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate date = LocalDate.parse("03/10/2003", formatter);

		User user1 = new User(Helper.generateUserId(), Helper.generateAccountNumber(), "user1", date, 1000, "vtfs2",
				"12345");

		userList.add(user1);
	}

	public Admin adminLogin(String userName, String password) {

		for (Admin admin : adminList) {
			if (admin.getUserName().equals(userName) && admin.getPassword().equals(password))
				return admin;

		}

		return null;
	}

	public void searchUserByAccountNumber(String accountNumber) {
		for (User c : userList) {
			if (c.getAccountNumber().toLowerCase().contains(accountNumber.toLowerCase())) {
				System.out.println(c.getUserData());
				return;
			}
		}

		System.out.println("no match found....");
	}

	public void searchUserByName(String userdata) {

		List<String> list = new ArrayList<>();
		for (User c : userList) {
			if (c.getName().toLowerCase().contains(userdata.toLowerCase()))
				list.add(c.getUserData());
		}

		if (list.isEmpty())
			System.out.println("No user found...");
		else {
			for (String s : list) {
				System.out.println(s);
			}
		}
	}

	public void changePassword(User user, String password) {
		user.setPassword(Helper.encryption(password));
		System.out.println("Password changed successfully");

	}

	public User userLogin(String accountNumber, String password) {
		String pass = Helper.encryption(password);

		for (User c : userList) {
			if (c.getAccountNumber().equalsIgnoreCase(accountNumber) && c.getPassword().equals(pass))
				return c;
		}

		return null;
	}

	public void addUser(String userName, String dob, double amount, String mobile) {

		int custId = Helper.generateUserId();
		String accountNumber = Helper.generateAccountNumber();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate date = LocalDate.parse(dob, formatter);
		String password = Helper.generatePassword(date);

		User newUser = new User(custId, accountNumber, userName, date, amount, Helper.encryption(password), mobile);

		if (userList.add(newUser)) {
			System.out.println("user added syccessfully......");
			System.out.println("Use these fields to Login:");
			System.out.println("Account number: " + accountNumber + " Password: " + password);

		} else {
			System.out.println("The user is already having an account");
		}
	}

	public void blockGiftCard(GiftCard card) {
		card.setStatus(2);

		credit(card.getBalance(), card.getAccountNumber());

		GiftCardTransaction cardTransaction = new GiftCardTransaction(card.getCardNumber(), card.getBalance(), 0,
				card.getAccountNumber(), card.getRewardPoints());
		giftCardTransactionList.add(cardTransaction);
		card.setBalance(0);

	}

	public double getGiftCardBalance(String cardNumber) {

		for (GiftCard card : giftCardList) {
			if (card.getCardNumber().equals(cardNumber)) {
				return card.getBalance();
			}
		}

		return 0;
	}

	public boolean isValidCardandPin(String cardNumber, String pin) {
		for (GiftCard card : giftCardList) {
			if (card.getCardNumber().equals(cardNumber) && card.getPin().equals(pin)) {
				return true;
			}
		}

		return false;
	}

	public void viewUsers() {

		System.out.println("id \t accountNumber \t\t name \t\t dob \t\t balance\t password \t mobile");

		for (User c : userList) {
			System.out.println(c.getId() + "\t" + c.getAccountNumber() + "\t\t\t" + c.getName() + "\t\t" + c.getDob()
					+ "\t" + c.getBalance() + "\t\t" + c.getPassword() + "\t\t" + c.getMobile());
		}
	}

	public void credit(double amount, String accountNumber) {

		for (User c : userList) {
			if (c.getAccountNumber().equalsIgnoreCase(accountNumber)) {
				double newAmount = c.getBalance() + amount;
				c.setBalance(newAmount);

			}
		}

	}

	public boolean isMobileNumberExist(String mobile) {

		for (User c : userList) {
			if (c.getMobile().equals(mobile)) {
				return true;
			}
		}

		return false;

	}

	public boolean isAccountNumberExist(String accountNumber) {

		for (User c : userList) {
			if (c.getAccountNumber().equalsIgnoreCase(accountNumber))
				return true;
		}
		return false;
	}

	public User getUser(String accountNumber) {

		for (User c : userList) {
			if (c.getAccountNumber().equalsIgnoreCase(accountNumber)) {
				return c;
			}
		}

		return null;

	}

	public void debitFromUser(User user, double amount) {

		user.setBalance(user.getBalance() - amount);

	}

	public GiftCard generateGiftCard(User user, double amount) {

		String cardNumber;
		boolean isUnique;

		do {
			cardNumber = Helper.generateCardNumber();
			isUnique = true;

			for (GiftCard card : giftCardList) {
				if (card.getCardNumber().equals(cardNumber)) {
					isUnique = false;
					break;
				}
			}

		} while (!isUnique);

		String pin = Helper.generatePin();
		GiftCard newCard = new GiftCard(cardNumber, pin, user.getAccountNumber(), amount);
		giftCardList.add(newCard);
		GiftCardTransaction cardTransaction = new GiftCardTransaction(newCard.getCardNumber(), 0, newCard.getBalance(),
				newCard.getAccountNumber(), newCard.getRewardPoints());
		giftCardTransactionList.add(cardTransaction);
		return newCard;

	}

	public void giftCardTransaction(GiftCard card) {

		System.out.println("cardNumber\tinitialBalance \tfinalBalance \tcardProvider \trewardPoints");
		for (GiftCardTransaction c : giftCardTransactionList) {
			if (c.getCardNumber().equals(card.getCardNumber())) {
				System.out.println(c.getCardNumber() + " \t\t" + c.getInitialBalance() + "\t\t" + c.getFinalBalance()
						+ "\t\t" + c.getAccountNumber() + "\t\t" + c.getRewardPoints());
			}
		}

	}

	public void topUpGiftCard(GiftCard card, double amount) {

		amount += card.getBalance();

		GiftCardTransaction cardTransaction = new GiftCardTransaction(card.getCardNumber(), card.getBalance(), amount,
				card.getAccountNumber(), card.getRewardPoints());
		card.setBalance(amount);
		giftCardTransactionList.add(cardTransaction);
	}

	public GiftCard getCardDataOf(String cardNumber) {
		for (GiftCard c : giftCardList) {
			if (c.getCardNumber().equals(cardNumber))
				return c;
		}

		return null;
	}

	public List<GiftCard> getGiftCardList(User user) {

		String useracc = user.getAccountNumber();

		List<GiftCard> card = new ArrayList<>();
		for (GiftCard c : giftCardList) {
			if (c.getAccountNumber().equalsIgnoreCase(useracc)) {
				card.add(c);
			}
		}

		return card;
	}

	public double getAccountBalance(User user) {

		return user.getBalance();

	}

	public void debitAmountFromCard(GiftCard card, double amount) {

		float points = card.getRewardPoints();

		float newPoints = (float) (amount / 100);

		points += newPoints;

		double newBalance = card.getBalance() - amount;

		if (points >= 10) {
			newBalance += points;
			points = 0;
		}

		GiftCardTransaction cardTransaction = new GiftCardTransaction(card.getCardNumber(), card.getBalance(),
				newBalance, card.getAccountNumber(), card.getRewardPoints() + 1);
		card.setBalance(newBalance);
		giftCardTransactionList.add(cardTransaction);
		System.out.println("Amount transferred successfully.....");
		System.out.println("Balance amount: " + card.getBalance());

	}

	public List<GiftCard> getAvailableCardListOf(User user) {

		List<GiftCard> cardList = new ArrayList<>();
		for (GiftCard c : giftCardList) {
			if (c.getAccountNumber().equalsIgnoreCase(user.getAccountNumber())) {
				cardList.add(c);
			}
		}
		return cardList;
	}

	public List<GiftCard> getAvailableCardListOf(User user, int status) {

		List<GiftCard> cardList = new ArrayList<>();
		for (GiftCard c : giftCardList) {
			if (c.getAccountNumber().equalsIgnoreCase(user.getAccountNumber()) && c.getStatus() == status) {
				cardList.add(c);
			}
		}

		return cardList;
	}

}
