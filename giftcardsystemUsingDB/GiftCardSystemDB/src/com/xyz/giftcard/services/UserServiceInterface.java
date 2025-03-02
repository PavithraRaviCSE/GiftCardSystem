package com.xyz.giftcard.services;

import java.util.List;

import com.xyz.giftcard.entity.GiftCard;
import com.xyz.giftcard.entity.User;

public interface UserServiceInterface {

	void userFunctionalities();

	void userMenu(User user);

	void generateGiftCard(User user);

	void viewCards(List<GiftCard> cardList);

	void changePassword(User user, boolean isTrue);

	void viewCardTransaction(User user);

	void topUp(User user);

	void blockGiftCard(User user);

}
