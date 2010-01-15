<!-- 
***** BEGIN LICENSE BLOCK *****
Zimbra Collaboration Suite Web Client
Copyright (C) 2004, 2005, 2006, 2007, 2010 Zimbra, Inc.

The contents of this file are subject to the Zimbra Public License
Version 1.3 ("License"); you may not use this file except in
compliance with the License.  You may obtain a copy of the License at
http://www.zimbra.com/license.

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
***** END LICENSE BLOCK *****
-->

<%@ page language="java" 
         import="java.lang.*, java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<c:if test="${inline != 'inline'}">
<script type="text/javascript" src="/ZimbraConsole/js/dwt/core/DWT.js"></script>
<script type="text/javascript" src="/ZimrbaConsole/js/dwt/events/EventTypes.js"></script>
<script type="text/javascript" src="/ZimbraConsole/js/dwt/events/DWTEvent.js"></script>
<script type="text/javascript" src="/ZimbraConsole/js/dwt/events/KeyEvent.js"></script>
<script type="text/javascript" src="/ZimbraConsole/js/dwt/events/MouseEvent.js"></script>
<script type="text/javascript" src="/ZimbraConsole/js/dwt/widgets/Widget.js"></script>
<script type="text/javascript" src="/ZimbraConsole/js/dwt/widgets/Control.js"></script>
<script type="text/javascript" src="/ZimbraConsole/js/dwt/widgets/Composite.js"></script>
<script type="text/javascript" src="/ZimbraConsole/js/dwt/widgets/Text.js"></script>
<script type="text/javascript" src="/ZimbraConsole/js/dwt/widgets/Label.js"></script>
<script type="text/javascript" src="/ZimbraConsole/js/dwt/widgets/Button.js"></script>
<script type="text/javascript" src="/ZimbraConsole/js/dwt/widgets/Window.js"></script>
</c:if>

<c:if test="${inline == 'inline'}">
<script language="JavaScript>
<jsp:include page="/js/dwt/core/DWT.js"/>
<jsp:include page="/js/dwt/events/EventTypes.js"/>
<jsp:include page="/js/dwt/events/DWTEvent.js"/>
<jsp:include page="/js/dwt/events/KeyEvent.js"/>
<jsp:include page="/js/dwt/events/MouseEvent.js"/>
<jsp:include page="/js/dwt/widgets/Widget.js"/>
<jsp:include page="/js/dwt/widgets/Control.js"/>
<jsp:include page="/js/dwt/widgets/Composite.js"/>
<jsp:include page="/js/dwt/widgets/Text.js"/>
<jsp:include page="/js/dwt/widgets/Label.js"/>
<jsp:include page="/js/dwt/widgets/Button.js"/>
<jsp:include page="/js/dwt/widgets/Window.js"/>
</script>
</c:if>