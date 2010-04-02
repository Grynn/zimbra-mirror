/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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


function com_zimbra_emailreminderHdlr() {
}

com_zimbra_emailreminderHdlr.prototype = new ZmZimletBase();
com_zimbra_emailreminderHdlr.prototype.constructor = com_zimbra_emailreminderHdlr;

com_zimbra_emailreminderHdlr.CALENDAR_VIEW = "appointment";
com_zimbra_emailreminderHdlr.followupFolder = "Email Reminders";


com_zimbra_emailreminderHdlr.prototype.init =
function() {


	this.emailReminderZimletON = this.getUserProperty("turnONEmailReminderZimlet") == "true";
	if (!this.emailReminderZimletON) {
		return;
	}
	this._allowFlag = this.getUserProperty("emailReminder_allowFlag") == "true";
	this._allowDrag = this.getUserProperty("emailReminder_allowDrag") == "true";
	this.ereminder_showInCompose = this.getUserProperty("ereminder_showInCompose") == "true";
	this._notesPrefix = this.getMessage("EmailReminder_NotesPrefix");
};

com_zimbra_emailreminderHdlr.prototype.getEmailFollowupFolderId =
function() {
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
			if (f && f.name == com_zimbra_emailreminderHdlr.followupFolder && f.view == com_zimbra_emailreminderHdlr.CALENDAR_VIEW) {
				this.emailFollowupFolderId = f.id;
				return;
			}
		}
	}
	//there is no such folder, so create one.
	this.createEmailFollowupFolder(top.id);
};

com_zimbra_emailreminderHdlr.prototype.createEmailFollowupFolder =
function() {
	var soapDoc = AjxSoapDoc.create("CreateFolderRequest", "urn:zimbraMail");
	var folderNode = soapDoc.set("folder");
	folderNode.setAttribute("name", com_zimbra_emailreminderHdlr.followupFolder);
	folderNode.setAttribute("l", appCtxt.getFolderTree().root.id);
	folderNode.setAttribute("view", com_zimbra_emailreminderHdlr.CALENDAR_VIEW);
	var command = new ZmCsfeCommand();
	var resp = command.invoke({soapDoc: soapDoc});
	var id = resp.Body.CreateFolderResponse.folder[0].id;
	if (!id) {
		throw new AjxException(this.getMessage("EmailReminder_CouldNotCreateCal"), AjxException.INTERNAL_ERROR, "createEmailFollowupFolder");
	}
	this.emailFollowupFolderId = id;

	soapDoc = AjxSoapDoc.create("FolderActionRequest", "urn:zimbraMail");
	var actionNode = soapDoc.set("action");
	actionNode.setAttribute("op", "color");
	actionNode.setAttribute("id", id);
	actionNode.setAttribute("color", "5");
	command = new ZmCsfeCommand();
	resp = command.invoke({soapDoc: soapDoc});
	this._justCreatedCalendarFolder = true;

};

com_zimbra_emailreminderHdlr.prototype._createReminderDialog =
function() {
	//if zimlet dialog already exists...
	if (this._erDialog) {
		this._getAbsHoursMenu();//reset the timemenu
		document.getElementById("emailReminder_notesField").value = "";
		this._erDialog.popup();
		return;
	}
	this.erView = new DwtComposite(this.getShell());
	this.erView.setSize("500", "80");
	//this.erView.getHtmlElement().style.background = "white";
	this.erView.getHtmlElement().style.overflow = "auto";
	this.erView.getHtmlElement().innerHTML = this._createErView();
	this._getAbsHoursMenu();
	this._setTodayToDateField();
	this._createCalendarWidget();
	this._erDialog = this._createDialog({title:this.getMessage("EmailReminder_PrefDlgLabel"), view:this.erView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	this._erDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListener));
};

