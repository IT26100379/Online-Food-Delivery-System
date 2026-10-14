package com.fooddelivery.controller;

import com.fooddelivery.exception.ValidationException;
import com.fooddelivery.model.Review;
import com.fooddelivery.model.Role;
import com.fooddelivery.model.User;
import com.fooddelivery.service.ReviewService;
import com.fooddelivery.service.SampleFoodCatalog;
import com.fooddelivery.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin side of Review Management: list all reviews and delete inappropriate ones.
 * AuthFilter already protects /admin/*, and this servlet checks the role again (defense in depth).
 */
@WebServlet("/admin/reviews")
public class AdminReviewServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/admin/reviews.jsp";

    // When Food Management is merged, change this line (and the same line in ReviewServlet).
    private final ReviewService reviewService = new ReviewService(new SampleFoodCatalog());
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = requireAdmin(req, resp);
        if (admin == null) {
            return;
        }
        moveMessagesToRequest(req);

        String foodId = cleanFoodId(req);
        List<Review> reviews = foodId.isEmpty()
                ? reviewService.getAllReviews()
                : reviewService.getReviewsByFood(foodId);

        req.setAttribute("reviews", reviews);
        req.setAttribute("selectedFoodId", foodId);
        req.setAttribute("foodNames", reviewService.getFoodNames());
        req.setAttribute("userNames", loadUserNames());
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = requireAdmin(req, resp);
        if (admin == null) {
            return;
        }
        if (!"delete".equals(req.getParameter("action"))) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            // ReviewService checks again that this user is the owner or an admin
            reviewService.deleteReview(admin, req.getParameter("reviewId"));
            req.getSession().setAttribute("reviewSuccess", "The review was deleted.");
        } catch (ValidationException e) {
            req.getSession().setAttribute("reviewError", e.getMessage());
        }

        String back = req.getContextPath() + "/admin/reviews";
        String foodId = cleanFoodId(req);
        if (!foodId.isEmpty()) {
            back += "?foodId=" + URLEncoder.encode(foodId, StandardCharsets.UTF_8);
        }
        resp.sendRedirect(back);
    }

    // ------------------------------------------------------------ helpers
    private User requireAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("loggedUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        if (user.getRole() != Role.ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins only");
            return null;
        }
        return user;
    }

    private String cleanFoodId(HttpServletRequest req) {
        String foodId = req.getParameter("foodId");
        return foodId == null ? "" : foodId.trim();
    }

    private Map<String, String> loadUserNames() {
        Map<String, String> names = new LinkedHashMap<>();
        for (User u : userService.getAll()) {
            names.put(u.getId(), u.getFullName());   // only id and name
        }
        return names;
    }

    // messages.jsp reads "success" (String) and "errors" (List)
    private void moveMessagesToRequest(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return;
        }
        Object success = session.getAttribute("reviewSuccess");
        if (success != null) {
            req.setAttribute("success", success);
            session.removeAttribute("reviewSuccess");
        }
        Object error = session.getAttribute("reviewError");
        if (error != null) {
            req.setAttribute("errors", List.of(error.toString()));
            session.removeAttribute("reviewError");
        }
    }
}