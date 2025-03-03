package com.example.dao;

import java.sql.Connection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.example.model.GiftCardTransactions;

public class FiledTransactionScheduler {

	private static final Queue<GiftCardTransactions> failedTransactions = new ConcurrentLinkedQueue<>();
	private static ScheduledFuture<?> currentTask = null;

	public static void startScheduler(GiftCardTransactions transaction) {
		failedTransactions.add(transaction);
		if (currentTask != null && !currentTask.isDone()) {
			System.out.println("Scheduler is already running. Will not start another instance.");
			return;
		}

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

		System.out.println("Scheduler is running...");

		currentTask = scheduler.schedule(() -> {

			while (!failedTransactions.isEmpty()) {
				GiftCardTransactions tr = failedTransactions.poll();
				try (Connection conn = DatabaseConnection.getConnection();) {

					UserDAO.addGiftCardTransaction(conn, tr.getGiftCardId(), tr.getAmountTransferred(), tr.getBalance(),
							tr.getTransactionType());

					System.out.println("Transaction logged successfully: " + tr);
				} catch (Exception e) {
					System.out.println(e);
					System.err.println("Retry failed for transaction: " + tr);
					failedTransactions.add(tr);
				}
			}

			if (failedTransactions.isEmpty()) {
				System.out.println("All transactions processed. Shutting down scheduler.");
				scheduler.shutdown();

			}
		}, 15, TimeUnit.SECONDS);

		System.out.println("future: " + currentTask.toString());

	}

}
