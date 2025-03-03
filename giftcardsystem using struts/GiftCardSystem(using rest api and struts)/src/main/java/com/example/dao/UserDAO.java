package com.example.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.example.helper.Helper;
import com.example.model.GiftCard;
import com.example.model.GiftCardTransactions;
import com.example.model.Purchase;
import com.example.model.User;
import com.example.service.RedisService;
import com.example.service.TemporaryBlockCardScheduler;
import com.example.service.UserException;

enum DateUnit {
	SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, YEAR;
}

public class UserDAO {

	private static Connection getConnection() throws SQLException {
		return DatabaseConnection.getConnection(); // Replace with your actual connection method
	}

	public static User login(long userId, String password) throws SQLException {
		User user = null;
		String query = "SELECT * FROM user WHERE user_id = ? AND password = ?";
		String passwordEnc = null;

		try {
			passwordEnc = Helper.encryption(password);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			conn = getConnection();
			pst = conn.prepareStatement(query);
			pst.setLong(1, userId);
			pst.setString(2, passwordEnc);

			rs = pst.executeQuery();
			if (rs.next()) {
				System.out.println("user exists");
				user = new User();
				user.setUserId(rs.getLong("user_Id"));
				user.setAccountNumber(rs.getString("account_number"));
				user.setUserName(rs.getString("user_name"));
				user.setUserType(rs.getString("user_type"));
				user.setDob(rs.getDate("dob").toLocalDate());
				user.setBalance(rs.getDouble("balance"));
				user.setPassword(rs.getString("password"));
				user.setMobile(rs.getString("mobile"));
				user.setCreatedAt(rs.getTimestamp("created_At"));
				user.setIsFirstLogin(rs.getBoolean("is_First_Login"));
			}
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pst != null)
					pst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
//			DatabaseConnection.release(conn);
		}
		return user;
	}

	public static double checkUserBalance(long userId) throws SQLException {
		double balance = 0;
		String query = "SELECT balance FROM user WHERE user_id = ?";
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setLong(1, userId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				balance = rs.getDouble("balance");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return balance;
	}

	public static long generateCardNumber(Connection conn, String cardNumber) throws SQLException, UserException {
		return HelperDAO.fetchValue(conn, cardNumber);
	}

	public static boolean createGiftCard(Connection conn, GiftCard giftCard) throws SQLException {
		String query = "INSERT INTO giftcard (giftcard_number, pin, balance, user_id) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			pst.setLong(1, giftCard.getGiftCardNumber());
			pst.setLong(2, giftCard.getPin());
			pst.setDouble(3, giftCard.getBalance());
			pst.setLong(4, giftCard.getUserId());
			return pst.executeUpdate() > 0;
		}
	}

	public static boolean debitFromUser(long userId, double amount) throws SQLException {
		String query = "UPDATE user SET balance = balance - ? WHERE user_id = ? ";
		try (Connection conn = getConnection(); PreparedStatement pst = conn.prepareStatement(query)) {
			pst.setDouble(1, amount);
			pst.setLong(2, userId);
			return pst.executeUpdate() > 0;
		}
	}

	public static boolean addGiftCardTransaction(Connection conn, long cardId, double amount, double balance,
			String transactionType) throws SQLException {
		String insertTransactionQuery = "INSERT INTO giftcardTransactions (giftcard_id, amount_transferred, balance, transaction_type) VALUES (?, ?,? , ?)";

		try (PreparedStatement insertPst = conn.prepareStatement(insertTransactionQuery)) {
			insertPst.setLong(1, cardId);
			insertPst.setDouble(2, amount);
			insertPst.setDouble(3, balance);
			insertPst.setString(4, transactionType);

			System.out.println("card transaction logged successfully");
			return insertPst.executeUpdate() > 0;
		}
	}

	public static long addGiftCardTransactions(Connection conn, long cardId, double amount, double balance,
			String transactionType) throws SQLException {
		String callProcedure = "{CALL addGiftCardTransaction(?, ?, ?, ?, ?)}";

		try (CallableStatement cstmt = conn.prepareCall(callProcedure)) {
			cstmt.setLong(1, cardId);
			cstmt.setDouble(2, amount);
			cstmt.setDouble(3, balance);
			cstmt.setString(4, transactionType);
			cstmt.registerOutParameter(5, Types.BIGINT);
			cstmt.execute();
			return cstmt.getLong(5); // Retrieve the transaction ID
		}
	}

	public static void processGiftCardTransaction(Connection conn, long cardId, double amount, double balance,
			String transactionType, String category) {
		try {
			long transactionId = addGiftCardTransactions(conn, cardId, amount, balance, transactionType);
			if (transactionId > 0) {
				boolean purchaseSuccess = addPurchase(conn, transactionId, category);
				if (purchaseSuccess) {
					System.out.println("Purchase record added successfully.");
				} else {
					System.out.println("Failed to add purchase record.");
				}
			} else {
				System.out.println("Failed to add transaction.");
			}
		} catch (Exception e) {
			
		}

		
	}

	public static boolean addGiftCardTransaction(Connection conn, long cardId, double amount, double balance,
			String transactionType, boolean bool) throws SQLException, UserException, InterruptedException {

		if (bool) {
			throw new UserException("transaction cannot logged..");
		}
		String insertTransactionQuery = "INSERT INTO giftcardTransactions (giftcard_id, amount_transferred, balance, transaction_type) VALUES (?, ?,? , ?)";

		try (PreparedStatement insertPst = conn.prepareStatement(insertTransactionQuery)) {
			insertPst.setLong(1, cardId);
			insertPst.setDouble(2, amount);
			insertPst.setDouble(3, balance);
			insertPst.setString(4, transactionType);
			return insertPst.executeUpdate() > 0;
		}
	}

	public static List<GiftCard> getGiftCardList(long userId, Integer status) throws SQLException {
		List<GiftCard> giftCardList = new ArrayList<>();

		String query = "SELECT * FROM giftcard WHERE user_id = ?";

		if (status != null) {
			query += " AND status = ?";
		}

		System.out.println("User is: " + userId);

		try (Connection conn = getConnection(); PreparedStatement pst = conn.prepareStatement(query)) {
			pst.setLong(1, userId);

			if (status != null) {
				pst.setInt(2, status);
			}

			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				GiftCard giftCard = new GiftCard();
				giftCard.setGiftCardId(rs.getLong("giftcard_id"));
				giftCard.setGiftCardNumber(rs.getLong("giftcard_number"));
				giftCard.setPin(rs.getLong("pin"));
				giftCard.setBalance(rs.getDouble("balance"));
				giftCard.setStatus(rs.getInt("status"));
				giftCard.setCreatedAt(rs.getTimestamp("created_at"));
				giftCard.setUserId(userId);
				giftCardList.add(giftCard);
			}

			System.out.println("card list " + giftCardList);
		}
		return giftCardList;
	}

	public static List<GiftCardTransactions> getCardTransactionListOf(Long cardId) throws SQLException {
		List<GiftCardTransactions> transactionList = new ArrayList<>();
		String query = "SELECT * FROM giftcardTransactions WHERE giftcard_id = ?";

		try (Connection conn = getConnection(); PreparedStatement pst = conn.prepareStatement(query)) {

			pst.setLong(1, cardId);
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					long transactionId = rs.getLong("transaction_id");
					double amount = rs.getLong("amount_Transferred");
					double balance = rs.getDouble("balance");
					Timestamp transactionTime = rs.getTimestamp("created_at");
					String transactionType = rs.getString("transaction_type");

					transactionList.add(new GiftCardTransactions(transactionId, cardId, amount, balance,
							transactionType, transactionTime));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error fetching transactions for gift card ID " + cardId);
			e.printStackTrace();
			throw e;
		}

		return transactionList;
	}

	public static boolean topUpCard(long giftCardId, double amount, long userId) throws SQLException, UserException {
		try (Connection conn = DatabaseConnection.getConnection()) {
			conn.setAutoCommit(false);

			String carddata = "Select balance, status from giftcard where giftCard_Id = ? and user_Id = ?";
			String debitUserBalanceQuery = "UPDATE user SET balance = balance - ? WHERE user_id = ?";
			String updateGiftCardBalanceQuery = "UPDATE giftcard SET balance = balance + ? WHERE giftcard_id = ?";
			String insertTransactionQuery = "INSERT INTO giftcardTransactions (giftcard_id,amount_transferred, balance, transaction_type) VALUES (?, ?, ?, ?)";

			double currentBalance;

			try (PreparedStatement cardPst = conn.prepareStatement(carddata)) {
				cardPst.setLong(1, giftCardId);
				cardPst.setLong(2, userId);
				ResultSet rs = cardPst.executeQuery();
				if (rs.next()) {

					if (rs.getInt("status") == 0) {
						throw new UserException("Card is Blocked");
					}
					currentBalance = rs.getDouble("balance");
//					userId = rs.getLong("user_id");

				} else {
					throw new UserException("No card found with cardId: " + giftCardId + " for the userId: " + userId);
				}
			}

			try (PreparedStatement debitPst = conn.prepareStatement(debitUserBalanceQuery)) {
				debitPst.setDouble(1, amount);
				debitPst.setLong(2, userId);

				int rowsAffected = debitPst.executeUpdate();
				if (rowsAffected == 0) {
					throw new UserException("No card found with user id: " + userId);
				}
			}

			try (PreparedStatement updatePst = conn.prepareStatement(updateGiftCardBalanceQuery)) {
				updatePst.setDouble(1, amount);
				updatePst.setLong(2, giftCardId);

				int rowsAffected = updatePst.executeUpdate();
				if (rowsAffected == 0) {
					conn.rollback();
					throw new UserException("No card found with user id: " + giftCardId);
				}
				conn.commit();
			}

			try (PreparedStatement insertPst = conn.prepareStatement(insertTransactionQuery)) {
				insertPst.setLong(1, giftCardId);
				insertPst.setDouble(2, amount);
				insertPst.setDouble(3, currentBalance + amount);
				insertPst.setString(4, "top-up");

				int rowsAffected = insertPst.executeUpdate();
				if (rowsAffected == 0) {
					conn.rollback();
					return false;
				}
			}

			conn.commit();
			return true;
		}
	}

	public static GiftCard createGiftCard(long userId, double amount, long pin) throws SQLException, UserException {
		Connection conn = null;
		try {

			try {
				conn = DatabaseConnection.getConnection();
			} catch (Exception e) {
				System.out.println("error: " + e);
				throw new UserException("Error try again later");
			}

			conn.setAutoCommit(false);

			double currentBalance = checkUserBalance(userId);
			if (currentBalance < amount) {
				throw new UserException("Balance is not sufficitent");
			}

			try {
				boolean isdebitSuccess = debitFromUser(userId, amount);
				if (!isdebitSuccess) {

					System.out.println("Debit operation failed. Transaction rolled back.");
					throw new UserException("Cannot debit form user...");

				}

			} catch (Exception e) {
				throw new UserException("Error in Debit form user try again later...");
			}
			long cardNumber = generateCardNumber(conn, "card_number");
			System.out.println("card_number: " + cardNumber);
			GiftCard giftCard = new GiftCard();
			giftCard.setGiftCardNumber(cardNumber);
			giftCard.setBalance(amount);
			giftCard.setPin(pin);
			giftCard.setUserId(userId);

			boolean cardCreated = createGiftCard(conn, giftCard);
			if (!cardCreated) {
				conn.rollback();
				throw new UserException("Gift card creation failed. Transaction rolled back.");
			} else {
				conn.commit();
			}

			long cardId = getGiftCardIdByCardNumber(cardNumber);
			System.out.println("card id:" + cardId);

			try {
				boolean transactionLogged = addGiftCardTransaction(conn, cardId, amount, amount, "credit", true);
				System.out.println("transactionLogged :" + transactionLogged);
				if (!transactionLogged) {
					System.out.println("transaction not logged");
				}
			} catch (Exception e) {
				GiftCardTransactions failedTransaction = new GiftCardTransactions(cardId, amount, amount, "credit");

				FiledTransactionScheduler.startScheduler(failedTransaction);
			}

			conn.commit();
			return giftCard;

		} catch (Exception e) {
			try {
				System.out.println("roll back ");
				conn.rollback();
				conn.setAutoCommit(true);
			} catch (Exception e2) {
				System.out.println("cannot roll back ");
			}
			throw new UserException(e.getMessage());

		}

	}

	public static Long getGiftCardIdByCardNumber(long cardNumber) throws SQLException, UserException {
		String query = "SELECT giftcard_id FROM giftcard WHERE giftcard_number = ?;";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query)) {
			pst.setLong(1, cardNumber);
			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) { // Check if there is a result
					return rs.getLong("giftcard_id");
				} else {
					throw new UserException("Gift card ID not found for the provided card number.");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UserException("Error occurred while fetching gift card ID: " + e.getMessage());
		}
	}

	public boolean blockGiftCard(GiftCard giftCard) {
		double cardBalance = giftCard.getBalance();

		String updateCardQuery = "UPDATE giftcard SET status = 0 WHERE giftcard_id = ?";
		String updateUserBalanceQuery = "UPDATE user SET balance = balance + ? WHERE user_id = ?";

		Connection conn = null;

		try {
			conn = DatabaseConnection.getConnection();
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

			addGiftCardTransaction(conn, giftCard.getGiftCardId(), cardBalance, 0, "blocked");

			conn.commit();
			System.out.println(
					"Gift card blocked and amount credited back to user account. Transaction for blocking the card added.");

			return true;
		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback();
					System.out.println("Transaction rolled back due to an error.");
				} catch (SQLException ex) {
					System.out.println(e);
				}
			}
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return false;
	}

	public static boolean changePassword(long userId, String currentPassword, String newPassword, boolean isFirstLogin)
			throws SQLException {

		String selectQuery = "SELECT password FROM user WHERE user_id = ?;";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(selectQuery)) {

			pst.setLong(1, userId);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				String storedPassword = rs.getString("password");

				System.out.println("current password: " + storedPassword + " " + currentPassword);
				if (!storedPassword.equals(currentPassword)) {
					return false;
				}

			} else {
				System.out.println("no user found with id: " + userId);
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new SQLException(e);
		}

		// Step 3: Update the password
		String updateQuery = "UPDATE user SET password = ?";

		if (isFirstLogin) {
			updateQuery += ", is_first_login = false";
		}

		updateQuery += " WHERE user_id = ?;";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(updateQuery)) {

			pst.setString(1, newPassword); // Set the new password
			pst.setLong(2, userId); // Set the user ID

			int rowsUpdated = pst.executeUpdate();
			return rowsUpdated > 0; // Return true if update was successful
		} catch (SQLException e) {
			e.printStackTrace();
			throw new SQLException(e);
		}
	}