com_zimbra_emailreminderHdlr.prototype._okBtnListener =
function() {
	var startDate = new Date();
	var endDate = "";
	var hoursVal = document.getElementById("emailReminder_absMenu").value;
	var deltaHrs = hoursVal.split(":");
	var hours = parseInt(deltaHrs[0]);
	var min = parseInt(deltaHrs[1]);
	startDate = AjxDateUtil.simpleParseDateStr(document.getElementById("emailReminder_datefield").value);
	startDate.setHours(hours, min);
	endDate = new Date(startDate.getTime() + 1000 * 60 * 60);
	this._notes = document.getElementById("emailReminder_notesField").value;
	//add notes
	var subject = "";
	if (this._notes != "")
		subject = this._subject + " (" + this._notesPrefix + this._notes + ")";
	else
		subject = this._subject;

	this._createAppt(startDate, endDate, subject);
	this._erDialog.popdown();
};

com_zimbra_emailreminderHdlr.prototype._createAppt =
function(startDate, endDate, subject) {
	var reminderMinutes = appCtxt.getSettings().getSetting("CAL_REMINDER_WARNING_TIME").value;
	try {
		var appt = new ZmAppt();
		appt.setStartDate(startDate);
		appt.setEndDate(endDate);
		appt.setName(subject);
		appt.setTextNotes(this._notesPrefix + this._notes);
		appt.setReminderMinutes(reminderMinutes);
		appt.freeBusy = "F";
		appt.privacy = "PRI";
		appt.transparency = "O";
		appt.setFolderId(this.emailFollowupFolderId);
		appt.save();
	} catch(e) {
		return;
	}
	var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.FADE_OUT ];
	appCtxt.getAppController().setStatusMsg(this.getMessage("EmailReminder_Created"), ZmStatusView.LEVEL_INFO, null, transitions);
};

com_zimbra_emailreminderHdlr.prototype.doDrop =
function(msg) {
	if (this._allowDrag && this.emailReminderZimletON) {
		this._msgObj = msg;
		this._setSubjectAndShowDlg(msg.subject);
	}
};

com_zimbra_emailreminderHdlr.prototype.onMailFlagClick =
function(msgs, on) {
	if (this._allowFlag && this.emailReminderZimletON) {
		this._msgObj = msgs[0];
		if(on == null || on == undefined) {
			//when on is null, delay this so we can figure out if the item was flagged or unflagged
			setTimeout(AjxCallback.simpleClosure(this._showDlg, this), 1000);
		} else if(on == true){
			this._showDlg(true);
		}
	}
};

com_zimbra_emailreminderHdlr.prototype._showDlg =
function(force) {
	if(this._msgObj.isFlagged || force) {
		this._setSubjectAndShowDlg(this._msgObj.subject);
	}
};

com_zimbra_emailreminderHdlr.prototype._setSubjectAndShowDlg =
function(subject) {
	if (this._apptComposeController == undefined) {//load calendar package when we are creating appt for the first time(since login)
		this.getEmailFollowupFolderId();
		this._apptComposeController = AjxDispatcher.run("GetApptComposeController");
	}

	if (this._justCreatedCalendarFolder) {//if the calendar was created in this session, we need to refresh.
		this._showReloadBrowserDlg();
		return;
	}
	this._subject = subject;
	if (subject.length > 50)
		subject = subject.substring(0, 50) + "...";

	this._createReminderDialog();
	document.getElementById("emailReminder_subjectField").innerHTML = subject;
	this._erDialog.popup();
};

com_zimbra_emailreminderHdlr.prototype.initializeToolbar = function(app, toolbar, controller, viewId) {
	if(viewId.indexOf("COMPOSE") >=0 && this.ereminder_showInCompose)
		this._addReminderBtnToCompose(toolbar, controller);
};

com_zimbra_emailreminderHdlr.prototype._addReminderBtnToCompose = function(toolbar, controller) {
	var ID = "EMAIL_REMINDER";
	// Add button to toolbar
	if (!toolbar.getButton(ID)) {
		var btn = toolbar.createOp(
			ID,
		{
			text	: this.getMessage("EmailReminder_SendAndRemind"),
			tooltip : this.getMessage("EmailReminder_SendAndRemindTT"),
			index   : 1,
			image   : "emailreminder-panelIcon"
		}
		);
		toolbar.addOp(ID, 2);
	    btn.addSelectionListener(new AjxListener(this, this._createReminderFromCompose));
		this._composerCtrl = controller;
	}
};

