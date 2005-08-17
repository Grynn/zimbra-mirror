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
<jsp:include page="/js/dwt/widgets/Zabel.js"/>
<jsp:include page="/js/dwt/widgets/Button.js"/>
<jsp:include page="/js/dwt/widgets/Window.js"/>
</script>
</c:if>