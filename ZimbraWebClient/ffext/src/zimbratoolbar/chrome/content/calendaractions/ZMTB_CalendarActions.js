var ZMTB_CalendarActions = function(zmtb)
{
	ZMTB_Actions.call(this, zmtb);
	zmtb.getRequestManager().addUpdateListener(this);
	var This = this;
	this._folderMan.setFilter("calendars", {type:"appointment", exclude:[3]})
	
	//Menu items
	document.getElementById("ZimTB-NewAppointment").addEventListener("click",function(event){
		This.newApptCommand();
	},false);
	document.getElementById("ZimTB-NewCalendar").addEventListener("click",function(event){
		This.openActions("ZimTB-NewCal-Bar");
		document.getElementById("ZimTB-NewCal-Name").focus();
	},false);
	document.getElementById("ZimTB-RemoteCalendar").addEventListener("click",function(event){
		This.openActions("ZimTB-NewRemCal-Bar");
		document.getElementById("ZimTB-NewRemCal-Name").focus();
	},false);
	document.getElementById("ZimTB-SharedCalendar").addEventListener("click",function(event){
		This.openActions("ZimTB-LinkToCal-Bar");
		document.getElementById("ZimTB-LinkToCal-Name").focus();
	},false);
	document.getElementById("ZimTB-ViewCalendars").addEventListener("click",function(event){
		This._rqManager.goToPath("?app=calendar")
	},false);
	
	//Context Menu
	document.getElementById("contentAreaContextMenu").addEventListener("popupshowing", function(e){
		if(gContextMenu.onLink && (gContextMenu.linkURL.indexOf(".ics") >=0))
			document.getElementById("ZMTB-ContextAction-AddCal").hidden = false;
		else
			document.getElementById("ZMTB-ContextAction-AddCal").hidden = true;
	}, false);
	document.getElementById("ZMTB-ContextAction-AddCal").addEventListener("command",function(){
		This.openActions("ZimTB-NewRemCal-Bar");
		document.getElementById("ZimTB-NewRemCal-URL").value=gContextMenu.linkURL;
		document.getElementById("ZimTB-NewRemCal-Name").focus();
	}, false);
	
	//Appts
	document.getElementById("ZimTB-NewAppt-Create").addEventListener("command", function(){
		This.newAppt(document.getElementById("ZimTB-NewAppt-Calendar").selectedItem.value, document.getElementById("ZimTB-NewAppt-Subject").value, document.getElementById("ZimTB-NewAppt-Location").value, document.getElementById("ZimTB-NewAppt-StartDate").dateValue, document.getElementById("ZimTB-NewAppt-StartTime").dateValue, document.getElementById("ZimTB-NewAppt-EndDate").dateValue, document.getElementById("ZimTB-NewAppt-EndTime").dateValue, document.getElementById("ZimTB-NewAppt-Repeat").selectedItem.value, document.getElementById("ZimTB-NewAppt-Alarm").selectedItem.value, document.getElementById("ZimTB-NewAppt-AllDay").checked);
		This.hideActions();
	}, false);
	document.getElementById("ZimTB-NewAppt-Close").addEventListener("command", function(){
			This.hideActions();
	}, false);
	
	//New Calendar
	document.getElementById("ZimTB-NewCal-Create").addEventListener("command", function(){
		if(document.getElementById("ZimTB-NewCal-Name").value=="")
		{
			This._zmtb.notify(This._localstrings.getString("calendaraction_newcal_needname"), null, "failure");
			return;
		}
		This.newFolder(document.getElementById("ZimTB-NewCal-Name").value, "appointment", 1);
		This.hideActions();
		document.getElementById("ZimTB-NewCal-Name").value="";
	}, false);
	document.getElementById("ZimTB-NewCal-Close").addEventListener("command", function(){This.hideActions()}, false);
	
	//Remote Calendar
	document.getElementById("ZimTB-NewRemCal-Create").addEventListener("command", function(){
		if(document.getElementById("ZimTB-NewRemCal-Name").value=="")
		{
			This._zmtb.notify(This._localstrings.getString("calendaraction_newremcal_needname"), null, "failure");
			return;
		}
		else if(document.getElementById("ZimTB-NewRemCal-URL").value=="")
		{
			This._zmtb.notify(This._localstrings.getString("calendaraction_newremcal_needurl"), null, "failure");
			return;
		}
		This.newFolder(document.getElementById("ZimTB-NewRemCal-Name").value, "appointment", 1, document.getElementById("ZimTB-NewRemCal-URL").value);
		This.hideActions();
		document.getElementById("ZimTB-NewRemCal-Name").value="";
		document.getElementById("ZimTB-NewRemCal-URL").value="";
	}, false);
	document.getElementById("ZimTB-NewRemCal-Close").addEventListener("command", function(){This.hideActions()}, false);
	
	//Link to Calendar
	document.getElementById("ZimTB-LinkToCal-Create").addEventListener("command", function(){
		if(document.getElementById("ZimTB-LinkToCal-Name").value=="")
		{
			This._zmtb.notify(This._localstrings.getString("calendaraction_linkedcal_needname"), null, "failure");
			return;
		}
		else if(document.getElementById("ZimTB-LinkToCal-Owner").value=="")
		{
			This._zmtb.notify(This._localstrings.getString("calendaraction_linkedcal_needname"), null, "failure");
			return;
		}
		else if(document.getElementById("ZimTB-LinkToCal-Path").value=="")
		{
			This._zmtb.notify(This._localstrings.getString("calendaraction_linkedcal_needname"), null, "failure");
			return;
		}
		This.newLinked(document.getElementById("ZimTB-LinkToCal-Name").value, "appointment", 1, document.getElementById("ZimTB-LinkToCal-Owner").value,  document.getElementById("ZimTB-LinkToCal-Path").value);
		This.hideActions();
		document.getElementById("ZimTB-LinkToCal-Name").value="";
		document.getElementById("ZimTB-LinkToCal-Owner").value="";
		document.getElementById("ZimTB-LinkToCal-Path").value="";
	}, false);
	document.getElementById("ZimTB-LinkToCal-Close").addEventListener("command", function(){This.hideActions()}, false);
	
	
}

