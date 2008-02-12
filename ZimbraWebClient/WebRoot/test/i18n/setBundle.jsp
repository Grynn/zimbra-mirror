<%@ taglib prefix="jfmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="zfmt" uri="com.zimbra.i18n" %>

<% int times = 1000; %>

<h2>Loading ZhMsg Comparison</h2>
<% long before, after; %>

<h3>&lt;fmt:setBundle> (JSTL, <%=times%> times)</h3>
<% before = System.currentTimeMillis(); %>
<% for (int i = 0; i < times; i++) { %>
	<jfmt:setBundle basename="/messages/ZhMsg" />
<% } %>
<% after = System.currentTimeMillis(); %>
<li>Time: <%=after-before%> ms</li>

<h3>&lt;fmt:setBundle> (Zimbra, <%=times%> times)</h3>
<% before = System.currentTimeMillis(); %>
<% for (int i = 0; i < times; i++) { %>
	<zfmt:setBundle basename="/messages/ZhMsg" />
<% } %>
<% after = System.currentTimeMillis(); %>
<li>Time: <%=after-before%> ms</li>

<h2>Loading ZmMsg Comparison</h2>

<h3>&lt;fmt:setBundle> (JSTL, <%=times%> times)</h3>
<% before = System.currentTimeMillis(); %>
<% for (int i = 0; i < times; i++) { %>
	<jfmt:setBundle basename="/messages/ZmMsg" />
<% } %>
<% after = System.currentTimeMillis(); %>
<li>Time: <%=after-before%> ms</li>

<h3>&lt;fmt:setBundle> (Zimbra, <%=times%> times)</h3>
<% before = System.currentTimeMillis(); %>
<% for (int i = 0; i < times; i++) { %>
	<zfmt:setBundle basename="/messages/ZmMsg" />
<% } %>
<% after = System.currentTimeMillis(); %>
<li>Time: <%=after-before%> ms</li>
