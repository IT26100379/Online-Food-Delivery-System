<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="../common/header.jsp"><jsp:param name="title" value="Manage Users"/></jsp:include>
<div class="container wide">
    <jsp:include page="../common/messages.jsp"/>
    <div class="card">
        <h2>All Users</h2>
        <form class="search" method="get" action="${pageContext.request.contextPath}/admin/users">
            <input type="text" name="q" placeholder="Search by ID, name or email" value="<c:out value='${q}'/>">
            <button class="btn" type="submit">Search</button>
        </form>
        <table>
            <thead>
            <tr><th>ID</th><th>Name</th><th>Email</th><th>Phone</th><th>Role</th><th>Action</th></tr>
            </thead>
            <tbody>
            <c:forEach var="u" items="${users}">
                <tr>
                    <td><c:out value="${u.id}"/></td>
                    <td><c:out value="${u.fullName}"/></td>
                    <td><c:out value="${u.email}"/></td>
                    <td><c:out value="${u.phone}"/></td>
                    <td><span class="badge">${u.role}</span></td>
                    <td>
                        <c:if test="${u.id != sessionScope.loggedUser.id}">
                            <form class="inline" method="post" action="${pageContext.request.contextPath}/admin/users"
                                  onsubmit="return confirm('Delete user ${u.id}?');">
                                <input type="hidden" name="id" value="${u.id}">
                                <button class="btn btn-danger" style="margin-top:0" type="submit">Delete</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty users}"><tr><td colspan="6">No users found.</td></tr></c:if>
            </tbody>
        </table>
    </div>
</div>
</body></html>