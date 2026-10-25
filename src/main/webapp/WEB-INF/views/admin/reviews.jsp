<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="Moderate Reviews"/></jsp:include>
<jsp:include page="/WEB-INF/views/review/reviewStyles.jsp"/>
<main id="main-content" class="container wide">
    <jsp:include page="/WEB-INF/views/common/messages.jsp"/>

    <div class="card">
        <h2>Review Moderation <span class="badge"><c:out value="${reviews.size()}"/> shown</span></h2>

        <form class="toolbar" method="get" action="${pageContext.request.contextPath}/admin/reviews">
            <select name="foodId" aria-label="Filter by food">
                <option value="">All foods</option>
                <c:forEach var="f" items="${foodNames}">
                    <option value="<c:out value='${f.key}'/>" ${f.key == selectedFoodId ? 'selected' : ''}><c:out value="${f.value}"/></option>
                </c:forEach>
            </select>
            <button class="btn btn-small" type="submit">Filter</button>
            <a class="btn btn-small btn-light" href="${pageContext.request.contextPath}/reviews?action=list">Public review page</a>
        </form>

        <c:choose>
            <c:when test="${empty reviews}">
                <p class="empty">No reviews to moderate.</p>
            </c:when>
            <c:otherwise>
                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr><th>ID</th><th>Food</th><th>Rating</th><th>Comment</th><th>Reviewer</th><th>Date</th><th>Action</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach var="r" items="${reviews}">
                            <c:set var="foodName" value="${empty foodNames[r.foodId] ? r.foodId : foodNames[r.foodId]}"/>
                            <c:set var="reviewer" value="${empty userNames[r.customerId] ? 'Deleted user' : userNames[r.customerId]}"/>
                            <tr>
                                <td><c:out value="${r.reviewId}"/></td>
                                <td><c:out value="${foodName}"/><div class="muted"><c:out value="${r.foodId}"/></div></td>
                                <td><span class="stars"><c:out value="${r.stars}"/></span><div class="muted">${r.rating}/5</div></td>
                                <td class="comment"><c:out value="${r.comment}"/></td>
                                <td><c:out value="${reviewer}"/><div class="muted"><c:out value="${r.customerId}"/></div><div><span class="badge"><c:out value="${r.badgeLabel}"/></span></div></td>
                                <td class="muted"><c:out value="${r.createdAt}"/><c:if test="${r.updatedAt != r.createdAt}"><div>edited <c:out value="${r.updatedAt}"/></div></c:if></td>
                                <td class="actions">
                                    <form class="inline" method="post" action="${pageContext.request.contextPath}/admin/reviews"
                                          onsubmit="return confirm('Delete review ${r.reviewId}? This cannot be undone.');">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="reviewId" value="<c:out value='${r.reviewId}'/>">
                                        <input type="hidden" name="foodId" value="<c:out value='${selectedFoodId}'/>">
                                        <button class="btn btn-small btn-danger" type="submit">Delete</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>