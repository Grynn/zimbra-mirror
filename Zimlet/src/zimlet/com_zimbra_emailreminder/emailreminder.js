/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 *
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 *
 * ***** END LICENSE BLOCK *****
 *@Author Raja Rao DV
 */


function com_zimbra_emailreminder() {
}

com_zimbra_emailreminder.prototype = new ZmZimletBase();
com_zimbra_emailreminder.prototype.constructor = com_zimbra_emailreminder;


com_zimbra_emailreminder.discover = "DISCOVER";
com_zimbra_emailreminder.CALENDAR_VIEW = "appointment";
com_zimbra_emailreminder.followupFolder = 'Email Reminders';

com_zimbra_emailreminder.prototype.init =
function() {


    this.emailReminderZimletON = this.getUserProperty("turnONEmailReminderZimlet") == "true";
    if (!this.emailReminderZimletON) {
        return;
    }
    this._allowFlag = this.getUserProperty("emailReminder_allowFlag") == "true";
    this._allowDrag = this.getUserProperty("emailReminder_allowDrag") == "true";

};

com_zimbra_emailreminder.prototype.getEmailFollowupFolderId =
function() {
	this._justCreatedCalendarFolder = false;
    var soapDoc = AjxSoapDoc.create("GetFolderRequest", "urn:zimbraMail");
    var command = new ZmCsfeCommand();
    var top = command.invoke({soapDoc: soapDoc}).Body.GetFolderResponse.folder[0];

    var folders = top.folder;
    if (folders) {
        for (var i = 0; i < folders.length; i++) {
            var f = folders[i];
            if (f && f.name == com_zimbra_emailreminder.followupFolder && f.view == com_zimbra_emailreminder.CALENDAR_VIEW) {
                this.emailFollowupFolderId = f.id;
                return;
            }
        }
    }

    //there is no such folder, so create one.
    this.createEmailFollowupFolder(top.id);
};

