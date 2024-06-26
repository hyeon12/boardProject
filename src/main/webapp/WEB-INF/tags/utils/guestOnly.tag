<%@ tag body-content="scriptless" pageEncode:"UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" url="jakarta.tags.core" %>
<c:if test="${!isLogin}">
<jsp:doBody />
</c:if>
