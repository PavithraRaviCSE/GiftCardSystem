package com.example.interceptor;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.struts2.ServletActionContext;

import com.example.service.JwtGenerator;
import com.example.service.ThreadLocalClass;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.util.ValueStack;

public class SessionInterceptor implements Interceptor {

    private static final Logger logger = LogManager.getLogger(SessionInterceptor.class);

    @Override
    public void destroy() {
        logger.info("SessionInterceptor destroyed.");
    }

    @Override
    public void init() {
        logger.info("SessionInterceptor initialized.");
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        logger.info("Session interceptor invoked.");
        System.out.println("sessionINTERCEPTOR called");

        HttpServletRequest request = (HttpServletRequest) ActionContext.getContext()
                .get(ServletActionContext.HTTP_REQUEST);

        String userURL = request.getRequestURL().toString();

        HttpServletResponse response = ServletActionContext.getResponse();

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            logger.debug("Token from header: {}", accessToken);

            Map<String, Object> userData = JwtGenerator.validateToken(accessToken);
            if (!userData.isEmpty()) {
                return processValidToken(invocation, userData, request);
            } else {
                logger.error("Invalid token received.");
            }
        } else {
            logger.warn("Authorization header is missing or malformed.");
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\": \"Unauthorized\"}");
        response.getWriter().flush();
        logger.info("Unauthorized access attempt. Responded with 401.");
        return null;
    }

    private String processValidToken(ActionInvocation invocation, Map<String, Object> userData,
            HttpServletRequest request) throws Exception {
        String path = request.getRequestURI();
        logger.debug("Processing valid token. Path: {}", path);

        if (path.contains("/admin/") && !userData.get("userType").equals("admin")
                || path.contains("/user/") && !userData.get("userType").equals("user")) {

            HttpServletResponse response = ServletActionContext.getResponse();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized\"}");
            response.getWriter().flush();
            logger.warn("Unauthorized userType access attempt. Responded with 401.");
            return null;
        }

        logger.info("Valid token for userId: {} with userType: {}", userData.get("userId"), userData.get("userType"));
        ThreadLocalClass.setUserInfo((long) userData.get("userId"), (String) userData.get("userType"));

        return invocation.invoke();
    }
}
