package com.fooddelivery.controller;

import com.fooddelivery.model.Delivery;
import com.fooddelivery.model.DeliveryStatus;
import com.fooddelivery.model.User;
import com.fooddelivery.model.Role;
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
 * Delivery Management Controller
 *
 * Customer: View and search deliveries only
 * Admin: Create, Edit, Delete and Update Delivery Status
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

    // ADMIN PERMISSION CHECK
    private boolean requireAdmin(HttpServletRequest req,
                                 HttpServletResponse resp) throws IOException {

        HttpSession session = req.getSession(false);

        Object loggedUser = session == null
                ? null
                : session.getAttribute("loggedUser");

        if (!(loggedUser instanceof User)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return false;
        }

        User user = (User) loggedUser;

        if (user.getRole() != Role.ADMIN) {
            resp.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Access Denied! Admins Only."
            );
            return false;
        }

        return true;
    }

    // GET REQUESTS
    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws ServletException, IOException {

        String action = param(req, "action");

        switch (action.isEmpty() ? "list" : action) {

            case "new":

                if (!requireAdmin(req, resp)) return;

                showForm(req, resp, new Delivery(), false);
                break;

            case "edit":

                if (!requireAdmin(req, resp)) return;

                Optional<Delivery> existing =
                        service.findById(param(req, "id"));

                if (existing.isPresent()) {
                    showForm(req, resp, existing.get(), true);
                } else {
                    flash(req, FLASH_ERROR,
                            "That delivery no longer exists.");
                    redirectToList(req, resp);
                }
                break;

            case "list":
                showList(req, resp);
                break;

            default:
                resp.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Unknown action"
                );
        }
    }

    // DELIVERY LIST
    private void showList(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {

        String query = param(req, "q");

        DeliveryStatus status =
                DeliveryStatus.parse(param(req, "status"));

        List<Delivery> deliveries =
                service.search(query, status);

        req.setAttribute("deliveries", deliveries);
        req.setAttribute("q", query);
        req.setAttribute("selectedStatus", status);
        req.setAttribute("statuses", DeliveryStatus.values());

        moveFlashToRequest(req, FLASH_OK, "flashOk");
        moveFlashToRequest(req, FLASH_ERROR, "flashError");

        req.getRequestDispatcher(VIEWS + "list.jsp")
                .forward(req, resp);
    }

    // DELIVERY FORM
    private void showForm(HttpServletRequest req,
                          HttpServletResponse resp,
                          Delivery delivery,
                          boolean editing)
            throws ServletException, IOException {

        req.setAttribute("delivery", delivery);
        req.setAttribute("editing", editing);
        req.setAttribute("statuses", DeliveryStatus.values());

        req.getRequestDispatcher(VIEWS + "form.jsp")
                .forward(req, resp);
    }

    // POST REQUESTS
    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        // Only Admin can modify delivery data
        if (!requireAdmin(req, resp)) return;

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
                resp.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Unknown action"
                );
        }
    }

    // CREATE / UPDATE DELIVERY
    private void save(HttpServletRequest req,
                      HttpServletResponse resp,
                      boolean editing)
            throws ServletException, IOException {

        Delivery delivery = readForm(req);

        List<String> errors = delivery.validate();

        if (editing &&
                service.findById(delivery.getDeliveryId()).isEmpty()) {

            flash(req, FLASH_ERROR,
                    "That delivery no longer exists.");

            redirectToList(req, resp);
            return;
        }

        if (errors.isEmpty()) {

            try {

                if (editing) {

                    service.update(delivery);

                    flash(req, FLASH_OK,
                            "Delivery " + delivery.getDeliveryId() + " saved.");

                } else {

                    Delivery created = service.create(delivery);

                    flash(req, FLASH_OK,
                            "Delivery " + created.getDeliveryId() + " created.");
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

    // DELETE DELIVERY
    private void delete(HttpServletRequest req,
                        HttpServletResponse resp) throws IOException {

        String id = param(req, "id");

        if (service.delete(id)) {

            flash(req, FLASH_OK,
                    "Delivery " + id + " deleted.");

        } else {

            flash(req, FLASH_ERROR,
                    "That delivery no longer exists.");
        }

        redirectToList(req, resp);
    }

    // ADVANCE DELIVERY STATUS
    private void advance(HttpServletRequest req,
                         HttpServletResponse resp) throws IOException {

        String id = param(req, "id");

        Optional<Delivery> existing = service.findById(id);

        if (existing.isEmpty()) {

            flash(req, FLASH_ERROR,
                    "That delivery no longer exists.");

        } else if (existing.get().getStatus().getNext() == null) {

            flash(req, FLASH_ERROR,
                    "Delivery " + id + " is already " +
                            existing.get().getStatus().getLabel().toLowerCase() + ".");

        } else {

            DeliveryStatus next =
                    existing.get().getStatus().getNext();

            try {

                service.updateStatus(id, next);

                flash(req, FLASH_OK,
                        "Delivery " + id + " is now " +
                                next.getLabel().toLowerCase() + ".");

            } catch (IllegalStateException | IllegalArgumentException e) {

                flash(req, FLASH_ERROR, e.getMessage());
            }
        }

        redirectToList(req, resp);
    }

    // READ FORM DATA
    private Delivery readForm(HttpServletRequest req) {

        Delivery d = new Delivery();

        d.setDeliveryId(param(req, "deliveryId"));
        d.setOrderId(param(req, "orderId"));
        d.setDriverName(param(req, "driverName"));
        d.setDriverPhone(param(req, "driverPhone"));
        d.setDeliveryAddress(param(req, "deliveryAddress"));

        d.setStatus(
                DeliveryStatus.parse(param(req, "status"))
        );

        try {

            d.setEstimatedMinutes(
                    Integer.parseInt(param(req, "estimatedMinutes"))
            );

        } catch (NumberFormatException e) {

            d.setEstimatedMinutes(-1);
        }

        return d;
    }

    // HELPER METHODS
    private String param(HttpServletRequest req, String name) {

        String value = req.getParameter(name);

        return value == null ? "" : value.trim();
    }

    private void flash(HttpServletRequest req,
                       String key,
                       String message) {

        req.getSession().setAttribute(key, message);
    }

    private void moveFlashToRequest(HttpServletRequest req,
                                    String sessionKey,
                                    String requestKey) {

        HttpSession session = req.getSession(false);

        if (session != null && session.getAttribute(sessionKey) != null) {

            req.setAttribute(
                    requestKey,
                    session.getAttribute(sessionKey)
            );

            session.removeAttribute(sessionKey);
        }
    }

    private void redirectToList(HttpServletRequest req,
                                HttpServletResponse resp)
            throws IOException {

        resp.sendRedirect(req.getContextPath() + "/delivery");
    }
}