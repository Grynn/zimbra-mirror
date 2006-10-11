<%@ tag body-content="empty" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class=Tree>
    <table width=100% cellpadding=0 cellspacing=0>
        <tr><th><fmt:message key="searches"/></th></tr>
        <zm:forEachFolder var="folder">
            <c:if test="${folder.isSearchFolder and (folder.depth eq 0)}">
                <app:overviewSearchFolder folder="${folder}"/>
            </c:if>
        </zm:forEachFolder>
        <tr><td>&nbsp;</td></tr>
    </table>
</div>
