<!-- 
/*
* ***** BEGIN LICENSE BLOCK *****
* Version: MPL 1.1
*
* The contents of this file are subject to the Mozilla Public
* License Version 1.1 ("License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.zimbra.com/license
*
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
* the License for the specific language governing rights and limitations
* under the License.
*
* The Original Code is: Zimbra AJAX Toolkit.
*
* The Initial Developer of the Original Code is Zimbra, Inc.
* Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
* All Rights Reserved.
*
* Contributor(s):
*
* ***** END LICENSE BLOCK *****
*/

-->
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Content Mixing Example</title>
    <style type="text/css">
      <!--
        @import url(../common/img/hiRes/dwtimgs.css);
        @import url(img/hiRes/imgs.css);
        @import url(MixingExample.css);
      -->
    </style>
   	
    <jsp:include page="../Messages.jsp"/>
    <jsp:include page="../Ajax.jsp"/>
    <script type="text/javascript" src="MixingExample.js"></script>
  </head>
    <body>
	    <noscript><p><b>Javascript must be enabled to use this.</b></p></noscript>
	    <script language="JavaScript"> 
	    	shell = null;  	
	    	function doIt() {
	   			DBG = new AjxDebug(AjxDebug.DBG1, null, false);
				shell = new DwtShell("MainShell", false, null, null, true);
				shell.setVisible(false);
				
				var l = new AjxListener(null, listenerFunc);
				var b = new DwtButton(shell);
				b.addSelectionListener(l);
				b.setText("Button1");
				// New version of DwtControl has reparentHtmlElement method which could be
				// called as follows: b.reparentHtmlElement("R1C1");
				document.getElementById("R1C1").appendChild(b.getHtmlElement());	
		
				b = new DwtButton(shell);
				b.addSelectionListener(l);
				b.setText("Button2");
				document.getElementById("R2C2").appendChild(b.getHtmlElement());
				
		    }
		    
		    function listenerFunc(ev) {
		    	alert("Button Pressed: " + ev.item.getText());
		    }
		    
		    treeCreated = false;
		    function createTree(id) {
		    	if (!treeCreated) {
					var t = new DwtTree(shell);
					var ti = new DwtTreeItem(t);
					ti.setText("Node 1");
					var ti1 = new DwtTreeItem(ti);
					ti1.setText("Node 1A");
					ti1 = new DwtTreeItem(ti);
					ti1.setText("Node 1B");
					ti1 = new DwtTreeItem(ti);
					ti1.setText("Node 1C");
					ti = new DwtTreeItem(t);
					ti.setText("Node 2");				
					ti1 = new DwtTreeItem(ti);
					ti1.setText("Node 2A");
					ti1 = new DwtTreeItem(ti);
					ti1.setText("Node 1B");
					ti1 = new DwtTreeItem(ti);
					ti1.setText("Node 2C");
					ti = new DwtTreeItem(t);
					ti.setText("Node 3");				
					ti = new DwtTreeItem(t);
					ti.setText("Node 4");
									
					var div = document.getElementById(id);
					div.innerHTML = "";
					div.appendChild(t.getHtmlElement());
					
					treeCreated = true;
		    	}
		    }
		    
	        AjxCore.addOnloadListener(doIt);
	    </script>
	    
	    <h1>EXAMPLE OF HTML WITH DWT MIXED IN</h1>
	    <h4>Check out the table below. It has DwtButton objects in it!</h4>
	    <p/>
		<table border=1 width='100%'> 
		    <tr>
		       <td id='R1C1'></td><td id='R1C2'>R1C2</td>
		    </tr>
		    <tr>
		       <td id='R2C1'>R2C1</td><td id='R2C2'></td>
		    </tr>
		</table>
	    <p/>
	    <h4>The table above has DwtButton objects in it!</h4>
	    <h4>Below we have a tree</h4>
	    <hr/>
	    <div style='width:100px;height150px;background-color:red;' id='TABLE_DIV' onmouseover='javascript:createTree("TABLE_DIV");'>MOUSE OVER ME!!!</div>
	    <hr/>
    </body>
</html>

