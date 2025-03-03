package com.example.model;
import java.sql.Timestamp;

public class GiftCard {

	private long giftCardId;
	private long giftCardNumber;
	private long pin;
	private long userId;
	private double balance;
	private int status;
	private float rewardPoints;
	private Timestamp createdAt;

	public GiftCard() {

	}

	public GiftCard(long giftCardNumber, int pin, long userId, double balance) {
		super();
		this.giftCardNumber = giftCardNumber;
		this.pin = pin;
		this.userId = userId;
		this.balance = balance;
		this.status = 1;
		this.rewardPoints = 0;
	}

	public GiftCard(long giftCardId, long giftcardNumber, long pin, long userId, double balance, int status,
			float rewardPoints, Timestamp createdAt) {
		super();
		this.giftCardId = giftCardId;
		this.giftCardNumber = giftcardNumber;
		this.pin = pin;
		this.userId = userId;
		this.balance = balance;
		this.status = status;
		this.rewardPoints = rewardPoints;
		this.createdAt = createdAt;

	}

	public long getGiftCardNumber() {
		return giftCardNumber;
	}

	public void setGiftCardNumber(long giftCardNumber) {
		this.giftCardNumber = giftCardNumber;
	}

	public long getPin() {
		return pin;
	}

	public void setPin(long pin) {
		this.pin = pin;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
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

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return String.format("%-15s %-15s %-15s %-20s %-10s %-10s %-15s %-20s", giftCardId, giftCardNumber, pin, userId,
				balance, status, rewardPoints, createdAt);
	}

	public static String getColumn() {
		return String.format("%-15s %-15s %-15s %-20s %-10s %-10s %-15s %-20s", "giftCardId", "cardNumber", "pin",
				"userId", "balance", "status", "rewardPoints", "creationTime");
	}

	public long getGiftCardId() {
		return giftCardId;
	}

	public void setGiftCardId(long giftCardId) {
		this.giftCardId = giftCardId;
	}

}
