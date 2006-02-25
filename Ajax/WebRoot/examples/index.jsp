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
<% 
   String contextPath = (String)request.getContextPath(); 
%>
<html>
<head>
</head>
<h2> Sample Apps </h2>
<ul>
<li> <a href="<%= contextPath %>/examples/tree/TreeExample.jsp">Tree Example</a></li>
<li> <a href="<%= contextPath %>/examples/htmlEditor/DwtHtmlEditorExample.jsp">DwtEditor Example</a></li>
<li> <a href="<%= contextPath %>/examples/dataViewer/FlightInfo.jsp">Data Viewer Example (FlightInfo)</a></li>
<li> <a href="<%= contextPath %>/examples/mixing/MixingExample.jsp">Mixing DWT & Plain HTML</a></li>
<li> <a href="<%= contextPath %>/examples/mixing2/MixingExample.jsp">Mixing DWT into an HTML Page</a></li>
<li> <a href="<%= contextPath %>/examples/xforms_test/xforms.jsp">Xforms Example</a></li>
</ul>
</html>