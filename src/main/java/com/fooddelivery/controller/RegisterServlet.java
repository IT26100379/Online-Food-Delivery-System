package com.fooddelivery.controller;

import com.fooddelivery.exception.ValidationException;
import com.fooddelivery.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserService service = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String fullName = trim(req.getParameter("fullName"));
        String email = trim(req.getParameter("email"));
        String phone = trim(req.getParameter("phone"));
        String address = trim(req.getParameter("address"));
        try {
            service.register(fullName, email, phone, address,
                    req.getParameter("password"), req.getParameter("confirmPassword"));
            resp.sendRedirect(req.getContextPath() + "/login?registered=true");
        } catch (ValidationException e) {
            req.setAttribute("errors", e.getErrors());
            req.setAttribute("fullName", fullName);
            req.setAttribute("email", email);
            req.setAttribute("phone", phone);
            req.setAttribute("address", address);
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
        }
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}