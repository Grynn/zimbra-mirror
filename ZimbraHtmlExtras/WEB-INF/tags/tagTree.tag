<%@ tag body-content="empty" %>

<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class=Tree>
    <table width=100% cellpadding=0 cellspacing=0>
        <tr><th><fmt:message key="tags"/></th></tr>
        <zm:forEachTag var="tag">
            <app:overviewTag tag="${tag}"/>
        </zm:forEachTag>
        <tr><td>&nbsp;</td></tr>
    </table>
</div>
