/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2009, 2010 Zimbra, Inc.
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 *@Author Raja Rao DV
 */

/**
 * Constructor.
 *
 */
function com_zimbra_birthdayreminder_HandlerObject() {
}

com_zimbra_birthdayreminder_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_birthdayreminder_HandlerObject.prototype.constructor = com_zimbra_birthdayreminder_HandlerObject;

/**
 * Simplify Zimlet handler name.
 */
var BirthdayReminderZimlet = com_zimbra_birthdayreminder_HandlerObject;

/**
 * Defines the "calendar" view type.
 */
BirthdayReminderZimlet.CALENDAR_VIEW = "appointment";


/**
 * Defines reminder type. ON_THE_DAY creates reminder on the birth day
 */
BirthdayReminderZimlet.REMINDER_TYPE_ON_THE_DAY= "ON_THE_DAY";

/**
 *  Defines reminder type. DAYS_BEFORE creates reminder x days before Birth day
 */
BirthdayReminderZimlet.REMINDER_TYPE_DAYS_BEFORE= "DAYS_BEFORE";

/**
 *  Defines reminder type. WEEKS_BEFORE creates reminder x days before Birth day
 */
BirthdayReminderZimlet.REMINDER_TYPE_WEEKS_BEFORE= "WEEKS_BEFORE";


/**
 *  Initialize
 */
BirthdayReminderZimlet.prototype.init =
function() {
	this.calendarName = this.getMessage("BirthdayReminder_CalendarName");
};


/**
 *  Called by Framework and adds toolbar button to Contact Edit view
 */
BirthdayReminderZimlet.prototype.initializeToolbar = function(app, toolbar, controller, viewId) {
	if (viewId == "CN") {
		this._initBirthdayReminderToolbar(toolbar, controller);
	}
};

/**
 *  Called by Framework and adds toolbar button
 */
BirthdayReminderZimlet.prototype._initBirthdayReminderToolbar = function(toolbar, controller) {
	if (!toolbar.getButton("SAVE_AND_BIRTHDAY")) {
		var btn = toolbar.createOp("SAVE_AND_BIRTHDAY", {image:"Save", text:"Save & Add Birthday", tooltip:"Saves Contact and creates birthday reminders", index:1});
		var buttonIndex = 0;
		toolbar.addOp("SAVE_AND_BIRTHDAY", buttonIndex);
		this._composerCtrl = controller;
		this._composerCtrl._birthdayReminderZimlet = this;
		btn.addSelectionListener(new AjxListener(this._composerCtrl, this._saveAndAddHandler));
	}
};

/**
 * Fethes Birthday information(if any), save the contact and shows Birthday Reminder dialog w/ this Birthday information
 */
BirthdayReminderZimlet.prototype._saveAndAddHandler = function() {
	var view = this._listView[this._currentView];
	var mods = view.getModifiedAttrs();
	var birthday = "";
	var fullName = "";
	var email = "";
	if(mods && mods.birthday) {
		birthday = mods.birthday;
	}
	if(mods && mods.email) {
		email = mods.email;
	}
	if(mods && mods.firstName && mods.lastName) {
		fullName = [mods.firstName, " ",  mods.lastName].join("");
	} else if(mods && mods.firstName) { //just use first name
		fullName = mods.firstName;
	}
	if(view._items){
		if(birthday == "" && view._items.OTHER != undefined && view._items.OTHER.value != undefined) {
			var arry = view._items.OTHER.value;
			for(var i =0; i < arry.length; i++) {
				var obj = arry[i];
				if(obj.type == "birthday") {
					birthday = obj.value;
				}
			}
		}
		
		if(birthday != "" && fullName  == "" && view._items.FULLNAME != undefined && view._items.FULLNAME.value != undefined) {
			fullName = view._items.FULLNAME.value;
		}

		
		if(birthday != "" &&  email == ""  && view._items.EMAIL != undefined && view._items.EMAIL.value != undefined) {
			 if(view._items.EMAIL.value instanceof Array) {
				email = view._items.EMAIL.value[0];
			} else {
				email = view._items.EMAIL.value;
			}
		}
	}
	if(birthday != "") {//test for yyyy-mm-dd format
		var test= /\d{4}-\d{2}-\d{2}/.test(birthday);
		if(!test) {
			appCtxt.getAppController().setStatusMsg({msg:this._birthdayReminderZimlet.getMessage("BirthdayReminder_formatIncorrect"), level:ZmStatusView.LEVEL_WARNING});
			return;
		}
	}

	//At this point, we have everything from Contacts view. so save it.
	this._saveListener();

	if(birthday != "" && (fullName != "" || email != "")) {
		this._birthdayReminderZimlet.filteredContactsArry = new Array();
		this._birthdayReminderZimlet.filteredContactsArry.push({attr:{birthday:birthday, fullName:fullName, email: email}});
		this._birthdayReminderZimlet._setBirthdayReminderFolderId(new AjxCallback(this._birthdayReminderZimlet, this._birthdayReminderZimlet._showBRDlg));
	}	
};

