<%@ page language="java" 
         import="java.lang.*, java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>DwtCalTest</title>
    <style type="text/css">
      <!--
        @import url(/liquid/img/loRes/imgs.css);
        @import url(/liquid/js/liquidMail/config/style/lm.css);
      -->
    </style>
	<script language="JavaScript">
    	DwtConfigPath = "/liquid/js/dwt/config";
    </script>
    	
    <jsp:include page="../../public/Messages.jsp"/>
    <jsp:include page="../../public/Liquid.jsp"/>
    <jsp:include page="../../public/Dwt.jsp"/>
    <jsp:include page="../../public/LiquidMail.jsp"/>
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

