/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2007, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
 * Constructor.
 * 
 */
function Com_Zimbra_WebEx() {
}

Com_Zimbra_WebEx.gInstance;

Com_Zimbra_WebEx.prototype = new ZmZimletBase();
Com_Zimbra_WebEx.prototype.constructor = Com_Zimbra_WebEx;

Com_Zimbra_WebEx.USER_PROP_WEBEX_USER = "WebExUser";
Com_Zimbra_WebEx.USER_PROP_WEBEX_PASSWORD = "WebExPass";
Com_Zimbra_WebEx.USER_PROP_WEBEX_ID = "WebExId";
Com_Zimbra_WebEx.USER_PROP_WEBEX_MAX_MEETINGS = "WebExMax";

/**
 * Initializes the zimlet.
 * 
 */
Com_Zimbra_WebEx.prototype.init = function() {
	Com_Zimbra_WebEx.gInstance = this;
};

/**
 * Schedule a new meeting.
 */
Com_Zimbra_WebEx.prototype.scheduleMeeting = function() {
	this.doScheduleMtgDlg( this.newMtgObject() );
}

Com_Zimbra_WebEx.prototype.doScheduleMtgDlg = function( objMtg ) {
    var dlg = new Com_Zimbra_WebEx_CreateMtgDlg();
	dlg.displayDialog( this, objMtg );
}

/**
 * List up-coming meetings.
 * 
 */
Com_Zimbra_WebEx.prototype.listMeeting = function() {
    this.setBusyIcon();
    var request = this.newLstSummaryMeetingRequest();
	AjxRpc.invoke(request, this.postUri(), {"Content-Type":"text/xml"}, new AjxCallback(this, this.onListMeetingRpcComplete), false, false);
}

/**
 * Shows the message.
 * 
 */
Com_Zimbra_WebEx.ShowMessage = function( msg, style ) {
	var dlg = appCtxt.getMsgDialog();
	dlg.setMessage( msg, style );
	dlg.popup();
}

/**
 * Display the meetings in the meeting dialog box.
 */
Com_Zimbra_WebEx.prototype.onListMeetingRpcComplete = function(result) {
    this.resetIcon();
    var objResult = this.xmlToObject(result);
	if( !objResult ) {
		return;
	}
	
	if( !objResult.header || !objResult.header.response || !objResult.header.response.result || objResult.header.response.result != "SUCCESS" ) {
		var msg = "Unable to display meeting list.";
		if( objResult && objResult.header && objResult.header.response && objResult.header.response.reason ) {
			msg += "\n" + objResult.header.response.reason;
		}
		Com_Zimbra_WebEx.ShowMessage(msg, DwtMessageDialog.WARNING_STYLE);
		return;
	}
	
	//// if we didn't get a valid response object, yeah, later.
	var view = new DwtComposite(this.getShell());
	var dialog_args = {
			view  : view,
			parent	: this.getShell(),
			title : "WebEx Meetings"
		};
	
	var dlg = new ZmDialog(dialog_args);
	
	dlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, function() { dlg.popdown(); dlg.dispose();}));
	dlg.setButtonListener(DwtDialog.CANCEL_BUTTON, new AjxListener(this, function() {dlg.popdown(); dlg.dispose(); }));
    var el = view.getHtmlElement();
    var div = document.createElement("div");
    el.appendChild(div);
    
    var html = 
	"<table width=\"539\" cellSpacing=\"0\" cellPadding=\"0\"><tr><td class=\"wx_banner\"></td></tr>" +
		"<tr><td><table width=\"100%\" cellspacing=\"1\" cellpadding=\"2\" align=\"center\">" +
		"<tr class=\"wx_mtglist_spacer\"><td colspan=4></td></tr>" +
		"<tr class=\"wx_mtglist_title\"><td></td><td>Host</td><td>Description</td><td>Start Time</td></tr>";
						
	if( objResult != null && objResult.body != null && objResult.body.bodyContent != null )
	{
		var mtg = objResult.body.bodyContent.meeting;
		if( mtg[0] == null ) {
			html += Com_Zimbra_WebEx.meetingToRow( mtg );
		} else {
			for( var i = 0; i < mtg.length; i++ ) {
				html += Com_Zimbra_WebEx.meetingToRow( mtg[i] );
			}
		}
	}
	
	html += "<tr class=\"wx_mtglist_spacer_bottom\"><td colspan=4></td></tr></table></td></tr></table>";
	div.innerHTML = html;
	dlg.popup();
}

