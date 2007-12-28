<%@page import="com.zimbra.cs.offline.jsp.TestBean"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@taglib prefix="zd" tagdir="/WEB-INF/tags/test" %>

<jsp:useBean id="myBean" class="com.zimbra.cs.offline.jsp.TestBean" scope="request"/>



<c:catch var="error">
<jsp:setProperty name="myBean" property="*"/>
</c:catch>

<%

myBean.validate();

boolean isFooNull = true;

if (myBean.getFoo() != null) {
	isFooNull = false;
}

pageContext.setAttribute("isFooNull", isFooNull);
%>


<html>
<body>

<c:if test="${not empty myBean.error}">
${myBean.error}<p>
</c:if>

<c:out value="${isFooNull}"/><br>

<c:out value="${myBean.myObj.name}"/><br>
<c:out value="${myBean.myObj.number}"/><br>

<zd:test post_url="/zimbra/test/test.jsp" />

<jsp:setProperty name="myBean" property="foo" value="bar"/>

<%

out.println(myBean.getFoo());

%>

</body>
</html>