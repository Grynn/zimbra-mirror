<%@ tag body-content="empty" %>
<%@ attribute name="message" rtexprvalue="true" required="true" type="com.zimbra.cs.taglib.bean.ZMessageBean" %>
<%@ attribute name="nosubject" rtexprvalue="true" required="false" %>
<%@ attribute name="startNew" rtexprvalue="true" required="true" %>
<%@ attribute name="externalImageUrl" rtexprvalue="true" required="false" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>


<table border=0 cellpadding=0 cellspacing=0>
<c:if test="${startNew}">
	<tr>
		<td rowspan=2 class="cTopLeft"></td>
		<td class="cTop" colspan=4 height=4></td>
		<td rowspan=2 class="cTopRight"></td>
	</tr>
</c:if>
<c:if test="${!startNew}">
	<tr>
		<td rowspan=2 class="cConnector"></td>
		<td class="cTop" colspan=4 height=4></td>
		<td rowspan=2 class="cTopRight"></td>
	</tr>
</c:if>
<tr>
	<td class="msgHeaderCell">
        <fmt:message var="unknownSender" key="unknownSender"/>
        <app:img src="${message.isFlagged ? 'star_on_sm_2.gif' : 'star_off_2.gif'}" style="cursor:pointer"/>
		<c:out value="${message.displayFrom}" default="${unknownSender}"/>&nbsp;
	</td>
	<td width=100% valign=bottom>
		<div class="msgText">
			to ${fn:escapeXml(message.displayTo)}
		</div>
	</td>
	<td class="msgOptions"><a href="javascript:;"><fmt:message key="moreOptions"/></a></td>
	<td class="nowrap" align=right>
		<c:if test="${message.hasAttachment eq true}">
			&nbsp;<app:img src="paperclip.gif" width='15' height='15' border='0' style="vertical-align:middle"/>
		</c:if>
		${fn:escapeXml(zm:displayMsgDate(pageContext, message.sentDate))}
	</td>
</tr>
</table>
<div class="cMiddle convTopContent">
	<c:set var="body" value="${message.body}"/>
	<c:if test="${body.contentType eq 'text/html'}">
		${body.content}
	</c:if>
	<c:if test="${!(body.contentType eq 'text/html')}">
		${body.textContentAsHtml}
	</c:if>
	<br><br>
	<c:if test="${!empty message.attachments}">
		<div class="msgAttachSep"></div><br>
		<div class="msgAttachContainer">
		<c:forEach var="part" items="${message.attachments}">
      <c:if test="${part.isMssage}">
    		<zm:getMessage var="partMessage" id="${message.id}" part="${part.partName}"/>
    		<c:set var="body" value="${partMessage.body}"/>
    		<c:if test="${body.contentType eq 'text/html'}">
    			${body.content}
    		</c:if>
    		<c:if test="${!(body.contentType eq 'text/html')}">
    			${body.textContentAsHtml}
    		</c:if>
    		<br><br>
  	  </c:if>
  	</c:forEach>  
  	<c:forEach var="part" items="${message.attachments}">
      <c:if test="${!part.isMssage}">
    		<c:set var="pname" value="${part.displayName}"/>
    		<c:if test="${empty pname}">
    			<fmt:message key="unknownContentType" var="pname"><fmt:param value="${part.contentType}"/></fmt:message>
    		</c:if>
    		<c:set var="url" value="/home/~/?id=${message.id}&part=${part.partName}&auth=co"/>    
    		<table border=0 cellpadding=1 cellspacing=1>
    		<tr>
    			<td>
    				<c:choose>
    					<c:when test="${part.isImage}">
    						<img class='msgAttachImage' src="${url}" alt="${part.displayName}"/>
    					</c:when>
    					<c:otherwise>
    						<app:img src="${part.image}" alt="${part.displayName}" title="${part.contentType}"/>
    					</c:otherwise>
    				</c:choose>
    			</td>
    			<td width=7></td>
    			<td>
    				<b>${fn:escapeXml(pname)}</b><br />
    				${part.displaySize}
    				<a target="_blank" href="${url}&disp=i"><fmt:message key="view"/></a>
    				<a href="${url}&disp=a"><fmt:message key="download"/></a>
    			</td>
    		</tr>
    		</table>
    	</c:if>
    </c:forEach>
    </div>
	</c:if>
	<br><br>
</div>
<div class="cMiddle cActionMiddle">
	<span class="msgActionLink"><fmt:message key="reply"/></span>
	<span class="msgActionLink"><fmt:message key="forward"/></span>
	<textarea wrap=soft rows=2 class="msgActionTextArea"></textarea>
</div>
<table border=0 cellpadding=0 cellspacing=0 height=8><tr>
	<td class="cBotLeft cGray"></td>
	<td class="cBot cGray"></td>
	<td class="cBotRight cGray"></td>
</tr></table><br>