/**
 *  Deletes Birthday Reminders Calendar
 */
BirthdayReminderZimlet.prototype._deleteBRFolder =
function(postCallback) {
	if(!this.birthdayreminderFolderId) {
		this._setBirthdayReminderFolderId(postCallback);
		return;
	}
	var jsonObj = {
		FolderActionRequest: {
			_jsns:	"urn:zimbraMail",
			action:	{
				op:		"delete",
				id:		this.birthdayreminderFolderId
			}
		}
	};

	var response=  appCtxt.getAppController().sendRequest({jsonObj:jsonObj, asyncMode:false});
	if(!response) {
		appCtxt.getAppController().setStatusMsg(this.getMessage("BirthdayReminder_couldNotDeleteCalendar"), ZmStatusView.LEVEL_WARNING);
	} else {
		this._setBirthdayReminderFolderId(postCallback);
	}
};



/**
 * Sets the birthday reminder folder id.
 */
BirthdayReminderZimlet.prototype._setBirthdayReminderFolderId =
function(postCallback) {
	var soapDoc = AjxSoapDoc.create("GetFolderRequest", "urn:zimbraMail");
	var folderNode = soapDoc.set("folder");
	folderNode.setAttribute("l", appCtxt.getFolderTree().root.id);

	var command = new ZmCsfeCommand();
	var top = command.invoke({soapDoc: soapDoc}).Body.GetFolderResponse.folder[0];

	var folders = top.folder;
	if (folders) {
		for (var i = 0; i < folders.length; i++) {
			var f = folders[i];
			if (f && f.name == this.calendarName && f.view == BirthdayReminderZimlet.CALENDAR_VIEW) {
				this.birthdayreminderFolderId = f.id;
				if(postCallback) {
					postCallback.run(this);
				}
				return;
			}
		}
	}
	//there is no such folder, so create one.
	var params = {color:3, name:this.calendarName, view:BirthdayReminderZimlet.CALENDAR_VIEW, l:"1", postCallback:postCallback};
	this._createBirthdayCalendar(params);
};


/**
 * Creates Calendar folder.
 *
 * @param {hash} params 		a hash of parameters
 * @param  {number} params.color  	the id of folder color
 * @param  {string} params.name  	the calendar name
 * @param  {string} params.view		the view name "Calendar"|"Mail" etc
 * @param  {number} params.l 		the parent folder id
 * @param  {AjxCallback} params.postcallback  a callback that should be called upon successful AJAX call
 */
BirthdayReminderZimlet.prototype._createBirthdayCalendar =
function(params) {
	var jsonObj = {CreateFolderRequest:{_jsns:"urn:zimbraMail"}};
	var folder = jsonObj.CreateFolderRequest.folder = {};
	for (var i in params) {
		if (i == "callback" || i == "errorCallback" || i == "postCallback") {
			continue;
		}

		var value = params[i];
		if (value) {
			folder[i] = value;
		}
	}
	var _createFldrCallback = new AjxCallback(this, this._createFldrCallback, params);
	var _createFldrErrCallback = new AjxCallback(this, this._createFldrErrCallback, params);
	return appCtxt.getAppController().sendRequest({jsonObj:jsonObj, asyncMode:true, errorCallback:_createFldrErrCallback, callback:_createFldrCallback});
};

/**
 * Handles CreateFolderResponse on Success.
 *
 * @param {hash} params 		a hash of parameters
 * @param  {number} params.color  	the id of folder color
 * @param  {string} params.name  	the calendar name
 * @param  {string} params.view		the view name "Calendar"|"Mail" etc
 * @param  {number} params.l 		the parent folder id
 * @param {object} response 	contains the createFolderResponse object
 */
