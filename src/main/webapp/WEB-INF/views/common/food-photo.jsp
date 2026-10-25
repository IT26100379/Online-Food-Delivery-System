<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="nameLower" value="${fn:toLowerCase(param.foodName)}"/>
<c:set var="categoryLower" value="${fn:toLowerCase(param.foodCategory)}"/>
<c:choose>
  <c:when test="${fn:contains(nameLower,'chicken') and fn:contains(nameLower,'rice')}"><c:set var="photoId" value="photo-1512058564366-18510be2db19"/></c:when>
  <c:when test="${fn:contains(nameLower,'pizza') or fn:contains(categoryLower,'pizza')}"><c:set var="photoId" value="photo-1513104890138-7c749659a591"/></c:when>
  <c:when test="${fn:contains(nameLower,'burger') or fn:contains(categoryLower,'burger')}"><c:set var="photoId" value="photo-1568901346375-23c9450c58cd"/></c:when>
  <c:when test="${fn:contains(nameLower,'fried rice') or fn:contains(nameLower,'rice') or fn:contains(categoryLower,'rice')}"><c:set var="photoId" value="photo-1603133872878-684f208fb84b"/></c:when>
  <c:when test="${fn:contains(nameLower,'noodle') or fn:contains(nameLower,'pasta')}"><c:set var="photoId" value="photo-1569718212165-3a8278d5f624"/></c:when>
  <c:when test="${fn:contains(categoryLower,'beverage') or fn:contains(nameLower,'juice') or fn:contains(nameLower,'drink')}"><c:set var="photoId" value="photo-1544145945-f90425340c7e"/></c:when>
  <c:when test="${fn:contains(categoryLower,'dessert') or fn:contains(nameLower,'cake') or fn:contains(nameLower,'ice cream')}"><c:set var="photoId" value="photo-1488477181946-6428a0291777"/></c:when>
  <c:otherwise><c:set var="photoId" value="photo-1546069901-ba9599a7e63c"/></c:otherwise>
</c:choose>
<img class="yc-food-photo" loading="lazy" decoding="async" alt="${fn:escapeXml(param.foodName)}" src="https://images.unsplash.com/${photoId}?auto=format&amp;fit=crop&amp;w=900&amp;q=80" onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/assets/food-photo-fallback.svg';"/>
