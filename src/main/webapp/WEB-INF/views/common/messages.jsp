<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:if test="${not empty errors}">
    <div class="alert alert-error">
        <c:forEach var="e" items="${errors}"><div><c:out value="${e}"/></div></c:forEach>
    </div>
</c:if>
<c:if test="${not empty success}">
    <div class="alert alert-success"><c:out value="${success}"/></div>
</c:if>