BirthdayReminderZimlet.prototype._createFldrCallback =
function(params, response) {
	if (params.name == this.calendarName) {
		var transitions = [ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.FADE_OUT];
		appCtxt.getAppController().setStatusMsg(this.getMessage("BirthdayReminder_BRCalendarCreated"), ZmStatusView.LEVEL_INFO, null, transitions);
		this.birthdayreminderFolderId = response.getResponse().CreateFolderResponse.folder[0].id;
		if (params.postCallback) {
			params.postCallback.run(this);
		}
	} else {
		var transitions = [ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.FADE_OUT];
		appCtxt.getAppController().setStatusMsg(this.getMessage("BirthdayReminder_couldNotCreateCalendar"), ZmStatusView.LEVEL_WARNING, null, transitions);
	}
};

/**
 * Handles Create Folder Response on Error.
 *
 * @param {hash} params 		a hash of parameters
 * @param  {number} params.color  	the id of folder color
 * @param  {string} params.name  	the calendar name
 * @param  {string} params.view		the view name "Calendar"|"Mail" etc
 * @param  {number} params.l 		the parent folder id
 * @param {object} ex 		contains the createFolderResponse object
 */
BirthdayReminderZimlet.prototype._createFldrErrCallback =
function(params, ex) {
	if (!params.url && !params.name) {
		return false;
	}
	var msg;
	if (params.name && (ex.code == ZmCsfeException.MAIL_ALREADY_EXISTS)) {
		msg = AjxMessageFormat.format(ZmMsg.errorAlreadyExists, [params.name]);
	} else if (params.url) {
		var errorMsg = (ex.code == ZmCsfeException.SVC_RESOURCE_UNREACHABLE) ? ZmMsg.feedUnreachable : ZmMsg.feedInvalid;
		msg = AjxMessageFormat.format(errorMsg, params.url);
	}
	var transitions = [ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.FADE_OUT];
	appCtxt.getAppController().setStatusMsg(this.getMessage("BirthdayReminder_couldNotCreateCalendar"), ZmStatusView.LEVEL_WARNING, null, transitions);
	if (msg) {
		this._showErrorMsg(msg);
		return true;
	}
	return false;
};
/**
 * Shows Error message dialog.
 * 
 * @param {string} msg  	the error message
 */
BirthdayReminderZimlet.prototype._showErrorMsg =
function(msg) {
	var msgDialog = appCtxt.getMsgDialog();
	msgDialog.reset();
	msgDialog.setMessage(msg, DwtMessageDialog.CRITICAL_STYLE);
	msgDialog.popup();
};

/**
 * Creates 3 appointments (on the day, days before and weeks before to remind you of Birthday).
 *
 * @param {hash} obj		a hash of parameters
 * @param  {date} obj.b  	the birthday date
 * @param  {string} obj.email  	the mail address of  birthday boy/girl
 * @param  {string} obj.chkbx_id  	the id of the checkbox corresponding to the Birthday contact
 * @param {number} currentCnt		the birthday Contact number (used to show status)
 * @param {number} total 	the total number of contacts for which Reminders will be created
 */
BirthdayReminderZimlet.prototype._create3Appts =
function(obj, currentCnt, total) {
	var msg = this.getMessage("BirthdayReminder_CreatingReminderMsg").replace("{0}", currentCnt).replace("{1}", total);
	appCtxt.getAppController().setStatusMsg(msg , ZmStatusView.LEVEL_INFO);
	var chkbx = document.getElementById(obj.chkbx_id);
	if (!chkbx.checked)
		return;

	if (document.getElementById("breminder_onTheDayChk").checked) {
		this._createVariousAppts(obj, BirthdayReminderZimlet.REMINDER_TYPE_ON_THE_DAY);
	}

	if (document.getElementById("breminder_daysChk").checked) {
		this._createVariousAppts(obj, BirthdayReminderZimlet.REMINDER_TYPE_DAYS_BEFORE);
	}
	if (document.getElementById("breminder_weeksChk").checked) {
		this._createVariousAppts(obj, BirthdayReminderZimlet.REMINDER_TYPE_WEEKS_BEFORE);
	}
};

