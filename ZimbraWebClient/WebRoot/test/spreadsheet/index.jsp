<!--
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
-->
<!-- 
***** BEGIN LICENSE BLOCK *****
Version: ZAPL 1.1

The contents of this file are subject to the Zimbra AJAX Public
License Version 1.1 ("License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.zimbra.com/license

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
the License for the specific language governing rights and limitations
under the License.

The Original Code is: Zimbra AJAX Toolkit.

The Initial Developer of the Original Code is Zimbra, Inc.
Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
All Rights Reserved.

Contributor(s):

***** END LICENSE BLOCK *****
-->

<%@ page language="java" 
         import="java.lang.*, java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>ZmSpreadSheet test</title>
    <style type="text/css">
      <!--

		@import url(/zimbra/img/loRes/imgs.css?v=060307123104);
		@import url(/zimbra/img/loRes/skins/steel/skin.css?v=060307123104);
	
	        @import url(/zimbra/js/zimbraMail/config/style/common.css?v=060123132725);
	        /* @import url(/zimbra/skins/steel/skin.css?v=060123132725); */

	@import url(/zimbra/js/ajax/config/style/dwt.css?v=060307123104);
        @import url(style.css);
      -->
    </style>
	<script language="JavaScript">
    	DwtConfigPath = "/ajax/js/dwt/config";
    </script>
    	
<% 
   String contextPath = (String)request.getContextPath(); 
   String vers = (String)request.getAttribute("version");
   String ext = (String)request.getAttribute("fileExtension");
   if (vers == null){
      vers = "";
   }
   if (ext == null){
      ext = "";
   }
%>
    <jsp:include page="../../public/Messages.jsp"/>
    <jsp:include page="../../public/Ajax.jsp"/>
    <jsp:include page="../../public/Dwt.jsp"/>

<script type="text/javascript" src="<%= contextPath %>/js/zimbraMail/share/view/spreadsheet/msgs.js<%= ext %>?v=<%= vers %>"></script>
<script type="text/javascript" src="<%= contextPath %>/js/zimbraMail/share/view/spreadsheet/ZmSpreadSheet.js<%= ext %>?v=<%= vers %>"></script>
<script type="text/javascript" src="<%= contextPath %>/js/zimbraMail/share/view/spreadsheet/ZmSpreadSheetModel.js<%= ext %>?v=<%= vers %>"></script>
<script type="text/javascript" src="<%= contextPath %>/js/zimbraMail/share/view/spreadsheet/ZmSpreadSheetFormulae.js<%= ext %>?v=<%= vers %>"></script>
<script type="text/javascript" src="<%= contextPath %>/js/zimbraMail/share/view/spreadsheet/ZmSpreadSheetToolbars.js<%= ext %>?v=<%= vers %>"></script>

    <script type="text/javascript" src="test.js"></script>
  </head>
    <body>
    <noscript><p><b>Javascript must be enabled to use this.</b></p></noscript>
    <script language="JavaScript">   	
   		function launch() {
   			DBG = new AjxDebug(AjxDebug.NONE, null, false);
 	    	Test.run();
	    }
        AjxCore.addOnloadListener(launch);
    </script>
<%
//        <textarea id="testdata" style="display: none">< % @include file="test.json" % ></textarea>
%>
    </body>
</html>

