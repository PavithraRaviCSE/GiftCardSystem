package com.example.dao;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class BlockingQueueConnectionPool {
	private static final String jdbcUrl = "jdbc:mysql://localhost:3306/giftcardsystemservletDB";
	private static final String dbUser = "root";
	private static final String dbPassword = "Admin";
	private static final int maxConnections = 10;
	private static BlockingQueue<Connection> connectionPool;

	static {
		connectionPool = new ArrayBlockingQueue<>(maxConnections);
		for (int i = 0; i < maxConnections; i++) {
			try {
				connectionPool.add(createNewConnection());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private static Connection createNewConnection() throws SQLException {
		return DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
	}

	public static Connection getConnection() throws InterruptedException {
		System.out.println("connection pool size:" + connectionPool.size());
		Connection conn = connectionPool.poll(10, TimeUnit.SECONDS);
		if (conn == null) {
			return null; // No available connections
		}

		return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),
				new Class<?>[] { Connection.class }, new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if ("close".equals(method.getName())) {

							System.out.println("clode method called: " + method.getName());
							release(conn);
							return null;
						}
						System.out.println("other methods: " + method.getName());
						return method.invoke(conn, args);
					}
				});
	}

	public static void release(Connection connection) {
		if (connection != null) {
			try {
				if (!connectionPool.offer(connection, 5, TimeUnit.SECONDS)) {
					connection.close();
					System.out.println("cannot add the connection into the pool...");
				} else {
					System.out.println("the conenction  obj is added to the pool successfilly..");
				}
			} catch (Exception e) {
				System.out.println("e: " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			try {
				Connection newConnection = createNewConnection();
				connectionPool.offer(newConnection);
				System.out
						.println("connection object is null new connection object is created and added into the queue");
			} catch (SQLException e) {
				System.err.println("Failed to create a new connection for the pool.");
				e.printStackTrace();
			}
		}
	}

	public static void shutdown() throws SQLException {
		for (Connection connection : connectionPool) {
			connection.close();
		}
	}
}

//
//public BlockingQueueConnectionPool(String url, String user, String password, int maxConnections)
//		throws SQLException {
//	this.url = url;
//	this.user = user;
//	this.password = password;
//	BlockingQueue<Connection> connectionPool = new ArrayBlockingQueue<>(maxConnections);
//	for (int i = 0; i < maxConnections; i++) {
//		connectionPool.add(createNewConnection());
//	}
//}