com_zimbra_emailreminder.prototype.createEmailFollowupFolder =
function(parent) {
    var soapDoc = AjxSoapDoc.create("CreateFolderRequest", "urn:zimbraMail");
    var folderNode = soapDoc.set("folder");
    folderNode.setAttribute("name", com_zimbra_emailreminder.followupFolder);
    folderNode.setAttribute("l", parent);
    folderNode.setAttribute("view", com_zimbra_emailreminder.CALENDAR_VIEW);
    var command = new ZmCsfeCommand();
    var resp = command.invoke({soapDoc: soapDoc});
    var id = resp.Body.CreateFolderResponse.folder[0].id;
    if (!id) {
        throw new AjxException("Cannot create Email Followup folder folder ", AjxException.INTERNAL_ERROR, "createEmailFollowupFolder");
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





com_zimbra_emailreminder.prototype._createReminderDialog =
function() {
    //if zimlet dialog already exists...
    if (this._erDialog) {
        this._getAbsHoursMenu();//reset the timemenu
        this._erDialog.popup();
        return;
    }
    this.erView = new DwtComposite(this.getShell());
    this.erView.setSize("500", "60");
    //this.erView.getHtmlElement().style.background = "white";
    this.erView.getHtmlElement().style.overflow = "auto";
    this.erView.getHtmlElement().innerHTML = this._createErView();
    this._getAbsHoursMenu();
    this._setTodayToDateField();
    this._createCalendarWidget();
    this._erDialog = this._createDialog({title:"Email Reminder Setup", view:this.erView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
    this._erDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListener));


};

com_zimbra_emailreminder.prototype._okBtnListener =
function() {
    var todayDate = new Date();
    var startDate = todayDate;
    var endDate = "";
    var parsed = false;

    var hoursVal = document.getElementById("emailReminder_absMenu").value;
    var deltaHrs = hoursVal.split(":");
    var hours = parseInt(deltaHrs[0]);
    var min = parseInt(deltaHrs[1]);
    startDate = AjxDateUtil.simpleParseDateStr(document.getElementById("emailReminder_datefield").value);
    startDate.setHours(hours, min);
    endDate = new Date(startDate.getTime() + 1000 * 60 * 60);
    this._createAppt(startDate, endDate, this._subject);
    this._erDialog.popdown();
};

com_zimbra_emailreminder.prototype._createAppt =
function(startDate, endDate, subject) {
    var reminderMinutes = appCtxt.getSettings().getSetting("CAL_REMINDER_WARNING_TIME").value;
	try{
	    var appt = new ZmAppt();
		appt.setStartDate(startDate);
		appt.setEndDate(endDate);
		appt.setName(subject);
		appt.setReminderMinutes(reminderMinutes);
		appt.freeBusy = "F"; 
		appt.privacy = "PRI";
		appt.transparency = "O";
		appt.setFolderId(this.emailFollowupFolderId);
		appt.save();
	} catch(e) {
		
	}
};

com_zimbra_emailreminder.prototype.doDrop =
function(msg) {
    if (this._allowDrag && this.emailReminderZimletON) {
        this._setSubjectAndShowDlg(msg.subject);
    }
};
com_zimbra_emailreminder.prototype.onMailFlagClick =
function(msgs) {
    if (this._allowFlag && this.emailReminderZimletON) {
        this._setSubjectAndShowDlg(msgs[0].subject);
    }
};

com_zimbra_emailreminder.prototype._setSubjectAndShowDlg =
function(subject) {
    if (this._apptComposeController == undefined) {//load calendar package when we are creating appt for the first time(since login)
		this.getEmailFollowupFolderId();
        this._apptComposeController = AjxDispatcher.run("GetApptComposeController");
    }

	if(this._justCreatedCalendarFolder) {//if the calendar was created in this session, we need to refresh.
		this._showReloadBrowserDlg();
		return;
	}
    this._subject = subject;
    this._createReminderDialog();
    document.getElementById("emailReminder_subjectField").innerHTML = subject;
    this._erDialog.popup();
}

com_zimbra_emailreminder.prototype._createErView =
function() {
    var html = new Array();
    var i = 0;
    html[i++] = "<DIV>";
    html[i++] = "<TABLE width=100%>";
    html[i++] = "<TR><TD width=2%><img src='" + this.getResource("emailReminder.gif") + "'/></TD><TD>Remind me about <b>'<span id='emailReminder_subjectField'></span>'</b> email at:</TD><TR>";
    html[i++] = "</TABLE>";
    html[i++] = "<TABLE>";
    html[i++] = "<TR><TD id='emailReminder_absMenuTD'></TD><TD><input type='text' id='emailReminder_datefield' SIZE=9></input></TD><TD width='10px' id='emailReminder_calendarMenu'></TD><TD>(<span  id='emailReminder_dateFriendlyName'></span>)</TD></TR>";
    html[i++] = "</TABLE>";
    html[i++] = "</DIV>";

    return html.join("");

}

com_zimbra_emailreminder.prototype._createPrefView =
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
    html[i++] = "<input id='turnONEmailReminderChkbx'  type='checkbox'/>Turn ON 'Email Reminder'-Zimlet";
    html[i++] = "</DIV>";
    html[i++] = "<DIV>";
    html[i++] = "<BR>";
    html[i++] = "<FONT size='1pt'>*Changes to the above preferences would refresh the browser</FONT>";
    html[i++] = "</DIV>";
    return html.join("");

}

com_zimbra_emailreminder.prototype._createReloadBrowserView =
function() {
    var html = new Array();
    var i = 0;
    html[i++] = "<DIV>";
    html[i++] = "Just now we created a Calendar, '";
    html[i++] = com_zimbra_emailreminder.followupFolder;
    html[i++] = "' to keep Email Reminders.<br> But, we need to reload browser for this to work. Reload Browser?";
    html[i++] = "</DIV>";
    return html.join("");

}

com_zimbra_emailreminder.prototype._getAbsHoursMenu =
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

}

com_zimbra_emailreminder.prototype._createCalendarWidget =
function() {
    var dateButtonListener = new AjxListener(this, this._dateButtonListener);
    var dateCalSelectionListener = new AjxListener(this, this._dateCalSelectionListener);
    this._startDateButton = ZmCalendarApp.createMiniCalButton(this.erView, "emailReminder_calendarMenu", dateButtonListener, dateCalSelectionListener);

}


com_zimbra_emailreminder.prototype._setTodayToDateField =
function() {
    document.getElementById("emailReminder_datefield").value = AjxDateUtil.simpleComputeDateStr(new Date());
    this._setdayName();
}

