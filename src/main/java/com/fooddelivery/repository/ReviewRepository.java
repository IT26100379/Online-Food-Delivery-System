package com.fooddelivery.repository;

import com.fooddelivery.model.PublicReview;
import com.fooddelivery.model.Review;
import com.fooddelivery.model.VerifiedReview;
import com.fooddelivery.util.FileHandler;
import com.fooddelivery.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * File based CRUD for reviews (reviews.txt in the data folder).
 * Line format: reviewId|foodId|customerId|rating|comment|TYPE|createdAt|updatedAt
 * Backslash, pipe and new lines are escaped so that one review is always exactly one line.
 */
public class ReviewRepository {

    private static final String FILE = "reviews.txt";
    private static final int FIELD_COUNT = 8;
    private static final Object LOCK = new Object();

    // ---------- Read ----------
    public List<Review> findAll() {
        synchronized (LOCK) {
            List<Review> reviews = new ArrayList<>();
            for (String line : FileHandler.readLines(FILE)) {
                Review review = parse(line);
                if (review != null) {          // malformed lines are skipped
                    reviews.add(review);
                }
            }
            return reviews;
        }
    }

    public Optional<Review> findById(String reviewId) {
        return findAll().stream().filter(r -> r.getReviewId().equals(reviewId)).findFirst();
    }

    public List<Review> findByCustomerId(String customerId) {
        return findAll().stream().filter(r -> r.getCustomerId().equals(customerId)).toList();
    }

    public List<Review> findByFoodId(String foodId) {
        return findAll().stream().filter(r -> r.getFoodId().equals(foodId)).toList();
    }

    /** Next free ID such as R001, R002 ... (call inside the service's lock together with save). */
    public String nextId() {
        List<String> ids = findAll().stream().map(Review::getReviewId).toList();
        return IdGenerator.next("R", ids);
    }

    // ---------- Create ----------
    public void save(Review review) {
        synchronized (LOCK) {
            if (findById(review.getReviewId()).isPresent()) {
                throw new IllegalStateException("Review ID already exists: " + review.getReviewId());
            }
            List<String> lines = readNonBlankLines();
            lines.add(toFileLine(review));
            FileHandler.writeLines(FILE, lines);
        }
    }

    // ---------- Update ----------
    public boolean update(Review review) {
        synchronized (LOCK) {
            List<String> result = new ArrayList<>();
            boolean found = false;
            for (String line : readNonBlankLines()) {
                Review existing = parse(line);
                if (existing != null && existing.getReviewId().equals(review.getReviewId())) {
                    result.add(toFileLine(review));
                    found = true;
                } else {
                    result.add(line);          // other lines stay untouched, even malformed ones
                }
            }
            if (found) {
                FileHandler.writeLines(FILE, result);
            }
            return found;
        }
    }

    // ---------- Delete ----------
    public boolean deleteById(String reviewId) {
        synchronized (LOCK) {
            List<String> result = new ArrayList<>();
            boolean removed = false;
            for (String line : readNonBlankLines()) {
                Review existing = parse(line);
                if (existing != null && existing.getReviewId().equals(reviewId)) {
                    removed = true;
                } else {
                    result.add(line);
                }
            }
            if (removed) {
                FileHandler.writeLines(FILE, result);
            }
            return removed;
        }
    }

    // ---------- Helpers ----------
    private List<String> readNonBlankLines() {
        List<String> lines = new ArrayList<>();
        for (String line : FileHandler.readLines(FILE)) {
            if (!line.isBlank()) {
                lines.add(line);
            }
        }
        return lines;
    }

    private String toFileLine(Review r) {
        return String.join("|",
                escape(r.getReviewId()),
                escape(r.getFoodId()),
                escape(r.getCustomerId()),
                String.valueOf(r.getRating()),
                escape(r.getComment()),
                r.getReviewType(),
                escape(r.getCreatedAt()),
                escape(r.getUpdatedAt()));
    }

    /** Returns null when the line is blank or malformed. */
    private Review parse(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        String[] p = line.split("\\|", -1);
        if (p.length != FIELD_COUNT) {
            return null;
        }
        try {
            String reviewId = unescape(p[0]);
            String foodId = unescape(p[1]);
            String customerId = unescape(p[2]);
            int rating = Integer.parseInt(p[3].trim());
            String comment = unescape(p[4]);
            String type = p[5].trim();
            String createdAt = unescape(p[6]);
            String updatedAt = unescape(p[7]);

            if (reviewId.isBlank() || foodId.isBlank() || customerId.isBlank()) {
                return null;
            }
            if ("VERIFIED".equals(type)) {
                return new VerifiedReview(reviewId, foodId, customerId, rating, comment, createdAt, updatedAt);
            }
            if ("PUBLIC".equals(type)) {
                return new PublicReview(reviewId, foodId, customerId, rating, comment, createdAt, updatedAt);
            }
            return null;                       // unknown type
        } catch (NumberFormatException e) {
            return null;                       // rating is not a number
        }
    }

    private static String escape(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '|' -> sb.append("\\p");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String unescape(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\\' && i + 1 < text.length()) {
                char next = text.charAt(++i);
                switch (next) {
                    case 'p' -> sb.append('|');
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case '\\' -> sb.append('\\');
                    default -> sb.append('\\').append(next);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}