/**
 * Creates various kinds of Appointments to setup reminders
 *
 * @param {hash} obj		a hash of parameters
 * @param  {date} obj.b  	the birthday date
 * @param  {string} obj.email  	the mail address of birthday contact
 * @param  {string} obj.chkbx_id  the id of the checkbox corresponding to the Birthday contact
 * @param {string} apptType 	the appointment type (see <code>BirthdayReminderZimlet.REMINDER_TYPE_</code> constants)
 */
BirthdayReminderZimlet.prototype._createVariousAppts =
function(obj, apptType) {
	var tmparry = obj.b.split("-");
	var birthday = this._normalizeDate(tmparry[2],  tmparry[1], tmparry[0]);
	var subject = "";
	var startDate = "";
	var email_name = "";
	if (obj.e == "") {
		email_name = obj.fn;
	} else if (obj.fn == "") {
		email_name = obj.e;
	} else {
		email_name = obj.fn + " (" + obj.e + ")";
	}
	if (apptType == BirthdayReminderZimlet.REMINDER_TYPE_ON_THE_DAY) {
		startDate = AjxDateUtil.simpleParseDateStr(birthday);
		subject = this.getMessage("BirthdayReminder_TodayIsBirthday").replace("{0}", email_name);
	} else if (apptType == BirthdayReminderZimlet.REMINDER_TYPE_DAYS_BEFORE) {
		var breminder_daysSlct = document.getElementById("breminder_daysSlct");
		var daysCnt = breminder_daysSlct.options[breminder_daysSlct.selectedIndex].text;
		daysCnt = parseInt(daysCnt);
		startDate = AjxDateUtil.simpleParseDateStr(birthday);
		startDate.setHours("0", "00");
		startDate = new Date(startDate.getTime() - (daysCnt * 24 * 3600 * 1000));
		subject = this.getMessage("BirthdayReminder_BirthdayDaysAway").replace("{0}", email_name).replace("{1}", daysCnt);
		if(daysCnt > 1) {
			subject = subject.replace("{2}", this.getMessage("BirthdayReminder_days"));
		} else {
			subject = subject.replace("{2}",  this.getMessage("BirthdayReminder_day"));
		}
	} else if (apptType == BirthdayReminderZimlet.REMINDER_TYPE_WEEKS_BEFORE) {
		var breminder_weekSlct = document.getElementById("breminder_weekSlct");
		var weeksCnt = breminder_weekSlct.options[breminder_weekSlct.selectedIndex].text;
		weeksCnt = parseInt(weeksCnt);
		startDate = AjxDateUtil.simpleParseDateStr(birthday);
		startDate.setHours("0", "00");
		startDate = new Date(startDate.getTime() - (weeksCnt * 7 * 24 * 3600 * 1000));
		subject = this.getMessage("BirthdayReminder_BirthdayWeeksAway").replace("{0}", email_name).replace("{1}", weeksCnt);
		if(weeksCnt > 1) {
			subject = subject.replace("{2}",  this.getMessage("BirthdayReminder_weeks"));
		} else {
			subject = subject.replace("{2}",  this.getMessage("BirthdayReminder_week"));
		}
	}
	startDate.setHours("5", "00");
	var endDate = new Date(startDate.getTime() + 4000 * 60 * 60);
	this._createAppt(startDate, endDate, subject);
};



/**
 * Takes months, day and year info and returns timezone specific date format.
 * 
 * @param {string} month    the month
 * @param {string} day  	the day
 * @param {string} year		the year
 * @return {string}	timezone specific date format
 */
BirthdayReminderZimlet.prototype._normalizeDate =
function(month, day, year) {
	var fString = [];
	var ds = I18nMsg.formatDateShort.toLowerCase();
	var arry = [];
	arry.push({name:"m", indx:ds.indexOf("m")});
	arry.push({name:"y", indx:ds.indexOf("y")});
	arry.push({name:"d", indx:ds.indexOf("d")});
	var sArry = arry.sort(BirthdayReminder_sortTimeObjs);
	for(var i = 0; i < sArry.length; i++) {
		var name = sArry[i].name;
		if(name == "m") {
			fString.push(month);
		} else if(name == "y") {
			fString.push(year);
		}  else if(name == "d") {
			fString.push(day);
		} 
	}
	return fString.join("/");
};

function BirthdayReminder_sortTimeObjs(a, b) {
	var x = parseInt(a.indx);
	var y = parseInt(b.indx);
	return ((x > y) ? 1 : ((x < y) ? -1 : 0));
}

