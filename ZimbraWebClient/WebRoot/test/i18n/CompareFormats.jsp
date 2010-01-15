<!--
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
-->
<%@ page import="java.text.*,java.util.*" contentType="text/html;charset=UTF-8" %>
<%!
	static class DateLengthPair {
		public String key;
		public int value;
		public DateLengthPair(String key, int value) {
			this.key = key;
			this.value = value;
		}
	}
	static class DatePatternLetter {
		public char letter;
		public String description;
		public int length;
		public DatePatternLetter(char letter, String description, int length) {
			this.letter = letter;
			this.description = description;
			this.length = length;
		}
	}
%>
<%!
	static String escape(String s) {
		StringBuilder str = new StringBuilder(s.length());
		int length = s.length();
		for (int i = 0; i < length; i++) {
			char c = s.charAt(i);
			switch (c) {
				case '\'': str.append("\\'"); break;
				case '\t': str.append("\\t"); break;
				case '\r': str.append("\\r"); break;
				case '\n': str.append("\\n"); break;
				default: str.append(c);
			}
		}
		return str.toString();
	}
	static Locale getLocale(String locid) {
		if (locid == null || locid.length() == 0) return Locale.US;
		String[] parts = locid.split("_");
		if (parts.length == 1) return new Locale(parts[0]);
		if (parts.length == 2) return new Locale(parts[0], parts[1]);
		return new Locale(parts[0], parts[1], parts[2]);
	}
	static String getQueryString(Locale locale) {
		String[] parts = locale.toString().split("_");
		StringBuilder str = new StringBuilder("language="+parts[0]);
		if (parts.length > 1) str.append("&country="+parts[1]);
		if (parts.length > 2) str.append("&variant="+parts[2]);
		return str.toString();
	}
%>
<%  Locale locale = getLocale(request.getParameter("locid"));
	Date now = new Date();

	Locale[] localeArray = Locale.getAvailableLocales();
	List<Locale> locales = new LinkedList<Locale>();
	for (Locale l : localeArray) {
		locales.add(l);
	}

	DateLengthPair[] dateLengths = {
		new DateLengthPair("SHORT", DateFormat.SHORT),
		new DateLengthPair("MEDIUM", DateFormat.MEDIUM),
		new DateLengthPair("LONG", DateFormat.LONG),
		new DateLengthPair("FULL", DateFormat.FULL),
	};

	DatePatternLetter[] dateLetters = {
		new DatePatternLetter('G', "Era designator", 1),
		new DatePatternLetter('y', "Year", 4),
		new DatePatternLetter('M', "Month in year", 4),
		new DatePatternLetter('w', "Week in year", 2),
		new DatePatternLetter('W', "Week in month", 1),
		new DatePatternLetter('D', "Day in year", 3),
		new DatePatternLetter('d', "Day in month", 2),
		new DatePatternLetter('F', "Day of week in month", 1),
		new DatePatternLetter('E', "Day in week", 4),
		new DatePatternLetter('a', "Am/pm marker", 1),
		new DatePatternLetter('H', "Hour in day (0-23)", 2),
		new DatePatternLetter('k', "Hour in day (1-24)", 2),
		new DatePatternLetter('K', "Hour in am/pm (0-11)", 2),
		new DatePatternLetter('h', "Hour in am/pm (1-12)", 2),
		new DatePatternLetter('m', "Minute in hour", 2),
		new DatePatternLetter('s', "Second in minute", 2),
		new DatePatternLetter('S', "Millisecond", 3),
		new DatePatternLetter('z', "General time zone", 4),
		new DatePatternLetter('Z', "RFC 822 time zone", 4)
	};
%>
<html>
<head>
	<title>Compare Formats</title>
	<style>
		TH { background: silver; }
	</style>
	<script src="/zimbra/res/I18nMsg,AjxMsg.js?debug=1&<%=getQueryString(locale)%>"></script>
	<script src="/zimbra/js/ajax/util/AjxText.js"></script>
	<script src="/zimbra/js/ajax/util/AjxTimezoneData.js"></script>
	<script src="/zimbra/js/ajax/util/AjxTimezone.js"></script>
	<script>
		var LOCALE = "<%=locale%>";
		var NOW = new Date(<%=now.getTime()%>);
	</script>
	<script>
		function escape(s) {
			return s.replace(/&/g,"&amp;").replace(/</g,"&lt;");
		}
	</script>
</head>
<body>

<h1>Compare Formats</h1>
<table border=0>
	<tr><td><li>locale</li></td><td>=</td>
		<td><form action="">
			<select name='locid' onchange="this.form.submit()">
			<% for (Locale l : locales) { %>
				<option value='<%=l%>' <%=l.equals(locale)?"selected":""%>><%=l.getDisplayName(l)%></option>
			<% } %>
			</select>
			</form>
		</td>
	</tr>
</table>

<h2>Date and Time</h2>
<li>now = <%=now%> (<%=now.getTime()%> ms since epoch)

