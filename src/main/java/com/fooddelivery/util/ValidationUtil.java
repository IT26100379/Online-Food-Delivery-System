package com.fooddelivery.util;

import java.util.List;
import java.util.regex.Pattern;

public final class ValidationUtil {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern PHONE = Pattern.compile("^0\\d{9}$");
    private static final Pattern NAME = Pattern.compile("^[A-Za-z][A-Za-z .'-]{1,49}$");

    private ValidationUtil() {}

    public static boolean isValidEmail(String email) {
        return email != null && email.length() <= 80 && EMAIL.matcher(email).matches();
    }

    public static void validateProfile(String fullName, String phone, String address, List<String> errors) {
        if (fullName == null || !NAME.matcher(fullName).matches()) {
            errors.add("Full name must be 2-50 letters (spaces, . ' - allowed).");
        }
        if (phone == null || !PHONE.matcher(phone).matches()) {
            errors.add("Phone must be 10 digits starting with 0 (e.g. 0771234567).");
        }
        if (address == null || address.length() < 5 || address.length() > 100 || address.contains("|")) {
            errors.add("Address must be 5-100 characters and must not contain '|'.");
        }
    }

    public static void validatePassword(String password, List<String> errors) {
        if (password == null || password.length() < 8
                || !password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
            errors.add("Password must be at least 8 characters with a letter and a digit.");
        }
    }
}