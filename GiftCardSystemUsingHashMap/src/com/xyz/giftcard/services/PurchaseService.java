package com.xyz.giftcard.services;

import java.util.Scanner;

import com.xyz.giftcard.entity.GiftCard;
import com.xyz.giftcard.repo.UserRepository;

public class PurchaseService {
	private UserRepository userRepo = new UserRepository();
	private Scanner input = new Scanner(System.in);

	public void purchase() {
		System.out.print("Enter your card number: ");
		String cardNumber = input.next();
		GiftCard card = userRepo.getCardDataOf(cardNumber);

		if (card == null) {
			System.out.println("No card found...");
			return;
		}
		if (card.getStatus() == 2) {
			System.out.println("Gift card expired...");
			return;
		}

		System.out.print("Enter your pin: ");
		String pin = input.next();

		if (!card.getPin().equalsIgnoreCase(pin)) {
			System.out.println("Incorrect pin.");
			return;
		}

		System.out.print("Enter the purchase amount: ");
		double amount = input.nextDouble();
		double balance = card.getBalance();

		if (balance < amount) {
			System.out.println("Your balance is insufficient.");
			return;
		}

		userRepo.debitAmountFromCard(card, amount);

		input.close();
	}

}
