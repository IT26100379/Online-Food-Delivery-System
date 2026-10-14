package com.fooddelivery.controller;

import com.fooddelivery.model.Delivery;
import com.fooddelivery.model.DeliveryStatus;
import com.fooddelivery.service.DeliveryService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Front controller for delivery management at /delivery.
 *
 * GET  ?action=list (default) | new | edit&id=D001
 * POST action=create | update | delete | advance
 *
 * Every successful POST redirects back to the list (post/redirect/get) and leaves a one-time message in the session.
 */
@WebServlet(name = "DeliveryServlet", urlPatterns = "/delivery")
public class DeliveryServlet extends HttpServlet {

    private static final String VIEWS = "/WEB-INF/views/delivery/";
    private static final String FLASH_OK = "deliveryFlashOk";
    private static final String FLASH_ERROR = "deliveryFlashError";

    private DeliveryService service;

    @Override
    public void init() throws ServletException {
        String dir = getServletContext().getInitParameter("fooddelivery.dataDir");
        if (dir == null || dir.isBlank()) {
            dir = System.getProperty("fooddelivery.dataDir");
        }
        Path dataDir = (dir == null || dir.isBlank())
                ? Path.of(System.getProperty("user.home"), "fooddelivery-data")
                : Path.of(dir);
        service = new DeliveryService(dataDir);
    }

    // ---------------------------------------------------------------- GET

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = param(req, "action");
        switch (action.isEmpty() ? "list" : action) {
            case "new":
                showForm(req, resp, new Delivery(), false);
                break;
            case "edit":
                Optional<Delivery> existing = service.findById(param(req, "id"));
                if (existing.isPresent()) {
                    showForm(req, resp, existing.get(), true);
                } else {
                    flash(req, FLASH_ERROR, "That delivery no longer exists.");
                    redirectToList(req, resp);
                }
                break;
            default:
                showList(req, resp);
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = param(req, "q");
        DeliveryStatus status = DeliveryStatus.parse(param(req, "status"));

        List<Delivery> deliveries = service.search(query, status);
        req.setAttribute("deliveries", deliveries);
        req.setAttribute("q", query);
        req.setAttribute("selectedStatus", status);
        req.setAttribute("statuses", DeliveryStatus.values());
        moveFlashToRequest(req, FLASH_OK, "flashOk");
        moveFlashToRequest(req, FLASH_ERROR, "flashError");

        req.getRequestDispatcher(VIEWS + "list.jsp").forward(req, resp);
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Delivery delivery, boolean editing)
            throws ServletException, IOException {
        req.setAttribute("delivery", delivery);
        req.setAttribute("editing", editing);
        req.setAttribute("statuses", DeliveryStatus.values());
        req.getRequestDispatcher(VIEWS + "form.jsp").forward(req, resp);
    }

    // ---------------------------------------------------------------- POST

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        switch (param(req, "action")) {
            case "create":
                save(req, resp, false);
                break;
            case "update":
                save(req, resp, true);
                break;
            case "delete":
                delete(req, resp);
                break;
            case "advance":
                advance(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
        }
    }

    private void save(HttpServletRequest req, HttpServletResponse resp, boolean editing)
            throws ServletException, IOException {
        Delivery delivery = readForm(req);
        List<String> errors = delivery.validate();

        if (editing && service.findById(delivery.getDeliveryId()).isEmpty()) {
            flash(req, FLASH_ERROR, "That delivery no longer exists.");
            redirectToList(req, resp);
            return;
        }

        if (errors.isEmpty()) {
            try {
                if (editing) {
                    service.update(delivery);
                    flash(req, FLASH_OK, "Delivery " + delivery.getDeliveryId() + " saved.");
                } else {
                    Delivery created = service.create(delivery);
                    flash(req, FLASH_OK, "Delivery " + created.getDeliveryId() + " created.");
                }
                redirectToList(req, resp);
                return;
            } catch (IllegalStateException | IllegalArgumentException e) {
                errors.add(e.getMessage());
            }
        }

        req.setAttribute("errors", errors);
        showForm(req, resp, delivery, editing);
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = param(req, "id");
        if (service.delete(id)) {
            flash(req, FLASH_OK, "Delivery " + id + " deleted.");
        } else {
            flash(req, FLASH_ERROR, "That delivery no longer exists.");
        }
        redirectToList(req, resp);
    }

    /** Moves a delivery to the next stage in its normal flow. */
    private void advance(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = param(req, "id");
        Optional<Delivery> existing = service.findById(id);

        if (existing.isEmpty()) {
            flash(req, FLASH_ERROR, "That delivery no longer exists.");
        } else if (existing.get().getStatus().getNext() == null) {
            flash(req, FLASH_ERROR, "Delivery " + id + " is already " + existing.get().getStatus().getLabel().toLowerCase() + ".");
        } else {
            DeliveryStatus next = existing.get().getStatus().getNext();
            try {
                service.updateStatus(id, next);
                flash(req, FLASH_OK, "Delivery " + id + " is now " + next.getLabel().toLowerCase() + ".");
            } catch (IllegalStateException | IllegalArgumentException e) {
                flash(req, FLASH_ERROR, e.getMessage());
            }
        }
        redirectToList(req, resp);
    }

    // ---------------------------------------------------------------- helpers

    private Delivery readForm(HttpServletRequest req) {
        Delivery d = new Delivery();
        d.setDeliveryId(param(req, "deliveryId"));
        d.setOrderId(param(req, "orderId"));
        d.setDriverName(param(req, "driverName"));
        d.setDriverPhone(param(req, "driverPhone"));
        d.setDeliveryAddress(param(req, "deliveryAddress"));
        d.setStatus(DeliveryStatus.parse(param(req, "status")));
        try {
            d.setEstimatedMinutes(Integer.parseInt(param(req, "estimatedMinutes")));
        } catch (NumberFormatException e) {
            d.setEstimatedMinutes(-1); // fails validation with a clear message
        }
        return d;
    }

    private String param(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        return value == null ? "" : value.trim();
    }

    private void flash(HttpServletRequest req, String key, String message) {
        req.getSession().setAttribute(key, message);
    }

    private void moveFlashToRequest(HttpServletRequest req, String sessionKey, String requestKey) {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute(sessionKey) != null) {
            req.setAttribute(requestKey, session.getAttribute(sessionKey));
            session.removeAttribute(sessionKey);
        }
    }

    private void redirectToList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/delivery");
    }
}