/**
 * Convert the webex meeting summary item to a row in the meeting summary table.
 * 
 */
Com_Zimbra_WebEx.meetingToRow = function( mtg ) {

	var style = "wx_mtglist";
	if( mtg.status == "INPROGRESS" ) {
		style = "wx_mtglist_inprogress";
	}
	
	var launchScript = "javascript:Com_Zimbra_WebEx.Launch(\'" + mtg.meetingKey + "\', \'" + mtg.hostWebExID + "\');";
	
	return row = "<tr class=\"" + style + "\">" +
		"<td class=\"wx_start_mtg\" onclick=\"" + launchScript + "\"></td>" + 
		"<td>" + mtg.hostWebExID + "</td>" +
		"<td>" + mtg.confName + "</td>" +
		"<td>" + mtg.startDate + " " + Com_Zimbra_WebEx.TZMap[mtg.timeZoneID] + "</td></tr>";
}


/**
 * Launches the 'host/join a meeting' process.  makes a request to get the login url.
 * 
 */
Com_Zimbra_WebEx.Launch = function( meetingKey, hostWebExID ) {
	
	var gInstance = Com_Zimbra_WebEx.gInstance;
	
	//build the correct request, join or host?
	var request = gInstance.newGetHostUrlMeetingRequest( meetingKey );
	var userId = gInstance.getUserProperty(Com_Zimbra_WebEx.USER_PROP_WEBEX_USER);
	if( hostWebExID != userId ) {
		var attendeeName = appCtxt.get(ZmSetting.DISPLAY_NAME);
		if( !attendeeName ) { 
			attendeeName = "" 
		}
		request = Com_Zimbra_WebEx.gInstance.newGetJoinUrlMeetingRequest( meetingKey, attendeeName );
	}
	
	//make the request
	AjxRpc.invoke(
		request, 
		Com_Zimbra_WebEx.gInstance.postUri(), 
		{"Content-Type":"text/xml"}, 
		new AjxCallback(Com_Zimbra_WebEx.OnLaunchComplete), 
		false, 
		false);
}

/**
 * Gets the response of a get[join|host]UrlMeeting request and launches the url in a new browser.
 *
 */
Com_Zimbra_WebEx.OnLaunchComplete = function(result) {
	var objResult = Com_Zimbra_WebEx.gInstance.xmlToObject(result);
	if( objResult != null && objResult.body != null && objResult.body.bodyContent != null ) {
		var url = objResult.body.bodyContent.hostMeetingURL;
		if( url == null ) {
			url = objResult.body.bodyContent.joinMeetingURL;
		}
		//launch the url in a new window
		window.open(url);
	}
}

/**
 * If zimlet not configured diplay the configuration UI.
 * 
 */
Com_Zimbra_WebEx.prototype.ensureConfigured = function() {
    var wuser = this.getUserProperty(Com_Zimbra_WebEx.USER_PROP_WEBEX_USER);
    var wpass = this.getUserProperty(Com_Zimbra_WebEx.USER_PROP_WEBEX_PASSWORD);
    var wid   = this.getUserProperty(Com_Zimbra_WebEx.USER_PROP_WEBEX_ID);
    var wmax  = this.getUserProperty(Com_Zimbra_WebEx.USER_PROP_WEBEX_MAX_MEETINGS);
    
    if( !wuser || !wpass || !wid || !wmax) {
		this.displayStatusMessage("WebEx configuration required.");
		this.createPropertyEditor();
	}
}

/**
 * The uri to post all webex xml requests to.
 * 
 */
Com_Zimbra_WebEx.prototype.postUri = function() {
	return ZmZimletBase.PROXY + 
		AjxStringUtil.urlComponentEncode("https://" + this.getUserProperty(Com_Zimbra_WebEx.USER_PROP_WEBEX_ID) + ".webex.com/WBXService/XMLService");
}

/**
 * Called by the Zimbra framework when a menu item is selected
 * dispatch the call, ensuring the webex configuration is set.
 * 
 */