ZMTB_CalendarActions.prototype = new ZMTB_Actions();
ZMTB_CalendarActions.prototype.constructor = ZMTB_CalendarActions;

ZMTB_CalendarActions.prototype.enable = function()
{
	document.getElementById("ZimTB-Calendar-Button").disabled = false;
}

ZMTB_CalendarActions.prototype.disable = function()
{
	document.getElementById("ZimTB-Calendar-Button").disabled = true;
}

ZMTB_CalendarActions.prototype.receiveUpdate = function(responseObj)
{
	if(responseObj.code)
		return;
	if(responseObj.Body.CreateAppointmentResponse)
	{
		this._zmtb.notify(this._localstrings.getString("calendaraction_newappt_success"), null, "success");
		this._rqManager.updateAll();
	}
	if(responseObj.Body.CreateFolderResponse && responseObj.Body.CreateFolderResponse.folder)
	{
		for (var i=0; i < responseObj.Body.CreateFolderResponse.folder.length; i++)
		{
			if(responseObj.Body.CreateFolderResponse.folder[i].view == "appointment")
			{
				if(responseObj.Body.CreateFolderResponse.folder[i].url)
					this._zmtb.notify(this._localstrings.getString("calendaraction_newremcal_success"), null, "success");
				else
					this._zmtb.notify(this._localstrings.getString("calendaraction_newcal_success"), null, "success");
			}
		};
	}
	if(responseObj.Body.CreateMountpointResponse && responseObj.Body.CreateMountpointResponse.link)
	{
		for (var i=0; i < responseObj.Body.CreateMountpointResponse.link.length; i++)
			if(responseObj.Body.CreateMountpointResponse.link[i].view == "appointment")
				this._zmtb.notify(this._localstrings.getString("calendaraction_linkedcal_success"), null, "success");
	}
}

ZMTB_CalendarActions.prototype.reset = function(){}

