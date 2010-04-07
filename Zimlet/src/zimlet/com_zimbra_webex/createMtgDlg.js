/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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
function Com_Zimbra_WebEx_CreateMtgDlg() {
}

Com_Zimbra_WebEx_CreateMtgDlg.gInstance;
Com_Zimbra_WebEx_CreateMtgDlg.objMtg;
Com_Zimbra_WebEx_CreateMtgDlg.prototype.webEx = null;
Com_Zimbra_WebEx_CreateMtgDlg.dlg;

Com_Zimbra_WebEx_CreateMtgDlg.prototype.displayDialog = function( webEx, objMtg ) {
	Com_Zimbra_WebEx_CreateMtgDlg.gInstance = this;
	Com_Zimbra_WebEx_CreateMtgDlg.objMtg = objMtg;
	this.webEx = webEx;
	var view = new DwtComposite(webEx.getShell());
	
	var dialog_args = {
			view  : view,
			parent : webEx.getShell(),
			title : "Create WebEx Meeting"
		};
	var dlg = new ZmDialog(dialog_args);
	
	dlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(webEx, Com_Zimbra_WebEx_CreateMtgDlg.StartCreateMtg));
	dlg.setButtonListener(DwtDialog.CANCEL_BUTTON, new AjxListener(webEx, function() {dlg.popdown(); dlg.dispose(); }));
	Com_Zimbra_WebEx_CreateMtgDlg.dlg = dlg;
    var el = view.getHtmlElement();
    var div = document.createElement("div");
    el.appendChild(div);
    div.innerHTML = Com_Zimbra_WebEx_CreateMtgDlg.getOriginalDlgHtml();
    
	var objFormElements = Com_Zimbra_WebEx_CreateMtgDlg.GetFormElements();
	objFormElements.Subj.value = objMtg.confName
	
	objFormElements.Month.value = objMtg.startDate.getMonth();
	objFormElements.Day.value = objMtg.startDate.getDate();
	objFormElements.Year.value = objMtg.startDate.getYear();
	objFormElements.Hour.value = objMtg.startDate.getHours()%12;
	objFormElements.Minute.value = (Math.ceil(objMtg.startDate.getMinutes()/15) * 15)%60;
	objFormElements.AMPM.value = Math.floor(objMtg.startDate.getHours()/12);
	objFormElements.TimeZone.value = 4;
	
	
	var strAttendees = "";
	for( var i = 0; i < objMtg.attendees.length; i++ ) {
		strAttendees += objMtg.attendees[i].toString() + ";";
	}
	objFormElements.Attendees.value = strAttendees;
    dlg.popup();
}

Com_Zimbra_WebEx_CreateMtgDlg.StartCreateMtg = function() {
	var inst = Com_Zimbra_WebEx_CreateMtgDlg.gInstance;
	var values = Com_Zimbra_WebEx_CreateMtgDlg.GetFormElementValues();
	
	if( !values.Subj || values.Subj.length == 0 ) {
		Com_Zimbra_WebEx_CreateMtgDlg.ShowError( "Please enter a meeting topic." );
		return;
	} else if( !values.Attendees || values.Attendees.length == 0) {
		Com_Zimbra_WebEx_CreateMtgDlg.ShowError( "Please enter meeting attendees." );
		return;
	} else if( !values.Password || values.Password.length == 0) {
		Com_Zimbra_WebEx_CreateMtgDlg.ShowError( "Please enter a meeting password." );
		return;
	} else if( !values.ConfirmPassword || values.ConfirmPassword.length == 0) {
		Com_Zimbra_WebEx_CreateMtgDlg.ShowError( "Please confirm the meeting password." );
		return;
	} else if( values.Password != values.ConfirmPassword ) {
		Com_Zimbra_WebEx_CreateMtgDlg.ShowError( "Passwords do not match." );
		return;
	}
	
	//ensure the attendee list is valid
	var mailboxes = AjxEmailAddress.parseEmailString( values.Attendees );
	var malformedSpecs = mailboxes.bad;
	if( malformedSpecs && malformedSpecs.size() > 0) {
		var badAddrs = "";
		var malformedSpecsArray = malformedSpecs.getArray();
		for( var i = 0; i < malformedSpecsArray.length; i++ ) {
			badAddrs += malformedSpecsArray[i].toString() + " ";
		}
		Com_Zimbra_WebEx_CreateMtgDlg.ShowError( "Invalid attendee address(es):\n" + badAddrs );
		return;
	}
	
	//disable the ui or something here
	var request = inst.webEx.newCreateMeetingRequest( values );
	AjxRpc.invoke(
		request, 
		inst.webEx.postUri(), 
		{"Content-Type":"text/xml"}, 
		new AjxCallback(inst, inst.OnCreateMtgComplete), false, false);
}