com_zimbra_emailreminder.prototype._setdayName =
function() {
    var val = AjxDateUtil.simpleParseDateStr(document.getElementById("emailReminder_datefield").value);
    var dateFormatter = AjxDateFormat.getDateTimeInstance(AjxDateFormat.FULL);
    var dayName = dateFormatter.format(new Date(val)).split(",")[0];
    document.getElementById("emailReminder_dateFriendlyName").innerHTML = dayName;
}
com_zimbra_emailreminder.prototype._dateButtonListener =
function(ev) {
    var calDate = AjxDateUtil.simpleParseDateStr(document.getElementById("emailReminder_datefield").value);

    // always reset the date to current field's date
    var menu = ev.item.getMenu();
    var cal = menu.getItem(0);
    cal.setDate(calDate, true);
    ev.item.popup();
}

com_zimbra_emailreminder.prototype._dateCalSelectionListener =
function(ev) {
    var parentButton = ev.item.parent.parent;
    var newDate = AjxDateUtil.simpleComputeDateStr(ev.detail);
    document.getElementById("emailReminder_datefield").value = newDate;
    this._setdayName();
};

com_zimbra_emailreminder.prototype.doubleClicked = function() {
    this.singleClicked();
};
com_zimbra_emailreminder.prototype.singleClicked = function() {
    this._showPreferenceDlg();
}
com_zimbra_emailreminder.prototype._showPreferenceDlg = function() {
    //if zimlet dialog already exists...
    if (this._preferenceDialog) {
        this._setZimletCurrentPreferences();
        this._preferenceDialog.popup();
        return;
    }
    this._preferenceView = new DwtComposite(this.getShell());
    //this._preferenceView.setSize("400", "300");
    this._preferenceView.getHtmlElement().style.overflow = "auto";
    this._preferenceView.getHtmlElement().innerHTML = this._createPrefView();

    this._preferenceDialog = this._createDialog({title:"Email Reminder Zimlet Preferences", view:this._preferenceView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
    this._preferenceDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okPreferenceBtnListener));
    this._setZimletCurrentPreferences();
    this._preferenceDialog.popup();
}

com_zimbra_emailreminder.prototype._showReloadBrowserDlg = function() {
    //if zimlet dialog already exists...
    if (this._reloadBrowserDialog) {
        this._reloadBrowserDialog.popup();
        return;
    }
    this._reloadBrowserView = new DwtComposite(this.getShell());
    this._reloadBrowserView.getHtmlElement().style.overflow = "auto";
    this._reloadBrowserView.getHtmlElement().innerHTML = this._createReloadBrowserView();

    this._reloadBrowserDialog = this._createDialog({title:"Email Reminder: Need to Reload Browser", view:this._reloadBrowserView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
    this._reloadBrowserDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okReloadBrowserBtnListener));
    this._reloadBrowserDialog.popup();
}

com_zimbra_emailreminder.prototype._okReloadBrowserBtnListener =
function() {
		this._reloadBrowser();
		this._reloadBrowserDialog.popdown();
}

com_zimbra_emailreminder.prototype._setZimletCurrentPreferences =
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
}

com_zimbra_emailreminder.prototype._okPreferenceBtnListener =
function() {
    this._reloadRequired = false;
    if (document.getElementById("turnONEmailReminderChkbx").checked) {
        if (!this.emailReminderZimletON) {
            this._reloadRequired = true;
        }
        this.setUserProperty("turnONEmailReminderZimlet", "true", true);

    } else {
        this.setUserProperty("turnONEmailReminderZimlet", "false", true);
        if (this.emailReminderZimletON)
            this._reloadRequired = true;
    }

    if (document.getElementById("ereminder_flagChkbx").checked) {
        if (!this._allowFlag) {
            this._reloadRequired = true;
        }
        this.setUserProperty("emailReminder_allowFlag", "true", true);

    } else {
        this.setUserProperty("emailReminder_allowFlag", "false", true);
        if (this._allowFlag)
            this._reloadRequired = true;
    }
    if (document.getElementById("ereminder_dragChkbx").checked) {
        if (!this._allowDrag) {
            this._reloadRequired = true;
        }
        this.setUserProperty("emailReminder_allowDrag", "true", true);

    } else {
        this.setUserProperty("emailReminder_allowDrag", "false", true);
        if (this._allowDrag)
            this._reloadRequired = true;
    }

    this._preferenceDialog.popdown();
    if (this._reloadRequired) {
		this._reloadBrowser();
    }
};
com_zimbra_emailreminder.prototype._reloadBrowser =
function() {
        window.onbeforeunload = null;
        var url = AjxUtil.formatUrl({});
        ZmZimbraMail.sendRedirect(url);
}