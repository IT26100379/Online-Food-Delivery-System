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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Customer side of Review Management.
 * GET  /reviews?action=list|mine|new|edit   (show pages)
 * POST /reviews  action=create|update|delete (change data)
 */
@WebServlet("/reviews")
public class ReviewServlet extends HttpServlet {

    private static final String VIEW_DIR = "/WEB-INF/views/review/";

    // When Food Management is merged, only this line changes.
    private final ReviewService reviewService = new ReviewService(new SampleFoodCatalog());
    private final UserService userService = new UserService();

    // ---------------------------------------------------------------- GET
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = requireLogin(req, resp);
        if (user == null) {
            return;
        }
        moveMessagesToRequest(req);

        String action = req.getParameter("action");
        if (action == null || action.isBlank()) {
            action = "list";
        }
        switch (action) {
            case "list" -> showList(req, resp);
            case "mine" -> showMine(req, resp, user);
            case "new" -> showAddForm(req, resp, user);
            case "edit" -> showEditForm(req, resp, user);
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String foodId = req.getParameter("foodId");
        String selected = foodId == null ? "" : foodId.trim();
        List<Review> reviews = selected.isEmpty()
                ? reviewService.getAllReviews()
                : reviewService.getReviewsByFood(selected);
        req.setAttribute("selectedFoodId", selected);
        setListAttributes(req, reviews);
        forward(req, resp, "reviewList.jsp");
    }

    private void showMine(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        setListAttributes(req, reviewService.getReviewsByCustomer(user.getId()));
        forward(req, resp, "myReviews.jsp");
    }

    private void showAddForm(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        if (user.getRole() != Role.CUSTOMER) {
            redirectWithError(req, resp, "list", "Only customers can write reviews.");
            return;
        }
        String foodId = req.getParameter("foodId");
        req.setAttribute("formFoodId", foodId == null ? "" : foodId.trim());
        req.setAttribute("formRating", "");
        req.setAttribute("formComment", "");
        req.setAttribute("foodNames", reviewService.getFoodNames());
        forward(req, resp, "addReview.jsp");
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        Review review = findReviewOrNull(req.getParameter("id"));
        if (review == null) {
            redirectWithError(req, resp, "mine", "Review not found.");
            return;
        }
        if (!review.getCustomerId().equals(user.getId())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You can only edit your own reviews.");
            return;
        }
        req.setAttribute("review", review);
        req.setAttribute("foodNames", reviewService.getFoodNames());
        req.setAttribute("formRating", String.valueOf(review.getRating()));
        req.setAttribute("formComment", review.getComment());
        forward(req, resp, "editReview.jsp");
    }

    // --------------------------------------------------------------- POST
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");   // must run before any getParameter (Sinhala / emoji comments)

        User user = requireLogin(req, resp);
        if (user == null) {
            return;
        }
        String action = req.getParameter("action");
        if (action == null) {
            action = "";
        }
        switch (action) {
            case "create" -> createReview(req, resp, user);
            case "update" -> updateReview(req, resp, user);
            case "delete" -> deleteReview(req, resp, user);
            default -> resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void createReview(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        String foodId = req.getParameter("foodId");
        String rating = req.getParameter("rating");
        String comment = req.getParameter("comment");
        try {
            // the owner is the logged-in user from the session, never a form field
            reviewService.addReview(user, foodId, rating, comment);
            redirectWithSuccess(req, resp, "mine", "Your review was added.");
        } catch (ValidationException e) {
            req.setAttribute("errors", e.getErrors());
            req.setAttribute("formFoodId", foodId == null ? "" : foodId);
            req.setAttribute("formRating", rating == null ? "" : rating);
            req.setAttribute("formComment", comment == null ? "" : comment);
            req.setAttribute("foodNames", reviewService.getFoodNames());
            forward(req, resp, "addReview.jsp");
        }
    }

    private void updateReview(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        String reviewId = req.getParameter("reviewId");
        String rating = req.getParameter("rating");
        String comment = req.getParameter("comment");
        try {
            reviewService.updateReview(user, reviewId, rating, comment);
            redirectWithSuccess(req, resp, "mine", "Your review was updated.");
        } catch (ValidationException e) {
            Review review = findReviewOrNull(reviewId);
            if (review == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Review not found.");
                return;
            }
            if (!review.getCustomerId().equals(user.getId())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You can only edit your own reviews.");
                return;
            }
            req.setAttribute("errors", e.getErrors());
            req.setAttribute("review", review);
            req.setAttribute("formRating", rating == null ? "" : rating);
            req.setAttribute("formComment", comment == null ? "" : comment);
            req.setAttribute("foodNames", reviewService.getFoodNames());
            forward(req, resp, "editReview.jsp");
        }
    }

    private void deleteReview(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        String back = user.getRole() == Role.ADMIN ? "list" : "mine";
        try {
            reviewService.deleteReview(user, req.getParameter("reviewId"));
            redirectWithSuccess(req, resp, back, "The review was deleted.");
        } catch (ValidationException e) {
            redirectWithError(req, resp, back, e.getMessage());
        }
    }

    // ------------------------------------------------------------ helpers
    private User requireLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("loggedUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
        }
        return user;
    }

    private Review findReviewOrNull(String reviewId) {
        try {
            return reviewService.getReviewById(reviewId);
        } catch (ValidationException e) {
            return null;
        }
    }

    private void setListAttributes(HttpServletRequest req, List<Review> reviews) {
        req.setAttribute("reviews", reviews);
        req.setAttribute("foodNames", reviewService.getFoodNames());
        req.setAttribute("userNames", loadUserNames());
    }

    private Map<String, String> loadUserNames() {
        Map<String, String> names = new LinkedHashMap<>();
        for (User u : userService.getAll()) {
            names.put(u.getId(), u.getFullName());   // only id and name, never email or password data
        }
        return names;
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        req.getRequestDispatcher(VIEW_DIR + view).forward(req, resp);
    }

    // Post/Redirect/Get: keep a one-time message in the session, then redirect.
    private void redirectWithSuccess(HttpServletRequest req, HttpServletResponse resp, String action, String message)
            throws IOException {
        req.getSession().setAttribute("reviewSuccess", message);
        resp.sendRedirect(req.getContextPath() + "/reviews?action=" + action);
    }

    private void redirectWithError(HttpServletRequest req, HttpServletResponse resp, String action, String message)
            throws IOException {
        req.getSession().setAttribute("reviewError", message);
        resp.sendRedirect(req.getContextPath() + "/reviews?action=" + action);
    }

    private void moveMessagesToRequest(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return;
        }
        Object success = session.getAttribute("reviewSuccess");
        if (success != null) {
            req.setAttribute("success", success);                    // messages.jsp reads "success"
            session.removeAttribute("reviewSuccess");                // show it only once
        }
        Object error = session.getAttribute("reviewError");
        if (error != null) {
            req.setAttribute("errors", List.of(error.toString()));   // messages.jsp reads "errors" (a list)
            session.removeAttribute("reviewError");
        }
    }
}