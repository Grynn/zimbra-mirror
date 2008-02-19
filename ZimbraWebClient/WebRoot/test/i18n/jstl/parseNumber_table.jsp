<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<h3>Locale: "${requestScope.locale}"</h3>
<fmt:setLocale value="${requestScope.locale}" /> 
<table border="1" cellspacing=0 cellpadding=3>
	<tr><th>Type</th><th>Pattern</th><th>Example</th><th>Parsed</th></tr>
	<jsp:include page="parseNumber_row.jsp">
		<jsp:param name="type" value="number" />
	</jsp:include>
	<jsp:include page="parseNumber_row.jsp">
		<jsp:param name="type" value="currency" />
	</jsp:include>
	<jsp:include page="parseNumber_row.jsp">
		<jsp:param name="type" value="percent" />
	</jsp:include>
</table>
