package com.fooddelivery.controller;

import com.fooddelivery.model.FoodItem;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.Role;
import com.fooddelivery.model.User;
import com.fooddelivery.service.FoodService;
import com.fooddelivery.service.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {
    private OrderService service;
    private FoodService foods;

    @Override public void init() {
        service = new OrderService(System.getProperty("user.home") + "/fooddelivery-data/orders.txt");
        foods = new FoodService(getServletContext().getRealPath("/WEB-INF/data/foods.txt"));
    }

    private User user(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        Object u = s == null ? null : s.getAttribute("loggedUser");
        return u instanceof User ? (User) u : null;
    }
    private boolean requireLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (user(req) != null) return true;
        resp.sendRedirect(req.getContextPath() + "/login");
        return false;
    }
    private boolean requireAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!requireLogin(req, resp)) return false;
        if (user(req).getRole() == Role.ADMIN) return true;
        resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins only");
        return false;
    }
    private String token(HttpServletRequest req) {
        HttpSession s = req.getSession();
        String v = (String) s.getAttribute("orderCsrf");
        if (v == null) { v = UUID.randomUUID().toString(); s.setAttribute("orderCsrf", v); }
        return v;
    }
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!requireLogin(req, resp)) return;
        String action = req.getParameter("action");
        if (action == null || action.isBlank()) action = "list";
        User u = user(req);
        switch (action) {
            case "list": {
                List<Order> all = service.getAllOrders();
                req.setAttribute("orders", u.getRole() == Role.ADMIN ? all :
                        all.stream().filter(o -> u.getId().equals(o.getUserId())).collect(Collectors.toList()));
                req.getRequestDispatcher("/WEB-INF/views/order-list.jsp").forward(req, resp);
                return;
            }
            case "place": {
                if (u.getRole() != Role.CUSTOMER) { resp.sendError(403); return; }
                String id = req.getParameter("foodId");
                FoodItem selected = id == null ? null : foods.getFoodById(id);
                if (selected == null || !selected.isAvailable()) {
                    resp.sendError(404, "Food is unavailable"); return;
                }
                req.setAttribute("food", selected);
                req.setAttribute("csrf", token(req));
                req.getRequestDispatcher("/WEB-INF/views/customer-order.jsp").forward(req, resp);
                return;
            }
            case "new": {
                if (!requireAdmin(req, resp)) return;
                req.getRequestDispatcher("/WEB-INF/views/order-form.jsp").forward(req, resp);
                return;
            }
            case "edit": {
                if (!requireAdmin(req, resp)) return;
                Optional<Order> selected = service.getOrderById(req.getParameter("id"));
                if (selected.isEmpty()) { resp.sendError(404); return; }
                req.setAttribute("order", selected.get());
                req.getRequestDispatcher("/WEB-INF/views/order-form.jsp").forward(req, resp);
                return;
            }
            case "delete": resp.sendError(405, "Use POST"); return;
            default: resp.sendError(400, "Unknown action");
        }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!requireLogin(req, resp)) return;
        User u = user(req);
        String action = req.getParameter("action");
        if ("place".equals(action)) {
            if (u.getRole() != Role.CUSTOMER) { resp.sendError(403); return; }
            String provided = req.getParameter("csrf");
            String expected = (String) req.getSession().getAttribute("orderCsrf");
            if (expected == null || !expected.equals(provided)) { resp.sendError(403, "Invalid request token"); return; }
            String foodId = req.getParameter("foodId");
            FoodItem selected = foodId == null ? null : foods.getFoodById(foodId);
            if (selected == null || !selected.isAvailable()) { resp.sendError(400, "Food is unavailable"); return; }
            int quantity;
            try { quantity = Integer.parseInt(req.getParameter("quantity")); }
            catch (NumberFormatException e) { resp.sendError(400, "Invalid quantity"); return; }
            if (quantity < 1 || quantity > 100) { resp.sendError(400, "Quantity must be 1 to 100"); return; }
            double total = Math.round(selected.getPrice() * quantity * 100.0) / 100.0;
            if (!Double.isFinite(total) || total < 0) { resp.sendError(400, "Invalid price"); return; }
            service.createOrder(new Order(null, u.getId(), selected.getFoodId(), quantity, total,
                    "PLACED", LocalDate.now()));
            req.getSession().removeAttribute("orderCsrf");
            resp.sendRedirect(req.getContextPath() + "/orders?placed=1");
            return;
        }
        if (!requireAdmin(req, resp)) return;
        if ("delete".equals(action)) {
            String id = req.getParameter("id");
            if (id == null || id.isBlank()) { resp.sendError(400); return; }
            service.deleteOrder(id);
        } else {
            String id = req.getParameter("orderId");
            String userId = req.getParameter("userId");
            String foodId = req.getParameter("foodId");
            String status = req.getParameter("status");
            int qty; double total;
            try {
                qty = Integer.parseInt(req.getParameter("quantity"));
                total = Double.parseDouble(req.getParameter("totalPrice"));
            } catch (NumberFormatException e) { resp.sendError(400, "Invalid number"); return; }
            if (qty < 1 || qty > 100 || !Double.isFinite(total) || total < 0 ||
                    userId == null || userId.isBlank() || foodId == null || foodId.isBlank() ||
                    status == null || !List.of("PLACED", "PREPARING", "DELIVERED", "CANCELLED").contains(status)) {
                resp.sendError(400, "Invalid order"); return;
            }
            if (id == null || id.isBlank()) {
                service.createOrder(new Order(null, userId, foodId, qty, total, status, LocalDate.now()));
            } else {
                Optional<Order> old = service.getOrderById(id);
                if (old.isEmpty()) { resp.sendError(404); return; }
                service.updateOrder(new Order(id, userId, foodId, qty, total, status, old.get().getOrderDate()));
            }
        }
        resp.sendRedirect(req.getContextPath() + "/orders");
    }
}
