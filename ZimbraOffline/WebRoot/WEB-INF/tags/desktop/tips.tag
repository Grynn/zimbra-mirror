<%--
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
--%>
<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>
<%@ taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>
<%@ attribute name="userAgent" required="true" %>

  <table class="ZPanelBottom" align="center" cellpadding="4" cellspacing="0">
    <tr>
      <td><a href="http://www.zimbra.com/products/desktop.html" target="_blank"><fmt:message key='TipsHome'/></a></td>
      <td>&#8226;</td>
      <td><a href="http://www.zimbra.com/desktop/help/en_US/Zimbra_Mail_Help.htm" target="_blank"><fmt:message key='TipsHelp'/></a></td>
      <td>&#8226;</td>
      <td><a href="http://wiki.zimbra.com/index.php?title=Yahoo!_Zimbra_Desktop" target="_blank"><fmt:message key='TipsNotes'/></a></td>
      <td>&#8226;</td>
      <td><a href="http://wiki.zimbra.com/index.php?title=Yahoo!_Zimbra_Desktop_FAQ" target="_blank"><fmt:message key='TipsFaq'/></a></td>
      <td>&#8226;</td>
      <td><a href="http://www.zimbra.com/forums/zimbra-desktop/" target="_blank"><fmt:message key='TipsForums'/></a></td>
      <c:if test="${zdf:isPrism(userAgent)}">
        <td>&#8226;</td>
        <td><a href="javascript:window.platform.openURI('${zdf:addAuthToken(zdf:getBaseUri(), devMode)}');"><fmt:message key='TipsOpenInBrowser'/></a></td>
      </c:if>
    </tr>
  </table>
