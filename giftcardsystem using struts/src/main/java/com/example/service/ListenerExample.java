package com.example.service;

import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.example.dao.BlockingQueueConnectionPool;
import com.example.dao.DatabaseConnection;

public class ListenerExample implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		TemporaryBlockCardScheduler.unBlock();
		System.out.println("Servlet context initialized. Server started.");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
//		try {
//			BlockingQueueConnectionPool.shutdown();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		RedisService.closeJedisPool();
		DatabaseConnection.closeDataSource();
		System.out.println("Servlet context destroyed. Server stopped.");
	}
}
