package com.fooddelivery.model;

/**
 * Abstract base class for every user of the system (Abstraction + Inheritance).
 * File format (pipe separated):
 * ROLE|id|fullName|email|phone|address|salt|passwordHash
 */
public abstract class User {

    private String id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String salt;
    private String passwordHash;

    protected User(String id, String fullName, String email, String phone,
                   String address, String salt, String passwordHash) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.salt = salt;
        this.passwordHash = passwordHash;
    }

    /** Polymorphic: each subclass reports its own role. */
    public abstract Role getRole();

    /** Polymorphic: where the user lands after login (relative to context path). */
    public abstract String getHomePath();

    public String toFileLine() {
        return String.join("|", getRole().name(), id, fullName, email, phone, address, salt, passwordHash);
    }

    public static User fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length != 8) {
            throw new IllegalArgumentException("Corrupt user record: " + line);
        }
        Role role = Role.valueOf(p[0]);
        return switch (role) {
            case ADMIN -> new Admin(p[1], p[2], p[3], p[4], p[5], p[6], p[7]);
            case CUSTOMER -> new Customer(p[1], p[2], p[3], p[4], p[5], p[6], p[7]);
        };
    }

    // ---- getters / setters (Encapsulation) ----
    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getSalt() { return salt; }
    public String getPasswordHash() { return passwordHash; }

    public void setPassword(String salt, String passwordHash) {
        this.salt = salt;
        this.passwordHash = passwordHash;
    }
}