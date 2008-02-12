<h3>TimeZone: "${param.timeZone}", Locale: "${param.locale}"</h3>
<h4>Date</h4>
<jsp:include page="parseDate_table.jsp">
	<jsp:param name="timeZone" value="${param.timeZone}" />
	<jsp:param name="locale" value="${param.locale}" />
	<jsp:param name="type" value="date" />
</jsp:include>
<h4>Time</h4>
<jsp:include page="parseDate_table.jsp">
	<jsp:param name="timeZone" value="${param.timeZone}" />
	<jsp:param name="locale" value="${param.locale}" />
	<jsp:param name="type" value="time" />
</jsp:include>
