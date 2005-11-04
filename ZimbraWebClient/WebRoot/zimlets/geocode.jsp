<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"  %>
<c:set var="url">http://api.local.yahoo.com/MapsService/V1/geocoding?appid=ZimbraMail&location=<c:out value="${param.address}" /></c:set>
<c:import url="${url}"/>