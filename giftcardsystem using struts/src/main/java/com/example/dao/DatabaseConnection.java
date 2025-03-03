package com.example.dao;

import java.sql.Connection;
import java.sql.SQLException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConnection {
	private static String jdbcUrl = "jdbc:mysql://localhost:3306/giftcardsystemservletDB";
	private static String dbUser = "root";
	private static String dbPassword = "Admin";
	private static BlockingQueueConnectionPool pool;


	private static final HikariDataSource dataSource;

	static {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:mysql://localhost:3306/giftcardsystemservletDB");
		config.setUsername("root");
		config.setPassword("Admin");
		config.setMaximumPoolSize(10);
		config.setMinimumIdle(2);

        // Auto-reconnect: Ensure connection is alive before use
		config.setConnectionTestQuery("SELECT 1");

		dataSource = new HikariDataSource(config);
	}

	public static Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	public static void closeDataSource() {
		if (dataSource != null) {
			dataSource.close();
		}
	}
}
