<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Manage restaurants" />
</jsp:include>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/restaurant.css">
<main class="restaurant-page">
    <div class="restaurant-shell restaurant-content">
        <div class="restaurant-section-title">
            <div><p class="restaurant-overline">ADMIN PANEL</p><h1>Restaurant Management</h1><p>Add, search, update and delete restaurants.</p></div>
            <a class="restaurant-button" href="${pageContext.request.contextPath}/admin/restaurants?action=new">+ Add Restaurant</a>
        </div>
        <c:choose>
            <c:when test="${msg == 'created'}"><div class="restaurant-alert success">Restaurant added successfully.</div></c:when>
            <c:when test="${msg == 'updated'}"><div class="restaurant-alert success">Restaurant updated successfully.</div></c:when>
            <c:when test="${msg == 'deleted'}"><div class="restaurant-alert success">Restaurant deleted successfully.</div></c:when>
            <c:when test="${msg == 'notfound'}"><div class="restaurant-alert error">Restaurant was not found.</div></c:when>
        </c:choose>
        <div class="restaurant-panel">
            <form class="restaurant-search restaurant-search-admin" method="get" action="${pageContext.request.contextPath}/admin/restaurants">
                <input type="search" name="q" value="<c:out value='${query}'/>" placeholder="Search by ID, name, location, or type">
                <button class="restaurant-button" type="submit">Search</button>
                <a class="restaurant-button restaurant-button-light" href="${pageContext.request.contextPath}/admin/restaurants">Reset</a>
            </form>
            <div class="restaurant-table-scroll">
                <table class="restaurant-table">
                    <thead><tr><th>ID</th><th>Restaurant</th><th>Type</th><th>Location</th><th>Opening hours</th><th>Status</th><th>Actions</th></tr></thead>
                    <tbody>
                    <c:forEach var="r" items="${restaurants}">
                        <c:url var="editUrl" value="/admin/restaurants">
                            <c:param name="action" value="edit"/><c:param name="id" value="${r.restaurantId}"/>
                        </c:url>
                        <tr>
                            <td><c:out value="${r.restaurantId}"/></td>
                            <td><strong><c:out value="${r.name}"/></strong></td>
                            <td><c:out value="${r.typeLabel}"/></td>
                            <td><c:out value="${r.location}"/></td>
                            <td><c:out value="${r.openingTimeText}"/>–<c:out value="${r.closingTimeText}"/></td>
                            <td><span class="restaurant-status ${r.available ? 'is-open' : 'is-closed'}"><c:out value="${r.statusLabel}"/></span></td>
                            <td><div class="restaurant-actions">
                                <a class="restaurant-small-button" href="${editUrl}">Edit</a>
                                <form method="post" action="${pageContext.request.contextPath}/admin/restaurants"
                                      onsubmit="return confirm('Delete this restaurant?');">
                                    <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="id" value="<c:out value='${r.restaurantId}'/>">
                                    <button class="restaurant-small-button restaurant-delete" type="submit">Delete</button>
                                </form>
                            </div></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty restaurants}">
                        <tr><td colspan="7" class="restaurant-empty-table">No restaurants to display. Click Add Restaurant to create one.</td></tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</main>
</body></html>
