<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="My Reviews"/></jsp:include>
<jsp:include page="/WEB-INF/views/review/reviewStyles.jsp"/>
<main id="main-content" class="container wide">
    <jsp:include page="/WEB-INF/views/common/messages.jsp"/>

    <div class="card">
        <h2>My Reviews</h2>

        <div class="toolbar">
            <a class="btn btn-small btn-light" href="${pageContext.request.contextPath}/reviews?action=list">All Reviews</a>
            <c:if test="${sessionScope.loggedUser.role == 'CUSTOMER'}">
                <a class="btn btn-small" href="${pageContext.request.contextPath}/reviews?action=new">Write a Review</a>
            </c:if>
        </div>

        <c:choose>
            <c:when test="${empty reviews}">
                <p class="empty">You have not written any reviews yet.</p>
            </c:when>
            <c:otherwise>
                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr><th>Food</th><th>Rating</th><th>Comment</th><th>Date</th><th>Actions</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach var="r" items="${reviews}">
                            <c:set var="foodName" value="${empty foodNames[r.foodId] ? r.foodId : foodNames[r.foodId]}"/>
                            <tr>
                                <td><c:out value="${foodName}"/><div class="muted"><c:out value="${r.foodId}"/></div></td>
                                <td><span class="stars"><c:out value="${r.stars}"/></span><div class="muted">${r.rating}/5</div></td>
                                <td class="comment"><c:out value="${r.comment}"/></td>
                                <td class="muted"><c:out value="${r.createdAt}"/><c:if test="${r.updatedAt != r.createdAt}"><div>edited <c:out value="${r.updatedAt}"/></div></c:if></td>
                                <td class="actions">
                                    <a class="btn btn-small" href="${pageContext.request.contextPath}/reviews?action=edit&amp;id=<c:out value='${r.reviewId}'/>">Edit</a>
                                    <form class="inline" method="post" action="${pageContext.request.contextPath}/reviews"
                                          onsubmit="return confirm('Delete this review?');">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="reviewId" value="<c:out value='${r.reviewId}'/>">
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