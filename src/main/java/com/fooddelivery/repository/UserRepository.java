package com.fooddelivery.repository;

import com.fooddelivery.model.User;
import com.fooddelivery.util.FileHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** File based CRUD for users (users.txt in the data folder). */
public class UserRepository {

    private static final String FILE = "users.txt";
    private static final Object LOCK = new Object();

    public List<User> findAll() {
        synchronized (LOCK) {
            List<User> users = new ArrayList<>();
            for (String line : FileHandler.readLines(FILE)) {
                if (!line.isBlank()) {
                    users.add(User.fromFileLine(line));
                }
            }
            return users;
        }
    }

    public Optional<User> findById(String id) {
        return findAll().stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    public Optional<User> findByEmail(String email) {
        return findAll().stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst();
    }

    public void save(User user) {                       // Create
        synchronized (LOCK) {
            List<User> users = findAll();
            users.add(user);
            writeAll(users);
        }
    }

    public void update(User user) {                     // Update
        synchronized (LOCK) {
            List<User> users = findAll();
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getId().equals(user.getId())) {
                    users.set(i, user);
                    break;
                }
            }
            writeAll(users);
        }
    }

    public boolean deleteById(String id) {              // Delete
        synchronized (LOCK) {
            List<User> users = findAll();
            boolean removed = users.removeIf(u -> u.getId().equals(id));
            if (removed) {
                writeAll(users);
            }
            return removed;
        }
    }

    private void writeAll(List<User> users) {
        FileHandler.writeLines(FILE, users.stream().map(User::toFileLine).toList());
    }
}