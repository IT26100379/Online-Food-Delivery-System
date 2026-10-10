<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="common/header.jsp"><jsp:param name="title" value="My Profile"/></jsp:include>
<div class="container">
    <jsp:include page="common/messages.jsp"/>

    <div class="card">
        <h2>My Profile <span class="badge">${sessionScope.loggedUser.id} · ${sessionScope.loggedUser.role}</span></h2>
        <form method="post" action="${pageContext.request.contextPath}/profile">
            <input type="hidden" name="action" value="update">
            <label>Email (cannot be changed)</label>
            <input type="text" value="<c:out value='${sessionScope.loggedUser.email}'/>" readonly>
            <label for="fullName">Full Name</label>
            <input type="text" id="fullName" name="fullName" value="<c:out value='${sessionScope.loggedUser.fullName}'/>" required>
            <label for="phone">Phone</label>
            <input type="text" id="phone" name="phone" value="<c:out value='${sessionScope.loggedUser.phone}'/>" required>
            <label for="address">Address</label>
            <textarea id="address" name="address" rows="2" required><c:out value='${sessionScope.loggedUser.address}'/></textarea>
            <button class="btn" type="submit">Update Profile</button>
        </form>
    </div>

    <div class="card">
        <h2>Change Password</h2>
        <form method="post" action="${pageContext.request.contextPath}/profile">
            <input type="hidden" name="action" value="password">
            <label for="currentPassword">Current Password</label>
            <input type="password" id="currentPassword" name="currentPassword" required>
            <label for="newPassword">New Password</label>
            <input type="password" id="newPassword" name="newPassword" required>
            <label for="confirmPassword">Confirm New Password</label>
            <input type="password" id="confirmPassword" name="confirmPassword" required>
            <button class="btn" type="submit">Change Password</button>
        </form>
    </div>

    <c:if test="${sessionScope.loggedUser.role != 'ADMIN'}">
        <div class="card">
            <h2>Delete Account</h2>
            <p>This permanently removes your account.</p>
            <form method="post" action="${pageContext.request.contextPath}/profile"
                  onsubmit="return confirm('Delete your account permanently?');">
                <input type="hidden" name="action" value="delete">
                <button class="btn btn-danger" type="submit">Delete My Account</button>
            </form>
        </div>
    </c:if>
</div>
</body></html>