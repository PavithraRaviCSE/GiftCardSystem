package com.example.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.dao.PurchaseDAO;
import com.example.dao.UserDAO;
import com.example.model.*;
import com.example.service.ThreadLocalClass;
import com.opensymphony.xwork2.ActionSupport;

public class PurchaseDataController extends ActionSupport {

	private Map<String, Object> response = new HashMap<>();

	public Map<String, Object> getResponse() {
		return response;
	}

	Map<String, Object> userInfo = ThreadLocalClass.getUserInfo();

	private long id;
	public void setId(long id) {
		this.id = id;
	}

	public String index() {

		if (userInfo != null && userInfo.containsKey("userId")) {
			List<Purchase> purchaseList = PurchaseDAO.getPurchasesByUserId((long) userInfo.get("userId"));
			response.put("purchaseList", purchaseList);
		}

		return SUCCESS;
	}

	public String show() {
		
		

		if (userInfo != null && userInfo.containsKey("userId")) {
			System.out.println("show function" + id);
			List<Purchase> purchaseList = PurchaseDAO.getPurchasesByGiftCard((long) userInfo.get("userId"), id);
			response.put("purchaseList", purchaseList);
		}

		return SUCCESS;
	}
	
}
