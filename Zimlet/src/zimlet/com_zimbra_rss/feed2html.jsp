<%@page import="java.io.*" %>
<%@page import="javax.xml.transform.*" %>
<%@page import="javax.xml.transform.stream.*" %>
<%!
  static Templates stylesheet;
  static Exception ex;
%><%
  // create stylesheet, if needed
  if (ex == null) {
    synchronized (this) {
      if (stylesheet == null) {
        try {
          String servletPath = request.getServletPath();
          String stylesheetURI = servletPath.substring(0, servletPath.lastIndexOf("/"))+"/feed2html.xsl";
          String filename = this.getServletContext().getRealPath(stylesheetURI);

          TransformerFactory factory = TransformerFactory.newInstance();
          Source source = new StreamSource(new File(filename));
          stylesheet = factory.newTemplates(source);
        }
        catch (Exception e) {
          ex = e;
        }
      }
    }
  }

  // can we do anything at all?
  if (stylesheet == null) {
    out.println(ex);
    return;
  }

  // get parameters
  String url = request.getParameter("url");
  String limit = request.getParameter("limit");

  // return feed
  Source source = new StreamSource(url);
  Result result = new StreamResult(out);
  Transformer transformer = stylesheet.newTransformer();
  if (limit != null && limit.length() > 0) {
    transformer.setParameter("limit", limit);
  }
  transformer.transform(source, result);
%>