<h3>Standard Patterns</h3>
<%  String[] dateCategories = { "Date", "Time", "Date/Time" };

	for (int i = 0; i < dateCategories.length; i++) { %>
	<p>
	<table border=1 cellpadding=2>
		<tr><th rowspan=2><%=dateCategories[i]%></th>
			<th colspan=2>Pattern</th><th colspan=2>Value</th>
		</tr>
		<tr><th>java.text.DateFormat</th><th>AjxDateFormat</th><th>java.text.DateFormat</th><th>AjxDateFormat</th></tr>
	<% for (DateLengthPair pair : dateLengths) {
		DateFormat format = i == 0 ? DateFormat.getDateInstance(pair.value, locale)
						: (i == 1 ? DateFormat.getTimeInstance(pair.value, locale) :
							DateFormat.getDateTimeInstance(pair.value, pair.value, locale));
		%>
		<script>
			var length = AjxDateFormat['<%=pair.key%>'];
			var format = <%=i%> == 0 ? AjxDateFormat.getDateInstance(length)
			           : (<%=i%> == 1 ? AjxDateFormat.getTimeInstance(length) :
			             AjxDateFormat.getDateTimeInstance(length, length));
			var expectedPattern = '<%=escape(((SimpleDateFormat)format).toPattern())%>';
			var actualPattern = format.toPattern();
			var colorPattern = expectedPattern == actualPattern ? "transparent" : "yellow";
			var expected = '<%=escape(format.format(now))%>';
			var actual = format.format(NOW);
			var color = expected == actual ? "lightgreen" : (expected.toLowerCase() == actual.toLowerCase() ? "yellow" : "red");
			document.write(
				"<tr><th><%=pair.key%></th>",
					"<td>",escape(expectedPattern),"</td>",
					"<td style='background:",colorPattern,"'>",escape(actualPattern),"</td>",
					"<td>",escape(expected),"</td>",
					"<td style='background:",color,"'>",escape(actual),"</td></tr>"
			);
		</script>
	<% } %>
	</table>
	</p>
<% } %>

<h3>Custom Patterns</h3>
<table border=1 cellpadding=2>
	<tr><th colspan=2>Pattern</th><th colspan=2>Value</th></tr>
	<tr><th>Description</th><th>Letter</th><th>java.text.SimpleDateFormat</th><th>AjxDateFormat</th></tr>
	<% for (DatePatternLetter dateLetter : dateLetters) {
		for (int i = 0; i < dateLetter.length; i++) {
			char[] letters = new char[i+1];
			for (int j = 0; j < i + 1; j++) {
				letters[j] = dateLetter.letter;
			}
			String pattern = new String(letters, 0, letters.length);
			SimpleDateFormat format = new SimpleDateFormat(pattern, locale); %>
			<script>
				var pattern = '';
				for (var i = 0; i < <%=i+1%>; i++) {
					pattern += '<%=dateLetter.letter%>';
				}
				var expected = '<%=escape(format.format(now))%>';
				var actual = AjxDateFormat.format(pattern, NOW);
				var color = expected == actual ? "lightgreen" : (expected.toLowerCase() == actual.toLowerCase() ? "yellow" : "red");
				document.write(
					"<tr>",
					pattern.length == 1 ? "<th rowspan=<%=dateLetter.length%> align=left>"+escape('<%=dateLetter.description%>')+"</th>" : "",
					"<th align=left>",pattern,"</th>",
					"<td>",escape(expected),"</td>",
					"<td style='background:",color,"'>",escape(actual),"</td>",
					"</tr>"
				);
			</script>
		<% }
		out.println("</th>");
	} %>
</table>

<h2>Numbers</h2>
<% Number number = new Double(123.456789); %>
<li>number = <%=number%></li>
<p>
<table border=1 cellpadding=2>
	<tr><th rowspan=2>Type</th><th colspan=2>Pattern</th><th colspan=2>Value</th></tr>
	<tr><th>java.util.NumberFormat</th><th>AjxNumberFormat</th><th>java.util.NumberFormat</th><th>AjxNumberFormat</th></tr>
	<script>
		var formatNames = [ "", "Number", "Integer", "Percent" ];
		var formats = new Array(formatNames.length);
		for (var i = 0; i < formatNames.length; i++) {
			formats[i] = AjxNumberFormat["get"+formatNames[i]+"Instance"]();
		}
		var number = 123.456;
	</script>
	<%  NumberFormat[] formats = {
			NumberFormat.getInstance(locale), NumberFormat.getNumberInstance(locale),
			NumberFormat.getIntegerInstance(locale), NumberFormat.getPercentInstance(locale)
		};
		for (int i = 0; i < formats.length; i++) { %>
			<script>
				var expectedPattern = '<%=escape(((DecimalFormat)formats[i]).toPattern())%>';
				var actualPattern = formats[<%=i%>].toPattern();
				var expected = '<%=escape(formats[i].format(number))%>';
				var colorPattern = expectedPattern == actualPattern ? "transparent" : "yellow";
				var actual = formats[<%=i%>].format(number);
				var color = expected == actual ? "lightgreen" : "red";
				document.write(
					"<tr><th>",formatNames[<%=i%>]||"<i>Default</i>",
					"<td>",escape(expectedPattern),"</td>",
					"<td style='background:",colorPattern,"'>",escape(actualPattern),"</td>",
					"<td>",escape(expected),"</td>",
					"<td style='background:",color,"'>",escape(actual),"</td>"
				);
			</script>
		<% }
	%>
</table>
</p>

</body>
</html>