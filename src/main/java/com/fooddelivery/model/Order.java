package com.fooddelivery.model;

import java.time.LocalDate;

public class Order {
    private String orderId;     // O001
    private String userId;      // U001
    private String foodId;      // F001
    private int quantity;
    private double totalPrice;
    private String status;      // PLACED, PREPARING, DELIVERED, CANCELLED
    private LocalDate orderDate;

    public Order() {}

    public Order(String orderId, String userId, String foodId, int quantity,
                 double totalPrice, String status, LocalDate orderDate) {
        this.orderId = orderId;
        this.userId = userId;
        this.foodId = foodId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = status;
        this.orderDate = orderDate;
    }

    public String toFileString() {
        return String.join("|", orderId, userId, foodId, String.valueOf(quantity),
                String.valueOf(totalPrice), status, orderDate.toString());
    }

    public static Order fromFileString(String line) {
        String[] p = line.split("\\|");
        return new Order(p[0], p[1], p[2], Integer.parseInt(p[3]),
                Double.parseDouble(p[4]), p[5], LocalDate.parse(p[6]));
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getFoodId() { return foodId; }
    public void setFoodId(String foodId) { this.foodId = foodId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
}