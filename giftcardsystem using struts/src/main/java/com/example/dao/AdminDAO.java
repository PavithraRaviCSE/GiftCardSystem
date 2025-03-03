package com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.model.User;
import com.example.service.UserException;

public class AdminDAO {

	private final static String insertUser = "INSERT INTO user (account_number,user_name,user_type, dob, balance,  password, mobile, is_first_login) values (?,?, ?, ?, ?, ?, ?,?);";
	private final static String selectAllUser = "SELECT * FROM user";

	public static boolean insertUser(Connection conn, User user) throws UserException {

		String query = insertUser;
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			pst.setString(1, user.getAccountNumber());
			pst.setString(2, user.getUserName());
			pst.setString(3, user.getUserType());
			pst.setDate(4, java.sql.Date.valueOf(user.getDob()));
			pst.setDouble(5, user.getBalance());
			pst.setString(6, user.getPassword());
			pst.setString(7, user.getMobile());
			pst.setInt(8, 1);
			int rowsAffected = pst.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			System.out.println(e);
			throw new UserException("Error in user insertion: " + e.getMessage());
		}

	}

	public static List<User> findUsersByName(String name) throws SQLException, InterruptedException {
		List<User> userList = new ArrayList<>();
		String query = "SELECT * FROM user WHERE user_name LIKE ?";

		try (Connection conn = BlockingQueueConnectionPool.getConnection();
				PreparedStatement pst = conn.prepareStatement(query)) {

			pst.setString(1, "%" + name + "%");
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					userList.add(mapResultSetToUser(rs));
				}
			}
		}
		return userList;
	}

	public static long getUserId(String accountNumber) throws SQLException {
		long userId = 0;
		String query = "SELECT user_id FROM user WHERE account_number = ?";

		try (Connection conn = DatabaseConnection.getConnection(); // Connection managed here
				PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, accountNumber);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					userId = rs.getLong("user_id");
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return userId;
	}

	public static List<User> getAllUsers() throws SQLException {
		List<User> userList = new ArrayList<>();
		String query = selectAllUser;

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				User user = mapResultSetToUser(rs);
				userList.add(user);
			}
		}
		return userList;
	}

	private static User mapResultSetToUser(ResultSet rs) throws SQLException {
		User user = new User();
		user.setUserId(rs.getLong("user_id"));
		user.setAccountNumber(rs.getString("account_number"));
		user.setUserName(rs.getString("user_name"));
		user.setPassword(rs.getString("password"));
		user.setCreatedAt(rs.getTimestamp("created_At"));
		user.setDob(rs.getDate("dob").toLocalDate());
		user.setBalance(rs.getDouble("balance"));
		user.setMobile(rs.getString("mobile"));
		user.setUserType(rs.getString("user_type"));
		return user;
	}

	public static boolean UpdateBalance(String accountnumber, double amount) {
		String query = "UPDATE user SET balance = balance + ? WHERE account_number = ?";
		try (Connection conn = DatabaseConnection.getConnection(); // Connection managed here
				PreparedStatement st = conn.prepareStatement(query)) {
			st.setDouble(1, amount);
			st.setString(2, accountnumber);
			return st.executeUpdate() > 0;
		} catch (SQLException e) {
			System.out.println("Balance not updated: " + e);
		}
		return false;
	}

	public static boolean isMobileNumberExists(String mobile) {
		String query = "SELECT COUNT(*) FROM user WHERE mobile = ?";
		try (Connection conn = DatabaseConnection.getConnection(); // Connection managed here
				PreparedStatement preparedStatement = conn.prepareStatement(query)) {
			preparedStatement.setString(1, mobile);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					int count = resultSet.getInt(1);
					return count > 0;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Error occurred while checking mobile number existence: " + e.getMessage());
		}
		return false;
	}

	public static String generateAccountNumber(Connection conn, String accountNumber) throws UserException {
		Long value = HelperDAO.fetchValue(conn, accountNumber);
		return "ACC" + value;
	}

	public static User findUserByAccountNumber(String accountNumber) throws SQLException {
		String query = "SELECT * FROM user WHERE account_number = ?";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, accountNumber);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return mapResultSetToUser(rs);
				}
			}
		}
		return null;
	}

	public static boolean creditAmount(String accountNumber, double amount) throws SQLException {
		String query = "UPDATE user SET balance = balance + ? WHERE account_number = ?";
		boolean isUpdated = false;

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setDouble(1, amount);
			stmt.setString(2, accountNumber);

			int rowsAffected = stmt.executeUpdate();
			isUpdated = rowsAffected > 0;
			System.out.println("Is amount credited: " + isUpdated);
		} catch (SQLException e) {
			throw new SQLException("Error updating account balance: " + e.getMessage(), e);
		}

		return isUpdated;
	}

	public static boolean checkMobileNumberExist(String mobile) {

		String query = "SELECT * FROM user WHERE mobile = ?;";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {
			pst.setString(1, mobile);
			ResultSet rst = pst.executeQuery();

			if (rst.next()) {
				return true;
			}

		} catch (SQLException e) {
			System.out.println(e);
		}
		return false;

	}

}