Com_Zimbra_WebEx.prototype.menuItemSelected = function(itemId) {

	switch (itemId) {
		case "SCHEDULE_MEETING":
			this.ensureConfigured();
			this.scheduleMeeting();
			break;
		case "LIST_MEETINGS":
			this.ensureConfigured();
			this.listMeeting();
			break;
		case "PREFERENCES":
			this.createPropertyEditor();
			break;
	}
};

/**
 * When a contact or list of contacts is dropped on the zimlet,
 * pop up the createMtg dialog with the contacts as attendees.
 * 
 */
Com_Zimbra_WebEx.prototype.contactDropped = function(objContact) {
	
	var objMtg = this.newMtgObject();
	if( objContact[0] == null ) {
		var attendee = this.ajxEmailAddressFromContact( objContact );
		if( attendee ) {
			objMtg.attendees.push( attendee );
		}
	} else {
		for( var i = 0; i < objContact.length; i++ ) {
			var attendee = this.ajxEmailAddressFromContact( objContact[i] );
			if( attendee ) {
				objMtg.attendees.push( attendee );
			}
		}
	}
	
	//display the createMtg dialog with the mtg defaults
	this.doScheduleMtgDlg( objMtg );
}

/**
 * Create an attendee from a zimbra contact object.
 * 
 */
Com_Zimbra_WebEx.prototype.ajxEmailAddressFromContact = function( objContact ) {
	
	var email = objContact.email;
	if( !email ) { email = objContact.email2; }
	if( !email ) { email = objContact.email3; }
	if( !email ) { return null; }
	
	var name = objContact.firstName + " " + objContact.lastName;
	if( !objContact.firstName || !objContact.lastName ) {
		name = "";
	}
	return AjxEmailAddress.parse( name + " " + email );
}

/**
 * When an appt is dropped on the zimlet, pop up the createMtg dialog
 * defaulting the params to the information matching the appt.
 * 
 */
Com_Zimbra_WebEx.prototype.apptDropped = function(objAppt) {
	
	var objMtg = this.newMtgObject();
	if( objAppt[0] != null ) {
		this.displayErrorMessage("Drag & drop of multiple appointments is not supported.");
		return;
	}
	
	objMtg.confName = objAppt.subject;
	objMtg.agenda = objAppt.notes;
	
	//add to attendees
	var objAttendeeEmails = AjxEmailAddress.parseEmailString( objAppt.attendees ).good;
	if( objAttendeeEmails ) { 
		var attendeeEmails = objAttendeeEmails.getArray();
		for( var i = 0; i < attendeeEmails.length; i++ ) {
			var attendee = attendeeEmails[i];
			if( attendee ) {
				objMtg.attendees.push( attendee );
			}
		}
	}
	
	objMtg.startDate = objAppt.startDate;
	objMtg.duration = (objAppt.endDate.getTime() - objAppt.startDate.getTime()) / 60000;
	
	//display the createMtg dialog with the mtg defaults
	this.doScheduleMtgDlg( objMtg );
}

/**
 * Instantiate a new webex meeting object.
 * 
 */
Com_Zimbra_WebEx.prototype.newMtgObject = function() {
	var obj = new Object();
	obj.password = "";
	obj.confName = "";
	obj.agenda = "";
	obj.attendees = new Array();
	obj.startDate = new Date();
	obj.duration = 60;
	return obj;
}

/**
 * Called by the Zimbra framework upon an accepted drag'n'drop.
 * 
 */
Com_Zimbra_WebEx.prototype.doDrop = function(obj) {
	switch (obj.TYPE) {
	    case "ZmContact":
			this.contactDropped(obj);
			break;
	    case "ZmAppt":
			this.apptDropped(obj);
			break;
	    default:
			this.displayErrorMessage("Drag & Drop of a \"" + obj.TYPE + "\" is not supported.");
	}
};

/*
 * 
 * Xml related helper functions
 * 
 */

/**
 * Convert the xml response to a javascript object.
 * 
 */
Com_Zimbra_WebEx.prototype.xmlToObject = function(result) {
    var xd = null;
    try {
		var xd1 = null;
		if( result.xml && result.xml.childNodes.length > 0 ) {
			xd1 = new AjxXmlDoc.createFromDom(result.xml);
		} else {
			xd1 = new AjxXmlDoc.createFromXml(result.text);
		}
        xd = xd1.toJSObject(true, false);
    } catch(ex) {
        this.displayErrorMessage("Error executing WebEx XML API Request.", result.text, "WebEx Error");
        return null;
    }

    //for some reason in ff, it doesn't throw an exception
    if( result.text && result.text.length > 5 && result.text.substring( 0, 6 ).toLowerCase() == "<html>" ) {
		xd = null;
		this.displayErrorMessage("Error executing WebEx XML API Request.", result.text, "WebEx Error");
    }

    return xd;
};