Com_Zimbra_WebEx_CreateMtgDlg.prototype.OnCreateMtgComplete = function(result) {

	var objResult = this.webEx.xmlToObject(result);
	if( !objResult ) {
		return;
	}
	
	if( !objResult.header || !objResult.header.response || !objResult.header.response.result || objResult.header.response.result != "SUCCESS" ) {
		var msg = "Unable to create meeting.";
		if( objResult && objResult.header && objResult.header.response && objResult.header.response.reason ) {
			msg += "\n" + objResult.header.response.reason;
		}
		Com_Zimbra_WebEx_CreateMtgDlg.ShowError(msg);
		return;
	}
	
	var key = objResult.body.bodyContent.meetingKey;
	var hostUrl = objResult.body.bodyContent.hostICalURL;
	var attendeeUrl = objResult.body.bodyContent.attendeeICalURL;
	
	Com_Zimbra_WebEx_CreateMtgDlg.ShowInfo("Meeting created succcesfully.");
	Com_Zimbra_WebEx_CreateMtgDlg.dlg.popdown(); 
	Com_Zimbra_WebEx_CreateMtgDlg.dlg.dispose();
	Com_Zimbra_WebEx_CreateMtgDlg.dlg = null;
}

Com_Zimbra_WebEx_CreateMtgDlg.ShowError = function(msg) {
	Com_Zimbra_WebEx_CreateMtgDlg.ShowMessage(msg,DwtMessageDialog.WARNING_STYLE);
}

Com_Zimbra_WebEx_CreateMtgDlg.ShowInfo = function(msg) {
	Com_Zimbra_WebEx_CreateMtgDlg.ShowMessage(msg,DwtMessageDialog.INFO_STYLE);
}

Com_Zimbra_WebEx_CreateMtgDlg.ShowMessage = function( msg, style ) {
	var dlg = appCtxt.getMsgDialog();
	dlg.setMessage( msg, style );
	dlg.popup();
}

Com_Zimbra_WebEx_CreateMtgDlg.GetFormElementValues = function() {
	var elements = Com_Zimbra_WebEx_CreateMtgDlg.GetFormElements();
	var values = new Object();
	values.Subj = elements.Subj.value;
	values.Month = elements.Month.value;
	values.Day = elements.Day.value;
	values.Year = elements.Year.value;
	values.Hour = elements.Hour.value;
	values.Minute = elements.Minute.value;
	values.AMPM = elements.AMPM.value;
	values.TimeZone = elements.TimeZone.value;
	values.Attendees = elements.Attendees.value;
	values.Password = elements.Password.value;
	values.ConfirmPassword = elements.ConfirmPassword.value;
	return values;
}

Com_Zimbra_WebEx_CreateMtgDlg.GetFormElements = function() {
	var objFormElement = new Object();
	objFormElement.Subj = document.getElementById("wxCreateMtg_Subject");
	objFormElement.Month = document.getElementById("wxCreateMtg_Month");
	objFormElement.Day = document.getElementById("wxCreateMtg_Day");
	objFormElement.Year = document.getElementById("wxCreateMtg_Year");
	objFormElement.Hour = document.getElementById("wxCreateMtg_Hour");
	objFormElement.AMPM = document.getElementById("wxCreateMtg_AMPM");
	objFormElement.Minute = document.getElementById("wxCreateMtg_Minute");
	objFormElement.TimeZone = document.getElementById("wxCreateMtg_TimeZone");
	objFormElement.Attendees = document.getElementById("wxCreateMtg_Attendees");
	objFormElement.Password = document.getElementById("wxCreateMtg_Password");
	objFormElement.ConfirmPassword = document.getElementById("wxCreateMtg_ConfirmPassword");
	return objFormElement;
}

