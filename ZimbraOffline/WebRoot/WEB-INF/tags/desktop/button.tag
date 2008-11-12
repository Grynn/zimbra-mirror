<%@ tag body-content="empty" %>
<%@ attribute name="onclick" required="true" %>
<%@ attribute name="text" required="true" %>
<%@ attribute name="primary" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<span class="ZButton" onclick="${onclick}">
    <table
	onmouseover	= "this.className = 'Over'"
	onmousedown	= "this.className = 'Down'; return false"
	onmouseup	= "this.className = 'Over'"
	onmouseout	= "this.className = ''">
	<tr>
	    <td class="ZButton title"><nobr>${text}</nobr></td>
	</tr>
    </table>
</span>
