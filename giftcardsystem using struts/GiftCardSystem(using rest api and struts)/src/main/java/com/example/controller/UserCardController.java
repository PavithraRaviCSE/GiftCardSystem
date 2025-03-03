package com.example.controller;

import com.example.dao.UserDAO;
import com.example.model.GiftCard;
import com.example.service.RedisService;
import com.example.service.ThreadLocalClass;
import com.example.service.UserException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserCardController extends ActionSupport {

	private long id;
	private long pin;
	private double amount;
	private Integer status;
	private final RedisService redisService = new RedisService();
	private Map<String, Object> response = new HashMap<>();

	public void setId(long id) {
		this.id = id;
	}

	public void setStatus(Integer status) {
		this.status  = (status == null )? null : status;
	}
	public void setPin(long pin) {
		this.pin = pin;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Map<String, Object> getResponse() {
		return response;
	}

	Map<String, Object> userInfo = ThreadLocalClass.getUserInfo();

	public String index() { // View all cards
		try {
			if (userInfo != null && userInfo.containsKey("userId")) {
				getGiftCardList((Long) userInfo.get("userId"), null);
			} else {
				System.out.println("User ID is null");
			}
		} catch (Exception e) {
			response.put("error", "Error occurred: " + e.getMessage());
		}
		return SUCCESS;
	}

	private void getGiftCardList(long userId, Integer status) {
		System.out.println("userId: " + userId + " status: "+ status);
		
		List<GiftCard> cachedList = RedisService.getGiftCardsByStatus(userId, status);
		System.out.println("" + cachedList );
		if (cachedList != null  ) {
			System.out.println("Cached cards: " + cachedList);
			response.put("giftCardList", cachedList);
			return;
		}

		try {
			List<GiftCard> giftCardList = UserDAO.getGiftCardList(userId, null);
			RedisService.saveGiftCardToCache(userId, giftCardList);
			cachedList = RedisService.getGiftCardsByStatus(userId, status);
			response.put("giftCardList", cachedList);
		} catch (Exception e) {
			response.put("error", "Failed to get gift card: " + e.getMessage());
		} finally {
			ThreadLocalClass.removeUserInfo(); // Clean up the thread to avoid memory leaks
		}
	}

	public String show() { // Get the card with status
		
		
		status = (int) id;
		
		System.out.println("show card: " + status);
		getGiftCardList((Long) userInfo.get("userId"), status);
		return SUCCESS;
	}

	public String create() {
		try {
			if (pin < 1000 || pin > 9999) {
				System.out.println("pin: " + pin);
				response.put("error", "Pin number should be a 4-digit number");
				return SUCCESS;
			}
			GiftCard card = UserDAO.createGiftCard((Long) userInfo.get("userId"), amount, pin);
			response.put("cardNumber", card.getGiftCardNumber());
			clearCache((Long) userInfo.get("userId"));
		} catch (SQLException | UserException e) {
			response.put("error", "" + e.getMessage());
		}
		finally {
			ThreadLocalClass.removeUserInfo(); 
		}
		return SUCCESS;
	}

	public String destroy() {
		if (id != 0) {
			try {
				boolean isBlocked = UserDAO.blockCard(id, (Long) userInfo.get("userId"));
				if (isBlocked) {
					response.put("success", "Card blocked successfully");
					clearCache((Long) userInfo.get("userId"));
				} else {
					response.put("error", "Cannot block");
				}
			} catch (Exception e) {
				response.put("error", "Failed: " + e.getMessage());
			}finally {
				ThreadLocalClass.removeUserInfo(); 
			}
		} else {
			response.put("error", "Card number is null");
		}
		return SUCCESS;
	}

	public String update() { // Top-up card
		if (id != 0 && amount != 0) {
			try {
				boolean isTopUped = UserDAO.topUpCard(id, amount, (Long) userInfo.get("userId"));
				if (isTopUped) {
					response.put("success", "Top-up successful");
					clearCache((Long) userInfo.get("userId"));
				} else {
					response.put("error", "Failed to top-up");
				}
			} catch (Exception e) {
				response.put("error", "Failed to top-up: " + e.getMessage());
			}
			finally {
				ThreadLocalClass.removeUserInfo(); 
			}
		} else {
			response.put("error", "ID or amount is null");
		}
		return SUCCESS;
	}

	private void clearCache(long userId) {
		RedisService.removeFromCache("giftCardList:" + userId);
		RedisService.removeFromCache("giftCardListWithStatus*:" + userId);
	}

	public String viewTransaction() {
		response.put("error", "ID or amount is null");
		return SUCCESS;
	}
}
