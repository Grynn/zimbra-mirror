<%@ tag body-content="empty" dynamic-attributes="dynattrs" %>
<%@ attribute name="urlTarget" rtexprvalue="true" required="true" %>
<%@ attribute name="context" rtexprvalue="true" required="true" type="com.zimbra.cs.jsp.tag.SearchContext"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>
<c:if test="${context.searchResult.hasNextPage}">
	<c:url value="${urlTarget}" var="url">
		<c:param name='so' value='${context.searchResult.nextOffset}'/>
		<c:if test="${! empty context.id}"><c:param name='sc' value='${context.id}'/></c:if>
		<c:if test="${!empty param.sq}"><c:param name='sq' value='${param.sq}'/></c:if>
		<c:if test="${!empty param.sfi}"><c:param name='sfi' value='${param.sfi}'/></c:if>
		<c:if test="${!empty param.sti}"><c:param name='sti' value='${param.sti}'/></c:if>
	</c:url>
	<a accesskey="f" href="${url}">Older &rsaquo;</a>
</c:if>
