<%@ tag body-content="scriptless" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<zm:getMailbox var="mailbox"/>

<tr>
	<td colspan=100 align=middle>
		<c:if test="${empty requestScope.statusMessage}">
			<div style='visibility:hidden'>
		</c:if>
		<c:if test="${!empty requestScope.statusMessage}">
			<div style='width:200px' class="niftyError">
		</c:if>
			<b class="rtopNiftyError">
				<b class="r1"></b>
				<b class="r2"></b>
				<b class="r3"></b>
				<b class="r4"></b>
			</b>
			<c:if test="${empty requestScope.statusMessage}">
				<div>&nbsp;</div>
			</c:if>
			<c:if test="${!empty requestScope.statusMessage}">
				<div class='${requestScope.statusClass} unread'>${requestScope.statusMessage}</div>
			</c:if>
			<b class="rbottomNiftyError">
				<b class="r4"></b>
				<b class="r3"></b>
				<b class="r2"></b>
				<b class="r1"></b>
			</b>
		</div>
	</td>
</tr>