/**
 * Returns a string containing the xml for the security context header.
 * 
 */
Com_Zimbra_WebEx.prototype.newSecurityContext = function() {
	
	return securityContext =
		"<securityContext>" + 
			"<webExID>"  + this.getUserProperty(Com_Zimbra_WebEx.USER_PROP_WEBEX_USER) + "</webExID>"  + 
			"<password>" + this.getUserProperty(Com_Zimbra_WebEx.USER_PROP_WEBEX_PASSWORD) + "</password>" + 
			"<siteName>" + this.getUserProperty(Com_Zimbra_WebEx.USER_PROP_WEBEX_ID) + "</siteName>" + 
		"</securityContext>";
}

/**
 * Return an AjxXmlDoc containt the webex request with the given body
 * appends the security context header.
 * 
 */
Com_Zimbra_WebEx.prototype.newWebExRequest = function( requestBody ) {
	
	return requestXmlStr =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
		"<serv:message xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " + 
					  "xmlns:serv=\"http://www.webex.com/schemas/2002/06/service\" " + 
					  "xmlns:schemaLocation=\"http://www.webex.com/schemas/2002/06/service http://www.webex.com/schemas/2002/06/service/service.xsd\">" +
			"<header>" + this.newSecurityContext() + "</header>" + 
			"<body>" + requestBody + "</body>" + 
		"</serv:message>";
}

/**
 * Returns an AjxXmlDoc containing a LstSummaryMeeting request.
 */
Com_Zimbra_WebEx.prototype.newLstSummaryMeetingRequest = function() {

	var max = this.getUserProperty(Com_Zimbra_WebEx.USER_PROP_WEBEX_MAX_MEETINGS);

	var requestBody = 
		"<bodyContent xsi:type=\"java:com.webex.service.binding.meeting.LstsummaryMeeting\" " +
		             "xmlns:meet=\"http://www.webex.com/schemas/2002/06/service/meeting\">" + 
			"<listControl><startFrom>1</startFrom><maximumNum>" + max + "</maximumNum></listControl>" + 
			"<order><orderBy>STARTTIME</orderBy><orderAD>DESC</orderAD></order>" + 
		"</bodyContent>";
	
	return this.newWebExRequest( requestBody );
}

/**
 * Return an AjxXmlDoc containing a GethosturlMeeting request.
 * 
 */
Com_Zimbra_WebEx.prototype.newGetHostUrlMeetingRequest = function( meetingKey ) {

	var requestBody = 
		"<bodyContent xsi:type=\"java:com.webex.service.binding.meeting.GethosturlMeeting\">" + 
			"<meetingKey>" + meetingKey + "</meetingKey>" + 
		"</bodyContent>";
	
	return this.newWebExRequest( requestBody );
}

/**
 * Return an AjxXmlDoc containing a GetjoinurlMeeting request.
 * 
 */
Com_Zimbra_WebEx.prototype.newGetJoinUrlMeetingRequest = function( meetingKey, attendeeName ) {

	var requestBody = 
		"<bodyContent xsi:type=\"java:com.webex.service.binding.meeting.GetjoinurlMeeting\">" + 
			"<meetingKey>" + meetingKey + "</meetingKey>" + 
			"<attendeeName>"+ attendeeName + "</attendeeName>" + 
		"</bodyContent>";
	
	return this.newWebExRequest( requestBody );
}

/**
 * Return an AjxXmlDoc containing a CreateMeeting request.
 * 
 * required: password, confName, agenda, attendees, startDate, duration
 */
