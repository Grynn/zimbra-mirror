<%@ tag body-content="empty" %>
<%@ attribute name="onclick" required="true" %>
<%@ attribute name="text" required="true" %>
<%@ attribute name="type" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<span class="ZButton" onclick="${onclick}">
  <span class="ZButton ${type}">
    <table
      onmouseover = "this.className = 'Over'"
      onmousedown = "this.className = 'Down'; return false"
      onmouseup	  = "this.className = 'Over'"
      onmouseout  = "this.className = ''">
      <tr>
<td class="ZButton Title">${text}</td>
      </tr>
    </table>
  </span>
</span>

