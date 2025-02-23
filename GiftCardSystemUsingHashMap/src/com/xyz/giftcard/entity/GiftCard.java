package com.xyz.giftcard.entity;

public class GiftCard {

	private String cardNumber;
	private String pin;
	private String cardProvider;
	private double balance;
	private int status;
	private float rewardPoints;

	public GiftCard(String cardNumber, String pin, String cardProvider, double balance) {
		super();
		this.cardNumber = cardNumber;
		this.pin = pin;
		this.cardProvider = cardProvider;
		this.balance = balance;
		this.status = 1;
		this.rewardPoints = 0;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getAccountNumber() {
		return cardProvider;
	}

	public void setAccountNumber(String accountNumber) {
		this.cardProvider = accountNumber;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public float getRewardPoints() {
		return rewardPoints;
	}

	public void setRewardPoints(float rewardPoints) {
		this.rewardPoints = rewardPoints;
	}

	@Override
	public String toString() {
		return "GiftCard [cardNumber=" + cardNumber + ", pin=" + pin + ", cardProvider=" + cardProvider + ", balance="
				+ balance + ", status=" + status + ", rewardPoints=" + rewardPoints + "]";
	}

}
