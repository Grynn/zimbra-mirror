<%@ page language="java" 
         import="java.lang.*, java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>DwtCalTest</title>
    <style type="text/css">
      <!--
        @import url(/liquid/js/liquidMail/config/style/lm.css);
      -->
    </style>
	<script language="JavaScript">
    	DwtConfigPath = "/liquid/js/dwt/config";
    </script>
    	
    <jsp:include page="../../public/Messages.jsp"/>
    <jsp:include page="../../public/Liquid.jsp"/>
    <jsp:include page="../../public/Dwt.jsp"/>
    <script type="text/javascript" src="DwtCalTest.js"></script>
    <script language="JavaScript">   	
   		function launch() {
   			DBG = new LsDebug(LsDebug.DBG1, null, false);
	    	DwtCalTest.run();
	    }
        LsCore.addOnloadListener(launch);
    </script>
  </head>
    <body>
    </body>
</html>

