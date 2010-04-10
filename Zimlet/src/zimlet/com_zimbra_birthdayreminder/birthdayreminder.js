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
 * Simplify Zimlet handler name
 */
var BirthDayReminderZimlet = com_zimbra_birthdayreminder_HandlerObject;

/**
 * Defines the "calendar" view type.
 */
BirthDayReminderZimlet.CALENDAR_VIEW = "appointment";


/**
 * Defines reminder type. ON_THE_DAY creates reminder on the birth day
 */
BirthDayReminderZimlet.REMINDER_TYPE_ON_THE_DAY= "ON_THE_DAY";

/**
 *  Defines reminder type. DAYS_BEFORE creates reminder x days before Birth day
 */
BirthDayReminderZimlet.REMINDER_TYPE_DAYS_BEFORE= "DAYS_BEFORE";

/**
 *  Defines reminder type. WEEKS_BEFORE creates reminder x days before Birth day
 */
BirthDayReminderZimlet.REMINDER_TYPE_WEEKS_BEFORE= "WEEKS_BEFORE";


/**
 *  Initialize
 */
BirthDayReminderZimlet.prototype.init =
function() {
	this.calendarName = this.getMessage("BirthdayReminder_CalendarName");
};

/**
 * Sets the birthday reminder folder id.
 */
BirthDayReminderZimlet.prototype._setBirthdayReminderFolderId =
function(postCallback) {
	this._justCreatedCalendarFolder = false;
	var soapDoc = AjxSoapDoc.create("GetFolderRequest", "urn:zimbraMail");
	var folderNode = soapDoc.set("folder");
	folderNode.setAttribute("l", appCtxt.getFolderTree().root.id);

	var command = new ZmCsfeCommand();
	var top = command.invoke({soapDoc: soapDoc}).Body.GetFolderResponse.folder[0];

	var folders = top.folder;
	if (folders) {
		for (var i = 0; i < folders.length; i++) {
			var f = folders[i];
			if (f && f.name == this.calendarName && f.view == BirthDayReminderZimlet.CALENDAR_VIEW) {
				this.birthdayreminderFolderId = f.id;
				if(postCallback) {
					postCallback.run(this);
				}
				return;
			}
		}
	}
	//there is no such folder, so create one.
	var params = {color:3, name:this.calendarName, view:BirthDayReminderZimlet.CALENDAR_VIEW, l:"1", postCallback:postCallback};
	this._createBirthdayCalendar(params);
};


/**
 * Creates Calendar folder
 *
 * @param {hash} params A hash of parameters
 * @param  {int} params.color  Id of folder color
 * @param  {string} params.name  Calendar name
 * @param  {string} params.view  View name Calendar|Mail etc
 * @param  {int} params.l  Parent folder id
 * @param  {AjxCallback} params.postcallback  A function that should be called upon successul AJAX call
 */
BirthDayReminderZimlet.prototype._createBirthdayCalendar =
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
 *  Handles Create Folder Response on Success
 *
 * @param {hash} params A hash of parameters
 * @param  {int} params.color  Id of folder color
 * @param  {string} params.name  Calendar name
 * @param  {string} params.view  View name Calendar|Mail etc
 * @param  {int} params.l  Parent folder id
 * @param {object} response Contains createFolderResponse object
 */
BirthDayReminderZimlet.prototype._createFldrCallback =
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
 *  Handles Create Folder Response on Error
 *
 * @param {hash} params A hash of parameters
 * @param  {int} params.color  Id of folder color
 * @param  {string} params.name  Calendar name
 * @param  {string} params.view  View name Calendar|Mail etc
 * @param  {int} params.l  Parent folder id
 * @param {object} ex Contains createFolderResponse object
 */
BirthDayReminderZimlet.prototype._createFldrErrCallback =
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
 *  Shows Error message dialog
 * @param {String} msg  Error message string 
 */
BirthDayReminderZimlet.prototype._showErrorMsg =
function(msg) {
	var msgDialog = appCtxt.getMsgDialog();
	msgDialog.reset();
	msgDialog.setMessage(msg, DwtMessageDialog.CRITICAL_STYLE);
	msgDialog.popup();
};

