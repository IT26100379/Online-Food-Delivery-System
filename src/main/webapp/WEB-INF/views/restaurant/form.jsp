<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Restaurant form" />
</jsp:include>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/restaurant.css">
<main class="restaurant-page" id="main-content">
    <div class="restaurant-shell restaurant-content restaurant-form-shell">
        <a class="restaurant-back" href="${pageContext.request.contextPath}/admin/restaurants">&larr; Back to manage restaurants</a>
        <div class="restaurant-panel">
            <p class="restaurant-overline">ADMIN PANEL</p>
            <h1>${editing ? 'Edit Restaurant' : 'Add Restaurant'}</h1>
            <p class="restaurant-lead">${editing ? 'Update restaurant details below.' : 'Complete this form to list a new restaurant.'}</p>
            <c:if test="${not empty errors}">
                <div class="restaurant-alert error">
                    <c:forEach var="error" items="${errors}"><div><c:out value="${error}"/></div></c:forEach>
                </div>
            </c:if>
            <form method="post" action="${pageContext.request.contextPath}/admin/restaurants" class="restaurant-form">
                <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
                <input type="hidden" name="action" value="${editing ? 'update' : 'create'}">
                <input type="hidden" name="restaurantId" value="<c:out value='${restaurant.restaurantId}'/>">
                <c:if test="${editing}">
                    <div class="restaurant-field"><label>Restaurant ID</label><input type="text" value="<c:out value='${restaurant.restaurantId}'/>" readonly></div>
                </c:if>
                <div class="restaurant-field"><label for="restaurantName">Restaurant name *</label>
                    <input id="restaurantName" name="name" type="text" maxlength="80" required value="<c:out value='${restaurant.name}'/>" placeholder="e.g. City Bites"></div>
                <div class="restaurant-field"><label for="restaurantType">Restaurant type *</label>
                    <select id="restaurantType" name="type" required>
                        <option value="FAST_FOOD" ${restaurant.type == 'FAST_FOOD' ? 'selected' : ''}>Fast Food</option>
                        <option value="TRADITIONAL" ${restaurant.type == 'TRADITIONAL' ? 'selected' : ''}>Traditional</option>
                    </select></div>
                <div class="restaurant-field"><label for="restaurantLocation">Location *</label>
                    <input id="restaurantLocation" name="location" type="text" maxlength="120" required value="<c:out value='${restaurant.location}'/>" placeholder="e.g. Negombo"></div>
                <div class="restaurant-field"><label for="restaurantContact">Contact number *</label>
                    <input id="restaurantContact" name="contactNumber" type="tel" required pattern="\+?[0-9]{9,15}"
                           value="<c:out value='${restaurant.contactNumber}'/>" placeholder="e.g. 0771234567"></div>
                <div class="restaurant-form-row">
                    <div class="restaurant-field"><label for="openTime">Opening time *</label>
                        <input id="openTime" name="openingTime" type="time" required value="<c:out value='${restaurant.openingTimeText}'/>"></div>
                    <div class="restaurant-field"><label for="closeTime">Closing time *</label>
                        <input id="closeTime" name="closingTime" type="time" required value="<c:out value='${restaurant.closingTimeText}'/>"></div>
                </div>
                <p class="restaurant-tip">This version supports same-day hours (e.g. 09:00–22:00, not overnight).</p>
                <div class="restaurant-field"><label for="restaurantAvailable">Restaurant status *</label>
                    <select id="restaurantAvailable" name="available">
                        <option value="true" ${restaurant.available ? 'selected' : ''}>Open</option>
                        <option value="false" ${!restaurant.available ? 'selected' : ''}>Closed</option>
                    </select>
                </div>
                <div class="restaurant-form-footer">
                    <button class="restaurant-button" type="submit">${editing ? 'Save Changes' : 'Add Restaurant'}</button>
                    <a class="restaurant-button restaurant-button-light" href="${pageContext.request.contextPath}/admin/restaurants">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>
