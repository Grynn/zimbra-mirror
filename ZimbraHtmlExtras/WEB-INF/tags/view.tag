<%@ tag body-content="scriptless" %>
<%@ attribute name="selected" rtexprvalue="true" required="false" %>
<%@ attribute name="folders" rtexprvalue="true" required="false" %>
<%@ attribute name="searches" rtexprvalue="true" required="false" %>
<%@ attribute name="contacts" rtexprvalue="true" required="false" %>
<%@ attribute name="calendars" rtexprvalue="true" required="false" %>
<%@ attribute name="ads" rtexprvalue="true" required="false" %>
<%@ attribute name="tags" rtexprvalue="true" required="false" %>
<%@ attribute name="mailbox" rtexprvalue="true" required="true" type="com.zimbra.cs.taglib.bean.ZMailboxBean" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<table border=0 cellpadding=0 cellspacing=0 width=100%>
<tr>
	<td colspan="10" class="topbar">
		<table border=0 cellpadding=0 cellspacing=0 width=100%><tr>
			<td>
				<table border=0 cellpadding=1 cellspacing=1><tr>
					<td class="username">Zmail</td>
					<td class="appLinks"><a href="javascript:;">Calendar</a></td>
					<td class="appLinks"><a href="javascript:;">Documents</a></td>
					<td class="appLinks"><a href="javascript:;">Photos</a></td>
					<td class="appLinks"><a href="javascript:;">Groups</a></td>
					<td class="appLinks"><a href="javascript:;">Web</a></td>
					<td class="appLinks" style='padding-right:0px'><a href="javascript:;">more</a></td>
					<td valign=bottom><app:img src="more.gif" width='11' height='11' border='0'/></td>
				</tr></table>
			</td>
			<td align=right>
				<table border=0><tr>
					<td class="username" id="username">${mailbox.name}</td>
					<td class='cellSeparator'>|</td>
					<td class="nowrap"><a href="javascript:;"><fmt:message key="settings"/></a></td>
					<td class='cellSeparator'>|</td>
					<td class="nowrap"><a href="javascript:;"><fmt:message key="help"/></a></td>
					<td class='cellSeparator'>|</td>
					<td class="nowrap"><a href="<c:url value="/login?op=logout"/>"><fmt:message key="signOut"/></a></td>
				</tr></table>
			</td>
		</tr></table>
	</td>
</tr>
</table>
<div class="contentBody">
	<table border=0 cellpadding=0 cellspacing=0 width=100%>
	<tr>
		<td class="leftPane">
			<a href="http://www.zimbra.com" target="_blank"><app:img src="zimbra_logo.gif" width='150' height='50' border='0'/></a><br><br>
		</td>
	    <td valign=top colspan=2>
			<form method="get" action="clv">
				<br>
				<table border=0 width=100%>
				<tr>
					<td><input id="searchbox" type="text" size=25 maxlength=256 name=sq></td>
					<td><input id="searchMailButton" type="submit" name="mail" value='<fmt:message key="searchMail"/>'></td>
					<td><input id="searchWebButton" type="submit" name="web" value='<fmt:message key="searchWeb"/>'></td>
					<td class="searchOptions" width=100%>
						<a href="javascript:;"><fmt:message key="showSearchOptions"/></a><br>
						<a href="javascript:;"><fmt:message key="createFilter"/></a>
					</td>
				</tr>
				<app:statusArea/>
				</table>
			</form>
		</td>
	</tr>
	<tr>
		<td valign=top class="leftPane">
			<app:overviewTree selected="${selected}" contacts="${contacts}" tags="${tags}" searches="${searches}" folders="${folders}"/>
		</td>
		<td valign=top>
			<table border=0 cellpadding=0 cellspacing=0 width=100%>
			<tr>
				<td><jsp:doBody/></td>
			</tr>
			<tr>
				<td><br></td>
			</tr>
			<tr>
				<td class="quota">
	                <fmt:message var="unlimited" key="unlimited"/>
	                <c:set var="max" value="${mailbox.attrs.zimbraMailQuota[0]}"/>
					You are currently using ${zm:displaySize(mailbox.size)}
					of ${max==0 ? unlimited : zm:displaySize(max)}.
				</td>
			</tr>
			<tr>
				<td class="footer footer-small">
					<span class="copyright">&copy;2007 Zimbra Inc.</span>
					<a href="http://www.zimbra.com/legal.html#copyright" target="_blank"><fmt:message key="copyright"/></a> -
					<a href="http://www.zimbra.com/privacy.html" target="_blank"><fmt:message key="privacyPolicy"/></a> -
					<a href="http://www.zimbra.com/license/" target="_blank"><fmt:message key="license"/></a> -
					<a href="http://www.zimbra.com/legal.html" target="_blank"><fmt:message key="trademarks"/></a>
				</td>
			</tr>
			</table>
		</td>
		<td>&nbsp;</td>
	</tr>
	</table>
</div>
