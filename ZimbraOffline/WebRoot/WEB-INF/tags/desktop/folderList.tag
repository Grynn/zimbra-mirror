<%@ tag body-content="empty" %>
<%@ attribute name="label" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>
<%@ taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>

<fmt:setBundle basename="/desktop/ZdMsg" scope="request"/>

<script type="text/javascript">
<!--
function toggleFolder() {
    zd.toggle("folders");
}

function folderName() {
    var folder = document.getElementsByName("folder");
    var folders = document.getElementsByName("folderName");

    if (folder[0].checked) {
        for (var i = 0; i < folders.length; i++) {
            if (folders[i].checked)
                return folders[i].value;
        }
    }
    return "";
}

//-->
</script>

<table width="100%">
    <tr>
        <td>
            <table>
                <tr>
                    <td width="1%"><input type="checkbox" name="folder" onClick="toggleFolder()"></td>
                    <td><nobr><fmt:message key="${label}"/></nobr></td>
                </tr>
            </table>
        </td>
    </tr>
    <tr align="left" id="folders" style="display:none">
        <td>
            <table align="left" style="margin-left: 50px;">
                <c:set var="first" value="${true}"/>
                <zm:forEachFolder var="folder">
                <c:set var="folderName" value="${zm:getFolderPath(pageContext, folder.id)}"/>
                <c:if test="${first}">
                </c:if>
                <tr>
                    <td width="1%"><input type="radio" name="folderName" value="${folderName}" ${first ? "checked" : ""}></td>
                    <td><nobr>${folderName}</nobr></td>
                </tr>
                <c:set var="first" value="${false}"/>
                </zm:forEachFolder>
            </table>
        </td>
    </tr>
</table>
