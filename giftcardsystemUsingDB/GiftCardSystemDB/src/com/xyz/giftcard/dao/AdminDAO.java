package com.xyz.giftcard.dao;

import java.util.ArrayList;
import java.util.List;

import com.xyz.giftcard.entity.Admin;
import com.xyz.giftcard.entity.User;
import java.sql.*;

public class AdminDAO implements AdminDAOInterface {

	private static PreparedStatement pst;
	private Connection conn;

	public AdminDAO() {
		try {
			conn = DatabaseConnection.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Admin adminLogin(String name, String password) {
		String query = "Select * from admin where username = ?  and password = ? ";
		Admin admin;
		PreparedStatement pst;
		try {
			pst = conn.prepareStatement(query);
			pst.setString(1, name);
			pst.setString(2, password);
			ResultSet rs;
			rs = pst.executeQuery();
			if (rs.next()) {
				admin = new Admin(rs.getLong("admin_id"), rs.getNString("username"), rs.getNString("password"));
				return admin;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;

	}

	@Override
	public boolean insertUser(User user) {
		String query = "INSERT INTO user (name, dob, balance, account_number, password, mobile, is_first_login) VALUES (?,?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			pst.setString(1, user.getName());
			pst.setDate(2, java.sql.Date.valueOf(user.getDob()));
			pst.setDouble(3, user.getBalance());
			pst.setString(4, user.getAccountNumber());
			pst.setString(5, user.getPassword());
			pst.setString(6, user.getMobile());
			pst.setInt(7, 1);

			int rowsAffected = pst.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			System.err.println("Error while inserting user: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<User> getAllUser() {
		List<User> userList = new ArrayList<>();
		try {
			String query = "SELECT * FROM user";
			pst = conn.prepareStatement(query);
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				User user = new User(rs.getInt("user_id"), rs.getString("account_number"), rs.getString("name"),
						rs.getDate("dob").toLocalDate(), rs.getDouble("balance"), rs.getString("password"),
						rs.getString("mobile"), rs.getTimestamp("created_at"), rs.getBoolean("is_first_login"));
				userList.add(user);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return userList;
	}

	@Override
	public List<User> getUserByName(String name) {

		List<User> userList = new ArrayList<>();

		String query = "SELECT * FROM user WHERE name LIKE ?";
		try {
			PreparedStatement pst = conn.prepareStatement(query);
			pst.setString(1, "%" + name + "%");
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {

				User user = new User(rs.getInt("user_id"), rs.getString("account_number"), rs.getString("name"),
						rs.getDate("dob").toLocalDate(), rs.getDouble("balance"), rs.getString("password"),
						rs.getString("mobile"), rs.getTimestamp("created_at"), rs.getBoolean("is_first_login"));
				userList.add(user);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return userList;

	}

	@Override
	public List<User> getUsersByAccountNumber(String accountNumber) {
		List<User> userList = new ArrayList<>();

		String query = "Select * from user where account_number like ? ";

		try (PreparedStatement pst = conn.prepareStatement(query)) {
			pst.setString(1, accountNumber);
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				User user = new User(rs.getInt("user_id"), rs.getString("account_number"), rs.getString("name"),
						rs.getDate("dob").toLocalDate(), rs.getDouble("balance"), rs.getString("password"),
						rs.getString("mobile"), rs.getTimestamp("created_at"), rs.getBoolean("is_first_login"));
				userList.add(user);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return userList;
	}

	public boolean printUserByAccountNumber(String accountNumber) {
		String query = "SELECT * FROM user WHERE account_number = ?";

		try (PreparedStatement pst = conn.prepareStatement(query)) {
			pst.setString(1, accountNumber);

			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				System.out.println(User.getColumn());
				long userId = rs.getLong("user_Id");
				String name = rs.getString("name");
				Date dob = rs.getDate("dob");
				double balance = rs.getDouble("balance");
				String mobile = rs.getString("mobile");
				Timestamp createdAt = rs.getTimestamp("created_at");
				boolean isFirstLogin = rs.getBoolean("is_first_login");
				System.out.println(userId + "\t\t" + accountNumber + "\t\t" + name + "\t\t" + dob + "\t\t" + balance
						+ "\t\t" + mobile + "\t" + createdAt + "\t" + isFirstLogin);
				return true;
			} else {

				System.out.println("No user found with the account number: " + accountNumber);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean UpdateBalance(String accountnumber, double amount) {

		try {
			String query = "Update user set balance = balance + " + amount + " where account_number = '" + accountnumber
					+ "' ;";

			Statement st = conn.createStatement();
			return st.executeUpdate(query) > 0;
		} catch (Exception e) {
			System.out.println("balance not updated: " + e);
		}

		return false;
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
			} else {
				String query1 = "insert into idgenerator(name, value) values(?,? )";
				try (PreparedStatement PST = conn.prepareStatement(query1)) {
					PST.setString(1, name);
					PST.setLong(1, 10002);
					int row = PST.executeUpdate();
					if (row > 0) {
						return 10001;
					}

				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0L;
	}

	public String generateAccountNumber(String accountNumber) {
		Long value = fetchValue(accountNumber);
		return "ACC" + value;
	}

}
