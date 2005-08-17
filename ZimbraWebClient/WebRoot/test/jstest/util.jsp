<%@ page language="java" 
         import="java.lang.*, java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<c:if test="${inline != 'inline'}">
<script type="text/javascript" src="/LiquidConsole/js/util/Vector.js"></script>
</c:if>

<c:if test="${inline == 'inline'}">
<script language="JavaScript>
<jsp:include page="/js/util/Vector.js"/>
</script>
</c:if>