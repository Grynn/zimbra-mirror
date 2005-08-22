<!-- 
***** BEGIN LICENSE BLOCK *****
Version: ZPL 1.1

The contents of this file are subject to the Zimbra Public License Version 1.1 ("License");
You may not use this file except in compliance with the License. You may obtain a copy of
the License at http://www.zimbra.com/license

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY
OF ANY KIND, either express or implied. See the License for the specific language governing
rights and limitations under the License.

The Original Code is: Zimbra Collaboration Suite.

The Initial Developer of the Original Code is Zimbra, Inc.
Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
All Rights Reserved.
Contributor(s): ______________________________________.

***** END LICENSE BLOCK *****
-->

<%@ page language="java" 
         import="java.lang.*, java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>DwtCalTest</title>
    <style type="text/css">
      <!--
        @import url(/zimbra/js/zimbraMail/config/style/zm.css);
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

