<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="common/header.jsp"><jsp:param name="title" value="Register"/></jsp:include>
<main id="main-content" class="container auth-container">
    <div class="card">
        <span class="eyebrow">GET STARTED</span>
        <h2 class="auth-heading">Create your account</h2>
        <p class="auth-subtitle">Join YummyCart and discover something delicious.</p>
        <jsp:include page="common/messages.jsp"/>
        <form method="post" action="${pageContext.request.contextPath}/register">
            <label for="fullName">Full Name</label>
            <input type="text" id="fullName" name="fullName" value="<c:out value='${fullName}'/>" required>
            <label for="email">Email</label>
            <input type="email" id="email" name="email" value="<c:out value='${email}'/>" required>
            <label for="phone">Phone (0771234567)</label>
            <input type="text" id="phone" name="phone" value="<c:out value='${phone}'/>" required>
            <label for="address">Address</label>
            <textarea id="address" name="address" rows="2" required><c:out value='${address}'/></textarea>
            <label for="password">Password</label>
            <input type="password" id="password" name="password" required>
            <label for="confirmPassword">Confirm Password</label>
            <input type="password" id="confirmPassword" name="confirmPassword" required>
            <button class="btn" type="submit">Register</button>
        </form>
        <p>Already registered? <a href="${pageContext.request.contextPath}/login">Login</a></p>
    </div>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>