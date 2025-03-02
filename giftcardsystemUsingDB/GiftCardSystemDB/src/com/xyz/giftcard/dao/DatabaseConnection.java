package com.xyz.giftcard.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

	public static Connection getConnection() throws SQLException {
		
		String URL = "jdbc:mysql://localhost:3306/giftcardsystem";
		String USERNAME = "root";
		String PASSWORD = "Admin";
		try {
			return DriverManager.getConnection(URL, USERNAME, PASSWORD);
		} catch (SQLException e) {
			System.out.println("Error while connecting to the database");
			throw e;
		}
	}
}
