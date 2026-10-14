package com.fooddelivery.util;

import java.util.Collection;

public final class IdGenerator {

    private IdGenerator() {}

    public static String next(String prefix, Collection<String> existingIds) {
        int max = 0;
        for (String id : existingIds) {
            if (id != null && id.startsWith(prefix)) {
                try {
                    max = Math.max(max, Integer.parseInt(id.substring(prefix.length())));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return String.format("%s%03d", prefix, max + 1);
    }
}