<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="base" value="${pageContext.request.contextPath}/delivery" />
<c:set var="isAdmin" value="${sessionScope.loggedUser.role == 'ADMIN'}" />

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Deliveries</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/delivery.css">
</head>

<body>

<header class="bar">
    <h1>Deliveries</h1>

    <c:if test="${isAdmin}">
        <a class="btn primary" href="${base}?action=new">
            New delivery
        </a>
    </c:if>
</header>

<main>

    <c:if test="${not empty flashOk}">
        <p class="notice ok" role="status">
            <c:out value="${flashOk}" />
        </p>
    </c:if>

    <c:if test="${not empty flashError}">
        <p class="notice error" role="alert">
            <c:out value="${flashError}" />
        </p>
    </c:if>

    <form class="filters" method="get" action="${base}">

        <input type="search"
               name="q"
               value="<c:out value='${q}' />"
               placeholder="Search by delivery ID, order ID, driver or address"
               aria-label="Search deliveries">

        <select name="status" aria-label="Filter by status">
            <option value="">All statuses</option>

            <c:forEach items="${statuses}" var="s">
                <option value="${s}"
                        ${s == selectedStatus ? 'selected' : ''}>
                    <c:out value="${s.label}" />
                </option>
            </c:forEach>
        </select>

        <button class="btn" type="submit">
            Search
        </button>

        <c:if test="${not empty q or not empty selectedStatus}">
            <a class="btn" href="${base}">
                Clear
            </a>
        </c:if>

    </form>

    <c:choose>

        <c:when test="${empty deliveries}">

            <div class="table-wrap">
                <p class="empty">

                    <c:choose>

                        <c:when test="${not empty q or not empty selectedStatus}">
                            No deliveries match your search.
                            <a href="${base}">
                                Clear the filters
                            </a>
                            to see everything.
                        </c:when>

                        <c:otherwise>
                            No deliveries yet.

                            <c:if test="${isAdmin}">
                                <a href="${base}?action=new">
                                    Create the first one
                                </a>.
                            </c:if>
                        </c:otherwise>

                    </c:choose>

                </p>
            </div>

        </c:when>

        <c:otherwise>

            <p class="count">
                ${deliveries.size()}
                ${deliveries.size() == 1 ? 'delivery' : 'deliveries'}
            </p>

            <div class="table-wrap">

                <table>

                    <thead>
                    <tr>
                        <th scope="col">Delivery</th>
                        <th scope="col">Order</th>
                        <th scope="col">Driver</th>
                        <th scope="col">Address</th>
                        <th scope="col">ETA</th>
                        <th scope="col">Status</th>

                        <c:if test="${isAdmin}">
                            <th scope="col">Actions</th>
                        </c:if>
                    </tr>
                    </thead>

                    <tbody>

                    <c:forEach items="${deliveries}" var="d">

                        <tr>

                            <td class="id">
                                <c:out value="${d.deliveryId}" />

                                <span class="sub">
                                    <c:out value="${d.createdAtDisplay}" />
                                </span>
                            </td>

                            <td class="id">
                                <c:out value="${d.orderId}" />
                            </td>

                            <td>
                                <c:choose>
                                    <c:when test="${d.hasDriver()}">

                                        <c:out value="${d.driverName}" />

                                        <span class="sub">
                                            <c:out value="${d.driverPhone}" />
                                        </span>

                                    </c:when>

                                    <c:otherwise>
                                        <span class="none">
                                            Unassigned
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <td>
                                <c:out value="${d.deliveryAddress}" />
                            </td>

                            <td>
                                <c:out value="${d.estimatedMinutes}" /> min
                            </td>

                            <td>
                                <span class="chip status-${d.status}">
                                    <c:out value="${d.status.label}" />
                                </span>
                            </td>

                            <c:if test="${isAdmin}">

                                <td>

                                    <div class="actions">

                                        <c:url var="editUrl" value="/delivery">
                                            <c:param name="action" value="edit" />
                                            <c:param name="id" value="${d.deliveryId}" />
                                        </c:url>

                                        <a class="btn small" href="${editUrl}">
                                            Edit
                                        </a>

                                        <c:if test="${d.status.next != null}">

                                            <form method="post" action="${base}">

                                                <input type="hidden"
                                                       name="action"
                                                       value="advance">

                                                <input type="hidden"
                                                       name="id"
                                                       value="<c:out value='${d.deliveryId}' />">

                                                <button class="btn small"
                                                        type="submit">
                                                    Mark as
                                                    <c:out value="${d.status.next.label}" />
                                                </button>

                                            </form>

                                        </c:if>

                                        <form method="post"
                                              action="${base}"
                                              onsubmit="return confirm('Delete this delivery? This cannot be undone.');">

                                            <input type="hidden"
                                                   name="action"
                                                   value="delete">

                                            <input type="hidden"
                                                   name="id"
                                                   value="<c:out value='${d.deliveryId}' />">

                                            <button class="btn small danger"
                                                    type="submit">
                                                Delete
                                            </button>

                                        </form>

                                    </div>

                                </td>

                            </c:if>

                        </tr>

                    </c:forEach>

                    </tbody>

                </table>

            </div>

        </c:otherwise>

    </c:choose>

</main>

</body>
</html>