<table border="1" cellspacing=0 cellpadding=3>
	<tr><th>Length</th><th>Pattern</th><th>Example</th><th>Parsed</th></tr>
	<jsp:include page="parseDate_row.jsp">
		<jsp:param name="timeZone" value="${param.timeZone}" />
		<jsp:param name="locale" value="${param.locale}" />
		<jsp:param name="type" value="${param.type}" />
		<jsp:param name="length" value="short" />
	</jsp:include>
	<jsp:include page="parseDate_row.jsp">
		<jsp:param name="timeZone" value="${param.timeZone}" />
		<jsp:param name="locale" value="${param.locale}" />
		<jsp:param name="type" value="${param.type}" />
		<jsp:param name="length" value="medium" />
	</jsp:include>
	<jsp:include page="parseDate_row.jsp">
		<jsp:param name="timeZone" value="${param.timeZone}" />
		<jsp:param name="locale" value="${param.locale}" />
		<jsp:param name="type" value="${param.type}" />
		<jsp:param name="length" value="long" />
	</jsp:include>
	<jsp:include page="parseDate_row.jsp">
		<jsp:param name="timeZone" value="${param.timeZone}" />
		<jsp:param name="locale" value="${param.locale}" />
		<jsp:param name="type" value="${param.type}" />
		<jsp:param name="length" value="full" />
	</jsp:include>
</table>