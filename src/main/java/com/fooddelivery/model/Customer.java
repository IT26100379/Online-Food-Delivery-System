package com.fooddelivery.model;

public class Customer extends User {

    public Customer(String id, String fullName, String email, String phone,
                    String address, String salt, String passwordHash) {
        super(id, fullName, email, phone, address, salt, passwordHash);
    }

    @Override
    public Role getRole() {
        return Role.CUSTOMER;
    }

    @Override
    public String getHomePath() {
        return "/profile";
    }
}