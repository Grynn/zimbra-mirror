<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"  %>
<c:set var="url">http://geocoder.us/service/rest/?address=<c:out value="${param.address}" /></c:set>
<c:import url="${url}"/>