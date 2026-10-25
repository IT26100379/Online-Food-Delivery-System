<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en" data-theme="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="theme-color" content="#0c111b">
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/assets/yummycart-icon.svg">
    <title><c:out value="${empty param.title ? 'Home' : param.title}"/> | YummyCart</title>
    <script>
        (function () {
            try { document.documentElement.setAttribute('data-theme', localStorage.getItem('YummyCart-theme') === 'light' ? 'light' : 'dark'); }
            catch (e) { document.documentElement.setAttribute('data-theme', 'dark'); }
        }());
    </script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/theme.css">
    <script defer src="${pageContext.request.contextPath}/js/theme.js"></script>
</head>
<body>
<a class="skip-link" href="#main-content">Skip to content</a>
<header class="site-header">
    <div class="site-header-inner">
        <a class="site-brand" href="${pageContext.request.contextPath}/" aria-label="YummyCart home">
            <span class="brand-mark" aria-hidden="true">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.9" stroke-linecap="round" stroke-linejoin="round"><circle cx="10" cy="20" r="1"/><circle cx="18" cy="20" r="1"/><path d="M2 3h2l2.3 12h12.1l2.1-9H5"/><path d="M8 12h10"/></svg>
            </span>
            <span class="brand-name">Yummy<span class="brand-accent">Cart</span></span>
        </a>
        <nav class="site-nav" id="site-navigation" aria-label="Main navigation">
            <a href="${pageContext.request.contextPath}/" data-nav="home">Home</a>
            <a href="${pageContext.request.contextPath}/restaurants" data-nav="restaurants">Restaurants</a>
            <a href="${pageContext.request.contextPath}/food" data-nav="food">Food menu</a>
            <a href="${pageContext.request.contextPath}/orders" data-nav="orders">Orders</a>
            <a href="${pageContext.request.contextPath}/delivery" data-nav="delivery">Delivery</a>
            <a href="${pageContext.request.contextPath}/reviews" data-nav="reviews">Reviews</a>
            <c:if test="${sessionScope.loggedUser.role == 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/admin/dashboard" data-nav="admin" class="nav-admin">Dashboard</a>
            </c:if>
            <c:choose>
                <c:when test="${not empty sessionScope.loggedUser}">
                    <a class="nav-mobile-auth" href="${pageContext.request.contextPath}/profile">My profile</a>
                    <form class="nav-mobile-auth nav-mobile-logout" method="post" action="${pageContext.request.contextPath}/logout"><button type="submit">Log out</button></form>
                </c:when>
                <c:otherwise>
                    <a class="nav-mobile-auth" href="${pageContext.request.contextPath}/login">Log in</a>
                    <a class="nav-mobile-auth" href="${pageContext.request.contextPath}/register">Create account</a>
                </c:otherwise>
            </c:choose>
        </nav>
        <div class="site-actions">
            <button type="button" class="theme-toggle" id="theme-toggle" aria-label="Switch color theme" title="Toggle dark / light mode">
                <svg class="theme-moon" width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M20.5 14.5A8.5 8.5 0 0 1 9.5 3.5a8.5 8.5 0 1 0 11 11z"/></svg>
                <svg class="theme-sun" width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="12" cy="12" r="4"/><path d="M12 2v2m0 16v2M4.93 4.93l1.42 1.42m11.3 11.3 1.42 1.42M2 12h2m16 0h2M4.93 19.07l1.42-1.42m11.3-11.3 1.42-1.42"/></svg>
            </button>
            <c:choose>
                <c:when test="${not empty sessionScope.loggedUser}">
                    <a class="header-profile" href="${pageContext.request.contextPath}/profile" title="My profile">
                        <span class="avatar" aria-hidden="true"><c:out value="${sessionScope.loggedUser.role == 'ADMIN' ? 'A' : 'U'}"/></span>
                        <span class="header-profile-text"><span class="header-greeting">My account</span><strong><c:out value="${sessionScope.loggedUser.fullName}"/></strong></span>
                    </a>
                    <form class="header-logout" method="post" action="${pageContext.request.contextPath}/logout"><button type="submit" class="header-logout-btn">Log out</button></form>
                </c:when>
                <c:otherwise>
                    <a class="header-login" href="${pageContext.request.contextPath}/login">Log in</a>
                    <a class="header-signup" href="${pageContext.request.contextPath}/register">Sign up <span aria-hidden="true">↗</span></a>
                </c:otherwise>
            </c:choose>
            <button type="button" class="mobile-menu-button" id="mobile-menu-button" aria-expanded="false" aria-controls="site-navigation" aria-label="Open navigation">
                <svg width="21" height="21" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M4 7h16M4 12h16M4 17h16"/></svg>
            </button>
        </div>
    </div>
</header>
