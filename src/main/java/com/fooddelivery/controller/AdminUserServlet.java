package com.fooddelivery.controller;

import com.fooddelivery.exception.ValidationException;
import com.fooddelivery.model.User;
import com.fooddelivery.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/** Admin: list / search / delete users. Access is guarded by AuthFilter. */
@WebServlet("/admin/users")
public class AdminUserServlet extends HttpServlet {

    private final UserService service = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = req.getParameter("q");
        req.setAttribute("users", service.search(q));
        req.setAttribute("q", q == null ? "" : q);
        req.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User me = (User) req.getSession().getAttribute("loggedUser");
        String id = req.getParameter("id");
        try {
            if (me.getId().equals(id)) {
                throw new ValidationException("You cannot delete your own account.");
            }
            service.deleteUser(id);
            req.setAttribute("success", "User " + id + " deleted.");
        } catch (ValidationException e) {
            req.setAttribute("errors", e.getErrors());
        }
        doGet(req, resp);
    }
}