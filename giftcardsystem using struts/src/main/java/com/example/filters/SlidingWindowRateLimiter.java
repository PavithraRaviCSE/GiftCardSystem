package com.example.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class SlidingWindowRateLimiter implements Filter {
	private int limit;
	private long windowSize;
	private Jedis jedis;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
		this.limit = 100;
		this.windowSize = 60;
		this.jedis = new Jedis("localhost");
		System.out.println("Sliding Window Rate Limiter initialized.");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String ip = request.getRemoteAddr();
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		if (isRequestAllowed(ip)) {
			System.out.println("Allowed user: " + ip);
			chain.doFilter(request, response);
		} else {
			httpResponse.setStatus(429);
			httpResponse.getWriter().write("Too many requests");
		}
	}

	private boolean isRequestAllowed(String ip) {
	    String key = "rate_limit_ip:" + ip;
	    long currentTime = System.currentTimeMillis() / 1000;

	    try (Jedis jedis = new Jedis("localhost", 6379)) {
	        List<String> timestamps = jedis.lrange(key, 0, -1);

	        for (String time : timestamps) {
	            if (currentTime - Long.parseLong(time)> windowSize) {
	            	jedis.del(key , time);
	            }
	        }

	        if (timestamps.size() < limit) {
	            jedis.rpush(key, String.valueOf(currentTime));
	            return true;
	        } else {
	            System.out.println("Limit exceeded: " + timestamps.size());
	            return false;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}


	@Override
	public void destroy() {
		if (jedis != null) {
			jedis.close();
			System.out.println("Sliding Window Rate Limiter destroyed. Jedis connection closed.");
		}
	}
}
