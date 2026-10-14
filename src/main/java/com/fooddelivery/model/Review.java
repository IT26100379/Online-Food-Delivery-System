package com.fooddelivery.model;

public abstract class Review {
    private String reviewId;
    private String foodId;
    private String customerId;
    private int rating;
    private String comment;
    private String createdAt;
    private String updatedAt;

    public Review(String reviewId, String foodId, String customerId,
                  int rating, String comment, String createdAt, String updatedAt) {
        this.reviewId = reviewId;
        this.foodId = foodId;
        this.customerId = customerId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }

    public String getFoodId() { return foodId; }
    public void setFoodId(String foodId) { this.foodId = foodId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // reviews.txt එකේ save වෙන අගය (PUBLIC / VERIFIED)
    public abstract String getReviewType();

    // web page එකේ පෙන්වන text එක
    public abstract String getBadgeLabel();

    // rating එක ★★★★☆ වගේ පෙන්වන method එක
    public String getStars() {
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            stars.append(i <= rating ? "\u2605" : "\u2606");
        }
        return stars.toString();
    }
}