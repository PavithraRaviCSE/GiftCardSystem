package com.example.filters;

import redis.clients.jedis.Jedis;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.service.RedisService;

import java.io.IOException;

public class RateLimitFilter implements Filter {

	private static final int MAX_REQUESTS = 100; 
	private static final long EXPIRATION_TIME = 60;

	@Override
	public void init(FilterConfig filterConfig) {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String ip = httpRequest.getRemoteAddr();

		try (Jedis jedis = RedisService.getConnection()) {

			if (jedis.exists("blocked:" + ip)) {
				System.out.println("ip is temporarily blocked:" + ip);
				httpResponse.setStatus(429);
				httpResponse.getWriter().write("Too many requests try again later");
				return;
			}

			String key = "request_count:" + ip;
			long count = jedis.incr(key);

			if (count == 1) { 
				jedis.expire(key, EXPIRATION_TIME);
			}

			System.out.println("User request count: " + count);

			if (count > MAX_REQUESTS) {
				jedis.setex("blocked:" + ip, 100, "blocked");
				httpResponse.setStatus(429);
				httpResponse.getWriter().write("Too many requests");
				return;
			}

			chain.doFilter(request, response);
			
		}

	}

	@Override
	public void destroy() {
	}
}