com_zimbra_emailreminderHdlr.prototype._createReminderFromCompose =
function() {
	var msg = this._composerCtrl._composeView.getMsg();
	if (msg) {
		this._composerCtrl._send();
		this._setSubjectAndShowDlg(this._composerCtrl._composeView._subjectField.value);
	}
};

com_zimbra_emailreminderHdlr.prototype._createErView =
function() {
	var hdrMsg = this.getMessage("EmailReminder_HdrMsg").replace("{0}", "<b><span id='emailReminder_subjectField'></span></b>"); 
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV>";
	html[i++] = "<TABLE width=100%>";
	html[i++] = ["<TR><TD width=2%><img src='" , this.getResource("emailReminder.gif") , "'/></TD><TD>",hdrMsg,"</TD><TR>"].join("");
	html[i++] = "</TABLE>";
	html[i++] = "<TABLE>";
	html[i++] = "<TR><TD id='emailReminder_absMenuTD'></TD><TD><input type='text' id='emailReminder_datefield' SIZE=9></input></TD><TD width='10px' id='emailReminder_calendarMenu'></TD><TD>(<span  id='emailReminder_dateFriendlyName'></span>)</TD></TR>";
	html[i++] = "</TABLE>";
	html[i++] = "</DIV>";
	html[i++] = "<DIV>";
	html[i++] = this._notesPrefix;
	html[i++] = "<input id='emailReminder_notesField' type=text style=\"width:400px;\"></input>";
	html[i++] = "</DIV>";
	return html.join("");
};

com_zimbra_emailreminderHdlr.prototype._createPrefView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV>";
	html[i++] = "<input id='ereminder_flagChkbx'  type='checkbox'/>Show Email Reminder when an email is Flagged";
	html[i++] = "</DIV>";
	html[i++] = "<DIV>";
	html[i++] = "<input id='ereminder_dragChkbx'  type='checkbox'/>Show Email Reminder when an email is drag-dropped";
	html[i++] = "</DIV>";
	html[i++] = "<BR>";
	html[i++] = "<DIV>";
	html[i++] = "<input id='ereminder_showInCompose'  type='checkbox'/>Show 'Send & Remind' Button in Mail Compose.<br>(Sends email and allows creating reminder for that email)";
	html[i++] = "</DIV>";
	html[i++] = "<BR>";
	html[i++] = "<BR>";
	html[i++] = "<DIV>";
	html[i++] = "<input id='turnONEmailReminderChkbx'  type='checkbox'/>Turn ON 'Email Reminder'-Zimlet";
	html[i++] = "</DIV>";
	html[i++] = "<DIV>";
	html[i++] = "<BR>";
	html[i++] = "<FONT size='1pt'>*Changes to the above preferences would refresh the browser</FONT>";
	html[i++] = "</DIV>";
	return html.join("");

};

com_zimbra_emailreminderHdlr.prototype._createReloadBrowserView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV>";
	html[i++] = "Email Reminder Zimlet Setup: We created a Calendar, '";
	html[i++] = com_zimbra_emailreminderHdlr.followupFolder;
	html[i++] = "' to store Email Reminders.<br> We need to reload Browser for setup to complete. Reload Browser?";
	html[i++] = "</DIV>";
	return html.join("");
};

