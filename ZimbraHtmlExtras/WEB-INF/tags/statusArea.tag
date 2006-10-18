<%@ tag body-content="scriptless" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<zm:getMailbox var="mailbox"/>

<tr>
	<td colspan=100 align=middle>
		<table border=0 cellpadding=0 cellspacing=0><tr><td>
		<c:if test="${empty requestScope.statusMessage}">
			<div style='visibility:hidden'>
		</c:if>
		<c:if test="${!empty requestScope.statusMessage}">
			<div class="niftyError">
		</c:if>
			<b class="rtopNiftyError">
				<b class="r1"></b>
				<b class="r2"></b>
				<b class="r3"></b>
				<b class="r4"></b>
			</b>
			<c:if test="${empty requestScope.statusMessage}">
				&nbsp;
			</c:if>
			<c:if test="${!empty requestScope.statusMessage}">
				&nbsp;<span class='${requestScope.statusClass} unread'>${requestScope.statusMessage}</span>&nbsp;
			</c:if>
			<b class="rbottomNiftyError">
				<b class="r4"></b>
				<b class="r3"></b>
				<b class="r2"></b>
				<b class="r1"></b>
			</b>
		</div>
		</td></tr></table>
	</td>
</tr>
