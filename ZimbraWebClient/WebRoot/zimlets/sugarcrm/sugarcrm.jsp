<%@ page language="java"
         import="org.apache.commons.httpclient.*,
                 org.apache.commons.httpclient.methods.*,
                 org.apache.commons.httpclient.auth.*" %>
<%

HttpClient proxy = new HttpClient();
PostMethod post = new PostMethod(request.getParameter("address"));
post.setRequestBody(request.getInputStream());

try {
        proxy.executeMethod(post);
        response.getWriter().print(post.getResponseBodyAsString());
} finally {
        post.releaseConnection();
}

%>