/**
 * Creates an Appointment.
 *
 * @param {date} startDate 	the start date of the appointment
 * @param {date} endDate	the end date of the appointment
 * @param {string} subject	the subject of the appointment
 */
BirthdayReminderZimlet.prototype._createAppt =
function(startDate, endDate, subject) {
	var reminderMinutes = appCtxt.getSettings().getSetting("CAL_REMINDER_WARNING_TIME").value;
	try {
		var appt = new ZmAppt();
		appt.setStartDate(startDate);
		appt.setEndDate(endDate);
		appt.setName(subject);
		appt.setRecurType(ZmRecurrence.YEARLY);
		appt.setReminderMinutes(reminderMinutes);
		appt.freeBusy = "F";
		appt.privacy = "PRI";
		appt.transparency = "O";
		appt.setFolderId(this.birthdayreminderFolderId);
		appt.save();
	} catch(e) {
	}
};

/**
 * Creates a Birthday Reminder dialog view.
 *
 * @return {string} the Html of the view
 */
BirthdayReminderZimlet.prototype._createBRView =
function() {
	var html = new Array();
	var i = 0;
	var id_indx = 0;
	this._bDayAndEmail = new Array();
	html[i++] = "<DIV class='breminder_msgDiv' id='breminder_foundMsgId'>";
	html[i++]  = this.getMessage("BirthdayReminder_FoundContacts").replace("{0}",this.filteredContactsArry.length);
	html[i++] = "</DIV>";
	html[i++] = ["<div style='padding:2px'><a id='breminder_selectAllOrNone' href=# style='font-weight:bold;'>",
				this.getMessage("BirthdayReminder_selectAllOrNone"),"</a></div>"].join("");
	html[i++] = "<DIV class='breminder_mainDiv' style=\"overflow:auto;height:260px;width:99%\" >";
	for (var j = 0; j < this.filteredContactsArry.length; j++) {
		var contact = this.filteredContactsArry[j];
		var card = new Array();
		var n = 0;


		var attr = contact.attr ? contact.attr : contact._attrs;
		var email = attr["email"] ? attr["email"] : (attr["email2"] ? attr["email2"] : "");
		var birthday = attr["birthday"] ? attr["birthday"] : "";
		var fullName = attr["fullName"] ? attr["fullName"] : "";

		var k = 0;
		var chkbxId = "breminder_chkbox" + id_indx;
		var tmparry = birthday.split("-");
		var normalizedBD = this._normalizeDate(tmparry[2],  tmparry[1], tmparry[0]);
		html[i++] = ["<DIV id='breminder_cardHdrDiv", id_indx, "' class='breminder_cardHdrDiv'>"
			, "<TABLE width='100%' CELLPADDING=3 class='breminder_HdrTable'>", "<TR><TD><input id='", chkbxId
			, "'  type='checkbox' checked/></TD>",
			,"<TD  width='75%'>", fullName, "<label style='color:gray;font-weight:normal'>&nbsp;&nbsp;", email,"</label></TD>"
			,"<TD  width='20%'>", normalizedBD, "</TD></TR></TABLE></DIV>"].join("");

		this._bDayAndEmail[j] = {b:birthday, e:email, fn:fullName, chkbx_id: chkbxId};
		id_indx++;
	}
	html[i++] = "</DIV>";
	html[i++] = "<DIV>";
	html[i++] = ["<input id='breminder_onTheDayChk' type='checkbox' checked>",this.getMessage("BirthdayReminder_RemindMeOnTheDay"),"</input>"].join("");
	html[i++] = "</DIV>";
	html[i++] = "<DIV>";
	html[i++] = "<input id='breminder_daysChk' type='checkbox' checked> </input>";
	html[i++] =  this.getMessage("BirthdayReminder_AlsoRemindMeDaysBefore").replace("{0}", this._getDaysMenu());
	html[i++] = "</DIV>";
	html[i++] = "<DIV>";
	html[i++] = "<input id='breminder_weeksChk' type='checkbox' checked> </input>";
	html[i++] =  this.getMessage("BirthdayReminder_AlsoRemindMeWeeksBefore").replace("{0}", this._getWeekMenu());
	html[i++] = "</DIV>";

	html[i++] = "<br/><DIV>";
	html[i++] = ["<input id='breminder_resetBRCalendar' type='checkbox' title='",this.getMessage("BirthdayReminder_resetHelp"),"'> </input>",
			"<label style='color:brown'>", this.getMessage("BirthdayReminder_resetBRCalendar"), 
			"&nbsp;&nbsp;</label><a style='color:blue;text-decoration:underline;font-weight:bold' title='",
			this.getMessage("BirthdayReminder_resetHelp"),"'>help</a>"].join("");
	html[i++] = "</DIV>";
	return html.join("");
};

