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

<table border=0 cellpadding=0 cellspacing=0 width=100%>
<tr class="topbar">
	<td class="leftPane">
		<a href="http://www.zimbra.com" target="_blank"><app:img src="zimbra_logo.gif" width='150' height='50' border='0'/></a>
	</td>
    <td valign=top colspan=2>
		<table border=0 align=right><tr>
			<td class="username" id="username">${mailbox.name}</td>
			<td class='cellSeparator'>|</td>
			<td class="nowrap"><a href="login?op=options">Settings</a></td>
			<td class='cellSeparator'>|</td>
			<td class="nowrap"><a href="login?op=help">Help</a></td>
			<td class='cellSeparator'>|</td>
			<td class="nowrap"><a href="login?op=logout">Sign out</a></td>
		</tr></table>
		<br><br>
		<form method="get" action="clv">
			<table border=0 width=100%>
			<tr>
				<td><input id="searchbox" type="text" size=25 maxlength=256 name=sq></td>
				<td><input id="searchMailButton" type="submit" name="mail" value="Search Mail"></td>
				<td><input id="searchWebButton" type="submit" name="web" value="Search the Web"></td>
				<td class="searchOptions">
					<a href="javascript:;">Show search options</a><br>
					<a href="javascript:;">Create a filter</a>
				</td>
				<td width=100%></td>
			</tr>
			<tr>
				<td colspan=100 align=middle>
					<div style='width:200px' class="niftyError" id='errorContainer'>
						<b class="rtopNiftyError">
							<b class="r1"></b>
							<b class="r2"></b>
							<b class="r3"></b>
							<b class="r4"></b>
						</b>
						<span id='error' class='error'>
							Error goes here
						</span>
						<b class="rbottomNiftyError">
							<b class="r4"></b>
							<b class="r3"></b>
							<b class="r2"></b>
							<b class="r1"></b>
						</b>
					</div>
				</td>
			</tr>
			</table>
		</form>
	</td>
</tr>
<tr>
	<td valign=top class="leftPane" id="leftPane">
		<app:overviewTree selected="${selected}" contacts="${contacts}" tags="${tags}" searches="${searches}" folders="${folders}"/>
	</td>
	<td valign=top>
		<table border=0 cellpadding=0 cellspacing=0 width=100%>
		<tr>
			<td id="rightPane">
				<jsp:doBody/>
			</td>
		</tr>
		<tr>
			<td><br></td>
		</tr>
		<tr>
			<td class="quota" id="quota">
				<c:set var="max" value="${mailbox.attrs.zimbraMailQuota[0]}"/>
				You are currently using ${zm:displaySize(mailbox.size)}
				of ${max==0 ? zm:m(pageContext, 'unlimited') : zm:displaySize(max)}.
			</td>
		</tr>
		<tr>
			<td class="footer footer-small">
				<a href="http://www.zimbra.com/privacy.html" target="_blank">Privacy Policy</a> -
				<a href="http://www.zimbra.com/license/" target="_blank">License</a> -
				<a href="http://www.zimbra.com/legal.html" target="_blank">Trademarks</a><br>
				&copy; 2006 Zimbra Inc.
			</td>
		</tr>
		</table>
	</td>
	<td>&nbsp;</td>
</tr>
</table>
