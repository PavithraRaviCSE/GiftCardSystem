package com.example.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.example.model.Purchase;

public class PurchaseDAO {
	public static List<Purchase> getPurchasesByUserId(Long userId) {
	    List<Purchase> purchases = new ArrayList<>();
	    String query = "SELECT pn.transaction_id, pn.purchase_category " +
	                   "FROM purchase_new pn " +
	                   "JOIN giftcardtransactions t ON pn.transaction_id = t.transaction_id " +
	                   "JOIN giftcard g ON t.card_id = g.giftcard_id " +
	                   "WHERE t.transaction_type = 'debit' AND g.user_id = ?";

	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setLong(1, userId);
	        ResultSet rs = stmt.executeQuery();

	        while (rs.next()) {
	            System.out.println("purchase history.....");
	            Purchase purchase = new Purchase();
	            purchase.setTransactionId(rs.getLong("transaction_id"));
	            purchase.setPurchaseCategory(rs.getString("purchase_category"));
	            purchases.add(purchase);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    System.out.println("purchase: " + purchases);
	    return purchases;
	}

	public static List<Purchase> getPurchasesByGiftCard(Long userId, Long giftCardId) {
		System.out.println("giftcard purchase data......");
	    List<Purchase> purchases = new ArrayList<>();
	    String query = "SELECT pn.transaction_id, pn.purchase_category, t.amount_transferred " +
	               "FROM purchase_new pn " +
	               "JOIN giftcardtransactions t ON pn.transaction_id = t.transaction_id " +
	               "JOIN giftcard g ON t.giftcard_id = g.giftcard_id " +
	               "WHERE t.transaction_type = 'debit' AND g.user_id = ? AND g.giftcard_id = ?";

	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setLong(1, userId);
	        stmt.setLong(2, giftCardId);
	        ResultSet rs = stmt.executeQuery();

	        while (rs.next()) {
//	            System.out.println("purchase history.....");
	            Purchase purchase = new Purchase();
	            purchase.setTransactionId(rs.getLong("transaction_id"));
	            purchase.setPurchaseCategory(rs.getString("purchase_category"));
	            purchase.setAmountSpent(rs.getDouble("amount_transferred"));
	            purchases.add(purchase);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    System.out.println("purchase: " + purchases);
	    return purchases;
	}

	
//	public static List<Purchase> getPurchasesByUserId(Long userId) {
//		List<Purchase> purchases = new ArrayList<>();
//		String query = "SELECT p.purchase_id, p.giftcard_id, p.transaction_id, p.purchase_category, p.amount_spent, p.purchase_date"
//				+ " FROM purchase p "
//				+ " JOIN giftcard g ON p.giftcard_id = g.giftcard_id "
//				+ "WHERE g.user_id = ?;";
//
//		try (Connection conn = DatabaseConnection.getConnection();
//				PreparedStatement stmt = conn.prepareStatement(query)) {
//
//			stmt.setLong(1, userId);
//			ResultSet rs = stmt.executeQuery();
//
//			while (rs.next()) {
//				System.out.println("purchse history.....");
//				Purchase purchase = new Purchase();
//				purchase.setPurchaseId(rs.getLong("purchase_id"));
//				purchase.setGiftcardId(rs.getLong("giftcard_id"));
//				purchase.setTransactionId(rs.getLong("transaction_id"));
//				purchase.setPurchaseCategory(rs.getString("purchase_category"));
//				purchase.setAmountSpent(rs.getDouble("amount_spent"));
//				purchase.setPurchaseDate(rs.getTimestamp("purchase_date"));
//				purchases.add(purchase);
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		System.out.println("purchase: " +purchases );
//		return purchases;
//	}
//	
//	
//	
//	public static List<Purchase> getPurchasesByGiftCard(Long userId, Long giftCardId) {
//	    List<Purchase> purchases = new ArrayList<>();
//	    String query = "SELECT p.purchase_id, p.giftcard_id, p.transaction_id, p.purchase_category, p.amount_spent, p.purchase_date "
//	                 + "FROM purchase p "
//	                 + "JOIN giftcard g ON p.giftcard_id = g.giftcard_id "
//	                 + "WHERE g.user_id = ? AND p.giftcard_id = ?;";
//	    try (Connection conn = DatabaseConnection.getConnection();
//				PreparedStatement stmt = conn.prepareStatement(query)) {
//
//			stmt.setLong(1, userId);
//
//			stmt.setLong(2, giftCardId);
//			ResultSet rs = stmt.executeQuery();
//
//			while (rs.next()) {
//				System.out.println("purchse history.....");
//				Purchase purchase = new Purchase();
//				purchase.setPurchaseId(rs.getLong("purchase_id"));
//				purchase.setGiftcardId(rs.getLong("giftcard_id"));
//				purchase.setTransactionId(rs.getLong("transaction_id"));
//				purchase.setPurchaseCategory(rs.getString("purchase_category"));
//				purchase.setAmountSpent(rs.getDouble("amount_spent"));
//				purchase.setPurchaseDate(rs.getTimestamp("purchase_date"));
//				purchases.add(purchase);
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		System.out.println("purchase: " +purchases );
//		return purchases;
//	}
//	
}