/**
 * Gets html of days menu
 *
 * @return {string}	HTML select menu for days
 */
BirthdayReminderZimlet.prototype._getDaysMenu =
function() {
	var html = new Array();
	var j = 0;
	html[j++] = "<SELECT id='breminder_daysSlct'>";
	for (var i = 1; i <= 7; i++) {
		if (i == 2) {
			html[j++] = ["<option value=\"", i, "",this.getMessage("BirthdayReminder_days"),"\" selected>", i , "</option>"].join("");
		} else {
			html[j++] = ["<option value=\"", i, "",this.getMessage("BirthdayReminder_days"),"\">", i , "</option>"].join("");
		}
	}
	html[j++] = "</SELECT>";

	return html.join("");
};

/**
 * Gets HTML of weeks menu.
 * 
 * @return {string}	the HTML select menu for weeks
 */
BirthdayReminderZimlet.prototype._getWeekMenu =
function() {
	var html = new Array();
	var j = 0;
	html[j++] = "<SELECT id='breminder_weekSlct'>";
	for (var i = 1; i <= 8; i++) {
		if (i == 1)  {
			html[j++] = ["<option value=\"", i, "",this.getMessage("BirthdayReminder_weeks"),"\" selected>", i , "</option>"].join("");
		} else {
			html[j++] = ["<option value=\"", i, "",this.getMessage("BirthdayReminder_weeks"),"\">", i , "</option>"].join("");
		}
	}
	html[j++] = "</SELECT>";

	return html.join("");
};

/**
 * Displays preferences dialog.
 * 
 */
BirthdayReminderZimlet.prototype._showPrefDialog =
function() {
	//if zimlet dialog already exists...
	if (this.pbDialog) {
		document.getElementById("BirthdayReminder_showStatusDiv").innerHTML = "";
		this.pbDialog.popup();
		return;
	}
	this.pView = new DwtComposite(this.getShell());
	this.pView.getHtmlElement().innerHTML = this._createPrefView();
	var scanABButtonId = Dwt.getNextId();
	var scanABButton = new DwtDialog_ButtonDescriptor(scanABButtonId, this.getMessage("BirthdayReminder_scanForBirthdays"), DwtDialog.ALIGN_CENTER);

	var dialog_args = {
			title	: this.getMessage("BirthdayReminder_zimletPreferences"),
			view	: this.pView,
        	parent	: this.getShell(),
			standardButtons : [DwtDialog.CANCEL_BUTTON],
			extraButtons : [scanABButton]
	};

	this.pbDialog = new ZmDialog(dialog_args);
	this.pbDialog.setButtonListener(scanABButtonId, new AjxListener(this, this._scanABListner));

	this.pbDialog.popup();
};

/**
 * Creates Preferences view.
 *
 * @return {string} the HTML of the preferences view
 */
BirthdayReminderZimlet.prototype._createPrefView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV>";
	html[i++] = this.getMessage("BirthdayReminder_PleaseNote").replace("{0}", this.getMessage("BirthdayReminder_CalendarName"));
	html[i++] = "</DIV><br/>";
	html[i++] = "<DIV><label id='BirthdayReminder_showStatusDiv' style='color:blue;font:bold'></label></div>";
	return html.join("");
};

/**
 * Scans the Address Book.
 * 
 */
BirthdayReminderZimlet.prototype._scanABListner =
function() {
	//this.pbDialog.popdown();
	this._setBirthdayReminderFolderId(new AjxCallback(this, this._startScanning));
};

/**
 * Starts scanning the address book.
 * 
 */
