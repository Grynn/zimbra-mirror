<!-- 
***** BEGIN LICENSE BLOCK *****
Zimbra Collaboration Suite Web Client
Copyright (C) 2005, 2006, 2007 Zimbra, Inc.

The contents of this file are subject to the Zimbra Public License
Version 1.2 ("License"); you may not use this file except in
compliance with the License.  You may obtain a copy of the License at
http://www.zimbra.com/license.

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
***** END LICENSE BLOCK *****
-->

<%@ page language="java" 
         import="java.lang.*, java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Zimbra Mail</title>
    <style type="text/css">
      <!--
        @import url(/zimbra/img/imgs.css);

        @import url(/zimbra/js/dwt/config/style/dwt.css);
        @import url(/zimbra/js/zimbraMail/config/style/zm.css);

      -->
.DwtSelect{
  width:50px;
  height:22px;
  background-color:white; 
  border: 1px solid rgb(127,157, 185); 
  width:105px;
}
.DwtSelect table {
  width:100%;
  table-collapse: collapse;
  padding:0;
}

.DwtSelect td {
  padding:0;
}

.DwtSelect_button{
  width:12px;
  background-color:blue;
}
.DwtSelect_button table{
  table-collapse: collapse;
  padding: 0;
  text-align: auto;
}
.DwtSelect_button td{
  padding: 0;
  text-align: auto;
}

.DwtSelect_display {
  width:60px;
  height:100%;
}

    </style>
    <jsp:include page="../../public/Messages.jsp"/>
    <jsp:include page="../../public/Boot.jsp"/>
    <jsp:include page="../../public/Ajax.jsp"/>
    <jsp:include page="../../public/jsp/Zimbra.jsp"/>
    <jsp:include page="../../public/jsp/ZimbraCore.jsp"/>
    <script language="JavaScript">   	
var _TIME_OF_DAY_CHOICES = [ 
	{ label:'12:00 AM', value:'0:00' }, { label:'12:30 AM', value: '0:30' },
	{ label:'1:00 AM', value: '1:00' }, { label:'1:30 AM', value: '1:30' },
	{ label:'2:00 AM', value: '2:00' },	{ label:'2:30 AM', value: '2:30' },
	{ label:'3:00 AM', value: '3:00' },	{ label:'3:30 AM', value: '3:30' },
	{ label:'4:00 AM', value: '4:00' },	{ label:'4:30 AM', value: '4:30' },
	{ label:'5:00 AM', value: '5:00' },	{ label:'5:30 AM', value: '5:30' },
	{ label:'6:00 AM', value: '6:00' },	{ label:'6:30 AM', value: '6:30' },
	{ label:'7:00 AM', value: '7:00' },	{ label:'7:30 AM', value: '7:30' },
	{ label:'8:00 AM', value: '8:00' },	{ label:'8:30 AM', value: '8:30' },
	{ label:'9:00 AM', value: '9:00' },	{ label:'9:30 AM', value: '9:30' },
	{ label:'10:00 AM', value: '10:00' }, { label:'10:30 AM', value: '10:30' },
	{ label:'11:00 AM', value: '11:00' }, { label:'11:30 AM', value: '11:30' },
	{ label:'12:00 PM', value:'12:00' }, { label:'12:30 PM', value: '12:30' },
	{ label:'1:00 PM', value: '13:00' }, { label:'1:30 PM', value: '13:30' },
	{ label:'2:00 PM', value: '14:00' }, { label:'2:30 PM', value: '14:30' },
	{ label:'3:00 PM', value: '15:00' }, { label:'3:30 PM', value: '15:30' },
	{ label:'4:00 PM', value: '16:00' }, { label:'4:30 PM', value: '16:30' },
	{ label:'5:00 PM', value: '17:00' }, { label:'5:30 PM', value: '17:30' },
	{ label:'6:00 PM', value: '18:00' }, { label:'6:30 PM', value: '18:30' },
	{ label:'7:00 PM', value: '19:00' }, { label:'7:30 PM', value: '19:30' },
	{ label:'8:00 PM', value: '20:00' }, { label:'8:30 PM', value: '20:30' },
	{ label:'9:00 PM', value: '21:00' }, { label:'9:30 PM', value: '21:30' },
	{ label:'10:00 PM', value: '22:00' }, { label:'10:30 PM', value: '22:30' },
	{ label:'11:00 PM', value: '23:00' }, { label:'11:30 PM', value: '23:30' }
 ];

	function launch() {
		DBG = new AjxDebug(AjxDebug.DBG1, null, false);
		var x = new DwtShell();
		//sel = new DwtSelect(x, _TIME_OF_DAY_CHOICES);

		var value = location.protocol + "//" + location.hostname + (location.port == "80" ? "/service/soap/" : ":" + location.port + "/service/soap/");

		if (location.search && location.search.indexOf("host=") != -1)
			value += location.search;
		ZmCsfeCommand.setServerUri(value);

		var soapDoc = AjxSoapDoc.create("GetFreeBusyRequest", "urn:zimbraMail");
		var now = new Date();
		now.setHours(0);
		now.setMinutes(0);
		now.setSeconds(0);
		//now.setDate(10);
		var end = new Date();
		end.setHours(0);
		end.setSeconds(0);
		end.setMinutes(0);
		//end.setDate(now.getDate());
		soapDoc.setMethodAttribute("s", now.getTime());
		// TODO: Not sure what the period should be here
		end.setDate(now.getDate() + 1);
		soapDoc.setMethodAttribute("e", end.getTime());
		soapDoc.setMethodAttribute("uid", "user1@db682461.zimbra.com");
		// This is erroring out
		var resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, false);
		//var resp = ZmZimbraMail.prototype.sendRequest(soapDoc).firstChild;
		var userSchedules = ZmUserSchedule.loadFromDom(resp.Body);
		var dummyAppt = new ZmAppt();
		dummyAppt.startDate = new Date();
		dummyAppt.startDate.setHours(8);
		dummyAppt.startDate.setMinutes(30);
		dummyAppt.startDate.setSeconds(0);
		dummyAppt.endDate = new Date();
		dummyAppt.endDate.setHours(13);
		dummyAppt.endDate.setMinutes(30);
		dummyAppt.endDate.setSeconds(0);

		var freeBusy =  new ZmFreeBusyView(x,  userSchedules, now, end, dummyAppt);
		//for(var i = 0 ; i < _TIME_OF_DAY_CHOICES.length; i++){
		//	sel.addOption(new DwtSelectOptionData(_TIME_OF_DAY_CHOICES[i].value, _TIME_OF_DAY_CHOICES[i].label));
		//}
		//sel.setSelectedValue("0:00");
	}
function clearOptionsAndReset () {
	sel.clearOptions();
	for(var i = 0 ; i < _TIME_OF_DAY_CHOICES.length; i++){
		sel.addOption(new DwtSelectOptionData(_TIME_OF_DAY_CHOICES[i].value, _TIME_OF_DAY_CHOICES[i].label));
	}
	sel.setSelectedValue("0:00");
};

function getSelectedValue(){
	document.getElementById('selVal').innerHTML= sel.getValue();
}

function shutdown() {
	delete DwtComposite._pendingElements;
}
    </script>
  </head>
  <body onload="javascript:void launch()" onunload="javascript:void shutdown()">
	<!--div>
	  <a href="javascript:clearOptionsAndReset();">click to clearOptions and reset</a>
	</div>
	<div>
	  <a href="javascript:getSelectedValue()">Click for the selected value</a>
	  <div id ='selVal'></div>
	</div-->
   </body>
</html>

