package com.example.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class SampleController extends ActionSupport {

	private HttpServletResponse response;

    @Override
    public String execute() throws IOException {
    	 HttpServletResponse response = ServletActionContext.getResponse(); 
         if (response == null) {
             throw new IllegalStateException("HttpServletResponse is null");
         }
         response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
         response.getWriter().write("{\"error\": \"Unauthorized\"}");
         response.getWriter().flush();
         return null;
    }
}
