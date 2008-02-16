<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<style type="text/css">
	TR { vertical-align: top; }
	TH { text-align: left; }
	TD { text-align: right; }
</style>

<c:set var="number" value="${1234.5678}" />
<c:set var="pattern" value="plus #,##0.0;minus #,##0.0" />

<h3>Default</h3>
<li>number = ${number}</li>
<%--<li>FAILS TO COMPILE!
		no value:--%>
	<%--<% try { %>--%>
		<%--<fmt:formatNumber />--%>
	<%--<% } catch (Exception e) { %>--%>
		<%--<i><%=e.getMessage()%></i>--%>
	<%--<% } %>--%>
<%--</li>--%>
<li>value (in attr, as text):
	<% try { %>
		<fmt:formatNumber value="1234.5678" />
	<% } catch (Exception e) { %>
		<i><%=e.getMessage()%></i>
	<% } %>
</li>
<li>value (in attr, as number):
	<% try { %>
		<fmt:formatNumber value="${number}" />
	<% } catch (Exception e) { %>
		<i><%=e.getMessage()%></i>
	<% } %>
</li>
<li>value (in body, as text):
	<% try { %>
		<fmt:formatNumber>1234.5678</fmt:formatNumber>
	<% } catch (Exception e) { %>
		<i><%=e.getMessage()%></i>
	<% } %>
</li>
<li>value (in body, as number):
	<% try { %>
		<fmt:formatNumber>${number}</fmt:formatNumber>
	<% } catch (Exception e) { %>
		<i><%=e.getMessage()%></i>
	<% } %>
</li>
<%--<li>FAILS TO COMPILE!--%>
	<%--value (in <i>both</i>, as number):--%>
	<%--<% try { %>--%>
		<%--<fmt:formatNumber value='${number}'>${8765.4321}</fmt:formatNumber>--%>
	<%--<% } catch (Exception e) { %>--%>
		<%--<i><%=e.getMessage()%></i>--%>
	<%--<% } %>--%>
<%--</li>--%>
<p></p>
<table border="1" cellspacing="0" cellpadding="3">
	<tr><th>Attribute</th><th>Value</th><th>Output</th></tr>
	<tr><td>currencyCode (no type)</td>
		<td><c:set var="code" value="EUR" /> ${code}</td>
		<td><fmt:formatNumber value="${number}" currencyCode="${code}" /></td>
	</tr>
	<tr><td rowspan="2">currencyCode</td>
		<td><c:set var="code" value="EUR" /> ${code}</td>
		<td><fmt:formatNumber value="${number}" currencyCode="${code}" type='currency' /></td>
	</tr>
	<tr><td><c:set var="code" value="Monkey Money" /> ${code}</td>
		<td><% try { %>
				<fmt:formatNumber value="${number}" currencyCode="${code}" type='currency' />
			<% } catch (Exception e) { %>
				<i><%=e.getMessage()%></i>
			<% } %>
		</td>
	</tr>
	<tr><td>currencySymbol (no type)</td>
		<td><c:set var="symbol" value="Monkey Money" /> ${symbol}</td>
		<td><fmt:formatNumber value="${number}" currencySymbol="${symbol}" /></td>
	</tr>
	<tr><td>currencySymbol</td>
		<td><c:set var="symbol" value="Monkey Money" /> ${symbol}</td>
		<td><fmt:formatNumber value="${number}" currencySymbol="${symbol}" type='currency' /></td>
	</tr>
	<tr><td rowspan="2">groupingUsed</td>
		<td><c:set var="used" value="true" /> ${used}</td>
		<td><fmt:formatNumber value="${number}" groupingUsed='${used}' /></td>
	</tr>
	<tr><td><c:set var="used" value="false" /> ${used}</td>
		<td><fmt:formatNumber value="${number}" groupingUsed='${used}' /></td>
	</tr>
	<tr><td rowspan="2">maxIntegerDigits</td>
		<td><c:set var="digits" value="0" /> ${digits}</td>
		<td><fmt:formatNumber value="${number}" maxIntegerDigits='${digits}' /></td>
	</tr>
	<tr><td><c:set var="digits" value="2" /> ${digits}</td>
		<td><fmt:formatNumber value="${number}" maxIntegerDigits='${digits}' /></td>
	</tr>
	<tr><td rowspan="2">minIntegerDigits</td>
		<td><c:set var="digits" value="0" /> ${digits}</td>
		<td><fmt:formatNumber value="${number}" minIntegerDigits='${digits}' /></td>
	</tr>
	<tr><td><c:set var="digits" value="6" /> ${digits}</td>
		<td><fmt:formatNumber value="${number}" minIntegerDigits='${digits}' /></td>
	</tr>
	<tr><td rowspan="2">maxFractionDigits</td>
		<td><c:set var="digits" value="0" /> ${digits}</td>
		<td><fmt:formatNumber value="${number}" maxFractionDigits='${digits}' /></td>
	</tr>
	<tr><td><c:set var="digits" value="2" /> ${digits}</td>
		<td><fmt:formatNumber value="${number}" maxFractionDigits='${digits}' /></td>
	</tr>
	<tr><td rowspan="2">minFractionDigits</td>
		<td><c:set var="digits" value="0" /> ${digits}</td>
		<td><fmt:formatNumber value="${number}" minFractionDigits='${digits}' /></td>
	</tr>
	<tr><td><c:set var="digits" value="6" /> ${digits}</td>
		<td><fmt:formatNumber value="${number}" minFractionDigits='${digits}' /></td>
	</tr>
