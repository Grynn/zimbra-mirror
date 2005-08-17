<%@ page language="java" 
         import="java.lang.*, java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<fmt:setBundle basename="config" var="configBundle" scope="session"/>

<script type="text/javascript" src="<fmt:message key="DwtMsg" bundle="${configBundle}"/>"></script>
<script type="text/javascript" src="<fmt:message key="LsMsg" bundle="${configBundle}"/>"></script>

