<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="Home"/></jsp:include>
<main class="page" id="main-content">
  <section class="hero" aria-labelledby="hero-title">
    <div class="hero-inner">
      <div class="hero-copy">
        <span class="pill">✦ &nbsp; Your next favourite meal starts here</span>
        <h1 id="hero-title">Food worth <em>coming back for</em></h1>
        <p>Discover great local restaurants, explore delicious menus and make every mealtime a little more special.</p>
        <div class="hero-actions">
          <a class="btn" href="${pageContext.request.contextPath}/restaurants">Explore restaurants <span aria-hidden="true">↗</span></a>
          <a class="btn btn-light" href="${pageContext.request.contextPath}/food">Browse food menu <span aria-hidden="true">→</span></a>
        </div>
        <div class="hero-trust"><span class="trust-dot"></span> Local favourites <span aria-hidden="true">·</span> Easy browsing <span aria-hidden="true">·</span> Made for you</div>
      </div>
      <div class="hero-art" aria-hidden="true">
        <img class="hero-meal" alt="" src="${pageContext.request.contextPath}/assets/hero-bowl.svg" width="430" height="430">
        <span class="floating-chip one"><span class="dot"></span> Fresh &amp; delicious</span>
        <span class="floating-chip two">✦ &nbsp; Taste the good stuff</span>
      </div>
    </div>
  </section>

  <section class="section-pad" aria-labelledby="explore-heading">
    <div class="section-heading"><div><span class="eyebrow">DISCOVER MORE</span><h2 id="explore-heading">Everything you need, in one place</h2></div><a class="section-link" href="${pageContext.request.contextPath}/restaurants">Start exploring →</a></div>
    <div class="feature-grid">
      <a class="feature-card quick-link" href="${pageContext.request.contextPath}/restaurants"><span class="feature-icon">⌂</span><strong>Find restaurants</strong><small>Discover something delicious nearby.</small><span class="quick-arrow">↗</span></a>
      <a class="feature-card quick-link" href="${pageContext.request.contextPath}/food"><span class="feature-icon">✦</span><strong>Explore the menu</strong><small>Find your next go-to dish.</small><span class="quick-arrow">↗</span></a>
      <a class="feature-card quick-link" href="${pageContext.request.contextPath}/orders"><span class="feature-icon">▤</span><strong>Manage orders</strong><small>Keep up with your orders in one spot.</small><span class="quick-arrow">↗</span></a>
      <a class="feature-card quick-link" href="${pageContext.request.contextPath}/delivery"><span class="feature-icon">↗</span><strong>Delivery updates</strong><small>See the latest delivery information.</small><span class="quick-arrow">↗</span></a>
      <a class="feature-card quick-link" href="${pageContext.request.contextPath}/reviews"><span class="feature-icon">☆</span><strong>Reviews &amp; ratings</strong><small>Read what other food lovers think.</small><span class="quick-arrow">↗</span></a>
      <c:choose>
        <c:when test="${sessionScope.loggedUser.role == 'ADMIN'}"><a class="feature-card quick-link" href="${pageContext.request.contextPath}/admin/dashboard"><span class="feature-icon">▦</span><strong>Admin dashboard</strong><small>Manage the whole platform.</small><span class="quick-arrow">↗</span></a></c:when>
        <c:when test="${not empty sessionScope.loggedUser}"><a class="feature-card quick-link" href="${pageContext.request.contextPath}/profile"><span class="feature-icon">◎</span><strong>My account</strong><small>Your details, all in one place.</small><span class="quick-arrow">↗</span></a></c:when>
        <c:otherwise><a class="feature-card quick-link" href="${pageContext.request.contextPath}/register"><span class="feature-icon">◎</span><strong>Join YummyCart</strong><small>Make yourself at home.</small><span class="quick-arrow">↗</span></a></c:otherwise>
      </c:choose>
    </div>
  </section>

  <section class="insight-bar"><div><strong>Good food brings everyone together.</strong><p>Explore your favourites and discover a little something new.</p></div><a class="btn" href="${pageContext.request.contextPath}/food">See the menu →</a></section>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>