/// id's of interesting controls:
///  SUbject: 			wxCreateMtg_Subject
///  Month: 			wxCreateMtg_Month
///  Day: 				wxCreateMtg_Day
///  Year: 				wxCreateMtg_Year
///  Hour: 				wxCreateMtg_Hour
//// AM/PM: 			wxCreateMtg_AMPM
///  Minute: 			wxCreateMtg_Minute
///  TimeZone: 			wxCreateMtg_TimeZone
///  Attendees: 		wxCreateMtg_Attendees
///  Password: 			wxCreateMtg_Password
///  ConfirmPassword: 	wxCreateMtg_ConfirmPassword
///
Com_Zimbra_WebEx_CreateMtgDlg.getOriginalDlgHtml = function() {
	var html =  
	"<table width=\"539\" cellSpacing=\"0\" cellPadding=\"0\">" +
		"<tr>" +
			"<td class=\"wx_banner\"></td>" +
		"</tr>" +
		"<tr>" +
			"<td>" +
				"<TABLE class=\"wx_createmtg\" cellSpacing=\"0\" cellPadding=\"5\" width=\"100%\" border=\"0\">" +
					"<TR>" +
						"<TD class=\"wx_createmtg_label\">Topic</TD>" +
						"<TD colSpan=\"3\"><INPUT class=\"wx_createmtg_ctrl\" type=\"text\" size=\"78\" name=\"wxCreateMtg_Subject\" id=\"wxCreateMtg_Subject\"></TD>" +
					"</TR>" +
					"<TR>" +
						"<TD class=\"wx_createmtg_label\">Date</TD>" +
						"<TD colSpan=\"3\">" +
							"<TABLE class=\"wx_createmtg\" id=\"Table2\" cellSpacing=\"0\" cellPadding=\"0\" border=\"0\">" +
								"<TR>" +
									"<TD><SELECT class=\"wx_createmtg_ctrl\" name=\"wxCreateMtg_Month\" id=\"wxCreateMtg_Month\">" +
											"<OPTION value=\"0\">Jan</OPTION>" +
											"<OPTION value=\"1\">Feb</OPTION>" +
											"<OPTION value=\"2\">Mar</OPTION>" +
											"<OPTION value=\"3\" selected>Apr</OPTION>" +
											"<OPTION value=\"4\">May</OPTION>" +
											"<OPTION value=\"5\">Jun</OPTION>" +
											"<OPTION value=\"6\">Jul</OPTION>" +
											"<OPTION value=\"7\">Aug</OPTION>" +
											"<OPTION value=\"8\">Sep</OPTION>" +
											"<OPTION value=\"9\">Oct</OPTION>" +
											"<OPTION value=\"10\">Nov</OPTION>" +
											"<OPTION value=\"11\">Dec</OPTION>" +
										"</SELECT></TD>" +
									"<TD><SELECT class=\"wx_createmtg_ctrl\" name=\"wxCreateMtg_Day\" id=\"wxCreateMtg_Day\">" +
											"<OPTION value=\"1\">1</OPTION>" +
											"<OPTION value=\"2\">2</OPTION>" +
											"<OPTION value=\"3\">3</OPTION>" +
											"<OPTION value=\"4\">4</OPTION>" +
											"<OPTION value=\"5\">5</OPTION>" +
											"<OPTION value=\"6\">6</OPTION>" +
											"<OPTION value=\"7\">7</OPTION>" +
											"<OPTION value=\"8\" selected>8</OPTION>" +
											"<OPTION value=\"9\">9</OPTION>" +
											"<OPTION value=\"10\">10</OPTION>" +
											"<OPTION value=\"11\">11</OPTION>" +
											"<OPTION value=\"12\">12</OPTION>" +
											"<OPTION value=\"13\">13</OPTION>" +
											"<OPTION value=\"14\">14</OPTION>" +
											"<OPTION value=\"15\">15</OPTION>" +
											"<OPTION value=\"16\">16</OPTION>" +
											"<OPTION value=\"17\">17</OPTION>" +
											"<OPTION value=\"18\">18</OPTION>" +
											"<OPTION value=\"19\">19</OPTION>" +
											"<OPTION value=\"20\">20</OPTION>" +
											"<OPTION value=\"21\">21</OPTION>" +
											"<OPTION value=\"22\">22</OPTION>" +
											"<OPTION value=\"23\">23</OPTION>" +
											"<OPTION value=\"24\">24</OPTION>" +
											"<OPTION value=\"25\">25</OPTION>" +
											"<OPTION value=\"26\">26</OPTION>" +
											"<OPTION value=\"27\">27</OPTION>" +
											"<OPTION value=\"28\">28</OPTION>" +
											"<OPTION value=\"29\">29</OPTION>" +
											"<OPTION value=\"30\">30</OPTION>" +
											"<OPTION value=\"31\">31</OPTION>" +
										"</SELECT></TD>" +
									"<TD><SELECT class=\"wx_createmtg_ctrl\" name=\"wxCreateMtg_Year\" id=\"wxCreateMtg_Year\">" +
											"<OPTION value=\"2001\">2001</OPTION>" +
											"<OPTION value=\"2002\">2002</OPTION>" +
											"<OPTION value=\"2003\">2003</OPTION>" +
											"<OPTION value=\"2004\">2004</OPTION>" +
											"<OPTION value=\"2005\">2005</OPTION>" +
											"<OPTION value=\"2006\">2006</OPTION>" +
											"<OPTION value=\"2007\" selected>2007</OPTION>" +
											"<OPTION value=\"2008\">2008</OPTION>" +
											"<OPTION value=\"2009\">2009</OPTION>" +
											"<OPTION value=\"2010\">2010</OPTION>" +
											"<OPTION value=\"2011\">2011</OPTION>" +
											"<OPTION value=\"2012\">2012</OPTION>" +
										"</SELECT></TD>" +
								"</TR>" +
							"</TABLE>" +
						"</TD>" +
					"</TR>" +
					"<TR>" +
						"<TD class=\"wx_createmtg_label\">Time</TD>" +
						"<TD colSpan=\"3\">" +
							"<TABLE class=\"wx_createmtg\" id=\"Table1\" cellSpacing=\"0\" cellPadding=\"0\" border=\"0\">" +
								"<TR>" +
									"<TD><SELECT class=\"wx_createmtg_ctrl\" name=\"wxCreateMtg_Hour\" id=\"wxCreateMtg_Hour\">" +
											"<OPTION value=\"0\">0</OPTION>" +
											"<OPTION value=\"1\">1</OPTION>" +
											"<OPTION value=\"2\">2</OPTION>" +
											"<OPTION value=\"3\">3</OPTION>" +
											"<OPTION value=\"4\">4</OPTION>" +
											"<OPTION value=\"5\">5</OPTION>" +
											"<OPTION value=\"6\">6</OPTION>" +
											"<OPTION value=\"7\">7</OPTION>" +
											"<OPTION value=\"8\" selected>8</OPTION>" +
											"<OPTION value=\"9\">9</OPTION>" +
											"<OPTION value=\"10\">10</OPTION>" +
											"<OPTION value=\"11\">11</OPTION>" +
										"</SELECT></TD>" +
									"<TD><SELECT class=\"wx_createmtg_ctrl\" name=\"wxCreateMtg_Minute\" id=\"wxCreateMtg_Minute\">" +
											"<OPTION value=\"0\" selected>00</OPTION>" +
											"<OPTION value=\"15\">15</OPTION>" +
											"<OPTION value=\"30\">30</OPTION>" +
											"<OPTION value=\"45\">45</OPTION>" +
										"</SELECT></TD>" +
									"<TD><SELECT class=\"wx_createmtg_ctrl\" name=\"wxCreateMtg_AMPM\" id=\"wxCreateMtg_AMPM\">" +
											"<OPTION value=\"0\" selected>AM</OPTION>" +
											"<OPTION value=\"1\">PM</OPTION>" +
										"</SELECT></TD>" +
									"<TD><SELECT class=\"wx_createmtg_ctrl\" name=\"wxCreateMtg_TimeZone\" id=\"wxCreateMtg_TimeZone\">" +
											"<OPTION value=\"0\">Dateline (Eniwetok)</OPTION>" +
											"<OPTION value=\"1\">Samoa (Samoa)</OPTION>" +
											"<OPTION value=\"2\">Hawaii (Honolulu)</OPTION>" +
											"<OPTION value=\"3\">Alaska (Anchorage)</OPTION>" +
											"<OPTION value=\"4\" selected>Pacific (San Jose)</OPTION>" +
											"<OPTION value=\"5\">Mountain (Arizona)</OPTION>" +
											"<OPTION value=\"6\">Mountain (Denver)</OPTION>" +
											"<OPTION value=\"7\">Central (Chicago)</OPTION>" +
											"<OPTION value=\"8\">Mexico (Mexico City, Tegucigalpa)</OPTION>" +
											"<OPTION value=\"9\">Central (Regina)</OPTION>" +
											"<OPTION value=\"10\">America Pacific (Bogota)</OPTION>" +
											"<OPTION value=\"11\">Eastern (New York)</OPTION>" +
											"<OPTION value=\"12\">Eastern (Indiana)</OPTION>" +
											"<OPTION value=\"13\">Atlantic (Halifax)</OPTION>" +
											"<OPTION value=\"14\">S. America Western (Caracas)</OPTION>" +
											"<OPTION value=\"15\">Newfoundland (Newfoundland)</OPTION>" +
											"<OPTION value=\"16\">S. America Eastern (Brasilia)</OPTION>" +
											"<OPTION value=\"17\">S. America Eastern (Buenos Aires)</OPTION>" +
											"<OPTION value=\"18\">Mid-Atlantic (Mid-Atlantic)</OPTION>" +
											"<OPTION value=\"19\">Azores (Azores)</OPTION>" +
											"<OPTION value=\"20\">Greenwich (Casablanca)</OPTION>" +
											"<OPTION value=\"21\">GMT (London)</OPTION>" +
											"<OPTION value=\"22\">Europe (Amsterdam)</OPTION>" +
											"<OPTION value=\"23\">Europe (Paris)</OPTION>" +
											"<OPTION value=\"24\">Europe (Prague)</OPTION>" +
											"<OPTION value=\"25\">Europe (Berlin)</OPTION>" +
											"<OPTION value=\"26\">Greece (Athens)</OPTION>" +
											"<OPTION value=\"27\">Eastern Europe (Bucharest)</OPTION>" +
											"<OPTION value=\"28\">Egypt (Cairo)</OPTION>" +
											"<OPTION value=\"29\">South Africa (Pretoria)</OPTION>" +
											"<OPTION value=\"30\">Northern Europe (Helsinki)</OPTION>" +
											"<OPTION value=\"31\">Israel (Tel Aviv)</OPTION>" +
											"<OPTION value=\"32\">Saudi Arabia (Baghdad)</OPTION>" +
											"<OPTION value=\"33\">Russian (Moscow)</OPTION>" +
											"<OPTION value=\"34\">Nairobi (Nairobi)</OPTION>" +
											"<OPTION value=\"35\">Iran (Tehran)</OPTION>" +
											"<OPTION value=\"36\">Arabian (Abu Dhabi, Muscat)</OPTION>" +
											"<OPTION value=\"37\">Baku (Baku)</OPTION>" +
											"<OPTION value=\"38\">Afghanistan (Kabul)</OPTION>" +
											"<OPTION value=\"39\">West Asia (Ekaterinburg)</OPTION>" +
											"<OPTION value=\"40\">West Asia (Islamabad)</OPTION>" +
											"<OPTION value=\"41\">India (Bombay)</OPTION>" +
											"<OPTION value=\"42\">Columbo (Columbo)</OPTION>" +
											"<OPTION value=\"43\">Central Asia (Almaty)</OPTION>" +
											"<OPTION value=\"44\">Bangkok (Bangkok)</OPTION>" +
											"<OPTION value=\"45\">China (Beijing)</OPTION>" +
											"<OPTION value=\"46\">Australia Western (Perth)</OPTION>" +
											"<OPTION value=\"47\">Singapore (Singapore)</OPTION>" +
											"<OPTION value=\"48\">Taipei (Hong Kong)</OPTION>" +
											"<OPTION value=\"49\">Tokyo (Tokyo)</OPTION>" +
											"<OPTION value=\"50\">Korea (Seoul)</OPTION>" +
											"<OPTION value=\"51\">Yakutsk (Yakutsk)</OPTION>" +
											"<OPTION value=\"52\">Australia Central (Adelaide)</OPTION>" +
											"<OPTION value=\"53\">Australia Central (Darwin)</OPTION>" +
											"<OPTION value=\"54\">Australia Eastern (Brisbane)</OPTION>" +
											"<OPTION value=\"55\">Australia Eastern (Sydney)</OPTION>" +
											"<OPTION value=\"56\">West Pacific (Guam)</OPTION>" +
											"<OPTION value=\"57\">Tasmania (Hobart)</OPTION>" +
											"<OPTION value=\"58\">Vladivostok (Vladivostok)</OPTION>" +
											"<OPTION value=\"59\">Central Pacific (Solomon Is)</OPTION>" +
											"<OPTION value=\"60\">New Zealand (Wellington)</OPTION>" +
											"<OPTION value=\"61\">Fiji (Fiji)</OPTION>" +
										"</SELECT></TD>" +
								"</TR>" +
							"</TABLE>" +
						"</TD>" +
					"</TR>" +
					"<TR>" +
						"<TD class=\"wx_createmtg_label\">Attendees</TD>" +
						"<TD colSpan=\"3\"><INPUT class=\"wx_createmtg_ctrl\" type=\"text\" size=\"78\" name=\"wxCreateMtg_Attendees\" id=\"wxCreateMtg_Attendees\"></TD>" +
					"</TR>" +
					"<TR>" +
						"<TD class=\"wx_createmtg_label\">Password</TD>" +
						"<TD><INPUT class=\"wx_createmtg_ctrl\" type=\"password\" size=\"25\" name=\"wxCreateMtg_Password\" id=\"wxCreateMtg_Password\"></TD>" +
						"<TD class=\"wx_createmtg_label\">Confirm</TD>" +
						"<TD><INPUT class=\"wx_createmtg_ctrl\" type=\"password\" size=\"25\" name=\"wxCreateMtg_ConfirmPassword\" id=\"wxCreateMtg_ConfirmPassword\"></TD>" +
					"</TR>" +
				"</TABLE>" +
			"</td>" +
		"</tr>" +
	"</table>";
	
	return html;
}



