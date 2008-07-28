<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>
<%@ taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>

<fmt:setBundle basename="/desktop/ZdMsg" scope="request"/>

<script type="text/javascript">
<!--
function toggleTypes() {
    zd.toggle("types");
}

function dataTypes() {
    var selected = document.getElementsByName("selected");
    var types = null;

    if (selected[0].checked) {
        var type = document.getElementsByName("type");

        for (var i = 0; i < type.length; i++) {
            if (type[i].checked) {
                if (types.length != 0)
                    types += ",";
                types += type[i].value;
            }
        }
        if (types == null)
            alert("<fmt:message key="TypeEmpty"/>");
    } else {
        types = "";
    }
    return types;
}

//-->
</script>

<table>
    <tr>
        <td>
            <table width="100%">
                <tr>
                    <td width="1%"><input type="checkbox" name="selected" onClick="toggleTypes()"></td>
                    <td><nobr><fmt:message key="TypeLabel"/></nobr></td>
                </tr>
            </table>
        </td>
    </tr>
    <tr align="left" id="types" style="display:none">
        <td>
            <table align="left" style="margin-left: 40px;">
                <tr>
                    <td width="1%"><input type="checkbox" name="type" value="message"></td>
                    <td width="1%"><img src="/zimbra/img/startup/ImgEnvelopeOpen.gif"></td>
                    <td><nobr><fmt:message key="TypeMessage"/></nobr></td>
                </tr>
                <tr>
                    <td><input type="checkbox" name="type" value="contact"></td>
                    <td><img src="/zimbra/img/startup/ImgContactsApp.gif"></td>
                    <td><nobr><fmt:message key="TypeContact"/></nobr></td>
                </tr>
                <tr>
                    <td><input type="checkbox" name="type" value="appointment"></td>
                    <td><img src="/zimbra/img/startup/ImgCalendarApp.gif"></td>
                    <td><nobr><fmt:message key="TypeAppointment"/></nobr></td>
                </tr>
                <tr>
                    <td><input type="checkbox" name="type" value="task"></td>
                    <td><img src="/zimbra/img/startup/ImgTaskList.gif"></td>
                    <td><nobr><fmt:message key="TypeTask"/></nobr></td>
                </tr>
                <tr style="display:none">
                    <td><input type="checkbox" name="type" value="note"></td>
                    <td><img src="/zimbra/img/startup/ImgNoteApp.gif"></td>
                    <td><nobr><fmt:message key="TypeNote"/></nobr></td>
                </tr>
                <tr  style="display:none">
                    <td><input type="checkbox" name="type" value="chat"></td>
                    <td><img src="/zimbra/img/startup/ImgImStartChat.gif"></td>
                    <td><nobr><fmt:message key="TypeChat"/></nobr></td>
                </tr>
                <tr>
                    <td><input type="checkbox" name="type" value="wiki"></td>
                    <td><img src="/zimbra/img/startup/ImgNotebook.gif"></td>
                    <td><nobr><fmt:message key="TypeWiki"/></nobr></td>
                </tr>
                <tr>
                    <td><input type="checkbox" name="type" value="document"></td>
                    <td><img src="/zimbra/img/startup/ImgFolder.gif"></td>
                    <td><nobr><fmt:message key="TypeDocument"/></nobr></td>
                </tr>
            </table>
        </td>
    </tr>
</table>
