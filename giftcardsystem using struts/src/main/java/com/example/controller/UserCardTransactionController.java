package com.example.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.dao.UserDAO;
import com.example.model.GiftCardTransactions;
import com.example.service.ThreadLocalClass;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class UserCardTransactionController extends ActionSupport {

	private long id;
	private Map<String, Object> response =  new HashMap<>();

	Map<String, Object> userInfo  = ThreadLocalClass.getUserInfo();
    

	public void setId(long id) {
		this.id = id;
	}
	public Map<String, Object> getResponse() {
		return response;
	}
	
	public String show() {

		try {
			List<GiftCardTransactions> GiftcardTransactionList = UserDAO.getCardTransactionListOf(id);
	
			response.put("GiftcardTransactionList" , GiftcardTransactionList);
			System.out.println("Card ID: " + id);

		} catch (Exception e) {
			response.put("error" , "cannot get transaction");
			
			e.printStackTrace();
		}

		return SUCCESS;
	}
	
	
	
	
}
