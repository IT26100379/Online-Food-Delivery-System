package com.fooddelivery.model;

public class VerifiedReview extends Review {

    public VerifiedReview(String reviewId, String foodId, String customerId,
                          int rating, String comment, String createdAt, String updatedAt) {
        super(reviewId, foodId, customerId, rating, comment, createdAt, updatedAt);
    }

    @Override
    public String getReviewType() {
        return "VERIFIED";
    }

    @Override
    public String getBadgeLabel() {
        return "Verified Purchase";
    }
}