package com.example.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.*;

import com.example.dao.DatabaseConnection;

public class TemporaryBlockCardScheduler {

	private static ScheduledFuture<?> currentTask = null;

	public static void unBlock() {
		if (currentTask != null && !currentTask.isDone()) {
			System.out.println("Scheduler is already running.......");
			return;
		}

		System.out.println("Scheduler is running...");

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

		currentTask = scheduler.scheduleAtFixedRate(() -> {
			try (Connection conn = DatabaseConnection.getConnection()) {
				
				String updateQuery = "UPDATE giftcard SET status = 1, temp_block_time = null WHERE status = 2 AND TIMESTAMPDIFF(MINUTE, temp_block_time, NOW()) > 1";

				try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
					int updatedRows = updateStmt.executeUpdate();
					System.out.println(updatedRows + " cards unblocked.");
				}

				String countQuery = "SELECT COUNT(*) FROM giftcard WHERE status = 2";
				try (PreparedStatement countStmt = conn.prepareStatement(countQuery);
						ResultSet countRs = countStmt.executeQuery()) {
					if (countRs.next() && countRs.getInt(1) > 0) {
						System.out.println("There are still blocked cards. No action needed.");
					} else {
						System.out.println("No more blocked cards. Shutting down the scheduler.");
						scheduler.shutdown();
					}
				}

			} catch (SQLException e) {
				System.err.println("Error unblocking cards: " + e);
			}
		}, 1, 1, TimeUnit.MINUTES);

		System.out.println("Task scheduled: " + currentTask);
	}

}
