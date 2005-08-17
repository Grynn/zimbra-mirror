<%@ page language="java" 
         import="java.lang.*, java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>DwtHtmlEditor Example</title>
    <style type="text/css">
      <!--
        @import url(../common/img/hiRes/dwtimgs.css);
        @import url(img/hiRes/imgs.css);
        @import url(DwtHtmlEditorExample.css);
      -->
    </style>
	<script language="JavaScript">
    	DwtConfigPath = "/ajax/js/dwt/config";
    </script>
    	
    <jsp:include page="../Messages.jsp"/>
    <jsp:include page="../Ajax.jsp"/>
    <script type="text/javascript" src="ExMsg.js"></script>
    <script type="text/javascript" src="ExImg.js"></script>
    <script type="text/javascript" src="DwtHtmlEditorExample.js"></script>
  </head>
    <body>
    <noscript><p><b>Javascript must be enabled to use this.</b></p></noscript>
    <script language="JavaScript">   	
   		function launch() {
   			DBG = new LsDebug(LsDebug.DBG1, null, false);
			LsImg.setMode(LsImg.SINGLE_IMG);
	    	DwtHtmlEditorExample.run();
	    }
        LsCore.addOnloadListener(launch);
    </script>
    </body>
</html>