/**  This is the html of the dialog:
		<table width="539" cellSpacing="0" cellPadding="0">
			<tr>
				<td class="wx_banner"></td>
			</tr>
			<tr>
				<td>
					<TABLE class="wx_createmtg" cellSpacing="0" cellPadding="5" width="100%" border="0">
						<TR>
							<TD class="wx_createmtg_label">Topic</TD>
							<TD colSpan="3"><INPUT class="wx_createmtg_ctrl" type="text" size="78"></TD>
						</TR>
						<TR>
							<TD class="wx_createmtg_label">Date</TD>
							<TD colSpan="3">
								<TABLE class="wx_createmtg" id="Table2" cellSpacing="0" cellPadding="0" border="0">
									<TR>
										<TD><SELECT class="wx_createmtg_ctrl" id="Select5" name="Select5">
												<OPTION>Jan</OPTION>
												<OPTION>Feb</OPTION>
												<OPTION>Mar</OPTION>
												<OPTION selected>Apr</OPTION>
												<OPTION>May</OPTION>
												<OPTION>Jun</OPTION>
												<OPTION>Jul</OPTION>
												<OPTION>Aug</OPTION>
												<OPTION>Sep</OPTION>
												<OPTION>Oct</OPTION>
												<OPTION>Nov</OPTION>
												<OPTION>Dec</OPTION>
											</SELECT></TD>
										<TD><SELECT class="wx_createmtg_ctrl" id="Select6" name="Select6">
												<OPTION>1</OPTION>
												<OPTION>2</OPTION>
												<OPTION>3</OPTION>
												<OPTION>4</OPTION>
												<OPTION>5</OPTION>
												<OPTION>6</OPTION>
												<OPTION>7</OPTION>
												<OPTION selected>8</OPTION>
												<OPTION>9</OPTION>
												<OPTION>10</OPTION>
												<OPTION>11</OPTION>
												<OPTION>12</OPTION>
												<OPTION>13</OPTION>
												<OPTION>14</OPTION>
												<OPTION>15</OPTION>
												<OPTION>16</OPTION>
												<OPTION>17</OPTION>
												<OPTION>18</OPTION>
												<OPTION>19</OPTION>
												<OPTION>20</OPTION>
												<OPTION>21</OPTION>
												<OPTION>22</OPTION>
												<OPTION>23</OPTION>
												<OPTION>24</OPTION>
												<OPTION>25</OPTION>
												<OPTION>26</OPTION>
												<OPTION>27</OPTION>
												<OPTION>28</OPTION>
												<OPTION>29</OPTION>
												<OPTION>30</OPTION>
												<OPTION>31</OPTION>
											</SELECT></TD>
										<TD><SELECT class="wx_createmtg_ctrl" id="Select7" name="Select7">
												<OPTION>2001</OPTION>
												<OPTION>2002</OPTION>
												<OPTION>2003</OPTION>
												<OPTION>2004</OPTION>
												<OPTION>2005</OPTION>
												<OPTION>2006</OPTION>
												<OPTION selected>2007</OPTION>
												<OPTION>2008</OPTION>
												<OPTION>2009</OPTION>
												<OPTION>2010</OPTION>
												<OPTION>2011</OPTION>
												<OPTION>2012</OPTION>
											</SELECT></TD>
									</TR>
								</TABLE>
							</TD>
						</TR>
						<TR>
							<TD class="wx_createmtg_label">Time</TD>
							<TD colSpan="3">
								<TABLE class="wx_createmtg" id="Table1" cellSpacing="0" cellPadding="0" border="0">
									<TR>
										<TD><SELECT class="wx_createmtg_ctrl" id="Select1" name="Select1">
												<OPTION>1</OPTION>
												<OPTION>2</OPTION>
												<OPTION>3</OPTION>
												<OPTION>4</OPTION>
												<OPTION>5</OPTION>
												<OPTION>6</OPTION>
												<OPTION>7</OPTION>
												<OPTION selected>8</OPTION>
												<OPTION>9</OPTION>
												<OPTION>10</OPTION>
												<OPTION>11</OPTION>
												<OPTION>12</OPTION>
											</SELECT></TD>
										<TD><SELECT class="wx_createmtg_ctrl" id="Select2" name="Select2">
												<OPTION selected>00</OPTION>
												<OPTION>15</OPTION>
												<OPTION>30</OPTION>
												<OPTION>45</OPTION>
											</SELECT></TD>
										<TD><SELECT class="wx_createmtg_ctrl" id="Select3" name="Select3">
												<OPTION selected>AM</OPTION>
												<OPTION>PM</OPTION>
											</SELECT></TD>
										<TD><SELECT class="wx_createmtg_ctrl" id="Select4" name="Select4">
												<OPTION selected>Dateline (Eniwetok)</OPTION>
												<OPTION>Samoa (Samoa)</OPTION>
												<OPTION>Hawaii (Honolulu)</OPTION>
												<OPTION>Alaska (Anchorage)</OPTION>
												<OPTION>Pacific (San Jose)</OPTION>
												<OPTION>Mountain (Arizona)</OPTION>
												<OPTION>Mountain (Denver)</OPTION>
												<OPTION>Central (Chicago)</OPTION>
												<OPTION>Mexico (Mexico City, Tegucigalpa)</OPTION>
												<OPTION>Central (Regina)</OPTION>
												<OPTION>America Pacific (Bogota)</OPTION>
												<OPTION>Eastern (New York)</OPTION>
												<OPTION>Eastern (Indiana)</OPTION>
												<OPTION>Atlantic (Halifax)</OPTION>
												<OPTION>S. America Western (Caracas)</OPTION>
												<OPTION>Newfoundland (Newfoundland)</OPTION>
												<OPTION>S. America Eastern (Brasilia)</OPTION>
												<OPTION>S. America Eastern (Buenos Aires)</OPTION>
												<OPTION>Mid-Atlantic (Mid-Atlantic)</OPTION>
												<OPTION>Azores (Azores)</OPTION>
												<OPTION>Greenwich (Casablanca)</OPTION>
												<OPTION>GMT (London)</OPTION>
												<OPTION>Europe (Amsterdam)</OPTION>
												<OPTION>Europe (Paris)</OPTION>
												<OPTION>Europe (Prague)</OPTION>
												<OPTION>Europe (Berlin)</OPTION>
												<OPTION>Greece (Athens)</OPTION>
												<OPTION>Eastern Europe (Bucharest)</OPTION>
												<OPTION>Egypt (Cairo)</OPTION>
												<OPTION>South Africa (Pretoria)</OPTION>
												<OPTION>Northern Europe (Helsinki)</OPTION>
												<OPTION>Israel (Tel Aviv)</OPTION>
												<OPTION>Saudi Arabia (Baghdad)</OPTION>
												<OPTION>Russian (Moscow)</OPTION>
												<OPTION>Nairobi (Nairobi)</OPTION>
												<OPTION>Iran (Tehran)</OPTION>
												<OPTION>Arabian (Abu Dhabi, Muscat)</OPTION>
												<OPTION>Baku (Baku)</OPTION>
												<OPTION>Afghanistan (Kabul)</OPTION>
												<OPTION>West Asia (Ekaterinburg)</OPTION>
												<OPTION>West Asia (Islamabad)</OPTION>
												<OPTION>India (Bombay)</OPTION>
												<OPTION>Columbo (Columbo)</OPTION>
												<OPTION>Central Asia (Almaty)</OPTION>
												<OPTION>Bangkok (Bangkok)</OPTION>
												<OPTION>China (Beijing)</OPTION>
												<OPTION>Australia Western (Perth)</OPTION>
												<OPTION>Singapore (Singapore)</OPTION>
												<OPTION>Taipei (Hong Kong)</OPTION>
												<OPTION>Tokyo (Tokyo)</OPTION>
												<OPTION>Korea (Seoul)</OPTION>
												<OPTION>Yakutsk (Yakutsk)</OPTION>
												<OPTION>Australia Central (Adelaide)</OPTION>
												<OPTION>Australia Central (Darwin)</OPTION>
												<OPTION>Australia Eastern (Brisbane)</OPTION>
												<OPTION>Australia Eastern (Sydney)</OPTION>
												<OPTION>West Pacific (Guam)</OPTION>
												<OPTION>Tasmania (Hobart)</OPTION>
												<OPTION>Vladivostok (Vladivostok)</OPTION>
												<OPTION>Central Pacific (Solomon Is)</OPTION>
												<OPTION>New Zealand (Wellington)</OPTION>
												<OPTION>Fiji (Fiji)</OPTION>
											</SELECT></TD>
									</TR>
								</TABLE>
							</TD>
						</TR>
						<TR>
							<TD class="wx_createmtg_label">Attendees</TD>
							<TD colSpan="3"><INPUT class="wx_createmtg_ctrl" type="text" size="78"></TD>
						</TR>
						<TR>
							<TD class="wx_createmtg_label">Password</TD>
							<TD><INPUT class="wx_createmtg_ctrl" type="password" size="25"></TD>
							<TD class="wx_createmtg_label">Confirm</TD>
							<TD><INPUT class="wx_createmtg_ctrl" type="password" size="25"></TD>
						</TR>
					</TABLE>
				</td>
			</tr>
		</table>
**/