BirthdayReminderZimlet.prototype._startScanning =
function() {
	document.getElementById("BirthdayReminder_showStatusDiv").innerHTML = this.getMessage("BirthdayReminder_PleaseWait");
	if (this._apptComposeController == undefined) {//load calendar package when we are creating appt for the first time(since login)
		this._apptComposeController = AjxDispatcher.run("GetApptComposeController");
	}
	this.filteredContactsArry = new Array();
	this.__oldNumContacts = 0;
	this._noOpLoopCnt = 0;
	this._totalWaitCnt = 0;

	this._contactsAreLoaded = false;
	this._waitForContactToLoadAndProcess();
	this._contactsAreLoaded = true;
};


/**
 * Refreshes the browser.
 * 
 */
BirthdayReminderZimlet.prototype._refreshBrowser =
function() {
	window.onbeforeunload = null;
	var url = AjxUtil.formatUrl({});
	ZmZimbraMail.sendRedirect(url);
};

/**
 * Called when the zimlet panel is double-clicked.
 */
BirthdayReminderZimlet.prototype.doubleClicked = function() {
	this.singleClicked();
};

/**
 * Called when the zimlet panel is single-clicked.
 */
BirthdayReminderZimlet.prototype.singleClicked = function() {
	this._showPrefDialog();
};

/**
 * Waits for all contacts to load by checking their count and waiting if the count increases b/w checks
 */
BirthdayReminderZimlet.prototype._waitForContactToLoadAndProcess = function() {
	this._contactList = AjxDispatcher.run("GetContacts");
	if (!this._contactList)
		return;

	this.__currNumContacts = this._contactList.getArray().length;
	if (this._totalWaitCnt < 2 || this._noOpLoopCnt < 3) {//minimum 2 cycles post currentCnt==oldCnt
		if (this.__oldNumContacts == this.__currNumContact) {
			this._noOpLoopCnt++;
		}
		this._totalWaitCnt++;
		this.__oldNumContacts = this.__currNumContact;
		setTimeout(AjxCallback.simpleClosure(this._waitForContactToLoadAndProcess, this), 5000);
	} else {
		this.pbDialog.popdown();
		this._startProcessing();//start processing
	}
};

/**
 * Starts processing Address Book.
 * 
 */
BirthdayReminderZimlet.prototype._startProcessing = function() {
	var _tmpArry = this._contactList.getArray();

	for (var j = 0; j < _tmpArry.length; j++) {
		var currentContact = _tmpArry[j];

		var attr = currentContact.attr ? currentContact.attr : currentContact._attrs;
		try {
			if (attr.birthday != undefined) {
				this.filteredContactsArry.push(currentContact);
			}
		} catch(e) {
		}
	}
	appCtxt.getAppController().setStatusMsg(this.getMessage("BirthdayReminder_scanningCompleted"), ZmStatusView.LEVEL_INFO);
	this._showBRDlg();
};

/**
 * Shows Birthday Reminder dialog.
 * 
 */
BirthdayReminderZimlet.prototype._showBRDlg = function() {
	//if zimlet dialog already exists...
	if (this._brDialog) {
		this._brDlgView.getHtmlElement().innerHTML = this._createBRView();
		this._addListeners();
		this._brDialog.popup();
		return;
	}
	this._brDlgView = new DwtComposite(this.getShell());
	this._brDlgView.setSize("510", "420");
	this._brDlgView.getHtmlElement().style.overflow = "auto";
	this._brDlgView.getHtmlElement().innerHTML = this._createBRView();
	var reminderBtnId = Dwt.getNextId();
	var createRemindersBtn = new DwtDialog_ButtonDescriptor(reminderBtnId, this.getMessage("BirthdayReminder_createReminders"), DwtDialog.ALIGN_RIGHT);

	var dialog_args = {
			title	: this.getMessage("BirthdayReminder_createRemindersForBirthdays"),
			view	: this._brDlgView,
        	parent	: this.getShell(),
			standardButtons : [DwtDialog.CANCEL_BUTTON],
			extraButtons:[createRemindersBtn]
	};

	this._brDialog = new ZmDialog(dialog_args);
	this._brDialog.setButtonListener(reminderBtnId, new AjxListener(this, this._handleResetBRCalendar));
	this._addListeners();
	this._brDialog.popup();
};

BirthdayReminderZimlet.prototype._handleResetBRCalendar =
function() {
	if(document.getElementById("breminder_resetBRCalendar").checked) {
		this._deleteBRFolder(new AjxCallback(this, this._createRemindersListener));
	} else {
		this._createRemindersListener();
	}
}

