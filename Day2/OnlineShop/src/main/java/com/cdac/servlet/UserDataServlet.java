package com.cdac.servlet;

import com.cdac.model.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/user-data")
public class UserDataServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
		    throws ServletException, IOException {
		    
		    response.setContentType("application/json");
		    
		    HttpSession session = request.getSession(false);
		    if (session != null && session.getAttribute("user") != null) {
		        User user = (User) session.getAttribute("user");
		        
		        String json = String.format(
		            "{\"id\":%d,\"username\":\"%s\",\"email\":\"%s\",\"role\":\"%s\"}",
		            user.getId(),
		            escapeJson(user.getUsername()),
		            escapeJson(user.getEmail()),
		            escapeJson(user.getRole())
		        );
		        
		        response.getWriter().write(json);
		    } else {
		        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		        response.getWriter().write("{}");
		    }
		}

		private String escapeJson(String input) {
		    return input.replace("\"", "\\\"")
		                .replace("\n", "\\n")
		                .replace("\r", "\\r")
		                .replace("\t", "\\t");
		}
    
}