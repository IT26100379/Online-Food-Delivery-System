package com.fooddelivery.controller;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.User;
import com.fooddelivery.model.Role;
import com.fooddelivery.service.OrderService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {

    private OrderService service;

    @Override
    public void init() {
        String path = System.getProperty("user.home") + "/fooddelivery-data/orders.txt";
        service = new OrderService(path);
    }

    // Admin permission check
    private boolean requireAdmin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession(false);

        User user = session == null
                ? null
                : (User) session.getAttribute("loggedUser");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return false;
        }

        if (user.getRole() != Role.ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Access Denied! Admins Only.");
            return false;
        }

        return true;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if (action == null) action = "list";

        switch (action) {

            case "new":
                if (!requireAdmin(req, resp)) return;

                req.getRequestDispatcher("/WEB-INF/views/order-form.jsp")
                        .forward(req, resp);
                break;

            case "edit":
                if (!requireAdmin(req, resp)) return;

                // Keep the existing form route.
                req.getRequestDispatcher("/WEB-INF/views/order-form.jsp")
                        .forward(req, resp);
                break;

            case "delete":
                // Delete should never happen through GET.
                resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                        "Delete must use POST.");
                break;

            case "list":
                req.setAttribute("orders", service.getAllOrders());
                req.getRequestDispatcher("/WEB-INF/views/order-list.jsp")
                        .forward(req, resp);
                break;

            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid action.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!requireAdmin(req, resp)) return;

        String action = req.getParameter("action");

        if ("delete".equals(action)) {
            String id = req.getParameter("id");

            if (id == null || id.isBlank()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Order ID is required.");
                return;
            }

            service.deleteOrder(id);
            resp.sendRedirect(req.getContextPath() + "/orders");
            return;
        }

        String id = req.getParameter("orderId");

        Order order = new Order(
                id,
                req.getParameter("userId"),
                req.getParameter("foodId"),
                Integer.parseInt(req.getParameter("quantity")),
                Double.parseDouble(req.getParameter("totalPrice")),
                req.getParameter("status"),
                LocalDate.now()
        );

        if (id == null || id.trim().isEmpty()) {
            service.createOrder(order);
        } else {
            service.updateOrder(order);
        }

        resp.sendRedirect(req.getContextPath() + "/orders");
    }
}