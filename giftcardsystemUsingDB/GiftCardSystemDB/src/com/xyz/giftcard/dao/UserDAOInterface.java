package com.xyz.giftcard.dao;

import java.util.List;

import com.xyz.giftcard.entity.GiftCard;
import com.xyz.giftcard.entity.User;

public interface UserDAOInterface {

	User userLogin(String accountNumber, String password);

	List<GiftCard> getAllCard(User user);

	List<GiftCard> getAllCardOf(User user, int status);

	GiftCard createGiftCard(User user, double amount);

	boolean debitFromUser(User user, double amount);

}
