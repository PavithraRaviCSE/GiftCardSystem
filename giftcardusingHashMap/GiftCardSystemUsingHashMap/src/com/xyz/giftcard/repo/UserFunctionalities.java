package com.xyz.giftcard.repo;

import java.util.List;

import com.xyz.giftcard.entity.GiftCard;
import com.xyz.giftcard.entity.User;

public interface UserFunctionalities {

	User userLogin(String name, String password);

	GiftCard generateGiftCard(User user, double amount);

	List<GiftCard> getAvailableCardListOf(User user, int status);

}
