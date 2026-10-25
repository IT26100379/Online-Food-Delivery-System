<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Restaurants" />
</jsp:include>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/restaurant.css">
<main class="restaurant-page">
    <section class="restaurant-hero">
        <div class="restaurant-shell">
            <span class="restaurant-eyebrow">EXPLORE LOCAL FLAVOURS</span>
            <h1>Restaurants near you</h1>
            <p>Browse delicious places and choose your next favourite meal.</p>
            <form method="get" action="${pageContext.request.contextPath}/restaurants" class="restaurant-search">
                <input type="search" name="q" value="<c:out value='${query}'/>"
                       placeholder="Search name, location, or type" aria-label="Search restaurants">
                <button class="restaurant-button" type="submit">Search</button>
                <a class="restaurant-button restaurant-button-light" href="${pageContext.request.contextPath}/restaurants">Reset</a>
            </form>
        </div>
    </section>

    <div class="restaurant-shell restaurant-content">
        <div class="restaurant-section-title">
            <div><p class="restaurant-overline">OUR PARTNERS</p><h2>Discover restaurants</h2></div>
            <span class="restaurant-count">${restaurants.size()} found</span>
        </div>
        <c:choose>
            <c:when test="${empty restaurants}">
                <div class="restaurant-empty"><h3>No restaurants found</h3><p>Try another keyword or check back later.</p></div>
            </c:when>
            <c:otherwise>
                <div class="restaurant-grid">
                    <c:forEach var="restaurant" items="${restaurants}">
                        <c:url var="detailsUrl" value="/restaurants">
                            <c:param name="action" value="view" />
                            <c:param name="id" value="${restaurant.restaurantId}" />
                        </c:url>
                        <article class="restaurant-tile">
                            <div class="restaurant-tile-banner"><span class="restaurant-tile-symbol">&#127860;</span>
                                <span class="restaurant-status ${restaurant.available ? 'is-open' : 'is-closed'}">
                                    <c:out value="${restaurant.statusLabel}" />
                                </span>
                            </div>
                            <div class="restaurant-tile-body">
                                <span class="restaurant-type"><c:out value="${restaurant.typeLabel}"/></span>
                                <h3><c:out value="${restaurant.name}"/></h3>
                                <p class="restaurant-description"><c:out value="${restaurant.serviceDescription}"/></p>
                                <p>&#128205; <c:out value="${restaurant.location}"/></p>
                                <p>&#128336; <c:out value="${restaurant.openingTimeText}"/> - <c:out value="${restaurant.closingTimeText}"/></p>
                                <a class="restaurant-button restaurant-button-full" href="${detailsUrl}">View details &rarr;</a>
                            </div>
                        </article>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>
</body></html>
