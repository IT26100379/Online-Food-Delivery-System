package com.fooddelivery.model;

public class Admin extends Person {
    private String adminId;       // A001 wage ID eka
    private String username;
    private String passwordHash;
    private String adminLevel;    // SUPER_ADMIN or MODERATOR

    public Admin(String adminId, String name, String email, String phone,
                 String username, String passwordHash, String adminLevel) {
        super(name, email, phone);
        this.adminId = adminId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.adminLevel = adminLevel;
    }

    public String getAdminId() { return adminId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getAdminLevel() { return adminLevel; }
    public void setAdminLevel(String adminLevel) { this.adminLevel = adminLevel; }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    // File ekata liyanna: eka line ekak = eka admin kenek
    public String toFileString() {
        return String.join("|", adminId, getName(), getEmail(), getPhone(),
                username, passwordHash, adminLevel);
    }

    // File eken line ekak kiyawala Admin object ekak hadanna
    public static Admin fromFileString(String line) {
        String[] p = line.split("\\|");
        return new Admin(p[0], p[1], p[2], p[3], p[4], p[5], p[6]);
    }
}