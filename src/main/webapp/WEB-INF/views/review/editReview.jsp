<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="Edit Review"/></jsp:include>
<jsp:include page="/WEB-INF/views/review/reviewStyles.jsp"/>
<div class="container">
    <jsp:include page="/WEB-INF/views/common/messages.jsp"/>

    <div class="card">
        <h2>Edit Review <span class="badge"><c:out value="${review.reviewId}"/></span></h2>
        <p>
            Food:
            <strong><c:out value="${empty foodNames[review.foodId] ? review.foodId : foodNames[review.foodId]}"/></strong>
            <span class="muted">(cannot be changed)</span>
        </p>

        <form method="post" action="${pageContext.request.contextPath}/reviews">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="reviewId" value="<c:out value='${review.reviewId}'/>">

            <label for="rating">Rating</label>
            <select id="rating" name="rating" required>
                <option value="5" ${formRating == '5' ? 'selected' : ''}>5 - Excellent</option>
                <option value="4" ${formRating == '4' ? 'selected' : ''}>4 - Good</option>
                <option value="3" ${formRating == '3' ? 'selected' : ''}>3 - Okay</option>
                <option value="2" ${formRating == '2' ? 'selected' : ''}>2 - Poor</option>
                <option value="1" ${formRating == '1' ? 'selected' : ''}>1 - Terrible</option>
            </select>

            <label for="comment">Comment</label>
            <textarea id="comment" name="comment" rows="4" maxlength="500" required><c:out value="${formComment}"/></textarea>
            <div class="muted"><span id="count">0</span> / 500</div>

            <button class="btn" type="submit">Save Changes</button>
            <a class="btn btn-light" href="${pageContext.request.contextPath}/reviews?action=mine">Cancel</a>
        </form>
    </div>
</div>
<script>
    var box = document.getElementById('comment');
    var counter = document.getElementById('count');
    function updateCount() { counter.textContent = box.value.length; }
    box.addEventListener('input', updateCount);
    updateCount();
</script>
</body></html>