package com.xyz.giftcard.services;

import java.util.List;

import com.xyz.giftcard.entity.GiftCard;
import com.xyz.giftcard.entity.User;

public interface UserServiceInterface {
	
	User userLogin(String accountNumber, String password);

	void userFunctionalities();

	void userMenu(User user);

	void generateGiftCard(User user);

	void viewCards(List<GiftCard> cardList);

	void changePassword(User user);

	void viewCardTransaction(User user);

	void topUp(User user);

	void blockGiftCard(List<GiftCard> list);

	void getAccountBalance(User user);

	void debitAccountFromUser(User user, double amount);

	List<GiftCard> getCardListOf(User user);
}
