package com.fooddelivery.model;

public class PublicReview extends Review {

    public PublicReview(String reviewId, String foodId, String customerId,
                        int rating, String comment, String createdAt, String updatedAt) {
        super(reviewId, foodId, customerId, rating, comment, createdAt, updatedAt);
    }

    @Override
    public String getReviewType() {
        return "PUBLIC";
    }

    @Override
    public String getBadgeLabel() {
        return "Customer Review";
    }
}