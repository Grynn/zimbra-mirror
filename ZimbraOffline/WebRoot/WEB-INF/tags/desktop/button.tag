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

