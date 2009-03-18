<%--
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008 Zimbra, Inc.
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
<%@ attribute name="onclick" required="true" %>
<%@ attribute name="text" required="true" %>
<%@ attribute name="type" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<span class="ZButton" onclick="${onclick}">
  <span class="ZButton ${type}">
    <table
      onmousedown = "this.className = 'Down'; return false"
      onmouseout  = "this.className = ''; return true"
      onmouseover = "this.className = 'Over'; return true"
      onmouseup	  = "this.className = 'Over'; return true">
      <tr>
        <td class="ZButton Title">
	  <a href="javascript:${onclick}"
	    onblur="this.parentNode.parentNode.parentNode.parentNode.className=''"
	    onfocus="window.status=''; if (this.parentNode.parentNode.parentNode.parentNode.className != 'Down') this.parentNode.parentNode.parentNode.parentNode.className='Over'"
	    onmousedown="window.status=''; this.parentNode.parentNode.parentNode.parentNode.className='Down'; return false"
	    onmouseover="window.status=''; this.parentNode.parentNode.parentNode.parentNode.className='Over'; return true">
	    ${text}
	  </a>
	</td>
      </tr>
    </table>
  </span>
</span>

