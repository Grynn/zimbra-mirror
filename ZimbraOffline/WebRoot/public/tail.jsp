<%@ page import="java.io.BufferedReader"%>
<%@ page import="java.io.FileReader"%>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <script type="text/javascript">

        function stopit() {
            //TODO
        }
    </script>

</head>
<body onunload="stopit()" onstop="stopit()">

<pre>
<%
    String log = request.getParameter("log");
    if (log == null) {
        log = "mailbox.log";
    }

    BufferedReader r = new BufferedReader(new FileReader(log));
    int idleCycles = 0;    
    while (idleCycles < 60) {
        String line = r.readLine();
        if (line != null) {
            out.println(line);
            idleCycles = 0;
        } else {
            out.flush();
            ++idleCycles;
            Thread.sleep(5000);
        }
    }
%>
</pre>

</body>
</html>