/**
 * Creates 3 appointments(on the day, days before and weeks before  and  to remind you of Birthday
 *
 * @param {hash} obj a hash of parameters
 * @param  {date} obj.b  Birthday date
 * @param  {string} obj.email  Email address of  birthday boy/girl
 * @param  {string} obj.chkbx_id  Id of the checkbox corresponding to the Birthday contact
 * @param {int} currentCnt Birthday Contact number(used to show status)
 * @param {int} total Total number of contacts for which Reminders will be created
 */
BirthDayReminderZimlet.prototype._create3Appts =
function(obj, currentCnt, total) {
	var msg = this.getMessage("BirthdayReminder_CreatingReminderMsg").replace("{0}", currentCnt).replace("{1}", total);
	appCtxt.getAppController().setStatusMsg(msg , ZmStatusView.LEVEL_INFO);
	var chkbx = document.getElementById(obj.chkbx_id);
	if (!chkbx.checked)
		return;

	if (document.getElementById("breminder_onTheDayChk").checked) {
		this._createVariousAppts(obj, BirthDayReminderZimlet.REMINDER_TYPE_ON_THE_DAY);
	}

	if (document.getElementById("breminder_daysChk").checked) {
		this._createVariousAppts(obj, BirthDayReminderZimlet.REMINDER_TYPE_DAYS_BEFORE);
	}
	if (document.getElementById("breminder_weeksChk").checked) {
		this._createVariousAppts(obj, BirthDayReminderZimlet.REMINDER_TYPE_WEEKS_BEFORE);
	}
};

/**
 * Creates various kinds of Appointments to setup reminders
 *
 * @param {hash} obj a hash of parameters
 * @param  {date} obj.b  Birthday date
 * @param  {string} obj.email  Email address of  birthday boy/girl
 * @param  {string} obj.chkbx_id  Id of the checkbox corresponding to the Birthday contact
 * @param {string} apptType Can be "ON_THE_DAY" | "DAYS_BEFORE" | "WEEKS_BEFORE"
 */
BirthDayReminderZimlet.prototype._createVariousAppts =
function(obj, apptType) {
	var tmparry = obj.b.split("/");
	var birthday = this._normalizeDate(tmparry[0],  tmparry[1], tmparry[2]);
	var subject = "";
	var startDate = "";
	var email_name = "";
	if (obj.e == "") {
		email_name = obj.fn;
	} else if (obj.fn == "") {
		email_name = obj.e;
	} else {
		email_name = obj.fn + "(" + obj.e + ")";
	}
	if (apptType == BirthDayReminderZimlet.REMINDER_TYPE_ON_THE_DAY) {
		startDate = AjxDateUtil.simpleParseDateStr(birthday);
		subject = this.getMessage("BirthdayReminder_TodayIsBirthday").replace("{0}", email_name);
	} else if (apptType == BirthDayReminderZimlet.REMINDER_TYPE_DAYS_BEFORE) {
		var breminder_daysSlct = document.getElementById("breminder_daysSlct");
		var daysCnt = breminder_daysSlct.options[breminder_daysSlct.selectedIndex].text;
		daysCnt = parseInt(daysCnt);
		startDate = AjxDateUtil.simpleParseDateStr(birthday);
		startDate.setHours("0", "00");
		startDate = new Date(startDate.getTime() - (daysCnt * 24 * 3600 * 1000));
		subject = this.getMessage("BirthdayReminder_BirthdayDaysAway").replace("{0}", email_name).replace("{1}", daysCnt);
	} else if (apptType == BirthDayReminderZimlet.REMINDER_TYPE_WEEKS_BEFORE) {
		var breminder_weekSlct = document.getElementById("breminder_weekSlct");
		var weeksCnt = breminder_weekSlct.options[breminder_weekSlct.selectedIndex].text;
		weeksCnt = parseInt(weeksCnt);
		startDate = AjxDateUtil.simpleParseDateStr(birthday);
		startDate.setHours("0", "00");
		startDate = new Date(startDate.getTime() - (weeksCnt * 7 * 24 * 3600 * 1000));
		subject = this.getMessage("BirthdayReminder_BirthdayWeeksAway").replace("{0}", email_name).replace("{1}", weeksCnt);
	}
	startDate.setHours("5", "00");
	var endDate = new Date(startDate.getTime() + 4000 * 60 * 60);
	this._createAppt(startDate, endDate, subject);
};



