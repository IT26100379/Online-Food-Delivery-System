package com.fooddelivery.controller;

import com.fooddelivery.exception.ValidationException;
import com.fooddelivery.model.Role;
import com.fooddelivery.model.User;
import com.fooddelivery.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/** View / update profile, change password, delete own account. */
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserService service = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        User current = (User) session.getAttribute("loggedUser");
        String action = req.getParameter("action") == null ? "" : req.getParameter("action");

        try {
            switch (action) {
                case "update" -> {
                    User updated = service.updateProfile(current.getId(),
                            val(req, "fullName"), val(req, "phone"), val(req, "address"));
                    session.setAttribute("loggedUser", updated);
                    req.setAttribute("success", "Profile updated successfully.");
                }
                case "password" -> {
                    service.changePassword(current.getId(), req.getParameter("currentPassword"),
                            req.getParameter("newPassword"), req.getParameter("confirmPassword"));
                    req.setAttribute("success", "Password changed successfully.");
                }
                case "delete" -> {
                    if (current.getRole() == Role.ADMIN) {
                        throw new ValidationException("Admin accounts cannot delete themselves.");
                    }
                    service.deleteUser(current.getId());
                    session.invalidate();
                    resp.sendRedirect(req.getContextPath() + "/login?deleted=true");
                    return;
                }
                default -> throw new ValidationException("Unknown action.");
            }
        } catch (ValidationException e) {
            req.setAttribute("errors", e.getErrors());
        }
        req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
    }

    private static String val(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }
}