<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="Admin dashboard"/></jsp:include>
<main class="page" id="main-content">
  <div class="page-top"><div><span class="eyebrow">CONTROL CENTER / ADMIN</span><h1 class="page-heading">Your dashboard</h1><p>A clear view of your YummyCart workspace and all the tools you need.</p></div><span class="pill">● Admin access</span></div>
  <section class="stats-grid" aria-label="Overview">
    <div class="stat-card"><span class="stat-icon">♙</span><span class="stat-number"><c:out value="${totalUsers}"/></span><strong>Registered users</strong><p class="muted">Total accounts on your platform</p></div>
    <div class="stat-card"><span class="stat-icon">✦</span><span class="stat-number">6</span><strong>Management areas</strong><p class="muted">Core areas accessible below</p></div>
    <div class="stat-card"><span class="stat-icon">◎</span><span class="stat-number">01</span><strong>Workspace</strong><p class="muted">One place for daily operations</p></div>
  </section>
  <section class="section-pad"><div class="section-heading"><div><span class="eyebrow">QUICK ACCESS</span><h2>Manage your platform</h2></div><span class="muted">Choose a section to get started</span></div>
    <div class="quick-grid">
      <a class="quick-link" href="${pageContext.request.contextPath}/admin/users"><span class="stat-icon">◎</span><strong>Users</strong><small>View and manage user accounts</small><span class="quick-arrow">↗</span></a>
      <a class="quick-link" href="${pageContext.request.contextPath}/admin/restaurants"><span class="stat-icon">⌂</span><strong>Restaurants</strong><small>Maintain your restaurant partners</small><span class="quick-arrow">↗</span></a>
      <a class="quick-link" href="${pageContext.request.contextPath}/food"><span class="stat-icon">✦</span><strong>Food menu</strong><small>View and update menu items</small><span class="quick-arrow">↗</span></a>
      <a class="quick-link" href="${pageContext.request.contextPath}/orders"><span class="stat-icon">▤</span><strong>Orders</strong><small>Manage customer orders</small><span class="quick-arrow">↗</span></a>
      <a class="quick-link" href="${pageContext.request.contextPath}/delivery"><span class="stat-icon">↗</span><strong>Deliveries</strong><small>Manage dispatch and status</small><span class="quick-arrow">↗</span></a>
      <a class="quick-link" href="${pageContext.request.contextPath}/admin/reviews"><span class="stat-icon">☆</span><strong>Review moderation</strong><small>Review community feedback</small><span class="quick-arrow">↗</span></a>
    </div>
  </section>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>
