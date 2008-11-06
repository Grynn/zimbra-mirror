<%@ tag body-content="empty" %>
<%@ attribute name="onclick" required="true" %>
<%@ attribute name="text" required="true" %>
<%@ attribute name="primary" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="color" value="Grey"></c:set>
<c:if test="${primary eq null || primary eq 'true'}">
    <c:set var="color" value="Blue"></c:set>
</c:if>

<span class="Z${color}Button" onclick="${onclick}">
    <table onmouseover="this.className='Over'"
	onmousedown="this.className='Down';return false"
	onmouseup  ="this.className='Over'"
	onmouseout ="this.className=''">
	<tr>
	    <td><div class="Img${color}Button_L"></div></td>
	    <td class="Img${color}Button title">${text}</td>
	    <td><div class="Img${color}Button_R"></div></td>
	</tr>
    </table>
</span>

