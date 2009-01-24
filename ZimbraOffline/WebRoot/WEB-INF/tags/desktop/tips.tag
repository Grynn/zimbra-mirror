<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>
<%@ taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>
<%@ attribute name="userAgent" required="true" %>

<tr>
  <td class="ZPanelBottom">
    <table width="100%" cellpadding="0" cellspacing="0">
      <tr>
        <td><a href="javascript:zd.toggle('helpTips')"><img src="/zimbra/img/startup/ImgHelp.gif" border="0"></a></td>
        <td>
          <div id="helpTips" style="display:none;overflow:hidden">
            <table align="right" cellpadding="0" cellspacing="0">
              <tr>
                <td><a href="http://www.zimbra.com/products/desktop.html" target="_blank"><fmt:message key='TipsHome'/></a></td>
                <td>&nbsp;&nbsp;&nbsp;&#8226;&nbsp;&nbsp;&nbsp;</td>
                <td><a href="http://wiki.zimbra.com/index.php?title=Yahoo!_Zimbra_Desktop_FAQ" target="_blank"><fmt:message key='TipsFaq'/></a></td>
                <td>&nbsp;&nbsp;&nbsp;&#8226;&nbsp;&nbsp;&nbsp;</td>
                <td><a href="http://wiki.zimbra.com/index.php?title=Yahoo!_Zimbra_Desktop" target="_blank"><fmt:message key='TipsNotes'/></a></td>
                <td>&nbsp;&nbsp;&nbsp;&#8226;&nbsp;&nbsp;&nbsp;</td>
                <td><a href="http://www.zimbra.com/desktop/help/${pageContext.request.locale}/Getting_Started/Zimbra_User_Help.htm" target="_blank"><fmt:message key='TipsHelp'/></a></td>
                <td>&nbsp;&nbsp;&nbsp;&#8226;&nbsp;&nbsp;&nbsp;</td>
                <td><a href="http://www.zimbra.com/forums/zimbra-desktop/" target="_blank"><fmt:message key='TipsForums'/></a></td>
                <c:if test="${zdf:isPrism(userAgent)}">
                  <td>&nbsp;&nbsp;&nbsp;&#8226;&nbsp;&nbsp;&nbsp;</td>
                  <td><a href="javascript:window.platform.openURI('${zdf:addAuthToken(zdf:getBaseUri())}');"><fmt:message key='TipsOpenInBrowser'/></a></td>
                </c:if>
              </tr>
            </table>
          </div>
        </td>
      </tr>
    </table>
  </td>
</tr>