Com_Zimbra_WebEx.prototype.newCreateMeetingRequest = function( mp ) {

	var timeZoneID = mp.TimeZone;
	var startDate = new Date();
	startDate.setMonth( mp.Month );
	startDate.setDate( mp.Day );
	startDate.setYear( mp.Year );
	startDate.setHours( parseInt(mp.Hour) + (12 * mp.AMPM) );
	startDate.setMinutes( mp.Minute );
	startDate.setSeconds(0);
	startDate.setMilliseconds(0);

	var formatter = new AjxDateFormat("MM/dd/yyyy HH:mm:ss");
	var startDateStr = formatter.format( startDate );
	
	var requestBody = 
		"<bodyContent xsi:type=\"java:com.webex.service.binding.meeting.CreateMeeting\">" +
			"<accessControl><meetingPassword>" + mp.Password + "</meetingPassword></accessControl>" + 
			"<metaData><confName>" + mp.Subj + "</confName><meetingType>3</meetingType></metaData>" + 
			"<participants>";
	
	var mailboxes = AjxEmailAddress.parseEmailString( mp.Attendees ).good.getArray();
	
	requestBody += "<attendees>";
	for( var i = 0; i < mailboxes.length; i++ ) {
		var a = mailboxes[i];
		requestBody += "<attendee><person><email>" + a.getAddress() + "</email>";
		if( a.getName() && a.getName().length > 0 ) {
			requestBody += "<name>" + a.getName() + "</name>";
		}
		requestBody += "</person></attendee>";
	}
	requestBody += "</attendees></participants>";
	requestBody += 	"<schedule>" +
		"<startDate>" + startDateStr + "</startDate>" + 
		"<duration>60</duration>" + 
		"<timeZoneID>" + timeZoneID + "</timeZoneID></schedule></bodyContent>";
		
	return this.newWebExRequest( requestBody );

}

/**
 * Map the webex timezone identifiers to friendly strings.
 * 
 */
Com_Zimbra_WebEx.TZMap = {
	"0":"Dateline (Eniwetok)",
	"1":"Samoa (Samoa)",
	"2":"Hawaii (Honolulu)",
	"3":"Alaska (Anchorage)",
	"4":"Pacific (San Jose)",
	"5":"Mountain (Arizona)",
	"6":"Mountain (Denver)",
	"7":"Central (Chicago)",
	"8":"Mexico (Mexico City, Tegucigalpa)",
	"9":"Central (Regina)",
	"10":"America Pacific (Bogota)",
	"11":"Eastern (New York)",
	"12":"Eastern (Indiana)",
	"13":"Atlantic (Halifax)",
	"14":"S. America Western (Caracas)",
	"15":"Newfoundland (Newfoundland)",
	"16":"S. America Eastern (Brasilia)",
	"17":"S. America Eastern (Buenos Aires)",
	"18":"Mid-Atlantic (Mid-Atlantic)",
	"19":"Azores (Azores)",
	"20":"Greenwich (Casablanca)",
	"21":"GMT (London)",
	"22":"Europe (Amsterdam)",
	"23":"Europe (Paris)",
	"24":"Europe (Prague)",
	"25":"Europe (Berlin)",
	"26":"Greece (Athens)",
	"27":"Eastern Europe (Bucharest)",
	"28":"Egypt (Cairo)",
	"29":"South Africa (Pretoria)",
	"30":"Northern Europe (Helsinki)",
	"31":"Israel (Tel Aviv)",
	"32":"Saudi Arabia (Baghdad)",
	"33":"Russian (Moscow)",
	"34":"Nairobi (Nairobi)",
	"35":"Iran (Tehran)",
	"36":"Arabian (Abu Dhabi, Muscat)",
	"37":"Baku (Baku)",
	"38":"Afghanistan (Kabul)",
	"39":"West Asia (Ekaterinburg)",
	"40":"West Asia (Islamabad)",
	"41":"India (Bombay)",
	"42":"Columbo (Columbo)",
	"43":"Central Asia (Almaty)",
	"44":"Bangkok (Bangkok)",
	"45":"China (Beijing)",
	"46":"Australia Western (Perth)",
	"47":"Singapore (Singapore)",
	"48":"Taipei (Hong Kong)",
	"49":"Tokyo (Tokyo)",
	"50":"Korea (Seoul)",
	"51":"Yakutsk (Yakutsk)",
	"52":"Australia Central (Adelaide)",
	"53":"Australia Central (Darwin)",
	"54":"Australia Eastern (Brisbane)",
	"55":"Australia Eastern (Sydney)",
	"56":"West Pacific (Guam)",
	"57":"Tasmania (Hobart)",
	"58":"Vladivostok (Vladivostok)",
	"59":"Central Pacific (Solomon Is)",
	"60":"New Zealand (Wellington)",
	"61":"Fiji (Fiji)" 
};
