<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Restaurant details" />
</jsp:include>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/restaurant.css">
<main class="restaurant-page">
    <div class="restaurant-shell restaurant-content">
        <a class="restaurant-back" href="${pageContext.request.contextPath}/restaurants">&larr; All restaurants</a>
        <article class="restaurant-panel restaurant-detail-panel">
            <div class="restaurant-detail-icon">&#127869;</div>
            <div class="restaurant-detail-content">
                <span class="restaurant-type"><c:out value="${restaurant.typeLabel}"/></span>
                <h1><c:out value="${restaurant.name}"/></h1>
                <p class="restaurant-lead"><c:out value="${restaurant.serviceDescription}"/></p>
                <span class="restaurant-status ${restaurant.available ? 'is-open' : 'is-closed'}">
                    <c:out value="${restaurant.statusLabel}"/>
                </span>
                <dl class="restaurant-facts">
                    <div><dt>Restaurant ID</dt><dd><c:out value="${restaurant.restaurantId}"/></dd></div>
                    <div><dt>Location</dt><dd><c:out value="${restaurant.location}"/></dd></div>
                    <div><dt>Phone</dt><dd><c:out value="${restaurant.contactNumber}"/></dd></div>
                    <div><dt>Opening hours</dt><dd><c:out value="${restaurant.openingTimeText}"/> – <c:out value="${restaurant.closingTimeText}"/></dd></div>
                </dl>
                <a class="restaurant-back" href="${pageContext.request.contextPath}/food?restaurantId=${restaurant.restaurantId}">View Foods at This Restaurant &rarr;</a>
            </div>
        </article>
    </div>
</main>
</body></html>
