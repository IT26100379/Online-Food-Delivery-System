package com.fooddelivery.model;

public class Admin extends User {

    public Admin(String id, String fullName, String email, String phone,
                 String address, String salt, String passwordHash) {
        super(id, fullName, email, phone, address, salt, passwordHash);
    }

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }

    @Override
    public String getHomePath() {
        return "/admin/users";
    }
}