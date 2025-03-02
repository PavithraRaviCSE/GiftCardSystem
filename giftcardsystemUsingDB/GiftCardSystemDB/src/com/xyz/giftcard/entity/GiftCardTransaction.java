package com.xyz.giftcard.entity;

import java.sql.Timestamp;

public class GiftCardTransaction {

	private long transactionId;
	private long giftCardId;
	private double balance;
	private int status;
	private String transactionType;
	private Timestamp createdAt;

	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

	public long getGiftCardId() {
		return giftCardId;
	}

	public void setGiftCardId(long giftcardId) {
		this.giftCardId = giftcardId;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public GiftCardTransaction() {

	}

	public GiftCardTransaction(long giftcardId, double balance, int status, Timestamp transactionTiming) {
		this.giftCardId = giftcardId;
		this.balance = balance;
		this.status = status;
		this.createdAt = transactionTiming;
	}

	public GiftCardTransaction(long transactionId, long giftcardId, double balance, int status, String transactionType,
			Timestamp transactionTiming) {
		this.transactionId = transactionId;
		this.giftCardId = giftcardId;
		this.balance = balance;
		this.status = status;
		this.transactionType = transactionType;
		this.createdAt = transactionTiming;
	}

	public static String getColumn() {
		return String.format("%-15s %-15s %-20s %-10s %-15s %-15s", "transactionId", "giftcardId", "Balance", "Status",
				"transactionType", "Transaction Timing");
	}

	@Override
	public String toString() {
		return String.format("%-15s %-15s %-20.2f %-10d %-15s %-15s", transactionId, giftCardId, balance, status,
				transactionType, createdAt);
	}

}
