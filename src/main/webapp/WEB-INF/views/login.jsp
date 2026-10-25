<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="common/header.jsp"><jsp:param name="title" value="Login"/></jsp:include>
<main id="main-content" class="container auth-container">
    <div class="card">
        <span class="eyebrow">WELCOME TO YUMMYCART</span>
        <h2 class="auth-heading">Welcome back</h2>
        <p class="auth-subtitle">Good food starts with a great sign-in.</p>
        <c:if test="${param.registered == 'true'}"><div class="alert alert-success">Registration successful. Please log in.</div></c:if>
        <c:if test="${param.loggedout == 'true'}"><div class="alert alert-success">You have been logged out.</div></c:if>
        <c:if test="${param.deleted == 'true'}"><div class="alert alert-success">Your account was deleted.</div></c:if>
        <jsp:include page="common/messages.jsp"/>
        <form method="post" action="${pageContext.request.contextPath}/login">
            <label for="email">Email</label>
            <input type="email" id="email" name="email" value="<c:out value='${email}'/>" required>
            <label for="password">Password</label>
            <input type="password" id="password" name="password" required>
            <button class="btn" type="submit">Login</button>
        </form>
        <p>No account? <a href="${pageContext.request.contextPath}/register">Register here</a></p>
    </div>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>