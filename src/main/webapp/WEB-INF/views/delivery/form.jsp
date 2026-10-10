<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="base" value="${pageContext.request.contextPath}/delivery" />
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>${editing ? 'Edit delivery' : 'New delivery'}</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/delivery.css">
</head>
<body>
  <header class="bar">
    <h1>Deliveries</h1>
    <a class="btn" href="${base}">Back to list</a>
  </header>

  <main>
    <section class="card">
      <h2>
        <c:choose>
          <c:when test="${editing}">Edit delivery <c:out value="${delivery.deliveryId}" /></c:when>
          <c:otherwise>New delivery</c:otherwise>
        </c:choose>
      </h2>

      <c:if test="${not empty errors}">
        <div class="notice error" role="alert">
          Fix the following and save again:
          <ul>
            <c:forEach items="${errors}" var="e"><li><c:out value="${e}" /></li></c:forEach>
          </ul>
        </div>
      </c:if>

      <form method="post" action="${base}">
        <input type="hidden" name="action" value="${editing ? 'update' : 'create'}">
        <c:if test="${editing}">
          <input type="hidden" name="deliveryId" value="<c:out value='${delivery.deliveryId}' />">
        </c:if>

        <div class="field">
          <label for="orderId">Order ID</label>
          <input id="orderId" name="orderId" required maxlength="10" placeholder="O001"
                 value="<c:out value='${delivery.orderId}' />">
        </div>

        <div class="row">
          <div class="field">
            <label for="driverName">Driver name</label>
            <input id="driverName" name="driverName" maxlength="60"
                   value="<c:out value='${delivery.driverName}' />">
            <span class="hint">Leave empty while the delivery is pending.</span>
          </div>
          <div class="field">
            <label for="driverPhone">Driver phone</label>
            <input id="driverPhone" name="driverPhone" type="tel" maxlength="16" placeholder="0771234567"
                   value="<c:out value='${delivery.driverPhone}' />">
          </div>
        </div>

        <div class="field">
          <label for="deliveryAddress">Delivery address</label>
          <input id="deliveryAddress" name="deliveryAddress" required maxlength="200"
                 value="<c:out value='${delivery.deliveryAddress}' />">
        </div>

        <div class="row">
          <div class="field">
            <label for="estimatedMinutes">Estimated time (minutes)</label>
            <input id="estimatedMinutes" name="estimatedMinutes" type="number" min="1" max="240" required
                   value="${delivery.estimatedMinutes}">
          </div>
          <div class="field">
            <label for="status">Status</label>
            <select id="status" name="status">
              <c:forEach items="${statuses}" var="s">
                <option value="${s}" ${s == delivery.status ? 'selected' : ''}>${s.label}</option>
              </c:forEach>
            </select>
          </div>
        </div>

        <div class="form-actions">
          <button class="btn primary" type="submit">${editing ? 'Save changes' : 'Create delivery'}</button>
          <a class="btn" href="${base}">Cancel</a>
        </div>
      </form>
    </section>
  </main>
</body>
</html>
