package com.example.controller;

import java.util.HashMap;
import java.util.Map;

import com.example.dao.UserDAO;
import com.example.service.RedisService;
import com.example.service.UserException;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

public class PurchaseController extends ActionSupport {

	private long cardNumber;
	private long pin;
	private double amount;
	private String category;

	public String getCategory() {
	    return category;
	}

	public void setCategory(String category) {
	    this.category = category;
	}
	private Map<String, Object> response = new HashMap<>();

	public void setCardNumber(long cardNumber) {
		this.cardNumber = cardNumber;
	}

	public void setPin(long pin) {
		this.pin = pin;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String execute() { // purchase
		try {
			System.out.println("cardNumber, pin, amount: " + cardNumber + " " + pin + " " + amount + " " + category);

			Map<String, String> errors = new HashMap<>();

			if (cardNumber == 0)
				errors.put("cardNumber", "Card number is required.");
			if (pin == 0)
				errors.put("pin", "PIN is required.");
			if (amount <= 0)
				errors.put("amount", "Amount must be greater than zero.");
			if(category == null)
				errors.put("category", "category is null.");
			if (!errors.isEmpty()) {
				response.put("error", errors);
				return SUCCESS;
			}

			Long userId = UserDAO.purchase(cardNumber, pin, amount, category);

			if (userId != null) {
				response.put("success", "Payment successful.");
				RedisService.removeUserData(userId);
			} else {
				response.put("error", "Payment process failed. try again later");
			}

		} catch (UserException e) {
			System.err.println("UserException: " + e.getMessage());
			response.put("error", e.getMessage());
		} catch (Exception e) {
			System.err.println("Unexpected Error: " + e.getMessage());
			response.put("error", "An unexpected error occurred. try again later");
		}

		return "success";
	}

	public Map<String, Object> getResponse() {
		return response;
	}

}
