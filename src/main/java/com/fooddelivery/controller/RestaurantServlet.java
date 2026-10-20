package com.fooddelivery.controller;

import com.fooddelivery.model.FastFoodRestaurant;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.model.TraditionalRestaurant;
import com.fooddelivery.model.Role;
import com.fooddelivery.model.User;
import com.fooddelivery.service.RestaurantService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

/** Public restaurant catalog + admin-only CRUD; /admin/* is guarded by existing AuthFilter. */
@WebServlet(name = "RestaurantServlet", urlPatterns = {"/restaurants", "/admin/restaurants"})
public class RestaurantServlet extends HttpServlet {
    private static final String VIEW_DIR = "/WEB-INF/views/restaurant/";
    private static final String CSRF_KEY = "restaurantCsrfToken";
    private RestaurantService service;

    @Override
    public void init() {
        String dir = getServletContext().getInitParameter("fooddelivery.dataDir");
        if (dir == null || dir.isBlank()) dir = System.getProperty("fooddelivery.dataDir");
        Path dataDir = (dir == null || dir.isBlank())
                ? Path.of(System.getProperty("user.home"), "fooddelivery-data")
                : Path.of(dir);
        service = new RestaurantService(dataDir);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = param(req, "action");
        if (isAdminPath(req)) {
            if (!isAdmin(req)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            switch (action) {
                case "new" -> form(req, resp, blankRestaurant(), false);
                case "edit" -> {
                    Restaurant r = service.findById(param(req, "id")).orElse(null);
                    if (r == null) resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    else form(req, resp, r, true);
                }
                default -> showManage(req, resp);
            }
        } else {
            if ("view".equals(action)) {
                Restaurant r = service.findById(param(req, "id")).orElse(null);
                if (r == null) resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                else {
                    req.setAttribute("restaurant", r);
                    req.getRequestDispatcher(VIEW_DIR + "details.jsp").forward(req, resp);
                }
            } else {
                req.setAttribute("restaurants", service.search(param(req, "q")));
                req.setAttribute("query", param(req, "q"));
                req.getRequestDispatcher(VIEW_DIR + "list.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if (!isAdminPath(req) || !isAdmin(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        HttpSession session = req.getSession(false);
        String expected = session == null ? null : (String) session.getAttribute(CSRF_KEY);
        if (expected == null || !expected.equals(req.getParameter("csrfToken"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid form token");
            return;
        }
        switch (param(req, "action")) {
            case "create" -> save(req, resp, false);
            case "update" -> save(req, resp, true);
            case "delete" -> {
                boolean deleted = service.delete(param(req, "id"));
                redirect(req, resp, deleted ? "deleted" : "notfound");
            }
            default -> resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void save(HttpServletRequest req, HttpServletResponse resp, boolean editing)
            throws ServletException, IOException {
        String type = param(req, "type");
        Restaurant restaurant = readForm(req, type);
        List<String> errors = restaurant.validate();
        if (!type.equals("FAST_FOOD") && !type.equals("TRADITIONAL")) {
            errors.add("Please select a valid restaurant type.");
        }
        if (editing && service.findById(restaurant.getRestaurantId()).isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (errors.isEmpty()) {
            try {
                if (editing) {
                    service.update(restaurant);
                } else {
                    service.create(restaurant);
                }
                redirect(req, resp, editing ? "updated" : "created");
                return;
            } catch (IllegalArgumentException e) {
                errors.add(e.getMessage());
            }
        }
        req.setAttribute("errors", errors);
        form(req, resp, restaurant, editing);
    }

    private Restaurant readForm(HttpServletRequest req, String type) {
        String id = param(req, "restaurantId");
        String name = param(req, "name");
        String location = param(req, "location");
        String phone = param(req, "contactNumber");
        LocalTime opening = parseTime(param(req, "openingTime"));
        LocalTime closing = parseTime(param(req, "closingTime"));
        boolean available = "true".equals(param(req, "available"));
        if ("TRADITIONAL".equals(type)) {
            return new TraditionalRestaurant(id, name, location, phone, opening, closing, available);
        }
        return new FastFoodRestaurant(id, name, location, phone, opening, closing, available);
    }

    private static LocalTime parseTime(String value) {
        try { return LocalTime.parse(value); }
        catch (DateTimeParseException e) { return null; }
    }

    private static Restaurant blankRestaurant() {
        return new FastFoodRestaurant("", "", "", "", null, null, true);
    }

    private void form(HttpServletRequest req, HttpServletResponse resp, Restaurant restaurant, boolean editing)
            throws ServletException, IOException {
        req.setAttribute("restaurant", restaurant);
        req.setAttribute("editing", editing);
        req.setAttribute("csrfToken", csrf(req));
        req.getRequestDispatcher(VIEW_DIR + "form.jsp").forward(req, resp);
    }

    private void showManage(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("restaurants", service.search(param(req, "q")));
        req.setAttribute("query", param(req, "q"));
        req.setAttribute("csrfToken", csrf(req));
        req.setAttribute("msg", param(req, "msg"));
        req.getRequestDispatcher(VIEW_DIR + "manage.jsp").forward(req, resp);
    }

    private static String csrf(HttpServletRequest req) {
        HttpSession session = req.getSession();
        String token = (String) session.getAttribute(CSRF_KEY);
        if (token == null) {
            token = UUID.randomUUID().toString();
            session.setAttribute(CSRF_KEY, token);
        }
        return token;
    }

    private static boolean isAdminPath(HttpServletRequest req) {
        return req.getServletPath().startsWith("/admin/");
    }

    private static boolean isAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return false;
        Object current = session.getAttribute("loggedUser");
        return current instanceof User user && user.getRole() == Role.ADMIN;
    }

    private static String param(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        return value == null ? "" : value.trim();
    }

    private static void redirect(HttpServletRequest req, HttpServletResponse resp, String msg)
            throws IOException {
        resp.sendRedirect(req.getContextPath() + "/admin/restaurants?msg=" + msg);
    }
}
