<%@ page pageEncoding="UTF-8" %>
<%@ include file="../../includes/tags.jspf" %><c:set var="pageTitle" value="${editing ? 'Chỉnh sửa' : 'Thêm mới'} User"/><%@ include file="../../includes/header.jspf" %>
<section class="page-heading"><div><div class="eyebrow">QUẢN TRỊ / USER</div><h1>${editing ? 'Chỉnh sửa' : 'Thêm mới'} tài khoản</h1><p class="muted">Các trường có dấu * là bắt buộc.</p></div></section>
<div class="card form-card"><c:choose><c:when test="${editing}"><c:url var="submitUrl" value="/admin/users/${recordId}"/></c:when><c:otherwise><c:url var="submitUrl" value="/admin/users"/></c:otherwise></c:choose>
<form:form modelAttribute="form" action="${submitUrl}" method="post"><form:errors path="*" cssClass="error-summary" element="div"/>
<div class="form-grid"><div><label for="username">Tên đăng nhập <span class="required">*</span></label><form:input path="username" id="username" required="required" maxlength="40"/><small>3–40 ký tự: chữ, số, dấu _ . -</small></div><div><label for="fullName">Họ và tên <span class="required">*</span></label><form:input path="fullName" id="fullName" required="required" maxlength="100"/></div></div>
<label for="email">Email <span class="required">*</span></label><form:input path="email" id="email" type="email" required="required" maxlength="120"/>
<label for="password">Mật khẩu ${editing ? '(để trống để giữ mật khẩu hiện tại)' : '*'}</label><form:password path="password" id="password" minlength="8" maxlength="72" autocomplete="new-password"/><small>Ít nhất 8 ký tự, tối đa 72 byte UTF-8. Bắt buộc khi thêm mới.</small>
<label for="role">Vai trò <span class="required">*</span></label><form:select path="role" id="role"><form:options items="${roles}"/></form:select>

<label class="checkbox"><form:checkbox path="enabled"/> Cho phép hoạt động</label><div class="actions form-actions"><button class="btn primary">Lưu tài khoản</button><a class="btn secondary" href="<c:url value='/admin/users'/>">Hủy</a></div></form:form></div>
<%@ include file="../../includes/footer.jspf" %>
