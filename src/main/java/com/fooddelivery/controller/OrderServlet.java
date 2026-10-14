package com.fooddelivery.controller;



import com.fooddelivery.model.Order;
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

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            String action = req.getParameter("action");
            if (action == null) action = "list";

            switch (action) {
                case "new":
                    req.getRequestDispatcher("/WEB-INF/views/order-form.jsp").forward(req, resp);
                    break;

                case "delete":
                    service.deleteOrder(req.getParameter("id"));
                    resp.sendRedirect(req.getContextPath() + "/orders");
                    break;
                default:
                    req.setAttribute("orders", service.getAllOrders());
                    req.getRequestDispatcher("/WEB-INF/views/order-list.jsp")
                            .forward(req, resp);
                    break;
            }
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            String id = req.getParameter("orderId");
            Order o = new Order(
                    id,
                    req.getParameter("userId"),
                    req.getParameter("foodId"),
                    Integer.parseInt(req.getParameter("quantity")),
                    Double.parseDouble(req.getParameter("totalPrice")),
                    req.getParameter("status"),
                    LocalDate.now());

            if (id == null || id.trim().isEmpty()) {
                service.createOrder(o);
            } else {
                service.updateOrder(o);
            }

            resp.sendRedirect(req.getContextPath() + "/orders");

    }
}
