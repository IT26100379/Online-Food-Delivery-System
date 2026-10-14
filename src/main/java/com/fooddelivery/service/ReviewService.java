package com.fooddelivery.service;

import com.fooddelivery.exception.ValidationException;
import com.fooddelivery.model.PublicReview;
import com.fooddelivery.model.Review;
import com.fooddelivery.model.Role;
import com.fooddelivery.model.User;
import com.fooddelivery.repository.ReviewRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ReviewService {

    private static final int MAX_COMMENT_LENGTH = 500;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Object WRITE_LOCK = new Object();

    private final ReviewRepository repo = new ReviewRepository();
    private final FoodCatalog foodCatalog;

    public ReviewService(FoodCatalog foodCatalog) {
        this.foodCatalog = foodCatalog;
    }

    // ---------- Create ----------
    public Review addReview(User user, String foodId, String ratingText, String comment) {
        requireLoggedIn(user);
        if (user.getRole() != Role.CUSTOMER) {
            throw new ValidationException("Only customers can write reviews.");
        }

        String cleanFoodId = foodId == null ? "" : foodId.trim();
        String cleanComment = comment == null ? "" : comment.trim();
        int rating = parseRating(ratingText);

        List<String> errors = new ArrayList<>();
        if (!foodCatalog.getFoodNames().containsKey(cleanFoodId)) {
            errors.add("Please choose a valid food item.");
        }
        validateRatingAndComment(rating, cleanComment, errors);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        synchronized (WRITE_LOCK) {
            String now = LocalDateTime.now().format(TIME_FORMAT);
            // No Order data is available, so every review is a PublicReview (never "verified").
            Review review = new PublicReview(repo.nextId(), cleanFoodId, user.getId(),
                    rating, cleanComment, now, now);
            repo.save(review);
            return review;
        }
    }

    // ---------- Read ----------
    public List<Review> getAllReviews() {
        return newestFirst(repo.findAll());
    }

    public List<Review> getReviewsByCustomer(String customerId) {
        return newestFirst(repo.findByCustomerId(customerId == null ? "" : customerId));
    }

    public List<Review> getReviewsByFood(String foodId) {
        return newestFirst(repo.findByFoodId(foodId == null ? "" : foodId));
    }

    public Review getReviewById(String reviewId) {
        return repo.findById(reviewId == null ? "" : reviewId)
                .orElseThrow(() -> new ValidationException("Review not found."));
    }

    public Map<String, String> getFoodNames() {
        return foodCatalog.getFoodNames();
    }

    // ---------- Update (owner only, even admins cannot edit others' reviews) ----------
    public Review updateReview(User user, String reviewId, String ratingText, String comment) {
        requireLoggedIn(user);

        String cleanComment = comment == null ? "" : comment.trim();
        int rating = parseRating(ratingText);

        List<String> errors = new ArrayList<>();
        validateRatingAndComment(rating, cleanComment, errors);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        synchronized (WRITE_LOCK) {
            Review review = getReviewById(reviewId);
            if (!review.getCustomerId().equals(user.getId())) {
                throw new ValidationException("You can only edit your own reviews.");
            }
            review.setRating(rating);
            review.setComment(cleanComment);
            review.setUpdatedAt(LocalDateTime.now().format(TIME_FORMAT));
            repo.update(review);
            return review;
        }
    }

    // ---------- Delete (owner, or admin for moderation) ----------
    public void deleteReview(User user, String reviewId) {
        requireLoggedIn(user);

        synchronized (WRITE_LOCK) {
            Review review = getReviewById(reviewId);
            boolean isOwner = review.getCustomerId().equals(user.getId());
            boolean isAdmin = user.getRole() == Role.ADMIN;
            if (!isOwner && !isAdmin) {
                throw new ValidationException("You can only delete your own reviews.");
            }
            repo.deleteById(review.getReviewId());
        }
    }

    // ---------- Helpers ----------
    private static void requireLoggedIn(User user) {
        if (user == null) {
            throw new ValidationException("Please log in first.");
        }
    }

    private static int parseRating(String ratingText) {
        try {
            return Integer.parseInt(ratingText == null ? "" : ratingText.trim());
        } catch (NumberFormatException e) {
            return 0;   // 0 is outside 1-5, so validation will reject it
        }
    }

    private static void validateRatingAndComment(int rating, String comment, List<String> errors) {
        if (rating < 1 || rating > 5) {
            errors.add("Rating must be a whole number from 1 to 5.");
        }
        if (comment.isBlank()) {
            errors.add("Comment cannot be empty.");
        } else if (comment.length() > MAX_COMMENT_LENGTH) {
            errors.add("Comment must be " + MAX_COMMENT_LENGTH + " characters or fewer.");
        }
    }

    private static List<Review> newestFirst(List<Review> source) {
        List<Review> copy = new ArrayList<>(source);
        Collections.reverse(copy);   // reviews.txt is in creation order, so reverse = newest first
        return copy;
    }
}