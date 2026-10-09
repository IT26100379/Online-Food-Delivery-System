package com.fooddelivery.service;



import com.fooddelivery.model.Order;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

    public class OrderService {
        private final Path filePath;
        private static final Object LOCK = new Object();

        public OrderService(String filePath) {
            this.filePath = Paths.get(filePath);
        }

        // CREATE
        public Order createOrder(Order order) throws IOException {
            synchronized (LOCK) {
                List<Order> orders = readAll();
                order.setOrderId(nextId(orders));
                orders.add(order);
                writeAll(orders);
                return order;
            }
        }

        // READ
        public List<Order> getAllOrders() throws IOException {
            synchronized (LOCK) { return readAll(); }
        }

        public Optional<Order> getOrderById(String id) throws IOException {
            synchronized (LOCK) {
                return readAll().stream().filter(o -> o.getOrderId().equals(id)).findFirst();
            }
        }

        // UPDATE
        public boolean updateOrder(Order updated) throws IOException {
            synchronized (LOCK) {
                List<Order> orders = readAll();
                for (int i = 0; i < orders.size(); i++) {
                    if (orders.get(i).getOrderId().equals(updated.getOrderId())) {
                        orders.set(i, updated);
                        writeAll(orders);
                        return true;
                    }
                }
                return false;
            }
        }

        // DELETE
        public boolean deleteOrder(String id) throws IOException {
            synchronized (LOCK) {
                List<Order> orders = readAll();
                boolean removed = orders.removeIf(o -> o.getOrderId().equals(id));
                if (removed) writeAll(orders);
                return removed;
            }
        }

        // File helpers
        private List<Order> readAll() throws IOException {
            List<Order> orders = new ArrayList<>();
            if (Files.notExists(filePath)) return orders;
            try (BufferedReader br = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.trim().isEmpty()) orders.add(Order.fromFileString(line));
                }
            }
            return orders;
        }

        private void writeAll(List<Order> orders) throws IOException {
            if (filePath.getParent() != null) Files.createDirectories(filePath.getParent());
            try (BufferedWriter bw = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
                for (Order o : orders) {
                    bw.write(o.toFileString());
                    bw.newLine();
                }
            }
        }

        private String nextId(List<Order> orders) {
            int max = 0;
            for (Order o : orders) {
                max = Math.max(max, Integer.parseInt(o.getOrderId().substring(1)));
            }
            return String.format("O%03d", max + 1);
        }
}