com_zimbra_emailreminderHdlr.prototype._getAbsHoursMenu =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<select id=\"emailReminder_absMenu\">";
	var menItems = "";
	for (var j = 1; j <= 24; j++) {
		var ampmnoon = "";
		if (j < 12)
			ampmnoon = " AM";
		else if (j == 12)
			ampmnoon = " noon";
		else if (j == 24)
				ampmnoon = " midnight";
			else
				ampmnoon = " PM";

		var k = j % 12;
		if (k == 0) {
			k = 12;
		}

		var selectNum = new Date().getHours() + 2;
		if (selectNum > 24)
			selectNum = 24;


		if (j == selectNum) {
			menItems = menItems + "<option value=\"" + j + ":00\" selected>" + k + ":00 " + ampmnoon + "</option>";
		} else {
			menItems = menItems + "<option value=\"" + j + ":00\">" + k + ":00 " + ampmnoon + "</option>";
		}

		if (j != 24 && j != 12) {//add 30 minutes for everything but 24hours
			menItems = menItems + "<option value=\"" + j + ":30\" >" + k + ":30 " + ampmnoon + "</option>";
		} else if (j == 12) {//dont add noon to 12:30
			menItems = menItems + "<option value=\"" + j + ":30\" >" + k + ":30 PM</option>";
		}


	}
	html[i++] = menItems;
	html[i++] = "</select>";
	var reminderCell = document.getElementById("emailReminder_absMenuTD");
	if (reminderCell)
		reminderCell.innerHTML = html.join("");

};

com_zimbra_emailreminderHdlr.prototype._createCalendarWidget =
function() {
	var dateButtonListener = new AjxListener(this, this._dateButtonListener);
	var dateCalSelectionListener = new AjxListener(this, this._dateCalSelectionListener);
	this._startDateButton = ZmCalendarApp.createMiniCalButton(this.erView, "emailReminder_calendarMenu", dateButtonListener, dateCalSelectionListener);

};


com_zimbra_emailreminderHdlr.prototype._setTodayToDateField =
function() {
	document.getElementById("emailReminder_datefield").value = AjxDateUtil.simpleComputeDateStr(new Date());
	this._setdayName();
};

com_zimbra_emailreminderHdlr.prototype._setdayName =
function() {
	var val = AjxDateUtil.simpleParseDateStr(document.getElementById("emailReminder_datefield").value);
	var dateFormatter = AjxDateFormat.getDateTimeInstance(AjxDateFormat.FULL);
	document.getElementById("emailReminder_dateFriendlyName").innerHTML = dateFormatter.format(new Date(val)).split(",")[0];
};

com_zimbra_emailreminderHdlr.prototype._dateButtonListener =
function(ev) {
	var calDate = AjxDateUtil.simpleParseDateStr(document.getElementById("emailReminder_datefield").value);

	// always reset the date to current field's date
	var menu = ev.item.getMenu();
	var cal = menu.getItem(0);
	cal.setDate(calDate, true);
	ev.item.popup();
};

com_zimbra_emailreminderHdlr.prototype._dateCalSelectionListener =
function(ev) {
	document.getElementById("emailReminder_datefield").value = AjxDateUtil.simpleComputeDateStr(ev.detail);
	this._setdayName();
};

com_zimbra_emailreminderHdlr.prototype.doubleClicked = function() {
	this.singleClicked();
};
com_zimbra_emailreminderHdlr.prototype.singleClicked = function() {
	this._showPreferenceDlg();
};

com_zimbra_emailreminderHdlr.prototype._showPreferenceDlg = function() {
	this._allowFlag = this.getUserProperty("emailReminder_allowFlag") == "true";
	this._allowDrag = this.getUserProperty("emailReminder_allowDrag") == "true";
	this.turnONEmailReminderChkbx = this.getUserProperty("turnONEmailReminderChkbx") == "true";
	this.ereminder_showInCompose = this.getUserProperty("ereminder_showInCompose") == "true";

	//if zimlet dialog already exists...
	if (this._preferenceDialog) {
		this._setZimletCurrentPreferences();
		this._preferenceDialog.popup();
		return;
	}
	this._preferenceView = new DwtComposite(this.getShell());
	this._preferenceView.getHtmlElement().style.overflow = "auto";
	this._preferenceView.getHtmlElement().innerHTML = this._createPrefView();

	this._preferenceDialog = this._createDialog({title:"Email Reminder Zimlet Preferences", view:this._preferenceView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	this._preferenceDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okPreferenceBtnListener));
	this._setZimletCurrentPreferences();
	this._preferenceDialog.popup();
};

