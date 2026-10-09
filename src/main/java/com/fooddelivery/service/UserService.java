package com.fooddelivery.service;

import com.fooddelivery.exception.ValidationException;
import com.fooddelivery.model.Admin;
import com.fooddelivery.model.Customer;
import com.fooddelivery.model.User;
import com.fooddelivery.repository.UserRepository;
import com.fooddelivery.util.IdGenerator;
import com.fooddelivery.util.PasswordUtil;
import com.fooddelivery.util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserService {

    private static final Object WRITE_LOCK = new Object();
    private static final String DEFAULT_ADMIN_EMAIL = "admin@fooddelivery.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "Admin@123";

    private final UserRepository repo = new UserRepository();

    /** Creates a default admin the first time the app runs. Change its password afterwards! */
    public void ensureDefaultAdmin() {
        synchronized (WRITE_LOCK) {
            if (repo.findAll().stream().noneMatch(u -> u instanceof Admin)) {
                String salt = PasswordUtil.generateSalt();
                String id = IdGenerator.next("U", ids());
                repo.save(new Admin(id, "System Admin", DEFAULT_ADMIN_EMAIL, "0710000000",
                        "Head Office", salt, PasswordUtil.hash(DEFAULT_ADMIN_PASSWORD, salt)));
            }
        }
    }

    // ---------- Create ----------
    public User register(String fullName, String email, String phone, String address,
                         String password, String confirmPassword) {
        List<String> errors = new ArrayList<>();
        ValidationUtil.validateProfile(fullName, phone, address, errors);
        if (!ValidationUtil.isValidEmail(email)) {
            errors.add("Enter a valid email address.");
        }
        ValidationUtil.validatePassword(password, errors);
        if (password != null && !password.equals(confirmPassword)) {
            errors.add("Passwords do not match.");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        synchronized (WRITE_LOCK) {
            if (repo.findByEmail(email).isPresent()) {
                throw new ValidationException("This email is already registered.");
            }
            String salt = PasswordUtil.generateSalt();
            User user = new Customer(IdGenerator.next("U", ids()), fullName, email.toLowerCase(Locale.ROOT),
                    phone, address, salt, PasswordUtil.hash(password, salt));
            repo.save(user);
            return user;
        }
    }

    // ---------- Login ----------
    public User login(String email, String password) {
        User user = repo.findByEmail(email == null ? "" : email).orElse(null);
        if (user == null || password == null
                || !PasswordUtil.matches(password, user.getSalt(), user.getPasswordHash())) {
            throw new ValidationException("Invalid email or password.");
        }
        return user;
    }

    // ---------- Read ----------
    public User getById(String id) {
        return repo.findById(id).orElseThrow(() -> new ValidationException("User not found."));
    }

    public List<User> getAll() {
        return repo.findAll();
    }

    public List<User> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAll();
        }
        String k = keyword.trim().toLowerCase(Locale.ROOT);
        return repo.findAll().stream()
                .filter(u -> u.getId().toLowerCase(Locale.ROOT).contains(k)
                        || u.getFullName().toLowerCase(Locale.ROOT).contains(k)
                        || u.getEmail().toLowerCase(Locale.ROOT).contains(k))
                .toList();
    }

    // ---------- Update ----------
    public User updateProfile(String id, String fullName, String phone, String address) {
        List<String> errors = new ArrayList<>();
        ValidationUtil.validateProfile(fullName, phone, address, errors);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        synchronized (WRITE_LOCK) {
            User user = getById(id);
            user.setFullName(fullName);
            user.setPhone(phone);
            user.setAddress(address);
            repo.update(user);
            return user;
        }
    }

    public void changePassword(String id, String current, String newPassword, String confirm) {
        List<String> errors = new ArrayList<>();
        ValidationUtil.validatePassword(newPassword, errors);
        if (newPassword != null && !newPassword.equals(confirm)) {
            errors.add("New passwords do not match.");
        }
        synchronized (WRITE_LOCK) {
            User user = getById(id);
            if (current == null || !PasswordUtil.matches(current, user.getSalt(), user.getPasswordHash())) {
                errors.add("Current password is incorrect.");
            }
            if (!errors.isEmpty()) {
                throw new ValidationException(errors);
            }
            String salt = PasswordUtil.generateSalt();
            user.setPassword(salt, PasswordUtil.hash(newPassword, salt));
            repo.update(user);
        }
    }

    // ---------- Delete ----------
    public void deleteUser(String id) {
        synchronized (WRITE_LOCK) {
            if (!repo.deleteById(id)) {
                throw new ValidationException("User not found.");
            }
        }
    }

    private List<String> ids() {
        return repo.findAll().stream().map(User::getId).toList();
    }
}