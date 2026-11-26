<%@ page pageEncoding="UTF-8" %>
<%@ include file="../../includes/tags.jspf" %><c:set var="pageTitle" value="${editing ? 'Chỉnh sửa' : 'Thêm mới'} Category"/><%@ include file="../../includes/header.jspf" %>
<section class="page-heading"><div><div class="eyebrow">QUẢN TRỊ / CATEGORY</div><h1>${editing ? 'Chỉnh sửa' : 'Thêm mới'} danh mục</h1><p class="muted">Các trường có dấu * là bắt buộc.</p></div></section>
<div class="card form-card"><c:choose><c:when test="${editing}"><c:url var="submitUrl" value="/admin/categories/${recordId}"/></c:when><c:otherwise><c:url var="submitUrl" value="/admin/categories"/></c:otherwise></c:choose>
<form:form modelAttribute="form" action="${submitUrl}" method="post"><form:errors path="*" cssClass="error-summary" element="div"/>
<label for="name">Tên danh mục <span class="required">*</span></label><form:input path="name" id="name" required="required" maxlength="100"/>
<label for="description">Mô tả</label><form:textarea path="description" id="description" rows="5" maxlength="1000"/>

<label class="checkbox"><form:checkbox path="enabled"/> Cho phép hoạt động</label><div class="actions form-actions"><button class="btn primary">Lưu danh mục</button><a class="btn secondary" href="<c:url value='/admin/categories'/>">Hủy</a></div></form:form></div>
<%@ include file="../../includes/footer.jspf" %>
