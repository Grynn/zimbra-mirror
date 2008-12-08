<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>
<%@ taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>

<c:if test="${not zdf:checkAuthToken(pageContext.request)}">
	<c:redirect url="/desktop/reject.jsp"/>
</c:if>
