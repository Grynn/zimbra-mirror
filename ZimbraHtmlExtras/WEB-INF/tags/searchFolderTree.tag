<%@ tag body-content="empty" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div style='width:140px' class="niftyLabel">
    <b class="rtopNiftyLabel">
        <b class="r1"></b>
        <b class="r2"></b>
        <b class="r3"></b>
        <b class="r4"></b>
    </b>
			<span style='cursor:pointer;'>
				&nbsp;<app:img src="opentriangle.gif" width='11' height='11'/> <fmt:message key="searches"/><br>
				<table border=0 cellpadding=3 cellspacing=0 width=100%>
                    <tr>
                        <td>
                            <table border=0 cellpadding=2 cellspacing=2 width=100% bgcolor="#FFFFFF">
                                <tr>
                                    <td>
                                        <zm:forEachFolder var="folder">
                                            <c:if test="${folder.isSearchFolder and (folder.depth eq 0)}">
                                                <app:overviewSearchFolder folder="${folder}"/>
                                            </c:if>
                                        </zm:forEachFolder>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
			</span>
    <b class="rbottomNiftyLabel">
        <b class="r4"></b>
        <b class="r3"></b>
        <b class="r2"></b>
        <b class="r1"></b>
    </b>
</div>