/**
 * Takes months, day and year info and returns timezone specific date format
 * @param {string} month    month
 * @param {string} day  day
 * @param {string} year year
 * @return timezone specific date format
 */
BirthDayReminderZimlet.prototype._normalizeDate =
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
 * Creates an Appointment
 *
 * @param {date} startDate start date of the appointment
 * @param {date} endDate end date of the appointment
 * @param {string} subject Subject of the appointment
 */
BirthDayReminderZimlet.prototype._createAppt =
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
 * Creates  Birthday Reminder dialog view
 *
 * @return {string} Html of the view
 */
BirthDayReminderZimlet.prototype._createBRView =
function() {
	var html = new Array();
	var i = 0;
	var id_indx = 0;
	this._bDayAndEmail = new Array();
	html[i++] = "<DIV class='breminder_msgDiv' id='breminder_foundMsgId'>";
	html[i++]  = this.getMessage("BirthdayReminder_FoundContacts").replace("{0}",this.filteredContactsArry.length);
	html[i++] = "</DIV>";
	html[i++] = "<BR>";
	html[i++] = "<BR>";
	html[i++] = "<DIV class='breminder_mainDiv' style=\"overflow:auto;height:260px;width:99%\" >";
	for (var j = 0; j < this.filteredContactsArry.length; j++) {
		var contact = this.filteredContactsArry[j];
		var card = new Array();
		var n = 0;
		var birthday = "";
		var email = "";
		var fullName = "";

		html[i++] = "<DIV id='breminder_card" + id_indx + "'  class='breminder_card'>";
		card[n++] = "<DIV id='breminder_cardDetailDiv" + id_indx + "' class='breminder_cardDetailDiv breminder_hidden'>";
		card[n++] = "<TABLE>";
		var attr = contact.attr ? contact.attr : contact._attrs;
		for (var el in attr) {
			var nm = ZmContact._AB_FIELD[el];
			if (nm == undefined)
				continue;

			if ((el == "email" || el == "email2") && email == "") {
				email = attr[el];
			}
			if (el == "fullName") {
				fullName = attr[el];
			}
			if (el == "birthday") {
				birthday = attr[el];
			}
			card[n++] = "<TR><TD><B>" + nm + "</B></TD><TD>" + attr[el] + "</TD></TR>";
		}
		card[n++] = "</TABLE>";
		card[n++] = "</DIV>";

		var k = 0;
		var chkbxId = "breminder_chkbox" + id_indx;
		//var cardHdr = new Array();
		html[i++] = ["<DIV id='breminder_cardHdrDiv", id_indx, "' class='breminder_cardHdrDiv'>"
			, "<TABLE width='100%' CELLPADDING=3 class='breminder_HdrTable'>", "<TR><TD><input id='", chkbxId
			, "'  type='checkbox' checked/></TD><TD width='5%' id='breminder_expCollIcon"
			, id_indx, "'  class='breminder_expCollIcon'>" , AjxImg.getImageHtml("NodeCollapsed"),"</TD>"
			,"<TD  width='75%'>", fullName, " ", email,"</TD>"
			,"<TD  width='20%'>", birthday, "</TD></TR></TABLE></DIV>"].join("");

		//html[i++] = cardHdr.join("");
		html[i++] = card.join("");
		html[i++] = "</DIV>";//for breminder_card
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
	html[i++] = "<input  id='breminder_weeksChk' type='checkbox' checked> </input>";
	html[i++] =  this.getMessage("BirthdayReminder_AlsoRemindMeWeeksBefore").replace("{0}", this._getWeekMenu());
	html[i++] = "</DIV>";
	return html.join("");
};

/**
 * Gets html of days menu
 *
 * @return Html select menu for days
 */
BirthDayReminderZimlet.prototype._getDaysMenu =
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
 *   Gets html of weeks  menu
 * @return Html select menu for weeks
 */
BirthDayReminderZimlet.prototype._getWeekMenu =
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
 *  Displays preferences dialog
 */
BirthDayReminderZimlet.prototype._showPrefDialog =
function() {
	//if zimlet dialog already exists...
	if (this.pbDialog) {
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
 * Creates Preferences view
 *
 * @return {string} Html of the preferences view
 */
BirthDayReminderZimlet.prototype._createPrefView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV>";
	html[i++] = this.getMessage("BirthdayReminder_PleaseNote").replace("{0}", this.getMessage("BirthdayReminder_CalendarName"));
	html[i++] = "</DIV>";
	return html.join("");
};

/**
 * Scans Address Book
 */
BirthDayReminderZimlet.prototype._scanABListner =
function() {
	this.pbDialog.popdown();
	this._setBirthdayReminderFolderId(new AjxCallback(this, this._startScanning));
};

BirthDayReminderZimlet.prototype._startScanning =
function() {
	if (this._apptComposeController == undefined) {//load calendar package when we are creating appt for the first time(since login)
		this._apptComposeController = AjxDispatcher.run("GetApptComposeController");
	}
	//if the calendar was created in this session, we need to refresh.
	if (this._justCreatedCalendarFolder) {
		this._showReloadBrowserDlg();
		return;
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
 * Shows Browser will be refreshed dialog
 */
BirthDayReminderZimlet.prototype._initiateBrowserRefresh =
function() {
	var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.FADE_OUT ];
	appCtxt.getAppController().setStatusMsg(this.getMessage("BirthdayReminder_BrowserWillBeRefreshed"), ZmStatusView.LEVEL_INFO, null, transitions);
	setTimeout(AjxCallback.simpleClosure(this._refreshBrowser, this), 2000);
};

/**
 * Refreshes browser
 */
BirthDayReminderZimlet.prototype._refreshBrowser =
function() {
	window.onbeforeunload = null;
	var url = AjxUtil.formatUrl({});
	ZmZimbraMail.sendRedirect(url);
};

/**
 * Called when the zimlet panel is double-clicked.
 */
BirthDayReminderZimlet.prototype.doubleClicked = function() {
	this.singleClicked();
};

/**
 * Called when the zimlet panel is single-clicked.
 */
BirthDayReminderZimlet.prototype.singleClicked = function() {
	this._showPrefDialog();
};

/**
 * Waits for all contacts to load by checking their count and waiting if the count increases b/w checks
 */
BirthDayReminderZimlet.prototype._waitForContactToLoadAndProcess = function() {
	if(!this._pleaseWaitShown){
		var transitions = [ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.FADE_OUT];
		appCtxt.getAppController().setStatusMsg(this.getMessage("BirthdayReminder_PleaseWait"), ZmStatusView.LEVEL_INFO, null, transitions);
		this._pleaseWaitShown = true;
	}
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
		this._pleaseWaitShown = false;//reset
		this._startProcessing();//start processing
	}
};

/**
 * Starts processing Address Book
 */
BirthDayReminderZimlet.prototype._startProcessing = function() {
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
	appCtxt.getAppController().setStatusMsg(this.getMessage("BirthdayReminder_scanningcCompleted"), ZmStatusView.LEVEL_INFO);
	this._showBRDlg();
};

/**
 * Shows Birthday Reminder dialog
 */
BirthDayReminderZimlet.prototype._showBRDlg = function() {
	//if zimlet dialog already exists...
	if (this._brDialog) {
		this._brDialog.popup();
		return;
	}
	this._brDlgView = new DwtComposite(this.getShell());
	this._brDlgView.setSize("510", "400");
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
	this._brDialog.setButtonListener(reminderBtnId, new AjxListener(this, this._createRemindersListener));
	this._addListListeners();
	this._brDialog.popup();
};

/**
 * Creates Reminders
 */
BirthDayReminderZimlet.prototype._createRemindersListener =
function() {
	this._reloadRequired = false;
	appCtxt.getAppController().setStatusMsg(this.getMessage("BirthdayReminder_pleaseWait"), ZmStatusView.LEVEL_INFO);
	var counter = 1;
	for (var i = 0; i < this._bDayAndEmail.length; i++) {
		var obj = this._bDayAndEmail[i];
		var chkbx = document.getElementById(obj.chkbx_id);
		if (!chkbx.checked)
			continue;

		//this._create3Appts(this._bDayAndEmail[i]);
		//schedule appt creation every 3 seconds( counter*5000) instead of all at once
		setTimeout(AjxCallback.simpleClosure(this._create3Appts, this, obj, counter, this._bDayAndEmail.length), counter * 4000);
		counter++;
	}
	//say done at the very end
	setTimeout(AjxCallback.simpleClosure(this._saydone, this), counter * 4000);//counter would know exactly how much to wait
	this._brDialog.popdown();
	if (this._reloadRequired) {
		this._reloadBrowser();
	}
};

/**
 * Alerts user after creating Birthday Reminders
 */
BirthDayReminderZimlet.prototype._saydone = function() {
	appCtxt.getAppController().setStatusMsg(this.getMessage("BirthdayReminder_doneCreatingReminders"), ZmStatusView.LEVEL_INFO);
};

/**
 * Adds listeners to Birthday Reminder list items
 */
BirthDayReminderZimlet.prototype._addListListeners = function() {
	var divs = this._brDialog.getHtmlElement().getElementsByTagName("div");
	for (var i = 0; i < divs.length; i++) {
		var hdr = divs[i];
		if (hdr.className == "breminder_cardHdrDiv") {
			hdr.onclick = AjxCallback.simpleClosure(this._onListClick, this, hdr);
		}
	}
};

/**
 *When the list is clicked, this function checks the list-item that was clicked andshows contact details
 *
 * @param {object} hdr DIV on which the user has clicked
 * @param ev event object
 */
BirthDayReminderZimlet.prototype._onListClick = function(hdr, ev) {
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

/**
 * Shows the reload browser dialog.
 *
 */
BirthDayReminderZimlet.prototype._showReloadBrowserDlg =
function() {
	//if zimlet dialog already exists...
	if (this._reloadBrowserDialog) {
		this._reloadBrowserDialog.popup();
		return;
	}
	this._reloadBrowserView = new DwtComposite(this.getShell());
	this._reloadBrowserView.getHtmlElement().style.overflow = "auto";
	this._reloadBrowserView.getHtmlElement().innerHTML = this._createReloadBrowserView();

	var dialog_args = {
			title	: this.getMessage("BirthdayReminder_needToReloadBrowser"),
			view	: this._reloadBrowserView,
			parent	: this.getShell(),
			standardButtons : [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]
	};

	this._reloadBrowserDialog = new ZmDialog(dialog_args);
	this._reloadBrowserDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okReloadBrowserBtnListener));
	this._reloadBrowserDialog.popup();
};

/**
 * Creates the reload browser view.
 *
 * @see		_showReloadBrowserDlg
 */
BirthDayReminderZimlet.prototype._createReloadBrowserView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV>";
	html[i++] =this.getMessage("BirthdayReminder_createdCalendarReloadBrowser").replace("{0}", this.calendarName);
	html[i++] = "</DIV>";
	return html.join("");
};

/**
 * Listens for the OK button.
 *
 * @see			_showReloadBrowserDlg
 */
BirthDayReminderZimlet.prototype._okReloadBrowserBtnListener =
function() {
	this._reloadBrowser();
	this._reloadBrowserDialog.popdown();
};

/**
 * Reloads the browser.
 */
BirthDayReminderZimlet.prototype._reloadBrowser =
function() {
	window.onbeforeunload = null;
	var url = AjxUtil.formatUrl({});
	ZmZimbraMail.sendRedirect(url);
};