com_zimbra_emailreminderHdlr.prototype._showReloadBrowserDlg = function() {
	//if zimlet dialog already exists...
	if (this._reloadBrowserDialog) {
		this._reloadBrowserDialog.popup();
		return;
	}
	this._reloadBrowserView = new DwtComposite(this.getShell());
	this._reloadBrowserView.getHtmlElement().style.overflow = "auto";
	this._reloadBrowserView.getHtmlElement().innerHTML = this._createReloadBrowserView();

	this._reloadBrowserDialog = this._createDialog({title:"Email Reminder Zimlet: Need to Reload Browser", view:this._reloadBrowserView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	this._reloadBrowserDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okReloadBrowserBtnListener));
	this._reloadBrowserDialog.popup();
};

com_zimbra_emailreminderHdlr.prototype._okReloadBrowserBtnListener =
function() {
	this._reloadBrowser();
	this._reloadBrowserDialog.popdown();
};

com_zimbra_emailreminderHdlr.prototype._setZimletCurrentPreferences =
function() {
	if (this.emailReminderZimletON) {
		document.getElementById("turnONEmailReminderChkbx").checked = true;
	}
	if (this._allowFlag) {
		document.getElementById("ereminder_flagChkbx").checked = true;
	}
	if (this._allowDrag) {
		document.getElementById("ereminder_dragChkbx").checked = true;
	}
	if (this.ereminder_showInCompose) {
		document.getElementById("ereminder_showInCompose").checked = true;
	}
};

com_zimbra_emailreminderHdlr.prototype._okPreferenceBtnListener =
function() {
	this._reloadRequired = false;
	if (document.getElementById("turnONEmailReminderChkbx").checked) {
		if (!this.emailReminderZimletON) {
			this._reloadRequired = true;
		}
		this.setUserProperty("turnONEmailReminderZimlet", "true");

	} else {
		this.setUserProperty("turnONEmailReminderZimlet", "false");
		if (this.emailReminderZimletON)
			this._reloadRequired = true;
	}

	if (document.getElementById("ereminder_flagChkbx").checked) {
		if (!this._allowFlag) {
			this._reloadRequired = true;
		}
		this.setUserProperty("emailReminder_allowFlag", "true");

	} else {
		this.setUserProperty("emailReminder_allowFlag", "false");
		if (this._allowFlag)
			this._reloadRequired = true;
	}
	if (document.getElementById("ereminder_dragChkbx").checked) {
		if (!this._allowDrag) {
			this._reloadRequired = true;
		}
		this.setUserProperty("emailReminder_allowDrag", "true");

	} else {
		this.setUserProperty("emailReminder_allowDrag", "false");
		if (this._allowDrag)
			this._reloadRequired = true;
	}
	if (document.getElementById("ereminder_showInCompose").checked) {
		if (!this.ereminder_showInCompose) {
			this._reloadRequired = true;
		}
		this.setUserProperty("ereminder_showInCompose", "true");

	} else {
		this.setUserProperty("ereminder_showInCompose", "false");
		if (this.ereminder_showInCompose)
			this._reloadRequired = true;
	}

	this._preferenceDialog.popdown();
	if (this._reloadRequired) {
		var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.FADE_OUT ];
		appCtxt.getAppController().setStatusMsg("Please wait. Browser will be refreshed for changes to take effect..", ZmStatusView.LEVEL_INFO, null, transitions);
		this.saveUserProperties(new AjxCallback(this, this._reloadBrowser));
	}
};

com_zimbra_emailreminderHdlr.prototype._reloadBrowser =
function() {
	window.onbeforeunload = null;
	var url = AjxUtil.formatUrl({});
	ZmZimbraMail.sendRedirect(url);
};