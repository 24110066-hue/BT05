<%@ page pageEncoding="UTF-8" %>
<%@ include file="../includes/tags.jspf" %><c:set var="pageTitle" value="Đăng ký"/><%@ include file="../includes/header.jspf" %>
<div class="card form-card auth-register"><div class="eyebrow">TÀI KHOẢN MỚI</div><h1>Đăng ký</h1><p class="muted">Tài khoản mới có quyền người dùng (USER).</p>
<c:url var="submitUrl" value="/register"/><form:form method="post" action="${submitUrl}" modelAttribute="form"><form:errors path="*" cssClass="error-summary" element="div"/>
<label for="fullName">Họ và tên</label><form:input path="fullName" id="fullName" maxlength="100" required="required" autocomplete="name"/>
<label for="username">Tên đăng nhập</label><form:input path="username" id="username" maxlength="40" required="required" autocomplete="username"/><small>3–40 ký tự: chữ, số, dấu _ . -</small>
<label for="email">Email</label><form:input path="email" id="email" type="email" maxlength="120" required="required" autocomplete="email"/>
<label for="password">Mật khẩu</label><form:password path="password" id="password" minlength="8" maxlength="72" required="required" autocomplete="new-password"/><small>Ít nhất 8 ký tự, tối đa 72 byte UTF-8.</small>
<label for="confirmPassword">Nhập lại mật khẩu</label><form:password path="confirmPassword" id="confirmPassword" required="required" autocomplete="new-password"/>
<button class="btn primary full">Tạo tài khoản</button></form:form><p class="auth-switch">Đã có tài khoản? <a href="<c:url value='/login'/>">Đăng nhập</a></p></div>
<%@ include file="../includes/footer.jspf" %>
