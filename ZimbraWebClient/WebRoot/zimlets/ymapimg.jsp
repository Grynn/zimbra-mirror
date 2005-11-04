<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"  %>
<c:set var="url">http://api.local.yahoo.com/MapsService/V1/mapImage?appid=ZimbraMail&zoom=4&image_height=245&image_width=345&location=<c:out value="${param.address}" /></c:set>
<c:import url="${url}"/>