/**
 * Creates the Reminders.
 * 
 */
BirthdayReminderZimlet.prototype._createRemindersListener =
function() {
	this._reloadRequired = false;
	this._atleastOneContactWasChecked = false;
	appCtxt.getAppController().setStatusMsg(this.getMessage("BirthdayReminder_pleaseWait"), ZmStatusView.LEVEL_INFO);
	var counter = 1;
	for (var i = 0; i < this._bDayAndEmail.length; i++) {
		var obj = this._bDayAndEmail[i];
		var chkbx = document.getElementById(obj.chkbx_id);
		if (!chkbx.checked) {
			continue;
		}
		this._atleastOneContactWasChecked = true;
		//this._create3Appts(this._bDayAndEmail[i]);
		//schedule appt creation every 3 seconds( counter*4000) instead of all at once
		setTimeout(AjxCallback.simpleClosure(this._create3Appts, this, obj, counter, this._bDayAndEmail.length), counter * 4000);
		counter++;
	}
	if(!this._atleastOneContactWasChecked) {
		appCtxt.getAppController().setStatusMsg({msg:this.getMessage("BirthdayReminder_noContactsWereChecked"), level:ZmStatusView.LEVEL_WARNING});
		return;
	}
	//say done at the very end
	setTimeout(AjxCallback.simpleClosure(this._saydone, this), counter * 4000);//counter would know exactly how much to wait
	this._brDialog.popdown();
	if (this._reloadRequired) {
		this._reloadBrowser();
	}
};

/**
 * Alerts user after creating Birthday Reminders.
 * 
 */
BirthdayReminderZimlet.prototype._saydone = function() {
	appCtxt.getAppController().setStatusMsg(this.getMessage("BirthdayReminder_doneCreatingReminders"), ZmStatusView.LEVEL_INFO);
};

/**
 * Adds listeners to Birthday Reminder list items.
 * 
 */
BirthdayReminderZimlet.prototype._addListeners = function() {
	/*
	var divs = this._brDialog.getHtmlElement().getElementsByTagName("div");
	for (var i = 0; i < divs.length; i++) {
		var hdr = divs[i];
		if (hdr.className == "breminder_cardHdrDiv") {
			hdr.onclick = AjxCallback.simpleClosure(this._onListClick, this, hdr);
		}
	}*/
	document.getElementById("breminder_selectAllOrNone").onclick =  AjxCallback.simpleClosure(this._onSelectAllOrNoneClick, this);
};

/**
 * Selects All or None list items
 * 
 */
BirthdayReminderZimlet.prototype._onSelectAllOrNoneClick = function() {
	if(this._allChecked == undefined) {
		this._allChecked = true;
	}
	if(this._allChecked) {
		for(var i=0; i < this._bDayAndEmail.length; i++) {
			document.getElementById(this._bDayAndEmail[i].chkbx_id).checked = false;
		}
		this._allChecked = false;
	} else {
		for(var i=0; i < this._bDayAndEmail.length; i++) {
			document.getElementById(this._bDayAndEmail[i].chkbx_id).checked = true;
		}
		this._allChecked = true;
	}

};


/**
 * When the list is clicked, this function checks the list-item that was clicked and shows contact details.
 *
 * @param {object} hdr 	the DIV on which the user has clicked
 * @param {object}	ev 	the event object
 */
BirthdayReminderZimlet.prototype._onListClick = function(hdr, ev) {
	var evnt = ev ? ev : window.event;
	if (evnt) {
		var target = evnt.target ? evnt.target : evnt.srcElement;
		if (target.tagName == "INPUT")//if checkbox, donothing special
			return;
	}
	var id = hdr.id;
	var indxId = id.replace("breminder_cardHdrDiv", "");
	var expndCell = document.getElementById("breminder_expCollIcon" + indxId);
	var detailsDiv = document.getElementById("breminder_cardDetailDiv" + indxId);

	if (detailsDiv.className == "breminder_cardDetailDiv breminder_hidden") {
		detailsDiv.className = "breminder_cardDetailDiv breminder_shown";
		expndCell.innerHTML = AjxImg.getImageHtml("NodeExpanded");
	} else {
		detailsDiv.className = "breminder_cardDetailDiv breminder_hidden";
		expndCell.innerHTML = AjxImg.getImageHtml("NodeCollapsed");
	}
};

