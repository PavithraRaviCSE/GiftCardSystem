package com.xyz.giftcard.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.xyz.giftcard.entity.GiftCard;
import com.xyz.giftcard.entity.GiftCardTransaction;
import com.xyz.giftcard.entity.User;
import com.xyz.giftcard.helper.Helper;

public class UserDAO implements UserDAOInterface {

	private static PreparedStatement pst;
	private Connection conn;
	Scanner input = new Scanner(System.in);

	public UserDAO() {
		try {
			conn = DatabaseConnection.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public User userLogin(String accountNumber, String password) {

		String query = "SELECT * FROM user where account_number = '" + accountNumber + "' and password  = '" + password
				+ "' ;";
		try {
			pst = conn.prepareStatement(query);

			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				User user = new User(rs.getInt("user_id"), rs.getString("account_number"), rs.getString("name"),
						rs.getDate("dob").toLocalDate(), rs.getDouble("balance"), rs.getString("password"),
						rs.getString("mobile"), rs.getTimestamp("created_at"), rs.getBoolean("is_first_login"));

				return user;

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public int generateCardNumber(String cardNumber) {
		Long value = fetchValue(cardNumber);
		if (value != null) {
			return Integer.parseInt(String.format("%05d", value)); // Corrected format specifier
		}
		return 0;
	}

	@Override
	public boolean debitFromUser(User user, double amount) {
		if (user.getBalance() >= amount) {
			double newBalance = user.getBalance() - amount;
			String query = "UPDATE user SET balance = ? WHERE account_number = ?";

			try (PreparedStatement pst = conn.prepareStatement(query)) {
				pst.setDouble(1, newBalance);
				pst.setString(2, user.getAccountNumber());
				pst.executeUpdate();

				user.setBalance(newBalance);

				return true;
//				System.out.println("Amount debited successfully. New balance: " + newBalance);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Insufficient balance to complete the transaction.");
		}
		return false;
	}

	public GiftCard createGiftCard(User user, double amount) {

		try {
			conn.setAutoCommit(false);
			if (!debitFromUser(user, amount)) {
				return null;
			}

			int cardNumber = generateCardNumber("card_number");
			System.out.println("Enter 4 digit pin: ");
			int pin = input.nextInt();

			String query = "INSERT INTO giftcard (card_number, pin,  balance, user_id) VALUES (?, ?,  ?, ?)";

			try (PreparedStatement pst = conn.prepareStatement(query)) {
				pst.setInt(1, cardNumber);
				pst.setInt(2, pin);
				pst.setDouble(3, amount);
				pst.setLong(4, user.getUserId());

				int rows = pst.executeUpdate();
				if (rows > 0) {
					conn.commit();
					GiftCard card = new GiftCard(cardNumber, pin, user.getUserId(), amount);

					addGiftCardTransaction(cardNumber, amount, "credit");
					return card;
				} else {
					conn.rollback();
					System.out.println("Gift card creation failed.");
				}
			} catch (SQLException e) {
				conn.rollback();
				e.printStackTrace();
				System.out.println("Error during gift card creation. Transaction rolled back.");
			}
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException rollbackEx) {
				rollbackEx.printStackTrace();
			}
			e.printStackTrace();
			System.out.println("Error during debit operation or overall transaction. Rollback performed.");
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private void addGiftCardTransaction(long cardNumber, double balance, String transactionType) {
		String fetchGiftCardIdQuery = "SELECT giftcard_id FROM giftcard WHERE card_number = ?";
		String insertTransactionQuery = "INSERT INTO giftcardTransactions (giftcard_id, balance, transaction_type) VALUES (?, ?, ?)";

		try (PreparedStatement fetchPst = conn.prepareStatement(fetchGiftCardIdQuery)) {
			fetchPst.setLong(1, cardNumber);

			ResultSet rs = fetchPst.executeQuery();
			if (rs.next()) {
				long giftCardId = rs.getLong("giftcard_id"); // Retrieve the giftcard_id

				try (PreparedStatement insertPst = conn.prepareStatement(insertTransactionQuery)) {
					insertPst.setLong(1, giftCardId);
					insertPst.setDouble(2, balance);
					insertPst.setString(3, transactionType);

					int rowsAffected = insertPst.executeUpdate();
					if (rowsAffected > 0) {
						System.out.println("Transaction added successfully for giftcard ID: " + giftCardId);
					} else {
						System.out.println("Failed to add transaction.");
					}
				}
			} else {
				System.out.println("Gift card not found for card number: " + cardNumber);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public GiftCard getCardData(String cardNumber) {

		String query = "Select * from GiftCard where card_number = " + cardNumber + " ;";

		try {
			pst = conn.prepareStatement(query);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				GiftCard card = new GiftCard(rs.getLong("giftcard_id"), rs.getInt("card_Number"), rs.getInt("pin"),
						rs.getLong("user_id"), rs.getDouble("balance"), rs.getInt("status"),
						rs.getFloat("reward_points"), rs.getTimestamp("created_at"));

				return card;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public List<GiftCard> getAllCard(User user) {

		List<GiftCard> giftCards = new ArrayList<>();
		String query = "SELECT * FROM gift_cards WHERE cardProvider = ?";

		try (PreparedStatement pst = conn.prepareStatement(query)) {
			pst.setString(1, user.getAccountNumber());
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				long cardId = rs.getLong("giftcard_id");
				int cardNumber = rs.getInt("card_number");
				int pin = rs.getInt("pin");
				long userId = rs.getLong("user_id");
				double balance = rs.getDouble("balance");
				int status = rs.getInt("status");
				float reward = rs.getFloat("reward_points");
				Timestamp ts = rs.getTimestamp("creation_timing");
				GiftCard giftCard = new GiftCard(cardId, cardNumber, pin, userId, balance, status, reward, ts);
				giftCards.add(giftCard);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return giftCards;

	}


	public void changePassword(User user, String password, boolean isFirstUser) {
		String query = "UPDATE user SET password = ?";

		if (isFirstUser) {
			query += " ,  is_first_login = false";
		}

		query += " WHERE user_id = ?;";
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			pst.setString(1, password);
			pst.setLong(2, user.getUserId());
			int rowsUpdated = pst.executeUpdate();
			if (rowsUpdated > 0) {
				System.out.println("Password updated successfully.");
			} else {
				System.out.println("Failed to update password. User not found.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private long fetchValue(String name) {

		String query = "SELECT value FROM idgenerator WHERE name = ?";
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			pst.setString(1, name);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				long id = rs.getLong("value");
				if (id == 0) {
					String updateQuery = "UPDATE idgenerator SET value = ? WHERE name = ?";
					try (PreparedStatement pt = conn.prepareStatement(updateQuery)) {
						long defaultValue = name.equals("account_number") ? 1002
								: name.equals("card_number") ? 10002 : 0;
						pt.setLong(1, defaultValue);
						pt.setString(2, name);
						pt.executeUpdate();
						return defaultValue - 1;
					}
				} else {
					String updateQuery = "UPDATE idgenerator SET value = value + 1 WHERE name = ?";
					try (PreparedStatement pt = conn.prepareStatement(updateQuery)) {
						pt.setString(1, name);
						if (pt.executeUpdate() > 0) {
							return id;
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0L;
	}

	public List<GiftCardTransaction> getTransactionHistory(GiftCard giftCard) {
		List<GiftCardTransaction> transactionList = new ArrayList<>();
		String query = "SELECT * FROM giftcardTransactions WHERE giftcard_id = ?";

		try (PreparedStatement pst = conn.prepareStatement(query)) {
			pst.setLong(1, giftCard.getGiftCardId());
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				long transactionId = rs.getLong("transaction_id");
				double balance = rs.getDouble("balance");
				Timestamp transactionTime = rs.getTimestamp("created_at");
				String transactionType = rs.getString("transaction_type");
				int status = rs.getInt("status");
				transactionList.add(new GiftCardTransaction(transactionId, giftCard.getGiftCardId(), balance, status,
						transactionType, transactionTime));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("An error occurred while fetching transaction history.");
		}

		return transactionList;
	}

//	public void printTransactionHistory(List<GiftCardTransaction> transactionList) {
//		if (transactionList.isEmpty()) {
//			System.out.println("No transactions found for this gift card.");
//			return;
//		}
//
//		System.out.println(GiftCardTransaction.getColumn());
//		System.out.println("---------------------------------------------------------------------------------------");
//
//		for (GiftCardTransaction transaction : transactionList) {
//			System.out.println(transaction);
//		}
//	}

	public boolean topUpGiftCard(User user, GiftCard card, double amount) {
		try {
			conn.setAutoCommit(false);

			String debitUserQuery = "UPDATE user SET balance = balance - ? WHERE user_id = ?";
			try (PreparedStatement debitPst = conn.prepareStatement(debitUserQuery)) {
				debitPst.setDouble(1, amount);
				debitPst.setLong(2, user.getUserId());
				debitPst.executeUpdate();
			}

			double initialBalance = card.getBalance();
			String updateCardQuery = "UPDATE giftcard SET balance = balance + ? WHERE giftcard_id = ?";
			try (PreparedStatement cardPst = conn.prepareStatement(updateCardQuery)) {
				cardPst.setDouble(1, amount);
				cardPst.setLong(2, card.getGiftCardId());
				int cardRowsUpdated = cardPst.executeUpdate();

				if (cardRowsUpdated > 0) {
					double finalBalance = initialBalance + amount;

					String transactionQuery = "INSERT INTO giftcardTransactions (giftcard_id, balance, transaction_type) VALUES (?, ?, ?)";
					try (PreparedStatement transactionPst = conn.prepareStatement(transactionQuery)) {
						transactionPst.setLong(1, card.getGiftCardId());
						transactionPst.setDouble(2, finalBalance);
						transactionPst.setString(3, "credit");
						transactionPst.executeUpdate();
						card.setBalance(finalBalance);

						updateUserOBJ(user);
						conn.commit();
						System.out.println("Gift card topped up successfully.");
						return true;
					}
				} else {
					System.out.println("Failed to update gift card balance.");
					conn.rollback();
				}
			}
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException rollbackEx) {
				System.err.println("Rollback failed: " + rollbackEx.getMessage());
			}
			e.printStackTrace();
			System.out.println("An error occurred while topping up the gift card.");
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return false;
	}

	void updateUserOBJ(User user) {
		String query = "SELECT balance FROM user WHERE user_id = ?";

		try (PreparedStatement pst = conn.prepareStatement(query)) {

			pst.setLong(1, user.getUserId());

			ResultSet rs = pst.executeQuery();
			if (rs.next()) {

				double balance = rs.getDouble("balance");
				user.setBalance(balance);
				System.out.println("userbalance: " + balance);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void blockGiftCard(GiftCard giftCard) {
		double cardBalance = giftCard.getBalance();

		String updateCardQuery = "UPDATE giftcard SET status = 0, balance = 0 WHERE giftcard_id = ?";

		String updateUserBalanceQuery = "UPDATE user SET balance = balance + ? WHERE user_id = ?";

		try {
			conn.setAutoCommit(false);

			try (PreparedStatement pst = conn.prepareStatement(updateCardQuery)) {
				pst.setLong(1, giftCard.getGiftCardId());
				pst.executeUpdate();
			}

			try (PreparedStatement pst = conn.prepareStatement(updateUserBalanceQuery)) {
				pst.setDouble(1, cardBalance);
				pst.setLong(2, giftCard.getUserId());
				pst.executeUpdate();
			}

			conn.commit();

			System.out.println("Gift card blocked and amount credited back to user account.");
			addTransactionHistory(giftCard.getGiftCardId(), 0, 0, "debit");
			System.out.println("Transaction for blocking the card added.");
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void addTransactionHistory(long giftCardId, double balance, int status, String transactionType) {
		String transactionQuery = "INSERT INTO giftcardTransactions (giftcard_id, balance,transaction_type ,status ) VALUES (?, ?,?, ?)";
		try (PreparedStatement pst = conn.prepareStatement(transactionQuery)) {
			pst.setLong(1, giftCardId);
			pst.setDouble(2, balance);
			pst.setString(3, transactionType);
			pst.setInt(4, status);
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<GiftCard> getGiftCardList(User user, Integer status) {
		List<GiftCard> giftCardList = new ArrayList<>();

		String query = "SELECT * FROM giftcard WHERE user_id = ?";

		if (status != null) {
			query += " AND status = ?";
		}

		try (PreparedStatement pst = conn.prepareStatement(query)) {
			pst.setLong(1, user.getUserId());

			if (status != null) {
				pst.setInt(2, status);
			}

			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				GiftCard giftCard = new GiftCard();
				giftCard.setGiftCardId(rs.getLong("giftcard_id"));
				giftCard.setCardNumber(rs.getInt("card_number"));
				giftCard.setPin(rs.getInt("pin"));
				giftCard.setBalance(rs.getDouble("balance"));
				giftCard.setStatus(rs.getInt("status"));
				giftCard.setCreationTime(rs.getTimestamp("created_at"));
				giftCard.setUserId(user.getUserId());
				giftCardList.add(giftCard);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return giftCardList;
	}

	public void debitFromCard(GiftCard card, double amount) {
		double newBalance = card.getBalance() - amount;

		float earnedPoints = (float) (amount / 100);
		float updatedRewardPoints = card.getRewardPoints() + earnedPoints;

		String updateCardQuery = "UPDATE giftcard SET balance = ?, reward_points = ? WHERE giftcard_Id = ?";

		String insertTransactionQuery = "INSERT INTO giftcardTransactions (giftcard_id, balance, status, transaction_type ) VALUES (?, ?, ?, ?)";

		try {
			PreparedStatement updateCardStmt = conn.prepareStatement(updateCardQuery);
			updateCardStmt.setDouble(1, newBalance);
			updateCardStmt.setDouble(2, updatedRewardPoints);
			updateCardStmt.setLong(3, card.getGiftCardId());
			updateCardStmt.executeUpdate();

			PreparedStatement insertTransactionStmt = conn.prepareStatement(insertTransactionQuery);
			insertTransactionStmt.setLong(1, card.getGiftCardId());
			insertTransactionStmt.setDouble(2, newBalance);
			insertTransactionStmt.setInt(3, 1);
			insertTransactionStmt.setString(4, "debit");
			insertTransactionStmt.executeUpdate();

			card.setBalance(newBalance);
			card.setRewardPoints(updatedRewardPoints);
			System.out.println("Amount debited successfully. New balance: " + newBalance);
			System.out.println("Reward points earned: " + earnedPoints);

			if (updatedRewardPoints >= 10) {
				carditAmountToCard(card, updatedRewardPoints);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void carditAmountToCard(GiftCard card, float updatedRewardPoints) {
		String updateBalanceQuery = "UPDATE giftcard SET balance = balance + ? WHERE giftcard_id = ?";

		String insertTransactionQuery = "INSERT INTO giftcardTransactions (giftcard_id, balance,status, transaction_type) VALUES (?, ?, ?, ?)";

		try {
			conn.setAutoCommit(false);

			try (PreparedStatement updateBalancePst = conn.prepareStatement(updateBalanceQuery)) {
				updateBalancePst.setFloat(1, updatedRewardPoints);
				updateBalancePst.setLong(2, card.getGiftCardId());
				updateBalancePst.executeUpdate();
			}

			try (PreparedStatement insertTransactionPst = conn.prepareStatement(insertTransactionQuery)) {
				insertTransactionPst.setLong(1, card.getGiftCardId());
				insertTransactionPst.setDouble(2, card.getBalance() + updatedRewardPoints);
				insertTransactionPst.setInt(3, 1);
				insertTransactionPst.setString(4, "Reward Points");
				insertTransactionPst.executeUpdate();
			}

			conn.commit();

			System.out.println("Balance updated and transaction recorded successfully.");
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException rollbackEx) {
				rollbackEx.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public double checkBalance(User user) {
		double balance = 0.0;

		String query = "SELECT balance FROM user WHERE user_id = ?";

		try (PreparedStatement pst = conn.prepareStatement(query)) {

			pst.setLong(1, user.getUserId());

			ResultSet rs = pst.executeQuery();
			if (rs.next()) {

				balance = rs.getDouble("balance");
				user.setBalance(balance);
			} else {
				System.out.println("User not found or no balance available.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return balance;
	}

	@Override
	public List<GiftCard> getAllCardOf(User user, int status) {
		// TODO Auto-generated method stub
		return null;
	}


}
