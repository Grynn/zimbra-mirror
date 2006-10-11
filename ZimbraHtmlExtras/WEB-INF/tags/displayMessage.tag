<%@ tag body-content="empty" %>
<%@ attribute name="message" rtexprvalue="true" required="true" type="com.zimbra.cs.jsp.bean.ZMessageBean" %>
<%@ attribute name="nosubject" rtexprvalue="true" required="false" %>
<%@ attribute name="externalImageUrl" rtexprvalue="true" required="false" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>

<div width=100% height=100% class=Msg>
    <div class=MsgHdr>
        <table width=100% cellpadding=0 cellspacing=0 border=0>
            <tr>
                <td>
                    <table width=100% cellpadding=2 cellspacing=0 border=0>
                        <c:if test="${!nosubject}">
                            <tr><td class='MsgHdrName MsgHdrSub'><fmt:message key="subject"/>:</td><td
                                    class='MsgHdrValue MsgHdrSub'>${fn:escapeXml(message.subject)}</td>
                            </tr>
                        </c:if>
                        <tr>
                            <td colspan="2" class='MsgHdrSender'><c:out value="${message.displayFrom}" default="${zm:m(pageContext, 'unknownSender')}"/>
                        </tr>
                        <c:set var="to" value="${message.displayTo}"/>
                        <c:if test="${!(empty to)}">
                            <tr><td class='MsgHdrName'><fmt:message key="to"/>:</td><td class='MsgHdrValue'><c:out
                                    value="${to}"/></td></tr>
                        </c:if>
                        <c:set var="cc" value="${message.displayCc}"/>
                        <c:if test="${!(empty cc)}">
                            <tr><td class='MsgHdrName'><fmt:message key="cc"/>:</td><td class='MsgHdrValue'><c:out
                                    value="${cc}"/></td></tr>
                        </c:if>
                        <c:set var="bcc" value="${message.displayBcc}"/>
                        <c:if test="${!(empty bcc)}">
                            <tr><td class='MsgHdrName'><fmt:message key="bcc"/>:</td><td class='MsgHdrValue'><c:out
                                    value="${bcc}"/></td></tr>
                        </c:if>
                        <c:set var="replyto" value="${message.displayReplyTo}"/>
                        <c:if test="${!(empty replyto)}">
                            <tr><td class='MsgHdrName'><fmt:message key="replyTo"/>:</td><td class='MsgHdrValue'><c:out
                                    value="${replyto}"/></td></tr>
                        </c:if>
                    </table>
                </td>
                <td valign='top'>
                    <table width=100% cellpadding=2 cellspacing=0 border=0>
                        <tr>
                            <td nowrap align='right'class='MsgHdrSent'>
                                <fmt:message var="dateFmt" key="formatDateSent"/>
                                <fmt:formatDate pattern="${dateFmt}" value="${message.sentDate}"/>
                            </td>
                        </tr>
                        <c:if test="${message.hasTags}">
                            <tr>
                                <td nowrap align='right' class='Tags'>
                                    <c:set var="tags" value="${zm:getTags(pageContext, message.tagIds)}"/>
                                    <c:forEach items="${tags}" var="tag">
                                        <app:img src="${tag.miniImage}"/> <span>${fn:escapeXml(tag.name)}</span>
                                    </c:forEach>
                                </td>
                            </tr>
                        </c:if>
                    </table>
                </td>
            </tr>
        </table>
    </div>

    <c:set var="body" value="${message.body}"/>
    <c:if test="${!empty externalImageUrl and (body.contentType eq 'text/html') and (fn:containsIgnoreCase(body.content,'dfsrc='))}">
    <div class='DisplayImages'>
        <fmt:message key="externalImages"/>&nbsp;<a accesskey='x' href="${externalImageUrl}"><fmt:message key="displayExternalImages"/></a>
    </div>
    </c:if>
    <div class=MsgBody>
        <c:if test="${body.contentType eq 'text/html'}">
                ${body.content}
        </c:if>
        <c:if test="${!(body.contentType eq 'text/html')}">
                ${body.textContentAsHtml}
        </c:if>
    </div>
</div>
