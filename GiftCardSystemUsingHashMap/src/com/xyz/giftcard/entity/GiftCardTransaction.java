package com.xyz.giftcard.entity;

public class GiftCardTransaction {

	private String cardNumber;
	private double initialBalance;
	private double finalBalance;
	private String cardProvider;
	private float rewardPoints;

	public GiftCardTransaction(String cardNumber, double initialBalance, double finalBalance, String cardProvider,
			float rewardPoints) {
		this.cardNumber = cardNumber;
		this.initialBalance = initialBalance;
		this.finalBalance = finalBalance;
		this.cardProvider = cardProvider;
		this.rewardPoints = rewardPoints;
	}

	// Getters and setters
	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public double getInitialBalance() {
		return initialBalance;
	}

	public void setInitialBalance(double initialBalance) {
		this.initialBalance = initialBalance;
	}

	public double getFinalBalance() {
		return finalBalance;
	}

	public void setFinalBalance(double finalBalance) {
		this.finalBalance = finalBalance;
	}

	public String getAccountNumber() {
		return cardProvider;
	}

	public void setAccountNumber(String accountNumber) {
		this.cardProvider = accountNumber;
	}

	public float getRewardPoints() {
		return rewardPoints;
	}

	public void setRewardPoints(float rewardPoints) {
		this.rewardPoints = rewardPoints;
	}

	@Override
	public String toString() {
		return "GiftCardTransaction [cardNumber=" + ", initialBalance=" + initialBalance + ", finalBalance="
				+ finalBalance + ", cardProvider=" + cardProvider + ", rewardPoints=" + rewardPoints + "]";
	}
}