ZMTB_CalendarActions.prototype.newApptCommand = function()
{
	this.openActions("ZimTB-NewAppt-Bar");
	document.getElementById("ZimTB-NewAppt-Subject").focus();
	this._populateList(document.getElementById("ZimTB-NewAppt-Calendar"), this._folderMan.getFolders("calendars"));
	for (var i=0; i < document.getElementById("ZimTB-NewAppt-Calendar").itemCount; i++)
	{
		if(document.getElementById("ZimTB-NewAppt-Calendar").getItemAtIndex(i).getAttribute("value") == "10") //Default calendar folder ID
			document.getElementById("ZimTB-NewAppt-Calendar").selectedIndex=i;
	}
	var This=this;
	var now = new Date();
	var sm = 1800000-now.getTime()%1800000+now.getTime();
	var start = new Date();
	start.setTime(sm);
	var end = new Date();
	end.setTime(1800000+start.getTime());
	document.getElementById("ZimTB-NewAppt-StartTime").dateValue = start;
	document.getElementById("ZimTB-NewAppt-StartDate").dateValue = start;
	document.getElementById("ZimTB-NewAppt-EndTime").dateValue = end;
	document.getElementById("ZimTB-NewAppt-EndDate").dateValue = end;
	document.getElementById("ZimTB-NewAppt-Subject").value = "";
	document.getElementById("ZimTB-NewAppt-Location").value = "";
}

ZMTB_CalendarActions.prototype.newAppt = function(cal, subj, loc, startdate, starttime, enddate, endtime, repeat, alarm, allday)
{
	var sd = ZMTB_AjxSoapDoc.create("CreateAppointmentRequest", ZMTB_RequestManager.NS_MAIL);
	// var sd = AjxSoapDoc.create("CreateAppointmentRequest", ZMTB_RequestManager.NS_MAIL);
	var st = startdate.getUTCFullYear().toString()+((startdate.getUTCMonth()+1).toString().length==2?(startdate.getUTCMonth()+1):"0"+(startdate.getUTCMonth()+1)).toString()+(startdate.getUTCDate().toString().length==2?startdate.getUTCDate():"0"+startdate.getUTCDate()).toString()+"T"+(starttime.getUTCHours().toString().length==2?starttime.getUTCHours():"0"+starttime.getUTCHours()).toString()+(starttime.getUTCMinutes().toString().length==2?starttime.getUTCMinutes():"0"+starttime.getUTCMinutes()).toString()+(starttime.getUTCSeconds().toString().length==2?starttime.getUTCSeconds():"0"+starttime.getUTCSeconds()).toString()+"Z";
	var et = enddate.getUTCFullYear().toString()+((enddate.getUTCMonth()+1).toString().length==2?(enddate.getUTCMonth()+1):"0"+(enddate.getUTCMonth()+1)).toString()+(enddate.getUTCDate().toString().length==2?enddate.getUTCDate():"0"+enddate.getUTCDate()).toString()+"T"+(endtime.getUTCHours().toString().length==2?endtime.getUTCHours():"0"+endtime.getUTCHours()).toString()+(endtime.getUTCMinutes().toString().length==2?endtime.getUTCMinutes():"0"+endtime.getUTCMinutes()).toString()+(endtime.getUTCSeconds().toString().length==2?endtime.getUTCSeconds():"0"+endtime.getUTCSeconds()).toString()+"Z";
	// <s d="YYYYMMDD['T'HHMMSS[Z]]" [tz="timezone_identifier"]>
	var m =  {"l":cal, 
				"su":subj,
				"inv":{ 
					"comp":{"name":subj, 
							"loc":loc, 
							"s":{"d":st}, 
							"e":{"d":et}
							}
						}
			};
	if(repeat)
		m["inv"]["comp"]["recur"]={"add":{"rule":{"freq":repeat}}};
	if(alarm)
		m["inv"]["comp"]["alarm"]={"action":"DISPLAY", "trigger":{"rel":{"neg":1, "m":alarm}}};
	sd.set("m", m);
	this._rqManager.sendRequest(sd);
}