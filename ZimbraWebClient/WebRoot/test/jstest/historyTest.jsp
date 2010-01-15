<!--
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
-->
<!--    
   Copyright (c) 2005, Brad Neuberg, bkn3@columbia.edu
   http://codinginparadise.org
   
   Permission is hereby granted, free of charge, to any person obtaining 
   a copy of this software and associated documentation files (the "Software"), 
   to deal in the Software without restriction, including without limitation 
   the rights to use, copy, modify, merge, publish, distribute, sublicense, 
   and/or sell copies of the Software, and to permit persons to whom the 
   Software is furnished to do so, subject to the following conditions:
   
   The above copyright notice and this permission notice shall be 
   included in all copies or substantial portions of the Software.
   
   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
   EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
   OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
   IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
   CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
   OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
   THE USE OR OTHER DEALINGS IN THE SOFTWARE.
-->

<head>
  <jsp:include page="../../public/Messages.jsp"/>
  <jsp:include page="../../public/Boot.jsp"/>
  <jsp:include page="../../public/jsp/Ajax.jsp"/>

      <script language="JavaScript">
         function initialize() {

   			DBG = new AjxDebug(AjxDebug.DBG1, null, false);

            // initialize our DHTML history
            var historyMgr = window.historyMgr = new AjxHistoryMgr();

            // subscribe to DHTML history change
            // events
            historyMgr.addListener(historyChange);
            
            // start adding history
            historyMgr.add("1");
            historyMgr.add("2");
            historyMgr.add("3");
            historyMgr.add("4");
         }
         
         /** Our callback to receive history change events. */
         function historyChange(ev) {
			debugMsg("<b>A history change has occurred:</b> " + "new location is " + ev.data);
         }
         
         function debugMsg(msg) {
            var debugMsg = document.getElementById("debugMsg");
            debugMsg.innerHTML = msg;
         }
      </script>
   </head>
   
   <body onload="initialize();">
      <h1>Test DHTML History</h1>
      <h2>Test Output</h2>
      <div id="debugMsg"></div>
   </body>
</html>
