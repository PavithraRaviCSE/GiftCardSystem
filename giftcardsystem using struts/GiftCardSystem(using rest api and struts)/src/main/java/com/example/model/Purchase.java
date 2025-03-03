package com.example.model;

import java.sql.Timestamp;

public class Purchase {
    private Long purchaseId;
    private Long giftcardId;
    private Long transactionId;
    private String purchaseCategory;
    private Double amountSpent;
    private Timestamp purchaseDate;

    // Constructors
    public Purchase() {}

    public Purchase(Long id, Long giftcardId, Long transactionId, String purchaseCategory, Double amountSpent, Timestamp purchaseDate) {
        this.purchaseId = id;
        this.giftcardId = giftcardId;
        this.transactionId = transactionId;
        this.purchaseCategory = purchaseCategory;
        this.amountSpent = amountSpent;
        this.purchaseDate = purchaseDate;
    }

    // Getters and Setters
    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long id) {
        this.purchaseId = id;
    }

    public Long getGiftcardId() {
        return giftcardId;
    }

    public void setGiftcardId(Long giftcardId) {
        this.giftcardId = giftcardId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getPurchaseCategory() {
        return purchaseCategory;
    }

    public void setPurchaseCategory(String purchaseCategory) {
        this.purchaseCategory = purchaseCategory;
    }

    public Double getAmountSpent() {
        return amountSpent;
    }

    public void setAmountSpent(Double amountSpent) {
        this.amountSpent = amountSpent;
    }

    public Timestamp getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Timestamp purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    // toString Method
    @Override
    public String toString() {
        return "Purchase{" +
                "id=" + purchaseId +
                ", giftcardId=" + giftcardId +
                ", transactionId=" + transactionId +
                ", purchaseCategory='" + purchaseCategory + '\'' +
                ", amountSpent=" + amountSpent +
                ", purchaseDate=" + purchaseDate +
                '}';
    }
}