//	public static boolean addPurchaseRecord(Connection conn, long transactionId, long giftCardId, double amount, String category) throws SQLException {
//	    String insertQuery = "INSERT INTO purchase (transaction_id, giftcard_id, amount_spent, purchase_category) VALUES (?, ?, ?, ?)";
//
//	    try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
//	        pstmt.setLong(1, transactionId);
//	        pstmt.setLong(2, giftCardId);
//	        pstmt.setDouble(3, amount);
//	        pstmt.setString(4, category);
//
//	        return pstmt.executeUpdate() > 0;
//	    }
//	}
	public static boolean addPurchase(Connection conn, Long transactionId, String category) {
		String insertQuery = "INSERT INTO purchase_new (transaction_id, purchase_category) VALUES (?, ?)";

		try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

			pstmt.setLong(1, transactionId);
			pstmt.setString(2, category);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static Long purchase(long cardNumber, long pin, double amount, String category) throws UserException {
		Connection conn = null;

		try {
			conn = DatabaseConnection.getConnection();

			GiftCard card = getCardByCardNumber(cardNumber);
			System.out.println("card: " + card);
			if (card == null) {
				throw new UserException("Incorrect card number.");
			}
			if (card.getPin() != pin) {
				return handleFailedAttempt(cardNumber, card.getGiftCardId(), card.getUserId());
			}
			if (card.getStatus() == 0 && pin == card.getPin()) {
				throw new UserException("The card is permanently blocked.");
			}
			if (card.getStatus() == 2 && pin == card.getPin()) {
				throw new UserException("The card is temporarily blocked.");
			}

			if (card.getBalance() < amount) {
				throw new UserException("Insufficient balance. Available balance: " + card.getBalance());
			}

			try {

				conn.setAutoCommit(false);
				boolean isAmountDebited = debitFromCard(card, amount);
				if (isAmountDebited) {

					try {
						processGiftCardTransaction(conn, card.getGiftCardId(), amount, card.getBalance() - amount,
								"debit", category);

					} catch (Exception e) {
						System.out.println("transaction is not logged");
					}
				}
				conn.commit();
			} catch (Exception e) {
				conn.rollback();
				throw new UserException("Transaction failed. Try again later");
			}

			return card.getUserId();

		} catch (SQLException e) {
			throw new UserException(e.getMessage());
		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static Long handleFailedAttempt(long cardNumber, long cardId, long userId) throws UserException {

		int count = RedisService.temporarilyBlockedCards(cardNumber);
		if (count == 3) {
			blockCardTemporarily(2, cardId, 1, DateUnit.MINUTE);
			TemporaryBlockCardScheduler.unBlock();
			RedisService.removeUserData(userId);
			throw new UserException("Too many failed attempts! Card is temporarily blocked for 10 minutes.");

		} else {
			throw new UserException("Incorrect Pin, Remaining attempts: " + (3 - count));
		}

	}

	private static void blockCardTemporarily(int status, long cardId, int interval, DateUnit unit) {
		String updateCardQuery = "UPDATE giftcard SET status = ?, temp_block_time = NOW() WHERE giftcard_id = ?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(updateCardQuery)) {
			pst.setLong(1, status);
			pst.setLong(2, cardId);
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static GiftCard getCardByCardNumber(long cardNumber) {
		String query = "Select * from giftcard where giftcard_number = ?";
		try (Connection con = DatabaseConnection.getConnection();
				PreparedStatement pst = con.prepareStatement(query);) {

			pst.setLong(1, cardNumber);

			ResultSet rs = pst.executeQuery();

			if (rs.next()) {

				return mapResultSetToGiftCard(rs);

			} else {
				System.out.println("No card fount");
			}
		} catch (SQLException e) {
			System.out.println("Error: " + e);
		}

		return null;

	}

	private static GiftCard getCard(long cardNumber, long pin) {
		String query = "Select * from giftcard where giftcard_number = ? and pin = ?";
		try (Connection con = DatabaseConnection.getConnection();
				PreparedStatement pst = con.prepareStatement(query);) {

			pst.setLong(1, cardNumber);
			pst.setLong(2, pin);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {

				return mapResultSetToGiftCard(rs);

			} else {
				System.out.println("No card fount");
			}
		} catch (SQLException e) {
			System.out.println("Error" + e);
		}

		return null;
	}

	private static GiftCard mapResultSetToGiftCard(ResultSet rs) throws SQLException {
		GiftCard card = new GiftCard();

		card.setGiftCardId(rs.getLong("giftcard_id"));
		card.setUserId(rs.getLong("user_id"));
		card.setBalance(rs.getDouble("balance"));
		card.setPin(rs.getLong("pin"));
		card.setRewardPoints(rs.getFloat("reward_points"));
		card.setCreatedAt(rs.getTimestamp("created_at"));
		card.setStatus(rs.getInt("status"));
		card.setGiftCardNumber(rs.getLong("giftcard_number"));

		return card;
	}

	public static boolean debitFromCard(GiftCard card, double amount) {
		double newBalance = card.getBalance() - amount;

		float earnedPoints = (float) (amount / 100);
		float updatedRewardPoints = card.getRewardPoints() + earnedPoints;

		String updateCardQuery = "UPDATE giftcard SET balance = ?, reward_points = ? WHERE giftcard_Id = ?";

		String insertTransactionQuery = "INSERT INTO giftcardTransactions (giftcard_id,amount_transferred, balance, transaction_type ) VALUES (?,?, ?, ?)";

		Connection conn = null;
		try {
			conn = DatabaseConnection.getConnection();
			PreparedStatement updateCardStmt = conn.prepareStatement(updateCardQuery);
			updateCardStmt.setDouble(1, newBalance);
			updateCardStmt.setDouble(2, updatedRewardPoints);
			updateCardStmt.setLong(3, card.getGiftCardId());
			updateCardStmt.executeUpdate();

			PreparedStatement insertTransactionStmt = conn.prepareStatement(insertTransactionQuery);
			insertTransactionStmt.setLong(1, card.getGiftCardId());
			insertTransactionStmt.setDouble(2, amount);
			insertTransactionStmt.setDouble(3, newBalance);
			insertTransactionStmt.setString(4, "debit");
			insertTransactionStmt.executeUpdate();

			card.setBalance(newBalance);
			card.setRewardPoints(updatedRewardPoints);
			System.out.println("Amount debited successfully. New balance: " + newBalance);
			System.out.println("Reward points earned: " + earnedPoints);

			if (updatedRewardPoints >= 10) {
				carditAmountToCard(card, updatedRewardPoints);
			}

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;

	}

	private static void carditAmountToCard(GiftCard card, float updatedRewardPoints) {
		String updateBalanceQuery = "UPDATE giftcard SET balance = balance + ? WHERE giftcard_id = ?";
		String insertTransactionQuery = "INSERT INTO giftcardTransactions (giftcard_id, amount_transferred, balance, transaction_type) VALUES (?,?,  ?, ?)";

		Connection conn = null;
		try {
			conn = DatabaseConnection.getConnection();
			try (PreparedStatement updateBalancePst = conn.prepareStatement(updateBalanceQuery)) {
				updateBalancePst.setFloat(1, updatedRewardPoints);
				updateBalancePst.setLong(2, card.getGiftCardId());
				updateBalancePst.executeUpdate();
			}

			try (PreparedStatement insertTransactionPst = conn.prepareStatement(insertTransactionQuery)) {
				insertTransactionPst.setLong(1, card.getGiftCardId());
				insertTransactionPst.setDouble(2, card.getBalance() + updatedRewardPoints);
				insertTransactionPst.setDouble(3, card.getBalance() + updatedRewardPoints);
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

	public static boolean blockCard(long giftCardId, long userId) throws UserException, SQLException {

		String cardData = "Select * from giftcard where giftcard_id = ? and user_Id = ?";
		String cardUpdateQuery = "UPDATE giftcard SET status = 0 , balance = 0 where giftcard_id = ? ";
		String userUpdateQuery = "UPDATE user SET balance = balance + ? where user_id = ? ";
		String transactionUpdateQuery = "insert into giftcardtransactions(giftcard_id, amount_transferred, balance, transaction_type)"
				+ " values (?,?,?,?)";

		Connection conn = null;
		try {

			conn = DatabaseConnection.getConnection();
			conn.setAutoCommit(false);

			PreparedStatement pst = conn.prepareStatement(cardData);
			pst.setLong(1, giftCardId);
			pst.setLong(2, userId);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {

				GiftCard card = mapResultSetToGiftCard(rs);

				if (card.getStatus() == 0) {
					throw new UserException("card is already blocked....");
				}

				PreparedStatement pst2 = conn.prepareStatement(cardUpdateQuery);
				pst2.setLong(1, giftCardId);
				if (!(pst2.executeUpdate() > 0)) {
					throw new UserException("No card found using this id+ " + giftCardId);
				}

				PreparedStatement pst3 = conn.prepareStatement(userUpdateQuery);
				pst3.setDouble(1, card.getBalance());
				pst3.setLong(2, card.getUserId());
				if (!(pst3.executeUpdate() > 0)) {
					throw new UserException("No use found with this userid" + card.getUserId());
				}
				conn.commit();

				PreparedStatement pst4 = conn.prepareStatement(transactionUpdateQuery);
				pst4.setLong(1, giftCardId);
				pst4.setDouble(2, card.getBalance());
				pst4.setDouble(3, 0);
				pst4.setString(4, "Block card");
				if (!(pst4.executeUpdate() > 0)) {
					System.out.println("not added in transaction.....");
					throw new UserException("cannot transation updation for this cardId: " + giftCardId);
				} else {
					System.out.println("transaction added....");
				}
				conn.commit();
				return true;
			} else {
				throw new UserException("no card number: " + giftCardId + " found with the userId: " + userId);
			}

		} catch (Exception e) {

			try {
				if (conn != null) {
					conn.rollback();
					conn.setAutoCommit(true);

				}
			} catch (Exception e2) {
				throw new UserException("Error in rollback in block cards...." + e2.getMessage());
			}
			throw new UserException("Exception in blocking card: " + e.getMessage());
		}
	}

	public static boolean checkCardNumber(long cardNumber) {

		String cardQuery = "select * from giftcard where card_number = ?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(cardQuery)) {

			boolean isValidCardNumber = pst.execute();
			if (isValidCardNumber) {
				return true;
			}

		} catch (Exception e) {
			System.out.println("exception while chencking the card number " + e);
		}
		return false;
	}

}