</table>

<h3>Locale: request</h3>
<fmt:setLocale value="${pageContext.request.locale}" />
<table border="1" cellspacing="0" cellpadding="3">
	<tr><th rowspan="2">Value</th><th colspan=3>Type</th><th>Pattern</th></tr>
	<tr><th>number</th><th>currency</th><th>percentage</th><th>${pattern}</th></tr>
	<tr><td><c:set var="number" value="${-1234}" /> ${number}</td>
		<td><fmt:formatNumber value="${number}" type="number" /></td>
		<td><fmt:formatNumber value="${number}" type="currency" /></td>
		<td><fmt:formatNumber value="${number}" type="percent" /></td>
		<td><fmt:formatNumber value="${number}" pattern="${pattern}" /></td>
	</tr>
	<tr><td><c:set var="number" value="${0}" /> ${number}</td>
		<td><fmt:formatNumber value="${number}" type="number" /></td>
		<td><fmt:formatNumber value="${number}" type="currency" /></td>
		<td><fmt:formatNumber value="${number}" type="percent" /></td>
		<td><fmt:formatNumber value="${number}" pattern="${pattern}" /></td>
	</tr>
	<tr><td><c:set var="number" value="${1234}" /> ${number}</td>
		<td><fmt:formatNumber value="${number}" type="number" /></td>
		<td><fmt:formatNumber value="${number}" type="currency" /></td>
		<td><fmt:formatNumber value="${number}" type="percent" /></td>
		<td><fmt:formatNumber value="${number}" pattern="${pattern}" /></td>
	</tr>
	<tr><td><c:set var="number" value="${1234.56}" /> ${number}</td>
		<td><fmt:formatNumber value="${number}" type="number" /></td>
		<td><fmt:formatNumber value="${number}" type="currency" /></td>
		<td><fmt:formatNumber value="${number}" type="percent" /></td>
		<td><fmt:formatNumber value="${number}" pattern="${pattern}" /></td>
	</tr>
	<tr><td><c:set var="number" value="${1234.5678}" /> ${number}</td>
		<td><fmt:formatNumber value="${number}" type="number" /></td>
		<td><fmt:formatNumber value="${number}" type="currency" /></td>
		<td><fmt:formatNumber value="${number}" type="percent" /></td>
		<td><fmt:formatNumber value="${number}" pattern="${pattern}" /></td>
	</tr>
</table>

<h3>Locale: ja_JP</h3>
<fmt:setLocale value="ja_JP" />
<table border="1" cellspacing="0" cellpadding="3">
	<tr><th rowspan="2">Value</th><th colspan=3>Type</th><th>Pattern</th></tr>
	<tr><th>number</th><th>currency</th><th>percentage</th><th>${pattern}</th></tr>
	<tr><td><c:set var="number" value="${-1234}" /> ${number}</td>
		<td><fmt:formatNumber value="${number}" type="number" /></td>
		<td><fmt:formatNumber value="${number}" type="currency" /></td>
		<td><fmt:formatNumber value="${number}" type="percent" /></td>
		<td><fmt:formatNumber value="${number}" pattern="${pattern}" /></td>
	</tr>
	<tr><td><c:set var="number" value="${0}" /> ${number}</td>
		<td><fmt:formatNumber value="${number}" type="number" /></td>
		<td><fmt:formatNumber value="${number}" type="currency" /></td>
		<td><fmt:formatNumber value="${number}" type="percent" /></td>
		<td><fmt:formatNumber value="${number}" pattern="${pattern}" /></td>
	</tr>
	<tr><td><c:set var="number" value="${1234}" /> ${number}</td>
		<td><fmt:formatNumber value="${number}" type="number" /></td>
		<td><fmt:formatNumber value="${number}" type="currency" /></td>
		<td><fmt:formatNumber value="${number}" type="percent" /></td>
		<td><fmt:formatNumber value="${number}" pattern="${pattern}" /></td>
	</tr>
	<tr><td><c:set var="number" value="${1234.56}" /> ${number}</td>
		<td><fmt:formatNumber value="${number}" type="number" /></td>
		<td><fmt:formatNumber value="${number}" type="currency" /></td>
		<td><fmt:formatNumber value="${number}" type="percent" /></td>
		<td><fmt:formatNumber value="${number}" pattern="${pattern}" /></td>
	</tr>
	<tr><td><c:set var="number" value="${1234.5678}" /> ${number}</td>
		<td><fmt:formatNumber value="${number}" type="number" /></td>
		<td><fmt:formatNumber value="${number}" type="currency" /></td>
		<td><fmt:formatNumber value="${number}" type="percent" /></td>
		<td><fmt:formatNumber value="${number}" pattern="${pattern}" /></td>
	</tr>
</table>