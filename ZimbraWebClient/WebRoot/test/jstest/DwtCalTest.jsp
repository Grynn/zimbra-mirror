<%@ page language="java" 
         import="java.lang.*, java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>DwtCalTest</title>
    <style type="text/css">
      <!--
        @import url(/zimbra/js/zimbraMail/config/style/lm.css);
      -->
    </style>
	<script language="JavaScript">
    	DwtConfigPath = "/zimbra/js/dwt/config";
    </script>
    	
    <jsp:include page="../../public/Messages.jsp"/>
    <jsp:include page="../../public/Zimbra.jsp"/>
    <jsp:include page="../../public/Dwt.jsp"/>
    <script type="text/javascript" src="DwtCalTest.js"></script>
    <script language="JavaScript">   	
   		function launch() {
   			DBG = new AjxDebug(AjxDebug.DBG1, null, false);
	    	DwtCalTest.run();
	    }
        AjxCore.addOnloadListener(launch);
    </script>
  </head>
    <body>
    </body>
</html>

