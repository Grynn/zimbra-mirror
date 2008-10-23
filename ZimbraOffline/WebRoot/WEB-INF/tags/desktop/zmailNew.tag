<%@ tag body-content="empty" %>
<%@ attribute name="uri" required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>

<fmt:setBundle basename="/desktop/ZdMsg" scope="request"/>

<script type="text/javascript">
<!--
function InitScreen() {
    SetPort();
}

function SetPort() {
    if (zd.isDisabled("port")) {
        if (zd.isChecked("ssl")) {
            zd.set("port", "443");
        } else {
            zd.set("port", "80");
        }
    }
}

function OnPickType() {
    window.location = '/zimbra/desktop/new.jsp';
}

function OnCancel() {
    window.location = '/zimbra/desktop/console.jsp';
}

function OnSubmit() {
    beforeSubmit();
    zd.enable("port");
    zmailNew.submit();
}

function beforeSubmit() {
    disableButtons();
    zd.set("whattodo", "<span class='ZOfflineNotice'><fmt:message key='Processing'/></span>");
}

function disableButtons() {
    //zd.disable("typeButton");
    zd.disable("cancelButton");
    zd.disable("saveButton");
}
//-->
</script>


<span class="padding">
<form name="zmailNew" action="${uri}" method="POST">

    <input type="hidden" name="verb" value="add">
    <input type="hidden" name="accntType" value="ZimbraAcct">

    <table cellpadding="10">
        <tr>
            <td class="${zdf:isValid(bean, 'accountName') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='Description'/></td>
            <td><input class="ZField" type="text" id="accountName" name="accountName" value="${bean.accountName}">
                        <span id='service_hint' class='ZHint'><fmt:message key='DescHint2'/></span></td>
        </tr>
        <tr id='emailRow'>
           <td class="${zdf:isValid(bean, 'email') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='EmailAddr'/></td>
            <td><input class="ZField" type="text" id="email" name="email" value="${bean.email}">
                    <span id='email_hint' class='ZHint'>
            </td>
        </tr>
        <tr id='passwordRow'>
            <td class="${zdf:isValid(bean, 'password') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='Password'/></td>
            <td><input class="ZField" type="password" id="password" name="password" value="${bean.password}"></td>
        </tr>

        <tr id='mailServerRow'>
            <td class="${zdf:isValid(bean, 'host') ? 'ZFieldLabel' : 'ZFieldError'}" valign="top"><br><fmt:message key='ZmServer'/></td>
            <td>
                <table cellspacing=0 cellpadding=0>
                    <tr>
                        <td><input style='width:240px' class="ZField" type="text" id="host" name="host" value="${bean.host}">
                            <br><span class='ZHint'><fmt:message key='ZmSvrHint'/></span>
                        </td>
                        <td>&nbsp;&nbsp;&nbsp;</td>
                        <td class="${zdf:isValid(bean, 'port') ? 'ZFieldLabel' : 'ZFieldError'}" valign="top"><fmt:message key='Port'/></td>
                        <td width=100%  valign="top"><input style='width:50px' class="ZField" type="text" id="port" name="port" value="${bean.port}" ${bean.defaultPort ? 'disabled' : ''}>
                        <c:if test="${bean.defaultPort}">&nbsp;&nbsp;<a href="#" onclick="zd.enable('port');this.style.display='none'"><fmt:message key='Edit'/></a></c:if>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
 <tr id='mailSecureRow'>
            <td class="ZFieldLabel"></td>
            <td><table cellpadding="0" cellspacing="0" border="0"><tr>
            
            <td><input type="checkbox" id="ssl" name="ssl" ${bean.ssl ? 'checked' : ''} onclick="SetPort()"></td><td><fmt:message key='UseSSL'/>
            </td></tr></table>
            </td>
        </tr>
        <tr>
            <td class="ZFieldLabel"><fmt:message key='ZmSyncFrequency'/></td>
            <td>
                <select class="ZSelectSmall" id="syncFreqSecs" name="syncFreqSecs">
                    <option value="-1" ${bean.syncFreqSecs == -1 ? 'selected' : ''}><fmt:message key='SyncManually'/></option>
                    <option value="0" ${bean.syncFreqSecs == 0 ? 'selected' : ''}><fmt:message key='SyncNewArrive'/></option>
                    <option value="60" ${bean.syncFreqSecs == 60 ? 'selected' : ''}><fmt:message key='SyncEveryMin'/></option>
                    <option value="300" ${bean.syncFreqSecs == 300 ? 'selected' : ''}><fmt:message key='SyncEvery5'/></option>
                    <option value="900" ${bean.syncFreqSecs == 900 ? 'selected' : ''}><fmt:message key='SyncEvery15'/></option>
                    <option value="1800" ${bean.syncFreqSecs == 1800 ? 'selected' : ''}><fmt:message key='SyncEvery30'/></option>
                    <option value="3600" ${bean.syncFreqSecs == 3600 ? 'selected' : ''}><fmt:message key='SyncEvery1Hr'/></option>
                    <option value="14400" ${bean.syncFreqSecs == 14400 ? 'selected' : ''}><fmt:message key='SyncEvery4Hr'/></option>
                    <option value="43200" ${bean.syncFreqSecs == 43200 ? 'selected' : ''}><fmt:message key='SyncEvery12Hr'/></option>
                </select>
            </td>
        </tr>
       
    </table>

</form>
