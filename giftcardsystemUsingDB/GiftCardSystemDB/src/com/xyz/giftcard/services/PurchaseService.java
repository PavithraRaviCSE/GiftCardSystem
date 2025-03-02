package com.xyz.giftcard.services;

import java.util.Scanner;

import com.xyz.giftcard.dao.UserDAO;
import com.xyz.giftcard.entity.GiftCard;

public class PurchaseService {
	private Scanner input = new Scanner(System.in);
	private static UserDAO userdb = new UserDAO();

	public void purchase() {
		System.out.print("Enter 5 digit card number: ");
		String cardNumber = input.next();
		GiftCard card = userdb.getCardData(cardNumber);

		if (card == null) {
			System.out.println("No card found...");
			return;
		}
		if (card.getStatus() == 0) {
			System.out.println("Gift card expired...");
			return;
		}

		System.out.print("Enter your pin: ");
		int pin = input.nextInt();

		if (card.getPin() != pin) {
			System.out.println("Incorrect pin.");
			return;
		}

		System.out.print("Enter the purchase amount: ");
		double amount = input.nextDouble();
		double balance = card.getBalance();

		if (balance < amount) {
			System.out.println("Your balance is insufficient.");

			System.out.println("Balance: " + balance);

			return;
		}

		userdb.debitFromCard(card, amount);

	}

}
