package com.example.model;

import java.sql.Timestamp;

public class GiftCardTransactions {

	private long transactionId;
	private long giftCardId;
	private double amountTransferred;
	private double balance;
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

	public GiftCardTransactions() {

	}

	public GiftCardTransactions(long giftcardId, double amountTransferred, double balance, String TransactionType) {
		this.giftCardId = giftcardId;
		this.amountTransferred = amountTransferred;
		this.balance = balance;
		this.transactionType = TransactionType;
	}

	public GiftCardTransactions(long transactionId, long giftcardId, double amountTransferred, double balance,
			String transactionType, Timestamp transactionTiming) {
		this.transactionId = transactionId;
		this.giftCardId = giftcardId;
		this.amountTransferred = amountTransferred;
		this.balance = balance;
		this.transactionType = transactionType;
		this.createdAt = transactionTiming;
	}

	public static String getColumn() {
		return String.format("%-15s %-15s %-20s  %-20s %-15s %-15s", "transactionId", "giftcardId", "amountTransferred",
				"Balance", "transactionType", "Transaction Timing");
	}

	@Override
	public String toString() {
		return String.format("%-15s %-15s %-20.2f  %-20s  %-15s %-15s", transactionId, giftCardId, amountTransferred,
				balance, transactionType, createdAt);
	}

	public double getAmountTransferred() {
		return amountTransferred;
	}

	public void setAmountTransferred(double amountTransferred) {
		this.amountTransferred = amountTransferred;
	}

}
