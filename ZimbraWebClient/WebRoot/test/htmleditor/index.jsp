<!--
***** BEGIN LICENSE BLOCK *****
Version: ZAPL 1.1

The contents of this file are subject to the Zimbra AJAX Public
License Version 1.2 ("License"); you may not use this file except in
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
<%
    String contextPath = request.getContextPath();
    String vers = (String)request.getAttribute("version");
    String ext = (String)request.getAttribute("fileExtension");
//  String mode = (String) request.getAttribute("mode");
    if (vers == null){
       vers = "";
    }
    if (ext == null){
       ext = "";
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Zimbra HTML Editor Test</title>
	<style type="text/css">
	<!--
    @import url(<%=contextPath %>/img/loRes/imgs.css?v=<%=vers%>);
    @import url(<%=contextPath %>/img/loRes/skins/steel/steel.css?v=<%=vers%>);
    @import url(<%=contextPath %>/skins/steel/dwt.css?v=<%=vers%>);
    @import url(<%=contextPath %>/skins/steel/common.css?v=<%=vers%>);
    @import url(<%=contextPath %>/skins/steel/msgview.css?v=<%=vers%>);
    @import url(<%=contextPath %>/skins/steel/zm.css?v=<%=vers%>);
    @import url(<%=contextPath %>/skins/steel/spellcheck.css?v=<%=vers%>);
    @import url(<%=contextPath %>/skins/steel/steel.css?v=<%=vers%>);
    @import url(<%=contextPath %>/ALE/spreadsheet/style.css?v=<%=vers%>);
	-->
	</style>
    <script type="text/javascript">
       var appContextPath = "<%=contextPath%>";
    </script>
    <jsp:include page="../../public/Messages.jsp"/>
    <script type="text/javascript" src="EditorTest.js"></script>
    <jsp:include page="../../public/Boot.jsp"/>
    <jsp:include page="../../public/Ajax.jsp"/>
    <jsp:include page="../../public/jsp/Zimbra.jsp"/>
    <jsp:include page="../../public/jsp/ZimbraCore.jsp"/>
    <jsp:include page="../../public/jsp/Mail.jsp"/>
  </head>
    <body>
    <noscript><p><b>Javascript must be enabled to use this.</b></p></noscript>
    <script type="text/javascript" language="JavaScript">
        AjxCore.addOnloadListener(EditorTest.run);
    </script>
    </body>
</html>

