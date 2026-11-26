<%@ page pageEncoding="UTF-8" %>
<%@ include file="includes/tags.jspf" %><c:set var="pageTitle" value="Trang chủ"/><%@ include file="includes/header.jspf" %>
<section class="page-heading"><div><div class="eyebrow">PHIÊN LÀM VIỆC</div><h1>Xin chào, <c:out value="${account.fullName}"/>.</h1><p class="muted">Bạn đã đăng nhập thành công.</p></div><span class="badge purple"><c:out value="${account.role}"/></span></section>
<div class="card"><h2>Thông tin tài khoản</h2><dl><dt>Tên đăng nhập</dt><dd><c:out value="${account.username}"/></dd><dt>Email</dt><dd><c:out value="${account.email}"/></dd><dt>Vai trò</dt><dd><c:out value="${account.role}"/></dd></dl>
<c:choose><c:when test="${pageContext.request.isUserInRole('ADMIN')}"><a class="btn primary" href="<c:url value='/admin'/>">Mở trang quản trị →</a></c:when><c:otherwise><div class="alert info">Tài khoản USER đã sẵn sàng. Các chức năng quản trị chỉ dành cho ADMIN.</div></c:otherwise></c:choose></div>
<%@ include file="includes/footer.jspf" %>
