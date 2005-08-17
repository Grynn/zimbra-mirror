<%@ page language="java" 
         import="java.lang.*, java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>DwtCalTest</title>
    <style type="text/css">
      <!--
        @import url(/zimbra/img/loRes/imgs.css);
        @import url(/zimbra/js/zimbraMail/config/style/lm.css);
      -->
    </style>
	<script language="JavaScript">
    	DwtConfigPath = "/zimbra/js/dwt/config";
    </script>
    	
    <jsp:include page="../../public/Messages.jsp"/>
    <jsp:include page="../../public/Zimbra.jsp"/>
    <jsp:include page="../../public/Dwt.jsp"/>
    <jsp:include page="../../public/ZimbraMail.jsp"/>
    <script type="text/javascript" src="DwtHtmlEditorTest.js"></script>
  </head>
    <body>
    <noscript><p><b>Javascript must be enabled to use this.</b></p></noscript>
    <script language="JavaScript">   	
   		function launch() {
   			DBG = new LsDebug(LsDebug.DBG1, null, false);
	    	DwtHtmlEditorTest.run();
	    }
        LsCore.addOnloadListener(launch);
    </script>
    </body>
</html>

