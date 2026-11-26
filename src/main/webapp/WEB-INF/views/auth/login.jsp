<%@ page pageEncoding="UTF-8" %>
<%@ include file="../includes/tags.jspf" %><c:set var="pageTitle" value="Đăng nhập"/><%@ include file="../includes/header.jspf" %>
<section class="auth-layout"><div class="auth-intro"><div class="eyebrow">CATALOG MANAGEMENT</div><h1>Mọi thứ cần quản lý,<br>trong một nơi.</h1><p>Quản lý danh mục và tài khoản với quyền truy cập rõ ràng.</p><div class="intro-note">Đăng nhập để bắt đầu phiên làm việc của bạn.</div></div>
<div class="card auth-card"><div class="eyebrow">CHÀO MỪNG TRỞ LẠI</div><h2>Đăng nhập</h2><p class="muted">Nhập thông tin tài khoản của bạn.</p>
<c:if test="${param.error != null}"><div class="alert danger">Tên đăng nhập hoặc mật khẩu không đúng, hoặc tài khoản đã bị khóa.</div></c:if>
<c:if test="${param.logout != null}"><div class="alert success">Bạn đã đăng xuất.</div></c:if>
<c:if test="${param.changed != null}"><div class="alert danger">Tài khoản đã thay đổi. Vui lòng đăng nhập lại.</div></c:if>
<form method="post" action="<c:url value='/login'/>"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
<label for="username">Tên đăng nhập</label><input id="username" name="username" autocomplete="username" required maxlength="40" autofocus>
<label for="password">Mật khẩu</label><input id="password" name="password" type="password" autocomplete="current-password" required>
<button class="btn primary full">Đăng nhập</button></form><p class="auth-switch">Chưa có tài khoản? <a href="<c:url value='/register'/>">Đăng ký ngay</a></p></div></section>
<%@ include file="../includes/footer.jspf" %>
