package com.cdac.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import com.cdac.dao.UserDao;
import com.cdac.model.User;
import com.cdac.util.CookieUtil;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private UserDao userDao;
    
    @Override
    public void init() throws ServletException {
        super.init();
        userDao = new UserDao();
    }
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String rememberedUser = CookieUtil.getCookie(request, "rememberedUser");
        if (rememberedUser != null) {
            response.sendRedirect("login.html?rememberedUser=" + rememberedUser);
        } else {
            response.sendRedirect("login.html");
        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
	    String password = request.getParameter("password");
	    
	    User user = userDao.validateUser(username, password);
	    
	    if (user != null) {
	        HttpSession session = request.getSession();
	        session.setAttribute("user", user);
	        System.out.println("admin".equalsIgnoreCase(user.getRole()));
	        if ("admin".equalsIgnoreCase(user.getRole())) {
	            // Consider redirecting directly to admin.html if needed
	            response.sendRedirect("admin");
	        } else {
	            response.sendRedirect("welcome");
	        }
	    } else {
	        response.sendRedirect("login.html?error=Invalid credentials");
	    }
	}

}
