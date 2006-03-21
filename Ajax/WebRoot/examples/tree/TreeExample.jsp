<!-- 
/*
 * Copyright (C) 2006, The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
-->
<html>
  <head>
    <title>Tree Example</title>
    <style type="text/css">
      <!--
        @import url(../common/img/loRes/dwtimgs.css);
        @import url(TreeExample.css);
      -->
    </style>
    <jsp:include page="../Messages.jsp"/>
    <jsp:include page="../Ajax.jsp"/>
    <script type="text/javascript" src="ExMsg.js"></script>
    <script type="text/javascript" src="TreeExample.js"></script>
  </head>
    <body>
    <noscript><p><b>Javascript must be enabled to use this.</b></p></noscript>
    <script language="JavaScript">   	
   		function launch() {
   			DBG = new AjxDebug(AjxDebug.NONE, null, false);
 	    	TreeExample.run();
	    }
        AjxCore.addOnloadListener(launch);
    </script>
    </body>
</html>

