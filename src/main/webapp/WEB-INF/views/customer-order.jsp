<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="Place order"/></jsp:include>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/food-photos.css">
<main class="page-shell narrow" id="main-content">
  <a class="section-link" href="${pageContext.request.contextPath}/food">← Back to food menu</a>
  <div class="sub-hero"><span class="eyebrow">YUMMYCART CHECKOUT</span><h1>Place your order</h1><p>Choose the quantity and confirm your food order.</p></div>
  <div class="card">
    <div class="yc-checkout-photo">
      <jsp:include page="/WEB-INF/views/common/food-photo.jsp">
        <jsp:param name="foodName" value="${food.name}" />
        <jsp:param name="foodCategory" value="${food.category}" />
      </jsp:include>
    </div>
    <h2><c:out value="${food.name}"/></h2>
    <p class="muted">Food ID: <c:out value="${food.foodId}"/> · Rs. <c:out value="${food.price}"/> per item</p>
    <form action="${pageContext.request.contextPath}/orders" method="post">
      <input type="hidden" name="action" value="place">
      <input type="hidden" name="foodId" value="<c:out value='${food.foodId}'/>">
      <input type="hidden" name="csrf" value="<c:out value='${csrf}'/>">
      <label for="quantity">Quantity</label>
      <input id="quantity" type="number" name="quantity" value="1" min="1" max="100" required>
      <p class="muted">Total is calculated automatically from the current menu price when you place the order.</p>
      <div class="form-actions"><button type="submit" class="btn">Place order</button><a class="btn btn-light" href="${pageContext.request.contextPath}/food">Cancel</a></div>
    </form>
  </div>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>
